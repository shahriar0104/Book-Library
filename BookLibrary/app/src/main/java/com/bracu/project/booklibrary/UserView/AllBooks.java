package com.bracu.project.booklibrary.UserView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.bracu.project.booklibrary.AdminView.DeleteBookAdapter;
import com.bracu.project.booklibrary.BackgroundService.FetchBook;
import com.bracu.project.booklibrary.CommonClass;
import com.bracu.project.booklibrary.CommonTask;
import com.bracu.project.booklibrary.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AllBooks extends Fragment implements CommonTask{

    public static ListView lv;
    public static allBooksBookAdapter adapter;
    static ArrayList<String> book_name;
    static ArrayList<String> author_name;
    private boolean isSDPresent,isSDSupportedDevice;
    File mydir;
    static Context context;
    public static Activity activity;
    private View view;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        view=inflater.inflate(R.layout.book_list,container,false);
        /*Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Delete Review");*/

        isWriteStoragePermissionGranted();
        context = getActivity();
        activity = getActivity();

        isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        isSDSupportedDevice = Environment.isExternalStorageRemovable();

        if (isSDSupportedDevice && isSDPresent) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root + "/BookLibrary/");
            if (dir.exists() == false) {
                dir.mkdirs();
            }
        } else if (isWriteStoragePermissionGranted()){
            mydir = new File(Environment.getExternalStorageDirectory(), "BookLibrary");
            if (!mydir.exists()) {
                mydir.mkdirs();
            }
        }

        searchView = view.findViewById(R.id.search_bar);
        searchView.setQueryHint("Book or Author Name");

        lv = view.findViewById(R.id.lv);
        adapter = new allBooksBookAdapter(getActivity());
        lv.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
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
            if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // This is Case 1. Now we need to check further if permission was shown before or not

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

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

    @Override
    public void onTaskFinish(List<List<String>> firstArrayList, List<List<String>> secondArrayList) {
        CommonClass.booklist=firstArrayList;
        CommonClass.reviewList=secondArrayList;
    }
}
