var templeague_season_id = 7;
var tempemail = "";  // !No Caution anymore, I overwrite it in getCurrentWeek().
//var tempweek = 22; // !CAUTION!
//var currentDateTime = new Date("2015-09-23T15:00:00.000-04:00"); // !CAUTION! // before all games
//var currentDateTime = new Date("2016-09-11T15:00:00.000-04:00"); // !CAUTION! // midway through games
var currentDateTime = new Date();

function getCurrentWeek()
{

	var requestData = {}; // request parameters
	var currentWeek = 1; //
	
		//document.getElementById('getCurrentWeek').innerHTML = testText;
		
		gapi.client.playerAPI.getMe().execute(function(resp)  
			{
				
				if (!resp.code) 
				{
					tempemail = resp.email;
				}
				else
				{
					goodHTML = goodHTML + 'Error in retreiving 1';
					document.getElementById('maincontainer').innerHTML = goodHTML;
				}
			});
	
	
		gapi.client.playerAPI.getCurrentWeek(requestData).execute(function(resp)  
		{
			
			if (!resp.code) 
			{
				currentWeek = resp.number;
				getGames(currentWeek);
				getStandings();
			}
			else
			{
				goodHTML = goodHTML + 'Error in retreiving 1';
				document.getElementById('maincontainer').innerHTML = goodHTML;
			}
		});
	
}

function doSomething()
{
	//alert("DO SOMETHING!");
	
}

