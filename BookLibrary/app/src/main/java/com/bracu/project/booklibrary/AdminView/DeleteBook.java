package com.bracu.project.booklibrary.AdminView;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.bracu.project.booklibrary.BackgroundService.FetchBook;
import com.bracu.project.booklibrary.R;

import java.util.ArrayList;
import java.util.List;


public class DeleteBook extends Fragment {

    public static ListView lv;
    public static DeleteBookAdapter adapter;
    static ArrayList<String> book_name;
    static ArrayList<String> author_name;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.book_list,container,false);
        /*Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Delete Review");*/

        searchView = view.findViewById(R.id.search_bar);
        searchView.setQueryHint("Book or Author Name");

        lv = view.findViewById(R.id.lv);
        adapter = new DeleteBookAdapter(getActivity());
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
}
