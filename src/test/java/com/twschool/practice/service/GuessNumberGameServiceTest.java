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
    @Spy
    private GuessNumberGameRepository guessNumberGameRepository;
    @Mock
    private RandomAnswerGenerator randomAnswerGenerator;

    private GuessNumberGameService guessNumberGameService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        guessNumberGameService = new GuessNumberGameService(guessNumberGameRepository);
    }

    @Test
    public void should_return_guess_result_when_guess() {
        Answer answer = new Answer(Arrays.asList("1 2 3 4".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);
        String userId = "userId2";
        guessNumberGameService.register(userId);

        GuessNumberGame guessNumberGameStart = guessNumberGameService.start(userId);
        guessNumberGameStart.setAnswer(answer);
        String gameResult = guessNumberGameService.guess(userId, guessNumberGameStart.getMyGameId(), "1 2 3 4");

        Assert.assertEquals("4A0B", gameResult);
    }

    @Test
    public void should_return_game_user_info_when_register() {
        String userName = "name";
        GameUserInfo gameUserInfo = guessNumberGameService.register(userName);

        Assert.assertEquals(userName, gameUserInfo.getUserId());
        Assert.assertEquals(0, gameUserInfo.getScores());
        Assert.assertEquals(0, gameUserInfo.getSuccessStayTimes());
        assertThat(guessNumberGameRepository.getGameUserInfos()).contains(gameUserInfo);
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
        String userId = "1";
        guessNumberGameService.register(userId);

        GuessNumberGame guessNumberGameStart = guessNumberGameService.start(userId);
        guessNumberGameStart.setAnswer(answer);

        assertThat(answer).isEqualToComparingFieldByField(guessNumberGameStart.getAnswer());
        assertThat(6).isEqualTo(guessNumberGameStart.getLeftTryTimes());
        assertThat(GameStatus.CONTINUED).isEqualTo(guessNumberGameStart.getStatus());
    }

    @Test(expected = TheGameIsOverException.class)
    public void should_throw_exception_when_times_guess_is_more_than_6() {
        String userAnswer = "1 2 3 5";
        Answer answer = new Answer(Arrays.asList("1 2 3 4".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);
        String userId = "userId1";
        guessNumberGameService.register(userId);

        GuessNumberGame guessNumberGameStart = guessNumberGameService.start(userId);
        guessNumberGameStart.setAnswer(answer);

        guessNumberGameService.guess(userId, guessNumberGameStart.getMyGameId(), userAnswer);
        guessNumberGameService.guess(userId, guessNumberGameStart.getMyGameId(), userAnswer);
        guessNumberGameService.guess(userId, guessNumberGameStart.getMyGameId(), userAnswer);
        guessNumberGameService.guess(userId, guessNumberGameStart.getMyGameId(), userAnswer);
        guessNumberGameService.guess(userId, guessNumberGameStart.getMyGameId(), userAnswer);
        guessNumberGameService.guess(userId, guessNumberGameStart.getMyGameId(), userAnswer);

        guessNumberGameService.guess(userId, guessNumberGameStart.getMyGameId(), userAnswer);
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
        String userId = "2";
        guessNumberGameService.register(userId);

        GuessNumberGame guessNumberGameStart = guessNumberGameService.start(userId);
        guessNumberGameStart.setAnswer(answer);

        UserGameMapInfo userGameMapInfo = new UserGameMapInfo(userId, guessNumberGameStart.getMyGameId());
        assertThat(guessNumberGameRepository.getUserGameMapInfos()).contains(userGameMapInfo);
    }

    @Test
    public void should_add_3_scores_and_user_success_stay_times_add_1_when_guess_success() {
        Answer answer = new Answer(Arrays.asList("1 2 3 4".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);
        String userId = "3";
        GameUserInfo gameUserInfoNew = guessNumberGameService.register(userId);

        GuessNumberGame guessNumberGameStart = guessNumberGameService.start(userId);
        guessNumberGameStart.setAnswer(answer);

        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameStart.getMyGameId(), "1 2 3 4");

        int userScores = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGameStart.getMyGameId());
        gameUserInfoNew = guessNumberGameRepository.getGameUserInfo(gameUserInfoNew.getUserId());
        guessNumberGameStart = guessNumberGameRepository.getGuessNumberGame(guessNumberGameStart.getMyGameId());

        assertThat(userScores).isEqualTo(3);
        assertThat(gameUserInfoNew.getSuccessStayTimes()).isEqualTo(1);
        assertThat(gameUserInfoNew.getScores()).isEqualTo(3);
    }

    @Test
    public void should_minus_3_scores_and_user_success_stay_times_is_0_when_guess_failed() {
        Answer answer = new Answer(Arrays.asList("1 2 3 5".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);
        String userId = "4";
        GameUserInfo gameUserInfoNew = guessNumberGameService.register(userId);

        GuessNumberGame guessNumberGameStart = guessNumberGameService.start(userId);
        guessNumberGameStart.setAnswer(answer);

        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameStart.getMyGameId(), "1 2 3 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameStart.getMyGameId(), "1 2 3 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameStart.getMyGameId(), "1 2 3 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameStart.getMyGameId(), "1 2 3 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameStart.getMyGameId(), "1 2 3 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameStart.getMyGameId(), "1 2 3 4");

        int userScores = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGameStart.getGameId());
        gameUserInfoNew = guessNumberGameRepository.getGameUserInfo(gameUserInfoNew.getUserId());
        guessNumberGameStart = guessNumberGameRepository.getGuessNumberGame(guessNumberGameStart.getGameId());

        assertThat(userScores).isEqualTo(-3);
        assertThat(gameUserInfoNew.getSuccessStayTimes()).isEqualTo(0);
        assertThat(gameUserInfoNew.getScores()).isEqualTo(-3);
    }

    @Test
    public void when_user_win_once_and_fail_once() {
        String userId = "5";
        GameUserInfo gameUserInfoNew = guessNumberGameService.register(userId);

        Answer answer = new Answer(Arrays.asList("1 2 3 4".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);

        GuessNumberGame guessNumberGameOnce = guessNumberGameService.start(userId);
        guessNumberGameOnce.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameOnce.getMyGameId(), "1 2 3 4");

        GuessNumberGame guessNumberGameTwice = guessNumberGameService.start(userId);
        guessNumberGameTwice.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId(), "1 2 0 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId(), "1 2 0 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId(), "1 2 0 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId(), "1 2 0 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId(), "1 2 0 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId(), "1 2 0 4");

        int onceScores = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGameOnce.getMyGameId());
        int twiceScores = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId());
        gameUserInfoNew = guessNumberGameRepository.getGameUserInfo(gameUserInfoNew.getUserId());

        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGameOnce);
        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGameTwice);
        assertThat(onceScores).isEqualTo(3);
        assertThat(twiceScores).isEqualTo(-3);
        assertThat(gameUserInfoNew.getSuccessStayTimes()).isEqualTo(0);
        assertThat(gameUserInfoNew.getScores()).isEqualTo(0);
    }

    @Test
    public void when_user_win_3th() {
        String userId = "6";
        GameUserInfo gameUserInfoNew = guessNumberGameService.register(userId);

        Answer answer = new Answer(Arrays.asList("1 2 3 4".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);

        GuessNumberGame guessNumberGameOnce = guessNumberGameService.start(userId);
        guessNumberGameOnce.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameOnce.getMyGameId(), "1 2 3 4");

        GuessNumberGame guessNumberGameTwice = guessNumberGameService.start(userId);
        guessNumberGameTwice.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId(), "1 2 3 4");

        GuessNumberGame guessNumberGame3 = guessNumberGameService.start(userId);
        guessNumberGame3.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGame3.getMyGameId(), "1 2 3 4");

        int onceScores = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGameOnce.getMyGameId());
        int twiceScores = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId());
        int scores3 = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGame3.getMyGameId());
        gameUserInfoNew = guessNumberGameRepository.getGameUserInfo(gameUserInfoNew.getUserId());

        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGameOnce);
        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGameTwice);
        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGame3);
        assertThat(onceScores).isEqualTo(3);
        assertThat(twiceScores).isEqualTo(3);
        assertThat(scores3).isEqualTo(3);
        assertThat(gameUserInfoNew.getSuccessStayTimes()).isEqualTo(3);
        assertThat(gameUserInfoNew.getScores()).isEqualTo(11);
    }

    @Test
    public void when_user_win_5th() {
        String userId = "7";
        GameUserInfo gameUserInfoNew = guessNumberGameService.register(userId);

        Answer answer = new Answer(Arrays.asList("1 2 3 4".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);

        GuessNumberGame guessNumberGameOnce = guessNumberGameService.start(userId);
        guessNumberGameOnce.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameOnce.getMyGameId(), "1 2 3 4");

        GuessNumberGame guessNumberGameTwice = guessNumberGameService.start(userId);
        guessNumberGameTwice.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId(), "1 2 3 4");

        GuessNumberGame guessNumberGame3 = guessNumberGameService.start(userId);
        guessNumberGame3.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGame3.getMyGameId(), "1 2 3 4");

        GuessNumberGame guessNumberGame4 = guessNumberGameService.start(userId);
        guessNumberGame4.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGame4.getMyGameId(), "1 2 3 4");

        GuessNumberGame guessNumberGame5 = guessNumberGameService.start(userId);
        guessNumberGame5.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGame5.getMyGameId(), "1 2 3 4");

        int onceScores = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGameOnce.getMyGameId());
        int twiceScores = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId());
        int scores3 = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGame3.getMyGameId());
        int scores4 = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGame4.getMyGameId());
        int scores5 = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGame5.getMyGameId());
        gameUserInfoNew = guessNumberGameRepository.getGameUserInfo(gameUserInfoNew.getUserId());

        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGameOnce);
        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGameTwice);
        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGame3);
        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGame4);
        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGame5);
        assertThat(onceScores).isEqualTo(3);
        assertThat(twiceScores).isEqualTo(3);
        assertThat(scores3).isEqualTo(3);
        assertThat(scores4).isEqualTo(3);
        assertThat(scores5).isEqualTo(3);
        assertThat(gameUserInfoNew.getSuccessStayTimes()).isEqualTo(5);
        assertThat(gameUserInfoNew.getScores()).isEqualTo(20);
    }

    @Test
    public void when_user_win_4th_and_failed_once() {
        String userId = "8";
        GameUserInfo gameUserInfoNew = guessNumberGameService.register(userId);

        Answer answer = new Answer(Arrays.asList("1 2 3 4".split(" ")));
        Mockito.when(randomAnswerGenerator.generateAnswer()).thenReturn(answer);

        GuessNumberGame guessNumberGameOnce = guessNumberGameService.start(userId);
        guessNumberGameOnce.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameOnce.getMyGameId(), "1 2 3 4");

        GuessNumberGame guessNumberGameTwice = guessNumberGameService.start(userId);
        guessNumberGameTwice.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId(), "1 2 3 4");

        GuessNumberGame guessNumberGame3 = guessNumberGameService.start(userId);
        guessNumberGame3.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGame3.getMyGameId(), "1 2 3 4");

        GuessNumberGame guessNumberGame4 = guessNumberGameService.start(userId);
        guessNumberGame4.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGame4.getMyGameId(), "1 2 3 4");

        GuessNumberGame guessNumberGame5 = guessNumberGameService.start(userId);
        guessNumberGame5.setAnswer(answer);
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGame5.getMyGameId(), "1 2 0 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGame5.getMyGameId(), "1 2 0 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGame5.getMyGameId(), "1 2 0 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGame5.getMyGameId(), "1 2 0 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGame5.getMyGameId(), "1 2 0 4");
        guessNumberGameService.guess(gameUserInfoNew.getUserId(), guessNumberGame5.getMyGameId(), "1 2 0 4");

        int onceScores = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGameOnce.getMyGameId());
        int twiceScores = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGameTwice.getMyGameId());
        int scores3 = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGame3.getMyGameId());
        int scores4 = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGame4.getMyGameId());
        int scores5 = guessNumberGameRepository.getScores(gameUserInfoNew.getUserId(), guessNumberGame5.getMyGameId());
        gameUserInfoNew = guessNumberGameRepository.getGameUserInfo(gameUserInfoNew.getUserId());

        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGameOnce);
        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGameTwice);
        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGame3);
        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGame4);
        assertThat(guessNumberGameRepository.getGuessNumberGames()).contains(guessNumberGame5);
        assertThat(onceScores).isEqualTo(3);
        assertThat(twiceScores).isEqualTo(3);
        assertThat(scores3).isEqualTo(3);
        assertThat(scores4).isEqualTo(3);
        assertThat(scores5).isEqualTo(-3);
        assertThat(gameUserInfoNew.getSuccessStayTimes()).isEqualTo(0);
        assertThat(gameUserInfoNew.getScores()).isEqualTo(11);
    }
}
