import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * MyBot's Main class where MyBot is called and initiated
 */
public class MyMain {

	public static void main(String[] args) {
		ApiContextInitializer.init();
        TelegramBotsApi telegramBot = new TelegramBotsApi();
        try {
            telegramBot.registerBot(new MyBot());
        } catch (TelegramApiException e) {
        	System.out.println("Nouh");
        	e.printStackTrace();
        }
	}

}
