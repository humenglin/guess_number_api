package com.twschool.practice.domain;

import lombok.Data;

import java.util.List;

@Data
public class GuessNumberGame {
    public static final int CHANGE_SCORES = 3;
    private Answer answer;
    private GameStatus status = GameStatus.CONTINUED;
    private int MAX_TRY_TIMES = 6;
    private int leftTryTimes = MAX_TRY_TIMES;
    private RandomAnswerGenerator randomAnswerGenerator;
    private int gameScores = 0;

    public GuessNumberGame() {
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
            gameScores += CHANGE_SCORES;
        }
        if (GameStatus.FAILED == status) {
            gameScores -= CHANGE_SCORES;
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
