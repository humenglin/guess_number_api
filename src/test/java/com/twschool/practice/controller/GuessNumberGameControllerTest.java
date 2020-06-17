package com.twschool.practice.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class GuessNumberGameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_return_guess_result_when_guess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tw/test/api/guessGame/guess")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", "1")
                .param("gameId", "1")
                .param("userAnswer", "1 2 3 4"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gameId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userAnswer").value("1 2 3 4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gameResult").value("4A0B"));
    }
}
