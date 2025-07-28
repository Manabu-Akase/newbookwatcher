package com.example.newbookwatcher;


import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {

    @TypeConverter
    public static Date fromTimestemp(Long value){
        return value == null ? null : new Date(value) ;
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date){
        return date == null ? null :date.getTime() ;

    }
}
