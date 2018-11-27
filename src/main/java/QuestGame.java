import com.esotericsoftware.yamlbeans.YamlException;
        import com.esotericsoftware.yamlbeans.YamlReader;
        import database.DatabaseWorker;
        import database.GameDataSet;

        import java.io.FileNotFoundException;
        import java.io.FileReader;
        import java.util.*;


public class QuestGame implements IGame {
    private Map<Integer, String> questions = new HashMap<>();
    private ArrayList<String> answers = new ArrayList<>();

    private ArrayList<ArrayList<DestinationNode>> quizGraph;
    private Map<String, Integer> answersIndexes;
    private HashMap<String, HashMap<String, String>> characters;

    private DatabaseWorker db = new DatabaseWorker();

    QuestGame(String fileName) throws FileNotFoundException {
        try {
            YamlReader reader = new YamlReader(new FileReader(fileName));
            QuizFile quizFile = reader.read(QuizFile.class);

            characters = quizFile.characters;
            questions.put(0, "");
            answers.add(0, "");
            quizGraph = new ArrayList<>();
            answersIndexes = new HashMap<>();
            answersIndexes.put("", 0);
            quizGraph.add(new ArrayList<>());

            for (var item : quizFile.questions)
                questions.put(Integer.parseInt(item.get("id")) + 1, item.get("text"));

            var i = 0;
            for (var e : quizFile.answers) {
                answers.add(e.get("text"));
                answersIndexes.put(e.get("text"), i + 1);
                quizGraph.add(new ArrayList<>());
                int node = Integer.parseInt(e.get("from")) + 1;
                DestinationNode nextNode = new DestinationNode(Integer.parseInt(e.get("to")) + 1,
                        i + 1);
                quizGraph.get(node).add(nextNode);
                i++;
            }
            quizGraph.get(0).add(new DestinationNode(1, 0));

            db.connect();
            db.initDatabase(quizFile.questionsCount);

        } catch (YamlException e)
        {
            e.printStackTrace();
        }
    }

    private int getNextQuestionIndex(int edgeIndex, int currentQuestionId) {
        for (DestinationNode item : quizGraph.get(currentQuestionId)) {
            if (item.Edge == edgeIndex)
                return item.Node;
        }
        return  0;
    }

    private List<String> getAnswersList(int currentQuestionId){
        List<String> answersList = new ArrayList<>();
        for (DestinationNode item : quizGraph.get(currentQuestionId)){
            answersList.add(answers.get(item.Edge));
        }
        return answersList;
    }

    private GameDataSet getGameData(int userId) {
        return db.getGameData(userId);
    }


    @Override
    public ChatBotReply proceedRequest(String request, int userId) {
        GameDataSet userData = getGameData(userId);
        int currentQuestionId = userData.currentQuestionId;

        if (!IsInArea(0, 0, 0, 0))
            return new ChatBotReply("Подумай ещё раз!", getAnswersList(currentQuestionId));
        currentQuestionId = getNextQuestionIndex(answersIndexes.get(request), currentQuestionId);
        if (quizGraph.get(currentQuestionId).size() == 0) {
            markInactive(userId);
            String characterName = questions.get(currentQuestionId);
            return new ChatBotReply(String.format("Всё понятно. " + characters.get(characterName).get("description"),
                    characterName),
                    null,
                    characters.get(characterName).get("image"),
                    String.format("пикси %s", characterName));
        }
        db.setGameData(userId, new GameDataSet(userId, currentQuestionId));
        return new ChatBotReply(questions.get(currentQuestionId), getAnswersList(currentQuestionId));
    }

    private boolean IsInArea(double x1, double y1, double x2, double y2){
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