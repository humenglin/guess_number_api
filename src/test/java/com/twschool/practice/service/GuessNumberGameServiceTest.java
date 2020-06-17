package com.twschool.practice.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.twschool.practice.domain.*;
import com.twschool.practice.exception.TheGameIsOverException;
import com.twschool.practice.exception.UserIsExistException;
import com.twschool.practice.exception.UserIsNotExistException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;

public class GuessNumberGameServiceTest {
    @Spy
    private GuessNumberGame guessNumberGame;
    @Mock
    private RandomAnswerGenerator randomAnswerGenerator;

    private GuessNumberGameService guessNumberGameService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        guessNumberGame.setRandomAnswerGenerator(randomAnswerGenerator);
        guessNumberGameService = new GuessNumberGameService(guessNumberGame);
    }

    @Test
    public void should_return_guess_result_when_guess() {
        String userId = "1";
        int gameId = 1;
        String userAnswer = "1 2 3 4";
        Answer answer = new Answer(Arrays.asList(userAnswer.split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);
        guessNumberGame.setAnswer(answer);
        Mockito.when(guessNumberGame.guess(Arrays.asList(userAnswer.split(" ")))).thenReturn("4A0B");

        String gameResult = guessNumberGameService.guess(userId, gameId, userAnswer);

        Assert.assertEquals("4A0B", gameResult);
    }

    @Test
    public void should_return_game_user_info_when_register() {
        String userName = "name";
        GameUserInfo gameUserInfo = guessNumberGameService.register(userName);

        Assert.assertEquals(userName, gameUserInfo.getUserId());
        Assert.assertEquals(0, gameUserInfo.getScores());
        assertThat(guessNumberGameService.getGameUserInfos()).contains(gameUserInfo);
    }

    @Test(expected = UserIsExistException.class)
    public void should_throw_exception_when_register_given_userid_is_exist() {
        String userNameFirst = "name";
        GameUserInfo gameUserInfo = guessNumberGameService.register(userNameFirst);

        String userNameSecond = "name";
        guessNumberGameService.register(userNameSecond);
    }

    @Test
    public void should_return_leftTryTimes_is_6_and_GameStatus_CONTINUED_and_answer_is_1234_when_start() {
        Answer answer = new Answer(Arrays.asList("1 2 3 4".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);
        guessNumberGame.setAnswer(answer);
        String userId = "1";
        guessNumberGameService.register(userId);

        GuessNumberGame guessNumberGameStart = guessNumberGameService.start(userId);

        assertThat(answer).isEqualToComparingFieldByField(guessNumberGameStart.getAnswer());
        assertThat(6).isEqualTo(guessNumberGameStart.getLeftTryTimes());
        assertThat(GameStatus.CONTINUED).isEqualTo(guessNumberGameStart.getStatus());
    }

    @Test(expected = TheGameIsOverException.class)
    public void should_throw_exception_when_times_guess_is_more_than_6() {
        String userId = "1";
        int gameId = 1;
        String userAnswer = "1 2 3 5";
        Answer answer = new Answer(Arrays.asList("1 2 3 4".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);
        guessNumberGame.setAnswer(answer);

        guessNumberGameService.guess(userId, gameId, userAnswer);
        guessNumberGameService.guess(userId, gameId, userAnswer);
        guessNumberGameService.guess(userId, gameId, userAnswer);
        guessNumberGameService.guess(userId, gameId, userAnswer);
        guessNumberGameService.guess(userId, gameId, userAnswer);
        guessNumberGameService.guess(userId, gameId, userAnswer);
        Mockito.verify(guessNumberGame, Mockito.times(6)).guess(Mockito.any());

        guessNumberGameService.guess(userId, gameId, userAnswer);
    }

    @Test(expected = UserIsNotExistException.class)
    public void should_throw_exception_when_start_given_user_is_not_existed() {
        Answer answer = new Answer(Arrays.asList("1 2 3 4".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);
        guessNumberGame.setAnswer(answer);
        String userId = "no_existed";

        guessNumberGameService.start(userId);
    }

    @Test
    public void should_save_user_game_map_info_when_start() {
        Answer answer = new Answer(Arrays.asList("1 2 3 4".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);
        guessNumberGame.setAnswer(answer);
        String userId = "2";
        guessNumberGameService.register(userId);

        GuessNumberGame guessNumberGameStart = guessNumberGameService.start(userId);

        UserGameMapInfo userGameMapInfo = new UserGameMapInfo(userId, guessNumberGameStart.getGameId());
        assertThat(guessNumberGameService.getUserGameMapInfos()).contains(userGameMapInfo);
    }

    @Test
    public void should_add_3_scores_when_guess_success() {
        Answer answer = new Answer(Arrays.asList("1 2 3 4".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);
        guessNumberGame.setAnswer(answer);
        String userId = "3";
        GameUserInfo gameUserInfoNew = guessNumberGameService.register(userId);

        GuessNumberGame guessNumberGameStart = guessNumberGameService.start(userId);

        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameStart.getGameId(), "1 2 3 4");

        int userScores = guessNumberGameService.getScores(gameUserInfoNew.getUserId(), guessNumberGameStart.getGameId());

    }
}
