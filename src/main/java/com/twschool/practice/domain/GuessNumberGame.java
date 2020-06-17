package com.twschool.practice.domain;

import com.twschool.practice.constants.GuessNumberGameConstants;
import lombok.Data;

import java.util.List;

@Data
public class GuessNumberGame {
    private static int gameId = 0;
    private int myGameId = 0;
    private Answer answer;
    private GameStatus status = GameStatus.CONTINUED;
    private int leftTryTimes = GuessNumberGameConstants.MAX_TRY_TIMES;
    private RandomAnswerGenerator randomAnswerGenerator;
    private int gameScores = 0;

    public GuessNumberGame() {
    }

    public int getGameId() {
        return gameId;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public void setRandomAnswerGenerator(RandomAnswerGenerator randomAnswerGenerator) {
        this.randomAnswerGenerator = randomAnswerGenerator;
    }

    public GuessNumberGame(RandomAnswerGenerator randomAnswerGenerator) {
        this.randomAnswerGenerator = randomAnswerGenerator;
        this.answer = randomAnswerGenerator.generateAnswer();
        this.gameId ++;
        this.myGameId = gameId;
    }

    public String guess(List<String> userAnswerNumbers) {
        String result = answer.check(userAnswerNumbers);
        decreaseTryTimes();
        modifyStatus(result);
        controllerScore(status);
        return result;
    }

    private void controllerScore(GameStatus status) {
        if (GameStatus.SUCCEED == status) {
            gameScores += GuessNumberGameConstants.CHANGE_SCORES_PER_GAME;
        }
        if (GameStatus.FAILED == status) {
            gameScores -= GuessNumberGameConstants.CHANGE_SCORES_PER_GAME;
        }
    }

    private void modifyStatus(String result) {
        boolean noTryTimes = leftTryTimes == 0;
        if (noTryTimes) {
            status = GameStatus.FAILED;
        }
        boolean succeed = result.equals("4A0B");
        if (succeed) {
            status = GameStatus.SUCCEED;
        }
    }

    private void decreaseTryTimes() {
        leftTryTimes --;
    }

    public GameStatus getStatus() {
        return status;
    }
}
