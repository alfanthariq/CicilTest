package com.alfanthariq.ciciltest.rest;

import com.alfanthariq.ciciltest.pojo.MovieFullDetail;
import com.alfanthariq.ciciltest.pojo.MoviePojo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by alfanthariq on 05/06/2018.
 */

public interface ApiLibrary {
    @GET("/")
    Call<MoviePojo> getMovies(@QueryMap Map<String, String> params);

    @GET("/")
    Call<MovieFullDetail> getMovieDetail(@QueryMap Map<String, String> params);
}
