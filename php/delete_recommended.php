<?php 
require "db.php";

$bookid = $_POST["bookid"];

$sql="DELETE FROM request WHERE ser_no = '$bookid';";


if ($conn->query($sql) === TRUE) {
    echo "delete";
} else {
    echo "Book Deletation Failed";
}
 ?>