//GENERIC PAINTROW FUNCTION THAT RETURNS FORMATTED HTML TO REPAINT THE CONTENTS OF A DIV WITH A GAMEID
function paintRow(item)
{
	//alert(item.id);
	
	var goodHTML = "";
	var home_bet = " -.--";
	var away_bet = " -.--";
	var betLocked = false;
	var betUnlocked = true;
	
	var lockDateTime = new Date(item.freeze)
	
	if (lockDateTime < currentDateTime)
	{
		betLocked = true;
		betUnlocked = false;
		//alert("Lock on:" + lockDateTime + "Current Time:" + currentDateTime + "Bet Locked?:" + betLocked);
	}
	
	
	if(item.user_net_home_bet>0)
	{
		home_bet = item.user_net_home_bet;
		home_bet = home_bet.toFixed(2);
		away_bet = " -.--";
	}
	
	if(item.user_net_home_bet<0)
	{
		away_bet = item.user_net_home_bet*(-1);
		away_bet = away_bet.toFixed(2);
		home_bet = " -.--";
	}
	
	//UNALTERED DEFAULT BUTTONS
	
	var displayTypeHome = "<div class=\"col-lg-3 col-sm-4 col-xs-5\"><a id=\"" + item.home_team + "\" href=\"#\" onclick=\"setBet(\'" + item.home_team + "\'," + item.id + ")\" class=\"btn btn-primary btn-lg col-sm-12\" style=\"text-align:right;\">" + item.home_team + " | " + item.home_score + "</a></div>";
	var displayTypeAway = "<div class=\"col-lg-3 col-sm-4 col-xs-5\"><a id=\"" + item.away_team + "\" href=\"#\" onclick=\"setBet(\'" + item.away_team + "\'," + item.id + ")\" class=\"btn btn-primary btn-lg col-sm-12\" style=\"text-align:left;\">" + item.away_score + " | " + item.away_team + "</a></div>";
	
	
	if(betLocked)
	{
		//DISPLAYED LABELS FOR LOCKED GAMES
		displayTypeHome = "<div class=\"col-lg-3 col-sm-4 col-xs-5 btn btn-lg btn-default disabled\" style=\"text-align:right;\">" + item.home_team + " | " + item.home_score + "</div>";
		displayTypeAway = "<div class=\"col-lg-3 col-sm-4 col-xs-5 btn btn-lg btn-default disabled\" style=\"text-align:left;\">" + item.away_score + " | " + item.away_team + "</div>";
	}
	
	var home_bets = item.home_bets || [];
	var home_bets_string = "";
	
	if (home_bets.length > 0)
	{
		//alert(home_bets.length);
		
		for (z=0;z<home_bets.length;z++)
		{
			if (z>0)
			{
				home_bets_string = home_bets_string + ", "
			}
			home_bets_string = home_bets_string + home_bets[z].fname + " " + home_bets[z].linitial + ".";
			
			//alert (home_bets[z].email + tempemail + betUnlocked);
			
			if (home_bets[z].email == tempemail && betUnlocked)  // !CAUTION!
			{
				//alert("I bet on that!" + home_bets.email)
				displayTypeHome = "<div class=\"col-lg-3 col-sm-4 col-xs-5\"><a class=\"col-lg-12 col-sm-12 col-xs-12 btn btn-lg btn-success \" id=\"" + item.home_team + "\" href=\"#\" onclick=\"deleteBet(\'" + item.home_team + "\'," + item.id + ")\" style=\"text-align:right;\">" + item.home_team + " | " + item.home_score + "</a></div>";
				
				//make other side Unclickable
				//displayTypeAway = "<div class=\"col-sm-2\"><a id=\"" + item.away_team + "\" href=\"#\" class=\"btn btn-default col-sm-12\" style=\"text-align:left;\">" + item.home_line*(-1) + " | " + item.away_team + "</a></div>";
				
				//set the bet to $20
				//home_bet = 20;
				
				
			}
		}
	}
	
	var away_bets = item.away_bets || [];
	var away_bets_string = "";
	
	if (away_bets.length > 0)
	{
		//alert(away_bets.length);
		
		for (z=0;z<away_bets.length;z++)
		{

			if (z>0)
			{
				away_bets_string = away_bets_string + ", "
			}
			
			away_bets_string = away_bets_string + away_bets[z].fname + " " + away_bets[z].linitial + ".";
			
			if (away_bets[z].email == tempemail && betUnlocked)  // !CAUTION!
			{
				//alert("I bet on that!" + home_bets.email)
				displayTypeAway = "<div class=\"col-lg-3 col-sm-4 col-xs-5\"><a class=\"col-lg-12 col-sm-12 col-xs-12 btn btn-lg btn-success \" id=\"" + item.away_team + "\" href=\"#\" onclick=\"deleteBet(\'" + item.away_team + "\'," + item.id + ")\" style=\"text-align:left;\">" + item.away_score + " | " + item.away_team + "</a></div>";
				
				//make other side Unclickable
				//displayTypeHome = "<div class=\"col-sm-2\"><a id=\"" + item.home_team + "\" href=\"#\" class=\"btn btn-default col-sm-12\" style=\"text-align:right;\">" + item.home_team + " | " + item.home_line + "</a></div>";
				
				//set the bet to $20
				//away_bet = 20;
			}
			
		}
		
	}
	
	
						
	goodHTML = goodHTML + "<div class=\"row text-right\">";
	goodHTML = goodHTML + "<div class=\"col-lg-2 col-sm-1 col-xs-0\"></div>";
	goodHTML = goodHTML + displayTypeHome;
	goodHTML = goodHTML + "<div class=\"col-lg-1 col-sm-1 col-xs-1 text-left\"><h4>$" + home_bet + " </h4></div>";
	goodHTML = goodHTML + "<div class=\"col-lg-1 col-sm-1 col-xs-1 text-right\"><h4>$" + away_bet + " </h4></div>";
	goodHTML = goodHTML + displayTypeAway;
	goodHTML = goodHTML + "<div class=\"col-lg-2 col-sm-1 col-xs-0\"></div>";
	goodHTML = goodHTML + "</div>";
	goodHTML = goodHTML + "<div class=\"row\">";
	goodHTML = goodHTML + "<div class=\"col-lg-2 col-sm-1 col-xs-0\"></div>";
	goodHTML = goodHTML + "<div class=\"col-lg-3 col-sm-4 col-xs-5 text-left\"><h5>" + home_bets_string + "</h5></div>";
	goodHTML = goodHTML + "<div class=\"col-lg-2 col-sm-2 col-xs-2 text-center\"><h4>Line: " + item.home_line + "</h4></div>";
	goodHTML = goodHTML + "<div class=\"col-lg-3 col-sm-4 col-xs-5 text-right\"><h5>" + away_bets_string + "</h5></div>";
	goodHTML = goodHTML + "<div class=\"col-lg-2 col-sm-1 col-xs-0\"></div>";
	goodHTML = goodHTML + "</div>";

	
	//goodHTML = goodHTML + "		<div class=\"col-sm-3\">" + home_bets_string + "</div>";
	//goodHTML = goodHTML + "		<div class=\"col-sm-1\">$" + home_bet + "</div>"
	//goodHTML = goodHTML + 		displayTypeHome;
	//goodHTML = goodHTML + 		displayTypeAway;
	//goodHTML = goodHTML + "		<div class=\"col-sm-1\">$" + away_bet + "</div>"
	//goodHTML = goodHTML + "		<div class=\"col-sm-3\">" + away_bets_string + "</div>"
	
	return goodHTML;
	
}

