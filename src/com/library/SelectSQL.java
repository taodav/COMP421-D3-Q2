package com.library;

import java.sql.Statement;

public class SelectSQL {
    public static State viewLoan(Statement statement) {
        System.out.println("view loan");

        return State.Start;
    }

    public static State checkAvailable(Statement statement) {
        return State.Start;
    }

    public static State checkSection(Statement statement) {
        return State.Start;
    }
}
