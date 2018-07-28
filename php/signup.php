<?php 
include 'db.php';

$user_name = $_POST["user_name"];  
$email = $_POST["email"];
$pass=$_POST["password"];
$gender=$_POST["gender"];

$mysql_qry = "select * from user where name = '$user_name';";
$res=$conn->query($mysql_qry);

if ($data=$res->fetch_object()) {
	echo "user_exists";
}else{

	$email_qry = "select * from user where email = '$email';";
	$res=$conn->query($email_qry);

	if ($data=$res->fetch_object()) {
	echo "user_mail_exists";
	}else{
		$sql = "insert into user values('','$user_name','$email','$pass','2','$gender');"; 
	if ($conn->query($sql) === TRUE) {
		echo "account_successfully_created";
	} else {
		echo "account_creation_failed".$conn->connect_error;
	}
	}
}
?>