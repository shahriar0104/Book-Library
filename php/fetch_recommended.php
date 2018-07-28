<?php 
require "db.php";
 
$mysql_qry="select * from request ;";
$res=$conn->query($mysql_qry);

while ($data=$res->fetch_object()) {
	$flag[]=$data;

}
print(json_encode($flag));
?>
