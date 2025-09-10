package com.example.newbookwatcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RakutenBookAdapter extends RecyclerView.Adapter<RakutenBookAdapter.ViewHolder> {
    private List<RakutenItem>itemList;
    private Context context ;
    public RakutenBookAdapter(List<RakutenItem>itemList,Context context){
        this.itemList = itemList;
        this.context = context;
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
    }

    @Override
    public int getItemCount(){
        return itemList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, date, publisher ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvBookTitle);
            author = itemView.findViewById(R.id.tvAuthor);
            date = itemView.findViewById(R.id.tvDate);
        }
    }
}
