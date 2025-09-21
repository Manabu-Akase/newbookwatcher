package com.example.newbookwatcher;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import java.util.List;

@Dao
public interface BookDao {
    @Insert
    long insertBook(Book book);
    @Insert
    void insertBookAuthorsCrossRef(BookAuthorsCrossRef ref);
    @Update
    void update(Book book);
    @Delete
    void delete(Book book);
    @Query("SELECT * FROM books ORDER BY added_date DESC")
    List<Book> getAllBooks();
    @Query("SELECT * FROM books WHERE title LIKE '%' || :keyword || '%'")
    List<Book> searchBookByTitle(String keyword);
    @Query("SELECT * FROM books WHERE isFavorite = 1 ")
    List<Book> getFavoriteBooks();
    @Query("UPDATE books SET isFavorite = :isFavorite WHERE bookId = :bookId")
    void updateFavorite(int bookId, boolean isFavorite);
    //指定されたタイトルと完全に一致する本を検索するクエリ
    @Query("SELECT * FROM books WHERE title = :title")
    List<Book> getBookByExactTitle(String title);
    //お気に入りの本を取得するクエリ
    @Transaction
    @Query("SELECT * FROM books WHERE isFavorite = 1 ")
    List<BookWithAuthors>getFavoriteBooksWithAuthors();
    //著者付きの検索メソッド
    @Transaction
    @Query("SELECT * FROM books WHERE title LIKE '%'||:keyword|| '%'")
    List<BookWithAuthors>searchBookWithAuthorsByTitle(String keyword);

    @Query("SELECT * FROM books WHERE isbn = :isbn LIMIT 1 ")
    Book findBookByIsbn(String isbn);

}
