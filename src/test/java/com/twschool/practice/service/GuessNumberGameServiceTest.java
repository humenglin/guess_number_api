package com.twschool.practice.service;

import com.twschool.practice.domain.Answer;
import com.twschool.practice.domain.GameUserInfo;
import com.twschool.practice.domain.GuessNumberGame;
import com.twschool.practice.domain.RandomAnswerGenerator;
import com.twschool.practice.exception.UserIsExistException;
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
        String gameId = "1";
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
    }

    @Test(expected = UserIsExistException.class)
    public void should_throw_exception_when_register_given_userid_is_exist() {
        String userNameFirst = "name";
        GameUserInfo gameUserInfo = guessNumberGameService.register(userNameFirst);

        String userNameSecond = "name";
        guessNumberGameService.register(userNameSecond);
    }
}
