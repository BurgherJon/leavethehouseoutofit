package com.lthoi;

import com.google.api.server.spi.config.Api;

import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.utils.SystemProperty;
import java.util.logging.Logger;
import com.google.api.server.spi.response.InternalServerErrorException;
import com.google.api.server.spi.response.UnauthorizedException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.google.appengine.api.users.User;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(name = "playerAPI",
version = "v1",
scopes = {Constants.EMAIL_SCOPE}, 
clientIds = {Constants.WEB_CLIENT_ID})

public class PlayerAPI 
{

	@ApiMethod(name = "getGame")
    public Game getGame(User guser, @Named("league_season_id") int league_season_id, @Named("game_id") int game_id) throws UnauthorizedException
    {
        Game response = new Game(game_id, league_season_id, guser.getEmail());
       
        return response;
    }
	
	@ApiMethod(name = "getCurrentWeek")
    public Week getCurrentWeek(User guser) throws UnauthorizedException 
    {
        Week response = new Week();
        
        String strurl = "";
        String struser = "";
        String strpass = "";
		String strquery;
		final Logger log = Logger.getLogger(PlayerAPI.class.getName());
		
		log.info("Retreiving Current Week.");
		
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
			
			strquery = "Select CurrentWeek From lthoidb.SysInfo";
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				response = new Week(rs.getInt("CurrentWeek"));
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
        
        return response;
    }
	
	@ApiMethod(name = "getMe")
    public Me getMe(User guser) throws UnauthorizedException 
    {
        final Logger log = Logger.getLogger(PlayerAPI.class.getName());
		log.info("Running the GetMe function");
        
		Me response = new Me(guser.getEmail());
		return response;
    }
    
