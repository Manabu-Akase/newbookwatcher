package com.example.newbookwatcher;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "authors")
public class Author {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name="authorId")
    public int authorId ;

    @ColumnInfo(name = "authorName")
    public String authorName ;

    @ColumnInfo(name = "author_kana")
    public String authorKana ;

}
