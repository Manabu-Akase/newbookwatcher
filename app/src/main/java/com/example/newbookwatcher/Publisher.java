package com.example.newbookwatcher;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "publishers")
public class Publisher {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int publisherId ;

    @ColumnInfo(name = "name")
    public String name ;

}
