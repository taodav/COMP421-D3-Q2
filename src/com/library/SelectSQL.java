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
            System.out.println("Are you looking for the availability of a book or periodical?");
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

        String selectBookAvail = "SELECT media.title AS TITLE, " +
                "author.aname AS AUTHOR, copy.cid AS CID, " +
                "copy.laddress AS ADDRESS, copy.sid AS SEC, " +
                "copy.rental_status AS RSTAT" +
                " FROM media, book, author, copy" +
                " WHERE media.mid = book.mid AND author.aid = book.aid" +
                " AND copy.mid = media.mid AND copy.rental_status = 1";

        if (author.length() > 0) {
            selectBookAvail += " AND author.aname = '" + author + "'";
        }

        if (title.length() > 0) {
            selectBookAvail += " AND media.title = '" + title + "'";
        }

        try {
            ResultSet rs = statement.executeQuery(selectBookAvail);
            System.out.println("Here are the available copies we found:");
            String[] booksHeader = {"title", "author", "cid", "address", "section", "rental_status"};
            System.out.format("%20s%20s%20s%20s%20s%20s\n", booksHeader);
            while (rs.next()) {
                String section = String.valueOf(rs.getInt("SEC"));
                String cid = String.valueOf(rs.getInt("CID"));
                String address = rs.getString("ADDRESS");
                String currentTitle = rs.getString("TITLE");
                String currentAuthor = rs.getString("AUTHOR");
                String rentalStat = String.valueOf(rs.getInt("RSTAT"));

                String[] row = {currentTitle, currentAuthor, cid, address, section, rentalStat};
                System.out.format("%20s%20s%20s%20s%20s%20s\n", row);
            }
            System.out.println();
        }
        catch (SQLException e) {
            CustomSQLException.printSQLException(e);
        }
    }

    private void printPeriodicalAvail() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Who's the publisher of the periodical you're looking for? (leave blank to not specify)");
        String publisher = scanner.nextLine();
        System.out.println("What's the title of the periodical you're looking for? (leave blank to not specify)");
        String title = scanner.nextLine();
        System.out.println("What's the issue of the periodical you're looking for? (leave blank to not specify)");
        String issue = scanner.nextLine();

        String selectPeriodicalAvail = "SELECT media.title AS TITLE, " +
                "media.pname AS PUBNAME, periodical.issue AS ISSUE, copy.cid AS CID, " +
                "copy.laddress AS ADDRESS, copy.sid AS SEC, " +
                "copy.rental_status AS RSTAT" +
                " FROM media, periodical, copy" +
                " WHERE media.mid = periodical.mid" +
                " AND copy.mid = media.mid AND copy.rental_status = 1";

        if (publisher.length() > 0) {
            selectPeriodicalAvail += " AND media.pname = '" + publisher + "'";
        }
        if (title.length() > 0) {
            selectPeriodicalAvail += " AND media.title = '" + title + "'";
        }
        if (issue.length() > 0) {
            selectPeriodicalAvail += " AND periodical.issue = " + issue;
        }

        try {
            ResultSet rs = statement.executeQuery(selectPeriodicalAvail);
            System.out.println("Here are the available copies we found:");
            String[] booksHeader = {"title", "publisher", "issue", "cid", "address", "section", "rental_status"};
            System.out.format("%20s%20s%20s%20s%20s%20s%20s\n", booksHeader);
            while (rs.next()) {
                String section = String.valueOf(rs.getInt("SEC"));
                String cid = String.valueOf(rs.getInt("CID"));
                String address = rs.getString("ADDRESS");
                String currentTitle = rs.getString("TITLE");
                String currentPublisher = rs.getString("PUBNAME");
                String currentIssue = String.valueOf(rs.getInt("ISSUE"));
                String rentalStat = String.valueOf(rs.getInt("RSTAT"));

                String[] row = {currentTitle, currentPublisher, currentIssue, cid, address, section, rentalStat};
                System.out.format("%20s%20s%20s%20s%20s%20s%20s\n", row);
            }
            System.out.println();
        }
        catch (SQLException e) {
            CustomSQLException.printSQLException(e);
        }
    }

    public State viewPatronLoanHolds() {
        Scanner scanner = new Scanner(System.in);
        outer: while (true) {
            System.out.println("Please enter the user ID of the patron");
            System.out.print("> ");
            String id = scanner.nextLine();

            String selectLoans = "SELECT person.id AS ID, person.name AS PNAME," +
                    " media.title AS TITLE, media.mid AS MID" +
                    " copy.cid AS CID, loan.loan_period AS LPERIOD," +
                    " copy.rental_status AS RSTAT" +
                    " FROM media, copy, person, loan" +
                    " WHERE media.mid = copy.mid AND loan.pid = person.id" +
                    " AND copy.mid = media.mid AND loan.mid = media.mid" +
                    " AND loan.cid = copy.cid";

            String selectHolds = "SELECT person.id AS ID, person.name AS PNAME," +
                    " media.title AS TITLE, media.mid AS MID" +
                    " copy.cid AS CID, hold.hold_period AS HPERIOD," +
                    " copy.rental_status AS RSTAT" +
                    " FROM media, copy, person, hold" +
                    " WHERE media.mid = copy.mid AND hold.pid = person.id" +
                    " AND copy.mid = media.mid AND hold.mid = media.mid" +
                    " AND hold.cid = copy.cid";

            if (id.length() > 0) {
                selectLoans = " AND person.id = " + id;
                selectHolds = " AND person.id = " + id;

            }

            selectLoans += " ORDER BY loan.loan_period";

            try {
                ResultSet rs = statement.executeQuery(selectLoans);
                System.out.println("Here are the loans we found, ordered by due date:");
                String[] booksHeader = {"id", "name", "title", "mid", "cid", "loan_period", "rental_status"};
                System.out.format("%20s%20s%20s%20s%20s%20s%20s\n", booksHeader);
                while (rs.next()) {
                    String currentId = String.valueOf(rs.getInt("ID"));
                    String currentName = rs.getString("PNAME")
                    String cid = String.valueOf(rs.getInt("CID"));
                    String mid = String.valueOf(rs.getInt("MID"));
                    String currentTitle = rs.getString("TITLE");
                    String rentalStat = String.valueOf(rs.getInt("RSTAT"));
                    String loan_period = String.valueOf(rs.getDate("LPERIOD"));

                    String[] row = {currentId, currentName, currentTitle, mid, cid, loan_period, rentalStat};
                    System.out.format("%20s%20s%20s%20s%20s%20s%20s\n", row);
                }
                System.out.println();

                ResultSet rsHolds = statement.executeQuery(selectHolds);
                System.out.println("Here are the holds we found, ordered by due date:");
                String[] holdsHeader = {"id", "name", "title", "mid", "cid", "hold_period", "rental_status"};
                System.out.format("%20s%20s%20s%20s%20s%20s%20s\n", holdsHeader);
                while (rsHolds.next()) {
                    String currentId = String.valueOf(rs.getInt("ID"));
                    String currentName = rs.getString("PNAME")
                    String cid = String.valueOf(rs.getInt("CID"));
                    String mid = String.valueOf(rs.getInt("MID"));
                    String currentTitle = rs.getString("TITLE");
                    String rentalStat = String.valueOf(rs.getInt("RSTAT"));
                    String hold_period = String.valueOf(rs.getDate("HPERIOD"));

                    String[] row = {currentId, currentName, currentTitle, mid, cid, hold_period, rentalStat};
                    System.out.format("%20s%20s%20s%20s%20s%20s%20s\n", row);
                }
                inner: while (true) {
                    System.out.println();
                    System.out.println("Would you like to filter these results by:");
                    System.out.println("1. Previously due");
                    System.out.println("2. Currently loaned out/on hold");
                    System.out.println("3. Overdue loans\n");
                    System.out.println("4. Search another ID");
                    System.out.println("5. Back to main menu");

                    switch (scanner.nextLine()) {
                        case "1":
                            viewPreviouslyDue(rs, rsHolds);
                            break;
                        case "2":
                            viewCurrentlyDue(rs, rsHolds);
                            break;
                        case "3":
                            viewOverdue(rs);
                            break;
                        case "4":
                            break inner;
                        case "5":
                            break outer;
                        default:
                            System.out.println("Option unavailable. Please choose another option");
                            break;
                    }
                }
            }
            catch (SQLException e) {
                CustomSQLException.printSQLException(e);
            }

        }
        return State.Start;
    }

    private void viewPreviouslyDue(ResultSet rsLoans, ResultSet rsHolds) {

    }

    private void viewCurrentlyDue(ResultSet rsLoans, ResultSet rsHolds) {

    }

    private void viewOverdue(ResultSet rsLoans) {

    }


}
