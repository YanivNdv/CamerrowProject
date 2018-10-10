package com.camerrow.camerrowproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class FriendsViewHolder  extends RecyclerView.ViewHolder{

    ImageView image;
    private TextView name;
    private TextView username;


    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.friendObjectName);
        username = (TextView) itemView.findViewById(R.id.friendObjectUsername);
        image = (ImageView) itemView.findViewById(R.id.friendItemProfileImage);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setUsername(String username) {
        this.username.setText(username);
    }


    public void setImage(String image_ref , Context context) {
        Picasso.get().load(image_ref).into(image);
    }

    public ImageView getImage() {
        return image;
    }

    public TextView getName() {
        return name;
    }

    public TextView getUsername() {
        return username;
    }
}
