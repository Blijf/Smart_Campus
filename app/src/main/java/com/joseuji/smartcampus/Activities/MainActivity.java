package com.joseuji.smartcampus.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.joseuji.smartcampus.ClientWeb.RetrofitAPI;
import com.joseuji.smartcampus.ClientWeb.RetrofitServices;
import com.joseuji.smartcampus.Models.Ubicaciones;
import com.joseuji.smartcampus.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private Ubicaciones ubicaciones;
    private TextView tvUbicaciones;
    private RetrofitServices retrofitServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofitServices = RetrofitAPI.getApi().create(RetrofitServices.class);
        //------------------------------------------------------------------------------------------
        //                                  GETS TO UJI OPENDATA
        //------------------------------------------------------------------------------------------
//        Call<Ubicaciones>ubicacionesByidCall = retrofitServices.getUbicacionesByid("ubicaciones/DAA001PA");
//
//        ubicacionesByidCall.enqueue(new Callback<Ubicaciones>() {
//            @Override
//            public void onResponse(Call<Ubicaciones> call, Response<Ubicaciones> response) {
//
//                ubicaciones= response.body();
//
//                tvUbicaciones.setText(ubicaciones.getDescripcion());
//
//            }
//
//            @Override
//            public void onFailure(Call<Ubicaciones> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "No se ha podido obtener la ubicación",Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
