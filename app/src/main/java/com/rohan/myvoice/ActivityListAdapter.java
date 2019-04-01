package com.rohan.myvoice;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rohan.myvoice.pojo.activity_details.AnswerDatum;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class ActivityListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Activity_tab_obj[] answer_objects;

    //private String[] que_ary;
    private LayoutInflater inflater;
    private static int k = 0;

    public ActivityListAdapter(Context context, Activity_tab_obj[] answer_objects) {
        inflater = LayoutInflater.from(context);
        this.answer_objects = answer_objects;
    }

    @Override
    public int getCount() {
        return answer_objects.length;
    }

    @Override
    public Object getItem(int position) {
        return answer_objects[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_tab_list_item_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.ans = (TextView) convertView.findViewById(R.id.ans);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(answer_objects[position].question);
        holder.ans.setText(answer_objects[position].ans);

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.activity_tab_header_item_layout, parent, false);
            holder.text = convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        //set activity_header_item_layout text as first char in name
        String headerText = answer_objects[position].survey_title;//"" + countries[position].subSequence(0, 1).charAt(0);

        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        String header = answer_objects[position].survey_title.toString().toLowerCase().trim().replace(" ", "");
        String header_2 = header.substring(0, 4);
        long r = Long.parseLong(header_2, 36);
        Log.v("header ID", "" + r);
        return r;                           //countries[position].subSequence(0, 1).charAt(0);

    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView text, ans;

    }

}