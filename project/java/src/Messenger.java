/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Messenger {

   // reference to physical database connection.
   private Connection _connection = null;
   private static String SUPER_USER = null;
   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Messenger
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Messenger (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Messenger

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
      // creates a statement object 
      Statement stmt = this._connection.createStatement (); 
 
      // issues the query instruction 
      ResultSet rs = stmt.executeQuery (query); 
 
      /* 
       ** obtains the metadata object for the returned result set.  The metadata 
       ** contains row and column info. 
       */ 
      ResultSetMetaData rsmd = rs.getMetaData (); 
      int numCol = rsmd.getColumnCount (); 
      int rowCount = 0; 
 
      // iterates through the result set and saves the data returned by the query. 
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>(); 
      while (rs.next()){
          List<String> record = new ArrayList<String>(); 
         for (int i=1; i<=numCol; ++i) 
            record.add(rs.getString (i)); 
         result.add(record); 
      }//end while 
      stmt.close (); 
      return result; 
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current 
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();
	
	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Messenger.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
      Greeting();
      Messenger esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Messenger object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Messenger (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("Notifications");
                System.out.println("1. View Notifications");
                System.out.println(".........................");
                System.out.println("Messages");
                System.out.println("2. Write a new message");
                System.out.println("3. Reply to Messages");
                System.out.println(".........................");
                System.out.println("Contact List");
                System.out.println("4. Edit/View Contact List");
                System.out.println("5. Edit/View Blocked List");
                System.out.println(".........................");
                System.out.println("6. View Status");
                System.out.println("7. Update Status");
                System.out.println("8. Delete user account");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: ReadNotifications(esql); break;
                   case 2: NewMessage(esql); break;
                   case 3: ReplyToMessages(esql); break;
                   case 4: EditContactList(esql); break;
                   case 5: EditBlockedList(esql); break;
                   case 6: ViewStatus(esql); break;
                   case 7: UpdateStatus(esql); break;
                   case 8: DeleteUser(esql); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   //Going to show all the chats and then being able to select chat to reply 
   public static void ReplyToMessages(Messenger esql){
       
   }

   //Shows sub menu to add/delete contacts and view all contacts
   public static void EditContactList(Messenger esql){

   }

   //Shows sub menu to add/delete blocked users as well as viewing
   public static void EditBlockedList(Messenger esql){

   }
   
   //Just output the status of the user
   public static void ViewStatus(Messenger esql){

   }
   
   //Just update the status 
   public static void UpdateStatus(Messenger esql){

   }

   //Delete the user if is not an initial sender of any chat
   public static void DeleteUser(Messenger esql){

   }

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();

	 //Creating empty contact\block lists for a user
	    esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
	    int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
        esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
	    int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");
         
	    String query = 
        String.format("INSERT INTO USR (phoneNum, login, password, block_list, contact_list) VALUES ('%s','%s','%s',%s,%s)", 
        phone, login, password, block_id, contact_id);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end
   
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	      if (userNum > 0){
            SUPER_USER = login;
            return login;
         }
         
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

   public static void AddToContact(Messenger esql){
      // Your code goes here.
      // ...
      // ...
      try{
        System.out.print("\tEnter user to add: ");
        String user = in.readLine();
        
        //Checking to see if the user you want to add exists 
        String query = String.format("SELECT * FROM Usr WHERE login = '%s'",user);
        int userNum = esql.executeQuery(query);

        if(userNum > 0){
            //Selecting the contact_list from the SUPER_USER
            query = String.format("SELECT contact_list FROM USR WHERE login = '%s'",SUPER_USER);

            List<List<String> > result = esql.executeQueryAndReturnResult(query);
           
            //Doing Insertion into the query
            query = String.format("INSERT INTO USER_LIST_CONTAINS(list_id,list_member) VALUES('%s','%s')",
                                Integer.parseInt(result.get(0).get(0)), user);
            esql.executeUpdate(query);

            String success = String.format("Successfully adding '%s'", user);
            
            System.out.println(success);
            return;
        }
        else{
            System.out.println("Can't find selected user " + user);
        }
      }catch(Exception e){
        System.err.println(e.getMessage());
      }
   }//end

   //functon to delete contact from contacts list
   public static void DeleteFromContacts(Messenger esql){
        try{
            System.out.print("\tWho do you want to remove from contacts?");
            String user = in.readLine();

            String query = String.format("SELECT contact_list FROM Usr WHERE login='%s'", SUPER_USER);
            List<List<String> > result = esql.executeQueryAndReturnResult(query);
            query = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id='%s' AND list_member= '%s'", 
                                   Integer.parseInt(result.get(0).get(0)), user);
            esql.executeUpdate(query);

            System.out.println("Successfully deleted " + user + " from contact list"); 

        }catch(Exception e){
           System.err.println(e.getMessage());
        }
   }

   public static void ListContacts(Messenger esql){
      // Your code goes here.
      // ...
      // ...
      try{
         String query = String.format("SELECT contact_list FROM Usr WHERE login = '%s'", SUPER_USER);
         System.out.println("Received contact list from query");         
         
         List<List<String> > result = esql.executeQueryAndReturnResult(query);
         if(result.size() > 0){
            query = String.format("SELECT list_member FROM USER_LIST_CONTAINS WHERE list_id= '%s'", 
                                   Integer.parseInt(result.get(0).get(0)));   
            
            result = esql.executeQueryAndReturnResult(query);
            
            System.out.println("\tContact List");
            System.out.println("\t............");
            for( int i = 0; i < result.size(); i++){
               System.out.println("\t" + result.get(i).get(0));
            }
            
         } else{
            System.out.println("You don't have any contacts.");
         }    
      }catch(Exception e){
        System.err.println(e.getMessage());
      }
   }//end
   
   public static void ListBlockedContacts(Messenger esql){
      try{
         String query = String.format("SELECT block_list FROM Usr WHERE login = '%s'", SUPER_USER);
         System.out.println("Received contact list from query");         
         
         List<List<String> > result = esql.executeQueryAndReturnResult(query);
         if(result.size() > 0){
            query = String.format("SELECT list_member FROM USER_LIST_CONTAINS WHERE list_id= '%s'", 
                                   Integer.parseInt(result.get(0).get(0)));   
            
            result = esql.executeQueryAndReturnResult(query);
            
            System.out.println("\tBlocked Contact List");
            System.out.println("\t............");
            for( int i = 0; i < result.size(); i++){
               System.out.println("\t" + result.get(i).get(0));
            }
            
         } else{
            System.out.println("You don't have any blocked contacts.");
         }    
      }catch(Exception e){
        System.err.println(e.getMessage());
      }
   }

   public static void DeleteBlockedContact(Messenger esql){
        
         try{
            System.out.print("\tWho do you want to remove from blocked list?");
            String input = in.readLine();

            String query = String.format("SELECT block_list FROM Usr WHERE login = '%s'", SUPER_USER);
            List<List<String> > result = esql.executeQueryAndReturnResult(query);

            query = String.format("SELECT list_member FROM USER_LIST_CONTAINS WHERE list_id='%s' AND list_member='%s'",
                                   Integer.parseInt(result.get(0).get(0)),input);
            int num = esql.executeQuery(query);
            
            //blocked member exists so time to delete him
            if(num > 0){
                query = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_member='%s'",input);
                esql.executeUpdate(query);

                System.out.println("Succesfully removed user from blocked list.");
            } else{
                System.out.println("You can't remove a user that is not in your blocked list.");
            }

        }catch(Exception e){
            System.err.println(e.getMessage());
        }
   }

   public static void AddBlockedContact(Messenger esql){
          try{
            System.out.print("Enter user to add to blocked contacts: ");
            String input = in.readLine();


            String query = String.format("SELECT login FROM Usr WHERE login='%s'", input);
            int num = esql.executeQuery(query);

            //making sure the user you are attempting to add exists
            if(num > 0){
                query = String.format("SELECT block_list FROM Usr WHERE login = '%s'", SUPER_USER);
                List<List<String> > result = esql.executeQueryAndReturnResult(query);
                
                query = String.format("INSERT INTO USR_LIST_CONTAINS(list_id, list_member) VALUES('%s','%s')",
                                       Integer.parseInt(result.get(0).get(0)),input);
                esql.executeUpdate(query);

                System.out.println("Successfully added " + input + " into your blocked contacts");

            } else {
                System.out.println("The user you are attempting to block does not exist");
            }
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
   }
   public static void NewMessage(Messenger esql){
      // Your code goes here.
      // ...
      // ...
   }//end 

   public static void ReadNotifications(Messenger esql){
      // Your code goes here.
      // ...
      // ...
   }//end

   public static void Query6(Messenger esql){
      // Your code goes here.
      // ...
      // ...
   }//end Query6

}//end Messenger
