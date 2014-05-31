<?php

/**
 * A class file to connect to database
 */

require_once __DIR__ . '/db_config.php';
$GLOBALS['con'] = mysqli_connect(DB_SERVER, DB_USER, 
				 DB_PASSWORD, DB_DATABASE) 
		  or die(mysql_error());
class DB_CONNECT {
    
    // constructor
    function __construct() {
    }

    // destructor
    function __destruct() {
        // closing db connection
        $this->close();
    }

    /**
     * Function to connect with database
     */
    function connect() {
        // import database connection variables

        // Selecing database
        $db = mysqli_select_db($GLOBALS['con'], DB_DATABASE) or die(mysqli_error()) or die(mysqli_error());

        // returing connection cursor
        return $GLOBALS['con'];
    }

    /**
     * Function to close db connection
     */
    function close() {
        // closing db connection
	$GLOBALS['con']->close();
    }

}

?>
