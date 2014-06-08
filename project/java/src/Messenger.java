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
import java.util.Set;
import java.util.LinkedHashSet;
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
* Method to execute an update SQL statement. Update SQL instructions
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
* Method to execute an input query SQL instruction (i.e. SELECT). This
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
** obtains the metadata object for the returned result set. The metadata
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
* Method to execute an input query SQL instruction (i.e. SELECT). This
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
** obtains the metadata object for the returned result set. The metadata
** contains row and column info.
*/
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;
 
      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result = new ArrayList<List<String>>();
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
* Method to execute an input query SQL instruction (i.e. SELECT). This
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
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (SUPER_USER != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("\nMAIN MENU");
                System.out.println("---------");
                System.out.println("Notifications");
                System.out.println("\t1. View Notifications");
                System.out.println("\n.........................\n");
                System.out.println("Messages");
                System.out.println("\t2. Write a new message");
                System.out.println("\t3. Reply to Messages");
                System.out.println("\n.........................\n");
                System.out.println("Contact List");
                System.out.println("\t4. Edit/View Contact List");
                System.out.println("\t5. Edit/View Blocked List");
                System.out.println("\n.........................\n");
                System.out.println("User Options");
                System.out.println("\t6. View Status");
                System.out.println("\t7. Update Status");
                System.out.println("\t8. Delete user account");
                System.out.println("\t9. Log out\n");
                switch (readChoice()){
                   case 1: ReadNotifications(esql); break;
                   case 2: NewMessage(esql); break;
                   case 3: ReplyToMessages(esql); break;
                   case 4: EditContactList(esql); break;
                   case 5: EditBlockedList(esql); break;
                   case 6: ViewStatus(esql); break;
                   case 7: UpdateStatus(esql); break;
                   case 8: DeleteUser(esql);
                            usermenu=false;
                            break;
                   case 9: usermenu = false; SUPER_USER = null; break;
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
       try{

        ViewChat(esql);
        boolean chat_menu = true;
        while(chat_menu){
            System.out.println("\tChat Menu");
            System.out.println("\t---------");
            System.out.println("\t1. Select Chat");
            System.out.println("\t2. View Chats");
            System.out.println("\t.........................");
            System.out.println("\t9. Back to Main Menu");
        switch(readChoice()){
            case 1: SelectChat(esql); break;
            case 2: ViewChat(esql); break;
            case 9: chat_menu = false; break;
        default : System.out.println("Unrecognized choice!"); break;
            }
        }
        }
        catch(Exception e){
        System.err.println(e.getMessage ());
    }} 

//after viewing chats it allows the user to respond to the chat
     public static void ReplytoChat(Messenger esql, String chat_id){
         try{
            String query=String.format("SELECT member FROM CHAT_LIST where chat_id='%s'",chat_id);
            List<List<String>>Members=esql.executeQueryAndReturnResult(query);
            List<String>members= new ArrayList<String>();
            for(int i=0; i <Members.size(); i++)
            { 
                members.add(Members.get(i).get(0));
            }
            int id=Integer.parseInt(chat_id);
            enterMessage(esql,members,id);
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
     }
     
    public static void ViewChat(Messenger esql){
        try{ 
            System.out.println("Current Chats"); 
            System.out.println("\n.........................\n");
            String query = String.format("SELECT A.chat_id, B.member FROM chat A, chat_list B WHERE A.chat_id = B.chat_id and B.member = '%s'", SUPER_USER);
            List<List<String>> Chatid_list = esql.executeQueryAndReturnResult(query);
            for(int i = 0; i < Chatid_list.size(); i++){
                    String temp = String.format("SELECT member, chat_id FROM chat_list WHERE chat_id = '%s'", Chatid_list.get(i).get(0));
                    esql.executeQueryAndPrintResult(temp);
                }
            if(Chatid_list.size()<1)
            System.out.println("You don't have any chats");
            }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
    public static void ViewMessages(Messenger esql, String chat_id){
        try{
            //output message also serves as a test to see if chat id exists for the user
            String query = String.format("SELECT DISTINCT msg_timestamp, sender_login, M.msg_text, msg_id FROM message M, chat_list C WHERE M.chat_id = '%s' AND C.member = '%s'", chat_id, SUPER_USER);
            int user_check=esql.executeQuery(query);
            if(user_check>0){ 
                List<List<String>> Message=esql.executeQueryAndReturnResult(query);
                for(int i=Message.size()-1; i>=0; i--)
                {
                    System.out.print(Message.get(i).get(0));
                    System.out.print("\t");
                    System.out.print(Message.get(i).get(1));
                    System.out.print("\t");
                    System.out.print(Message.get(i).get(2)); 
                    System.out.println("\t");
                    String msg_id=Message.get(i).get(3);
                    query=String.format("SELECT media_type, url FROM media_attachment WHERE msg_id='%s'", msg_id); 
                    int num=esql.executeQuery(query);
                    if(num>0)
                    { 
                        System.out.print("\tThis message has attachment: "); 
                        esql.executeQueryAndPrintResult(query); 
                    } 
                    
                    if(i%10==0 && (i!=0)){
                        System.out.print("\tEnter 1: To load more messages, Enter anything to continue");
                        String choice=in.readLine(); 
                        if(!choice.equals("1")){
                            break;
                        }
                    }
                }

            }
            else{
                System.out.println("You are not a member of that chat."); 
                return; 
            }
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
        }
            
    public static void SelectChat(Messenger esql){
        try{ 
            System.out.print("Enter Chat ID: ");
            String chat_selected = in.readLine();
            
            ViewMessages(esql, chat_selected);
            boolean chatview = true;
            while(chatview){
                System.out.println("\t1. Add Member");
                System.out.println("\t2. Delete Member");
                System.out.println("\t3. Reply to Chat");
                System.out.println("\t4. Delete Whole Chat");
                System.out.println("\t.........................");
                System.out.println("\t9. Exit Chat Menu");
                switch(readChoice()){
                    case 1:AddMember(esql, chat_selected); break;
                    case 2:DeleteMember(esql, chat_selected); break;
                    case 3:ReplytoChat(esql, chat_selected); break;
                    case 4:DeleteWholeChat(esql, chat_selected); 
                        chatview=false;
                        break;
                    case 9:chatview = false; break;
                    default: System.out.println("Unrecognized choice!"); break;
                }
            }
        }
        catch(Exception e){
            System.err.println(e.getMessage()); 
        }
    }
    
    public static void AddMember(Messenger esql, String chat_id){
        try{
            String query=String.format("SELECT * FROM CHAT WHERE init_sender='%s' AND chat_id='%s'",SUPER_USER, chat_id); 
            int check=esql.executeQuery(query); 
            if(check>0){
                System.out.print("\tEnter user you want to add: ");
                String user_selected = in.readLine();
                query = String.format("INSERT INTO chat_list(chat_id, member) VALUES ('%s', '%s')", chat_id, user_selected);
                esql.executeUpdate(query);
                System.out.print("\tYou have successfully added them to your chat\n"); 
            }
            else{
                System.out.println("\tYou are not the creator of that chat, you cannot add members");
                return;  
            }
       }catch (Exception e) {
           System.out.println(e.getMessage());
       }
   }

    public static void DeleteMember(Messenger esql, String chat_id){
    try{
            String query=String.format("SELECT * FROM CHAT WHERE init_sender='%s' AND chat_id='%s'",SUPER_USER, chat_id); 
            int check=esql.executeQuery(query); 
            if(check>0){
                System.out.print("\tEnter user you want to remove: ");
                String user_selected = in.readLine();
                query=String.format("SELECT * FROM CHAT_LIST WHERE chat_id='%s' AND member='%s'",chat_id, user_selected);
                check=esql.executeQuery(query); 
                if(check<0){
                    System.out.println("User does not exist in chat"); 
                    return;
                }
                query = String.format("DELETE FROM CHAT_LIST WHERE chat_id='%s' and member='%s'", chat_id, user_selected);
                esql.executeUpdate(query);
                System.out.print("\tYou have successfully removed them from your chat\n"); 
            }
            else{
                System.out.println("\tYou are not the creator of that chat, you cannot remove members\n");
                return;  
            }
       }catch (Exception e) {
           System.out.println(e.getMessage());
       }
   }
   
   //deletes a whole chat if user is intial sender
    public static void DeleteWholeChat(Messenger esql, String chat_id){
        try{ 
            String query=String.format("SELECT * FROM CHAT WHERE init_sender='%s' AND chat_id='%s'",SUPER_USER, chat_id); 
            int check=esql.executeQuery(query); 
            if (check<0){
                System.out.println("\tYou did not create this chat, you can not delete it"); 
            }
            
            else{
                query = String.format("DELETE FROM chat_list WHERE member = '%s' AND chat_id = '%s'", SUPER_USER, chat_id);
                esql.executeUpdate(query);
                System.out.print("\tYou have successfully deleted the chat\n"); 
            }
        }
        catch (Exception e){ 
            System.err.println(e.getMessage()); 
            }
        }//end Delete Chat

      

   //Shows sub menu to add/delete contacts and view all contacts
   public static void EditContactList(Messenger esql){
        boolean contact_menu = true;

        while(contact_menu){
            System.out.println("\n.........................\n");
            System.out.println("Contact List");
            System.out.println("\t1. Add Contacts");
            System.out.println("\t2. Delete Contacts");
            System.out.println("\t3. Browse Your Contact List");
            System.out.println("\t9. Back to Main Menu");
            System.out.println("\n.........................\n");

            switch(readChoice()){
                case 1: AddToContact(esql); break;
                case 2: DeleteFromContacts(esql); break;
                case 3: ListContacts(esql); break;
                case 9: contact_menu = false; break;
                default: System.out.println("Unrecognized choice!"); break;
            }
        }
        return;
   }

   //Shows sub menu to add/delete blocked users as well as viewing
   public static void EditBlockedList(Messenger esql){
        boolean blocked_menu = true;

        while(blocked_menu){
            System.out.println("\n.........................\n");
            System.out.println("Blocked Contact List");
            System.out.println("\t1. Add Contacts to Blocked List");
            System.out.println("\t2. Delete Contacts from Blocked List");
            System.out.println("\t3. Browse Blocked Contact List");
            System.out.println("\t9. Back to Main Menu");
            System.out.println("\n.........................");
            
            switch(readChoice()){
                case 1: AddBlockedContact(esql); break;
                case 2: DeleteBlockedContact(esql); break;
                case 3: ListBlockedContacts(esql); break;
                case 9: blocked_menu = false; break;
                default: System.out.println("Unrecognized choice!"); break;
            }
        }
        return;
            
   }
   
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         " User Interface \n" +
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
         System.out.println("\nInvalid login and/or password!\n");
         SUPER_USER = null;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         SUPER_USER = null;
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
            System.out.print("\tEnter login to delete: ");
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
         
         List<List<String> > result = esql.executeQueryAndReturnResult(query);
         if(result.size() > 0){
            query = String.format("SELECT A.list_member, B.status FROM USER_LIST_CONTAINS A, Usr B WHERE A.list_id= '%s' AND B.login=A.list_member",
                                   Integer.parseInt(result.get(0).get(0)));
            
            result = esql.executeQueryAndReturnResult(query);
            
            System.out.println("\nYour Contacts\n");
            for( int i = 0; i < result.size(); i++){
                query = String.format("SELECT status FROM Usr WHERE login='%s'", result.get(i).get(0));
                System.out.print("\t"+ result.get(i).get(0));
                if(result.get(i).get(1) != null){
                    System.out.println("\t" + result.get(i).get(1));
                } else{
                    System.out.println("\t" + "None");
                }
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
         
         List<List<String> > result = esql.executeQueryAndReturnResult(query);
         if(result.size() > 0){
            query = String.format("SELECT list_member FROM USER_LIST_CONTAINS WHERE list_id= '%s'",
                                   Integer.parseInt(result.get(0).get(0)));
            
            result = esql.executeQueryAndReturnResult(query);
            if(result.size() > 0){
                System.out.println("\nYour Blocked Contacts\n");
            
                System.out.println("\nYour Blocked Contacts\n");
                for( int i = 0; i < result.size(); i++){
                    query = String.format("SELECT status FROM Usr WHERE login='%s'", result.get(i).get(0));
                    System.out.println("\t"+ result.get(i).get(0) + "\n");
                }
            } else{
                System.out.println("\nYou have no blocked contacts\n");
            }
         } else{
             System.out.println("Oops! An error occurred.");
         }
      }catch(Exception e){
        System.err.println(e.getMessage());
      }

   }

   public static void DeleteBlockedContact(Messenger esql){
        
         try{
            System.out.print("\tEnter login to remove: ");
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
            System.out.print("\tEnter user to block: ");
            String input = in.readLine();


            String query = String.format("SELECT login FROM Usr WHERE login='%s'", input);
            int num = esql.executeQuery(query);

            //making sure the user you are attempting to add exists
            if(num > 0){
                query = String.format("SELECT block_list FROM Usr WHERE login = '%s'", SUPER_USER);
                List<List<String> > result = esql.executeQueryAndReturnResult(query);
                
                query = String.format("INSERT INTO USER_LIST_CONTAINS(list_id, list_member) VALUES('%s','%s')",
                                       Integer.parseInt(result.get(0).get(0)),input);
                esql.executeUpdate(query);

                System.out.println("\nSuccessfully added " + input + " into your blocked contacts");

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
      try{
            
            //Warns user about creating a new chat, and asks to continue
            System.out.println("\n WARNING! This creates a new chat. do you want to continue: ");
            System.out.println("\t1. Create New Chat");
            System.out.println("\t2. Exit");
            System.out.println("\t......................... ");
            boolean message_menu = true;
            while(message_menu){
                switch(readChoice()){
                    case 1: CreateNewChat(esql); message_menu = false;break;
                    case 2: message_menu = false; break;
                    default: System.out.println("Uncrecognized choice!"); break;
                }
            }
      } catch(Exception e){
        System.err.println(e.getMessage());
      }
   }
    
   public static void CreateNewChat(Messenger esql){
    try{
        
        //Asks for chat type
        boolean chat_type = true;
        while(chat_type){
            System.out.println("\nChat types");
            System.out.println("\t1. Private");
            System.out.println("\t2. Group");
            System.out.println("\t3. Exit");
            System.out.println("\t......................... ");
       
            //making choices with chat type
            switch(readChoice()){
                case 1:
                    boolean result = privateChat(esql);
                    if(result){
                        System.out.println("SUCCESS");
                        return;
                    }else{

                        break;
                    }
                case 2: groupChat(esql); return;
                case 3: chat_type = false; break;
                default: System.out.println("Unrecognized choice!");
            }
        }
        return;
    } catch(Exception e){
        System.err.println(e.getMessage());
    }
   }

   public static boolean privateChat(Messenger esql){
        try{
            String query = null;
            
            while(true){
                System.out.print("\tEnter login of recipient(leave blank to quit): ");
                String member = in.readLine();
                if(member.length() > 0 ){
                    
                    query = String.format("SELECT * FROM Usr WHERE login='%s'", member);
                    int number = esql.executeQuery(query);

                    if(number > 0){
                        //Creating the CHAT
                        List<String> Members = new ArrayList<String>();
                        Members.add(SUPER_USER);
                        Members.add(member);
                        query = String.format("INSERT INTO CHAT(chat_type,init_sender) VALUES('private','%s')",
                                              SUPER_USER);
                        esql.executeUpdate(query);

                        int chat_id = esql.getCurrSeqVal("CHAT_chat_id_seq");
                        
                        //Creating the message to send to the specified user
                        enterMessage(esql,Members, chat_id);
                        return true;
                    } else{
                        System.out.println("User does not exist!");
                    }
                
                } else{
                    return false;
                }
            }

        } catch(Exception e){
            System.err.println(e.getMessage());
        }
        return false;
   }//end
   
   public static void groupChat(Messenger esql){
    try{
        String query = null;
        Set<String> Members = new LinkedHashSet<String>();
        List<String> all = new ArrayList<String>();
        System.out.print("\tEnter login of recipient: ");
        String member = in.readLine();
        
        boolean cont = true;
        while(cont){
            query = String.format("SELECT * FROM Usr WHERE login='%s'", member);
            Members.add(SUPER_USER);
          
         int res = esql.executeQuery(query);
         if(res > 0){
            Members.add(member);

         } else{
            System.out.println("User specified does not exist");

         }
         System.out.println("\t1. Enter another group member");
         System.out.println("\t2. Finish group list");
         switch(readChoice()){
            case 1:
                System.out.print("\tEnter login of recipient: ");
                member = in.readLine();
                break;
            case 2:
                cont = false;
                query = String.format("INSERT INTO CHAT(chat_type, init_sender) VALUES('group', '%s')",
                                      SUPER_USER);
                esql.executeUpdate(query);
                int chat_id = esql.getCurrSeqVal("chat_chat_id_seq");
                all.addAll(Members);
                enterMessage(esql, all, chat_id);
                break;
            default: System.out.println("Unrecognized choice!"); break;
         }
        }
    } catch(Exception e){
        System.err.println(e.getMessage());
    }
   }
   
   public static void enterMessage(Messenger esql, List<String> Members, int chat){
        try{
        System.out.print("\tEnter message text: ");
        String message=in.readLine();
        int chat_id = chat;
        //loops until message is correct length
        while(message.length() > 300)
        {
             
            message=""; //clear message body
            System.out.print("\tError: Message is too long. Message body must be less than 300 characters");
            System.out.print("\n\tEnter message text: ");
            message=in.readLine();
        }
         
        String query = String.format("INSERT INTO MESSAGE (msg_text,msg_timestamp, sender_login, chat_id) VALUES ('%s', 'now()', '%s', '%s')",
                                      message, SUPER_USER, chat_id);
        esql.executeUpdate(query);
        
        int mes_id = esql.getCurrSeqVal("message_msg_id_seq");
        long d_date = 0;
        int media_id = 0;

        boolean cont = true;

        while(cont){
            System.out.println("\t1. Add media Attachments");
            System.out.println("\t2. Add Self Destruct feature");
            System.out.println("\t3. Complete and Send Message");
            System.out.println("\t...............................");

            switch(readChoice()){
                case 1:
                    System.out.print("\tEnter attachment type: ");
                    String attach = in.readLine();

                    System.out.print("\tEnter attachment URL: ");
                    String URL = in.readLine();
                    query = String.format("INSERT INTO MEDIA_ATTACHMENT(media_type,URL, msg_id) VALUES('%s', '%s', '%s')",
                                          attach, URL, mes_id);

                    esql.executeUpdate(query);
                    media_id = esql.getCurrSeqVal("media_attachment_media_id_seq");
                    break;
                case 2:
                    System.out.println("\tSelf Delete message in how many days: ");
                    String date = in.readLine();
                    query = String.format("UPDATE MESSAGE set destr_timestamp = now() + interval('%s' days)",
                                           date);
                    esql.executeUpdate(query);
                    break;
                case 3:
                    cont = false;
                    break;
                default: System.out.print("Unrecognized choice!"); break;
            }
        }
        
        enterIntoChatList(esql,Members,chat_id);


        for(int i = 1; i < Members.size(); i++){
            String temp = Members.get(i);

            query = String.format("INSERT INTO NOTIFICATION(usr_login, msg_id) VALUES ('%s', '%s')",
                                   temp, mes_id);
            esql.executeUpdate(query);

        }
     } catch(Exception e){
        System.err.println(e.getMessage());
     }
   }
   
     public static void enterIntoChatList(Messenger esql, List<String> Members, int chat_id){
        try{
            for(int i = 0; i < Members.size(); i++){
                String query = String.format("SELECT * FROM CHAT_LIST WHERE chat_id = '%s' AND member='%s'",
                                              chat_id, Members.get(i));
                int result = esql.executeQuery(query);

                if(result > 0){
                    continue;
                } else{
                    query = String.format("INSERT INTO CHAT_LIST(chat_id, member) VALUES('%s','%s')",
                                           chat_id, Members.get(i));
                    esql.executeUpdate(query);
                }
            }
        } catch(Exception e){
            System.err.println(e.getMessage());
        }
     }

     public static void ReadNotifications(Messenger esql){
      // Your code goes here.
      try{
          String query = String.format("SELECT M.msg_id, M.msg_timestamp, M.sender_login, M.msg_text FROM notification N, Message M WHERE N.usr_login = '%s' AND N.msg_id = M.msg_id", SUPER_USER);
          esql.executeQueryAndPrintResult(query);
          
          query = String.format("SELECT N.msg_id FROM notification N, message M WHERE N.msg_id = M.msg_id AND N.usr_login = '%s'", SUPER_USER);
          List<List<String>> noti_rm = esql.executeQueryAndReturnResult(query);
          

         // System.out.println(noti_rm.size());
          for(int i = 0; i < noti_rm.size(); i++){
              System.out.println(noti_rm.get(i).get(0));
              String temp = String.format("DELETE FROM notification WHERE usr_login = '%s' AND msg_id = '%s'", SUPER_USER, noti_rm.get(i).get(0));
              esql.executeUpdate(temp);
          }
          return;
      }catch (Exception e){
          System.err.println(e.getMessage());
          return;
      }
   }//end

   //Just output the status of the user
   public static void ViewStatus(Messenger esql){
    try{
        String query = String.format("SELECT status FROM Usr WHERE login='%s'",
                                      SUPER_USER);
        List<List<String> > result = esql.executeQueryAndReturnResult(query);
        
        if(result.get(0).get(0) != null){
            System.out.println("\tUser Current Status: " + result.get(0).get(0));
        } else{
            System.out.println("\tNo recent status");
        }
    }catch(Exception e){
        System.err.println(e.getMessage());
    }
   }
   
   //Just update the status
   public static void UpdateStatus(Messenger esql){
        try{
            System.out.print("\tEnter new status: ");
            String status = in.readLine();
            String query = String.format("UPDATE USR SET status='%s' WHERE login='%s'",
                                         status,SUPER_USER);
            esql.executeUpdate(query);
            
            System.out.println("\nYour status has been successfully updated!");
             
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
   }

   //Delete the user if is not an initial sender of any chat
	public static void DeleteUser(Messenger esql){
    try{
        String query= String.format("SELECT * FROM CHAT WHERE init_sender='%s'", SUPER_USER);
        int num=esql.executeQuery(query);
        if (num>0){
        System.out.print("\tYou are an initial sender of a chat. You cannot delete your account. ");
        System.out.print("\n\tPlease go back and delete the chats you have created if you want to delete your account\n");
        return;
    }
    else{
        query=String.format("DELETE FROM CHAT_LIST WHERE member='%s'",SUPER_USER);
        esql.executeUpdate(query);
        query=String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_member='%s'", SUPER_USER);
        esql.executeUpdate(query);
        query=String.format("DELETE FROM USR WHERE login='%s'", SUPER_USER);
        esql.executeUpdate(query);
        query=String.format("DELETE FROM MESSAGE WHERE sender_login='%s'", SUPER_USER);
        System.out.print("Successful deletion: GOOD BYE! \n");
        SUPER_USER=null;
        return;
    }
    }
    catch(Exception e){
        System.err.println (e.getMessage ());
        return;
    }
   }



}//end Messenger


