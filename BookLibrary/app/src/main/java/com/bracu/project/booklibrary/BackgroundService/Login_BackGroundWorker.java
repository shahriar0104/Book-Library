package com.bracu.project.booklibrary.BackgroundService;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bracu.project.booklibrary.AdminView.AdminView;
import com.bracu.project.booklibrary.SignUpActivity;
import com.bracu.project.booklibrary.UserView.UserView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Anik on 2/22/2018.
 */

public class Login_BackGroundWorker extends AsyncTask<String, Void, String> {

    String login_url;
    Context context;
    AlertDialog alertDialog;
    private ProgressDialog dialog;

    public Login_BackGroundWorker(Context ctx) {
        context = ctx;
        login_url = PHPClass.login_url;
        dialog = new ProgressDialog(ctx);
    }
    String nameOrEmail,password;
    @Override
    protected String doInBackground(String... params) {

        try{
            nameOrEmail = params[0];
            password = params[1];
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data =
                    URLEncoder.encode("name_email", "UTF-8") + "=" + URLEncoder.encode(nameOrEmail, "UTF-8")+ "&" +
                    URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            bufferedWriter.write(post_data);
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
        dialog.setMessage("Checking Info,please wait...");
        dialog.show();
    }
    public static String[] userInfoArray=new String[6];
    @Override
    protected void onPostExecute(String result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (result.equals("failed")){
            Toast.makeText(context, "Incorrect Info", Toast.LENGTH_SHORT).show();
        }else {

            String [] userInfo=result.split(",");
            for (int i = 0; i < userInfo.length; i++) {
                userInfoArray[i]=userInfo[i];
            }

            SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isloggedIn", true);
            editor.putString("id", userInfoArray[0]);
            editor.putString("userName", userInfoArray[1]);
            editor.putString("userEmail", userInfoArray[2]);
            editor.putString("userPassword", userInfoArray[3]);
            editor.putString("userStatus", userInfoArray[4]);
            editor.putString("userSex", userInfoArray[5]);
            editor.apply();

           if (Integer.parseInt(userInfoArray[4])==1){
                context.startActivity(new Intent(context, AdminView.class));
            }else if (Integer.parseInt(userInfoArray[4])==2){
                context.startActivity(new Intent(context, UserView.class));
            }else {
                alertDialog.setMessage(result);
                alertDialog.show();
            }

        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
        dialog.setMessage("Checking Info,please wait...");
        dialog.show();
    }
}
