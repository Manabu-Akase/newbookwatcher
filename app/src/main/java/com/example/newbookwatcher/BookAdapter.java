package com.example.newbookwatcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List <Book> bookList;
    private Context context ;
    private Map<Integer,String>authorMap;

    public BookAdapter(List<Book> bookList,Context context,Map<Integer,String>authorMap){
        this.bookList = bookList ;
        this.context = context ;
        this.authorMap = authorMap ;
    }
    public void updateData(List<Book>newBookList) {
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
        Book book = bookList.get(position);
        holder.textViewBookTitle.setText(book.title);
        holder.tvDate.setText(book.release_date.toString());

        String authorName = authorMap.get(book.authorId);
        holder.tvAuthor.setText(authorName != null ? authorName:"不明な著者");
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
            textViewBookTitle = itemView.findViewById(R.id.textViewBookTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);

        }
    }
}