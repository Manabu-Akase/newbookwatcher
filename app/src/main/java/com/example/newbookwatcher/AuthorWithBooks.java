package com.example.newbookwatcher;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;
//著者1人に対して複数の書籍情報を取得するクラス
public class AuthorWithBooks {
    @Embedded
    public Author author;
    @Relation(
            parentColumn = "authorId",
            entityColumn = "bookId",
            associateBy = @Junction(BookAuthorsCrossRef.class)
    )
    public List<Book> books;
}
