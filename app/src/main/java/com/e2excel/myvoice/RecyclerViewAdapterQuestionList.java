package com.e2excel.myvoice;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e2excel.myvoice.Fragments.QuestionTypes_Fragments.MCQFragment;
import com.e2excel.myvoice.Fragments.QuestionTypes_Fragments.OTNFragment;
import com.e2excel.myvoice.Fragments.QuestionTypes_Fragments.OTTFragment;
import com.e2excel.myvoice.Fragments.QuestionTypes_Fragments.RNKFragment;
import com.e2excel.myvoice.Fragments.QuestionTypes_Fragments.SCQFragment;
import com.e2excel.myvoice.Fragments.QuestionTypes_Fragments.SCLFragment;
import com.e2excel.myvoice.GlobalValues.PublicClass;
import com.e2excel.myvoice.pojo.survey_questions_list.QuestionDatum;

import java.util.List;

public class RecyclerViewAdapterQuestionList extends RecyclerView.Adapter<RecyclerViewAdapterQuestionList.MyViewHolder> {
    Context mcontext;
    List<QuestionDatum> mdata;
    OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int Position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;

    }

    public RecyclerViewAdapterQuestionList(Context mcontext, List<QuestionDatum> mdata) {
        this.mcontext = mcontext;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterQuestionList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(mcontext).inflate(R.layout.questionlist_recyclerview_layout, viewGroup, false);
        RecyclerViewAdapterQuestionList.MyViewHolder myViewHolder = new RecyclerViewAdapterQuestionList.MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterQuestionList.MyViewHolder myViewHolder, int i) {
        myViewHolder.q_text.setText(mdata.get(i).getQuestionText());
    }


    @Override
    public int getItemCount() {
        return mdata.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView q_text;

        public MyViewHolder(final View itemView) {
            super(itemView);

            q_text = itemView.findViewById(R.id.q_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();

                    Fragment myFragment = null;
                    String q_type = String.valueOf(mdata.get(getPosition()).getQuestionType());

                    switch (q_type) {
                        case "SCQ": {
                            myFragment = new SCQFragment();
                            break;
                        }
                        case "MCQ": {
                            myFragment = new MCQFragment();
                            break;
                        }
                        case "OTT": {
                            myFragment = new OTTFragment();
                            break;
                        }
                        case "SCL": {
                            myFragment = new SCLFragment();
                            break;
                        }
                        case "RNK": {
                            myFragment = new RNKFragment();
                            break;
                        }
                        case "OTN": {
                            myFragment = new OTNFragment();
                            break;
                        }
                    }

                    Bundle b = new Bundle();
                    b.putString("q_text", mdata.get(getPosition()).getQuestionText());
                    b.putString("q_id", mdata.get(getPosition()).getQuestionID().toString());
                    myFragment.setArguments(b);

                    //save this question ID as mainParentID in global values -> useful when we ae going to submit child question answers
                    PublicClass.MainParentID = mdata.get(getPosition()).getQuestionID().toString();

                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_container, myFragment).addToBackStack(null).commit();

                }
            });
        }
    }
}

