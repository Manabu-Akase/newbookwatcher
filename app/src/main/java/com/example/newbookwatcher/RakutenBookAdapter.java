package com.example.newbookwatcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RakutenBookAdapter extends RecyclerView.Adapter<RakutenBookAdapter.ViewHolder> {
    private List<RakutenItem>itemList;
    private Context context ;
    private AppDatabase db;

    public RakutenBookAdapter(List<RakutenItem>itemList,Context context,AppDatabase database){
        this.itemList = itemList;
        this.context = context;
        this.db = database;
    }
    public void updateData(List<RakutenItem> newList){
        itemList.clear();
        itemList.addAll(newList);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.book_item , parent ,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position){
        RakutenItem item = itemList.get(position);
        holder.title.setText(item.title);
        holder.author.setText(item.author);
        holder.date.setText(item.salesDate);

        //Glideで画像を読み込む処理
        //largeImageUrlがないときはsmallImageUrlを使う,どちらもない時は❌を表示
        String imageUrl = (item.largeImageUrl != null && !item.largeImageUrl.isEmpty())
                ? item.largeImageUrl
                : item.smallImageUrl;

        Glide.with(context)
                .load(imageUrl) //楽天APIから取得
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_delete)
                .into(holder.imageView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //DBからお気に入り状態を取得して反映する
                Book book = db.bookDao().findBookByIsbn(item.isbn);
                boolean isFavorite = (book != null && book.isFavorite);

                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.favoriteButton.setImageResource(
                                isFavorite ? android.R.drawable.star_on : android.R.drawable.star_off
                        );
                    }
                });
            }
        }).start();

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Book book = db.bookDao().findBookByIsbn(item.isbn);
                        final boolean newStatus;
                        Book targetBook ;

                        if(book == null){
                            Book newBook = new Book();
                            newBook.title = item.title;
                            newBook.isbn = item.isbn;
                            newBook.image_url = item.largeImageUrl != null && !item.largeImageUrl.isEmpty()
                                    ? item.largeImageUrl : item.smallImageUrl;
                            //発売日を保存
                            long rel = ReminderScheduler.parseRakutenSalesDateToMillis(item.salesDate);
                            if(rel > 0) newBook.release_date = new java.util.Date(rel);
                            newBook.isFavorite = true ;
                            db.bookDao().insertBook(newBook);
                            newStatus = true ;
                            targetBook = newBook ;

                            SharedPreferences sharedPreferences = context.getSharedPreferences("NotificationSettings",Context.MODE_PRIVATE);
                            int daysBefore = sharedPreferences.getInt("daysBefore",1);

                            if (newBook.release_date != null ){
                                long notifyTime = newBook.release_date.getTime() - daysBefore * 24L * 60L * 60L * 1000L ;
                                if(notifyTime > System.currentTimeMillis()){
                                    ReminderScheduler.scheduleReminder(context.getApplicationContext() , newBook , notifyTime);
                                }
                            }
                        } else {
                            newStatus = !book.isFavorite;
                            db.bookDao().updateFavorite(book.bookId, newStatus);
                            targetBook = book ;
                            //お気に入り登録　→ スケジュール登録、お気に入り解除　→ スケジュール解除
                            if (newStatus){
                                SharedPreferences sharedPreferences = context.getSharedPreferences("NotificationSettings",Context.MODE_PRIVATE);
                                int daysBefore = sharedPreferences.getInt("daysBefore", 1 );

                                if (book.release_date != null){
                                    long notifyTime = book.release_date.getTime() - daysBefore * 24L * 60L * 60L * 1000L ;
                                    if (notifyTime > System.currentTimeMillis()){
                                        ReminderScheduler.scheduleReminder(context.getApplicationContext(), book , notifyTime);
                                    }
                                }
                            }else{
                                ReminderScheduler.cancelBookReminder(context.getApplicationContext() , book.isbn);
                            }
                        }

                        Book finalTargetBook = targetBook ;
                        holder.itemView.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.favoriteButton.setImageResource(
                                        newStatus ? android.R.drawable.star_on : android.R.drawable.star_off
                                );
                                if (newStatus){
                                    Toast.makeText(context,"お気に入りに追加されました！",Toast.LENGTH_SHORT).show();
                                    ReminderScheduler.scheduleTestReminder(context,finalTargetBook);
                                }else{
                                    Toast.makeText(context, "お気に入りから解除されました！",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).start();
            }
        });
    }

    @Override
    public int getItemCount(){
        return itemList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, date;
        ImageButton favoriteButton;
        ImageView imageView ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvBookTitle);
            author = itemView.findViewById(R.id.tvAuthor);
            date = itemView.findViewById(R.id.tvDate);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
