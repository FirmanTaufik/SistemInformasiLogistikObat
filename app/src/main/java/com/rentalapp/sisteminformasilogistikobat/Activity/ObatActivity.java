package com.rentalapp.sisteminformasilogistikobat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Adapter.ObatAdapter;
import com.rentalapp.sisteminformasilogistikobat.Model.KaryawanModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ListModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.StockObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;
import com.rentalapp.sisteminformasilogistikobat.Util.PrintObat;
import com.rentalapp.sisteminformasilogistikobat.Util.SortingData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import jrizani.jrspinner.JRSpinner;

public class ObatActivity extends AppCompatActivity {
    private String TAG ="ObatActivityTAG";
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private ObatAdapter obatAdapter;
//    private ArrayList<ObatModel> obatModels;
    private ArrayList<StockObatModel> stockObat;
    private Constant constant;
    boolean isExp =true;
    PrintObat printObat;
    private ArrayList<KaryawanModel> karyawanModels;
    private long endDate =System.currentTimeMillis();
    private long startDate = 0;
    private int sumberId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obat);
        karyawanModels = new ArrayList<>();
        constant = new Constant(this);

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

        FirebaseApp.initializeApp(ObatActivity.this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
       // obatModels = new ArrayList<>();
        stockObat = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
     //   recyclerView.setItemViewCacheSize(stockObat.size());


        printObat= new PrintObat(ObatActivity.this,
                getIntent().getParcelableArrayListExtra("obatModels"),
                mDatabase, startDate, endDate,karyawanModels, sumberId);

        getData();
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
    private void getData() {
        stockObat.clear();
        mDatabase.child("obatalkes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        stockObat.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            ObatModel obatModel = snapshot1.getValue(ObatModel.class);
                            obatModel.setObatId(snapshot1.getKey());

//                            stockObat.add(new StockObatModel(obatModel.getObatId(), obatModel.getName(), obatModel.getPack(),
//                                    0,0 ));

                            getSisaStock(obatModel );
                        }
                        // Collections.reverse(obatModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getSisaStock(ObatModel obatModel) {
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
                        if (obatModel.getObatId().equals(
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
                                if (obatModel.getObatId().equals(
                                        listModel.getObatId())){
                                    totalKeluar = totalKeluar+listModel.getJumlah();
                                }
                            }


                        }
                        //    holder.txtKeluar.setText("Total Keluar : "+String.valueOf(totalKeluar));
                        int sisa = totalMasuk - totalKeluar;

                        getExp(obatModel,sisa);
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



    private void getExp(ObatModel obatModel, int sisa) {
        Query query =  mDatabase.child("listDataMasuk");
        Query query2 =  mDatabase.child("listDataKeluar");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            int totalMasuk=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        ListModel listModel = d.getValue(ListModel.class);
                        listModel.setListId(d.getKey());
                        Log.d(TAG, "onDataChange: "+listModel.getJumlah());
                        if (listModel.getTglExp()<=System.currentTimeMillis()){
                            if (obatModel.getObatId().equals(  listModel.getObatId())){
                                totalMasuk = totalMasuk+listModel.getJumlah();
                            }
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
                                if (listModel.getTglExp()<=System.currentTimeMillis()){
                                    if (obatModel.getObatId().equals(
                                            listModel.getObatId())){
                                        totalKeluar = totalKeluar+listModel.getJumlah();
                                    }
                                }

                            }


                        }
                        //    holder.txtKeluar.setText("Total Keluar : "+String.valueOf(totalKeluar));
                        int jmlExp = totalMasuk - totalKeluar;
                        stockObat.add(new StockObatModel(obatModel.getObatId(), obatModel.getName(),
                                obatModel.getPack(),

                                sisa,jmlExp ));

                        sort();
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

    private void sort() {
        ArrayList<StockObatModel> fixArray;
        if (isExp) {
            Collections.sort(stockObat, new Comparator<StockObatModel>() {
                @Override
                public int compare(StockObatModel o1, StockObatModel o2) {
                    return Integer.compare(o1.getStockExp(), o2.getStockExp());
                }
            });
             Collections.reverse(stockObat);
            fixArray = stockObat;
        }else {
            Collections.sort(stockObat, new Comparator<StockObatModel>() {
                @Override
                public int compare(StockObatModel o1, StockObatModel o2) {
                    return Integer.compare(o1.getSisaStock(), o2.getSisaStock());
                }
            });
            fixArray =stockObat;
        }

        obatAdapter = new ObatAdapter(this, fixArray);
        recyclerView.setAdapter(obatAdapter);
     //   Collections.sort(stockObat);
        obatAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    public void addObat(View view) {
        View view1 = LayoutInflater.from(this).inflate(R.layout.form_obat,null  );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Obat dan Alkes");
        builder.setView(view1);
        TextInputEditText edtName = view1.findViewById(R.id.edtName);
        TextInputEditText edtPack = view1.findViewById(R.id.edtPack);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ObatModel obatModel = new ObatModel();
                obatModel.setName(edtName.getText().toString().trim());
                obatModel.setPack(edtPack.getText().toString().trim());

                String id = mDatabase.child("obatalkes").push().getKey();
                mDatabase.child("obatalkes").child(id)
                        .setValue(obatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ObatActivity.this, "sukses menambahkan", Toast.LENGTH_SHORT).show();

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
    Menu prevMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.prevMenu =  menu;
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.filter).setVisible(true).setIcon(R.drawable.ic_baseline_keyboard_double_arrow_down_24);
        menu.findItem(R.id.print).setVisible(true).setIcon(R.drawable.ic_baseline_list_alt_24);
        menu.findItem(R.id.add).setVisible(false);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.acion_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Nama Obat");

        searchView.setSubmitButtonEnabled(true);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                obatAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter){
            if (isExp){
                isExp= false;
                prevMenu.findItem(R.id.filter).setIcon(R.drawable.  ic_baseline_keyboard_double_arrow_up_24);
                Toast.makeText(this, "Merubah Sisa Stock Jadi Yang Teratas", Toast.LENGTH_SHORT).show();
            }else {
                isExp= true;
                prevMenu.findItem(R.id.filter).setIcon(R.drawable.ic_baseline_keyboard_double_arrow_down_24);
                Toast.makeText(this, "Merubah Stock Expired Jadi Yang Teratas", Toast.LENGTH_SHORT).show();
            }
            sort();
        }

        if (item.getItemId()==R.id.print){

            Intent intent = new Intent(ObatActivity.this, MutasiActivity.class);
            intent.putExtra("isObat", true);
            intent.putParcelableArrayListExtra("obatModels",getIntent().getParcelableArrayListExtra("obatModels"));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}