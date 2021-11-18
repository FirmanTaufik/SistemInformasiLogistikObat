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
import android.print.PdfPrint;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Adapter.MutasiAdapter;
import com.rentalapp.sisteminformasilogistikobat.Model.KaryawanModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ListModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MutasiModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;
import com.rentalapp.sisteminformasilogistikobat.Util.PrintArea;
import com.rentalapp.sisteminformasilogistikobat.Util.SortingData;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jrizani.jrspinner.JRSpinner;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MutasiActivity extends AppCompatActivity {
    private long startDate = 0;
    private long endDate = System.currentTimeMillis();
    private int sumberId= 0;
    private String TAG ="MutasiActivityTAG";
    private Toolbar toolbar;
    private Constant constant;
    private RecyclerView recyclerView;
    private ArrayList<MutasiModel> mutasiModels;
    private MutasiAdapter mutasiAdapter;
    private SortingData sortingData;
    private DatabaseReference mDatabase;
    private TableLayout tableLayout;
    private int PERMISSION_REQUEST = 0;
    private boolean allowSave = true;
    private ArrayList<KaryawanModel> karyawanModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutasi);
        karyawanModels = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        constant = new Constant(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        sortingData = new SortingData(this, getIntent().getParcelableArrayListExtra("obatModels"), mDatabase,startDate,
                        endDate, sumberId);
        tableLayout = findViewById(R.id.tableLayout);
        sortingData.setLayout(tableLayout);
        mutasiModels = sortingData.getMutasiList();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mutasiAdapter = new MutasiAdapter(this, mutasiModels,0,System.currentTimeMillis(),0);
//        recyclerView.setAdapter(mutasiAdapter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.acion_search).setVisible(false);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.acion_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Nama Obat/Alkes");
        searchView.setSubmitButtonEnabled(true);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter){
            View view1 = LayoutInflater.from(this).inflate(R.layout.form_filter_mutasi,null  );
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Filter Mutasi");
            builder.setView(view1);
            JRSpinner spinnerSumber = view1.findViewById(R.id.spinnerSumber);
            TextInputEditText edtStart = view1.findViewById(R.id.edtStart);
            TextInputEditText edtEnd = view1.findViewById(R.id.edtEnd);
            edtStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MutasiActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, month, dayOfMonth);
                            edtStart.setText(constant.changeFromDate(newDate.getTime()));
                            String yyyymmd = constant.changeFromDate(newDate.getTime()) +" 00:00:00";
                            startDate = constant.changeYyyyMMDDtoMili(yyyymmd) ;
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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MutasiActivity.this, new DatePickerDialog.OnDateSetListener() {
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

            spinnerSumber.setItems(constant.getSumberDanaNama(true));
            spinnerSumber.setOnItemClickListener(new JRSpinner.OnItemClickListener() { //set it if you want the callback
                @Override
                public void onItemClick(int position) {
                    sumberId = constant.getSumberId(position,true);
                    toolbar.setTitle(constant.getSumberNameById(constant.getSumberId(position,true)));
                 //   Toast.makeText(MutasiActivity.this, String.valueOf(constant.getSumberId(position,true)), Toast.LENGTH_SHORT).show();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    sortingData = new SortingData(MutasiActivity.this,
                            getIntent().getParcelableArrayListExtra("obatModels"), mDatabase,startDate,
                            endDate, sumberId);
                    tableLayout = findViewById(R.id.tableLayout);
                    tableLayout.removeAllViews();
                    View view  = LayoutInflater.from(MutasiActivity.this).inflate(R.layout.list_mutasi_header, null,false);
                    tableLayout.addView(view);
                    sortingData.setLayout(tableLayout);


                    String sorting =  edtStart.getText().toString().trim() +" / "+
                            edtEnd.getText().toString().trim() ;
                    toolbar.setSubtitle(sorting);
                    toolbar.setTitle(constant.getSumberNameById(sumberId));

//                    mutasiAdapter = new MutasiAdapter(MutasiActivity.this, mutasiModels,
//                            startDate, endDate,sumberId);
//                    recyclerView.setAdapter(mutasiAdapter);
//                    mutasiAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }else  if (item.getItemId() == R.id.print){
            if (karyawanModels.size()!=0){
                new PrintArea(MutasiActivity.this, sortingData.getMutasiList(),
                        sumberId, startDate, endDate,karyawanModels);
            }

          // move();
        }
        return super.onOptionsItemSelected(item);
    }



}