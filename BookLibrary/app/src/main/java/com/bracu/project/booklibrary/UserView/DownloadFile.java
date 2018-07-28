package com.bracu.project.booklibrary.UserView;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bracu.project.booklibrary.BackgroundService.FetchBook;
import com.bracu.project.booklibrary.CommonClass;
import com.bracu.project.booklibrary.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DownloadFile {

    private Context context;
    private String file_url;
    private int position;
    Boolean isSDPresent, isSDSupportedDevice;

    private DownloadManager downloadManager;
    private long refid;
    private Uri Download_Uri;
    ArrayList<Long> list = new ArrayList<>();

    int notificationId = 0;
    String CHANNEL_ID = "my_channel_02";
    String CHANNEL_NAME = "Download";
    int IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT;
    NotificationManager notificationManager;
    NotificationCompat.Builder mBuilder;
    private Intent myIntent;

    private ArrayList<String> urlArrayList;
    private String urlKey = "DOWNLOAD_ID";

    public DownloadFile(final Context context, String url, int position) {
        this.context = context;
        file_url = url;
        this.position = position;

        urlArrayList = new ArrayList();
        urlArrayList = getArrayList(urlKey);
        try {
            if (urlArrayList.size() == 0) {
                urlArrayList.add(file_url);
                saveArrayList(urlArrayList, urlKey);
                Toast.makeText(context, "Downloading in Background", Toast.LENGTH_SHORT).show();
                DownloadWorker();
            } else if (urlArrayList.size() != 0) {
                for (int i = 0; i < urlArrayList.size(); i++) {
                    if (urlArrayList.get(i).equals(file_url)) {
                        Toast.makeText(context, "Already Downloaded...", Toast.LENGTH_SHORT).show();
                        break;
                    } else if (!urlArrayList.get(i).equals(file_url) && i == (urlArrayList.size() - 1)) {
                        removeArrayList(urlKey);
                        urlArrayList.add(file_url);
                        saveArrayList(urlArrayList, urlKey);
                        Toast.makeText(context, "Downloading in Background", Toast.LENGTH_SHORT).show();
                        DownloadWorker();
                    }
                }
            }
        } catch (Exception e) {
            urlArrayList = new ArrayList<>();
            urlArrayList.add(file_url);
            saveArrayList(urlArrayList, urlKey);
            Toast.makeText(context, "Downloading in Background", Toast.LENGTH_SHORT).show();
            DownloadWorker();
        }
    }

    public void DownloadWorker() {
        isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        isSDSupportedDevice = Environment.isExternalStorageRemovable();

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Download_Uri = Uri.parse(file_url);
        String ext = MimeTypeMap.getFileExtensionFromUrl(file_url);

        list.clear();

        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle("Downloading " + FetchBook.booklist.get(position).get(1));
        request.setDescription("Downloading " + FetchBook.booklist.get(position).get(1));
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir("/BookLibrary", "/" + FetchBook.booklist.get(position).get(1) + ".pdf");

        refid = downloadManager.enqueue(request);
        list.add(refid);

        context.registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {

        @Override
        public void onReceive(Context ctxt, Intent intent) {

            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);


            Log.e("IN", "" + referenceId);

            list.remove(referenceId);

            notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.app.NotificationChannel mChannel = new android.app.NotificationChannel(
                        CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);
                notificationManager.createNotificationChannel(mChannel);
            }
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
            mBuilder.setContentTitle(FetchBook.booklist.get(position).get(1))
                    .setContentText("Download Complete")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationManager.notify(position, mBuilder.build());
        }
    };

    public void saveArrayList(ArrayList<String> list, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<String> getArrayList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void removeArrayList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key).commit();
    }
}
