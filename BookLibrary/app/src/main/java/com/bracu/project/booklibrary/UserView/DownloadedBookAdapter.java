package com.bracu.project.booklibrary.UserView;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.bracu.project.booklibrary.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anik on 2/23/2018.
 */
class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public TextView bookName;
    public ImageView duaDownloadLogo;
    private ItemClickListener itemClickListener;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        bookName = itemView.findViewById(R.id.bookName);
        duaDownloadLogo = itemView.findViewById(R.id.download);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public boolean onLongClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), true);
        return true;
    }
}

public class DownloadedBookAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private Context mCtx;
    private List<String> bookList;

    public DownloadedBookAdapter(Context mCtx, List<String> bookList) {
        this.mCtx = mCtx;
        this.bookList = bookList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.download_book_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        /*Typeface ubuntu = Typeface.createFromAsset(mCtx.getAssets(), "fonts/ubuntu.ttf");
        holder.duaNameText.setTypeface(ubuntu);*/
        holder.bookName.setText(bookList.get(position));
        /*if (position < DuaList.sizeOfdatabaseSongs){
            holder.duaDownloadLogo.setBackgroundResource(R.drawable.ic_download);
        } else if (position >= DuaList.sizeOfdatabaseSongs){
            holder.duaDownloadLogo.setBackgroundResource(R.drawable.ic_download_complete);
        }*/
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                mCtx.startActivity(new Intent(mCtx,DownloadedBookRead.class).putExtra("position",position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}