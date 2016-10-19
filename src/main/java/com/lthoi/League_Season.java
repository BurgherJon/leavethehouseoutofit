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
		String strquery;
		final Logger log = Logger.getLogger(League_Season.class.getName());
		
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
			
			strquery = "Select * From lthoidb.League_Seasons";
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				this.setLeague_Season_ID(rs.getInt("league_season_id"));
				this.league_name = rs.getString("league_name");
				this.num_players = 0;
				this.position = 0;
				this.season = rs.getInt("season");
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
			log.info("Connection String: " + env.db_url + "&" + env.db_user + "&" + env.db_password);
			log.info(e.getMessage());
		}
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

