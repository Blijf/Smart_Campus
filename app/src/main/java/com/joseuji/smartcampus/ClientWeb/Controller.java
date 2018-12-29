package com.joseuji.smartcampus.ClientWeb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller  {

    public static final int REQUEST_CODE = 1234;
    public static final String BASE_URL = "http://geo4.dlsi.uji.es:8008/";

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

