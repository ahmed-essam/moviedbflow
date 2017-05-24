package com.example.ahmedessam.moviedbflow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
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

public class DemoObjectFragment extends Fragment {
    public static final String ARG_OBJECT = "object";
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private final String BaseURL = "https://api.themoviedb.org/3/movie/";
    Call<MovieResponse> movieResponseCall;
    boolean internetFlag = false ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Connectivity.isConnected(getContext())){
            internetFlag = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_collection_object, container, false);
        Bundle args = getArguments();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.reecyclerview_main);

        recyclerView.setHasFixedSize(true);
        if (args.getInt(ARG_OBJECT)==1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }else{
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        }
        movieAdapter = new MovieAdapter(getContext(),args.getInt(ARG_OBJECT));
        recyclerView.setAdapter(movieAdapter);
        if (internetFlag){
            FeachMovies();

        }else {
            feachFromDB();
        }

        return rootView;
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

    @Override
    public void onStop() {
        super.onStop();
        super.onStop();
        if(!movieResponseCall.isCanceled()){
            movieResponseCall.cancel();
        }
    }
}
