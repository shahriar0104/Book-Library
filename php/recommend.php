<?php 
include 'db.php';


$book_name = $_POST["book_name"];  
$author_name = $_POST["author_name"];  

$sql = "insert into request values('','$book_name','$author_name');"; 

if ($conn->query($sql) === TRUE) {
	echo "Book Recommended";
} else {
	echo "failed". $conn->error;
}

?>