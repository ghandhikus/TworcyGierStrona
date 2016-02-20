package com.clockwise.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.clockwise.games.maren.Cheater;
import com.clockwise.games.maren.Game;
import com.clockwise.games.maren.Player;
import com.clockwise.model.account.Account;

@Controller
@RequestMapping(value = "/maren")
public class MarenGame {
	Game game = new Game();
	//private double spike = 0.001496983015127015d;
	private double spike = 0.0010d;
	
	@RequestMapping("/")
	public ModelAndView inGame(HttpServletRequest request, HttpServletResponse response)
	{
		// Check if is logged in
		if(request.getSession().getAttribute("account")==null)
			return new ModelAndView("redirect:/");
		
		ModelAndView ret = new ModelAndView("core");
		Account account = (Account) request.getSession().getAttribute("account");
		game.login(account);
		ret.addObject("account", account);
		ret.addObject("player", game.get(account));
		ret.addObject("speed", game.getSpeed());
		ret.addObject("width", game.getWidth());
		ret.addObject("height", game.getHeight());
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/ajax")
	public @ResponseBody String ajax(Float x, Float y, Boolean left, Boolean right, Boolean up, Boolean down,
			HttpServletRequest request)
	{		
		Account account = (Account) request.getSession().getAttribute("account");
		Player player = game.get(account);
		JSONArray json = new JSONArray();
		List<Player> list = game.getPlayerList();
		
		// Tell the player to disconnect if he is no longer logged in
		if(player == null)
		{
			json.add(0, "disconnect");
			//json.put("disconnect", true);
		}
		else 
		{
			// Is logged in
			if(x!=null && y!=null)
			{
				// Calculate length
				float oldX = player.getX();
				float oldY = player.getY();
				
				game.calcPing(player);

				float diffX = Math.abs(oldX - x);
				float diffY = Math.abs(oldY - y);
				
				double length = Math.sqrt(diffX*diffX + diffY*diffY);
				double timeDifference = game.getPingAverage(player);
				float pingJump = player.getPingJumps()/1000f+1.0f;
				//if(timeDifference<1) timeDifference = 1;
				// Length divided by time difference
				length /= pingJump;
				length /= timeDifference;
				length /= game.getSpeed();
				
				// Teleport strikes
				if(length>0.008d)
				{
					System.out.println("Teleport");
					game.cheatStrike(player, 10);
				}
				// Speed hack strikes
				else if(length>0.0025d)
					game.cheatStrike(player, 5);
				else if(length>0.0020d)
					game.cheatStrike(player, 3);
				else if(length>0.00175d)
					game.cheatStrike(player, 2);
				else if(length>0.0016d)
					game.cheatStrike(player, 1);
				
				if(spike<length)
					spike=length;
				
				if(length>0.0015d)
					System.out.println("spike("+timeDifference+", "+player.getPingJumps()+") : "+spike);
				
				// Debugs
				//else if(length>0.125d*game.getSpeed()) System.out.println("Possible cheater [length/time=("+length+")]\t: "+player.getAccountID());
				//else if(length>0.115d*game.getSpeed()) System.out.println("Kinda fast [length/time=("+length+")]\t: "+player.getAccountID());
				//else if(length>0.100d*game.getSpeed()) System.out.println("DEBUG [length/time=("+length+")]\t: "+player.getAccountID());

				// Debug
				if(game.getCheatStrikes(player)>1)
					System.out.println("Speed strikes("+game.getCheatStrikes(player)+") [length=("+Math.round(length*100000)/100000f+")]\t: "+account.getName());
				
				// Update
				try {
					game.update(player);
				} catch (Cheater e) {
					// Remove him if he cheats
					System.out.println("Removing Cheater [speed=("+length+") allowed("+0.15d*game.getSpeed()+")] : "+player.getAccountID());
					request.getSession().removeAttribute("account");
					game.logout(player);
					account = null;
					player = null;
					json.add(0, "disconnect");
				}
				
				if(player!=null)
				{
					player.setLastUpdate(new Date());
					player.setX(Math.round(x));
					player.setY(Math.round(y));
				}
			}
		}
		
		if(player!=null)
			for(int i=0;i<list.size();i++)
			{
				Player curPlayer = list.get(i);
				// Skip entries of the same player
				if(curPlayer.getAccountID() != player.getAccountID())
					json.add(i, list.get(i).toJSON());
				else
					json.add(i, "null");
			}
		
		// Return array
		return json.toJSONString();
	}
}
