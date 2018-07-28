package com.bracu.project.booklibrary.UserView;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bracu.project.booklibrary.CommonClass;
import com.bracu.project.booklibrary.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadedBook extends Fragment {

    private View view;
    public static List<String> BookListShow;
    RecyclerView recyclerView;
    public static DownloadedBookAdapter adapter;

    Boolean isSDPresent, isSDSupportedDevice;
    File mydir;
    static String[] fileOutput;
    public static File[] dirFiles;
    public static LinearLayout linearLayoutPlayingSong;
    static Context context;
    private TextView noDownload;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.download_book_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        noDownload=view.findViewById(R.id.noDownload);

        isWriteStoragePermissionGranted();

        isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        isSDSupportedDevice = Environment.isExternalStorageRemovable();

        if (isReadStoragePermissionGranted()) {
            if (isSDSupportedDevice && isSDPresent) {
                File root = Environment.getExternalStorageDirectory();
                File dir = new File(root + "/BookLibrary/");
                if (dir.exists()) {
                    dirFiles = dir.listFiles();
                    if (dirFiles.length != 0) {
                        fileOutput = new String[dirFiles.length];
                        for (int ii = 0; ii < dirFiles.length; ii++) {
                            fileOutput[ii] = dirFiles[ii].getName().toString();
                            if (fileOutput[ii].indexOf(".") > 0)
                                fileOutput[ii] = fileOutput[ii].substring(0, fileOutput[ii].lastIndexOf("."));
                        }
                    }
                }
            } else {
                mydir = new File(Environment.getExternalStorageDirectory(), "BookLibrary");
                if (mydir.exists()) {
                    dirFiles = mydir.listFiles();
                    if (dirFiles.length != 0) {
                        fileOutput = new String[dirFiles.length];
                        for (int ii = 0; ii < dirFiles.length; ii++) {
                            fileOutput[ii] = dirFiles[ii].getName().toString();
                            if (fileOutput[ii].indexOf(".") > 0)
                                fileOutput[ii] = fileOutput[ii].substring(0, fileOutput[ii].lastIndexOf("."));
                        }
                    }
                }
            }
        }

        context = getActivity();
        Uri uri = null;

        if (dirFiles.length != 0){
            noDownload.setVisibility(View.GONE);
            setUpList();
            adapter = new DownloadedBookAdapter(getActivity(), BookListShow);
            recyclerView.setAdapter(adapter);
        }else{
            //Toast.makeText(context, "You have not yet Download any books", Toast.LENGTH_SHORT).show();
            noDownload.setVisibility(View.VISIBLE);
            Typeface advent_bold = Typeface.createFromAsset(context.getAssets(),"AdventPro-Bold.ttf");
            noDownload.setTypeface(advent_bold);
        }

        return view;
    }

    public static void setUpList() {
        BookListShow = new ArrayList<>();
        for (int i = 0; i < fileOutput.length; i++) {
            BookListShow.add(fileOutput[i]);
        }
    }

    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            /*if (getContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }*/
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // This is Case 1. Now we need to check further if permission was shown before or not

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // This is Case 4.
                    return false;
                } else {
                    // This is Case 3. Request for permission here
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    return false;
                }

            } else {
                // This is Case 2. You have permission now you can do anything related to it
                return true;
            }
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                /*if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    isWriteStoragePermissionGranted();
                }*/
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // This is Case 2 (Permission is now granted)
                } else {
                    // This is Case 1 again as Permission is not granted by user

                    //Now further we check if used denied permanently or not
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // case 4 User has denied permission but not permanently
                        isWriteStoragePermissionGranted();
                    } else {
                        // case 5. Permission denied permanently.
                        // You can open Permission setting's page from here now.
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        //startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                    }

                }
                break;

            case 3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    isReadStoragePermissionGranted();
                }
                break;
        }
    }
}



