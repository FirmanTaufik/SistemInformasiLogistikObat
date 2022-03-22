package com.rentalapp.sisteminformasilogistikobat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Adapter.ObatAdapter;
import com.rentalapp.sisteminformasilogistikobat.Model.ListModel;
import com.rentalapp.sisteminformasilogistikobat.R;

public class ObatDetailActivity extends AppCompatActivity {
    private String TAG ="ObatDetailActivityTAG";
    private DatabaseReference mDatabase;
    private Toolbar toolbar;
    private TextView txtPack, txtMasuk,
            txtKeluar,txtSisa ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obat_detail);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar);
        txtSisa = findViewById(R.id.txtSisa);
        txtKeluar = findViewById(R.id.txtKeluar);
        txtMasuk = findViewById(R.id.txtMasuk);
        txtPack = findViewById(R.id.txtPack);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle(getIntent().getStringExtra("name"));
        txtPack.setText(getIntent().getStringExtra("kemasan"));
        getMasuk();
        getKeluar();
        getSisaStock();
    }


    private void getSisaStock() {
        Query query =  mDatabase.child("listDataMasuk");
        Query query2 =  mDatabase.child("listDataKeluar");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            int totalMasuk=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChangemas: "+d.getKey());
                        ListModel listModel = d.getValue(ListModel.class);
                        listModel.setListId(d.getKey());
                        Log.d(TAG, "onDataChange: "+listModel.getJumlah());
                        if (getIntent().getStringExtra("obatId").equals(
                                listModel.getObatId())){
                            totalMasuk = totalMasuk+listModel.getJumlah();
                        }
                    }


                }
                // holder.txtMasuk.setText("Total Masuk : "+String.valueOf(totalMasuk));
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    int totalKeluar=0;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                            for (DataSnapshot d : dataSnapshot.getChildren()){
                                Log.d(TAG, "onDataChangemas: "+d.getKey());
                                ListModel listModel = d.getValue(ListModel.class);
                                listModel.setListId(d.getKey());
                                Log.d(TAG, "onDataChange: "+listModel.getJumlah());
                                if (getIntent().getStringExtra("obatId").equals(
                                        listModel.getObatId())){
                                    totalKeluar = totalKeluar+listModel.getJumlah();
                                }
                            }


                        }
                        //    holder.txtKeluar.setText("Total Keluar : "+String.valueOf(totalKeluar));
                        int s = totalMasuk - totalKeluar;
                        txtSisa.setText("Sisa Stock : "+String.valueOf(s));
//                        if (s<=constant.maxStockSisa){
//                            holder.txtWarnSisa.setVisibility(View.VISIBLE);
//                            holder.txtWarnSisa.setText(String.valueOf(s));
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getKeluar() {
        Query query =  mDatabase.child("listDataKeluar");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            int totalKeluar=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChangemas: "+d.getKey());
                        ListModel listModel = d.getValue(ListModel.class);
                        listModel.setListId(d.getKey());
                        Log.d(TAG, "onDataChange: "+listModel.getJumlah());
                        if (getIntent().getStringExtra("obatId").equals(
                                listModel.getObatId())){
                            totalKeluar = totalKeluar+listModel.getJumlah();
                        }
                    }


                }
                txtKeluar.setText("Total Keluar : "+String.valueOf(totalKeluar));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getMasuk() {
        Query query =  mDatabase.child("listDataMasuk");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            int totalMasuk=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChangemas: "+d.getKey());
                        ListModel listModel = d.getValue(ListModel.class);
                        listModel.setListId(d.getKey());
                        Log.d(TAG, "onDataChange: "+listModel.getJumlah());
                        if (getIntent().getStringExtra("obatId").equals(
                                listModel.getObatId())){
                            totalMasuk = totalMasuk+listModel.getJumlah();
                        }
                    }


                }
                 txtMasuk.setText("Total Masuk : "+String.valueOf(totalMasuk));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}