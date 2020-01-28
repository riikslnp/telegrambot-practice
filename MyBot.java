
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Instant;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Multi-use Telegram bot 
 */
public class MyBot extends TelegramLongPollingBot {

	String latestMessage=""; //Stores last sent message for trainontime can get a response from user 
	
	/**
     * Responses to updates, custom response if update doesn't have text and for method callings
     * @param update from telegram, methods
     */
	@Override
	public void onUpdateReceived(Update update) {
		
		String command=update.getMessage().getText();
		SendMessage message = new SendMessage();
		message.setChatId(update.getMessage().getChatId());
		
		if(update.hasMessage()){
			
			if (update.getMessage().hasText()) {
				
				message.setText(command);
				
				if(command.contentEquals("/tellajoke")) {
					message.setText(tellJokes());
				}
				
				if(command.contentEquals("/trainshelsinki")) {
					message.setText(leavingTrains());
				}
				
				if(command.contentEquals("/trainontime")) {
					message.setText("Please send the train Id number");
				}
				
				if(latestMessage.contentEquals("Please send the train Id number")) {
					message.setText(trainOnTime(command));
				}
				latestMessage=message.getText();
	        }			
			else {
				message.setText("Anteeksi, en tiedä mitä tarkoitat:/");
				latestMessage=message.getText();
			}
			try {
            	execute(message);
        	} catch (TelegramApiException e) {
        		e.printStackTrace();
        	}
		}
	}
	
	/**
     * Returns a joke based on case structure and a randomized number between 0-10
     * @return String joke
     */
	public String tellJokes() {
		String joke="";
		int random=(int) (Math.random()*10);
		switch(random) {
			case 1:{
				joke="I love pressing F5. It's so refreshing.";
				break;
			}
			case 2:{
				joke="A password cracker walks into a bar. Orders a beer. Then a Beer. Then a BEER. beer. b33r. BeeR. Be3r. bEeR. bE3R. BeEr";
				break;
			}
			case 3:{
				joke="Why do Python Devs need glasses? Because they don't C#.";
				break;
			}
			case 4:{
				joke="The first computer dates back to Adam and Eve. It was an Apple with limited memory, just one byte. And then everything crashed";
				break;
			}
			case 5:{
				joke="Q: What do you call the security outside of a Samsung Store? A: Guardians of the Galaxy.";
				break;
			}
			case 6:{
				joke="I tried to escape the Apple store. I couldn't because there were no Windows.";
				break;
			}
			case 7:{
				joke="Any room is a panic room if you've lost your phone in it.";
				break;
			}
			case 8:{
				joke="Any room is a panic room if you've lost your phone in it.";
				break;
			}
			case 9:{
				joke="Moses had the first tablet that could connect to the cloud.";
				break;
			}
			default :{
				joke="I changed my password to \"incorrect\". So whenever I forget what it is the computer will say \"Your password is incorrect\".";
				break;
			}
			
		}
		return joke;
	}
	
	/**
     * Tells which trains will be leaving Helsinki station to the direction of Kerava if none method returns "Couldn't find any".
     * Gets information from Traffic Management Finlands interface 
     * @return train information or Couldn't find any-declaretion
     */
	public String leavingTrains() {
		String str="";
		String info="";
		Gson gson=new Gson();
		JsonParser parser=new JsonParser();
		try {
			str=readJsonFromUrl(urlMaker());
			if(str.startsWith("{\"queryString")) return "Couldn't find any.";
			JsonArray jA=(JsonArray) parser.parse(str);
			for(int i=0;i<jA.size();i++) {
				JsonElement id= jA.get(i).getAsJsonObject().get("commuterLineID");
				JsonElement track= jA.get(i).getAsJsonObject().get("timeTableRows").getAsJsonArray().get(0).getAsJsonObject().get("commercialTrack");
				JsonElement time= jA.get(i).getAsJsonObject().get("timeTableRows").getAsJsonArray().get(0).getAsJsonObject().get("scheduledTime");
				info=info+" id: "+gson.toJson(id)+"\n track: "+gson.toJson(track)+"\n departure_time: "+timeParsing(gson.toJson(time));
				if(i!=jA.size()-1) info=info+"\n\n";
			}
		} catch (IOException e) {
			System.out.println("Vituiks meni");
			e.printStackTrace();
		}
		return info;
	}
	
	/**
     * Reformats a string from deteTime to time and into the right timezone
     * @param String time wchich has to be formatted
     * @return String which has been reformatted
     */
	public static String timeParsing(String time) {
		String str="";
		int i= 2;
		time=time.substring(12, 17);
		int j=Integer.parseInt(time.substring(0, 2));
		i=i+j;
		str=""+i;
		time=time.replaceFirst(time.substring(0, 2), str);
		return time;
	}

	/**
     * Reads info from a url into a String and returns it
     * @param url where the json will be read from
     * @throws IOException if reading from the url fails
     * @return json info in a String
     */
	public static String readJsonFromUrl(String url) throws IOException {
	    InputStream is = new URL(url).openStream();
	    String jsonText = "";
	    try {
	      BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String str="";
	      while((str=reader.readLine())!=null) {
	    	  jsonText=jsonText+str;
	      }
	    } finally {
	      is.close();
	    }
	    return jsonText;
	}
	
	/**
     * Puts current time and date and current time+15min to the url so it can be used properly
     * @return the usable url with the time infoemation
     */
	public String urlMaker() {
		String startDate=Instant.now().toString().substring(0, 23)+"Z";
		String endDate=Instant.now().plusSeconds(900).toString().substring(0,23)+"Z";
		String url="https://rata.digitraffic.fi/api/v1/live-trains/station/HKI/KE?startDate="+
		startDate+"&endDate="+endDate+"&limit=4&include_nonstopping=false";
		return url;
	}
	
	/**
     * Tells how many minutes a train is late from its trainId, if something goes wrong method returns Couldn't find the data-decleration
     * @param the id oh the wanted train
     * @return String information
     */
	public String trainOnTime(String id) {
		String str="";
		String value="Couldn't find the data.";
		if(!check.NumberChecker.isNumbers(id) || (Integer.parseInt(id)>=166)) return value;
		Gson gson = new Gson();
		JsonParser parser=new JsonParser();
		try {
			str=readJsonFromUrl("https://rata.digitraffic.fi/api/v1/trains/latest/"+id);
			JsonArray obj=(JsonArray) parser.parse(str);
			JsonElement minutes= obj.get(0).getAsJsonObject().get("timeTableRows").getAsJsonArray().get(0).getAsJsonObject().get("differenceInMinutes");
			if(minutes==null) return "Your train is not late :)";
			else value="Your train "+id+" is "+gson.toJson(minutes)+" min late!";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	/**
     * Returns bots name in a String
     * @return bots name
     */
	@Override
	public String getBotUsername() {
		return "Yleis_Bot";
	}

	/**
     * Returns bots token in a String
     * @return bots token
     */
	@Override
	public String getBotToken() {
		return "991121511:AAHM-F8OI5O7jUhbpbgOcuH_SCsZubm-RDA";
	}
}
