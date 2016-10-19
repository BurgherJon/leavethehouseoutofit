package com.lthoi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

/** This is used to manage the active user. **/
public class Team
{
	private int id;
	private String name;
	private String city;
		
	//This constructor is for testing purposes and just creates the Steelers.
	public Team()
	{		
		this.id = 1; //Note that the Steelers are not actually ID 1 in previous versions.
		this.name = "Steelers";
		this.city = "Pittsburgh";
	}
	
	public Team (int id)
	{
		String strquery;
		final Logger log = Logger.getLogger(League_Season.class.getName());
		
		log.info("In the constructor for Team with the id passed.");
		log.info("ID passed: " + id);
		
		this.id = id;
		
		Environment env = new Environment();
        try
		{
			Class.forName(env.db_driver);
		}
		catch (ClassNotFoundException e)
		{
			log.severe("Unable to load database driver.");
			log.severe(e.getMessage());
		}
		
		Connection conn = null;
		
		try 
		{
			conn = DriverManager.getConnection(env.db_url, env.db_user, env.db_password);		
			
			strquery = "Select * From lthoidb.Teams WHERE team_id = '" + id + "';";
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				this.setName(rs.getString("name"));
				this.setCity(rs.getString("city"));
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
			log.info("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
			log.info(e.getMessage());
		}
	}
	
	//If the user passes the value of 0 for the id, then the team's info can be looked up from either the team name or the city.  The expectation here is that a string value representing one of the the two will be passed.  If it is the city than this "isCity" indicator should be 1.  Of it is the team name then it should be 0.
	public Team (String value, int isCity)
	{
		String strquery;
		final Logger log = Logger.getLogger(League_Season.class.getName());
		
		log.info("In the constructor for Team with the city or team name passed.");
		log.info("The indicator for whether it is the city is:  " + value);
		log.info("isCity indicator: " + isCity);
		
		Environment env = new Environment();
        try
		{
			Class.forName(env.db_driver);
		}
		catch (ClassNotFoundException e)
		{
			log.severe("Unable to load database driver.");
			log.severe(e.getMessage());
		}
 
		Connection conn = null;
		
		try 
		{
			conn = DriverManager.getConnection(env.db_url, env.db_user, env.db_password);			
			
			if (isCity == 1)
			{
				strquery = "Select * From lthoidb.Teams WHERE city = '" + value + "';";
			}
			else
			{
				strquery = "Select * FROM lthoidb.Teams WHERE name = '" + value +"';";
			}
			
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				this.setID(rs.getInt("team_id"));
				this.setName(rs.getString("name"));
				this.setCity(rs.getString("city"));
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
			log.info("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
			log.info(e.getMessage());
		}
	}
	
	
	public int getID()
	{
		return id;
	}
	
	public void setID(int update)
	{
		this.id = update;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(String update)
	{
		this.name = update;
	}
	
	public String getCity()
	{
		return city;
	}
	
	public void setCity(String update)
	{
		city = update;
	}
	
}

