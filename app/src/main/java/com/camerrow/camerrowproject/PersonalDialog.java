package com.camerrow.camerrowproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.camerrow.camerrowproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@SuppressLint("ValidFragment")
public class PersonalDialog extends AppCompatDialogFragment{

    private EditText personalDialogNameField;
    private PersonalDialogListener personalDialogListener;
    private double locationLatitude;
    private double locationLongitude;
    private String user_id;

    String defaultProfileImageUri = "https://firebasestorage.googleapis.com/v0/b/camerrow-project.appspot.com/o/ProfileImages%2Fdefault_profile_image.png?alt=media&token=9c11f8bb-dda9-495e-89d6-b2b3a2e241e5";


    public PersonalDialog(String user_id, double locationLatitude, double locationLongitude) {
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.user_id = user_id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(),R.style.LightDialogTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();


        View view = inflater.inflate(R.layout.personal_dialog_layout, null);

        builder.setView(view).setTitle("New Personal Location")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        PersonalObject personalObject = new PersonalObject();
                        personalObject.setName(personalDialogNameField.getText().toString());
                        personalObject.setImage(defaultProfileImageUri);
                        personalObject.setLatitude(locationLatitude);
                        personalObject.setLongitude(locationLongitude);
                        personalDialogListener.sendBack(personalObject);

                    }
                });

        personalDialogNameField = view.findViewById(R.id.personalObjectNameField);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            personalDialogListener = (PersonalDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement PersonalDialogListener");
        }
    }


    public interface PersonalDialogListener{
        void sendBack(PersonalObject personalObject);
    }
}
