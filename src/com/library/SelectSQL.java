package com.library;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class SelectSQL {
    private Statement statement;

    public SelectSQL(Statement statement) {
        this.statement = statement;
    }

    public State viewLoan() {
        System.out.println("Are you looking for loans of a book or periodical?");
        System.out.println("1. Book");
        System.out.println("2. Periodical\n");
        System.out.println("3. Go back\n");
        System.out.print("> ");

        Scanner scanner = new Scanner(System.in);
        outer: while (true) {
            switch (scanner.nextLine()) {
                case "1":
                    printBookLoans();
                    break;
                case "2":
                    break;
                case "3":
                    break outer;
                default:
                    System.out.println("Option not available. Please choose again.");
            }
        }

        return State.Start;
    }
    private void printBookLoans() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Who's the author of the book you're looking for? (leave blank to not specify)");
        String author = scanner.nextLine();
        System.out.println("What's the title of the book you're looking for? (leave blank to not specify)");
        String title = scanner.nextLine();

        String selectLoans = "SELECT loan.mid AS MID, loan.cid AS CID, loan.pid AS PID, media.title AS TITLE, author.aname AS AUTHOR" +
                " FROM loan, media, book, author WHERE loan.mid = media.mid AND media.mid = book.mid AND book.aid = author.aid";

        if (author.length() > 0) {
            selectLoans += "AND book.author = " + author;
        }

        if (title.length() > 0) {
            selectLoans += "AND media.title";
        }

        try {
            ResultSet rs = statement.executeQuery(selectLoans);
            while (rs.next()) {
                String mid = String.valueOf(rs.getInt("MID"));
                String cid = String.valueOf(rs.getInt("CID"));
                String pid = String.valueOf(rs.getInt("PID"));
                String currentTitle = rs.getString("TITLE");
                String currentAuthor = rs.getString("AUTHOR");

                System.out.println(mid + "\t" + cid + "\t" + pid + "\t" +
                        currentTitle + "\t" + currentAuthor);

            }// WHat's next??
        }
        catch (SQLException e) {
            CustomSQLException.printSQLException(e);
        }

    }

    public State checkAvailable() {
        return State.Start;
    }

    public State checkSection() {
        return State.Start;
    }
}
