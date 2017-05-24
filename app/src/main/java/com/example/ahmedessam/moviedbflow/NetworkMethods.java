package com.example.ahmedessam.moviedbflow;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by ahmed essam on 21/05/2017.
 */

public interface NetworkMethods {
    @GET("popular")
    Call<MovieResponse> getAllMovies();
}
