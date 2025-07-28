package com.example.newbookwatcher;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "authors")
public class Author {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int authorId ;

    @ColumnInfo(name = "name")
    public String name ;

    @ColumnInfo(name = "author_kana")
    public String authorKana ;

}
