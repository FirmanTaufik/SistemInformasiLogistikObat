package com.rentalapp.sisteminformasilogistikobat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.rentalapp.sisteminformasilogistikobat.Model.KaryawanModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.PrintSupplier;

import java.util.ArrayList;
import java.util.Collections;

public class SupplierActivity extends AppCompatActivity {
    private SupplierAdapter supplierAdapter;
    private ArrayList<SupplierModel> supplierModels;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;

    private ArrayList<KaryawanModel> karyawanModels;
    private PrintSupplier printSupplier;
    private long startDate = 0;
    private long endDate = System.currentTimeMillis();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);
        karyawanModels= new ArrayList<>();
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
      getKaryawan();
    }

    private void getKaryawan() {
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
//                        printSupplier =new PrintSupplier(SupplierActivity.this,mDatabase,startDate,endDate,karyawanModels,
//                                getIntent().getParcelableArrayListExtra("obatModels"),supplierModels);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.print).setVisible(true).setIcon(R.drawable.ic_baseline_list_alt_24);
        menu.findItem(R.id.add).setVisible(false);
        menu.findItem(R.id.acion_search).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.print){
            if (karyawanModels.size()!=0 && supplierModels.size()!=0){
                //printSupplier.createWebPrintJob();
               // printSupplier.input();

                Intent intent = new Intent(SupplierActivity.this, MutasiActivity.class);
                intent.putExtra("isSupplier", true);
                intent.putParcelableArrayListExtra("supplierModels", supplierModels);
                intent.putParcelableArrayListExtra("obatModels",getIntent().getParcelableArrayListExtra("obatModels"));
                startActivity(intent);
            }

        }
        return super.onOptionsItemSelected(item);
    }
}