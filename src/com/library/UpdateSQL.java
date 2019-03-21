package com.library;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class UpdateSQL {
    private Statement statement;

    public UpdateSQL(Statement statement) {
        this.statement = statement;
    }

    public State createLoan() {
    	 Scanner scanner = new Scanner(System.in);
         String regex = "[0-9]+";
    	 String MID = "";
    	 String CID = "";
    	 String PID = "";
    	 String LID = "";
    	 String DATE = "";
         outer: while (true) {
             System.out.println("Please specify an available media item, a patron, and optionally a librarian");
             System.out.println("Currently selected:");
             System.out.println("    Title: "+getTitle(MID));
             System.out.println("    Patron name: "+getName(PID));
             System.out.println("    Librarian name: "+getName(LID));
             System.out.println("1. Specify Media");
             System.out.println("2. Specify User");
             System.out.println("3. Specify Librarian");
             System.out.println("4. Specify Loan date");
             System.out.println("5. Create loan");
             System.out.println("6. Go back\n");
             System.out.print("> ");
             switch (scanner.nextLine()) {
                 case "1":
                	 System.out.println("Enter either the mediaID or the title of the item you wish to loan");
                     System.out.print("> ");
                     String mediaIdOrTitle = scanner.nextLine();
                     String tmpMID = null;
                     if(mediaIdOrTitle.matches(regex)) {
                    	 System.out.println("You have entered a mediaId");
                    	 tmpMID = mediaIdOrTitle;
                     } else {
                    	 System.out.println("You have entered a title");
                    	 tmpMID = getMediaId(mediaIdOrTitle);
                     }
                     if(tmpMID==null) {
                		 System.out.println("No media item found with: "+mediaIdOrTitle);
                		 break;
                	 }
                     String tmpCID = getCopyId(tmpMID, RentalStatus.Available);
                     if(tmpCID==null) {
                    	 System.out.println("No available copies");
                    	 break;
                     }
                     MID = tmpMID;
                     CID = tmpCID;
                     break;
                 case "2":
                	 System.out.println("Enter either the patronId or the name of the patron to loan to");
                     System.out.print("> ");
                     String patronIdOrTitle = scanner.nextLine();
                     String tmpPID = null;
                     if(patronIdOrTitle.matches(regex)) {
                    	 System.out.println("You have entered a patronId");
                    	 tmpPID = patronIdOrTitle;
                     } else {
                    	 System.out.println("You have entered a name");
                    	 tmpPID = getPatronId(patronIdOrTitle);
                     }
                     if(tmpPID==null) {
                		 System.out.println("No patron found with: "+patronIdOrTitle);
                		 break;
                	 }                    
                     PID = tmpPID;
                     break;
                 case "3":
                	 System.out.println("Enter either the librarianId or the name of the librarian");
                     System.out.print("> ");
                     String librarianIdOrTitle = scanner.nextLine();
                     String tmpLID = null;
                     if(librarianIdOrTitle.matches(regex)) {
                    	 System.out.println("You have entered a librarianId");
                    	 tmpLID = librarianIdOrTitle;
                     } else {
                    	 System.out.println("You have entered a name");
                    	 tmpLID = getLibrarianId(librarianIdOrTitle);
                     }
                     if(tmpLID==null) {
                		 System.out.println("No librarian found with: "+librarianIdOrTitle);
                		 break;
                	 }
                     LID = tmpLID;
                     break;
                 case "4":
                	 System.out.println("Please enter the year like 1998");
                     System.out.print("> ");
                     String year = scanner.nextLine();
                     System.out.println("Please enter the month like 02");
                     System.out.print("> ");
                     String month = scanner.nextLine();
                     System.out.println("Please enter the day like 23");
                     System.out.print("> ");
                     String day = scanner.nextLine();
                     DATE = year+"-"+month+"-"+day;
                	 break;
                 case "5":
                	 if(MID.isEmpty()) {
                		 System.out.println("Please specify a media item before attempting to create a loan");
                		 break;
                	 }
                	 if(CID.isEmpty()) {
                		 System.out.println("Please specify a media item before attempting to create a loan");
                		 break;
                	 }
                	 if(PID.isEmpty()) {
                		 System.out.println("Please specify a patron before attempting to create a loan");
                		 break;
                	 }
                	 if(DATE.isEmpty()) {
                		 System.out.println("Please specify a date before attempting to create a loan");
                		 break;
                	 }
                	 
                	 String query = "INSERT VALUES("+MID+", "+CID+", "+PID+", "+LID+", "+DATE+", 0)";
                	 
                	 try {
             			ResultSet rs = statement.executeQuery(query);
                     }
             		 catch (SQLException e) {
             			 CustomSQLException.printSQLException(e);
             	     }
                     break;
                 case "6":
                     break outer;
                 default:
                     System.out.println("Option not available. Please choose again.");
            }
        }
        
        return State.Start;
    }
    
    private String getMediaId(String title) {
    	String mediaId = null;
    	String query = "SELECT mid as MID FROM Media WHERE title="+title;
        try {
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next()) {
				mediaId = String.valueOf(rs.getInt("MID"));
			}
        }
		catch (SQLException e) {
			CustomSQLException.printSQLException(e);
	    }    	
    	return mediaId;
    }
    
    private String getCopyId(String mediaId, RentalStatus r) {
    	String copyId = null;
    	
    	String query = "SELECT cid as CID FROM Copy WHERE mid="+mediaId;
    	
    	switch(r) {
    	case Nothing:
    		break;
    	case Available:
    		query += " AND rental_status=1";
    		break;
    	case OnLoan:
    		query += " AND rental_status=2";
    		break;
    	case OnHold:
    		query += " AND rental_status=3";
    		break;
    	default:
    		break;
    	}

    	List<String> cids = new ArrayList<String>();
    	try {
    		ResultSet rs = statement.executeQuery(query);
 			
 			while (rs.next()) {
 				cids.add(String.valueOf(rs.getInt("CID")));
 			}
        }
    	catch (SQLException e) {
			CustomSQLException.printSQLException(e);
	    }   
    	if(!cids.isEmpty()) {
        	copyId = cids.get(0);
    	}
    	return copyId;
    }
    
    private String getTitle(String MID) {
    	String title = "";
    	
    	String query = "SELECT title as TITLE FROM Media WHERE mid="+MID;
    	
    	List<String> titles = new ArrayList<String>();
    	try {
    		ResultSet rs = statement.executeQuery(query);
 			
 			while (rs.next()) {
 				titles.add(String.valueOf(rs.getString("TITLE")));
 			}
        }
    	catch (SQLException e) {
			CustomSQLException.printSQLException(e);
	    } 
    	if(!titles.isEmpty()) {
        	title = titles.get(0);
    	}
    	
    	return title;
    }
    
    private String getName(String ID) {
    	String name = "";
    	
    	String query = "SELECT name as NAME FROM Person WHERE id="+ID;
    	
    	List<String> names = new ArrayList<String>();
    	try {
    		ResultSet rs = statement.executeQuery(query);
 			
 			while (rs.next()) {
 				names.add(String.valueOf(rs.getString("NAME")));
 			}
        }
    	catch (SQLException e) {
			CustomSQLException.printSQLException(e);
	    } 
    	if(!names.isEmpty()) {
    		name = names.get(0);
    	}
    	return name;
    }
    
    private String getPatronId(String name) {
    	String id = null;
    	

    	String query = "SELECT id as ID FROM Person WHERE name="+name+" AND ID in Patron";
    	
    	List<String> ids = new ArrayList<String>();
    	try {
    		ResultSet rs = statement.executeQuery(query);
 			
 			while (rs.next()) {
 				ids.add(String.valueOf(rs.getString("ID")));
 			}
        }
    	catch (SQLException e) {
			CustomSQLException.printSQLException(e);
	    } 
    	if(!ids.isEmpty()) {
    		id = ids.get(0);
    	}
    	
    	return id;
    }
    
    private String getLibrarianId(String name) {
    	String id = null;
    	

    	String query = "SELECT id as ID FROM Person WHERE name="+name+" AND ID in Librarian";
    	
    	List<String> ids = new ArrayList<String>();
    	try {
    		ResultSet rs = statement.executeQuery(query);
 			
 			while (rs.next()) {
 				ids.add(String.valueOf(rs.getString("ID")));
 			}
        }
    	catch (SQLException e) {
			CustomSQLException.printSQLException(e);
	    } 
    	if(!ids.isEmpty()) {
    		id = ids.get(0);
    	}
    	
    	return id;
    }
}
