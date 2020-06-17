package com.twschool.practice.service;

import com.twschool.practice.domain.Answer;
import com.twschool.practice.domain.GuessNumberGame;
import com.twschool.practice.domain.RandomAnswerGenerator;
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
}
