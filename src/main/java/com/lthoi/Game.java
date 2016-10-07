package com.lthoi;
import java.util.Date;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/** This is used to manage the active user. **/
public class Game 
{
	private int id;
	private String home_team;
	private String home_city;
	private String away_team;
	private String away_city;
	private double home_line;
	private Date start;
	private Date freeze;
	private String week;
	private int home_score;
	private int away_score;
	private int isFinished;
	private ArrayList<Player> home_bets;
	private ArrayList<Player> away_bets;
	
	private int mins_remaining;
	private int secs_remaining;
	private double user_net_home_bet;
		
	public Game()
	{
		
	}
	
	
	public Game(int game_id, int league_season_id, String email)
	{		
		String strurl = "";
        String struser = "";
        String strpass = "";
		String strquery;
		int timetoremove = 0;
		double bet_amount = 0;
		double house_amount = 1;
		int user_id = -1;
		final Logger log = Logger.getLogger(Game.class.getName());
		
		log.info("Constructing Game Entity.");
		log.info("league season passed: " + league_season_id);
		log.info("Game id passed: " + game_id);
		
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
			
			//TODO: This is an inefficient query as it unconditionally joins league seasons just so that I can get the seaason and freeze time values and pass it back.
			strquery = "SELECT g.isFinished AS isFinished, ls.freeze_minutes as freeze_minutes, g.home_score as home_score, g.away_score as away_score, g.mins_remaining as mins_remaining, g.secs_remaining as secs_remaining, g.game_id AS game_id, ht.name AS home_name, ht.city AS home_city, at.name AS away_name, at.city AS away_city, w.name_short AS week_short, g.home_line AS home_line, g.start AS start FROM lthoidb.Games g INNER JOIN lthoidb.Teams at on at.team_id = g.away_team INNER JOIN lthoidb.Teams ht on ht.team_id = g.home_team INNER JOIN lthoidb.Weeks w on w.id = g.week_id INNER JOIN lthoidb.League_Seasons ls ON ls.season = w.season WHERE g.game_id = " + game_id + " AND ls.league_season_id = " + league_season_id + ";";
			
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				this.id = game_id;
				this.home_city = rs.getString("home_city");
				this.home_team = rs.getString("home_name");
				this.away_city = rs.getString("away_city");
				this.away_team = rs.getString("away_name");
				this.home_line = rs.getDouble("home_line");
				this.mins_remaining = rs.getInt("mins_remaining");
				this.secs_remaining = rs.getInt("secs_remaining");
				this.home_score = rs.getInt("home_score");
				this.away_score = rs.getInt("away_score");
				this.start = rs.getTimestamp("start");
				this.isFinished = rs.getInt("isFinished");
				timetoremove = rs.getInt("freeze_minutes");
				
				Calendar date = Calendar.getInstance();
				date.setTimeInMillis(this.start.getTime());
				date.add(Calendar.MINUTE, (-1 * timetoremove));
				this.freeze = new Date();
				this.freeze.setTime(date.getTimeInMillis());
				log.info("Freeze Date: " + date.toString());
				
			}
			else //Nothing in the result set.
			{
				log.severe("Nothing in the result set for query.");
				log.severe("Query Executed: " + strquery);
			}
			
			
			//Calculate the bet amount and the house_bet amount.
			strquery = "SELECT MAX(ls.bet_amount) AS bet_amount, COUNT(ls.bet_amount) AS players FROM League_Seasons ls INNER JOIN League_Season_User_Map lsum ON lsum.league_season_id = ls.league_season_id WHERE lsum.league_season_id = " + league_season_id + ";";
			rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				bet_amount = rs.getDouble("bet_amount");
				house_amount = bet_amount / (rs.getInt("players") - 1);
				
			}
			else //Nothing in the result set.
			{
				log.severe("Unable to find the bet_amount or number of players for the league_season.");
				log.severe("Query Executed: " + strquery);
			}
			
			
			//Retrieve the current user's user_id
			//Calculate the bet amount and the house_bet amount.
			strquery = "Select u.user_id FROM Users u WHERE u.email = '" + email + "';";
			rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				user_id = rs.getInt("user_id");
				
			}
			else //Nothing in the result set.
			{
				log.severe("Unable to find the user's id!");
				log.severe("Query Executed: " + strquery);
			}
			
			
			//Get the bets for each side.
			this.home_bets = new ArrayList<Player>();
			this.away_bets = new ArrayList<Player>();
			this.user_net_home_bet = 0;
			strquery = "SELECT b.user_id, b.home FROM Bets b WHERE b.game_id = " + this.id + " AND b.league_season_id = " + league_season_id + ";";
			rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				do				
					if (rs.getInt("home") == 1)
					{
						this.home_bets.add(new Player(rs.getInt("user_id"), league_season_id));
						
						if (rs.getInt("user_id") == user_id)
						{
							this.user_net_home_bet += bet_amount;
						}
						else
						{
							this.user_net_home_bet += house_amount;
						}
					}
					else
					{
						this.away_bets.add(new Player(rs.getInt("user_id"), league_season_id));
						
						if (rs.getInt("user_id") == user_id)
						{
							this.user_net_home_bet -= bet_amount;
						}
						else
						{
							this.user_net_home_bet -= house_amount;
						}
					}
				while (rs.next());
			}
			else //Nothing in the result set.
			{
				log.info("Nothing bet on this game.");
				log.info("Query Executed: " + strquery);
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
	
	public int getID()
	{
		return id;
	}
	
	public void setID(int update)
	{
		id = update;
	}
	
	public String gethome_team()
	{
		return home_team;
	}
	
	public void sethome_team(String update)
	{
		home_team = update;
	}
	
	public String gethome_city()
	{
		return home_city;
	}
	
	public void sethome_city(String update)
	{
		home_team = update;
	}
	
	public String getaway_team()
	{
		return away_team;
	}
	
	public void setaway_team(String update)
	{
		away_team = update;
	}
	
	public String getaway_city()
	{
		return away_city;
	}
	
	public void setaway_city(String update)
	{
		away_team = update;
	}
	
	public double gethome_line()
	{
		return home_line;
	}
	
	public void sethome_line(double update)
	{
		home_line = update;
	}
	
	public Date getstart ()
	{
		return start;
	}
	
	public void setstart(Date update)
	{
		start = update;
	}
	
	public Date getfreeze ()
	{
		return freeze;
	}
	
	public void setfreeze(Date update)
	{
		freeze = update;
	}
	
	public int gethome_score()
	{
		return home_score;
	}
	
	public void sethome_score(int update)
	{
		home_score = update;
	}
	
	public int getaway_score()
	{
		return away_score;
	}
	
	public void setaway_score(int update)
	{
		away_score = update;
	}
	
	public ArrayList<Player> gethome_bets()
	{
		return home_bets;
	}
	
	public void sethome_bets(ArrayList<Player> update)
	{
		home_bets = update;
	}
	
	public ArrayList<Player> getaway_bets()
	{
		return away_bets;
	}
	
	public void setaway_bets(ArrayList<Player> update)
	{
		away_bets = update;
	}
	
	public int getmins_remaining()
	{
		return mins_remaining;
	}
	
	public void setmins_remaining(int update)
	{
		mins_remaining = update;
	}
	
	public int getsecs_remaining()
	{
		return secs_remaining;
	}
	
	public void setsecs_remaining(int update)
	{
		secs_remaining = update;
	}
	
	public double getuser_net_home_bet()
	{
		return user_net_home_bet;
	}
	
	public void setuser_net_home_bet(double update)
	{
		user_net_home_bet = update;
	}
	
	public int getisFinished()
	{
		return isFinished;
	}
	
	public void setisFinished(int update)
	{
		isFinished = update;
	}
}
