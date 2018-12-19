import org.telegram.telegrambots.meta.api.objects.Location;

import java.util.ArrayList;
import java.util.List;

class ChatBot {
    private IGame gameInstance;
    private IGameFactory gameFactory;
    private List<Pair<String, Class<? extends IGame>>> tests;
    private Integer curTest;

    ChatBot(IGameFactory gameFactory, List<Pair<String, Class<? extends IGame>>> tests) {
        this.gameFactory = gameFactory;
        this.tests = tests;
        curTest = 0;
        gameInstance = gameFactory.create(tests.get(curTest).getSecond(), tests.get(curTest).getFirst());
    }

    ChatBotReply answer(String message, Location location, int userId) {
        if (message.equals("Да")) curTest++;
        if ("/start".equals(message) || "Старт".equals(message)) curTest = 0;
        switch (message) {
            case "/start":
            case "Старт":
            case "Да":
                gameInstance = gameFactory.create(tests.get(curTest).getSecond(), tests.get(curTest).getFirst());
                gameInstance.markActive(userId);
                ChatBotReply firstQuestion = gameInstance.proceedRequest(null, userId);
                return new ChatBotReply(gameInstance.getInitialMessage(userId) +
                        '\n' + firstQuestion.message, firstQuestion.keyboardOptions, firstQuestion.imageUrl);
            case "/stop":
            case "Стоп":
            case "Нет":
                curTest = 0;
                if (!gameInstance.isActive(userId))
                    return new ChatBotReply("Игра ещё не началась.", null);
                gameInstance.markInactive(userId);
                return new ChatBotReply("Игра закончена.", null);
            default:
                if (gameInstance.isActive(userId)) {
                    ChatBotReply reply = gameInstance.proceedRequest(location, userId);
                    if (reply.imageUrl == null || tests.size() - 1 == curTest) return reply;
                    else {
                        var buttons = new ArrayList<String>();
                        buttons.add("Да");
                        buttons.add("Нет");
                        return new ChatBotReply(reply.message + "\nХочешь пройти следующий тест?", buttons, null, reply.imageUrl);
                    }
                }
                else
                    return new ChatBotReply("Игра ещё не началась.", null);
        }
    }
}
