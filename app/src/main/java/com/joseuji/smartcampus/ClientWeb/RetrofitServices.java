package com.joseuji.smartcampus.ClientWeb;

import com.joseuji.smartcampus.Models.Ubicaciones;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitServices {

    /*******************************************************************************************
     *GETS
     ********************************************************************************************/
    @GET("ubicaciones/")
    public Call<List<Ubicaciones>> getUbicaciones();

}

