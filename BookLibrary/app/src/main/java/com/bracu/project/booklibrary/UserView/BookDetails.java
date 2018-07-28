package com.bracu.project.booklibrary.UserView;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bracu.project.booklibrary.AdminView.DeleteBookAdapter;
import com.bracu.project.booklibrary.BackgroundService.FetchBook;
import com.bracu.project.booklibrary.BackgroundService.FetchReview;
import com.bracu.project.booklibrary.CommonClass;
import com.bracu.project.booklibrary.R;

import java.util.ArrayList;


public class BookDetails extends Fragment {

    public static ListView lv;
    public static BookDetailsAdapter adapter;
    private Context context;
    private TextView bookName,authorName,reviewUsers;
    private Button downLoad,readOnlineButton;
    private int i;
    private Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.book_details,container,false);
        context=getActivity();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            /*Window window = getActivity().getWindow();
            window.setStatusBarColor(ContextCompat
                    .getColor(context,R.color.whitegray));*/
            int flags = view.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            i = bundle.getInt("position", 0);
        }

        bookName=view.findViewById(R.id.bookName);
        authorName=view.findViewById(R.id.authorName);
        downLoad=view.findViewById(R.id.download);
        readOnlineButton=view.findViewById(R.id.readOnlineButton);
        reviewUsers=view.findViewById(R.id.reviewUsers);

        Typeface advent_bold = Typeface.createFromAsset(context.getAssets(),"AdventPro-Bold.ttf");
        Typeface advent_semi = Typeface.createFromAsset(context.getAssets(),"AdventPro-SemiBold.ttf");
        bookName.setTypeface(advent_bold);
        authorName.setTypeface(advent_semi);
        bookName.setText(FetchBook.booklist.get(i).get(1));
        authorName.setText(FetchBook.booklist.get(i).get(2));
        if (FetchReview.reviewList.size() == 0)
            reviewUsers.setText("No Reviews Yet");

        downLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri = Uri.parse(FetchBook.booklist.get(i).get(3));
                DownloadFile downloadFile = new DownloadFile(context, uri.toString(), i);
            }
        });
        readOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ReadOnline fragment = new ReadOnline();

                Bundle bundle = new Bundle();
                bundle.putInt("position", i);
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = AllBooks.activity.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.book_genre, fragment).addToBackStack(null);
                fragmentTransaction.commit();*/
                startActivity(new Intent(context,ReadOnline.class).putExtra("position",i));
            }
        });

        //Toast.makeText(context, ""+ FetchBook.booklist.get(i).get(1), Toast.LENGTH_SHORT).show();

        lv = view.findViewById(R.id.lv);
        adapter = new BookDetailsAdapter(getActivity());
        lv.setAdapter(adapter);
        return view;
    }
}
