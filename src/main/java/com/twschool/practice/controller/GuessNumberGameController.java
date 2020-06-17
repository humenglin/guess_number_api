package com.twschool.practice.controller;

import com.twschool.practice.controller.response.GuessNumberGameResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tw/test/api/guessGame")
public class GuessNumberGameController {

    @GetMapping("/guess")
    public GuessNumberGameResult guess(@RequestParam String userId, @RequestParam String gameId, @RequestParam String userAnswer) {
        GuessNumberGameResult guessNumberGameResult = new GuessNumberGameResult(userId, gameId, userAnswer);
        guessNumberGameResult.setGameResult("4A0B");
        return guessNumberGameResult;
    }
}
