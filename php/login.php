<?php 

include "db.php";

$name_email = $_POST["name_email"];
$password = $_POST["password"];

$mysql_qry = "select * from user where password = '$password' and (name = '$name_email' or email = '$name_email');";
$res=$conn->query($mysql_qry);

if ($data=$res->fetch_object()) {
	//echo "".$data->username;
	echo "".$data->user_id.",".$data->name.",".$data->email.",".$data->password.",".$data->status.",".$data->gender;
}else{
	echo "failed";
}
?>