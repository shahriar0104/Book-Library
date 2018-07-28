package com.bracu.project.booklibrary.UserView;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bracu.project.booklibrary.AdminView.DeleteBook;
import com.bracu.project.booklibrary.BackgroundService.DeleteBookBackWorker;
import com.bracu.project.booklibrary.BackgroundService.FetchBook;
import com.bracu.project.booklibrary.BackgroundService.FetchReview;
import com.bracu.project.booklibrary.BackgroundService.Login_BackGroundWorker;
import com.bracu.project.booklibrary.BackgroundService.Review_BackgroundWorker;
import com.bracu.project.booklibrary.CommonClass;
import com.bracu.project.booklibrary.CommonClass;
import com.bracu.project.booklibrary.CommonTask;
import com.bracu.project.booklibrary.R;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class allBooksBookAdapter extends BaseAdapter implements CommonTask,Filterable{

    private final Context mContext;
    private int selectedRow;
    private Uri uri;
    private Button sendReview, cancelReview;
    private EditText descriptionBox;
    private RatingBar ratingBar;
    ValueFilter valueFilter;
    List<List<String>> bookList;
    List<List<String>> bookCheckList;
    int finalPos;

    public allBooksBookAdapter(Context context) {
        mContext  = context;
        bookList = new ArrayList<>();
        bookList = FetchBook.booklist;
        bookCheckList = FetchBook.booklist;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final allBooksBookAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.all_book_item, parent, false);
            viewHolder = new allBooksBookAdapter.ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (allBooksBookAdapter.ViewHolder) convertView.getTag();
        }

        if (FetchBook.booklist.size() == 0){
            Toast.makeText(mContext, "No Books Added Yet", Toast.LENGTH_SHORT).show();
        }

        viewHolder.bookName=convertView.findViewById(R.id.bookName);
        viewHolder.bookIcon=convertView.findViewById(R.id.bookIcon);
        viewHolder.authorName=convertView.findViewById(R.id.authorName);
        viewHolder.menuIcon=convertView.findViewById(R.id.menuIcon);

        viewHolder.bookName.setText(bookList.get(position).get(1));
        viewHolder.authorName.setText(bookList.get(position).get(2));

        Typeface advent_bold = Typeface.createFromAsset(mContext.getAssets(),"AdventPro-Bold.ttf");
        Typeface advent_semi = Typeface.createFromAsset(mContext.getAssets(),"AdventPro-SemiBold.ttf");
        viewHolder.bookName.setTypeface(advent_bold);
        viewHolder.authorName.setTypeface(advent_semi);

        if (bookList.get(position).get(4).equalsIgnoreCase("Novel"))
            viewHolder.bookIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.novel));
        else if (bookList.get(position).get(4).equalsIgnoreCase("History"))
            viewHolder.bookIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.history));
        else if (bookList.get(position).get(4).equalsIgnoreCase("IT"))
            viewHolder.bookIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.it));
        else if (bookList.get(position).get(4).equalsIgnoreCase("Cooking"))
            viewHolder.bookIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cook_book));
        else if (bookList.get(position).get(4).equalsIgnoreCase("Fiction"))
            viewHolder.bookIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.fiction));
        else if (bookList.get(position).get(4).equalsIgnoreCase("Science Fiction"))
            viewHolder.bookIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.science_fiction));

        viewHolder.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(mContext, viewHolder.menuIcon);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.downloadMenu:
                                uri = Uri.parse(bookList.get(position).get(3));
                                DownloadFile downloadFile = new DownloadFile(mContext, uri.toString(), position);
                                return true;
                            case R.id.readOnlineMenu:
                                /*ReadOnline fragment = new ReadOnline();

                                Bundle bundle = new Bundle();
                                bundle.putInt("position", position);
                                fragment.setArguments(bundle);

                                FragmentManager fragmentManager = AllBooks.activity.getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.book_genre, fragment).addToBackStack(null);
                                fragmentTransaction.commit();*/
                                mContext.startActivity(new Intent(mContext,ReadOnline.class).putExtra("position",position));
                                return true;
                            case R.id.detailsMenu:
                                if (!haveNetworkConnection()) {
                                    Toast.makeText(mContext, "You have no internet connection...", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (int i = 0 ; i < bookCheckList.size(); i++){
                                        if (bookList.get(position).get(1).equalsIgnoreCase(bookCheckList.get(i).get(1)))
                                            finalPos = i;
                                    }
                                    FetchReview fetchReview = new FetchReview(mContext, AllBooks.activity,finalPos);
                                    fetchReview.execute(bookList.get(position).get(0));
                                }
                                return true;
                            case R.id.ratingMenu:

                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                                View mView = AllBooks.activity.getLayoutInflater().inflate(R.layout.user_dialog_give_review, null);
                                mBuilder.setView(mView);
                                final AlertDialog dialog = mBuilder.create();
                                TextView title = new TextView(mContext);

                                title.setText("Review");
                                title.setBackgroundColor(Color.DKGRAY);
                                title.setPadding(10, 10, 10, 10);
                                title.setGravity(Gravity.CENTER);
                                title.setTextColor(Color.WHITE);
                                title.setTextSize(20);

                                mBuilder.setCustomTitle(title);
                                dialog.setCancelable(false);
                                dialog.show();

                                sendReview = dialog.findViewById(R.id.sendReview);
                                cancelReview = dialog.findViewById(R.id.cancelReview);
                                descriptionBox = dialog.findViewById(R.id.descriptionBox);
                                ratingBar = dialog.findViewById(R.id.rating);

                                sendReview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String review = descriptionBox.getText().toString();
                                        double rating = ratingBar.getRating();
                                        Review_BackgroundWorker review_backgroundWorker = new Review_BackgroundWorker(mContext);
                                        review_backgroundWorker.execute(bookList.get(position).get(0), Login_BackGroundWorker.userInfoArray[1], Login_BackGroundWorker.userInfoArray[5] , "" + rating, review);
                                        dialog.dismiss();
                                    }
                                });
                                cancelReview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                return true;
                            default:
                                return false;
                        }
                        //return true;
                    }
                });
                popup.show();
            }
        });
        return convertView;
    }

    @Override
    public void onTaskFinish(List<List<String>> firstArrayList, List<List<String>> secondArrayList) {
        CommonClass.booklist=firstArrayList;
        CommonClass.reviewList=secondArrayList;
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


    private static class ViewHolder{
        ImageView bookIcon,menuIcon;
        TextView bookName,authorName;
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
}
