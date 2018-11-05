package com.joseuji.smartcampus.ClientWeb;

import com.joseuji.smartcampus.Models.Ubicaciones;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RetrofitServices {

    /*******************************************************************************************
     *GETS
     ********************************************************************************************/
    @GET("")
    Call<List<Ubicaciones>> getUbicaciones(@Query("descripcion") int des);



}

