import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {
    private static ChatBot chatBot;

    private static String BOT_USERNAME;
    private static String BOT_TOKEN;

    private final ReplyKeyboardRemove noKeyboard = new ReplyKeyboardRemove();

    private final String vkShareUrl = "https://vk.com/share.php?url=%s&title=%s&image=%s";

    TelegramBot(DefaultBotOptions botOptions) {
        super(botOptions);
        var tests = new ArrayList<Pair<String, Class<? extends IGame>>>();
        tests.add(new Pair<>("quest.yml", QuestGame.class));
        chatBot = new ChatBot(new GameFactory(), tests);
        try {
            BOT_USERNAME = System.getenv("BOT_USERNAME");
            BOT_TOKEN = System.getenv("BOT_TOKEN");
        }
        catch (NumberFormatException e) {
            System.out.println("Please set bot credentials!");
            System.exit(0);
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            var text = update.getMessage().getText();
            if (text == null)
                text = "";
            var loc = update.getMessage().getLocation();
            ChatBotReply reply = chatBot.answer(text,
                    loc,
                    update.getMessage().getFrom().getId());

            var sendMessage = new SendMessage(
                    update.getMessage().getChatId(),
                    reply.message
            );
            sendMessage.enableHtml(true);
            if (reply.keyboardOptions != null)
                sendMessage.setReplyMarkup(makeKeyboard(reply.keyboardOptions));
            else
                sendMessage.setReplyMarkup(noKeyboard);

            if (reply.imageUrl != null)
            {
                var sendPhoto = new SendPhoto();
                sendPhoto.setChatId(update.getMessage().getChatId());
                sendPhoto.setPhoto(new File(reply.imageUrl));
                execute(sendPhoto);
            }
            execute(sendMessage);
            if (reply.nextImg != null)
            {
                var sendPhoto = new SendPhoto();
                sendPhoto.setChatId(update.getMessage().getChatId());
                sendPhoto.setPhoto(new File(reply.nextImg));

                execute(sendPhoto);
                //new File(reply.nextImg).delete();
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup makeKeyboard(List<String> options) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(false);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (String row : options) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(row);
            keyboardRows.add(keyboardRow);
        }

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
}
