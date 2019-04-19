package com.e2excel.myvoice.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {
   //private static final String ROOT_URL = "http://sporel.ddns.net:9088/";
    //private static final String ROOT_URL = "http://192.168.1.202:8000/";          //eric's home server
    private static final String ROOT_URL = "http://192.168.0.106:8000/";             //e2e local server

    /**
     * Get Retrofit Instance
     */
    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
        }
}
