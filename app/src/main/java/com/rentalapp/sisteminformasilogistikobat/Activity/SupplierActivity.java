package com.rentalapp.sisteminformasilogistikobat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Adapter.SupplierAdapter;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;
import com.rentalapp.sisteminformasilogistikobat.R;

import java.util.ArrayList;
import java.util.Collections;

public class SupplierActivity extends AppCompatActivity {
    private SupplierAdapter supplierAdapter;
    private ArrayList<SupplierModel> supplierModels;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);
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
        FirebaseApp.initializeApp(SupplierActivity.this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        supplierModels = new ArrayList<>();
        supplierAdapter = new SupplierAdapter(this,supplierModels);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(supplierAdapter);


    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    private void getData() {
        mDatabase.child("supplier")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        supplierModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            SupplierModel supplierModel = snapshot1.getValue(SupplierModel.class);
                            supplierModel.setSupplierId(snapshot1.getKey());
                            supplierModels.add(supplierModel);
                        }
                        Collections.reverse(supplierModels);
                        supplierAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void addSupllier(View view) {
        View view1 = LayoutInflater.from(this).inflate(R.layout.form_supplier,null  );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Supplier");
        builder.setView(view1);
        TextInputEditText edtName = view1.findViewById(R.id.edtName);
        TextInputEditText edtPhone = view1.findViewById(R.id.edtPhone);
        TextInputEditText edtAddress = view1.findViewById(R.id.edtAddress);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SupplierModel supplierModel = new SupplierModel();
                supplierModel.setName(edtName.getText().toString().trim());
                supplierModel.setAddress(edtAddress.getText().toString().trim());
                supplierModel.setPhone(edtPhone.getText().toString().trim());

                String id = mDatabase.child("supplier").push().getKey();
                mDatabase.child("supplier").child(id)
                        .setValue(supplierModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SupplierActivity.this, "sukses menambahkan", Toast.LENGTH_SHORT).show();

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Write failed
                                // ...
                            }
                        });

                  }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}