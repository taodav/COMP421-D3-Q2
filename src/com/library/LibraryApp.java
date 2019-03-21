package com.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.InputMismatchException;
import java.util.Scanner;

enum State {Start, Loan_Copy, Return_Copy, View_Loans_Holds, Check_Available, View_Patron_Loans_Holds, Exit};
enum RentalStatus {Nothing, Available, OnLoan, OnHold};

public class LibraryApp {
    static private State state = State.Start;

    public static void main(String args[]) throws SQLException
    {
        // Register the driver
        try {
            DriverManager.registerDriver( new org.postgresql.Driver() ) ;
        } catch (Exception cnfe){
            System.out.println("Class not found");
        }

        String url = "jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421";

        String usernameString = "cs421g38";
//		String passwordString = PASSWORD;
        String passwordString = "Datamybase2";

        int sqlCode=0;      // Variable to hold SQLCODE
        String sqlState="00000";  // Variable to hold SQLSTATE

        Connection con = DriverManager.getConnection(url, usernameString, passwordString);
        Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        SelectSQL select = new SelectSQL(statement);

        System.out.println("Welcome to the Group 38 SQL Console 3000");
        while (true) {
            try {
                switch (state) {
                    case Start:
                        System.out.println("Please select one of the following options by entering it's corresponding number");
                        System.out.println("1. Loan a copy");
                        System.out.println("2. Return a copy");
                        System.out.println("3. View the loans and holds of a Book/Periodical");
                        System.out.println("4. Check the availability of a Book/Periodical");
                        System.out.println("5. Get the loans and holds of a Patron");
                        System.out.println("6. Exit\n");
                        System.out.print("> ");

                        Scanner scanner = new Scanner(System.in);
                        switch (scanner.nextLine()) {
                            case "1":
                                state = State.Loan_Copy;
                                break;
                            case "2":
                                state = State.Return_Copy;
                                break;
                            case "3":
                                state = State.View_Loans_Holds;
                                break;
                            case "4":
                                state = State.Check_Available;
                                break;
                            case "5":
                                state = State.View_Patron_Loans_Holds;
                                break;
                            case "6":
                                return;
                            default:
                                System.out.println("Invalid selection. Please try again");
                                break;
                        }
                        break;
                    case Loan_Copy:
                        break;
                    case Return_Copy:
                        break;
                    case View_Loans_Holds:
                        state = select.viewLoansHolds();
                        break;
                    case Check_Available:
                        state = select.checkAvailable();
                        break;
                    case View_Patron_Loans_Holds:
                        state = select.viewPatronLoanHolds();
                        break;
                    case Exit:
                        return;
                    default:
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
