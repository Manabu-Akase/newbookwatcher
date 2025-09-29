package com.example.newbookwatcher;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RakutenBookResponse {
    @SerializedName("Items")
    public List<RakutenBookWrapper>Items;
}