//INITIAL SCREEN PAINT FUNCTION THAT PAINTS A DIV FOR EACH GAME
function getGames(currentWeek)
{
	var testText = "Test worked";
	
	
	var requestData = {};
		requestData.league_season_id = templeague_season_id;
		requestData.week = currentWeek;
		
		//document.getElementById('maincontainer').innerHTML = "Made it at least this far " + currentWeek;
		document.getElementById('maincontainer').innerHTML = "<div class=\"row\"><h2>Leave The House Out Of It</h2></div>";

		gapi.client.playerAPI.getGames(requestData).execute(function(resp)  
		{		
			var goodHTML = "<div class=\"row\"><h2>Leave The House Out Of It</h2></div>";
			//document.getElementById('maincontainer').innerHTML = "test 2";
			
			if (!resp.code) 
           	{
			resp.items = resp.items || [];
			
				for (c=0;c<resp.items.length;c++)
    			{
					//goodHTML = goodHTML + "	<div id=\"" + resp.items[c].id + "\" class=\"row\" style=\"margin-bottom:3px\">"
					goodHTML = goodHTML + " <div id=\"" + resp.items[c].id + "\" class=\"row\" style=\"margin-bottom:3px\">";

					goodHTML = goodHTML + paintRow(resp.items[c]);
					goodHTML = goodHTML + "	</div>";
					goodHTML = goodHTML + "	<hr style=\"margin:3px;\" />";
				}
				
    			document.getElementById('maincontainer').innerHTML = goodHTML;
           	}
    		else
   			{
    			goodHTML = goodHTML + 'Error in retreiving 1';
    			document.getElementById('maincontainer').innerHTML = goodHTML;
   			}
   	});
}

//CALL TO SET BET, THEN PASSES THE ID ON TO GETGAME TO RETRIEVE SINGLE DAY
function setBet(team, id)
{
	//document.getElementById('test').innerHTML = team;
	
	var requestData = {};
	requestData.league_season_id = templeague_season_id;  //
	requestData.team_name = team;
	requestData.id = 0;  // this is what's wrong with it
	
	gapi.client.playerAPI.setBet(requestData).execute(function(resp)  
	{
		
		if (!resp.code) 
		{
			getGame(id);
		}
		else
		{
			var goodHTML = goodHTML + 'Error in retreiving 1';
			document.getElementById('maincontainer').innerHTML = goodHTML;
		}
	});
	
	
}

//CALL TO DELETE BET BASED ON PARAMETERS.  THIS IS UNTESTED.
function deleteBet(team, id)  // !CAUTION!
{
	var requestData = {};
	requestData.id = 0;
	requestData.team_name = team;
	requestData.league_season_id = templeague_season_id;  //
	
	gapi.client.playerAPI.deleteBet(requestData).execute(function(resp)  
	{
		
		if (!resp.code) 
		{
			getGame(id);
		}
		else
		{
			goodHTML = goodHTML + 'Error in retreiving 1';
			document.getElementById('maincontainer').innerHTML = goodHTML;
		}
	});
}

//GETS SINGLE GAME TO REPAINT THE CONTENTS OF THE SINGLE ROW DIV
function getGame(id)
{
	//alert("inside GetGame");
	
	var requestData = {};
	requestData.league_season_id = templeague_season_id;  //
	requestData.game_id = id;
	
	//alert("initially setting variable with something bogus");
	//var getGameReturn = "something bogus";
	
	gapi.client.playerAPI.getGame(requestData).execute(function(resp)  
	{
		
		if (!resp.code) 
		{
			//doSomething(id);
			resp.items = resp.items || [];
			
			//alert("about to call paintRow from inside GetGame with item");
			
			getGameReturn = paintRow(resp);
			//alert ("just called paintRow");
			
			//alert("about to return the result of getGame");
			document.getElementById(id).innerHTML = getGameReturn;
			
			//document.getElementById(team).className = 'btn btn-success col-sm-12'
			//document.getElementById(team).onclick = "";
		}
		else
		{
			goodHTML = goodHTML + 'Error in retreiving 1';
			document.getElementById('maincontainer').innerHTML = goodHTML;
		}
	});
}	

