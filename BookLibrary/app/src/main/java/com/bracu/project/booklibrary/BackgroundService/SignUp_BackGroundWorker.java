package com.bracu.project.booklibrary.BackgroundService;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
 * Created by DarkMan on 23-Feb-18.
 */

public class SignUp_BackGroundWorker extends AsyncTask<String, Void, String> {


    AlertDialog alertDialog;
    Context context;
    String reg_url;
    String user_name,email,password,gender;
    private ProgressDialog dialog;
    public SignUp_BackGroundWorker(Context ctx) {
        context=ctx;
        reg_url = PHPClass.signUp_url;
        dialog = new ProgressDialog(ctx);
    }

    @Override
    protected String doInBackground(String... params) {

        user_name = params[0];
        email = params[1];
        password = params[2];
        gender = params[3];

        try {
            URL url = new URL(reg_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
            String data =
                    URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&" +
                            URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                            URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                            URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8");
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
        alertDialog.setTitle("SignUp Status");
        dialog.setMessage("Creating User,please wait...");
        dialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (result.equals("account_successfully_created")){
            /*Login_BackGroundWorker login_backGroundWorker=new Login_BackGroundWorker(context);
            login_backGroundWorker.execute(mobile_number);*/
            Toast.makeText(context, "Redirecting ...", Toast.LENGTH_SHORT).show();
            Login_BackGroundWorker login_backGroundWorker = new Login_BackGroundWorker(context);
            login_backGroundWorker.execute(user_name,password);
        }else if (result.equals("user_exists")){
            Toast.makeText(context, "User Already Exist", Toast.LENGTH_SHORT).show();
        }else if (result.equals("user_mail_exists")){
            Toast.makeText(context, "User Already Exist with this Email", Toast.LENGTH_SHORT).show();
        } else {
            alertDialog.setMessage(result);
            alertDialog.show();
        }
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("SignUp Status");
        dialog.setMessage("Creating User,please wait...");
        dialog.show();
    }
}
