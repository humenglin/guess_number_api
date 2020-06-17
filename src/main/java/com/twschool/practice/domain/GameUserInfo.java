package com.twschool.practice.domain;

import lombok.Data;

@Data
public class GameUserInfo {
    private String userId;
    private int scores = 0;

    public GameUserInfo(String userId) {
        this.userId = userId;
    }
}
