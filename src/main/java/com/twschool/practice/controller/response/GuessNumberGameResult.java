package com.twschool.practice.controller.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GuessNumberGameResult {
    String userId;
    String gameId;
    String userAnswer;
    String gameResult;

    public GuessNumberGameResult(String userId, String gameId, String userAnswer) {
        this.userId = userId;
        this.gameId = gameId;
        this.userAnswer = userAnswer;
    }
}
