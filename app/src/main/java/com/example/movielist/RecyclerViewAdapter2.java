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

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerViewAdapter2  extends RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter2";
    private OnNoteListener mNoteListener;
    private ArrayList<Movie> movies ;
    private Context mContext;
    ImageView poster;


    public RecyclerViewAdapter2(Context mContext, ArrayList<Movie> mMovies,OnNoteListener onNoteListener)  {
        this.movies = mMovies;
        this.mContext = mContext;
        this.mNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem2,viewGroup,false);
        ViewHolder holder = new ViewHolder(view, mNoteListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Log.d(TAG,"onBindViewHolder2: called.");
        String title;
        title = movies.get(position).name.substring(0, 1).toUpperCase() + movies.get(position).name.substring(1);

        viewHolder.imageName.setText(title);
        viewHolder.Score.setText(Integer.toString(movies.get(position).Rating));

        //img = movies.get(i).Poster;
        //loadImageFromURL(img);
        String pstr = "https://image.tmdb.org/t/p/w500/"+movies.get(position).Poster;
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


        if(movies.get(position).Rating>=8)
        {
            Glide.with(mContext).asDrawable().load(R.drawable.green_rating).into(viewHolder.image);
        }

        else if(movies.get(position).Rating>=5)
        {
            Glide.with(mContext).asDrawable().load(R.drawable.yellow_rating).into(viewHolder.image);
        }

        else{ Glide.with(mContext).asDrawable().load(R.drawable.red_rating).into(viewHolder.image);}




       // Format formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        //String s = formatter.format(movies.get(position).dateAdded.getTime());
        viewHolder.DateAdded.setText(movies.get(position).genres);
        viewHolder.overview.setText(movies.get(position).overview);

        //viewHolder.DateAdded.setText(s);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView image;
        TextView imageName;
        RelativeLayout parentLayout;
        TextView Score;
        TextView DateAdded;
        TextView overview;
        OnNoteListener onNoteListener;

        public ViewHolder(View itemView,OnNoteListener onNoteListener){
            super(itemView);
            image = itemView.findViewById(R.id.rating_image);
            imageName= itemView.findViewById(R.id.movie_name);
            parentLayout = itemView.findViewById(R.id.parent_layout2);
            Score = itemView.findViewById(R.id.score);
            DateAdded = itemView.findViewById(R.id.details);
            poster = itemView.findViewById(R.id.imageView);
            overview = itemView.findViewById(R.id.overview);
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



