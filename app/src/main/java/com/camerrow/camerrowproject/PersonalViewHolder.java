package com.camerrow.camerrowproject;

import android.content.Context;
import android.net.Uri;
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

import java.net.URL;

public class PersonalViewHolder extends RecyclerView.ViewHolder {


    private ImageView image;
    private TextView name;

    public PersonalViewHolder(@NonNull View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.personalObjectName);
        image = (ImageView) itemView.findViewById(R.id.friendsObjectImage);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setImage(String uri, String user_id ,int position, Context context) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("ProfileImages").child("default_profile_image.png");
        Picasso.get().load(uri).into(image);
    }


}
