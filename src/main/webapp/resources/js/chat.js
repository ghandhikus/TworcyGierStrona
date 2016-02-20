/** @file chat.js */

/**
 * Cuts the string
 */
String.prototype.splice = String.prototype.splice
		|| function(index, count, add) {
			return this.slice(0, index) + (add || "")
					+ this.slice(index + count);
		}

/**
 * Controls the chat.
 */
var Chat = Chat || {
	_messagesCache : [],
	lastMessage: 0,
	_scrollBound: true,
	_connected: false,
	/**
	 * Sends the message.
	 * @param {String} message
	 */
	message : function(msg) {
		msg = msg.trim();
		if(msg.length>0)
			Chat._messagesCache.push(msg);
	},
	/**
	 * Initializes chat.
	 */
	init : function() {
		
		var oldMessages = Chat._getStorage();
		Chat._parseMessages(oldMessages);
		
		document.body.innerHTML += '<div id="chat"><div id="chat_text_wrapper"><div id="chat_text"></div></div><input type="text" id="chat_input" disabled="true"/><div id="chat_status" style="color:#F99;"></div></div><div id="chat_toggle">Pokaz czat</div>';
		
		$("#chat_input").bind("keypress", function (e){
			var code = e.keyCode || e.which;
			// Enter
			if(code == 13) {
				Chat.message($("#chat_input").val());
				$("#chat_input").val("");
			}
		});
		
		$("#chat_toggle").click(Chat.hide);

		$("#chat").fadeOut(0);
		
		$("#chat_text").scroll(Chat._onScroll);
		$(window).resize(Chat._onResize);
		Chat._onResize();
	},
	/**
	 * Net update. Sends messages and receives new ones.
	 */
	net : function() {
		var startTime = Date.now();
		
		var sendData = {
			'lastMessage' : Chat.lastMessage,
			'messages' : Chat._messagesCache,
		}
		Chat._messagesCache = [];
		
		// Calls ajax to retrieve data from the server
		$.ajax({
			url : ChatSettings.url,
			dataType : 'json',
			data : sendData,
			method : 'POST',
			timeout : 2000, //2 second timeout, 
			error : function(jqXHR, status, errorThrown) { //the status returned will be "timeout" 
				$("#chat_status").html("Connection problems...");
				$("#chat_status").animate({color:"#F99"});
				$("#chat_input").prop('disabled', true);
				Chat._connectionState(false);
				setTimeout(Chat.net, 250);
			},
			success : function(data) {
				if (data[0] == "disconnect") {
					//alert("You got disconnected!");
					$("#chat_status").html("You have been disconnected.");
					$("#chat_status").animate({color:"#F99"});
					$("#chat_input").prop('disabled', true);
					Chat._connectionState(false);
					setTimeout(Chat.net, 250);
				} else if (data[0] == "notLogged") {
					//alert("You got disconnected!");
					$("#chat_status").html("You are not logged in.");
					$("#chat_status").animate({color:"#F99"});
					$("#chat_input").prop('disabled', true);
					Chat._connectionState(false);
					setTimeout(Chat.net, 250);
				} else if (data[0] == "error") {
					//alert("You got disconnected!");
					$("#chat_status").html("Server error.");
					$("#chat_status").animate({color:"#F99"});
					$("#chat_input").prop('disabled', true);
					Chat._connectionState(false);
					setTimeout(Chat.net, 250);
				} else {
					var ping = Date.now() - startTime;
					$("#chat_status").html("Ping : " + ping);
					$("#chat_input").removeAttr('disabled');
					
					var colorString = "#";
					if		(ping>500) colorString += "f00";
					else if (ping>400) colorString += "f63";
					else if (ping>300) colorString += "f96";
					else if (ping>200) colorString += "fb9";
					else if (ping>100) colorString += "fff";
					else 			   colorString += "bfb";
					
					$("#chat_status").animate({color: colorString},100);
					//$("#chat_status").color(colorValue);
					
					Chat._connectionState(true);
					
					// Store old players
					var old = Chat.players;
					// Clear players
					Chat.players = [];
					
					if(data.length>0)
						console.log(data);
					
					var messageFound = false;
					// Iterate through data
					for (var i = 0; i < data.length; i++)
						// Skip invalid
						if (data[i] != null && data[i] != "null")
							// Entry is a message
							if(data[i]["msg"] != null)
							{
								messageFound = true;
								Chat._parseMessage(data[i]);
							}
					
					// Ask for more messages.
					if(messageFound)
						setTimeout(Chat.net, 50);
					else
						setTimeout(Chat.net, 250);
				}
			}
		});
	},
	/**
	 * Gets the array from local storage for a "chatMessages" tag. It is used only for chatMessages 
	 * @returns {Array}
	 * @private
	 */
	_getStorage:function(){
		if(typeof(Storage) !== "undefined") {
			var currentStorage = localStorage.getItem("chatMessages");
			if(currentStorage == null)
				return [];
			var ret = JSON.parse(currentStorage);
			if(Object.prototype.toString.call( ret ) !== '[object Array]') 
				return [];
			
			return ret;
		}
		else return [];
	},
	/**
	 * Sets the array of the chat messages to the local storage.
	 * @param {Array} data
	 * @private
	 */
	_setStorage:function(data)
	{
		if(typeof(Storage) !== "undefined" && Object.prototype.toString.call( data ) === '[object Array]') {
			localStorage.setItem("chatMessages", JSON.stringify(data));
		} else {
			console.error("Chat._setStorage failed : storageType("+typeof(Storage)+") / dataParam("+Object.prototype.toString.call( data )+"), Expected not 'undefined' and '[object Array]'");
		}
	},
	/**
	 * Parses the received message.
	 * @param {Array} message object
	 * @private
	 */
	_parseMessage: function(msg) {
		// Set the newest message.
		if(Chat.lastMessage < msg['id'])
			Chat.lastMessage = msg['id'];
		else
			return;
		
		
		// Add the message to chat
		$("#chat_text").html($("#chat_text").html()+"<br>"+msg['msg']);

		// Debug
		console.log("GOT CHAT MESSAGE : " + msg['id'] +"&&{"+msg['msg']+"}");

		if(Chat._scrollBound === true)
			$("#chat_text").scrollTop(Chat.getMaxScroll());
		
		// Add to storage
		var storage = Chat._getStorage();
		if(Object.prototype.toString.call( storage ) !== '[object Array]')
			storage = [];
		storage.push(msg);
		Chat._setStorage(storage);
	},
	/**
	 * Parses all the messages received from the host.
	 * @param {Array} array of message objects. 
	 * @private
	 */
	_parseMessages: function(data) {
		// Iterate
		for(var i = 0;i<data.length;i++)
			// Skip invalid
			if (data[i] != null && data[i] != "null")
				// Parse
				Chat._parseMessage(data[i]);
	},
	/**
	 * Is called when someone scrolled the chat.
	 * Locks the chat.
	 * @private
	 */
	_onScroll: function() {
		var maxScroll = Chat.getMaxScroll();
		var scrollValue = $("#chat_text").scrollTop();
		
		if(Math.abs(maxScroll-scrollValue)>10)
			Chat._scrollBound = false;
		else
			Chat._scrollBound = true;
		
		if(Chat._scrollBound === true)
			$("#chat_text").scrollTop(Chat.getMaxScroll());
	},
	/**
	 * Calculates the amount that chat can be scrolled with exclusion of the height of the chat.
	 * @returns {Number} innerHeight - height
	 */
	getMaxScroll: function() {
		if(typeof($("#chat_text")) === 'undefined') return 0;
		if(typeof($("#chat_text")[0]) === 'undefined') return 0;
		var innerHeight = $("#chat_text")[0].scrollHeight;
		var height = $("#chat_text").height();
		return innerHeight - height;
	},
	/**
	 * Is called on resizing of the chat.
	 * Handles the height and it's animation.
	 * @private
	 */
	_onResize: function() {
		if(Chat._connected === true) {
			var height = $(window).height()/2.66;
			if(height<200) height = 200;
			$("#chat").finish().animate({height:height, opacity:1.0},250);
		} else if(Chat._connected === false) {
			$("#chat").finish().animate({height:100, opacity:0.5},250);
		}
	},
	/**
	 * Is called upon connection.
	 * @private
	 */
	_onConnect: function () {
		Chat._onResize();
		$("#chat").fadeIn(1000);
	},
	/**
	 * Is called when the chat is disconnectd from the host.
	 * @private
	 */
	_onDisconnect: function () {
		Chat._onResize();
	},
	/**
	 * Is called when connection state changes.
	 * @param {Boolean} change - change to
	 * @returns {Boolean} debug return, returns the state of the connection.
	 * @private
	 */
	_connectionState: function(change) {
		if(change === true && Chat._connected === false) {
			Chat._connected = true;
			Chat._onConnect();
		}
		else if(change === false && Chat._connected === true) {
			Chat._connected = false;
			Chat._onDisconnect();
		}
		
		return Chat._connected;
	},
	/** Toggles the chat visilibity */
	hide: function() {$("#chat").fadeToggle();},
	/**
	 * Checks if chat is visible.
	 * @returns $("#chat").is(":visible")
	 */
	isHidden: function() {return $("#chat").is(":visible");},
}

// Add chat elements to screen.
$(Chat.init);
// Start connection if ready.
$(Chat.net);