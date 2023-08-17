package com.obektevCo.lunchlink;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {
    @GET("Time/current/zone?timeZone=Europe/Minsk")
    Call<Map<String, String>> getDate();
}
