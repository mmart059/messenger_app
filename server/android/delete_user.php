<?php
$response = array();

if( isset($_POST['login'])) {
    $login = $_POST['login'];

    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

    $result = msqli_query($db->connect(), "DELETE FROM usr WHERE login = $login");

    if(mysqli_affected_rows($result) > 0){
        $response["success"] = 1;
        $response["message"] = "Product successfully delete";

        echo json_encode($response);

    } else{
        $response["success"] = 0;
        $response["message"] = "No product found";

        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    echo json_encode($response);
}
?>
