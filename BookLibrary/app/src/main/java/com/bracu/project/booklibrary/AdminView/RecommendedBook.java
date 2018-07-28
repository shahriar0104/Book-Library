package com.bracu.project.booklibrary.AdminView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.bracu.project.booklibrary.BackgroundService.DeleteRecommendedBackWorker;
import com.bracu.project.booklibrary.BackgroundService.FetchRecommendedBook;
import com.bracu.project.booklibrary.BackgroundService.PHPClass;
import com.bracu.project.booklibrary.R;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.ArrayList;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import pub.devrel.easypermissions.AppSettingsDialog;

import static android.app.Activity.RESULT_OK;


public class RecommendedBook extends Fragment {

    public static ListView lv;
    public static RecommendedBookAdapter adapter;
    static ArrayList<String> book_name;
    static ArrayList<String> author_name;
    public static Fragment fragment;
    public static Context context;
    public static Activity activity;
    public static int PICK_PDF_REQUEST = 1;
    private static SpotsDialog spotsDialog;
    private Bitmap bitmap;
    private static Uri filePath;
    private static final String UPLOAD_URL = PHPClass.addBook_url;
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
        View view=inflater.inflate(R.layout.book_list,container,false);
        /*Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Delete Review");*/

        /*book_name=new ArrayList<>();
        author_name = new ArrayList<>();

        if (FetchBook.booklist.size() > 0) {
            for (int i = 0; i < FetchBook.booklist.size(); i++) {
                book_name.add(FetchBook.booklist.get(i).get(1));
                author_name.add(FetchBook.booklist.get(i).get(2));
            }
        }*/

        activity=getActivity();
        context=getActivity();

        searchView = view.findViewById(R.id.search_bar);
        searchView.setQueryHint("Book or Author Name");

        lv = view.findViewById(R.id.lv);
        adapter = new RecommendedBookAdapter(getActivity(), getActivity().getFragmentManager().findFragmentById(getId()));
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

    //handling the image chooser activity result
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST) {
            filePath = data.getData();
            String path = getPath(filePath);
            String [] arr=path.split("/");
            RecommendedBookAdapter.bookLink.setText(arr[arr.length-1]);
        }
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

        }
    }

    //method to get the file path from uri
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(Uri uri) {
        /*Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();*/

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(RecommendedBook.activity, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(RecommendedBook.activity, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(RecommendedBook.activity, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(RecommendedBook.activity, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;

        //return path;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void uploadMultipart(String bookName, String authorName , String genre , final String position , final int finalPos) {

        //getting the actual path of the image
        String path = getPath(filePath);
        spotsDialog = new SpotsDialog(context);
        spotsDialog.setTitle("Uploading...");

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(context, uploadId, UPLOAD_URL)
                    .addParameter("book_name", bookName)
                    .addParameter("author_name", authorName)
                    .addFileToUpload(path, "pdf")
                    .addParameter("genre", genre)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            spotsDialog.show();
                            spotsDialog.setMessage("Uploading...");
                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                            RecommendedBookAdapter.dialog.dismiss();
                            spotsDialog.dismiss();
                            AlertDialog alertDialog;
                            alertDialog = new AlertDialog.Builder(context).create();
                            //alertDialog.setTitle("Adding Book");
                            alertDialog.setMessage("Uploaded Successfully...");
                            //alertDialog.show();
                            DeleteRecommendedBackWorker deleteRecommendedBackWorker = new DeleteRecommendedBackWorker(context);
                            deleteRecommendedBackWorker.execute(position);
                            FetchRecommendedBook.recommendedBook.remove(finalPos);
                            RecommendedBookAdapter.bookList = new ArrayList<>();
                            RecommendedBookAdapter.bookList=FetchRecommendedBook.recommendedBook;
                            RecommendedBookAdapter.bookCheckList=FetchRecommendedBook.recommendedBook;
                            RecommendedBook.adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    })
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
