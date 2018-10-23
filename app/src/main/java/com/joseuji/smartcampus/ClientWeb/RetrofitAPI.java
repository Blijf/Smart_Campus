package com.joseuji.smartcampus.ClientWeb;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAPI {

    public static  final String BASE_URL= "http://ujiapps.uji.es/lod-autorest/api/datasets/";
    private static Retrofit retrofit= null;

    public static retrofit2.Retrofit getApi()
    {
        if (retrofit==null)
        {
            retrofit=new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

