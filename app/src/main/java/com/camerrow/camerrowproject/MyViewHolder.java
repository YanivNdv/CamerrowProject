package com.camerrow.camerrowproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyViewHolder extends RecyclerView.ViewHolder {


    private ImageView image;
    private TextView name;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.personalObjectName);
        image = (ImageView) itemView.findViewById(R.id.personalObjectImage);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setImage(String i , Context context) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(i);
        Glide.with(context).using(new FirebaseImageLoader()).load(ref).into(image);
        this.image = image;
    }
}
