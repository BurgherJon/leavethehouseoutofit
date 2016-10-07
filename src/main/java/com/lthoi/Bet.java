package com.lthoi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

/** This is used to manage the active user. **/
public class Bet
{
	private int id;
	private String email;
	private String picked_team;
	private String picked_city;
	private String against_team;
	private String against_city;
	private int week_number;
	private String week_short;
	private String week_long;
	private int league_season_id;
	private double line;
	private int isHouseBet;
	private String result;
	private Date start;
		
	//This constructor is for creating an entirely new bet.  It will add to the database.
	public Bet(String email, String team_name, int league_season_id)
	{		
		//Variables will be used to build the insert query.
		int user_id = 0;
		int game_id = 0;
		int home = -1;
		double bet_amount = 0;
		
		String strurl = "";
        String struser = "";
        String strpass = "";
		String strquery;
		final Logger log = Logger.getLogger(Bet.class.getName());
		
		log.info("Creating the bet.");
		log.info("league season passed: " + league_season_id);
		log.info("email passed: " + email);
		log.info("team passed: " + team_name);
		
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
			
			//Todo: this is an UGLY query as it unnaturally retreves the user's id
			//Get all of this week's games so that we can find the one they wanted to bet on.
			strquery = "SELECT ls.bet_amount AS bet_amount, ls.league_season_id, u.user_id, g.game_id, ht.name AS home_team, at.name AS away_team FROM Games g INNER JOIN Teams ht ON ht.team_id = g.home_team INNER JOIN Teams at ON at.team_id = g.away_team RIGHT OUTER JOIN SysInfo si ON si.CurrentWeek = g.week_id JOIN Users u JOIN League_Seasons ls WHERE ls.league_season_id = " + league_season_id + " AND u.email = '" + email + "';";
			
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				do
				{
					
					log.info("Trying " + rs.getString("home_team") + "vs" + rs.getString("away_team"));
					
					if (rs.getString("home_team").equals(team_name))
					{	
						user_id = rs.getInt("user_id");
						game_id = rs.getInt("game_id");
						home = 1;
						bet_amount = rs.getDouble("bet_amount");
						log.info("Matched to " + rs.getString("home_team"));
					}
					else if (rs.getString("away_team").equals(team_name))
					{
						user_id = rs.getInt("user_id");
						game_id = rs.getInt("game_id");
						home = 0;
						bet_amount = rs.getDouble("bet_amount");
						log.info("Matched to " + rs.getString("away_team"));
					}
				}
				while (rs.next());
				
			}
			else //Nothing in the result set.
			{
				log.severe("Nothing in the result set for query.");
				log.severe("Query Executed: " + strquery);
			}
			
			//Check if it found the game.
			//TODO: this should really throw an exception.
			if (user_id == 0)
			{
				log.severe("Didn't find the game!!!");
				log.severe(strquery);
			}
			else
			{
				//Check for an existing bet on the game, by the player... if they have one, remove it.
				strquery = "DELETE FROM Bets WHERE game_id = " + game_id + " AND user_id = " + user_id + " AND league_season_id = " + league_season_id + ";";
				log.info("Delete any existing bets from this player on this game in this league season: " + strquery);
				conn.createStatement().executeUpdate(strquery);
				
				log.info("Putting in the bet " + strquery);
				strquery = "INSERT INTO lthoidb.Bets(user_id, game_id, league_season_id, home, bet_amount) VALUES (" + user_id + ", " + game_id + ", " + league_season_id + ", " + home + ", " + bet_amount + ");";
				conn.createStatement().executeUpdate(strquery);
			}
			
			//Get the details of the bet from the database.
			strquery = "SELECT b.bet_id AS bet_id, b.league_season_id AS league_season_id, g.start AS start, w.number AS week_number, w.name_short AS week_short, w.name_long AS week_long, ht.name AS home_team, at.name AS away_team, ht.city AS home_city, at.city AS away_city, g.home_line AS home_line FROM Bets b INNER JOIN Games g ON b.game_id = g.game_id INNER JOIN Weeks w ON g.week_id = w.id INNER JOIN Teams ht ON ht.team_id = g.home_team INNER JOIN Teams at ON at.team_id = g.away_team WHERE b.game_id = " + game_id + " AND b.user_id = " + user_id + " AND b.league_season_id = " + league_season_id + ";";
			
			rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				this.id = rs.getInt("bet_id");
				this.isHouseBet = 0;
				this.league_season_id = rs.getInt("league_season_id");
				this.result = "Good Luck!";
				this.start = rs.getDate("start");
				this.week_long = rs.getString("week_long");
				this.week_short = rs.getString("week_short");
				this.week_number = rs.getInt("week_number");
				if (team_name.equals(rs.getString("home_team")))
				{
					this.picked_team = rs.getString("home_team");
					this.picked_city = rs.getString("home_city");
					this.against_city = rs.getString("away_city");
					this.against_team = rs.getString("away_team");
					this.line = rs.getDouble("home_line");
				}
				else
				{
					this.picked_team = rs.getString("away_team");
					this.picked_city = rs.getString("away_city");
					this.against_city = rs.getString("home_city");
					this.against_team = rs.getString("home_team");
					this.line = rs.getDouble("home_line");
				}
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
	
	//Note that this constructor will NOT fetch information from the database... you must populate it.
	public Bet(int id)
	{
		this.id = id;
	}
	
	public void generatehousebets(int usernum)
	{
		final Logger log = Logger.getLogger(Bet.class.getName());
		
		String strurl = "";
        String struser = "";
        String strpass = "";
		String strquery;
		int index = 0;
		double bet_amount = 0.0;
		ArrayList<Integer> players = new ArrayList<Integer>();
		
		log.info("Generating House Bets for " + this.id);
		
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
			
			//Going to require a select query to get all of the users.
			strquery = "SELECT lsum.user_id, ls.bet_amount FROM League_Season_User_Map lsum INNER JOIN League_Seasons ls ON ls.league_season_id = lsum.league_season_id  WHERE lsum.league_season_id = " + this.league_season_id + ";";
			
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
								
				log.info("Getting the bet amount.");
				bet_amount = rs.getDouble("bet_amount");
				
				do
				{
					if (rs.getInt("user_id") != usernum)
					{
						log.info("Adding a user");
						players.add(rs.getInt("user_id"));
					}
				}
				while (rs.next());
				
				bet_amount = bet_amount / (players.size());
				
			}
			else //Nothing in the result set.
			{
				log.severe("Nothing in the result set for query.");
				log.severe("Query Executed: " + strquery);
			}
				
			log.info("To the inserts.");
			for (index = 0; index < players.size(); index++)
			{
				strquery = "INSERT INTO House_Bets (parent_bet_id, user_id, bet_amount) VALUES (" + this.id + ", " + players.get(index) + ", " + bet_amount + ");";
				log.info("Adding one to DB: " + strquery);
				conn.createStatement().executeUpdate(strquery);
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
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int update)
	{
		this.id = update;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public void setEmail(String update)
	{
		email = update;
	}
	
	public Date getStart()
	{
		return start;
	}
	
	public void setStart(Date update)
	{
		start = update;
	}
	
	public String getPicked_City()
	{
		return picked_city;
	}
	
	public void setPicked_City(String update)
	{
		picked_city = update;
	}
	
	public String getAgainst_Team()
	{
		return against_team;
	}
	
	public void setAgainst_Team(String update)
	{
		against_team = update;
	}
	
	public String getWeek_Short()
	{
		return week_short;
	}
	
	public void setWeek_Short(String update)
	{
		week_short = update;
	}
	
	public String getWeek_Long()
	{
		return week_long;
	}
	
	public void setWeek_Long(String update)
	{
		week_long = update;
	}
	
	public double getLine()
	{
		return line;
	}
	
	public void setLine(double update)
	{
		line = update;
	}
	
	public int getIsHouseBet()
	{
		return isHouseBet;
	}
	
	public void setIsHouseBet(int update)
	{
		isHouseBet = update;
	}
	
	public int getLeague_Season_ID()
	{
		return this.league_season_id;
	}
	
	public void setLeague_Season_ID(int update)
	{
		this.league_season_id = update;
	}
	
	public String getResult()
	{
		return result;
	}
	
	public void setResult(String update)
	{
		result = update;
	}	
	
	public String getPicked_Team()
	{
		return picked_team;
	}
	
	public void setPicked_Team(String update)
	{
		picked_team = update;
	}
	
	public String getAgainst_City()
	{
		return against_city;
	}
	
	public void setAgainst_City(String update)
	{
		against_city = update;
	}
	
	public int getWeek_Number()
	{
		return week_number;
	}
	
	public void setWeek_Number(int update)
	{
		week_number = update;
	}
	
	
}

