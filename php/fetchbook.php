<?php 
include "db.php";

$genre = $_POST["genre"];  

$mysql_qry="select * from booklist where genre = '$genre' order by book_name ASC;";
$res=$conn->query($mysql_qry);

while ($data=$res->fetch_object()) {
	$flag[]=$data;

}
print(json_encode($flag));
?>
