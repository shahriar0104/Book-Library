package com.bracu.project.booklibrary.BackgroundService;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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
 * Created by DarkMan on 27-Mar-18.
 */

public class DeleteRecommendedBackWorker extends AsyncTask<String, Void, String> {
    AlertDialog alertDialog;
    Context context;

    public DeleteRecommendedBackWorker(Context context) {
        this.context=context;
    }

    @Override
    protected String doInBackground(String... params) {
        String bookid = params[0];
        URL url = null;
        try {
            url = new URL(PHPClass.delete_rec);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            httpURLConnection.setDoInput(true);
            OutputStream OS = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
            String data =
                    URLEncoder.encode("bookid", "UTF-8") + "=" + URLEncoder.encode(bookid, "UTF-8");
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
        alertDialog.setTitle("Add Book");
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.equals("delete")){
            //alertDialog.setMessage("Book Added Successfully");
            Toast.makeText(context, "Book Added Successfully", Toast.LENGTH_SHORT).show();
            //alertDialog.show();
        }else {
            alertDialog.setMessage("Book Deletion failed");
            Toast.makeText(context, "Book Deletion failed", Toast.LENGTH_SHORT).show();
            //alertDialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
