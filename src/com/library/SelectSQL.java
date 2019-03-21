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

    public State viewLoansHolds() {

        Scanner scanner = new Scanner(System.in);
        outer: while (true) {
            System.out.println("Are you looking for loans/holds of a book or periodical?");
            System.out.println("1. Book");
            System.out.println("2. Periodical");
            System.out.println("3. Go back\n");
            System.out.print("> ");
            switch (scanner.nextLine()) {
                case "1":
                    printBookLoansHolds();
                    break;
                case "2":
                    printPeriodicalLoansHolds();
                    break;
                case "3":
                    break outer;
                default:
                    System.out.println("Option not available. Please choose again.");
            }
        }

        return State.Start;
    }
    private void printPeriodicalLoansHolds() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Who's the publisher of the periodical you're looking for? (leave blank to not specify)");
        String publisher = scanner.nextLine();
        System.out.println("What's the title of the periodical you're looking for? (leave blank to not specify)");
        String title = scanner.nextLine();
        System.out.println("What's the issue of the periodical you're looking for? (leave blank to not specify)");
        String issue = scanner.nextLine();

        String selectLoans = "SELECT loan.mid AS MID, loan.cid AS CID, pat.name AS PNAME, lp.name AS LNAME, loan.loan_period AS LPERIOD, media.title AS TITLE, periodical.issue AS ISSUE, publisher.pname AS PUBNAME" +
                " FROM loan, media, periodical, publisher, person AS lp, person AS pat " +
                "WHERE loan.mid = media.mid AND media.mid = periodical.mid AND media.pname = publisher.pname AND lp.id = loan.lid AND pat.id = loan.pid";

        String selectHolds = "SELECT hold.mid AS MID, hold.cid AS CID, person.name AS PNAME, hold.hold_period AS HPERIOD, media.title AS TITLE, periodical.issue AS ISSUE, publisher.pname AS PUBNAME" +
                " FROM hold, media, periodical, publisher, person" +
                " WHERE hold.mid = media.mid AND media.mid = periodical.mid AND media.pname = publisher.pname AND person.id = hold.pid";

        if (publisher.length() > 0) {
            selectLoans += " AND media.pname = '" + publisher + "'";
            selectHolds += " AND media.pname = '" + publisher + "'";
        }

        if (title.length() > 0) {
            selectLoans += " AND media.title = '" + title + "'";
            selectHolds += " AND media.title = '" + title + "'";
        }

        if (issue.length() > 0) {
            selectLoans += " AND periodical.issue = " + issue;
            selectHolds += " AND periodical.issue = " + issue;
        }

        try {
            ResultSet rs = statement.executeQuery(selectLoans);
            System.out.println("Here are the loans that we found:");
            String[] loansHeader = {"mid", "cid", "patron_name", "librarian_name", "loan_period", "title", "issue", "publisher"};
            System.out.format("%20s%20s%20s%20s%20s%20s%20s%20s\n", loansHeader);
            while (rs.next()) {
                String mid = String.valueOf(rs.getInt("MID"));
                String cid = String.valueOf(rs.getInt("CID"));
                String pname = rs.getString("PNAME");
                String librarian = rs.getString("LNAME");
                String loan_period = String.valueOf(rs.getDate("LPERIOD"));
                String currentTitle = rs.getString("TITLE");
                String currentIssue = String.valueOf(rs.getInt("ISSUE"));
                String currentPublisher = rs.getString("PUBNAME");

                String[] row = {mid, cid, pname, librarian, loan_period, currentTitle, currentIssue, currentPublisher};
                System.out.format("%20s%20s%20s%20s%20s%20s%20s%20s\n", row);
            }

            System.out.println("\nHere are the holds that we found:");

            rs = statement.executeQuery(selectHolds);
            String[] holdsHeader = {"mid", "cid", "patron_name", "hold_period", "title", "issue", "publisher"};
            System.out.format("%20s%20s%20s%20s%20s%20s%20s\n", holdsHeader);
            while (rs.next()) {
                String mid = String.valueOf(rs.getInt("MID"));
                String cid = String.valueOf(rs.getInt("CID"));
                String pname = rs.getString("PNAME");
                String loan_period = String.valueOf(rs.getDate("HPERIOD"));
                String currentTitle = rs.getString("TITLE");
                String currentIssue = String.valueOf(rs.getInt("ISSUE"));
                String currentPublisher = rs.getString("PNAME");

                String[] row = {mid, cid, pname, loan_period, currentTitle, currentIssue, currentPublisher};
                System.out.format("%20s%20s%20s%20s%20s%20s%20s\n", row);
            }
            System.out.println();
        }
        catch (SQLException e) {
            CustomSQLException.printSQLException(e);
        }
    }

    private void printBookLoansHolds() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Who's the author of the book you're looking for? (leave blank to not specify)");
        String author = scanner.nextLine();
        System.out.println("What's the title of the book you're looking for? (leave blank to not specify)");
        String title = scanner.nextLine();

        String selectLoans = "SELECT loan.mid AS MID, loan.cid AS CID, pat.name AS PNAME, lp.name AS LNAME, loan.loan_period AS LPERIOD, media.title AS TITLE, author.aname AS AUTHOR" +
                " FROM loan, media, book, person AS lp, person AS pat, author " +
                "WHERE loan.mid = media.mid AND media.mid = book.mid AND book.aid = author.aid AND lp.id = loan.lid AND pat.id = loan.pid";

        String selectHolds = "SELECT hold.mid AS MID, hold.cid AS CID, person.name AS PNAME, hold.hold_period AS HPERIOD, media.title AS TITLE, author.aname AS AUTHOR" +
                " FROM hold, media, book, author, person" +
                " WHERE hold.mid = media.mid AND media.mid = book.mid AND book.aid = author.aid AND person.id = hold.pid";

        if (author.length() > 0) {
            selectLoans += " AND author.aname = '" + author + "'";
            selectHolds += " AND author.aname = '" + author + "'";

        }

        if (title.length() > 0) {
            selectLoans += " AND media.title = '" + title + "'";
            selectHolds += " AND media.title = '" + title + "'";
        }

        try {
            ResultSet rs = statement.executeQuery(selectLoans);
            System.out.println("Here are the loans that we found:");
            String[] loansHeader = {"mid", "cid", "patron_name", "librarian_name", "loan_period", "title", "author"};
            System.out.format("%15s%15s%15s%15s%15s%15s%15s\n", loansHeader);
            while (rs.next()) {
                String mid = String.valueOf(rs.getInt("MID"));
                String cid = String.valueOf(rs.getInt("CID"));
                String pname = rs.getString("PNAME");
                String librarian = rs.getString("LNAME");
                String loan_period = String.valueOf(rs.getDate("LPERIOD"));
                String currentTitle = rs.getString("TITLE");
                String currentAuthor = rs.getString("AUTHOR");

                String[] row = {mid, cid, pname, librarian, loan_period, currentTitle, currentAuthor};
                System.out.format("%15s%15s%15s%15s%15s%15s%15s\n", row);
            }

            System.out.println("\nHere are the holds that we found:");

            rs = statement.executeQuery(selectHolds);
            System.out.println("Here are the loans that we found:");
            String[] holdsHeader = {"mid", "cid", "patron_name", "hold_period", "title", "author"};
            System.out.format("%15s%15s%15s%15s%15s%15s\n", holdsHeader);
            while (rs.next()) {
                String mid = String.valueOf(rs.getInt("MID"));
                String cid = String.valueOf(rs.getInt("CID"));
                String pname = rs.getString("PNAME");
                String loan_period = String.valueOf(rs.getDate("HPERIOD"));
                String currentTitle = rs.getString("TITLE");
                String currentAuthor = rs.getString("AUTHOR");

                String[] row = {mid, cid, pname, loan_period, currentTitle, currentAuthor};
                System.out.format("%15s%15s%15s%15s%15s%15s\n", row);
            }
            System.out.println();
        }
        catch (SQLException e) {
            CustomSQLException.printSQLException(e);
        }
    }

    public State checkAvailable() {
        Scanner scanner = new Scanner(System.in);
        outer: while (true) {
            System.out.println("Are you looking for a book or periodical?");
            System.out.println("1. Book");
            System.out.println("2. Periodical");
            System.out.println("3. Go back\n");
            System.out.print("> ");
            switch (scanner.nextLine()) {
                case "1":
                    printBookAvail();
                    break;
                case "2":
                    printPeriodicalAvail();
                    break;
                case "3":
                    break outer;
                default:
                    System.out.println("Option not available. Please choose again.");
            }
        }

        return State.Start;
    }
    private void printBookAvail() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Who's the author of the book you're looking for? (leave blank to not specify)");
        String author = scanner.nextLine();
        System.out.println("What's the title of the book you're looking for? (leave blank to not specify)");
        String title = scanner.nextLine();
    }

    private void printPeriodicalAvail() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Who's the publisher of the periodical you're looking for? (leave blank to not specify)");
        String publisher = scanner.nextLine();
        System.out.println("What's the title of the periodical you're looking for? (leave blank to not specify)");
        String title = scanner.nextLine();
        System.out.println("What's the issue of the periodical you're looking for? (leave blank to not specify)");
        String issue = scanner.nextLine();
    }

    public State checkSection() {
        return State.Start;
    }
}
