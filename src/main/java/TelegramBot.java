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
            ChatBotReply reply = chatBot.answer(update.getMessage().getText(),
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

            if (reply.imageUrl != null && reply.characterName != null)
            {
                var sendPhoto = new SendPhoto();
                sendPhoto.setChatId(update.getMessage().getChatId());
                sendPhoto.setPhoto(reply.imageUrl);

                InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> inlineRows = new ArrayList<>();
                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(new InlineKeyboardButton()
                        .setText("Рассказать в VK")
                        .setUrl(String.format(vkShareUrl,
                                URLEncoder.encode("https://t.me/winxx_bot", StandardCharsets.UTF_8),
                                URLEncoder.encode(String.format("Я - %s из Winx. А ты?", reply.characterName),
                                        StandardCharsets.UTF_8),
                                URLEncoder.encode(reply.imageUrl, StandardCharsets.UTF_8))));
                inlineRows.add(row);
                inlineMarkup.setKeyboard(inlineRows);
                sendPhoto.setReplyMarkup(inlineMarkup);

                execute(sendPhoto);
            }
            execute(sendMessage);
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
