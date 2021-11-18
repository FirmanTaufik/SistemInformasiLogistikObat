package com.rentalapp.sisteminformasilogistikobat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.BuildConfig;
import com.rentalapp.sisteminformasilogistikobat.Model.UserModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.rentalapp.sisteminformasilogistikobat.Util.Constant.about_app;

public class SettingActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Constant constant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        constant = new Constant(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void toUser(View view) {
        View view1 = LayoutInflater.from(this).inflate(R.layout.form_verif,null  );
        TextInputEditText edtPassword = view1.findViewById(R.id.edtPassword);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Masukan Password");
        builder.setView(view1);
        builder.setIcon(R.mipmap.ic_logo);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkUser(edtPassword.getText().toString().trim());
            }
        });
        builder.show();
    }

    void checkUser(String password){
        SweetAlertDialog pDialog = new SweetAlertDialog(SettingActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        mDatabase.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel u = dataSnapshot.getValue(UserModel.class);
                    u.setUserId(dataSnapshot.getKey());
                    if (u.getUsername().equals(constant.getUserId(SettingActivity.this)) &&
                            u.getPassword().equals(password)) {
                        startActivity(new Intent(SettingActivity.this, UserActivity.class));

                        pDialog.dismissWithAnimation();
                        return;
                    }else {
                        Toast.makeText(SettingActivity.this, "password salah", Toast.LENGTH_SHORT).show();
                    }

                }

                pDialog.dismissWithAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void appInfo(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this)
                .setIcon(R.mipmap.ic_logo)
                .setTitle("App Version"  )
                .setMessage("Version "+ BuildConfig.VERSION_NAME +" Developed By "+getString(R.string.app_name))
                .create();
        alertDialog.show();
        alertDialog.setCancelable(true);
    }

    public void aboutApp(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this)
                .setIcon(R.mipmap.ic_logo)
                .setTitle("About App")
                .setMessage(about_app)
                .create();
        alertDialog.show();
        alertDialog.setCancelable(true);
    }

    public void contact(View view) {
        View view1 = LayoutInflater.from(this).inflate(R.layout.form_about,null  );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contact Us");
        builder.setView(view1);
        builder   .setIcon(R.mipmap.ic_logo);
        builder.setCancelable(true);
        builder.show();
    }
}