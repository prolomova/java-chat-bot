import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import database.DatabaseWorker;
import database.GameDataSet;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;


public class QuestGame implements IGame {
    private Map<Integer, HashMap<String, Float>> questions = new HashMap<>();
    private ArrayList<String> answers = new ArrayList<>();

    private HashMap<String, String> characters;

    private DatabaseWorker db = new DatabaseWorker();

    QuestGame(String fileName) throws FileNotFoundException {
        try {
            YamlReader reader = new YamlReader(new FileReader(fileName));
            QuizFile quizFile = reader.read(QuizFile.class);

            characters = quizFile.characters;

            for (var item : quizFile.questions) {
                Integer id = Integer.parseInt(item.get("id"));
                questions.put(id, new HashMap<>());
                questions.get(id).put("lat", Float.parseFloat(item.get("lat")));
                questions.get(id).put("lon", Float.parseFloat(item.get("lon")));
            }

            for (var item : quizFile.answers) {
                answers.add(item.get("text"));
            }

            db.connect();
            db.initDatabase(quizFile.questionsCount);

        } catch (YamlException e)
        {
            e.printStackTrace();
        }
    }

    private GameDataSet getGameData(int userId) {
        return db.getGameData(userId);
    }


    @Override
    public ChatBotReply proceedRequest(String request, int userId) {
        GameDataSet userData = getGameData(userId);
        int currentQuestionId = userData.currentQuestionId;

        if (!IsInArea(0, 0, 0, 0))
            return new ChatBotReply("Подумай ещё раз!", null);

//        if (quizGraph.get(currentQuestionId).size() == 0) {
//            markInactive(userId);
//            String characterName = questions.get(currentQuestionId);
//            return new ChatBotReply(String.format("Всё понятно. " + characters.get(characterName).get("description"),
//                    characterName),
//                    null,
//                    characters.get(characterName).get("image"),
//                    String.format("пикси %s", characterName));
//        }
        db.setGameData(userId, new GameDataSet(userId, currentQuestionId + 1));
        return new ChatBotReply("", null);
    }

    private boolean IsInArea(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) <= 500;
    }

    @Override
    public void markActive(int userId) {
        db.markGameActive(userId);
    }

    @Override
    public void markInactive(int userId) {
        db.markGameInactive(userId);
    }

    @Override
    public boolean isActive(int userId) {
        return db.isGameActive(userId);
    }

    @Override
    public String getInitialMessage(int userId) {
        return "Сейчас мы узнаем, как хорошо вы знакомы с конструктивизмом Екатеринбурга.";
    }
}