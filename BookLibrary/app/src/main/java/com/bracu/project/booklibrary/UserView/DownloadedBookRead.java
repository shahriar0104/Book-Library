package com.bracu.project.booklibrary.UserView;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bracu.project.booklibrary.BackgroundService.FetchBook;
import com.bracu.project.booklibrary.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class DownloadedBookRead extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_book_read);

        Integer pageNumber = 0;

        pdfView = findViewById(R.id.pdfView);

        Intent i = getIntent();
        final int link = i.getIntExtra("position", 0);

        pdfView.fromFile(DownloadedBook.dirFiles[link])
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10)
                .onPageError(this)
                .pageFitPolicy(FitPolicy.BOTH)
                .load();

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void onPageError(int page, Throwable t) {

    }
}
