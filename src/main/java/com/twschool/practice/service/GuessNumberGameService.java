package com.twschool.practice.service;

import com.twschool.practice.constants.GuessNumberGameConstants;
import com.twschool.practice.domain.*;
import com.twschool.practice.exception.TheGameIsOverException;
import com.twschool.practice.exception.UserIsExistException;
import com.twschool.practice.exception.UserIsNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GuessNumberGameService {
    private GuessNumberGameRepository guessNumberGameRepository;

    @Autowired
    public GuessNumberGameService(GuessNumberGameRepository guessNumberGameRepository) {
        this.guessNumberGameRepository = guessNumberGameRepository;
    }

    public String guess(String userId, int gameId, String userAnswer) {
        GuessNumberGame guessNumberGame = guessNumberGameRepository.getGuessNumberGame(gameId);
        if (guessNumberGame.getLeftTryTimes() <= 0) {
            throw new TheGameIsOverException();
        }
        List<String> userAnswerNumbers = Arrays.asList(userAnswer.split(" "));
        String result = guessNumberGame.guess(userAnswerNumbers);
        controllerUser(userId, guessNumberGame.getStatus());
        return result;
    }

    private void controllerUser(String userId, GameStatus status) {
        GameUserInfo gameUserInfo = guessNumberGameRepository.getGameUserInfo(userId);
        if (GameStatus.SUCCEED == status) {
            gameUserInfo.setScores(gameUserInfo.getScores() + GuessNumberGameConstants.CHANGE_SCORES_PER_GAME);
            gameUserInfo.setSuccessStayTimes(gameUserInfo.getSuccessStayTimes() + 1);
        }
        if (GameStatus.FAILED == status) {
            gameUserInfo.setScores(gameUserInfo.getScores() - GuessNumberGameConstants.CHANGE_SCORES_PER_GAME);
            gameUserInfo.setSuccessStayTimes(0);
        }

        if (gameUserInfo.getSuccessStayTimes()/3 != 0 && gameUserInfo.getSuccessStayTimes()%3 == 0) {
            gameUserInfo.setScores(gameUserInfo.getScores() + GuessNumberGameConstants.CHANGE_SCORES_THREE_GAMES);
        }

        if (gameUserInfo.getSuccessStayTimes()/5 != 0 && gameUserInfo.getSuccessStayTimes()%5 == 0) {
            gameUserInfo.setScores(gameUserInfo.getScores() + GuessNumberGameConstants.CHANGE_SCORES_FIVE_GAMES);
        }
    }

    public GameUserInfo register(String userName) {
        List<GameUserInfo> gameUserInfos = guessNumberGameRepository.getGameUserInfos();
        for (GameUserInfo gameUserInfoExist: gameUserInfos) {
            if (userName.equals(gameUserInfoExist.getUserId())) {
                throw new UserIsExistException();
            }
        }
        GameUserInfo gameUserInfo = new GameUserInfo(userName);
        guessNumberGameRepository.saveGameUserInfo(gameUserInfo);
        return gameUserInfo;
    }

    public GuessNumberGame start(String userId) {
        List<GameUserInfo> gameUserInfos = guessNumberGameRepository.getGameUserInfos();
        List<UserGameMapInfo> userGameMapInfos = guessNumberGameRepository.getUserGameMapInfos();
        List<GuessNumberGame> guessNumberGames = guessNumberGameRepository.getGuessNumberGames();
        if (gameUserInfos.size() == 0) {
            throw new UserIsNotExistException();
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

        GuessNumberGame guessNumberGame = new GuessNumberGame(new RandomAnswerGenerator());
        UserGameMapInfo userGameMapInfo = new UserGameMapInfo(userId, guessNumberGame.getGameId());
        guessNumberGameRepository.saveGuessNumberGame(guessNumberGame);
        guessNumberGameRepository.saveUserGameMapInfo(userGameMapInfo);
        return guessNumberGame;
    }
}
