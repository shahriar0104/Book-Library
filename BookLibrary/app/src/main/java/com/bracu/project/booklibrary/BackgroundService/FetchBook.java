package com.bracu.project.booklibrary.BackgroundService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bracu.project.booklibrary.AdminView.DeleteBook;
import com.bracu.project.booklibrary.CommonTask;
import com.bracu.project.booklibrary.R;
import com.bracu.project.booklibrary.UserView.AllBooks;
import com.bracu.project.booklibrary.UserView.UserView;

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

public class FetchBook extends AsyncTask<String, Void, String> {
    Context context;
    Activity activity;
    int userIdentify;
    final CommonTask commonTask;
    AlertDialog alertDialog;
    private ProgressDialog dialog;

    public FetchBook(Context ctx) {
        context = ctx;
        dialog = new ProgressDialog(ctx);
        commonTask = (CommonTask)ctx;
    }

    public FetchBook(Context ctx, Activity aty, int userIdentify) {
        context = ctx;
        activity = aty;
        this.userIdentify=userIdentify;
        dialog = new ProgressDialog(ctx);
        commonTask = (CommonTask)ctx;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            String genre = strings[0];
            URL url = new URL(PHPClass.fetchBook);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
            String data =
                    URLEncoder.encode("genre", "UTF-8") + "=" + URLEncoder.encode(genre, "UTF-8");
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
                booklist.add(new ArrayList<String>());
                booklist.get(i).add(jsonObject.getString("book_id"));
                booklist.get(i).add(jsonObject.getString("book_name"));
                booklist.get(i).add(jsonObject.getString("author_name"));
                booklist.get(i).add(jsonObject.getString("book_link"));
                booklist.get(i).add(jsonObject.getString("genre"));
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

    public static List<List<String>> booklist;

    @Override
    protected void onPreExecute() {
        booklist = new ArrayList<>();
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
        commonTask.onTaskFinish(booklist,null);

        if (userIdentify == 1){
            DeleteBook fragment = new DeleteBook();
            FragmentManager fragmentManager = activity.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.admin_view, fragment).addToBackStack(null);
            fragmentTransaction.commit();
        } else if (userIdentify == 2){
            AllBooks fragment = new AllBooks();
            FragmentManager fragmentManager = activity.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.book_genre, fragment).addToBackStack(null);
            fragmentTransaction.commit();
        }

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
