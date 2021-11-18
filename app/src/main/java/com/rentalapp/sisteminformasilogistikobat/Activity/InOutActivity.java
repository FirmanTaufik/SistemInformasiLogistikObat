package com.rentalapp.sisteminformasilogistikobat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.print.PdfPrint;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Adapter.KeluarAdapter;
import com.rentalapp.sisteminformasilogistikobat.Adapter.MasukAdapter;
import com.rentalapp.sisteminformasilogistikobat.Model.KeluarModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MasukModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;
import com.rentalapp.sisteminformasilogistikobat.Util.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import jrizani.jrspinner.JRSpinner;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class InOutActivity extends AppCompatActivity {
    private String TAG ="InOutActivityTAG";
    private MasukAdapter masukAdapter;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private Constant constant;
    private KeluarAdapter keluarAdapter;
    private long starDate=0;
    private long endDate= System.currentTimeMillis();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_out);
        constant = new Constant(InOutActivity.this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onResume() {
        if (getIntent().getBooleanExtra("isIn", true)) {
            setUiMasuk();
        }else {
            setUiKeluar();
        }
        super.onResume();
    }

    private void setUiKeluar() {
        toolbar.setTitle("Data Stock Keluar");
        Query query =  mDatabase.child("listKeluar")
                .orderByChild("tglKeluar")
                .startAt(starDate)
                .endAt(endDate);

        query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<KeluarModel> keluarModels = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            KeluarModel m = snapshot1.getValue(KeluarModel.class);
                            m.setKeluarId(snapshot1.getKey());
                            keluarModels.add(m);
                        }
                        Collections.reverse(keluarModels);
                        keluarAdapter =new KeluarAdapter(InOutActivity.this,
                                keluarModels,
                                getIntent().getParcelableArrayListExtra("obatModels") );
                        recyclerView.setAdapter(keluarAdapter);
                        //obatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void setUiMasuk() {
        toolbar.setTitle("Data Stock Masuk");
        Query query =  mDatabase.child("listMasuk")
                .orderByChild("tglMasuk")
                .startAt(starDate)
                .endAt(endDate);
        query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<MasukModel> masukModels = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MasukModel m = snapshot1.getValue(MasukModel.class);
                            m.setMasukId(snapshot1.getKey());
                            masukModels.add(m);
                        }
                        Collections.reverse(masukModels);
                        masukAdapter =new MasukAdapter(InOutActivity.this,
                                masukModels,
                                getIntent().getParcelableArrayListExtra("obatModels"),
                                getIntent().getParcelableArrayListExtra("supplierModels"));
                        recyclerView.setAdapter(masukAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.print).setVisible(false);
        menu.findItem(R.id.acion_search).setVisible(false);
        menu.findItem(R.id.ny).setVisible(true);
        menu.findItem(R.id.add).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add){
            Intent intent = new Intent(InOutActivity.this, LitsAddOutActivity.class);
            intent.putParcelableArrayListExtra("obatModels",getIntent().getParcelableArrayListExtra("obatModels"));
            intent.putParcelableArrayListExtra("supplierModels", getIntent().getParcelableArrayListExtra("supplierModels"));
            intent.putExtra("isIn", getIntent().getBooleanExtra("isIn", true));
            startActivity(intent);
            //    move();
        }

        if (item.getItemId()==R.id.ny) {
            View view1 = LayoutInflater.from(this).inflate(R.layout.form_filter_mutasi,null  );
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Filter Tanggal");
            builder.setView(view1);
            TextInputLayout spinnerSumber = view1.findViewById(R.id.inS);
            spinnerSumber.setVisibility(View.GONE);
            TextInputEditText edtStart = view1.findViewById(R.id.edtStart);
            TextInputEditText edtEnd = view1.findViewById(R.id.edtEnd);
            edtStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(InOutActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, month, dayOfMonth);
                            edtStart.setText(constant.changeFromDate(newDate.getTime()));
                            String yyyymmd = constant.changeFromDate(newDate.getTime()) +" 00:00:00";
                            starDate = constant.changeYyyyMMDDtoMili(yyyymmd) ;
                            Log.d(TAG, "onDateSetStart: "+constant.changeYyyyMMDDtoMili(yyyymmd) );
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            });

            edtEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(InOutActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, month, dayOfMonth);
                            edtEnd.setText(constant.changeFromDate(newDate.getTime()));

                            String yyyymmd = constant.changeFromDate(newDate.getTime()) +" 23:59:59";
                            endDate = constant.changeYyyyMMDDtoMili(yyyymmd) ;
                            Log.d(TAG, "onDateSetEnd: "+constant.changeYyyyMMDDtoMili(yyyymmd) );
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            });


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getIntent().getBooleanExtra("isIn", true)) {
                        setUiMasuk();
                    }else {
                        setUiKeluar();
                    }
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

        return super.onOptionsItemSelected(item);
    }

}