package com.bracu.project.booklibrary;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bracu.project.booklibrary.AdminView.AdminView;
import com.bracu.project.booklibrary.BackgroundService.Login_BackGroundWorker;
import com.bracu.project.booklibrary.UserView.UserView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.security.auth.callback.Callback;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        ProviderInstaller.ProviderInstallListener{

    TextInputEditText nameEmail,Password;
    Button SignUpButton,loginButton;
    String nameOrEmail,password;
    final static int REQUEST_STORAGE = 199;
    private static final int ERROR_DIALOG_REQUEST_CODE = 1;

    private boolean mRetryProviderInstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );*/

        updateAndroidSecurityProvider(this);

        nameEmail=findViewById(R.id.nameEmail);
        Password=findViewById(R.id.password);
        SignUpButton = findViewById(R.id.signUp);
        loginButton=findViewById(R.id.login);

        SharedPreferences sharedPreferences = this.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        final boolean    isloggedIn = sharedPreferences.getBoolean("isloggedIn", false);
        final String    userId = sharedPreferences.getString("id", "");
        final String    userName = sharedPreferences.getString("userName", "");
        final String   userEmail = sharedPreferences.getString("userEmail", "");
        final String   userPassword = sharedPreferences.getString("userPassword", "");
        final String   userStatus = sharedPreferences.getString("userStatus", "");
        final String   userSex = sharedPreferences.getString("userSex", "");

        Login_BackGroundWorker.userInfoArray=new String[6];

        if (isloggedIn){
            Login_BackGroundWorker.userInfoArray[0]=userId;
            Login_BackGroundWorker.userInfoArray[1]=userName;
            Login_BackGroundWorker.userInfoArray[2]=userEmail;
            Login_BackGroundWorker.userInfoArray[3]=userPassword;
            Login_BackGroundWorker.userInfoArray[4]=userStatus;
            Login_BackGroundWorker.userInfoArray[5]=userSex;

            if (!userStatus.isEmpty()){
                if (userStatus.equalsIgnoreCase("1")){

                    Intent homeIntent=new Intent(MainActivity.this, AdminView.class);
                    startActivity(homeIntent);
                    finish();
                }else if(userStatus.equalsIgnoreCase("2")){

                    Intent homeIntent=new Intent(MainActivity.this, UserView.class);
                    startActivity(homeIntent);
                    finish();
                }
            }
        }
        else{

            checkStoragePermission();

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nameOrEmail = nameEmail.getText().toString();
                    password =Password.getText().toString();
                    if (!haveNetworkConnection()){
                        Toast.makeText(MainActivity.this, "Connect to Internet", Toast.LENGTH_SHORT).show();
                    }else {
                        if (!(nameOrEmail.isEmpty()||password.isEmpty())){
                            Login_BackGroundWorker login_backGroundWorker = new Login_BackGroundWorker(MainActivity.this);
                            login_backGroundWorker.execute(nameOrEmail,password);
                        }else {
                            Toast.makeText(MainActivity.this, "Please Fill Up All The Field", Toast.LENGTH_SHORT).show();
                        }
                        //startActivity(new Intent(MainActivity.this, UserView.class));
                    }
                }
            });

            SignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,SignUpActivity.class));
                }
            });
        }
    }

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    private void checkStoragePermission() {
        String perms = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (EasyPermissions.hasPermissions(this, perms)) {

        } else {
            EasyPermissions.requestPermissions(this, "We need permissions for storage." +
                            "Otherwise we are not able to download Book",
                    REQUEST_STORAGE, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

        }
        if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
            // Adding a fragment via GoogleApiAvailability.showErrorDialogFragment
            // before the instance state is restored throws an error. So instead,
            // set a flag here, which will cause the fragment to delay until
            // onPostResume.
            mRetryProviderInstall = true;
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onProviderInstalled() {
        // Provider is up-to-date, app can make secure network calls.
    }

    /**
     * This method is called if updating fails; the error code indicates
     * whether the error is recoverable.
     */
    @Override
    public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        if (availability.isUserResolvableError(errorCode)) {
            // Recoverable error. Show a dialog prompting the user to
            // install/update/enable Google Play services.
            availability.showErrorDialogFragment(
                    this,
                    errorCode,
                    ERROR_DIALOG_REQUEST_CODE,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // The user chose not to take the recovery action
                            onProviderInstallerNotAvailable();
                        }
                    });
        } else {
            // Google Play services is not available.
            onProviderInstallerNotAvailable();
        }
    }

    /**
     * On resume, check to see if we flagged that we need to reinstall the
     * provider.
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mRetryProviderInstall) {
            // We can now safely retry installation.
            ProviderInstaller.installIfNeededAsync(this, this);
        }
        mRetryProviderInstall = false;
    }

    private void onProviderInstallerNotAvailable() {
        // This is reached if the provider cannot be updated for some reason.
        // App should consider all HTTP communication to be vulnerable, and take
        // appropriate action.
    }
}
