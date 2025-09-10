package com.example.newbookwatcher;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//APIにアクセスするクラス
public class RakutenApiClient {
    private final static String BASE_URL = "https://app.rakuten.co.jp/services/api/";

    public static RakutenBooksApi getApiService(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RakutenBooksApi.class);
    }
}
