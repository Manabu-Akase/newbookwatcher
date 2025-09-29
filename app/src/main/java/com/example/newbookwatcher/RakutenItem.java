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
    @SerializedName("isbn")
    public String isbn;
    @SerializedName("largeImageUrl")
    public String largeImageUrl;
    @SerializedName("smallImageUrl")
    public String smallImageUrl;
}
