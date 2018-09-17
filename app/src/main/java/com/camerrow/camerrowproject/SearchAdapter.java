package com.camerrow.camerrowproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter <SearchAdapter.SearchBarViewHolder>{

    Context context;
    private ArrayList<CamerrowUser> camerrowUserArrayList;

    class SearchBarViewHolder extends RecyclerView.ViewHolder {


        private ImageView profileImage;
        private TextView name;
        private TextView username;


        public SearchBarViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.searchItemName);
            username = (TextView) itemView.findViewById(R.id.searchItemUsername);
            profileImage = (ImageView) itemView.findViewById(R.id.searchItemProfileImage);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setUsername(String username) {
            this.username.setText(username);
        }
    }

    public SearchAdapter(Context context, ArrayList<CamerrowUser> camerrowUserArrayList) {
        this.context = context;
        this.camerrowUserArrayList = camerrowUserArrayList;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchBarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_item, viewGroup, false);
        return new SearchAdapter.SearchBarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchBarViewHolder holder, int position) {

        holder.name.setText(camerrowUserArrayList.get(position).getName());
        holder.username.setText(camerrowUserArrayList.get(position).getUsername());

        Glide.with(context).load(camerrowUserArrayList.get(position).getProfilePicture()).asBitmap().placeholder(R.mipmap.ic_launcher_round).into(holder.profileImage);


    }


    @Override
    public int getItemCount() {
        return camerrowUserArrayList.size();
    }
}
