package com.example.ahmedessam.moviedbflow;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmed essam on 21/05/2017.
 */

public class MovieResponse {
    @SerializedName("results")
    List<Movie> movies;


}
