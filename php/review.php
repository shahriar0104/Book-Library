<?php 

include 'db.php';

$book_id=$_POST["book_id"];
$reviewer_name=$_POST["reviewer_name"];
$reviewer_gender=$_POST["reviewer_gender"];
$review_star=$_POST["review_star"];
$review_post=$_POST["review_post"];
$dat=Date('d/m/y');

// $user_id=13;
// $review_star=2;
// $review_post="okay";
$sql="INSERT INTO review VALUES ('$book_id','$dat','$review_star','$review_post','$reviewer_name','$reviewer_gender');";

if ($conn->query($sql) === TRUE) {
	echo "review_posted_successfully";
} else {
	echo "review_failed_to_post";
}

?>