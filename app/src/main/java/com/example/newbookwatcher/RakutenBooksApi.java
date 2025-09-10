package com.example.newbookwatcher;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

//retrofitを使ったWeb APIへのリクエストをする際に必要なインターフェースを作成。
public interface RakutenBooksApi {
@GET("BooksBook/Search/20170404")
    Call<RakutenBookResponse> searchBooks(
            @Query("applicationId")
            String appId ,
            @Query("title")
            String title
    );
}
