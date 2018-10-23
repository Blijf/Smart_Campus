package com.joseuji.smartcampus.ClientWeb;

import com.joseuji.smartcampus.Models.Ubicaciones;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface RetrofitServices {

    /*******************************************************************************************
     *GETS
     ********************************************************************************************/
    @GET("ubicaciones/{id}/")
    public Call<Ubicaciones> getUbicacionesByid(@Path("id") String _id);

}

