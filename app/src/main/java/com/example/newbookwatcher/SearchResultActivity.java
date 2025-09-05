package com.example.newbookwatcher;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;

public class SearchResultActivity extends AppCompatActivity {
    private BookAdapter bookAdapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.search_result);
        //戻るボタンが押された時の処理
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //お気に入り一覧ボタンが押された時の処理
        Button favoriteListButton =findViewById(R.id.favoriteListButton);
        favoriteListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResultActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });

        //データベースを入れる
        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "book-database")
                .fallbackToDestructiveMigration()
                .build();

        //リサイクラービューの処理
        RecyclerView recyclerView = findViewById (R.id.recyclerViewResults);

        bookAdapter = new BookAdapter(new ArrayList<>(),this,db);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookAdapter);

        //テスト用の本のデータをデータベースに登録する
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Book>books=db.bookDao().getBookByExactTitle("サンプル本");
                if (books.isEmpty()) {

                //サンプル用の本を作成、Idを取得
                Book testBook = new Book();
                testBook.title = "サンプル本";
                long bookIdLong = db.bookDao().insertBook(testBook);
                int bookId =(int)bookIdLong;

                /*BookWithAuthorsから取り出すため削除
                testBook.authorId = 1;
                 */
                //著者を登録して authorIdを取得
                Author author = new Author();
                author.authorName = "佐藤太郎";
                author.authorKana = "サトウタロウ";
                long authorLong =db.authorDao().insert(author);
                int authorId = (int)authorLong;

                testBook.publisherId = 1;
                testBook.release_date = new Date();
                testBook.image_url = "https://example.com/sample.jpg";
                testBook.added_date = new Date();

                //BookAuthorsCrossRefに登録
                BookAuthorsCrossRef ref = new BookAuthorsCrossRef();
                ref.bookId = bookId;
                ref.authorId = authorId;
                db.bookDao().insertBookAuthorsCrossRef(ref);

                /*検索前はリストが空、検索した後に結果表示
                List<BookWithAuthors>result = db.bookDao().searchBookWithAuthorsByTitle("サンプル");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookAdapter.updateData(result);
                    }
                });
                 */
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String keyword = getIntent().getStringExtra("keyword");
                        String likekeyword = "%"+ keyword + "%";
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<BookWithAuthors>result = db.bookDao().searchBookWithAuthorsByTitle(likekeyword);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bookAdapter.updateData(result);
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
        }).start();

        //テスト用に２冊目の本を登録
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.d("MY_LOG", "スレッド2冊目開始");
                String keyword = "%サンプル%";
                List<Book>books=db.bookDao().searchBookByTitle(keyword);
                if (books.isEmpty()) {

                    //サンプル用の本を作成、Idを取得
                    Book testBook2 = new Book();
                    testBook2.title = "サンプル本２";
                    testBook2.publisherId = 2;
                    testBook2.release_date = new Date();
                    testBook2.added_date = new Date();
                    testBook2.image_url = "https://example.com/sample.jpg";

                    //著者を登録して authorIdを取得
                    Author author2 = new Author();
                    author2.authorName = "田中花子";
                    author2.authorKana = "タナカハナコ";
                    long authorLong =db.authorDao().insert(author2);
                    int authorId = (int)authorLong;

                    //本を登録
                    long bookIdLong = db.bookDao().insertBook(testBook2);
                    int bookId =(int)bookIdLong;

                    //BookAuthorsCrossRefに登録
                    BookAuthorsCrossRef ref = new BookAuthorsCrossRef();
                    ref.bookId = bookId;
                    ref.authorId = authorId;
                    db.bookDao().insertBookAuthorsCrossRef(ref);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("MY_LOG", "スレッド3冊目開始");
                String keyword = "%サンプル%";
                List<Book>books=db.bookDao().getBookByExactTitle("サンプル本3");
                if (books.isEmpty()) {

                    //サンプル用の本を作成、Idを取得
                    Book testBook3 = new Book();
                    testBook3.title = "サンプル本3";
                    testBook3.publisherId = 3;
                    testBook3.release_date = new Date();
                    testBook3.added_date = new Date();
                    testBook3.image_url = "https://example.com/sample.jpg";

                    //著者を登録して authorIdを取得
                    Author author3 = new Author();
                    author3.authorName = "佐藤次郎";
                    author3.authorKana = "サトウジロウ";
                    long authorLong =db.authorDao().insert(author3);
                    int authorId = (int)authorLong;

                    //本を登録
                    long bookIdLong = db.bookDao().insertBook(testBook3);
                    int bookId =(int)bookIdLong;

                    //BookAuthorsCrossRefに登録
                    BookAuthorsCrossRef ref = new BookAuthorsCrossRef();
                    ref.bookId = bookId;
                    ref.authorId = authorId;
                    db.bookDao().insertBookAuthorsCrossRef(ref);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("MY_LOG", "スレッド4冊目開始");
                String keyword = "%サンプル%";
                List<Book>books=db.bookDao().getBookByExactTitle("サンプル本4");
                if (books.isEmpty()) {

                    //サンプル用の本を作成、Idを取得
                    Book testBook4 = new Book();
                    testBook4.title = "サンプル本4";
                    testBook4.publisherId = 4;
                    testBook4.release_date = new Date();
                    testBook4.added_date = new Date();
                    testBook4.image_url = "https://example.com/sample.jpg";

                    //著者を登録して authorIdを取得
                    Author author4 = new Author();
                    author4.authorName = "鈴木大";
                    author4.authorKana = "スズキマサル";
                    long authorLong =db.authorDao().insert(author4);
                    int authorId = (int)authorLong;

                    //本を登録
                    long bookIdLong = db.bookDao().insertBook(testBook4);
                    int bookId =(int)bookIdLong;

                    //BookAuthorsCrossRefに登録
                    BookAuthorsCrossRef ref = new BookAuthorsCrossRef();
                    ref.bookId = bookId;
                    ref.authorId = authorId;
                    db.bookDao().insertBookAuthorsCrossRef(ref);
                }
            }
        }).start();

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                List<Book> allBooks = db.bookDao().getAllBooks();
                for (Book b : allBooks){
                    Log.d("Book_LOG","登録本"+b.title);
                }
            }
        }).start();
         */

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                List<BookWithAuthors> result = db.bookDao().searchBookWithAuthorsByTitle("%サンプル%");
                for (BookWithAuthors bookWithAuthors : result) {
                    Log.d("Check", "ヒットした本のタイトル" + bookWithAuthors.book.title);
                }
            }
        }).start();
         */

        // メニュー画面から検索された本のタイトルをテキストビューで表示する処理
        TextView SearchResultTitle = findViewById(R.id.tvSearchTitle);
        //キーワードを取得して表示
        String keyword = getIntent().getStringExtra("keyword");
        SearchResultTitle.setText("検索結果:"+keyword);


        /*検索欄にタイトルを入れる＋検索ボタンを押した時の処理 →入力したキーワードで本を探す
        → 結果を画面のリストに表示する。 */

        Button searchButton = findViewById(R.id.SearchButton);
        EditText searchBox = findViewById(R.id.editTextSearch);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //入力されたキーワードの取得
                String keyword = searchBox.getText().toString().trim();
                String likekeyword = "%"+keyword+"%";

                //スレッドでタイトルにキーワードが含まれる本を検索する
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<BookWithAuthors> result = db.bookDao().searchBookWithAuthorsByTitle(likekeyword);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bookAdapter.updateData(result);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    //他の画面から戻ってきたときに再読み込みする処理→画面に戻った際に再検索
    @Override
    protected void onResume(){
        super.onResume();

        String keyword= getIntent().getStringExtra("keyword");
        String likekeyword = "%"+ keyword + "%";

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<BookWithAuthors>result = db.bookDao().searchBookWithAuthorsByTitle(likekeyword);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookAdapter.updateData(result);
                    }
                });
            }
        }).start();
    }
}