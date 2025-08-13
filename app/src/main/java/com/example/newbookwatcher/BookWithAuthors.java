package com.example.newbookwatcher;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

//書籍1件に対して複数の著者情報を取得するクラス
public class BookWithAuthors {
    //@EmbeddedでBookの全てのフィールドを埋め込む
    @Embedded
    public Book book;
    //@RelationでRoomにリレーションを伝える
    @Relation(
            parentColumn = "bookId",
            entityColumn = "authorId",
            //@Junctionで中間テーブルを使う
            associateBy = @Junction(BookAuthorsCrossRef.class)
    )
    //リストに著者を格納
    public List<Author> authors;
}
