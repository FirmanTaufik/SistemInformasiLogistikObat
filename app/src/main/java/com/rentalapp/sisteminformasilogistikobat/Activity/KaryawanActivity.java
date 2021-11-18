package com.rentalapp.sisteminformasilogistikobat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Adapter.KaryawanAdapter;
import com.rentalapp.sisteminformasilogistikobat.Model.KaryawanModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;

import java.util.ArrayList;
import java.util.Collections;

import jrizani.jrspinner.JRSpinner;

public class KaryawanActivity extends AppCompatActivity {
    private String TAG ="KaryawanActivityTAG";
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private Constant constant;
    private KaryawanAdapter karyawanAdapter;
    private ArrayList<KaryawanModel> karyawanModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karyawan);
        constant = new Constant(this);
        karyawanModels = new ArrayList<>();
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
        karyawanAdapter = new KaryawanAdapter(this, karyawanModels);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(karyawanAdapter);

        getData();
    }

    private void getData() {
        mDatabase.child("karyawan")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        karyawanModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            KaryawanModel karyawanModel = snapshot1.getValue(KaryawanModel.class);
                            karyawanModel.setKaryawanId(snapshot1.getKey());
                            karyawanModels.add(karyawanModel);
                        }
                      //  Collections.reverse(karyawanModels);
                        karyawanAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void addKaryawan(View view) {
        View view1 = LayoutInflater.from(this).inflate(R.layout.form_karyawan,null  );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Karyawan");
        builder.setView(view1);
        KaryawanModel karyawanModel = new KaryawanModel();
        TextInputEditText edtNama = view1.findViewById(R.id.edtNama);
        TextInputEditText editNip = view1.findViewById(R.id.editNip);
        JRSpinner mySpinner = view1.findViewById(R.id.mySpinner);
        mySpinner.setItems(constant.getJabatanName());
        mySpinner.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                karyawanModel.setJabatan(constant.getJabatanId(position));
            }
        });
        builder.setPositiveButton("ok", null);
        builder.setNegativeButton("cancel", null);
        final AlertDialog mAlertDialog = builder.create();

        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (edtNama.getText().length()==0){
                            edtNama.setError("Masih Kosong");
                            return;
                        }
                        if (editNip.getText().length()==0){
                            editNip.setError("Masih Kosong");
                            return;
                        }

                        if (karyawanModel.getJabatan()==0){
                            mySpinner.setError("Pilih Jabatan");
                            return;
                        }

                        if (karyawanModels.size()==0){

                            karyawanModel.setNama(edtNama.getText().toString().trim());
                            karyawanModel.setNip(Integer.valueOf(editNip.getText().toString().trim()));

                            String id = mDatabase.child("karyawan").push().getKey();
                            mDatabase.child("karyawan").child(id)
                                    .setValue(karyawanModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mAlertDialog.dismiss();
                                    Toast.makeText(KaryawanActivity.this, "sukses menambahkan", Toast.LENGTH_SHORT).show();

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: "+e.getMessage());
                                            // Write failed
                                            // ...
                                        }
                                    });
                        }else {
                            if (karyawanModel.getJabatan()==3){

                                karyawanModel.setNama(edtNama.getText().toString().trim());
                                karyawanModel.setNip(Integer.valueOf(editNip.getText().toString().trim()));

                                String id = mDatabase.child("karyawan").push().getKey();
                                mDatabase.child("karyawan").child(id)
                                        .setValue(karyawanModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mAlertDialog.dismiss();
                                        Toast.makeText(KaryawanActivity.this, "sukses menambahkan", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: "+e.getMessage());
                                                // Write failed
                                                // ...
                                            }
                                        });
                            }else {
                                if (checkingKaryawan(karyawanModel.getJabatan())==0){

                                    karyawanModel.setNama(edtNama.getText().toString().trim());
                                    karyawanModel.setNip(Integer.valueOf(editNip.getText().toString().trim()));

                                    String id = mDatabase.child("karyawan").push().getKey();
                                    mDatabase.child("karyawan").child(id)
                                            .setValue(karyawanModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mAlertDialog.dismiss();
                                            Toast.makeText(KaryawanActivity.this, "sukses menambahkan", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: "+e.getMessage());
                                                    // Write failed
                                                    // ...
                                                }
                                            });
                                }else {
                                    Toast.makeText(KaryawanActivity.this, "jabatan sudah ada yang isi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }






                    }
                });
            }
        });
        mAlertDialog.show();
    }


    private int checkingKaryawan(double k){
        int balik =0;
        for (int i=0;i<karyawanModels.size(); i++){
            if (karyawanModels.get(i).getJabatan()==k){
                balik =1;
            }
        }
        return balik;
    }
}