package com.lthoi;
import java.io.*;
import java.sql.*;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

/** This resource is primarily for clients to use during the season, it does not include the ability to modify league seasons.  **/
public class League_Season 
{
	private int league_season_id;
	private int season;
	private String league_name;
	private int num_players;
	private int position;
	private int wins;
	private int losses;
	private int pushes;
	private double winnings;	
		
	//This constructor is for testing purposes and just returns Bojan where he was as of half-time of the week 3 games.
	public League_Season(int user_id, int league_season_id)
	{		
		String strurl = "";
        String struser = "";
        String strpass = "";
		String strquery;
		final Logger log = Logger.getLogger(League_Season.class.getName());
		
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
			log.severe("Unable to create connection string.");
			log.severe(e.getMessage());
		}
		
		Connection conn = null;
		
		try 
		{
			conn = DriverManager.getConnection(strurl, struser, strpass);			
			
			strquery = "Select * From lthoidb.League_Seasons";
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				this.setLeague_Season_ID(rs.getInt("league_season_id"));
			}
			else //Nothing in the result set.
			{
				log.warning("Nothing in the result set for query.");
			}
			
			conn.close();
		} 
		catch (SQLException e) 
		{
			log.severe("SQL Exception on connection!");
			log.info("Connection String: " + strurl + "&" + struser + "&" + strpass);
			log.info(e.getMessage());
		}
		
		
		
		//this.league_season_id = 1;
		this.season = 2015;
		this.num_players = 8;
		this.position = 3;
		this.wins = 4;
		this.losses = 3;
		this.pushes = 1;
		this.winnings = 28.57;
	}
	
	public int getLeague_Season_ID()
	{
		return league_season_id;
	}
	
	public void setLeague_Season_ID(int update)
	{
		league_season_id = update;
	}
	
	public int getPosition()
	{
		return position;
	}
	
	public void setPosition(int update)
	{
		position = update;
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
	
	public String getLeague_Name()
	{
		return league_name;
	}
	
	public void setLeague_Name(String update)
	{
		league_name = update;
	}
	
	public double getWinnings()
	{
		return winnings;
	}
	
	public void setWinnings(double update)
	{
		winnings = update;
	}
	
	public int getSeason()
	{
		return season;
	}
	
	public void setSeason(int update)
	{
		season = update;
	}
	
	public int getNum_Players()
	{
		return num_players;
	}
	
	public void setNum_Players(int update)
	{
		season = num_players;
	}
}

