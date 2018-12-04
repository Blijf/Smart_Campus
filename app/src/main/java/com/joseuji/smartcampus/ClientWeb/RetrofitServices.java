package com.joseuji.smartcampus.ClientWeb;

import com.joseuji.smartcampus.Models.Asignaturas;
import com.joseuji.smartcampus.Models.Ubicacion;
import com.joseuji.smartcampus.Models.Ubicaciones;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface RetrofitServices {
    @Headers
    ({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })

    /*******************************************************************************************
     *GETS
     ********************************************************************************************/
    @GET("ubicaciones?start=0&limit=10000")
    Call<Ubicaciones> getUbicaciones();

    @GET("asignaturas?start=0&limit=10000")
    Call<Asignaturas> getAsignaturas();


}

