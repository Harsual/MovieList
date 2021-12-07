package com.example.movielist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private OnNoteListener mNoteListener;
    private ArrayList<Movie> movies ;
    private Context mContext;
    ImageView poster;




    public RecyclerViewAdapter(Context mContext, ArrayList<Movie> mMovies,OnNoteListener onNoteListener)  {
        this.movies = mMovies;
        this.mContext = mContext;
        this.mNoteListener = onNoteListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem,viewGroup,false);
        ViewHolder holder = new ViewHolder(view, mNoteListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Log.d(TAG,"onBindViewHolder: called.");
        String title;
        //viewHolder.mShimmerViewContainer.stopShimmer();
        //viewHolder.mShimmerViewContainer.setVisibility(View.GONE);
        if(movies.get(position).name.equals("0"))
        {
            viewHolder.mShimmerViewContainer.startShimmer();
        }

        else {

            viewHolder.Vote_Average.setText("Average vote: "+movies.get(position).vote_average);
            //viewHolder.Score.setText("Average vote: "+movies.get(position).vote_average);
            viewHolder.mShimmerViewContainer.stopShimmer();
            viewHolder.mShimmerViewContainer.setVisibility(View.GONE);
            if(movies.get(position).releaseDate.length()>3)
            {title = movies.get(position).name.substring(0, 1).toUpperCase() + movies.get(position).name.substring(1) + " (" + movies.get(position).releaseDate.substring(0, 4) + ')';}
            else{title = movies.get(position).name.substring(0, 1).toUpperCase() + movies.get(position).name.substring(1);}
            viewHolder.imageName.setText(title);


            viewHolder.Vote_Average.setVisibility(View.VISIBLE);
            viewHolder.imageName.setVisibility(View.VISIBLE);



            //viewHolder.Score.setText(Integer.toString(movies.get(position).Rating));

            //img = movies.get(i).Poster;
            //loadImageFromURL(img);
            String pstr = "https://image.tmdb.org/t/p/w500/" + movies.get(position).Poster;
            Picasso.get().load(pstr).placeholder(R.drawable.gray_poster)
                    .error(R.drawable.gray_poster)
                    .into(poster, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

            poster.setVisibility(View.VISIBLE);
        /*Format formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String s = formatter.format(movies.get(position).dateAdded.getTime());*/



        }


    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView image;
        TextView imageName;
        TextView Score;
        TextView Vote_Average;
        OnNoteListener onNoteListener;
        ShimmerFrameLayout mShimmerViewContainer;

        RelativeLayout parentLayout;

        public ViewHolder(View itemView, OnNoteListener onNoteListener){
            super(itemView);
            image = itemView.findViewById(R.id.image);
            poster = itemView.findViewById(R.id.imageView);
            poster.setVisibility(View.INVISIBLE);
            Score = itemView.findViewById(R.id.Average_vote);
            Vote_Average = itemView.findViewById(R.id.details);
            Vote_Average.setVisibility(View.INVISIBLE);
            imageName = itemView.findViewById(R.id.image_name);
            imageName.setVisibility(View.INVISIBLE);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            mShimmerViewContainer = itemView.findViewById(R.id.shimmer_view_container);
            mShimmerViewContainer.stopShimmer();
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());

        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }

}
