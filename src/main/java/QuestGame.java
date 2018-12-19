import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import database.DatabaseWorker;
import database.GameDataSet;
import mapWorker.Image;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;


public class QuestGame implements IGame {
    private Map<Integer, HashMap<String, Float>> questions = new HashMap<>();
    private ArrayList<String> answers = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();

    private DatabaseWorker db = new DatabaseWorker();
    public Image image = new Image();

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
                images.add(Integer.parseInt(item.get("id")), item.get("image"));

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
        if (request == null) {
            image.getMap(lon, lat, Integer.toString(userId));
            return new ChatBotReply("Угадайте, где это!", null, image.Path);
        }
        var answerCoords = new GeographicalCoords(request.getLongitude(), request.getLatitude()).transformToRect();
        if (!IsInArea(rectCoords.getFirst(), rectCoords.getSecond(), answerCoords.getFirst(), answerCoords.getSecond()))
            return new ChatBotReply("Подумайте ещё раз!", null);

        db.setGameData(userId, new GameDataSet(userId, currentQuestionId + 1));
        new File(".\\tmp\\img" + Integer.toString(userId) + ".png").delete();
        if (currentQuestionId + 1 != questions.size()) {
            var next_lat = questions.get(currentQuestionId + 1).get("lat");
            var next_lon = questions.get(currentQuestionId + 1).get("lon");
            image.getMap(next_lon, next_lat, Integer.toString(userId));
            return new ChatBotReply(answers.get(currentQuestionId),
                    null,
                    ".\\images\\" + images.get(currentQuestionId), image.Path);
        }
        else {
            markInactive(userId);
            return new ChatBotReply(answers.get(currentQuestionId),
                    null,
                    ".\\images\\" + images.get(currentQuestionId));
        }
    }

    private boolean IsInArea(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) <= 600;
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