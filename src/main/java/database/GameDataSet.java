package database;

import java.util.Arrays;

public class GameDataSet {
    int userId;
    public int currentQuestionId;

    public GameDataSet(int userId, int currentQuestionId) {
        this.userId = userId;
        this.currentQuestionId = currentQuestionId;
    }
}
