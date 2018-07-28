package com.bracu.project.booklibrary.UserView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bracu.project.booklibrary.AdminView.DeleteBookAdapter;
import com.bracu.project.booklibrary.BackgroundService.FetchBook;
import com.bracu.project.booklibrary.CommonClass;
import com.bracu.project.booklibrary.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;
import java.net.URL;

public class ReadOnline extends AppCompatActivity {

    PDFView pdfView;
    private ProgressBar pdfProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_PROGRESS);

        /*class HelloWebViewClient extends WebViewClient {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        }*/
        setContentView(R.layout.fragment_read_online);
        pdfView = findViewById(R.id.pdfView);
        pdfProgressBar = findViewById(R.id.pdfProgressBar);

        Intent i = getIntent();
        final int link = i.getIntExtra("position", 0);

        WebView wv = findViewById(R.id.webview);
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        //wv.setWebViewClient(new HelloWebViewClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        wv.getSettings().setJavaScriptEnabled(true);
        final Activity activity = this;
        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setProgress(progress * 1000);
            }
        });
        wv.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pdfProgressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pdfProgressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });

        String ext = MimeTypeMap.getFileExtensionFromUrl(FetchBook.booklist.get(link).get(3));
        if (ext.equalsIgnoreCase("pdf")) {
            try {
                wv.loadUrl("https://docs.google.com/viewer?embedded=true&url=" + FetchBook.booklist.get(link).get(3));
            } catch (Exception e) {
                Toast.makeText(this, "Link Error", Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                wv.loadUrl("https://docs.google.com/viewer?embedded=true&url=" + FetchBook.booklist.get(link).get(3));
                //wv.loadUrl(FetchBook.booklist.get(link).get(3));
            } catch (Exception e) {
                wv.loadUrl(FetchBook.booklist.get(link).get(3));
            } catch (UnsatisfiedLinkError e) {
                Toast.makeText(this, "Link Error", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
