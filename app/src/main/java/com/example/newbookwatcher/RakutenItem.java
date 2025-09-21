package com.example.newbookwatcher;
import com.google.gson.annotations.SerializedName;

public class RakutenItem {
    @SerializedName("title")
    public String title;
    @SerializedName("author")
    public String author;
    @SerializedName("publisherName")
    public String publisherName;
    @SerializedName("salesDate")
    public String salesDate ;
    @SerializedName("ImageUrl")
    public String ImageUrl;
    @SerializedName("isbn")
    public String isbn;
}
