package com.bracu.project.booklibrary.BackgroundService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.bracu.project.booklibrary.AdminView.DeleteBook;
import com.bracu.project.booklibrary.CommonTask;
import com.bracu.project.booklibrary.R;
import com.bracu.project.booklibrary.UserView.AllBooks;
import com.bracu.project.booklibrary.UserView.BookDetails;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DarkMan on 23-Feb-18.
 */

public class FetchReview extends AsyncTask<String, Void, String> {
    Context context;
    Activity activity;
    int position;
    final CommonTask commonTask;
    AlertDialog alertDialog;
    private ProgressDialog dialog;
    String book_id;

    public FetchReview(Context ctx) {
        context = ctx;
        dialog = new ProgressDialog(ctx);
        commonTask = (CommonTask)ctx;
    }

    public FetchReview(Context ctx, Activity aty,int position) {
        context = ctx;
        activity = aty;
        this.position=position;
        dialog = new ProgressDialog(ctx);
        commonTask = (CommonTask)ctx;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            book_id = strings[0];
            URL url = new URL(PHPClass.fetch_review);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
            String data =
                    URLEncoder.encode("book_id", "UTF-8") + "=" + URLEncoder.encode(book_id, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            OS.close();


            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                reviewList.add(new ArrayList<String>());
                reviewList.get(i).add(jsonObject.getString("book_id"));
                reviewList.get(i).add(jsonObject.getString("date"));
                reviewList.get(i).add(jsonObject.getString("review_star"));
                reviewList.get(i).add(jsonObject.getString("review_text"));
                reviewList.get(i).add(jsonObject.getString("reviewer_name"));
                reviewList.get(i).add(jsonObject.getString("reviewer_gender"));
            }
            /*httpURLConnection.disconnect();
            inputStream.close();
            bufferedReader.close();*/

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<List<String>> reviewList;

    @Override
    protected void onPreExecute() {
        reviewList = new ArrayList<>();
        alertDialog = new AlertDialog.Builder(context).create();
        dialog.setMessage("Fetching Data...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onPostExecute(String val) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        commonTask.onTaskFinish(null,reviewList);

        BookDetails fragment = new BookDetails();

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.book_genre, fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Fetch Book");
        alertDialog.setMessage("Checking Info,please wait...");
        alertDialog.show();
    }
}
