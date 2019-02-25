package com.rohan.myvoice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rohan.myvoice.GlobalValues.PublicClass;
import com.rohan.myvoice.pojo.survey_details.ProjectDatum;

import java.util.List;

public class RecyclerViewAdapeter extends RecyclerView.Adapter<RecyclerViewAdapeter.MyViewHolder> {

    Context mcontext;
    List<ProjectDatum> mdata;

    public RecyclerViewAdapeter(Context mcontext, List<ProjectDatum> mdata) {
        this.mcontext = mcontext;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(mcontext).inflate(R.layout.survey_recyclerview_layout, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Glide.with(mcontext).load( mdata.get(i).getLogo()).into(myViewHolder.imageView);
        myViewHolder.title.setText(mdata.get(i).getTitle());
        myViewHolder.company.setText(mdata.get(i).getCompany());

        myViewHolder.date.setText(mdata.get(i).getDate());
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView title, company, date;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.survey_img);
            title = itemView.findViewById(R.id.survey_title);
            company = itemView.findViewById(R.id.survey_company);
            date = itemView.findViewById(R.id.survey_date);

        }
    }
}
