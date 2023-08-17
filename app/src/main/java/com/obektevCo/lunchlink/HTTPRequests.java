package com.obektevCo.lunchlink;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HTTPRequests {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://timeapi.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
}
