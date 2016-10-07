package com.lthoi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

/** This is used to manage the active user. **/
public class Player
{
	private int league_season_id;
	private String email;
	private String fname;
	private String linitial;
	private int wins;
	private int losses;
	private int pushes;
	private double winnings;
		
	//TODO: This constructor is a remnant of the days as a test harness, please delete.
	public Player(String name)
	{
		this.setEmail(name);;
	}
	
	
	//This constructor takes the email and the league_season_id and returns relevant informaiton.
	//TODO: Right now only pulls user info... nor record, etc...
	public Player(String email, int league_season_id)
	{		
		String strurl = "";
        String struser = "";
        String strpass = "";
		String strquery;
		final Logger log = Logger.getLogger(Player.class.getName());
		
		log.info("In the constructor for User.");
		log.info("Email passed: " + email);
		log.info("League Season passed: " + league_season_id);
		
		this.setEmail(email);
		this.setLeague_season_id(league_season_id);
		
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
			
			strquery = "Select * From lthoidb.Users WHERE email = '" + email + "';";
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				this.setFname(rs.getString("fname"));
				this.setLinitial(rs.getString("linitial"));
			}
			else //Nothing in the result set.
			{
				log.severe("Nothing in the result set for query.");
				log.severe("Query Executed: " + strquery);
			}
			
			conn.close();
			
			Record load = new Record(email, league_season_id);
			this.wins = load.wins;
			this.losses = load.losses;
			this.pushes = load.pushes;
			this.winnings = load.winnings;
		} 
		catch (SQLException e) 
		{
			log.severe("SQL Exception processing!");
			log.info("Connection String: " + strurl + "&" + struser + "&" + strpass);
			log.info(e.getMessage());
		}
	}
	
	public Player(int user_id, int league_season_id)
	{		
		String strurl = "";
        String struser = "";
        String strpass = "";
		String strquery;
		final Logger log = Logger.getLogger(Player.class.getName());
		
		log.info("In the constructor for User that's based on user_id.");
		log.info("User ID passed: " + user_id);
		log.info("League Season passed: " + league_season_id);
		
		this.setLeague_season_id(league_season_id);
		
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
			
			strquery = "Select * From lthoidb.Users WHERE user_id = '" + user_id + "';";
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				this.setFname(rs.getString("fname"));
				this.setLinitial(rs.getString("linitial"));
				this.setEmail(rs.getString("email"));
			}
			else //Nothing in the result set.
			{
				log.severe("Nothing in the result set for query.");
				log.severe("Query Executed: " + strquery);
			}
			
			Record load = new Record(email, league_season_id);
			this.wins = load.wins;
			this.losses = load.losses;
			this.pushes = load.pushes;
			this.winnings = load.winnings;
			
			conn.close();
		} 
		catch (SQLException e) 
		{
			log.severe("SQL Exception processing!");
			log.info("Connection String: " + strurl + "&" + struser + "&" + strpass);
			log.info(e.getMessage());
		}
	}
	
	public int getLeague_season_id()
	{
		return league_season_id;
	}
	
	public void setLeague_season_id(int update)
	{
		this.league_season_id = update;
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
		this.winnings = update;
	}
}

