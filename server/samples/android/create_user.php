<?php
   $response = array();

   if(isset($_POST['login']) && isset($_POST['password']) && 
      isset($_POST['phoneNum'])){
        $login = $_POST['login'];
        $password = $_POST['password'];
        $phoneNum = $_POST['phoneNum'];

        require_once __DIR__ . '/db_connect.php';

        $db = new DB_CONNECT();
        $result = mysqli_query($db->connect(), 
                               "INSERT INTO usr('$login','$password',
                                                '$phoneNum')");

        if($result){
            $response["success"] = 1;
            $response["message"] = "User successfully created.";

            echo json_encode($response);
        } else{
            $response["success"] = 0;
            $response["message"] = "Oops! an error occurred.";

            echo json_encdoe($response);
        }
   } else{
        $response["success"] = 0;
        $response["message"] = "Required field(s) is missing";
        
        echo json_encode($response);
   }


?>
