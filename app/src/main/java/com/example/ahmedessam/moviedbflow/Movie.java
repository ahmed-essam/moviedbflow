package com.example.ahmedessam.moviedbflow;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;

/**
 * Created by ahmed essam on 21/05/2017.
 */
@Table(database = AppDataBase.class)
public class Movie extends BaseModel {
    @SerializedName("id")
    @PrimaryKey()
   private long id ;
    @SerializedName("poster_path")
    @Column
    private String photoUrl;
    @SerializedName("title")
    @Column
    private String title;

    public static void ClearDB(){
        List<Movie> movieList = SQLite.select().from(Movie.class).queryList();
        FlowManager.getModelAdapter(Movie.class).deleteAll(movieList);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
