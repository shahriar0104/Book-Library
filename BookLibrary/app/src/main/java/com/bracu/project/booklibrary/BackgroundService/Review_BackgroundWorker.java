package com.bracu.project.booklibrary.BackgroundService;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by DarkMan on 22-Feb-18.
 */

public class Review_BackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;
    AlertDialog alertDialog;
    String make_review;
    private ProgressDialog dialog;

    public Review_BackgroundWorker(Context ctx) {
        context = ctx;
        make_review = PHPClass.reviewUpload;
        dialog = new ProgressDialog(ctx);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String book_id = params[0];
            String reviewer_name = params[1];
            String reviewer_gender = params[2];
            String review_star = params[3];
            String review_post = params[4];

            URL url = new URL(make_review);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            //  Log.d("asse","ei porzonto");
            //httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String data =
                    URLEncoder.encode("book_id", "UTF-8") + "=" + URLEncoder.encode(book_id, "UTF-8") + "&" +
                    URLEncoder.encode("reviewer_name", "UTF-8") + "=" + URLEncoder.encode(reviewer_name, "UTF-8") + "&" +
                            URLEncoder.encode("reviewer_gender", "UTF-8") + "=" + URLEncoder.encode(reviewer_gender, "UTF-8") + "&" +
                            URLEncoder.encode("review_star", "UTF-8") + "=" + URLEncoder.encode(review_star, "UTF-8") + "&" +
                            URLEncoder.encode("review_post", "UTF-8") + "=" + URLEncoder.encode(review_post, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();


            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void onPostExecute(String result) {

        if (result.equalsIgnoreCase("review_posted_successfully")) {
            alertDialog.setMessage("review posted successfully");
            alertDialog.show();

        } else if (result.equalsIgnoreCase("review_failed_to_post")) {
            alertDialog.setMessage("review failed to post");
            alertDialog.show();

        } else {
            //alertDialog.setMessage(result);
            // alertDialog.show();
        }
        dialog.dismiss();
    }

    @Override
    protected void onPreExecute() {

        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("User");
        dialog.setMessage("please wait...");
        dialog.show();
    }

    AlertDialog alertDialogs;

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
     /*   alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("User");
        dialog.setMessage("please wait...");
        dialog.show();*/
    }
}