function getStandings()
{

	//document.getElementById('standings').innerHTML = "STANDINGS PANE";
	var standingsHTML = "";
	
	var requestData = {};
	requestData.league_season = templeague_season_id;  //
	
	gapi.client.playerAPI.getUsers(requestData).execute(function(resp)  
	{
		
		if (!resp.code) 
		{
			
			//alert("Got the standings callback!");
			resp.items = resp.items || [];
			
			standingsHTML = standingsHTML + "<div class=\"row\"><h2>Standings</h2></div>"
			
    		standingsHTML = standingsHTML + "<div class=\"row\">";
			standingsHTML = standingsHTML + "<div class=\"col-sm-2\"></div>";
			standingsHTML = standingsHTML + "<div class=\"col-sm-3\">";
			standingsHTML = standingsHTML + "<h3>Name</h3>";
			standingsHTML = standingsHTML + "</div>"
			standingsHTML = standingsHTML + "<div class=\"col-sm-1 text-center\">";
			standingsHTML = standingsHTML + "<h3>Win</h3>";
			standingsHTML = standingsHTML + "</div>"
			standingsHTML = standingsHTML + "<div class=\"col-sm-1 text-center\">";
			standingsHTML = standingsHTML + "<h3>Lose</h3>";
			standingsHTML = standingsHTML + "</div>"
			standingsHTML = standingsHTML + "<div class=\"col-sm-1 text-center\">";
			standingsHTML = standingsHTML + "<h3>Push</h3>";
			standingsHTML = standingsHTML + "</div>"
			standingsHTML = standingsHTML + "<div class=\"col-sm-2 text-right\">";
			standingsHTML = standingsHTML + "<h3>Winnings</h3>";
			standingsHTML = standingsHTML + "</div>";
			standingsHTML = standingsHTML + "<div class=\"col-sm-2\"></div>";
			standingsHTML = standingsHTML + "</div>";

			
			for (c=0;c<resp.items.length;c++)
    		{
    			var currencyPrefix = "";
				
				standingsHTML = standingsHTML + "<hr style=\"margin:3px;\" />";
    			standingsHTML = standingsHTML + "<div class=\"row\">";
				standingsHTML = standingsHTML + "<div class=\"col-sm-2\"></div>";
				standingsHTML = standingsHTML + "<div class=\"col-sm-3\">";
				standingsHTML = standingsHTML + resp.items[c].fname + " " + resp.items[c].linitial + ".";
				standingsHTML = standingsHTML + "</div>"
				standingsHTML = standingsHTML + "<div class=\"col-sm-1 text-center\">";
				standingsHTML = standingsHTML + resp.items[c].wins;
				standingsHTML = standingsHTML + "</div>"
				standingsHTML = standingsHTML + "<div class=\"col-sm-1 text-center\">";
				standingsHTML = standingsHTML + resp.items[c].losses;
				standingsHTML = standingsHTML + "</div>"
				standingsHTML = standingsHTML + "<div class=\"col-sm-1 text-center\">";
				standingsHTML = standingsHTML + resp.items[c].pushes;
				standingsHTML = standingsHTML + "</div>"
				standingsHTML = standingsHTML + "<div class=\"col-sm-2 text-right\">";
				if (resp.items[c].winnings >= 0) {
					standingsHTML = standingsHTML + "$" + resp.items[c].winnings.toFixed(2);}
				else {
					standingsHTML = standingsHTML + "-$" + resp.items[c].winnings.toFixed(2)*(-1);}
				standingsHTML = standingsHTML + "</div>";
				standingsHTML = standingsHTML + "<div class=\"col-sm-2\"></div>";
				standingsHTML = standingsHTML + "</div>";

			}
			
			standingsHTML = standingsHTML + "<div style=\"margin-bottom:40px\";>&nbsp;</div>";
			
    		document.getElementById('standings').innerHTML = standingsHTML;
		}
		else
		{
			goodHTML = goodHTML + 'Error in retreiving 1';
			document.getElementById('standings').innerHTML = "ERRRORRR"
		}
	});
	
}
