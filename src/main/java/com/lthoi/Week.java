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
public class Week 
{
	private int number;
	private int season;
	private String short_name;
	private String long_name;
	private Date start;
		
	//This constructor is for testing purposes and just creates week 3 of the 2015 season.
	public Week()
	{		
		final Logger log = Logger.getLogger(League_Season.class.getName());
		log.info("This is the blank constructor for Week, shouldn't ever be here.");
	}
	
	public Week(int id)
	{
		String strquery;
		final Logger log = Logger.getLogger(League_Season.class.getName());
		
		log.info("In the constructor for Week with the id passed.");
		log.info("ID passed: " + id);
		
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
			
			strquery = "Select * From lthoidb.Weeks WHERE id = '" + id + "';";
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				this.setLong_Name(rs.getString("name_long"));
				this.setShort_Name(rs.getString("name_short"));
				this.setNumber(rs.getInt("number"));
				this.setSeason(rs.getInt("season"));
				//TODO: Load all of the games and figure out when the first one starts.
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
	
	public int getNumber()
	{
		return number;
	}
	
	public void setNumber(int update)
	{
		this.number = update;
	}
	
	public int getSeason()
	{
		return season;
	}
	
	public void setSeason(int update)
	{
		season = update;
	}
	
	public Date getStart()
	{
		return start;
	}
	
	public void setStart(Date update)
	{
		start = update;
	}
	
	public String getShort_Name()
	{
		return short_name;
	}
	
	public void setShort_Name(String update)
	{
		short_name = update;
	}
	
	public String getLong_Name()
	{
		return long_name;
	}
	
	public void setLong_Name(String update)
	{
		long_name = update;
	}
	
}

