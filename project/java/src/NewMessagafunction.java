   public static void NewMessage(Messenger esql){
      try{ 
            
            //Warns user about creating a new chat, and asks to continue
            System.out.print("\n WARNING! This creates a new chat. do you want to continue: ");
            System.out.print("\n\t1. Create New Chat \n\t2. Exit ");
            System.out.println("\n\t......................... ");
            
            switch(readChoice()){ 
                case 1: break; 
                case 2: System.out.print("\n\t Exiting new message creator\n"); return;
                default: System.out.print("Unrecognized choice!"); break; 
            } 
            
            String Chat_type="private";
            List<String> Members= new ArrayList<String>(); 
            
            //Asks for chat type
            System.out.print("\n Chat type: "); 
            System.out.print("\n\t1. Private \n\t2. Group"); 
            System.out.println("\n\t......................... ");
            switch(readChoice()){ 
                case 1: 
                    Chat_type= "private";
                    System.out.print("\n\tEnter login of recipient: ");
                    String member= in.readLine();
                    Members.add(member);
                    break; 
                case 2: 
                    Chat_type= "group"; 
                    System.out.print("\t Enter group member:"); 
                    member= in.readLine(); 
                    Members.add(member);
                    boolean Continue=true;
                    
                    while(Continue=true){
                        System.out.print("\t 1. Enter another group member \n\t 2. Finish group list");
                        System.out.println("\n\t......................... ");
                        String option=in.readLine();
                        if(!option.equals("1")){
                            Continue=false;
                            break;}
                        else
                            System.out.print("\t Enter group member: "); 
                            member=in.readLine();
                            Members.add(member);
                    }
                    break; 
                default: System.out.print("Unrecognized choice!"); break; 
            } 
             
            //add to chat table 
            String query=String.format("INSERT INTO CHAT (chat_type, init_sender) VALUES ('%s','%s')",Chat_type,SUPER_USER); 
            esql.executeUpdate(query); 
          
            //get current seqeunce value for chat id and update
            int chat_id = esql.getCurrSeqVal("CHAT_chat_id_seq");
                        
            //checks valid recipient login 
            for(int index=0; index< Members.size(); index++){
                String member= Members.get(index);
                query = String.format("SELECT * FROM Usr WHERE login= '%s'", member); 
                int userNum=esql.executeQuery(query); 
                index++;
                if(userNum>0){ 
                //continue 
                }
                else{
                String error = String.format("\tCan't find selected user '%s', Chat and Message not sent.",member);
                System.out.println(error);
                return; 
                }
            }
            
            System.out.print("\tEnter message text: ");
            String message=in.readLine(); 
            
            //loops until message is correct length
            while(message.length() > 300)
            {
                message=""; //clear message body 
                System.out.print("\tError: Message is too long. Message body must be less than 300 characters"); 
                System.out.print("\n\tEnter message text: "); 
                message=in.readLine(); 
            }
            
            //add to message table
            query = String.format("INSERT INTO MESSAGE (msg_text, msg_timestamp, sender_login, chat_id) VALUES ('%s', 'now()','%s','%s')", message, SUPER_USER, chat_id);
            esql.executeUpdate(query);
            int mes_id = esql.getCurrSeqVal("MESSAGE_msg_id_seq"); 

           
            
            long d_date=0; 
            int media_id=0;
            boolean Continue = true; 
            while(Continue){
            System.out.print("\n\t1. Add media Attachments \n\t2. Add Self Destruct feature \n\t3. Complete and Send Message ");
            System.out.println("\n\t......................... ");
            
            switch(readChoice()){ 
                case 1: 
                    System.out.print("\t Enter attachment type: "); 
                    String Attach_type=in.readLine(); 
                    System.out.print("\t Enter attachment URL: "); 
                    String URL=in.readLine(); 
                    query= String.format("INSERT INTO MEDIA_ATTACHMENT(media_type, URL, msg_id) VALUES ('%s','%s', '%s')", Attach_type, URL, mes_id); 
                    esql.executeUpdate(query);
                    media_id = esql.getCurrSeqVal("MEDIA_ATTACHMENT_media_id_seq");
                    break; 
                case 2: 
                    System.out.print("\n\t Self Delete message in how many days: ");
                    String date=in.readLine();
                    query=String.format("UPDATE MESSAGE set destr_timestamp= now()+ interval('%s' days)", date);
                    esql.executeUpdate(query);
                case 3: 
                    Continue=false; 
                    break;
                default: System.out.print("Unrecognized choice!"); break; 
            }
        } 
        
			//add initial sender to member of chat list
			query=String.format("INSERT INTO CHAT_LIST(chat_id, member) VALUES ('%s', '%s')", chat_id, SUPER_USER);
			esql.executeUpdate(query); 
			
            //add members to chat list and notifications
            for(int index=0; index< Members.size(); index++){
                String member= Members.get(index);
                query=String.format("INSERT INTO CHAT_LIST(chat_id, member) VALUES ('%s', '%s')", chat_id, member); 
                esql.executeUpdate(query); 
                query=String.format("INSERT INTO NOTIFICATIONS(user_login, msg_id) VALUES ('%s', '%s')", member, mes_id);
                index++;
            }        
                    
            System.out.print("\n\t Message Succesfully sent!\n "); 
        }
        catch(Exception e){
            System.err.println (e.getMessage());
        }
   }//end
