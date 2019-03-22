package com.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;


class DBPopulate
{
    private static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

    private static String randDate(int start_year, int end_year) {
        GregorianCalendar gc = new GregorianCalendar();
        int year = randBetween(start_year, end_year);
        gc.set(gc.YEAR, year);
        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
        gc.set(gc.DAY_OF_YEAR, dayOfYear);
        String strYear = String.format("%04d", gc.get(gc.YEAR));
        String strMonth = String.format("%02d", (gc.get(gc.MONTH) + 1));
        String strDay = String.format("%02d", gc.get(gc.DAY_OF_MONTH));
        return strYear + "-" + strMonth + "-" + strDay;
    }

    private static String randPhone() {
        int area = randBetween(100, 999);
        int first3 = randBetween(100, 999);
        int last4 = randBetween(1000, 9999);
        return area + "-" + first3 + "-" + last4;
    }

    public static void main ( String [ ] args ) throws SQLException
    {
        // Register the driver.  You must register the driver before you can use it.
        try {
            DriverManager.registerDriver( new org.postgresql.Driver() ) ;
        } catch (Exception cnfe){
            System.out.println("Class not found");
        }
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");


        // This is the url you must use for Postgresql.
        //Note: This url may not valid now !
        String url = "jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421";

        String usernameString = "cs421g38";
//        String passwordString = PASSWORD;
        String passwordString = "Datamybase2";
        int sqlCode=0;      // Variable to hold SQLCODE
        String sqlState="00000";  // Variable to hold SQLSTATE

        Connection con = DriverManager.getConnection(url, usernameString, passwordString);
        Statement statement = con.createStatement( ) ;


        // Inserting Data into the table
        // Assume Database was freshly populated with tables
        try {
            // Insert in Libraries
            String[] libNames = {"McLennan Library", "Schulich Library", "Redpath Library", "Law Library", "Music Library"};
            for (String libName : libNames) {
                String libraryString = "INSERT INTO library (laddress, hours, holidays) VALUES (\'" + libName + "\', \'0800 - 23:59\', \'Christmas\')";
                statement.executeUpdate(libraryString);
            }
            System.out.println("Libraries added");

            // Insert in Patrons
            int numPatrons = 20;
            for (int i = 0; i < numPatrons; i++) {
                String name = "patron_" + i;
                String dob = randDate(1980, 2010);
                String address = i + " fake street";
                String phone = randPhone();

                String insertPatron = "WITH ret AS (INSERT INTO person VALUES (DEFAULT, \'" + name + "\', \'" + dob + "\', \'" + address + "\') RETURNING id) " +
                        "INSERT INTO patron SELECT id, \'" + phone + "\', 1, 0.0, TRUE FROM ret";

                statement.executeUpdate(insertPatron);
            }
            System.out.println("Patrons added");

            // Insert publishers
            int numPublishers = 5;
            for (int i = 0; i < numPublishers; i++) {
                String publisher = "publisher_" + i;
                String insertPublisher = "INSERT INTO publisher VALUES (\'" + publisher + "\', \'USA\')";
                statement.executeUpdate(insertPublisher);
            }
            System.out.println("Publishers added");

            // Insert authors
            int numAuthors = 20;
            for (int i = 0; i < numAuthors; i++) {
                String author = "author_" + i;
                String insertAuthor = "INSERT INTO author VALUES (DEFAULT, \'" + author + "\')";
                statement.executeUpdate(insertAuthor);
            }
            System.out.println("Authors added");

            // Insert sections
            int sectionsPerLib = 10;
            for (int i = 0; i < sectionsPerLib; i++) {
                for (String libName : libNames) {
                    String insertSection = "INSERT INTO section VALUES (" + i + ", \'" + libName + "\')";
                    statement.executeUpdate(insertSection);
                }
            }
            System.out.println("Sections added");

            // Insert books and copies of books
            int numBooks = 40;

            for (int i = 0; i < numBooks; i++) {
                String title = "book_" + i;
                String publisher = "publisher_" + randBetween(0, numPublishers - 1);
                String isbn = String.valueOf(i * 20 + 12345);
                String authorName = "author_" + randBetween(0, numAuthors - 1);

                String insertBook = "WITH ret AS (INSERT INTO media VALUES (DEFAULT, \'" + title + "\', 1, \'" + publisher + "\') RETURNING mid)" +
                        "INSERT INTO book SELECT mid, \'" + isbn + "\', author.aid, \'Fiction\', 1 FROM ret, author WHERE author.aname = \'" + authorName + "\' LIMIT 1";

                statement.executeUpdate(insertBook);

                // Insert copies of book
                int copiesPerBook = 3;
                for (int j = 0; j < copiesPerBook; j++) {
                    String library = libNames[randBetween(0, libNames.length - 1)];
                    int section = randBetween(0, sectionsPerLib - 1);
                    String insertCopy = "WITH ret AS (SELECT media.mid, section.laddress, section.sid FROM media, section " +
                            "WHERE media.title = \'" + title + "\' AND section.laddress =  \'" + library + "\' AND section.sid = " + section + ")" +
                            "INSERT INTO copy SELECT mid, " + j + ", 1, laddress, sid FROM ret";
                    statement.executeUpdate(insertCopy);
                }

            }
            System.out.println("Books and Copies of books added");

            // Insert periodicals
            int numPeriodicals = 40;

            for (int i = 0; i < numPeriodicals; i++) {
                String publisher = "publisher_" + randBetween(0, numPublishers - 1);
                String title = "periodical_from_publisher_" + publisher + "_issue_" + i;

                String insertPeriodical = "WITH ret AS (INSERT INTO media VALUES (DEFAULT, \'" + title + "\', 1, \'" + publisher + "\') RETURNING mid)" +
                        "INSERT INTO periodical SELECT mid, "+ i + " FROM ret";

                statement.executeUpdate(insertPeriodical);

                // Insert copies of periodical
                int copiesPerPeriodical = 3;
                for (int j = 0; j < copiesPerPeriodical; j++) {
                    String library = libNames[randBetween(0, libNames.length - 1)];
                    int section = randBetween(0, sectionsPerLib - 1);
                    String insertCopy = "WITH ret AS (SELECT media.mid, section.laddress, section.sid FROM media, section " +
                            "WHERE media.title = \'" + title + "\' AND section.laddress =  \'" + library + "\' AND section.sid = " + section + ")" +
                            "INSERT INTO copy SELECT mid, " + j + ", 1, laddress, sid FROM ret";
                    statement.executeUpdate(insertCopy);
                }
            }
            System.out.println("Periodicals and Copies of periodicals added");

            // Insert librarians
            int numLibrarians = 10;
            for (int i = 0; i < numLibrarians; i++) {
                String name = "librarian_" + i;
                String dob = randDate(1970, 1990);
                String address = i + " fake librarian street";
                String library = libNames[randBetween(0, libNames.length - 1)];

                String insertLibrarian = "WITH ret AS (INSERT INTO person VALUES (DEFAULT, \'" + name + "\', \'" + dob + "\', \'" + address + "\') RETURNING id)" +
                        "INSERT INTO librarian SELECT id, \'Mon, Tues, Weds\', 3000, \'" + library + "\' FROM ret";

                statement.executeUpdate(insertLibrarian);
            }
            System.out.println("Librarians added");

            // 1 - copy is free, 2 - copy on hold, 3 - copy being lent out
            // Add Holds

            int numHolds = 20;
            for (int i = 1; i <= numHolds; i++) {
                // We also use i as the mediaID
                int cid = 1;
                int pid = randBetween(1, numPatrons);
                String date = randDate(1999, 2020);
                boolean active = true;
                try {
                    Date dueDate = formatter.parse(date);
                    if (today.compareTo(dueDate) > 0) {
                        active = false;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String insertHold = "WITH ret AS (SELECT copy.mid, copy.cid, person.id FROM copy, person " +
                        "WHERE copy.mid = " + i + " AND copy.cid = " + cid + " AND person.id = " + pid + ")" +
                        "INSERT INTO hold SELECT mid, cid, id, \'" + date + "\' FROM ret";
                statement.executeUpdate(insertHold);

                // Now we need to set our copy on hold
                if (active) {
                    String updateCopy = "UPDATE copy SET rental_status = 2 WHERE mid = " + i + " AND cid = " + cid;
                    statement.executeUpdate(updateCopy);
                }
            }
            System.out.println("Holds added");

            int numLoans = 20;

            for (int i = numBooks + 1; i <= numBooks + numLoans + 1; i++) {
                // We also use i as the mediaID
                int cid = 1;
                int pid = randBetween(1, numPatrons);
                int lid = randBetween(numPatrons + 1, numPatrons + numLibrarians);
                String date = randDate(1999, 2030);
                boolean active = true;
                try {
                    Date dueDate = formatter.parse(date);
                    if (today.compareTo(dueDate) > 0 && randBetween(0, 2) > 1) {
                        active = false;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String activeString = String.valueOf(active).toUpperCase();
                String insertLoan = "WITH ret AS (SELECT copy.mid, copy.cid, person.id, librarian.lid FROM copy, librarian, person " +
                        "WHERE copy.mid = " + i + " AND copy.cid = " + cid + " AND librarian.lid = " + lid + " AND person.id = " + pid + ")" +
                        "INSERT INTO loan SELECT mid, cid, id as pid, lid, \'" + date + "\', 0, " + activeString + " FROM ret";
                statement.executeUpdate(insertLoan);

                // Now we need to set our copy on hold
                if (active) {
                    String updateCopy = "UPDATE copy SET rental_status = 3 WHERE mid = " + i + " AND cid = " + cid;
                    statement.executeUpdate(updateCopy);
                }

            }
            System.out.println("Loans added");


        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            // Your code to handle errors comes here;
            // something more meaningful than a print would be good
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
        }

        // Finally but importantly close the statement and connection
        statement.close ( ) ;
        con.close ( ) ;
    }
}

