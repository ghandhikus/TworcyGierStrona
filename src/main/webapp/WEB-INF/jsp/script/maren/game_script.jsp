<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="/maren/ajax" var="ajax" />
<spring:url value="/" var="main_site" />

<script type="text/javascript">
/** @file game_script.js */
try
{
	// Allow splice for string
	String.prototype.splice = String.prototype.splice || function (index, count, add) { return this.slice(0, index) + (add || "") + this.slice(index + count); }
	
	/**
	 * Game main object
	 * @namespace Game
	 */
	var Game = {
		data : {
			// Position set by server
			x: parseInt("${player.getX()}"),
			y: parseInt("${player.getY()}"),
			left: false,
			right: false,
			up: false,
			down: false,
			lerp: false
		},
		players : [],
		ping: 0,
		pingSpikes:0,
		debugShow:0,
		debugText:"",
		aveUpdate:0,
		aveRender:0,
		net : function ()
		{
			var startTime = Date.now();
			
			//var simulateLagSpikes = 100*Math.random();
			var simulateLagSpikes = 0;
			setTimeout(function(){
			// Calls ajax to retrieve data from the server
			$.ajax({ 
				url: "${ajax}", 
				dataType: 'json', 
				data: Game.data,
				method: 'POST',
				timeout: 2000, //2 second timeout, 
				error: function(jqXHR, status, errorThrown){   //the status returned will be "timeout" 
					window.location.replace("${main_site}");
				},
				success: function(data){
					try
					{
						var curPing = Date.now()-startTime;
						Game.pingSpikes = (Game.pingSpikes*3+Math.abs(curPing-Game.ping))/4;
						Game.ping = curPing;
						if(data[0] == "disconnect")
						{
							//alert("You got disconnected!");
							window.location.replace("${main_site}");
						}
						else
						{							
							// Store old players
							var old = Game.players;
							// Clear players
							Game.players = [];
							
							// Iterate through data
							for(var i=0;i<data.length;i++)
								if(data[i]!="null") // skips this player
								{
									console.log("GOT PLAYER : "+data[i]['id']);
									// Recreates array
									Game.players.push({
										id: data[i]['id'],
										x: data[i]['x'],
										y: data[i]['y'],
										left: false,
										right: false,
										up: false,
										down: false,
										lerp: false,
									});
								}

							var timeout = 50 - Game.ping;
							if(timeout<0)
								timeout=0;
							
							for(var i=0;i<old.length;i++)
								for(var a=0;a<Game.players.length;a++)
									// Determine if player was in array
									if(old[i].id == Game.players[a].id)
									{
										Game.players[a].lerp = true;
										Game.players[a].toX = Game.players[a].x;
										Game.players[a].toY = Game.players[a].y;
										Game.players[a].x = old[i].x;
										Game.players[a].y = old[i].y;
										Game.players[a].fromX = old[i].x;
										Game.players[a].fromY = old[i].y;
										Game.players[a].lerpEnd = Date.now()+Game.ping+timeout;
										Game.players[a].lerpStart = Date.now();
									}
							
							// Timeout for net
							if(timeout<0)
								Game.net();
							else
								setTimeout(Game.net, timeout);
						}
					}
					catch(err)
					{
						alert("There was an error in the net code:"+err);
						throw err;
					}
				}
			});
			}, simulateLagSpikes);
		},
		lastUpdate: Date.now(),
		/**
		 * Updates game logic.
		 */
		update : function ()
		{
			var startTime = Date.now();
			
			// Debug text
			Game.debugShow++;
			if(Game.debugShow==10)
			{
				Game.debugShow = 0;
				Game.debugText =
					"FPS\t\t\t: " + Game.fps + "\n" +
					"Ping\t\t\t\t: " + Game.ping + "ms\n" +
					"Ping Spikes\t: " + Game.pingSpikes.toFixed(3) + "ms ~4\n" +
					"Average Update\t: "+Game.aveUpdate.toFixed(3) + "ms ~10\n" +
					"Average Render\t: "+Game.aveRender.toFixed(3) + "ms ~10\n" +
					"Average Whole\t: "+(Game.aveRender+Game.aveUpdate).toFixed(3) + "ms ~10";
			}
			var delta = (Date.now()-Game.lastUpdate)/1000.0;
			Game.lastUpdate = Date.now();
			// Update players
			for(var i=0;i<Game.players.length;i++)
				Game.updateSingle(Game.players[i], delta);
			// Update player
			Game.updateSingle(Game.data, delta);

			// Time of update
			var time = Date.now()-startTime;
			Game.aveUpdate = (Game.aveUpdate*9+time)/10;
			
			// Render
			requestAnimationFrame(Game.render);
			setTimeout(Game.update, 15);
		},
		
		lerp : function(from, to, time)
		{
			return from + time * (to - from);
		},
		
		updateSingle : function(data, delta)
		{
			if(data.lerp == true)
			{
				var difference = data.lerpEnd - data.lerpStart;
				var time = (Date.now()-data.lerpStart)/difference;
				//console.log("difference : "+difference+" time : "+time + " time elapsed : "+(Date.now()-data.lerpStart));
				//if(time>1) time = 1;
				data.x = Game.lerp(data.fromX, data.toX, time);
				data.y = Game.lerp(data.fromY, data.toY, time);
			}
			/*
			Game.players[a].x = old[i].x;
			Game.players[a].y = old[i].y;
			Game.players[a].fromX = old[i].x;
			Game.players[a].fromY = old[i].y;
			Game.players[a].toX = Game.players[a].x;
			Game.players[a].toY = Game.players[a].y;
			Game.players[a].lerpEnd = Date.now()+ping;
			Game.players[a].lerpStart = Date.now();
			*/
			// Set speeds to keys
			var speedX = 0;
			var speedY = 0;

			if(data.up) speedY--;
			if(data.down) speedY++;
			
			if(data.left) speedX--;
			if(data.right) speedX++;
			
			// Calculating length
			var length = Math.sqrt(speedX * speedX + speedY * speedY);
			
			// Normalize speed
			if(length != 0) {
				speedX = speedX/length;
				speedY = speedY/length;
				
				// Add speed
				data.x += speedX*delta*${speed};
				data.y += speedY*delta*${speed};

				var canvas = document.getElementById('game');
				var width = canvas.width;
				var height = canvas.height-75;
				
				if(data.x<0) data.x=0;
				if(data.y<0) data.y=0;

				if(data.x>=width) data.x=width;
				if(data.y>=height) data.y=height;
		    }
		},
		
		fps: 0,
		fpsCounter:0,
		fpsTimer: 0,
		fpsLastCheck: Date.now(),
		render : function ()
		{
			var startTime = Date.now();
			var canvas = document.getElementById('game');
			var ctx = canvas.getContext("2d");
			var width = canvas.width;
			var height = canvas.height;

			// Clear rect
			ctx.rect(0, 0, width, height);
			ctx.fillStyle="black";
			ctx.fill();

			// Draw debug text
			{
				// Set font
				ctx.fillStyle="yellow";
				ctx.font = '11px "Lucida Console", Monaco, monospace';
				// Split text by enters
				var splitText = Game.debugText.split('\n');
				// Replace tabulators
				for(var i = 0;i < splitText.length; i++)
				{
					var str = splitText[i];
					for(var r = 0;r < str.length; r++)
						if(str[r] == '\t' || str.codePointAt(r) == 9)
						{
							var pasteCount = r%4;
							for(var p = 0;p<pasteCount;p++)
								str = str.splice(r, 0, ' ');
						}
					splitText[i]=str;
				}
				// Draw text
				for(var i = 0;i < splitText.length; i++)
					ctx.fillText(splitText[splitText.length-1-i], 10, height-11*(i+1));
			}
			
			// Draw all players
			for(var i=0;i<Game.players.length;i++)
				Game.renderSingle(ctx, Game.players[i]);
			// Draw controlled player
			Game.renderSingle(ctx, Game.data);

			// Time of render
			time = Date.now()-startTime;
			Game.aveRender = (Game.aveRender*9+time)/10;
			
			// Set fps
			Game.fpsTimer += Date.now()-Game.fpsLastCheck;
			Game.fpsLastCheck = Date.now(); 
			Game.fpsCounter++;
			if(Game.fpsTimer>=1000)
			{
				Game.fpsTimer -= 1000;
				Game.fps = Game.fpsCounter;
				Game.fpsCounter = 0;
			}
		},

		renderSingle : function (ctx, data)
		{
			ctx.fillStyle="green";
			ctx.beginPath();
			ctx.arc(data.x, data.y, 16, 0, 2*Math.PI);
			ctx.fill();
		},
		
		keydown: function( event ) {
		    switch(event.which) {
		        case 37: // left
			        Game.data.left = true;
			    	event.preventDefault();
		        break;
	
		        case 38: // up
			        Game.data.up = true;
			    	event.preventDefault();
		        break;
	
		        case 39: // right
			        Game.data.right = true;
			    	event.preventDefault();
		        break;
	
		        case 40: // down
			        Game.data.down = true;
			    	event.preventDefault();
		        break;
	
		        default: return; // exit this handler for other keys
		    }
		},
		
		keyup: function( event ) {
		    switch(event.which) {
		        case 37: // left
			        Game.data.left = false;
			    	event.preventDefault();
		        break;
	
		        case 38: // up
			        Game.data.up = false;
			    	event.preventDefault();
		        break;
	
		        case 39: // right
			        Game.data.right = false;
			    	event.preventDefault();
		        break;
	
		        case 40: // down
			        Game.data.down = false;
			    	event.preventDefault();
		        break;
	
		        default: return; // exit this handler for other keys
		    }
		}
	};
	
	$(function(){
		$(document).keydown(Game.keydown);
		$(document).keyup(Game.keyup);
		
		Game.net();
		Game.update();
		$("#response").text("Requesting data from "+"${pageContext.request.contextPath}/ajax");
	});
}
catch(err)
{
	// After document is ready
	$(function(){
		// Show error message
		$("#response").text("There was error in the script : "+err);
	});
	// Rethrow it further
	throw err;
}
</script>