    @ApiMethod(name = "getGames", scopes = {Constants.EMAIL_SCOPE}, clientIds = {Constants.WEB_CLIENT_ID})
    public ArrayList<Game> getGames(User guser, @Named("league_season_id") int league_season_id, @Nullable @Named("week") int week) throws UnauthorizedException 
    {
    	ArrayList<Game> response = new ArrayList<Game>();
    	
    	String strurl = "";
        String struser = "";
        String strpass = "";
		String strquery;
		final Logger log = Logger.getLogger(PlayerAPI.class.getName());
		
		log.info("Retreiving Relevant Games.");
		log.info("league season passed: " + league_season_id);
		
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
			
			//Careful, there's no ; at the end of this because you may add week number.
			strquery = "SELECT game_id FROM lthoidb.Games g INNER JOIN lthoidb.Weeks w ON w.id = g.week_id INNER JOIN lthoidb.League_Seasons ls ON ls.season = w.season WHERE ls.league_season_id = " + league_season_id;
			log.info(strquery);
			
			if (week != 0)
			{
				log.info("Week passed: " + week);
				strquery = strquery + " AND w.number = " + week + ";";
			}
			else
			{
				log.info("No week was passed");
				strquery = strquery + ";";
			}
			
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				do 
				{
					log.info("Creating game with...");
					log.info("Game id: " + rs.getInt("game_id"));
					log.info("League Season: " + league_season_id);
					log.info("User Email: " + guser.getEmail());
					
					response.add(new Game(rs.getInt("game_id"), league_season_id, guser.getEmail()));
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
    	
    	
    	return response;
    }
    
    @ApiMethod(name = "getActiveLeagueSeasons", scopes = {Constants.EMAIL_SCOPE}, clientIds = {Constants.WEB_CLIENT_ID})
    public ArrayList<League_Season> getActiveLeagueSeasons(User guser) throws UnauthorizedException 
    {
    	ArrayList<League_Season> response;
    	response = new ArrayList<League_Season> ();
    	
    	String strurl = "";
        String struser = "";
        String strpass = "";
		String strquery;
		final Logger log = Logger.getLogger(PlayerAPI.class.getName());
		
		log.info("Retreiving League Seasons.");
		log.info("User logged in: " + guser.getEmail());
		
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
			
			strquery = "SELECT lsum.league_season_id, u.user_id FROM League_Season_User_Map lsum INNER JOIN Users u ON u.user_id = lsum.user_id WHERE u.email = '" + guser.getEmail() + "';";
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{	
				do 
				{
					response.add(new League_Season(rs.getInt("user_id"), rs.getInt("league_season_id")));
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
    	
    	
    	return response;
    }
    

    
    
    @ApiMethod(name = "setBet", scopes = {Constants.EMAIL_SCOPE}, clientIds = {Constants.WEB_CLIENT_ID})
    public Bet setBet(User guser, @Named("team_name") String team_name, @Named("league_season_id") int league_season_id, @Nullable @Named("id") int id) throws UnauthorizedException  
    {	
    	Bet response = new Bet(guser.getEmail(), team_name, league_season_id);
    	if (id != 0)
    	{
    		this.deleteBet(guser, id, "", 0);
    	}
    	
    	return response; 
    }
    
    @ApiMethod(name = "deleteBet", scopes = {Constants.EMAIL_SCOPE}, clientIds = {Constants.WEB_CLIENT_ID})
    public void deleteBet (User guser, @Named("id") int id, @Named("team_name") String team_name, @Named("league_season_id") int league_season_id) throws UnauthorizedException 
    {
    	String strurl = "";
        String struser = "";
        String strpass = "";
        String strquery;
		final Logger log = Logger.getLogger(PlayerAPI.class.getName());
		
		log.info("In the deleteBet method.");
		
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
			
			strquery = "";
			
			if (id != 0)
			{
				log.info("An id was passed, deleting: " + id);
				strquery = "DELETE FROM Bets WHERE bet_id = " + id + ";";
				conn.createStatement().executeUpdate(strquery);
			}
			else
			{
				log.info("No id was passed, deleting current bet weeks with the following:");
				log.info("league_season_id: " + league_season_id);
				log.info("team bet on: " + team_name);
				log.info("email: " + guser.getEmail());
				strquery = "SELECT * FROM Bets b INNER JOIN Games g ON g.game_id = b.game_id INNER JOIN SysInfo si ON g.week_id = si.CurrentWeek INNER JOIN Users u ON u.user_id = b.user_id INNER JOIN Teams ht ON g.home_team = ht.team_id INNER JOIN Teams at ON at.team_id = g.away_team WHERE league_season_id = " + league_season_id + " AND u.email = '" + guser.getEmail() + "' AND (at.name = '" + team_name + "' OR ht.name = '" + team_name + "');";
				ResultSet rs = conn.createStatement().executeQuery(strquery);
			
				if (rs.next()) //Anything in the result set?
				{
					deleteBet(guser, rs.getInt("bet_id"), "", 0);
				}
				else //Nothing in the result set.
				{
					log.severe("Nothing in the result set for query.");
					log.severe("Query Executed: " + strquery);
				}
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
   
    @ApiMethod(name = "getTeam", scopes = {Constants.EMAIL_SCOPE}, clientIds = {Constants.WEB_CLIENT_ID})
    public Team getTeam (@Named("id") int id, @Nullable @Named("city") String city, @Nullable @Named("team") String team) throws InternalServerErrorException, UnauthorizedException 
    {
    	Team result;
    	final Logger log = Logger.getLogger(PlayerAPI.class.getName());
    	
    	if (id != 0)
    	{
    		log.info("Was passed a non-zero team ID so getting the team name and city by id.");
    		result = new Team(id);
    	}
    	else
    	{
    		if (city != null)
    		{
    			log.info("The city value was passed, so retreiving team info by city.");
    			result = new Team(city, 1);
    		}
    		else if (team != null)
    		{
    			log.info("The team name was passed, so retreiving team info by team name.");
    			result = new Team(team, 0);
    		}
    		else
    		{
    			log.severe("There was a zero in the id, but no team name or city!");
    			throw new InternalServerErrorException("There was a zero in the id, but no team name or city!");
    		}
    	}
    	return result;
    }
    
    @ApiMethod(name = "getUser")
    public Player getUser (@Named("email") String email, @Named("league_season") int league_season_id) throws UnauthorizedException 
    {
    	final Logger log = Logger.getLogger(PlayerAPI.class.getName());
    	
    	return new Player(email, league_season_id);
    }

    @ApiMethod(name = "getUsers")
    public ArrayList<Player> getUsers (@Named("league_season") int league_season_id)
    {
    	
    	ArrayList<Player> response = new ArrayList<Player>();
    	String strurl = "";
        String struser = "";
        String strpass = "";
		String strquery;
		final Logger log = Logger.getLogger(PlayerAPI.class.getName());
		
		log.info("In the getUsers method.");
		log.info("League Season passed: " + league_season_id);
		
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
			
			strquery = "Select user_id From lthoidb.League_Season_User_Map WHERE league_season_id = " + league_season_id + ";";
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				do
				{
					response.add(new Player(rs.getInt("user_id"), league_season_id));
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
    	
    	
    	return response;
    }
    
    //The sloppy part about this is that there is no transactionality.  If it fails after processing x house bets
    //but before the last one, then the next run of the cronjub will process duplicate house bets.
    @ApiMethod(name = "cronJob")
    public void cronJob ()
    {
    	
    	String strurl = "";
        String struser = "";
        String strpass = "";
		String strquery;
		int updates = 0;
		Bet workingbet;
		final Logger log = Logger.getLogger(PlayerAPI.class.getName());
		
		log.info("In the Cron Job.");
				
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
			
			//Query will be updated to determine if there are any games that need to be frozen.
			strquery = "SELECT b.bet_id, b.league_season_id, b.user_id FROM Bets b WHERE b.game_id IN (SELECT g.game_id FROM Games g WHERE g.START <= NOW() - INTERVAL (SELECT ls.freeze_minutes FROM League_Seasons ls WHERE ls.league_season_id = b.league_season_id) MINUTE) AND (b.hbprocessed <> 1 OR b.hbprocessed IS NULL);";
			log.info("Made connection, going to run: " + strquery);
			ResultSet rs = conn.createStatement().executeQuery(strquery);
			if (rs.next()) //Anything in the result set?
			{
				strquery = "UPDATE Bets SET hbprocessed = 1 WHERE bet_id IN (";
				log.info("found games to update, stepping through them.");
				do
				{
					//Generate the house bets
					log.info("Working with bet: " + rs.getInt("bet_id"));
					workingbet = new Bet(rs.getInt("bet_id"));
					workingbet.setLeague_Season_ID(rs.getInt("league_season_id"));
					workingbet.generatehousebets(rs.getInt("user_id"));
					
					//Update the query to mark the house bets processed.
					if (updates == 0)
					{
						strquery = strquery + workingbet.getId();
					}
					else
					{
						strquery = strquery + ", " + workingbet.getId();
					}
										
					//Note that there is at least one that needs to be updated.
					updates = 1;
				} 
				while (rs.next());
				
				//Finish the query and run it if there were updates.
				if (updates == 1)
				{
					strquery = strquery + ");";
							
					//Run the update query.
					conn.createStatement().executeUpdate(strquery);
				}
			}
			else //Nothing in the result set.
			{
				log.severe("No house bets necessary.");
				log.severe("Query Executed: " + strquery);
			}
			
			conn.close();
		} 
		catch (SQLException e) 
		{
			log.severe("SQL Exception processing!");
			log.severe("Connection String: " + strurl + "&" + struser + "&" + strpass);
			log.severe(e.getMessage());
		}
    	
    }

}

