package com.twschool.practice.domain;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuessNumberGameRepository {
    private static List<GameUserInfo> gameUserInfos = new ArrayList<>();
    private static List<UserGameMapInfo> userGameMapInfos = new ArrayList<>();
    private static List<GuessNumberGame> guessNumberGames = new ArrayList<>();


    public List<GameUserInfo> getGameUserInfos() {
        return gameUserInfos;
    }

    public List<UserGameMapInfo> getUserGameMapInfos() {
        return userGameMapInfos;
    }

    public List<GuessNumberGame> getGuessNumberGames() {
        return guessNumberGames;
    }

    public int getScores(String userId, int gameId) {
        for (UserGameMapInfo userGameMapInfo : userGameMapInfos) {
            if (userId.equals(userGameMapInfo.getUserId()) && gameId == userGameMapInfo.getGameId()) {
                return getGuessNumberGame(gameId).getGameScores();
            }
        }
        return 0;
    }

    public GuessNumberGame getGuessNumberGame(int gameId) {
        for (GuessNumberGame guessNumberGame : guessNumberGames) {
            if (gameId == guessNumberGame.getGameId()) {
                return guessNumberGame;
            }
        }
        return null;
    }

    public GameUserInfo getGameUserInfo(String userId) {
        for (GameUserInfo gameUserInfo : gameUserInfos) {
            if (userId.equals(gameUserInfo.getUserId())) {
                return gameUserInfo;
            }
        }
        return null;
    }
}
