<?php 
require "db.php";

$book_id = $_POST["book_id"];  

$mysql_qry="select * from review where book_id = '$book_id';";
$res=$conn->query($mysql_qry);

while ($data=$res->fetch_object()) {
	$flag[]=$data;

}
print(json_encode($flag));
?>
