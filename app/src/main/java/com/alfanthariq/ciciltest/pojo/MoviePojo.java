package com.alfanthariq.ciciltest.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alfanthariq on 05/06/2018.
 */

public class MoviePojo {
    @SerializedName("Search")
    @Expose
    private List<MovieDetail> search = null;
    @SerializedName("totalResults")
    @Expose
    private int totalResults;
    @SerializedName("Response")
    @Expose
    private String response;

    public List<MovieDetail> getSearch() {
        return search;
    }

    public void setSearch(List<MovieDetail> search) {
        this.search = search;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
