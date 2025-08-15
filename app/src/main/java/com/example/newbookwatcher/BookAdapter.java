package com.example.newbookwatcher;
import android.util.Log;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;
import java.util.Map;

//Adapterクラスを作成
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List <BookWithAuthors> bookList;
    private Context context ;
    private Map<Integer,String>authorMap;
    private AppDatabase db ;


    //BookAdapterを作る時に使うコンストラクタ
    public BookAdapter(List<BookWithAuthors> bookList,Context context,Map<Integer,String>authorMap,AppDatabase db){
        this.bookList = bookList ;
        this.context = context ;
        this.authorMap = authorMap ;
        this.db = db ;
    }
    public void updateData(List<BookWithAuthors>newBookList) {
        bookList.clear();
        bookList.addAll(newBookList);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.book_item , parent ,false);
        return new BookViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder , int position){
        BookWithAuthors bookWithAuthors = bookList.get(position);
        Book book = bookWithAuthors.book;
        holder.textViewBookTitle.setText(book.title);
        holder.tvDate.setText(book.release_date.toString());

        //複数の著者をカンマで繋げて表示する処理

        StringBuilder sb =new StringBuilder();
        List<Author> authors = bookWithAuthors.authors;
        for (int i=0; i<authors.size();i++){
            sb.append(authors.get(i).authorName);
            if (i<authors.size()-1){
                sb.append(",");
            }
        }
        holder.tvAuthor.setText(sb.toString());

        //アイコン表示の切り替え処理（初期アイコン→変化させる）
        holder.favoriteButton.setImageResource(
                book.isFavorite ? android.R.drawable.star_on: android.R.drawable.star_off
        );

        //アイコンタップ時の処理
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newStatus = !book.isFavorite;
                book.isFavorite = newStatus;

                holder.favoriteButton.setImageResource(
                        book.isFavorite ? android.R.drawable.star_on: android.R.drawable.star_off
                );
            }
        });

        //データベースへ保存する処理　
        new Thread(new Runnable() {
            @Override
            public void run() {
                //データベースを設定→保存
                AppDatabase db = Room.databaseBuilder(
                        context.getApplicationContext(), AppDatabase.class, "book-database"
                ).build();

                db.bookDao().updateFavorite(book.bookId, book.isFavorite);
            }
        }).start();

    }
    @Override
    public int getItemCount(){
        return bookList.size();
    }
    public static class BookViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textViewBookTitle,tvDate,tvAuthor;
        ImageButton favoriteButton;
        public  BookViewHolder(@NonNull View itemView){

            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDate = itemView.findViewById(R.id.tvDate);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);

        }
    }
}