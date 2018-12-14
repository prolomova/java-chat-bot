import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import database.DatabaseWorker;
import database.GameDataSet;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;


public class QuestGame implements IGame {
    private Map<Integer, HashMap<String, Float>> questions = new HashMap<>();
    private ArrayList<String> answers = new ArrayList<>();

    private DatabaseWorker db = new DatabaseWorker();
    private Image image;

    QuestGame(String fileName) {
        try {
            YamlReader reader = new YamlReader(new FileReader(fileName));
            QuizFile quizFile = reader.read(QuizFile.class);

            for (var item : quizFile.questions) {
                Integer id = Integer.parseInt(item.get("id"));
                questions.put(id, new HashMap<>());
                questions.get(id).put("lat", Float.parseFloat(item.get("lat")));
                questions.get(id).put("lon", Float.parseFloat(item.get("lon")));
            }

            for (var item : quizFile.answers) {
                answers.add(Integer.parseInt(item.get("id")), item.get("text"));
            }

            db.connect();
            db.initDatabase();

        } catch (YamlException | FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private GameDataSet getGameData(int userId) {
        return db.getGameData(userId);
    }

    @Override
    public ChatBotReply proceedRequest(Location request, int userId) {
        GameDataSet userData = getGameData(userId);
        int currentQuestionId = userData.currentQuestionId;
        var lat = questions.get(currentQuestionId).get("lat");
        var lon = questions.get(currentQuestionId).get("lon");
        var rectCoords = new GeographicalCoords(lon, lat).transformToRect();
        if (request == null)
            return new ChatBotReply("", null, null);
        var answerCoords = new GeographicalCoords(request.getLongitude(), request.getLatitude()).transformToRect();
        System.out.println(rectCoords.getFirst());
        System.out.println(rectCoords.getSecond());
        System.out.println(answerCoords.getFirst());
        System.out.println(answerCoords.getSecond());
        System.out.println(d(rectCoords.getFirst(), rectCoords.getSecond(), answerCoords.getFirst(), answerCoords.getSecond()));
        if (!IsInArea(rectCoords.getFirst(), rectCoords.getSecond(), answerCoords.getFirst(), answerCoords.getSecond()))
            return new ChatBotReply("Подумай ещё раз!", null);

        db.setGameData(userId, new GameDataSet(userId, currentQuestionId + 1));
        return new ChatBotReply(answers.get(currentQuestionId),
                                        null,
                                        ""); //ссылка на картинку
    }

    private boolean IsInArea(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) <= 1000;
    }

    private double d(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
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