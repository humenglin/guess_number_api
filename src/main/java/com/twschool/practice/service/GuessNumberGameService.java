package com.twschool.practice.service;

import com.twschool.practice.domain.GameUserInfo;
import com.twschool.practice.domain.GuessNumberGame;
import com.twschool.practice.domain.GuessNumberGameRepository;
import com.twschool.practice.domain.UserGameMapInfo;
import com.twschool.practice.exception.TheGameIsOverException;
import com.twschool.practice.exception.UserIsExistException;
import com.twschool.practice.exception.UserIsNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GuessNumberGameService {
    private GuessNumberGame guessNumberGame;
    private GuessNumberGameRepository guessNumberGameRepository;

    @Autowired
    public GuessNumberGameService(GuessNumberGame guessNumberGame, GuessNumberGameRepository guessNumberGameRepository) {
        this.guessNumberGame = guessNumberGame;
        this.guessNumberGameRepository = guessNumberGameRepository;
    }

    public String guess(String userId, int gameId, String userAnswer) {
        if (guessNumberGame.getLeftTryTimes() <= 0) {
            throw new TheGameIsOverException();
        }
        List<String> userAnswerNumbers = Arrays.asList(userAnswer.split(" "));
        return guessNumberGame.guess(userAnswerNumbers);
    }

    public GameUserInfo register(String userName) {
        List<GameUserInfo> gameUserInfos = guessNumberGameRepository.getGameUserInfos();
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
        List<GameUserInfo> gameUserInfos = guessNumberGameRepository.getGameUserInfos();
        List<UserGameMapInfo> userGameMapInfos = guessNumberGameRepository.getUserGameMapInfos();
        List<GuessNumberGame> guessNumberGames = guessNumberGameRepository.getGuessNumberGames();
        if (gameUserInfos.size() == 0) {
            throw new UserIsExistException();
        }

        boolean existed = false;
        for (GameUserInfo gameUserInfoExist: gameUserInfos) {
            if (userId.equals(gameUserInfoExist.getUserId())) {
                existed = true;
            }
        }
        if (!existed) {
            throw new UserIsNotExistException();
        }

        UserGameMapInfo userGameMapInfo = new UserGameMapInfo(userId, guessNumberGame.getGameId());
        userGameMapInfos.add(userGameMapInfo);
        guessNumberGames.add(guessNumberGame);
        return guessNumberGame;
    }
}
