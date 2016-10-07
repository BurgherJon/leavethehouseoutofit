package com.lthoi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

/** This is used to manage the active user. **/
public class Me 
{
	private String email;
	private String fname;
	private String lname;
	private String linitial;
	private int wins;
	private int losses;
	private int pushes;
	private double winnings;
		
	//This constructor is for testing purposes and just creates the user with the agreed to test data (see TestHarnessScenario.doc)
	public Me(String email)
	{		
		String strurl = "";
        String struser = "";
        String strpass = "";
		String strquery;
		final Logger log = Logger.getLogger(Me.class.getName());
		
		log.info("In the constructor for Me with the email passed.");
		log.info("Email passed: " + email);
		
		this.email = email;
				
        try
		{
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
			{
				// Load the class that provides the new "jdbc:google:mysql://" prefix.
				Class.forName("com.mysql.jdbc.GoogleDriver");
				strurl = "jdbc:google:mysql://focal-acronym-94611:us-central1:lthoidb/lthoidb";
				struser = "root";
				strpass = "!VegasVaca2!";
			}
			else
			{
				//Local MySQL Instance to use during Dev.
				Class.forName("com.mysql.jdbc.Driver");
				strurl = "jdbc:mysql://127.0.0.1:3306/lthoidb";
				struser = "root";
				log.info("Running locally!");
			}
		}
		catch (ClassNotFoundException e)
		{
			log.severe("Unable to create connection string for the database.");
			log.severe(e.getMessage());
		}
		
        
        
		Connection conn = null;
		
		try 
		{
			conn = DriverManager.getConnection(strurl, struser, strpass);			
			
			strquery = "Select u.email AS email, u.fname AS fname, u.linitial AS linitial, u.lname AS lname, lsum.league_season_id AS league_season_id FROM lthoidb.Users u INNER JOIN lthoidb.League_Season_User_Map lsum ON u.user_id = lsum.user_id WHERE u.email = '" + email + "';";
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				this.setFname(rs.getString("fname"));
				this.setLname(rs.getString("lname"));
				this.setLinitial(rs.getString("Linitial"));
				this.setEmail(rs.getString("email"));
				
				this.losses = 0;
				this.wins = 0;
				this.pushes = 0;
				this.winnings = 0.0;
				
				Record load;
				
				do
				{
					load = new Record(this.getEmail(), rs.getInt("league_season_id"));
					this.losses = load.losses;
					this.wins = load.wins;
					this.pushes = load.pushes;
					this.winnings = load.winnings;
				}
				while (rs.next());
			}
			else //Nothing in the result set.
			{
				log.severe("Nothing in the result set for query.");
				log.severe("Query Executed: " + strquery);
			}
			
			conn.close();
		} 
		catch (SQLException e) 
		{
			log.severe("SQL Exception processing!");
			log.info("Connection String: " + strurl + "&" + struser + "&" + strpass);
			log.info(e.getMessage());
		}
		
	}
	
	public int getPushes()
	{
		return pushes;
	}
	
	public void setPushes(int update)
	{
		pushes = update;
	}
	
	public int getLosses()
	{
		return losses;
	}
	
	public void setLosses(int update)
	{
		losses = update;
	}
	
	public int getWins()
	{
		return wins;
	}
	
	public void setWins(int update)
	{
		wins = update;
	}
	
	public String getLinitial()
	{
		return linitial;
	}
	
	public void setLinitial(String update)
	{
		linitial = update;
	}
	
	public String getLname()
	{
		return lname;
	}
	
	public void setLname(String update)
	{
		lname = update;
	}
	
	public String getFname()
	{
		return fname;
	}
	
	public void setFname(String update)
	{
		fname = update;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public void setEmail(String update)
	{
		email = update;
	}
	
	public double getWinnings()
	{
		return winnings;
	}
	
	public void setWinnings(double update)
	{
		winnings = update;
	}
}

