package com.example.newbookwatcher;

import androidx.room.Entity;
//中間テーブルの作成
@Entity(PrimaryKeys={"bookId","authorId"})
public class BookAuthorCrossRef {

    private int bookId ;
    private int authorId;
}
