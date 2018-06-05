package com.alfanthariq.ciciltest.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alfanthariq.ciciltest.MovieActivity;
import com.alfanthariq.ciciltest.utils.GlideApp;
import com.alfanthariq.ciciltest.R;
import com.alfanthariq.ciciltest.pojo.MovieDetail;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

/**
 * Created by alfanthariq on 05/06/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<MovieDetail> contents;

    public MovieAdapter(Context context, List<MovieDetail> contents){
        this.context = context;
        this.contents = contents;
    }

    public void swap(List<MovieDetail> datas)
    {
        if (contents != null && contents.size()>0)
            contents.clear();
        contents.addAll(datas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list, parent, false);
        return new MovieListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MovieListHolder) {
            MovieListHolder mHolder = ((MovieListHolder) holder);

            mHolder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MovieActivity.class);
                    intent.putExtra("imdb_id", contents.get(position).getImdbID());
                    intent.putExtra("poster_url", contents.get(position).getPoster());
                    intent.putExtra("title", contents.get(position).getTitle());
                    context.startActivity(intent);
                }
            });

            mHolder.txtTitle.setText(contents.get(position).getTitle());
            mHolder.txtType.setText(contents.get(position).getType());
            mHolder.txtYear.setText(contents.get(position).getYear());
            final ProgressBar myProgress = mHolder.progress;
            GlideApp.with(context)
                    .load(contents.get(position).getPoster())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            myProgress.setVisibility(GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            myProgress.setVisibility(GONE);
                            return false;
                        }
                    })
                    .error(R.drawable.error_glide_draw)
                    .thumbnail(0.1f)
                    .into(mHolder.poster);
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    static class MovieListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.poster)
        ImageView poster;
        @BindView(R.id.txtTitle)
        TextView txtTitle;
        @BindView(R.id.txtType)
        TextView txtType;
        @BindView(R.id.txtYear)
        TextView txtYear;
        @BindView(R.id.progress)
        ProgressBar progress;
        @BindView(R.id.container)
        FrameLayout container;

        MovieListHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
