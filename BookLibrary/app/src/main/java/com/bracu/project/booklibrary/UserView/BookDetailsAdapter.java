package com.bracu.project.booklibrary.UserView;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bracu.project.booklibrary.BackgroundService.FetchReview;
import com.bracu.project.booklibrary.BackgroundService.Login_BackGroundWorker;
import com.bracu.project.booklibrary.CommonClass;
import com.bracu.project.booklibrary.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

public class BookDetailsAdapter extends BaseAdapter {

    private final Context mContext;
    private final SparseBooleanArray mCollapsedStatus;
    public char firstChar;

    public BookDetailsAdapter(Context context) {
        mContext  = context;
        mCollapsedStatus = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return FetchReview.reviewList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.book_details_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.expandableTextView = (ExpandableTextView) convertView.findViewById(R.id.expand_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.profileName=convertView.findViewById(R.id.profileName);
        viewHolder.profileImageChar=convertView.findViewById(R.id.profileImageChar);
        viewHolder.date=convertView.findViewById(R.id.dateOfReview);
        viewHolder.ratingBar=convertView.findViewById(R.id.ratingOfReview);

        if (FetchReview.reviewList.get(position).get(5).equalsIgnoreCase("Male"))
            viewHolder.profileImageChar.setImageDrawable(mContext.getResources().getDrawable(R.drawable.male_user));
        else
            viewHolder.profileImageChar.setImageDrawable(mContext.getResources().getDrawable(R.drawable.female_user));

        viewHolder.profileName.setText(FetchReview.reviewList.get(position).get(4));
        viewHolder.ratingBar.setRating(Float.parseFloat(FetchReview.reviewList.get(position).get(2)));
        viewHolder.date.setText(FetchReview.reviewList.get(position).get(1)); // "date" -> date[position]
        viewHolder.ratingBar.setRating(Float.parseFloat(FetchReview.reviewList.get(position).get(2))); // 2f -> rating[position]
        viewHolder.expandableTextView.setText(FetchReview.reviewList.get(position).get(3), mCollapsedStatus, position);
        //sampleStrings[position] -> descritionofrating[position]

        return convertView;
    }


    private static class ViewHolder{
        ExpandableTextView expandableTextView;
        ImageView profileImageChar;
        TextView profileName,date,time;
        RatingBar ratingBar;
    }
}