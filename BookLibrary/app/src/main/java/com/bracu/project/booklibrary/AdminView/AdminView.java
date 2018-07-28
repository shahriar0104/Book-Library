package com.bracu.project.booklibrary.AdminView;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
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

import com.bracu.project.booklibrary.BackgroundService.AdminView_BackgroundWorker;
import com.bracu.project.booklibrary.BackgroundService.FetchBook;
import com.bracu.project.booklibrary.BackgroundService.FetchRecommendedBook;
import com.bracu.project.booklibrary.BackgroundService.PHPClass;
import com.bracu.project.booklibrary.CommonClass;
import com.bracu.project.booklibrary.CommonTask;
import com.bracu.project.booklibrary.MainActivity;
import com.bracu.project.booklibrary.R;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import pub.devrel.easypermissions.AppSettingsDialog;

public class AdminView extends AppCompatActivity implements CommonTask {

    public AlertDialog dialog;
    private Button addBook, cancelBook;
    private EditText bookName, authorName ;
    private TextView bookLink;
    private Spinner genre;
    private String genreOfBook;
    private CardView novelCard,cookingCard,ITCard,recommendCard,historyCard,fictionCard,sciFictionCard;
    private ImageView LogOut;
    private int PICK_PDF_REQUEST = 1;
    private SpotsDialog spotsDialog;
    private Bitmap bitmap;
    private Uri filePath;
    private static final String UPLOAD_URL = PHPClass.addBook_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AdminView.this);
                View mView = getLayoutInflater().inflate(R.layout.admin_dialog_add_book, null);

                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.setCancelable(false);
                dialog.show();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                addBook = dialog.findViewById(R.id.addBook);
                cancelBook = dialog.findViewById(R.id.cancelBook);
                bookName = dialog.findViewById(R.id.bookName);
                authorName = dialog.findViewById(R.id.authorName);
                bookLink = dialog.findViewById(R.id.bookLink);

                List<String> genreList = new ArrayList<>();
                genreList.add("Novel");
                genreList.add("History");
                genreList.add("IT");
                genreList.add("Cooking");
                genreList.add("Fiction");
                genreList.add("Science Fiction");

                genre = new Spinner(AdminView.this);
                genre = dialog.findViewById(R.id.genre);
                ArrayAdapter<String> genreAdapter =
                        new ArrayAdapter<String>(getBaseContext(), R.layout.z_spinner_item, genreList) {
                            @Override
                            public boolean isEnabled(int position) {
                                return true;
                            }

                            @Override
                            public View getDropDownView(int position, View convertView,
                                                        ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view;
                                tv.setTextColor(Color.BLACK);
                                return view;
                            }
                        };
                genreAdapter.setDropDownViewResource(R.layout.z_spinner_item);
                genre.setAdapter(genreAdapter);
                genre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        genreOfBook = (String) parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                bookLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFileChooser();
                    }
                });
                addBook.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AdminView.this, "Upload starting...", Toast.LENGTH_SHORT).show();
                        String book = bookName.getText().toString().trim().replaceAll(" ","");
                        String author = authorName.getText().toString().trim();
                        //Toast.makeText(AdminView.this, ""+book, Toast.LENGTH_SHORT).show();

                        if (!haveNetworkConnection()) {
                            Toast.makeText(AdminView.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        } else {
                            if (bookLink.getText().toString().equalsIgnoreCase("Click to choose Pdf File")){
                                Toast.makeText(AdminView.this, "Please select a Pdf File", Toast.LENGTH_SHORT).show();
                            }else if (!(book.isEmpty() || author.isEmpty())) {
                                uploadMultipart(book, author, genreOfBook);
                            } else {
                                Toast.makeText(AdminView.this, "Please Fill Up All The Field", Toast.LENGTH_SHORT).show();
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

        novelCard = findViewById(R.id.novelCard);
        ITCard = findViewById(R.id.ITBookCard);
        cookingCard = findViewById(R.id.CookingBookCard);
        historyCard=findViewById(R.id.HistoryBookCard);
        fictionCard=findViewById(R.id.FictionBookCard);
        sciFictionCard=findViewById(R.id.sciFictionBookCard);
        recommendCard=findViewById(R.id.requestBookCard);
        LogOut = findViewById(R.id.logout);

        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = AdminView.this.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(AdminView.this, MainActivity.class));
                finish();
            }
        });

        ITCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!haveNetworkConnection()) {
                    Toast.makeText(AdminView.this, "You have no internet connection...", Toast.LENGTH_SHORT).show();
                } else {
                    FetchBook fetchBook = new FetchBook(AdminView.this, AdminView.this,1);
                    fetchBook.execute("IT");
                }
            }
        });

        novelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!haveNetworkConnection()) {
                    Toast.makeText(AdminView.this, "You have no internet connection...", Toast.LENGTH_SHORT).show();
                } else {
                    FetchBook fetchBook = new FetchBook(AdminView.this, AdminView.this,1);
                    fetchBook.execute("Novel");
                }
            }
        });

        cookingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!haveNetworkConnection()) {
                    Toast.makeText(AdminView.this, "You have no internet connection...", Toast.LENGTH_SHORT).show();
                } else {
                    FetchBook fetchBook = new FetchBook(AdminView.this, AdminView.this,1);
                    fetchBook.execute("Cooking");
                }
            }
        });

        historyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!haveNetworkConnection()) {
                    Toast.makeText(AdminView.this, "You have no internet connection...", Toast.LENGTH_SHORT).show();
                } else {
                    FetchBook fetchBook = new FetchBook(AdminView.this, AdminView.this,1);
                    fetchBook.execute("History");
                }
            }
        });

        fictionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!haveNetworkConnection()) {
                    Toast.makeText(AdminView.this, "You have no internet connection...", Toast.LENGTH_SHORT).show();
                } else {
                    FetchBook fetchBook = new FetchBook(AdminView.this, AdminView.this,1);
                    fetchBook.execute("Fiction");
                }
            }
        });

        sciFictionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!haveNetworkConnection()) {
                    Toast.makeText(AdminView.this, "You have no internet connection...", Toast.LENGTH_SHORT).show();
                } else {
                    FetchBook fetchBook = new FetchBook(AdminView.this, AdminView.this,1);
                    fetchBook.execute("Science Fiction");
                }
            }
        });

        recommendCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchRecommendedBook fetchRecommendedBook = new FetchRecommendedBook(AdminView.this, AdminView.this);
                fetchRecommendedBook.execute();
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

    @Override
    public void onTaskFinish(List<List<String>> firstArrayList, List<List<String>> secondArrayList) {
        CommonClass.booklist = firstArrayList;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() >= 1) {
            getFragmentManager().popBackStack();
        } else
        finishAffinity();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
    }

    //handling the image chooser activity result
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            String path = getPath(filePath);
            String [] arr=path.split("/");
            bookLink.setText(arr[arr.length-1]);

        }
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

        }
    }

    //method to get the file path from uri
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPath(Uri uri) {
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
        if (isKitKat && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {

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

                return getDataColumn(getApplicationContext(), contentUri, null, null);
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

                return getDataColumn(getApplicationContext(), contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(getApplicationContext(), uri, null, null);
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
    public void uploadMultipart(String bookName, String authorName,  String genre) {

        //getting the actual path of the image
        String path = getPath(filePath);
        spotsDialog = new SpotsDialog(this);
        spotsDialog.setTitle("Uploading...");

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
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

                            dialog.dismiss();
                            spotsDialog.dismiss();
                            AlertDialog alertDialog;
                            alertDialog = new AlertDialog.Builder(AdminView.this).create();
                            //alertDialog.setTitle("Adding Book");
                            alertDialog.setMessage("Uploaded Successfully...");
                            alertDialog.show();

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    })
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
