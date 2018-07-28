package com.bracu.project.booklibrary.UserView;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.AlertDialog;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bracu.project.booklibrary.AdminView.AdminView;
import com.bracu.project.booklibrary.AdminView.DeleteBook;
import com.bracu.project.booklibrary.BackgroundService.AdminView_BackgroundWorker;
import com.bracu.project.booklibrary.BackgroundService.Login_BackGroundWorker;
import com.bracu.project.booklibrary.BackgroundService.Recommend_BackgroundWorker;
import com.bracu.project.booklibrary.BackgroundService.Review_BackgroundWorker;
import com.bracu.project.booklibrary.MainActivity;
import com.bracu.project.booklibrary.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class UserView extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private CardView downloadedBook,allBooks,recommendation,LogOut;
    private TextView userName;
    private ImageView profile_photo;
    private AlertDialog dialog;
    private Button recommendBook,cancelBook;
    private EditText bookName,authorName;
    final static int REQUEST_STORAGE = 199;
    private boolean isSDPresent,isSDSupportedDevice;
    File mydir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        downloadedBook = findViewById(R.id.downloadedBookCard);
        allBooks = findViewById(R.id.allBooksCard);
        recommendation=findViewById(R.id.recommendationCard);
        LogOut = findViewById(R.id.LogOutCard);
        userName = findViewById(R.id.userName);
        profile_photo = findViewById(R.id.user_profile_photo);

        if (Login_BackGroundWorker.userInfoArray[5].equalsIgnoreCase("Male"))
            profile_photo.setImageDrawable(getResources().getDrawable(R.drawable.male_user));
        else if (Login_BackGroundWorker.userInfoArray[5].equalsIgnoreCase("Female"))
            profile_photo.setImageDrawable(getResources().getDrawable(R.drawable.female_user));
        userName.setText(Login_BackGroundWorker.userInfoArray[1]);

        checkStoragePermission();

        isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        isSDSupportedDevice = Environment.isExternalStorageRemovable();

        if (isSDSupportedDevice && isSDPresent) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root + "/BookLibrary/");
            if (dir.exists() == false) {
                dir.mkdirs();
            }
        } else {
            mydir = new File(Environment.getExternalStorageDirectory(), "BookLibrary");
            if (!mydir.exists()) {
                mydir.mkdirs();
            }
        }


        downloadedBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    checkStoragePermission();
                }else {
                    if (isSDSupportedDevice && isSDPresent) {
                        File root = Environment.getExternalStorageDirectory();
                        File dir = new File(root + "/BookLibrary/");
                        if (dir.exists() == false) {
                            dir.mkdirs();
                        }
                    } else {
                        mydir = new File(Environment.getExternalStorageDirectory(), "BookLibrary");
                        if (!mydir.exists()) {
                            mydir.mkdirs();
                        }
                    }

                    DownloadedBook fragment = new DownloadedBook();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.user_view, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        allBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    checkStoragePermission();
                }else
                    startActivity(new Intent(UserView.this,BookGenre.class));
            }
        });

        recommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(UserView.this);
                View mView = getLayoutInflater().inflate(R.layout.user_recommendation_dialog, null);

                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.setCancelable(false);
                dialog.show();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                recommendBook = dialog.findViewById(R.id.addBook);
                cancelBook = dialog.findViewById(R.id.cancelBook);
                bookName = dialog.findViewById(R.id.bookName);
                authorName = dialog.findViewById(R.id.authorName);


                recommendBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String book = bookName.getText().toString().trim();
                        String author = authorName.getText().toString().trim();

                        if (!haveNetworkConnection()) {
                            Toast.makeText(UserView.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!(book.isEmpty())) {
                                Recommend_BackgroundWorker recommend_backgroundWorker = new Recommend_BackgroundWorker(UserView.this);
                                recommend_backgroundWorker.execute(book, author);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(UserView.this, "Please Fill Up All The Field", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                cancelBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = UserView.this.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(UserView.this, MainActivity.class));
                finish();
            }
        });
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
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() >= 1) {
            getFragmentManager().popBackStack();
        } else
        finishAffinity();
    }
}
