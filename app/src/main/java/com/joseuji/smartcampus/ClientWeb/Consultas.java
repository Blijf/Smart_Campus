package com.joseuji.smartcampus.ClientWeb;

import android.content.Context;
import android.widget.Toast;

import com.joseuji.smartcampus.Models.Ubicacion;
import com.joseuji.smartcampus.Models.Ubicaciones;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Consultas {

    static Ubicaciones ubicaciones;
    //------------------------------------------------------------------------------------------
    //                                  GETS TO UJI OPENDATA
    //------------------------------------------------------------------------------------------

    public static Ubicaciones getUbicaciones(RetrofitServices retrofitServices, final Context context)
    {
            ubicaciones = new Ubicaciones();

            Call<Ubicaciones> call = retrofitServices.getUbicaciones();

            call.enqueue(new Callback<Ubicaciones>()
            {
                @Override
                public void  onResponse(Call<Ubicaciones> call, Response<Ubicaciones> response)
                {
                    if(response.code()==200)
                    {
                        ubicaciones = response.body();
                    }
                    else
                    {
                      Toast.makeText(context, "Servicio Web, se ha producido un error con código: "+response.code(),Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Ubicaciones> call, Throwable t)
                {
                    Toast.makeText(context, "No se ha podido obtener la ubicación",Toast.LENGTH_SHORT).show();
                }
            });

        return  ubicaciones;
    }

}
