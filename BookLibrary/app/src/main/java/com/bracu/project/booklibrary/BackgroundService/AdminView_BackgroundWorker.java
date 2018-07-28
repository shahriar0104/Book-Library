package com.bracu.project.booklibrary.BackgroundService;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by DarkMan on 25-Feb-18.
 */

public class AdminView_BackgroundWorker extends AsyncTask<String, Void, String> {


    AlertDialog alertDialog;
    Context context;
    String addNewBranch;
    public AdminView_BackgroundWorker(Context ctx) {
        context=ctx;

    }

    @Override
    protected String doInBackground(String... params) {
            String book_name = params[0];
            String author_name = params[1];
            String book_link = params[2];
            String read_link = params[3];
            String genre = params[4];

            try {

                URL url = new URL(PHPClass.addBook_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data =
                        URLEncoder.encode("book_name", "UTF-8") + "=" + URLEncoder.encode(book_name, "UTF-8") + "&" +
                                URLEncoder.encode("author_name", "UTF-8") + "=" + URLEncoder.encode(author_name, "UTF-8") + "&" +
                                URLEncoder.encode("book_link", "UTF-8") + "=" + URLEncoder.encode(book_link, "UTF-8") + "&" +
                                URLEncoder.encode("read_link", "UTF-8") + "=" + URLEncoder.encode(read_link, "UTF-8") + "&" +
                                URLEncoder.encode("genre", "UTF-8") + "=" + URLEncoder.encode(genre, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }


    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Adding Book");
    }

    @Override
    protected void onPostExecute(String result) {
        //Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        if (result.equals("Book Added")){
            alertDialog.setMessage("Book Added Succesfully");
            alertDialog.show();
        }else {
            alertDialog.setMessage(""+result);
            alertDialog.show();
        }

    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }
}