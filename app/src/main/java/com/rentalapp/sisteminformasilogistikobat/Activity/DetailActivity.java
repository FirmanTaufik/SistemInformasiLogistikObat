package com.rentalapp.sisteminformasilogistikobat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Model.ListModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private TableLayout tableLayout;
    private Constant constant;
    private ArrayList<ObatModel> obatModels;
    private TextView txtSupplier, txtSumber,txtSumber1;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        constant = new Constant(this);
        obatModels = getIntent().getParcelableArrayListExtra("listObat");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tableLayout = findViewById(R.id.tableLayout);
        txtSupplier = findViewById(R.id.txtSupplier);
        txtSumber = findViewById(R.id.txtSumber);
        txtSumber1 = findViewById(R.id.txtSumber1);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("tanggal"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (getIntent().hasExtra("isMasuk")){
            getDataMasuk();
        }else {
            getDataKeluar();
        }

    }

    private void getDataKeluar() {
         txtSumber.setVisibility(View.GONE);
        txtSumber1.setVisibility(View.VISIBLE);
        txtSupplier.setText(getIntent().getStringExtra("faskes"));
        mDatabase.child("listDataKeluar").child(getIntent().getStringExtra("idKeluar"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int no =1;

                        if (tableLayout.getChildCount()>1){
                            return;
                        }

                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            ListModel l = snapshot1.getValue(ListModel.class);
                            View view = LayoutInflater.from(DetailActivity.this).inflate(R.layout.list_data_tb,null, false);

                            TextView txtSumber = view.findViewById(R.id.txtSumber);
                            TextView txtName = view.findViewById(R.id.txtName);
                            TextView txtNo = view.findViewById(R.id.txtNo);
                            TextView txtJmlMasuk = view.findViewById(R.id.txtJmlMasuk);
                            TextView txtTglExp = view.findViewById(R.id.txtTglExp);
                            txtNo.setText(String.valueOf(no++));
                            txtTglExp.setText(constant.changeFromLong(l.getTglExp()));
                            txtJmlMasuk.setText(String.valueOf(l.getJumlah()));
                            txtName.setText(getNameObat(l.getObatId()));
                            txtSumber.setVisibility(View.VISIBLE);
                            txtSumber.setText(String.valueOf(constant.getSumberNameById(l.getSumberId())));
                            tableLayout.addView(view);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getDataMasuk() {
        txtSumber.setText(getIntent().getStringExtra("sumberDana"));
        txtSupplier.setText(getIntent().getStringExtra("supplier"));

        mDatabase.child("listDataMasuk").child(getIntent().getStringExtra("idMasuk"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int no =1;

                        if (tableLayout.getChildCount()>1){
                            return;
                        }
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            ListModel l = snapshot1.getValue(ListModel.class);
                            View view = LayoutInflater.from(DetailActivity.this).inflate(R.layout.list_data_tb,null, false);
                            TextView txtName = view.findViewById(R.id.txtName);
                            TextView txtNo = view.findViewById(R.id.txtNo);
                            TextView txtJmlMasuk = view.findViewById(R.id.txtJmlMasuk);
                            TextView txtTglExp = view.findViewById(R.id.txtTglExp);
                            txtNo.setText(String.valueOf(no++));
                            txtTglExp.setText(constant.changeFromLong(l.getTglExp()));
                            txtJmlMasuk.setText(String.valueOf(l.getJumlah()));
                            txtName.setText(getNameObat(l.getObatId()));
                            tableLayout.addView(view);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String getNameObat(String obatId) {
        for (int i =0; i<obatModels.size(); i++){
            if (obatModels.get(i).getObatId().equals(obatId)){
                return  obatModels.get(i).getName();
            }
        }
        return null;
    }
}