package com.twschool.practice.domain;

public enum GameStatus {
    CONTINUED("C"), FAILED("F"), SUCCEED("S");

    private String value;

    GameStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
