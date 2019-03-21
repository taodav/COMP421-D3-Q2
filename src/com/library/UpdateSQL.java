package com.library;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class UpdateSQL {
    private Statement statement;
    String regex = "[0-9]+";

    public UpdateSQL(Statement statement) {
        this.statement = statement;
    }
    
    public State returnLoan() {
    	Scanner scanner = new Scanner(System.in);
        outer: while (true) {
        	System.out.println("Please select one of the following options by entering it's corresponding number");
        	System.out.println("1. Return a Copy");
        	System.out.println("2. Go back\n");
            System.out.print("> ");
            switch(scanner.nextLine()) {
            case "1":
            	System.out.println("Enter either the mediaID or the title of the item you wish to return");
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
    		    List<String> tmpCIDs = getCopyIds(tmpMID, RentalStatus.OnLoan);
    		    if(tmpCIDs.isEmpty()) {
    		    	System.out.println("No copies on loan");
    		    	break;
    		    } 
    	    	System.out.println("There are "+tmpCIDs.size()+" copies of this item on hold");
    	    	System.out.print("Select one of: ");
    	    	for(String s: tmpCIDs) {
    	    		System.out.print(s+", ");
    	    	}
    	    	System.out.print("\n> ");
    	    	String selectedCID = scanner.nextLine();
    	    	if(!(tmpCIDs.contains(selectedCID))) {
    	    		System.out.println("The selected copy does not exist");
    	    		break;
    	    	}
    	    	
    	    	if(returnCopy(tmpMID, selectedCID)) {
    	    		System.out.println("Copy successfully returned");
    	    	} else {
    	    		System.out.println("Issue returning copy");
    	    	}
    	    	disactivateLoan(tmpMID, selectedCID);
            	break;
            case "2":
            	break outer;
            default:
                System.out.println("Option not available. Please choose again.");
                break;
            }
        }
    	return State.Start;
    }

    public State createLoan() {
    	 Scanner scanner = new Scanner(System.in);
    	 String MID = "NULL";
    	 String CID = "NULL";
    	 String PID = "NULL";
    	 String LID = "NULL";
    	 String DATE = "NULL";
         outer: while (true) {
             System.out.println("Please specify an available media item*, a patron*, a date*, and a librarian | *required");
             System.out.println("Currently selected:");
             System.out.println("    Title: "+getTitle(MID));
             System.out.println("    Patron name: "+getName(PID));
             System.out.println("    Loan End Date: "+DATE);
             System.out.println("    Librarian name: "+getName(LID));

             System.out.println("1. Specify Media");
             System.out.println("2. Specify User");
             System.out.println("3. Specify Loan date");
             System.out.println("4. Specify Librarian");
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
                	 System.out.println("Please enter the year like 1998");
                     System.out.print("> ");
                     String year = scanner.nextLine();
                     if(!year.matches("[0-9][0-9][0-9][0-9]")) {
                    	 System.out.println("Invalid year");
                    	 break;
                     }
                     System.out.println("Please enter the month like 02");
                     System.out.print("> ");
                     String month = scanner.nextLine();
                     if(!month.matches("[0-9][0-9]")) {
                    	 System.out.println("Invalid month");
                    	 break; 
                     }
                     System.out.println("Please enter the day like 23");
                     System.out.print("> ");
                     String day = scanner.nextLine();
                     if(!month.matches("[0-9][0-9]")) {
                    	 System.out.println("Invalid day");
                    	 break;
                     }
                     DATE = year+"-"+month+"-"+day;
                	 break;
                 case "4":
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
                	 
                	 String query = "INSERT INTO Loan VALUES("+MID+", "+CID+", "+PID+", "+LID+", '"+DATE+"', 0)";
                	 System.out.println(query);
                	 try {
             			ResultSet rs = statement.executeQuery(query);
                     }
             		 catch (SQLException e) {
             			 if(!(e.getErrorCode()==0))
             				 CustomSQLException.printSQLException(e);
             	     }
                     break;
                 case "6":
                     break outer;
                 default:
                     System.out.println("Option not available. Please choose again.");
                     break;
            }
        }
        
        return State.Start;
    }
    
    private boolean returnCopy(String mid, String cid) {
    	String query = "UPDATE Copy SET rental_status=1 WHERE mid='"+mid+"' AND cid='"+cid+"'";
    	
    	try {
			ResultSet rs = statement.executeQuery(query);
        }
		catch (SQLException e) {
			if(e.getErrorCode()!=0) {
				CustomSQLException.printSQLException(e);
				return false;
			}
	    }
    	
    	return true;
    }
    
	private void disactivateLoan(String mid, String cid) {
		String query = "UPDATE Loan SET active=false WHERE mid='"+mid+"' AND cid='"+cid+"'";
		
		try {
			ResultSet rs = statement.executeQuery(query);
        }
		catch (SQLException e) {
			if(e.getErrorCode()!=0) {
				CustomSQLException.printSQLException(e);
			}
	    }
	}
    
    private String getMediaId(String title) {
    	if(title==null || title=="NULL") return "";

    	String mediaId = null;
    	String query = "SELECT mid as MID FROM Media WHERE title='"+title+"'";
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
    	if(mediaId==null || mediaId=="NULL") return "";

    	String copyId = null;
    	
    	String query = "SELECT cid as CID FROM Copy WHERE mid='"+mediaId+"'";
    	
    	switch(r) {
    	case Nothing:
    		break;
    	case Available:
    		query += " AND rental_status=1";
    		break;
    	case OnHold:
    		query += " AND rental_status=2";
    		break;
    	case OnLoan:
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
    
    private List<String> getCopyIds(String mediaId, RentalStatus r) {
    	if(mediaId==null || mediaId=="NULL") return new ArrayList<String>();
    	
    	String query = "SELECT cid as CID FROM Copy WHERE mid='"+mediaId+"'";
    	
    	switch(r) {
    	case Nothing:
    		break;
    	case Available:
    		query += " AND rental_status=1";
    		break;
    	case OnHold:
    		query += " AND rental_status=2";
    		break;
    	case OnLoan:
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
    	return cids;
    }
    
    private String getTitle(String MID) {
    	if(MID==null || MID=="NULL") return "";
    	String title = "";
    	
    	String query = "SELECT title as TITLE FROM Media WHERE mid='"+MID+"'";
    	
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
    	if(ID==null || ID=="NULL") return "";

    	String name = "";
    	
    	String query = "SELECT name as NAME FROM Person WHERE id='"+ID+"'";
    	
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
    	if(name==null || name=="NULL") return "";

    	String id = null;
    	
    	String query = "SELECT id as ID FROM Person WHERE name='"+name+"' AND ID IN (SELECT pid FROM Patron)";

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
    	if(name==null || name=="NULL") return "";

    	String id = null;
    	

    	String query = "SELECT id as ID FROM Person WHERE name='"+name+"' AND ID in Librarian";
    	
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
