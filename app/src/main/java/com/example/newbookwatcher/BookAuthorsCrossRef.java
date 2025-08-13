package com.example.newbookwatcher;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

//中間テーブルの作成
//複合主キーとして　bookIdとauthorIdを使う
@Entity(primaryKeys ={"bookId","authorId"})

public class BookAuthorsCrossRef {

    public int bookId ;
    public int authorId;
}
