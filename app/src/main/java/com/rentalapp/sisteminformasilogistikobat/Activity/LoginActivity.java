package com.rentalapp.sisteminformasilogistikobat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Model.UserModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private TextInputEditText edtPassword;
    private TextInputEditText edtUsername;
    private Constant constant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        constant = new Constant(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);
    }

    public void login(View view) {
        SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        if (edtUsername.getText().length()==0
                || edtPassword.getText().length()==0) {
            pDialog.dismissWithAnimation();
            edtUsername.setError("Username tidak boleh kosong");
            edtPassword.setError("Password tidak boleh kosong");
            Toast.makeText(this, "username atau password masih kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabase.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            int countFind=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel u = dataSnapshot.getValue(UserModel.class);
                    u.setUserId(dataSnapshot.getKey());
                    if (u.getUsername().equals(edtUsername.getText().toString().trim()) &&
                            u.getPassword().equals(edtPassword.getText().toString().trim())) {
                        constant.setUserId(LoginActivity.this,u.getUsername() );
                        constant.setLevel(LoginActivity.this,u.getLevel());
                         startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        countFind= countFind+1;
                        return;
                    }
                }

                if (countFind==0){
                    Toast.makeText(LoginActivity.this, "username atau password salah", Toast.LENGTH_SHORT).show();
                }
                pDialog.dismissWithAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pDialog.dismissWithAnimation();

            }
        });
    }


            boolean doubleBackToExitPressedOnce =false;
    @SuppressLint("NewApi")
    public void onBackPressed(){
        if (doubleBackToExitPressedOnce) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap Sekali Lagi Untuk Keluar...", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

}