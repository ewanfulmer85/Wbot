/*
 * Ewan Fulmer
 * CS 2336
 * 
 * This project utilizes a weather api and a dictionary api
 * It can tell the user the weather based on a zipcode
 * and the definition of a word. It uses the pircbot framework
 * to do this and GSON to parse the JSON recevied from apis
 */


import org.jibble.pircbot.*;

public class MyBotMain 
{
	public static void main(String[] args) throws Exception
	{
		// Create Mybot object
		MyBot bot = new MyBot();
		
		// Set verbose as true
		bot.setVerbose(true);
		
		// Connect to IRC freenode
		bot.connect("irc.freenode.net");
		
		// Join channel
		bot.joinChannel("#pircbot");
		
		// Welcome message
		bot.sendMessage("#pircbot", "Hello! I am Wbot!");
		bot.sendMessage("#pircbot", "You can ask me the weather! Format: "
				+ "weather #zipcode (ex. weather 75080)");
		bot.sendMessage("#pircbot", " You can also ask me the definition of a word!" 
				+ " Formart: define <word> (ex. define dog)");
		
	}

}
