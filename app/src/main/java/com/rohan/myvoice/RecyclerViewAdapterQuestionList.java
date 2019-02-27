package com.rohan.myvoice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rohan.myvoice.pojo.survey_questions_list.QuestionDatum;

import java.util.List;

public class RecyclerViewAdapterQuestionList extends RecyclerView.Adapter<RecyclerViewAdapterQuestionList.MyViewHolder> {
    Context mcontext;
    List<QuestionDatum> mdata;
    RecyclerViewAdapterQuestionList.OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int Position);
    }

    public void setOnItemClickListener(RecyclerViewAdapterQuestionList.OnItemClickListener listener) {
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

                    //AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    // Fragment myFragment = new QuestionsListFragment();
                    // Bundle b = new Bundle();
                    // b.putString("logo", mdata.get(getPosition()).getLogo());
                    // b.putString("q_title", mdata.get(getPosition()).getTitle());
                    //b.putString("q_id", String.valueOf(mdata.get(getPosition()).getId()));
                    // myFragment.setArguments(b);
                    // activity.getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_container, myFragment).addToBackStack(null).commit();

                }
            });

        }


    }
}

