package com.example.ahmedessam.moviedbflow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private final String BaseURL = "https://api.themoviedb.org/3/movie/";
    Call<MovieResponse> movieResponseCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        recyclerView = (RecyclerView) findViewById(R.id.reecyclerview_main);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MovieListActivity.this));
        movieAdapter = new MovieAdapter(MovieListActivity.this);
        recyclerView.setAdapter(movieAdapter);

        if (Connectivity.isConnected(this)){

            FeachMovies();

        }else{
            feachFromDB();

        }
    }

    public void FeachMovies() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request requestBuilder = chain.request();
                HttpUrl originalHttpUrl = requestBuilder.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key", "3284b4b68f95a2b481b8f180ef10cbe5")
                        .build();
                return chain.proceed(requestBuilder.newBuilder().url(url).build());
            }
        };
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(httpLoggingInterceptor);
        okHttpClient.addInterceptor(interceptor);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build()).build();
        NetworkMethods networkMethods = retrofit.create(NetworkMethods.class);
        movieResponseCall = networkMethods.getAllMovies();
        movieResponseCall.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if(response.body() != null) {
                    MovieResponse movieResponse = response.body();
                    List<Movie> movies = movieResponse.movies;
                    Log.e("response", "onResponse: " + movies.size());
                    movieAdapter.addAll(movies);

                    insertToDataBase(movies);
                }else{
                    Log.e("response body", "onResponse: empty response");
                }

            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("retrofit", "onFailure: no response");

            }
        });


    }

//    public void updateView(List<Movie> movieList) {
//        movieAdapter = new MovieAdapter(movieList);
//        recyclerView.setAdapter(movieAdapter);
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!movieResponseCall.isCanceled()){
            movieResponseCall.cancel();
        }

    }
    public void insertToDataBase(List<Movie> movieList)  {
        Log.e("insert", "insertToDataBase: "+movieList.size());
        if(movieList.size()==0){
            Log.e("empty list", "insertToDataBase: "+movieList.size() );
        }
        else{

            Movie.ClearDB();
            for(int i=0 ;i <movieList.size();i++){
                Movie movie = movieList.get(i);
                movie.save();
                Log.e("for", "inserttodb: "+i );
            }

        }
    }
    public void feachFromDB(){
        Log.e("dbsize", "feachFromDB: enter" );
        List<Movie> movies = SQLite.select().from(Movie.class).queryList();
        Log.e("db size", "feachFromDB: "+movies.size() );
        movieAdapter.addAll(movies);
    }



}
