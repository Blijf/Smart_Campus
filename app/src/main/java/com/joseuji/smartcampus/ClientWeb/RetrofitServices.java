package com.joseuji.smartcampus.ClientWeb;

import com.joseuji.smartcampus.Models.Asignaturas;
import com.joseuji.smartcampus.Models.Ubicacion;
import com.joseuji.smartcampus.Models.Ubicaciones;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitServices {
    /*******************************************************************************************
     *GETS
     ********************************************************************************************/
    @Headers
    ({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    @GET("ubicaciones/busqueda")
    Call<Ubicaciones> getLugares(@Query("consulta") String consulta);






    @Headers
            ({
                    "Content-Type: application/json;charset=utf-8",
                    "Accept: application/json"
            })
    @GET("asignaturas?start=0&limit=10000")
    Call<Asignaturas> getAsignaturas();


}

