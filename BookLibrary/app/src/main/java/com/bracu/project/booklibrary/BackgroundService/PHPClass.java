package com.bracu.project.booklibrary.BackgroundService;

/**
 * Created by Anik on 2/22/2018.
 */

public class PHPClass {

    //static String ip="http://192.168.1.101/booklibrary/";
    static String ip = "https://iamtorab.com/booklibrary/";

    public static String login_url = ip + "login.php";
    public static String signUp_url = ip + "signup.php";
    public static String addBook_url = ip + "add_book.php";
    public static String fetchBook = ip + "fetchbook.php";
    public static String deleteBook = ip + "delete_book.php";
    public static String reviewUpload = ip + "review.php";
    public static String recommend = ip + "recommend.php";
    public static String recommend_fetch = ip + "fetch_recommended.php";
    public static String fetch_review = ip + "fetch_review.php";
    public static String delete_rec = ip + "delete_recommended.php";

}
