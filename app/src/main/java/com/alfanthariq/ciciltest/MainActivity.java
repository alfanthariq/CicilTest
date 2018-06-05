package com.alfanthariq.ciciltest;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfanthariq.ciciltest.adapter.MovieAdapter;
import com.alfanthariq.ciciltest.pojo.MovieDetail;
import com.alfanthariq.ciciltest.pojo.MoviePojo;
import com.alfanthariq.ciciltest.rest.ApiLibrary;
import com.alfanthariq.ciciltest.rest.RetrofitHelper;
import com.alfanthariq.ciciltest.utils.EndlessRecyclerViewScrollListener;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.txt_tes)
    TextView txt_tes;
    @BindView(R.id.up_fab)
    FloatingActionButton up_fab;
    @BindView(R.id.recyclerMovie)
    RecyclerView recyclerMovie;
    @BindView(R.id.empty_view)
    RelativeLayout emptyView;

    private ApiLibrary api;
    private MovieAdapter adapter;
    private List<MovieDetail> movies;
    private SearchView searchView;
    private EndlessRecyclerViewScrollListener endlessListener;
    private int last_page, totalPage;
    private String last_query;
    private String API_KEY = "a89ea0f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Iconify.with(new FontAwesomeModule());

        api = RetrofitHelper.createService(ApiLibrary.class);

        IconDrawable icon = new IconDrawable(this, "fa-angle-double-up");
        if (up_fab.getSize() == FloatingActionButton.SIZE_AUTO) {
            icon.sizeDp(18);
            icon.colorRes(R.color.colorWhite);
            up_fab.setScaleType(ImageView.ScaleType.CENTER);
        }
        up_fab.setImageDrawable(icon);
        up_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerMovie.smoothScrollToPosition(0);
            }
        });
        setupRecycler();
        last_page=1;
        last_query = "america";

        Map<String, String> data = new HashMap<>();
        data.put("apikey", API_KEY);
        data.put("s", last_query);
        data.put("page", String.valueOf(last_page));
        getMovies(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.main_menu, menu);

        final MenuItem myActionMenuItem = menu.findItem( R.id.mSearch);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                last_page=1;
                last_query = query;
                Map<String, String> data = new HashMap<>();
                data.put("apikey", API_KEY);
                data.put("s", last_query);
                data.put("page", String.valueOf(last_page));
                getMovies(data);
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.mSearch).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_search)
                        .colorRes(R.color.colorWhite)
                        .sizeDp(20)).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    private void setupRecycler(){
        GridLayoutManager gm = new GridLayoutManager(this, 2);
        recyclerMovie.setLayoutManager(gm);
        movies = new ArrayList<>();
        adapter = new MovieAdapter(this, movies);
        recyclerMovie.setAdapter(adapter);
        endlessListener = new EndlessRecyclerViewScrollListener(gm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                last_page += 1;
                double page_load = 10.0;
                int maxPage = (int) Math.ceil((double) totalPage / page_load);
                if (last_page<=maxPage) {
                    Map<String, String> data = new HashMap<>();
                    data.put("apikey", API_KEY);
                    data.put("s", last_query);
                    data.put("page", String.valueOf(last_page));
                    loadMoreMovies(data);
                    Log.d("MainActivity", "Load Page ".concat(String.valueOf(last_page)));
                }
            }
        };
        recyclerMovie.addOnScrollListener(endlessListener);
    }

    private void getMovies(Map<String, String> data){
        Call<MoviePojo> call = api.getMovies(data);
        call.enqueue(new Callback<MoviePojo>() {
            @Override
            public void onResponse(Call<MoviePojo> call, Response<MoviePojo> response) {
                if (response.body()!=null){
                    if (response.body().getResponse().equals("True")){
                        totalPage = Integer.valueOf(response.body().getTotalResults());
                        List<MovieDetail> data = response.body().getSearch();
                        if (data.size()>0) {
                            adapter.swap(data);
                            showEmpty(false);
                        } else {
                            showEmpty(true);
                        }
                    } else {
                        showEmpty(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviePojo> call, Throwable t) {

            }
        });
    }

    private void loadMoreMovies(Map<String, String> data){
        Call<MoviePojo> call = api.getMovies(data);
        call.enqueue(new Callback<MoviePojo>() {
            @Override
            public void onResponse(Call<MoviePojo> call, Response<MoviePojo> response) {
                if (response.body()!=null){
                    if (response.body().getResponse().equals("True")){
                        List<MovieDetail> data = response.body().getSearch();
                        movies.addAll(data);
                        adapter.notifyDataSetChanged();
                    } else {
                        showEmpty(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviePojo> call, Throwable t) {

            }
        });
    }

    private void showEmpty(boolean stat){
        if (stat) {
            up_fab.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            up_fab.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
