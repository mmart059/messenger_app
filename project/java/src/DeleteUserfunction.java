   */The main user interface needs to be updated with this for the case statement. (This is so it will pop out of the main menu
          case 8: 
					DeleteUser(esql); 
					usermenu=false;
					break;
						
		/*
		
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
