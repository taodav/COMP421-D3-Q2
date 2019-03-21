package com.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

enum State {Start, ViewLoan, CheckAvail, Locate, Return, Loan, Exit};


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
        Statement statement = con.createStatement();

        while (true) {
            try {
                switch(state) {
                    case Start:
                        System.out.println("Welcome to the McGill Library terminal!");
                        System.out.println("Please select an option below:");
                        System.out.println("1. View loans/holds (By user or title)");
                        System.out.println("2. Check for availability (By title and/or author)");
                        System.out.println("3. Locate media (By title, author, issue)");
                        System.out.println("4. Return a Loan");
                        System.out.println("5. Loan out media");
                        System.out.println("6. Exit\n");
                        System.out.print("> ");

                        try {
                            int userInp = -1;
                            Scanner inp = new Scanner(System.in);
                            userInp = inp.nextInt();
                            state = State.values()[userInp];
                        }
                        catch (InputMismatchException e) {
                            System.out.println("invalid input \n");
                        }
                    case ViewLoan:
                        state = SelectSQL.viewLoan(statement);

                        break;
                    case CheckAvail:

                        break;
                    case Locate:

                        break;
                    case Return:

                        break;
                    case Loan:

                        break;
                    case Exit:
                        return;
                    default:
                        break;
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

    }
}
