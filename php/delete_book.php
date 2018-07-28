<?php 
require "db.php";

$bookid = $_POST["bookid"];

$sql="DELETE FROM booklist WHERE book_id = '$bookid';";


if ($conn->query($sql) === TRUE) {
    echo "bookDeletedSuccessfully";
} else {
    echo "Book Deletation Failed";
}
 ?>