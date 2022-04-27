import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.jibble.pircbot.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Mybot which extends pircbot. Can tell weather based on zipcode
// and give word definitions
public class MyBot extends PircBot
{
	// Set name to Wbot (weather and words)
	public MyBot()
	{
		this.setName("WBot");
	}
	
	// If there is a message in the chat
	public void onMessage(String channel, String sender,
			String login, String hostname, String message)
	{
		// If user sends a hello message say hello back and explain
		// functionality
		if(message.equals("Hi Wbot") || message.equals("hi Wbot")|| message.equals("Hello Wbot")||
		   message.equals("hello Wbot"))
		{
			sendMessage("#pircbot", "Hello! I am WBot!");
			sendMessage("#pircbot", "You can ask me the weather! Format: "
					+ "weather #zipcode (ex. weather 75080)");
			sendMessage("#pircbot", " You can also ask me the definition of a word!" 
					+ " Formart: define <word> (ex. define dog)");
		}
		// If the message contains the word "weather"
		else if(message.contains("weather")||message.contains("Weather"))
		{
			// Get zipcode as string
			String zip = getZipCode(message);
			// Do if zip is 5 digit number
			if(zip.length() == 5)
			{
				// Reply to user with message if api call is
				// successful
				try {
					String reply = getWeatherMessage(zip);
					sendMessage(channel, sender +": " + reply);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// If the user message contains the word "define"
		else if(message.contains("Define")||message.contains("define"))
		{
			// Get the word to be defined from message
			String word = getWord(message);
			// Reply to user if api call is successful
			try {
				// Get reply message
				String reply = getDefineMessage(word);
				sendMessage(channel, sender + ": " + reply);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(message.equals("Disconnect Wbot")||
				message.equals("disconnect Wbot"))
		{
			this.disconnect();
		}
	}
	
	// Method for getting zipcode from message
	public static String getZipCode(String sentence)
	{
		// Erase any character that is not a number
		String zipcode = sentence.replaceAll("[^0-9.]", "");
		return zipcode;
	}
	
	// Method for returning the final message to be returned to the
	// user
	public static String getWeatherMessage(String zipcode) throws Exception
	{
		// Call api with zipcode added into the url
		URL url = new URL("http://api.openweathermap.org/data/2.5/weather?zip="+
				zipcode +"&appid=e4579ecfdb2d94d325a846aea2d46751");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
		int status = con.getResponseCode();
		
		// Read in the JSON
		BufferedReader in = new BufferedReader(
		new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null)
		{
			content.append(inputLine);
		}
		in.close();
				
		// Transfer read in data which is in buffer reader to
		// a string
		String cont = content.toString();
		// Parse the JSON object
		JsonParser jparse = new JsonParser();
	    JsonElement jelement = jparse.parse(cont);
	    JsonObject jobject = jelement.getAsJsonObject();
	    jobject = jobject.getAsJsonObject("main");
	    // Get the temperature along with the maximum and
	    // minimum temperature
	    double temp = jobject.get("temp").getAsDouble();
	    double temp_max = jobject.get("temp_max").getAsDouble();
	    double temp_min = jobject.get("temp_min").getAsDouble();
	    
	    // Parse further and get the weather description
	    jelement = jparse.parse(cont);
	    jobject = jelement.getAsJsonObject();
	    JsonArray jarray = jobject.getAsJsonArray("weather");
	    jobject = jarray.get(0).getAsJsonObject();
	    String weather = jobject.get("description").getAsString();
	    
	    // Calculate the temperatures into farenheit as the
	    // api gives them in Kelvin
	    temp = (temp - 273.15) * (9.0/5.0) + 32;
	    temp_max = (temp_max - 273.15) * (9.0/5.0) + 32;
	    temp_min = (temp_min -273.15) * (9.0/5.0) + 32;
	    
	    // Format the output
	    String tempS = String.format("%.1f", temp);
	    String temp_maxS = String.format("%.1f", temp_max);
	    String temp_minS = String.format("%.1f", temp_min);
	    
	    // Make final message output
	    String finalReply = "The weather in " + zipcode + " is " + weather + ". " +
	    					"The temperature in " + zipcode +" is " + tempS +
	    					" farenheit with a high of " + temp_maxS +" farenheit"
	    					+ " and a low of " + temp_minS + " farenheit";
	    // Returh the final reply
	    return finalReply;
	}
	
	// Method for getting word out of message
	public static String getWord(String sentence)
	{	
		// Removes the word "define" then replaces empty
		// spaces which should just leave the word to be
		// defined if user followed proper format
		if(sentence.contains("Define"))
		{
			String word = sentence.replace("Define", "");
			word = word.replaceAll(" ", "");
			return word;
		}
		else if(sentence.contains("define"))
		{
			String word = sentence.replace("define", "");
			word = word.replaceAll(" ", "");
			return word;
		}
		return "";
	}
	
	// Method to get final message for definition, much the same
	// as for weather
	public static String getDefineMessage(String word) throws Exception
	{
		//  Call api with zipcode put into the url
		URL url = new URL("https://od-api.oxforddictionaries.com:443/api/v2/entries/en-gb/" + word
				          + "?fields=definitions&strictMatch=false");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
		// This api required app id and key to be set as headers
		con.setRequestProperty("app_id","a3f79dbe");
		con.setRequestProperty("app_key","dd5666a8814e5a9f31daa7d6c24a44c0");
		
		int status = con.getResponseCode();
		
		BufferedReader in = new BufferedReader(
		new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null)
		{
			content.append(inputLine);
		}
		in.close();
	    		
		// Parse the JSON like was done with the weather
		// api
		String cont = content.toString();
		JsonParser jparse = new JsonParser();
	    JsonElement jelement = jparse.parse(cont);
	    JsonObject jobject = jelement.getAsJsonObject();
	    JsonArray jarray = jobject.getAsJsonArray("results");
	    jobject = jarray.get(0).getAsJsonObject();
	    jarray = jobject.getAsJsonArray("lexicalEntries");
	    jobject = jarray.get(0).getAsJsonObject();
	    jarray = jobject. getAsJsonArray("entries");
	    jobject = jarray.get(0).getAsJsonObject();
	    jarray = jobject. getAsJsonArray("senses");
	    jobject = jarray.get(0).getAsJsonObject();
	    jarray = jobject. getAsJsonArray("definitions");
	    // Get definition as string
	    String definition = jarray.get(0).getAsString();
	    
	    // Return final reply
	    String reply = word + ": " + definition;
	    return reply;
	}
}

