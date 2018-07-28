package com.bracu.project.booklibrary.BackgroundService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bracu.project.booklibrary.AdminView.DeleteBook;
import com.bracu.project.booklibrary.AdminView.RecommendedBook;
import com.bracu.project.booklibrary.CommonTask;
import com.bracu.project.booklibrary.R;
import com.bracu.project.booklibrary.UserView.AllBooks;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DarkMan on 23-Feb-18.
 */

public class FetchRecommendedBook extends AsyncTask<String, Void, String> {
    Context context;
    Activity activity;
    AlertDialog alertDialog;
    private ProgressDialog dialog;

    public FetchRecommendedBook(Context ctx) {
        context = ctx;
        dialog = new ProgressDialog(ctx);
    }

    public FetchRecommendedBook(Context ctx, Activity aty) {
        context = ctx;
        activity = aty;
        dialog = new ProgressDialog(ctx);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            URL url = new URL(PHPClass.recommend_fetch);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
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
                recommendedBook.add(new ArrayList<String>());
                recommendedBook.get(i).add(jsonObject.getString("ser_no"));
                recommendedBook.get(i).add(jsonObject.getString("book_name"));
                recommendedBook.get(i).add(jsonObject.getString("author_name"));
            }

            httpURLConnection.disconnect();
            inputStream.close();
            bufferedReader.close();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<List<String>> recommendedBook;

    @Override
    protected void onPreExecute() {
        recommendedBook = new ArrayList<>();
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
            RecommendedBook fragment = new RecommendedBook();
            FragmentManager fragmentManager = activity.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.admin_view, fragment).addToBackStack(null);
            fragmentTransaction.commit();

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Fetch Book");
        alertDialog.setMessage("Fetching,please wait...");
        alertDialog.show();
    }
}
