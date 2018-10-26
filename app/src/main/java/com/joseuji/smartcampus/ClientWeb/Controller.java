package com.joseuji.smartcampus.ClientWeb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller  {

    public static  final String BASE_URL= "https://ujiapps.uji.es/lod-autorest/api/datasets/";

    public RetrofitServices start() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitServices retrofitServices = retrofit.create(RetrofitServices.class);

        return retrofitServices;

    }

}

