package com.rohan.myvoice;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rohan.myvoice.Fragments.QuestionsListFragment;
import com.rohan.myvoice.pojo.survey_details.ProjectDatum;

import java.util.List;

public class RecyclerViewAdapterSurveyList extends RecyclerView.Adapter<RecyclerViewAdapterSurveyList.MyViewHolder> {

    Context mcontext;
    List<ProjectDatum> mdata;
    OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int Position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public RecyclerViewAdapterSurveyList(Context mcontext, List<ProjectDatum> mdata) {
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
        Glide.with(mcontext).load(mdata.get(i).getLogo()).into(myViewHolder.imageView);
        myViewHolder.title.setText(mdata.get(i).getTitle());
        myViewHolder.company.setText(mdata.get(i).getCompany());
        myViewHolder.date.setText(mdata.get(i).getDate());
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }


    public void clearData() {


        RecyclerViewAdapterSurveyList.this.notifyDataSetChanged(); // let your adapter know about the changes and reload view.
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView title, company, date;

        public MyViewHolder(final View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.survey_img);
            title = itemView.findViewById(R.id.survey_title);
            company = itemView.findViewById(R.id.survey_company);
            date = itemView.findViewById(R.id.survey_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment myFragment = new QuestionsListFragment();
                    Bundle b = new Bundle();
                    b.putString("logo", mdata.get(getPosition()).getLogo());
                    b.putString("q_title", mdata.get(getPosition()).getTitle());
                    b.putString("q_id", String.valueOf(mdata.get(getPosition()).getId()));
                    myFragment.setArguments(b);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_container, myFragment).addToBackStack(null).commit();

                }
            });
        }
    }
}
