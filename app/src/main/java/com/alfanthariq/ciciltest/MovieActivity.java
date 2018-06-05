package com.alfanthariq.ciciltest;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfanthariq.ciciltest.pojo.MovieFullDetail;
import com.alfanthariq.ciciltest.rest.ApiLibrary;
import com.alfanthariq.ciciltest.rest.RetrofitHelper;
import com.alfanthariq.ciciltest.utils.GlideApp;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class MovieActivity extends AppCompatActivity {
    @BindView(R.id.tab_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.loading_view)
    RelativeLayout mLoadingView;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.koneksi_view)
    LinearLayout mKoneksiView;
    @BindView(R.id.btn_coba_lagi)
    Button mBtnCobaLagi;
    @BindView(R.id.txt_judul)
    TextView txtJudul;
    @BindView(R.id.txt_release)
    TextView txtRelease;
    @BindView(R.id.txt_runtime)
    TextView txtRuntime;
    @BindView(R.id.txt_genre)
    TextView txtGenre;
    @BindView(R.id.txt_rating)
    TextView txtRating;
    @BindView(R.id.txt_director)
    TextView txtDirector;
    @BindView(R.id.txt_actor)
    TextView txtActor;
    @BindView(R.id.txt_country)
    TextView txtCountry;
    @BindView(R.id.txt_lang)
    TextView txtLang;
    @BindView(R.id.txt_production)
    TextView txtProduction;
    @BindView(R.id.txt_plot)
    TextView txtPlot;
    @BindView(R.id.poster)
    ImageView imgPoster;

    private ApiLibrary api;
    private String poster_url, imdb_id;
    private String API_KEY = "a89ea0f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        txtJudul.setText(intent.getStringExtra("title"));
        poster_url = intent.getStringExtra("poster_url");
        imdb_id = intent.getStringExtra("imdb_id");

        api = RetrofitHelper.createService(ApiLibrary.class);
        setToolbar();

        GlideApp.with(getApplicationContext())
                .load(poster_url)
                .error(R.drawable.error_glide_draw)
                .thumbnail(0.1f)
                .into(imgPoster);

        GlideApp.with(getApplicationContext())
                .load(R.drawable.loading_circle)
                .error(R.drawable.error_glide_draw)
                .into(mImgLoading);

        getDetailMovie(imdb_id);

        mBtnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDetailMovie(imdb_id);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar(){
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar!=null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }
    }

    public void showLoading(boolean stat){
        mKoneksiView.setVisibility(View.GONE);
        if (stat) {
            mLoadingView.setVisibility(View.VISIBLE);
        } else {
            mLoadingView.setVisibility(View.GONE);
        }
    }

    private void getDetailMovie(String imdbId){
        showLoading(true);
        Map<String, String> data = new HashMap<>();
        data.put("apikey", API_KEY);
        data.put("i", imdbId);
        data.put("plot", "full");
        Call<MovieFullDetail> call = api.getMovieDetail(data);
        call.enqueue(new Callback<MovieFullDetail>() {
            @Override
            public void onResponse(Call<MovieFullDetail> call, Response<MovieFullDetail> response) {
                if (response.body()!=null){
                    if (response.body().getResponse().equals("True")){
                        MovieFullDetail data = response.body();
                        txtActor.setText(data.getActors());
                        txtCountry.setText(data.getCountry());
                        txtDirector.setText(data.getDirector());
                        txtGenre.setText(data.getGenre());
                        txtLang.setText(data.getLanguage());
                        txtPlot.setText(data.getPlot());
                        txtProduction.setText(data.getProduction());
                        txtRating.setText(data.getImdbRating());
                        txtRelease.setText(data.getReleased());
                        txtRuntime.setText(data.getRuntime());
                    }
                }
                showLoading(false);
                mKoneksiView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieFullDetail> call, Throwable t) {
                mKoneksiView.setVisibility(View.VISIBLE);
            }
        });
    }
}
