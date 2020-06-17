package com.twschool.practice.service;

import com.twschool.practice.domain.GameStatus;
import com.twschool.practice.domain.GameUserInfo;
import com.twschool.practice.domain.GuessNumberGame;
import com.twschool.practice.exception.UserIsExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GuessNumberGameService {
    private static List<GameUserInfo> gameUserInfos = new ArrayList<>();
    private GuessNumberGame guessNumberGame;

    @Autowired
    public GuessNumberGameService(GuessNumberGame guessNumberGame) {
        this.guessNumberGame = guessNumberGame;
    }

    public String guess(String userId, String gameId, String userAnswer) {
        List<String> userAnswerNumbers = Arrays.asList(userAnswer.split(" "));
        return guessNumberGame.guess(userAnswerNumbers);
    }

    public GameUserInfo register(String userName) {
        for (GameUserInfo gameUserInfoExist: gameUserInfos) {
            if (userName.equals(gameUserInfoExist.getUserId())) {
                throw new UserIsExistException();
            }
        }
        GameUserInfo gameUserInfo = new GameUserInfo(userName);
        gameUserInfos.add(gameUserInfo);
        return gameUserInfo;
    }

    public GuessNumberGame start(String userId) {
        if (gameUserInfos.size() == 0) {
            throw new UserIsExistException();
        }

        for (GameUserInfo gameUserInfoExist: gameUserInfos) {
            if (userId.equals(gameUserInfoExist.getUserId())) {
                continue;
            }
            throw new UserIsExistException();
        }

        return guessNumberGame;
    }
}
