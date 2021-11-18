package com.rentalapp.sisteminformasilogistikobat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;
import com.rentalapp.sisteminformasilogistikobat.Model.UserModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private Constant constant;
    private DatabaseReference mDatabase;
    private ArrayList <ObatModel> obatModels;
    private ArrayList <SupplierModel> supplierModels;
    private CircleImageView img;
    private TextView txtUsername, txtName;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        img = findViewById(R.id.img);
        txtUsername = findViewById(R.id.txtUsername);
        txtName = findViewById(R.id.txtName);
        constant = new Constant(this);

        findViewById(R.id.wadah).setVisibility(View.GONE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (constant.getUserId(this)==null){
            startActivity(new Intent(this, LoginActivity.class));
        }else {
           // startActivity(new Intent(this, UserActivity.class));
            getUserInfo();
        }
        obatModels = new ArrayList<>();
        supplierModels = new ArrayList<>();

        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        mDatabase.child("obatalkes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        obatModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            ObatModel obatModel = snapshot1.getValue(ObatModel.class);
                            obatModel.setObatId(snapshot1.getKey());
                            obatModels.add(obatModel);
                        }
                        constant.setListObatAlkes(obatModels);
                        getSupplier();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getUserInfo() {
        mDatabase.child("user")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (userModel.getUsername().equals(constant.getUserId(MainActivity.this))){
                        Glide.with(MainActivity.this).load(userModel.getPhoto()).into(img);
                        txtUsername.setText(userModel.getUsername());
                        txtName.setText(userModel.getName());
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSupplier() {
        mDatabase.child("supplier")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        supplierModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            SupplierModel obatModel = snapshot1.getValue(SupplierModel.class);
                            obatModel.setSupplierId(snapshot1.getKey());
                            supplierModels.add(obatModel);
                        }
                        pDialog.dismissWithAnimation();
                        findViewById(R.id.wadah).setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void toSupplier(View view) {
        startActivity(new Intent(this, SupplierActivity.class));
    }

    public void toObatAlkes(View view) {
        startActivity(new Intent(this, ObatActivity.class));
    }

    public void toStockMasuk(View view) {
        Intent intent = new Intent(MainActivity.this, InOutActivity.class);
        intent.putParcelableArrayListExtra("obatModels",obatModels);
        intent.putParcelableArrayListExtra("supplierModels",supplierModels);
        intent.putExtra("isIn", true);
        startActivity(intent);
    }

    public void toStockKeluar(View view) {
        Intent intent = new Intent(MainActivity.this, InOutActivity.class);
        intent.putParcelableArrayListExtra("obatModels",obatModels);
        intent.putParcelableArrayListExtra("supplierModels",supplierModels);
        intent.putExtra("isIn", false);
        startActivity(intent);
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

    public void logout(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah Kamu Yakin Akan Logout?");
        builder.setPositiveButton("Iya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        constant.setUserId(MainActivity.this, null);
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void toMutasi(View view) {
        Intent intent = new Intent(MainActivity.this, MutasiActivity.class);
        intent.putParcelableArrayListExtra("obatModels",obatModels);
        startActivity(intent);
    }

    public void toSetting(View view) {
        startActivity(new Intent(MainActivity.this, SettingActivity.class));
    }

    public void toKaryawan(View view) {
        startActivity(new Intent(MainActivity.this, KaryawanActivity.class));
    }
}