package com.bracu.project.booklibrary.UserView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.bracu.project.booklibrary.AdminView.AdminView;
import com.bracu.project.booklibrary.BackgroundService.FetchBook;
import com.bracu.project.booklibrary.CommonClass;
import com.bracu.project.booklibrary.CommonTask;
import com.bracu.project.booklibrary.R;

import java.util.List;

public class BookGenre extends AppCompatActivity implements View.OnClickListener,CommonTask{

    private CardView novelBookcard,historyBookCard,itBookCard,cookingBookCard,fictionBookCard,sciFictionBookCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_genre);

        novelBookcard=findViewById(R.id.NovelBookCard);
        historyBookCard=findViewById(R.id.HistoryBookCard);
        itBookCard=findViewById(R.id.ITBookCard);
        cookingBookCard=findViewById(R.id.CookingBookCard);
        fictionBookCard=findViewById(R.id.FictionBookCard);
        sciFictionBookCard=findViewById(R.id.sciFictionBookCard);

        novelBookcard.setOnClickListener(this);
        historyBookCard.setOnClickListener(this);
        itBookCard.setOnClickListener(this);
        cookingBookCard.setOnClickListener(this);
        fictionBookCard.setOnClickListener(this);
        sciFictionBookCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == novelBookcard){
            if (!haveNetworkConnection()) {
                Toast.makeText(this, "You have no internet connection...", Toast.LENGTH_SHORT).show();
            } else {
                FetchBook fetchBook = new FetchBook(BookGenre.this, BookGenre.this,2);
                fetchBook.execute("Novel");
            }
        } else if (v == itBookCard){
            if (!haveNetworkConnection()) {
                Toast.makeText(this, "You have no internet connection...", Toast.LENGTH_SHORT).show();
            } else {
                FetchBook fetchBook = new FetchBook(BookGenre.this, BookGenre.this,2);
                fetchBook.execute("IT");
            }
        } else if (v == cookingBookCard){
            if (!haveNetworkConnection()) {
                Toast.makeText(this, "You have no internet connection...", Toast.LENGTH_SHORT).show();
            } else {
                FetchBook fetchBook = new FetchBook(BookGenre.this, BookGenre.this,2);
                fetchBook.execute("Cooking");
            }
        } else if (v == historyBookCard){
            if (!haveNetworkConnection()) {
                Toast.makeText(this, "You have no internet connection...", Toast.LENGTH_SHORT).show();
            } else {
                FetchBook fetchBook = new FetchBook(BookGenre.this, BookGenre.this,2);
                fetchBook.execute("History");
            }
        } else if (v == fictionBookCard){
            if (!haveNetworkConnection()) {
                Toast.makeText(this, "You have no internet connection...", Toast.LENGTH_SHORT).show();
            } else {
                FetchBook fetchBook = new FetchBook(BookGenre.this, BookGenre.this,2);
                fetchBook.execute("Fiction");
            }
        } else if (v == sciFictionBookCard){
            if (!haveNetworkConnection()) {
                Toast.makeText(this, "You have no internet connection...", Toast.LENGTH_SHORT).show();
            } else {
                FetchBook fetchBook = new FetchBook(BookGenre.this, BookGenre.this,2);
                fetchBook.execute("Science Fiction");
            }
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

    @Override
    public void onTaskFinish(List<List<String>> firstArrayList, List<List<String>> secondArrayList) {
        CommonClass.booklist = firstArrayList;
        CommonClass.reviewList = secondArrayList;
    }
}
