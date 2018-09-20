package com.camerrow.camerrowproject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter <SearchAdapter.SearchBarViewHolder>{

    Context context;
    private ArrayList<CamerrowUser> camerrowUserArrayList;
    private final ItemClickListener listener;



    class SearchBarViewHolder extends RecyclerView.ViewHolder{


        View itemView;
        private ImageView profileImage;
        private TextView name;
        private TextView username;



        public SearchBarViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.searchItemName);
            username = (TextView) itemView.findViewById(R.id.searchItemUsername);
            profileImage = (ImageView) itemView.findViewById(R.id.searchItemProfileImage);



            this.itemView = itemView;
        }



        public void setName(String name) {
            this.name.setText(name);
        }

        public void setUsername(String username) {
            this.username.setText(username);
        }


        public void bind(final CamerrowUser camerrowUser, final int position, final ItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(itemView, position, false);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onClick(itemView, position, true);
                    return true;
                }
            });
        }
    }

    public SearchAdapter(Context context, ArrayList<CamerrowUser> camerrowUserArrayList, ItemClickListener listener) {
        this.context = context;
        this.camerrowUserArrayList = camerrowUserArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchBarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_item, viewGroup, false);
        return new SearchAdapter.SearchBarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchBarViewHolder holder, final int position) {

        holder.name.setText(camerrowUserArrayList.get(position).getName());
        holder.username.setText(camerrowUserArrayList.get(position).getUsername());

        Glide.with(context).load(camerrowUserArrayList.get(position).getProfilePicture()).asBitmap().placeholder(R.mipmap.ic_launcher_round).into(holder.profileImage);

       holder.bind(camerrowUserArrayList.get(position),position,listener);



    }


    @Override
    public int getItemCount() {
        return camerrowUserArrayList.size();
    }




}
