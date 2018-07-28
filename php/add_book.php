<?php 
include "db.php";
$upload_path='PdfUploadFolder/';
$server_ip=gethostbyname(gethostname());
$upload_url='https://iamtorab.com'.'/booklibrary/'.$upload_path;

$fileinfo=pathinfo($_FILES['pdf']['name']);
$extension=$fileinfo['extension'];

$book_name = $_POST["book_name"];  
$author_name = $_POST["author_name"];
$genre=$_POST["genre"];

//$newName=getFileName();
$newName=$book_name;

$file_url=$upload_url . $newName .'.'.$extension;

$file_path=$upload_path. $newName .'.'.$extension;


if (move_uploaded_file($_FILES['pdf']['tmp_name'],$file_path)) {
	
}

$sql = "insert into booklist values('','$book_name','$author_name','$file_url','$genre');"; 

if ($conn->query($sql) === TRUE) {
	echo "Book Added";
} else {
	echo "Failed";
}

function getFileName()
{
return str_replace(".","",uniqid('',true));
}
?>