package com.joseuji.smartcampus.ClientWeb;

import android.content.Context;
import android.widget.Toast;

import com.joseuji.smartcampus.Models.Asignaturas;
import com.joseuji.smartcampus.Models.Ubicacion;
import com.joseuji.smartcampus.Models.Ubicaciones;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Consultas {

    public static Ubicaciones ubicaciones;
    public static Asignaturas asignaturas;

    public static double longitud;
    public static double latitud;
    public static double altitud;
    //------------------------------------------------------------------------------------------
    //                                  GETS TO UJI OPENDATA
    //------------------------------------------------------------------------------------------

    public static void getUbicaciones(RetrofitServices retrofitServices, final Context context, String textobusqueda)
    {
            ubicaciones = new Ubicaciones();

            Call<Ubicaciones> call = retrofitServices.getLugares(textobusqueda);

            call.enqueue(new Callback<Ubicaciones>()
            {
                @Override
                public void  onResponse(Call<Ubicaciones> call, Response<Ubicaciones> response)
                {
                    if(response.code()==200)
                    {
                        ubicaciones = response.body();
                        longitud= Consultas.ubicaciones.getDatos().get(0).getLocalizacion().getLatitud();
                        latitud= Consultas.ubicaciones.getDatos().get(0).getLocalizacion().getLongitud();
                        //altitud=Consultas.ubicaciones.getDatos().get(0).getLocalizacion().getAltitud();
                        altitud=0.0;
                    }
                    else
                    {
                      Toast.makeText(context, "Servicio Web (Ubicación), se ha producido un error con código: "+response.code(),Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Ubicaciones> call, Throwable t)
                {
                    Toast.makeText(context, "No se ha podido obtener la ubicación",Toast.LENGTH_SHORT).show();
                }
            });

    }

    public static Asignaturas getAsignaturas(RetrofitServices retrofitServices, final Context context)
    {
        asignaturas = new Asignaturas();

        Call<Asignaturas> call = retrofitServices.getAsignaturas();

        call.enqueue(new Callback<Asignaturas>()
        {
            @Override
            public void  onResponse(Call<Asignaturas> call, Response<Asignaturas> response)
            {
                if(response.code()==200)
                {
                    asignaturas = response.body();
                }
                else
                {
                    Toast.makeText(context, "Servicio Web (Asignatura), se ha producido un error con código: "+response.code(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Asignaturas> call, Throwable t)
            {
                Toast.makeText(context, "No se ha podido obtener la asignatura",Toast.LENGTH_SHORT).show();
            }
        });

        return  asignaturas;
    }
}
