package com.bracu.project.booklibrary.AdminView;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bracu.project.booklibrary.BackgroundService.DeleteBookBackWorker;
import com.bracu.project.booklibrary.BackgroundService.FetchBook;
import com.bracu.project.booklibrary.R;

import java.util.ArrayList;
import java.util.List;

public class DeleteBookAdapter extends BaseAdapter implements Filterable{

    private final Context mContext;
    private int selectedRow;
    ValueFilter valueFilter;
    List<List<String>> bookList;
    List<List<String>> bookCheckList;
    private int finalPos;

    public DeleteBookAdapter(Context context) {
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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final DeleteBookAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.delete_book_item, parent, false);
            viewHolder = new DeleteBookAdapter.ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DeleteBookAdapter.ViewHolder) convertView.getTag();
        }

        final Animation animation = AnimationUtils.loadAnimation(mContext,
                R.anim.slide_out_bottom);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                for (int i = 0 ; i < FetchBook.booklist.size(); i++){
                    if (bookList.get(position).get(0).equalsIgnoreCase(FetchBook.booklist.get(i).get(0)))
                        finalPos = i;
                }
                FetchBook.booklist.remove(finalPos);
                bookList = new ArrayList<>();
                bookList = FetchBook.booklist;
                bookCheckList = FetchBook.booklist;
                notifyDataSetChanged();
            }
        });


        viewHolder.bookName=convertView.findViewById(R.id.bookName);
        viewHolder.bookIcon=convertView.findViewById(R.id.bookIcon);
        viewHolder.authorName=convertView.findViewById(R.id.authorName);
        viewHolder.deleteBook=convertView.findViewById(R.id.deleteBook);

        viewHolder.bookName.setText(bookList.get(position).get(1));
        viewHolder.authorName.setText(bookList.get(position).get(2));
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

        viewHolder.deleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Delete Book?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DeleteBookBackWorker deleteBookBackWorker=new DeleteBookBackWorker(mContext);
                                int value= Integer.parseInt(bookList.get(position).get(0));
                                deleteBookBackWorker.execute("delete",""+value);
                                parent.getChildAt(position).startAnimation(animation);
                                selectedRow=position;
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                /*PopupMenu popup = new PopupMenu(mContext, viewHolder.profileImageChar);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(
                                mContext,
                                "You Clicked : " + item.getTitle(),
                                Toast.LENGTH_SHORT
                        ).show();
                        return true;
                    }
                });
                popup.show();*/
            }
        });
        return convertView;
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
        ImageView bookIcon;
        TextView bookName,authorName;
        Button deleteBook;
    }
}
