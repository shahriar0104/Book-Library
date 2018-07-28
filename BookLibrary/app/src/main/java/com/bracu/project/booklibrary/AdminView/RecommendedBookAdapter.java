package com.bracu.project.booklibrary.AdminView;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bracu.project.booklibrary.BackgroundService.AdminView_BackgroundWorker;
import com.bracu.project.booklibrary.BackgroundService.FetchRecommendedBook;
import com.bracu.project.booklibrary.BackgroundService.PHPClass;
import com.bracu.project.booklibrary.CommonTask;
import com.bracu.project.booklibrary.R;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import pub.devrel.easypermissions.AppSettingsDialog;

import static android.app.Activity.RESULT_OK;
import static com.bracu.project.booklibrary.AdminView.RecommendedBook.uploadMultipart;

public class RecommendedBookAdapter extends BaseAdapter implements CommonTask,Filterable {

    private final Context mContext;
    private int selectedRow;
    public static android.app.AlertDialog dialog;
    private Button addBook, cancelBook;
    private EditText bookName;
    public static TextView bookLink;
    private EditText authorName;
    private Spinner genre;
    private String genreOfBook;
    private Fragment fragment;
    ValueFilter valueFilter;
    public static List<List<String>> bookList;
    public static List<List<String>> bookCheckList;
    int finalPos;

    public RecommendedBookAdapter(Context context, Fragment fragment) {
        mContext = context;
        this.fragment=fragment;
        bookList = new ArrayList<>();
        bookList = FetchRecommendedBook.recommendedBook;
        bookCheckList = FetchRecommendedBook.recommendedBook;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final RecommendedBookAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.recommended_book_item, parent, false);
            viewHolder = new RecommendedBookAdapter.ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RecommendedBookAdapter.ViewHolder) convertView.getTag();
        }


        viewHolder.bookName = convertView.findViewById(R.id.bookName);
        viewHolder.addRecommended = convertView.findViewById(R.id.addRecommended);
        viewHolder.authorName = convertView.findViewById(R.id.authorName);

        viewHolder.bookName.setText(bookList.get(position).get(1));
        viewHolder.authorName.setText(bookList.get(position).get(2));

        viewHolder.addRecommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(mContext);
                View mView = RecommendedBook.activity.getLayoutInflater().inflate(R.layout.admin_dialog_add_book, null);

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

                bookName.setText(bookList.get(position).get(1));
                authorName.setText(bookList.get(position).get(2));

                List<String> genreList = new ArrayList<>();
                genreList.add("Novel");
                genreList.add("History");
                genreList.add("IT");
                genreList.add("Cooking");
                genreList.add("Fiction");
                genreList.add("Science Fiction");

                genre = new Spinner(mContext);
                genre = dialog.findViewById(R.id.genre);
                ArrayAdapter<String> genreAdapter =
                        new ArrayAdapter<String>(RecommendedBook.activity.getBaseContext(), R.layout.z_spinner_item, genreList) {
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

                RecommendedBookAdapter.bookLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFileChooser();
                    }
                });

                addBook.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "Upload starting...", Toast.LENGTH_SHORT).show();
                        String book = bookName.getText().toString().trim().replaceAll(" ","");
                        String author = authorName.getText().toString().trim();

                        if (!haveNetworkConnection()) {
                            Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        } else {
                            if (bookLink.getText().toString().equalsIgnoreCase("Click to choose Pdf File")) {
                                Toast.makeText(mContext, "Please select a Pdf File", Toast.LENGTH_SHORT).show();
                            } else if (!(book.isEmpty() || author.isEmpty() )) {
                                /*AdminView_BackgroundWorker adminView_backgroundWorker = new AdminView_BackgroundWorker(AdminView.this);
                                adminView_backgroundWorker.execute(book, author, readLink , genreOfBook);
                                dialog.dismiss();*/
                                for (int i = 0 ; i < bookCheckList.size(); i++){
                                    if (bookList.get(position).get(0).equalsIgnoreCase(bookCheckList.get(i).get(0)))
                                        finalPos = i;
                                }
                                RecommendedBook.uploadMultipart(book, author , genreOfBook , FetchRecommendedBook.recommendedBook.get(finalPos).get(0) ,finalPos);
                            } else {
                                Toast.makeText(mContext, "Please Fill Up All The Field", Toast.LENGTH_SHORT).show();
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

        return convertView;
    }

    @Override
    public void onTaskFinish(List<List<String>> firstArrayList, List<List<String>> secondArrayList) {

    }


    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<List<String>> newBooklist  = new ArrayList<>();
                for (int i = 0; i < bookCheckList.size(); i++) {
                    if (((bookCheckList.get(i).get(1).toUpperCase())
                            .contains(constraint.toString().toUpperCase())) || ((bookCheckList.get(i).get(2).toUpperCase())
                            .contains(constraint.toString().toUpperCase()))) {
                        newBooklist.add(bookCheckList.get(i));
                    }
                }
                results.count = newBooklist.size();
                results.values = newBooklist;
            } else {
                results.count = bookCheckList.size();
                results.values = bookCheckList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            //countrylist = (ArrayList<Country>) results.values;
            bookList = (List<List<String>>) results.values;
            notifyDataSetChanged();
        }
    }


    private static class ViewHolder {
        Button addRecommended;
        TextView bookName, authorName;
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fragment.startActivityForResult(Intent.createChooser(intent, "Select Pdf"), RecommendedBook.PICK_PDF_REQUEST);
    }
}
