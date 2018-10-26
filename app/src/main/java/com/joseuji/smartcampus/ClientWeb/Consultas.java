package com.joseuji.smartcampus.ClientWeb;

import android.content.Context;
import android.widget.Toast;

import com.joseuji.smartcampus.Models.Ubicaciones;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Consultas {

    //------------------------------------------------------------------------------------------
    //                                  GETS TO UJI OPENDATA
    //------------------------------------------------------------------------------------------

    public static List<Ubicaciones>getUbicaciones(RetrofitServices retrofitServices, final Context context)
    {
        final List<Ubicaciones>ubicaciones= new ArrayList<Ubicaciones>();

        Call<List<Ubicaciones>> ubicacionesCall = retrofitServices.getUbicaciones();

        ubicacionesCall.enqueue(new Callback<List<Ubicaciones>>()  {
            @Override
            public void onResponse(Call<List<Ubicaciones>> call, Response<List<Ubicaciones>> response)
            {
                if(response.code()==200)
                {

                    for(Ubicaciones ubi:response.body())
                    {
                        ubicaciones.add(ubi);
                    }
                }
                else
                {
                  Toast.makeText(context, "Servicio Web, se ha producido un error con código: "+response.code(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ubicaciones>> call, Throwable t)
            {
                Toast.makeText(context, "No se ha podido obtener la ubicación",Toast.LENGTH_SHORT).show();
            }
        });

        return  ubicaciones;
    }

}
