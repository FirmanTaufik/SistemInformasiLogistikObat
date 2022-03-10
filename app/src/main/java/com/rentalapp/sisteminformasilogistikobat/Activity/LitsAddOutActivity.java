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
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

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
import com.rentalapp.sisteminformasilogistikobat.Adapter.ListAdapter;
import com.rentalapp.sisteminformasilogistikobat.Adapter.MasukAdapter;
import com.rentalapp.sisteminformasilogistikobat.Model.KeluarModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ListModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MasukModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MutasiModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SisaStockModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import jrizani.jrspinner.JRSpinner;

public class LitsAddOutActivity extends AppCompatActivity {
    private String TAG ="LitsAddOutActivityTAG";
    private DatabaseReference mDatabase;
    private FloatingActionButton fabInOut;
    private Constant constant;
    private TextInputEditText edtTglMasuk  ;
    private JRSpinner spinnerSumber  ;
    private JRSpinner spinnerSupplier ;
    private ArrayList<ListModel> listModels;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private int sumberId=0;
    private String supplierId= null;
    private long tanggal=0;
    private int faskesId=0;
    private Toolbar toolbar;
    private ArrayList <SisaStockModel> sisaStockModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lits_add_out);
        mDatabase = FirebaseDatabase.getInstance().getReference();
          toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listModels = new ArrayList<>();
        constant = new Constant(LitsAddOutActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listAdapter = new ListAdapter(LitsAddOutActivity.this, listModels,
                getIntent().getParcelableArrayListExtra("obatModels"),
                getIntent().getBooleanExtra("isIn", true));
        recyclerView.setAdapter(listAdapter);

        edtTglMasuk = findViewById(R.id.edtTglMasuk);
        spinnerSumber =  findViewById(R.id.spinnerSumber);
        spinnerSupplier =  findViewById(R.id.spinnerSupplier);
        fabInOut =  findViewById(R.id.fabInOut);
        spinnerSumber.setItems(constant.getSumberDanaNama(false));
        spinnerSumber.setOnItemClickListener(new JRSpinner.OnItemClickListener() { //set it if you want the callback
            @Override
            public void onItemClick(int position) {
                sumberId = constant.getSumberId(position,false);
            }
        });

        edtTglMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(LitsAddOutActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        // in.setTglMasuk(newDate.getTimeInMillis());
                        edtTglMasuk.setText(constant.changeFromDate(newDate.getTime()));
                        tanggal = newDate.getTimeInMillis();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        if (getIntent().getBooleanExtra("isIn", true)) {
            setMasukUi();
        }else {
            setKeluarUi();
        }
    }

    private void getSisaStock(String obatId, TextInputEditText edtSisa) {
        sisaStockModels= new ArrayList<>();
        Query query =  mDatabase.child("listKeluar")
                .orderByChild("tglKeluar")
                .startAt(0)
                .endAt(System.currentTimeMillis());

        Query query1 =  mDatabase.child("listMasuk")
                .orderByChild("tglMasuk")
                .startAt(0)
                .endAt(System.currentTimeMillis());
//        ArrayList <ObatModel> obatModels = getIntent().getParcelableArrayListExtra("obatModels");
//        for (int i = 0; i <obatModels.size() ; i++) {
//            String obatId = obatModels.get(i).getObatId();
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                int totalMasuk=0;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        MasukModel masukModel = dataSnapshot.getValue(MasukModel.class);
                        masukModel.setMasukId(dataSnapshot.getKey());

                        mDatabase.child("listDataMasuk").child(dataSnapshot.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot s) {
                                        for (DataSnapshot d : s.getChildren()){
                                            Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                                            ListModel listModel = d.getValue(ListModel.class);
                                            listModel.setListId(d.getKey());
                                            Log.d(TAG, "onDataChange: "+listModel.getJumlah());
                                            if (obatId.equals(listModel.getObatId())){
                                                totalMasuk = totalMasuk+listModel.getJumlah();
                                            }
                                        }
                                        //txtMasuk.setText( String.valueOf(totalMasuk));

                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            int totalKeluar=0;
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    KeluarModel keluarModel = dataSnapshot.getValue(KeluarModel.class);
                                                    keluarModel.setKeluarId(dataSnapshot.getKey());
                                                    mDatabase.child("listDataKeluar").child(dataSnapshot.getKey())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot s) {
                                                                    for (DataSnapshot d : s.getChildren()){
                                                                        Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                                                                        ListModel listModel = d.getValue(ListModel.class);
                                                                        listModel.setListId(d.getKey());
                                                                        Log.d(TAG, "onDataChange: "+listModel.getJumlah());

                                                                        if (obatId.equals(  listModel.getObatId())){
                                                                            totalKeluar = totalKeluar+listModel.getJumlah();
                                                                        }

                                                                    }
                                                                    int m = totalMasuk - totalKeluar;
                                                                    edtSisa.setEnabled(false);
                                                                    edtSisa.setText(String.valueOf(m));
                                                                    Log.d(TAG, "sisastock: "+m);
                                                                  //  sisaStockModels.add(new SisaStockModel(totalMasuk, totalKeluar));
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });


                                                }
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    //    }


    }


    private void setKeluarUi() {
        toolbar.setTitle("Tambah Obat/Alkes Keluar");
        TextInputLayout txtInputLayoutSumber = findViewById(R.id.txtInputLayoutSumber);
        TextInputLayout txtInputLayout = findViewById(R.id.txtInputLayout);
        txtInputLayout.setHint("Pilih Faskes");
        txtInputLayoutSumber.setVisibility(View.GONE);


        spinnerSupplier.setTitle("Pilih Faskes");
        spinnerSupplier.setHint("Pilih Faskes");
        spinnerSupplier.setItems(constant.getFaskesName());
        spinnerSupplier.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                faskesId = constant.getFaskesModels().get(position).getFaskesId();
            }
        });

        if (getIntent().hasExtra("editOut" )){
            sumberId = getIntent().getIntExtra("sumberId",0);
            faskesId = getIntent().getIntExtra("faskesId",0);
            tanggal = getIntent().getLongExtra("tanggal",0);

            edtTglMasuk.setText(constant.changeFromLong(tanggal));
            spinnerSumber.setText(constant.getSumberNameById(sumberId));
            spinnerSupplier.setText(constant.getFaskesNameById(faskesId));

            mDatabase.child("listDataKeluar")
                    .child(getIntent().getStringExtra("keluarId"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listModels.clear();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                ListModel l = snapshot1.getValue(ListModel.class);
                                listModels.add(l);
                            }
                            listAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

        fabInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogKeluar();
            }
        });

    }

    private void showDialogKeluar() {
        ListModel listModel = new ListModel();
        View view1 = LayoutInflater.from(this).inflate(R.layout.form_masuk,null  );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah");
        builder.setView(view1);
        TextInputLayout txtInputJml = view1.findViewById(R.id.txtInputJml);
        txtInputJml.setHint("Jumlah Keluar");
        TextInputLayout txtInputSisa = view1.findViewById(R.id.txtInputSisa);
        txtInputSisa.setVisibility(View.VISIBLE);
        TextInputEditText edtSisa = view1.findViewById(R.id.edtSisa);
        TextInputLayout textInputLayout = view1.findViewById(R.id.textInputLayout);
        textInputLayout.setVisibility(View.VISIBLE);
        TextInputEditText edtTglExp =  view1.findViewById(R.id.edtTglExp);
        TextInputEditText edtJmlMasuk = view1.findViewById(R.id.edtJmlMasuk);
        JRSpinner spinnerObat = view1.findViewById(R.id.spinnerObat);
        JRSpinner spinner = view1.findViewById(R.id.spinner);
        edtTglExp.setEnabled(false);

        spinner.setItems(constant.getSumberDanaNama(false));

        spinner.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                sumberId = constant.getSumberId(position,false);
                spinnerObat.clear();
                spinnerObat.setItems(new String[0]);
                Log.d(TAG, "onItemClick: "+sumberId);
                mDatabase.child("listMasuk")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<String> listId = new ArrayList<>();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    MasukModel m = snapshot1.getValue(MasukModel.class);
                                    m.setMasukId(snapshot1.getKey());
                                    if (sumberId==m.getSumberId()){
                                        Log.d(TAG, "onDataChange: "+sumberId);
                                        listId.add(m.getMasukId());
                                    }
                                }
                                getDataObatMasuk(spinnerObat,listId);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

        builder.setPositiveButton("ok", null);
        builder.setNegativeButton("cancel", null);
        final AlertDialog mAlertDialog = builder.create();

        spinnerObat.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                getSisaStock(constant.getObatId(position),edtSisa);
                listModel.setObatId(constant.getObatId(position));
                edtTglExp.setText(constant.changeFromLong(Long.valueOf(constant.getObatPack(position))));
                listModel.setTglExp(Long.valueOf(constant.getObatPack(position)));
            }
        });

        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtJmlMasuk.getText().length()==0) {
                            Toast.makeText(LitsAddOutActivity.this,"Jumlah Masih Kosong", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int sisa = Integer.valueOf(edtSisa.getText().toString());
                        int jml = Integer.valueOf(edtJmlMasuk.getText().toString());

                        if (jml>sisa){
                            Toast.makeText(LitsAddOutActivity.this,"Jumlah Keluar Terlalu Banyak", Toast.LENGTH_SHORT).show();
                            return;
                        }
                            listModel.setSumberId(sumberId);
                            listModel.setJumlah(Integer.valueOf(edtJmlMasuk.getText().toString().trim()));
                            listModels.add(listModel);
                            listAdapter.notifyDataSetChanged();
                            mAlertDialog.dismiss();
                    }
                });
            }
        });
        mAlertDialog.show();
    }


    private void getDataObatMasuk(JRSpinner spinnerObat, ArrayList<String> listId) {
        ArrayList<ObatModel> sortBySumberId = new ArrayList<>();
        for (int i =0; i <listId.size();i++){
            int finalI = i;
            mDatabase.child("listDataMasuk").child(listId.get(i))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                ListModel m = snapshot1.getValue(ListModel.class);
                                 sortBySumberId.add(new ObatModel(m.getObatId(),
                                         getObatName(m.getObatId())
                                         ,String.valueOf(m.getTglExp()),null));
                            }

                            if (finalI == listId.size()-1){
                                constant.setListObatAlkes(sortBySumberId);
                                spinnerObat.setItems(constant.getObatNama());
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void setMasukUi() {
        TextInputLayout txtInputLayout = findViewById(R.id.txtInputLayout);
        txtInputLayout.setHint("Pilih Supplier");
        spinnerSupplier.setHint("Pilih Supplier");
        constant.setlistSupplier(getIntent().getParcelableArrayListExtra("supplierModels"));
        spinnerSupplier.setItems(constant.getSupplierNama());
        spinnerSupplier.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                supplierId = constant.getSupplierId(position);
            }
        });

        if (getIntent().hasExtra("editIn" )){
            sumberId = getIntent().getIntExtra("sumberId",0);
            supplierId = getIntent().getStringExtra("supplierId");
            tanggal = getIntent().getLongExtra("tanggal",0);

            edtTglMasuk.setText(constant.changeFromLong(tanggal));
            spinnerSumber.setText(constant.getSumberNameById(sumberId));
            spinnerSupplier.setText(constant.getSupplierNameById(supplierId));
            mDatabase.child("listDataMasuk")
                    .child(getIntent().getStringExtra("masukId"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listModels.clear();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                ListModel l = snapshot1.getValue(ListModel.class);
                             //   l.setListId(snapshot1.getKey());
                                listModels.add(l);
                            }
                            listAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        fabInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpinnerMasuk();
            }
        });
    }

    private void setSpinnerMasuk() {
        ListModel listModel = new ListModel();
        View view1 = LayoutInflater.from(this).inflate(R.layout.form_masuk,null  );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah");
        builder.setView(view1);
        TextInputEditText edtTglExp =  view1.findViewById(R.id.edtTglExp);
        TextInputEditText edtJmlMasuk = view1.findViewById(R.id.edtJmlMasuk);
        JRSpinner spinnerObat = view1.findViewById(R.id.spinnerObat);
        edtTglExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(LitsAddOutActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        listModel.setTglExp(newDate.getTimeInMillis());
                        edtTglExp.setText(constant.changeFromDate(newDate.getTime())); 
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        constant.setListObatAlkes(getIntent().getParcelableArrayListExtra("obatModels"));
        spinnerObat.setItems(constant.getObatNama());

        builder.setPositiveButton("ok", null);
        builder.setNegativeButton("cancel", null);
        final AlertDialog mAlertDialog = builder.create();

        spinnerObat.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                listModel.setObatId(constant.getObatId(position));
            }
        });

        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtJmlMasuk.getText().length()==0) {
                            Toast.makeText(LitsAddOutActivity.this,"Jumlah Masih Kosong", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        listModel.setJumlah(Integer.valueOf(edtJmlMasuk.getText().toString().trim()));
                        listModels.add(listModel);
                        listAdapter.notifyDataSetChanged();
                        mAlertDialog.dismiss();
                    }
                });
            }
        });
        mAlertDialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            if (getIntent().getBooleanExtra("isIn", true)) {
                saveMasuk();
            }else {
                saveKeluar();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private String keluarId;
    private void saveKeluar() {
        if (edtTglMasuk.getText().length()==0){
            Toast.makeText(this, "tanggal masih kosong", Toast.LENGTH_SHORT).show();
            return;
        }



        if (faskesId==0){
            Toast.makeText(this, "Faskes Belum di Pilih", Toast.LENGTH_SHORT).show();
            return;
        }
        if (listModels.size()==0){
            Toast.makeText(this, "List Obat/Alkes Kosong", Toast.LENGTH_SHORT).show();
            return;

        }
        if (getIntent().hasExtra("editOut")){
            keluarId = getIntent().getStringExtra("keluarId");
        }else {
            keluarId = mDatabase.child("listKeluar").push().getKey();
        }
        KeluarModel keluarModel = new KeluarModel();
        keluarModel.setTglKeluar(tanggal);
        keluarModel.setFaskesId(faskesId);
        mDatabase.child("listKeluar")
                .child(keluarId)
                .setValue(keluarModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (getIntent().hasExtra("editOut" )){
                            mDatabase.child("listDataKeluar")
                                    .child(keluarId).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: edit"+listAdapter.getListModels().size());
                                            addListData();
                                        }
                                    });
                        }else {
                            addListData();
                        }

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.print).setVisible(false);
        menu.findItem(R.id.add).setVisible(true).setIcon(R.drawable.ic_baseline_save_24);
        menu.findItem(R.id.acion_search).setVisible(false);
        return true;
    }

    String masukId;
    private void saveMasuk() {
        if (edtTglMasuk.getText().length()==0){
            Toast.makeText(this, "tanggal masih kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sumberId ==0){
            Toast.makeText(this, "Sumber Dana Belum di Pilih", Toast.LENGTH_SHORT).show();
            return;
        }

        if (supplierId==null){
            Toast.makeText(this, "Supplier Belum di Pilih", Toast.LENGTH_SHORT).show();
            return;
        }
        if (listModels.size()==0){
            Toast.makeText(this, "List Obat/Alkes Kosong", Toast.LENGTH_SHORT).show();
            return;

        }

        if (getIntent().hasExtra("editIn")){
            masukId = getIntent().getStringExtra("masukId");
        }else {
            masukId = mDatabase.child("listMasuk").push().getKey();
        }
        MasukModel masukModel = new MasukModel();
        masukModel.setTglMasuk(tanggal);
        masukModel.setSupplierId(supplierId);
        masukModel.setSumberId(sumberId);
        mDatabase.child("listMasuk")
                .child(masukId)
                .setValue(masukModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (getIntent().hasExtra("editIn" )){
                            mDatabase.child("listDataMasuk")
                                    .child(masukId).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: edit"+listAdapter.getListModels().size());
                                    addListData();
                                }
                            });
                        }else {
                            addListData();
                        }

                    }
                });


    }

    private String getObatName(String obatId) {
        ArrayList <ObatModel> obatModels = getIntent().getParcelableArrayListExtra("obatModels");
        for (int i =0; i<obatModels.size(); i++){
            if (obatModels.get(i).getObatId().equals(obatId)){
                return obatModels.get(i).getName();
            }
        }
        return null;
    }

    private void addListData() {
        if (getIntent().getBooleanExtra("isIn", true)) {
            for ( int i=0; i<listAdapter.getListModels().size(); i++ ){
                ListModel l = listAdapter.getListModels().get(i);

                String listId=  mDatabase.child("listDataMasuk")
                        .child(masukId).push().getKey();

                int finalI = i;
                mDatabase.child("listDataMasuk")
                        .child(masukId)
                        .child(listId)
                        .setValue(l)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (finalI == listAdapter.getListModels().size()-1){
                                    Toast.makeText(LitsAddOutActivity.this, "berhasil menyimpan", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
            }
        }else {
            for ( int i=0; i<listAdapter.getListModels().size(); i++ ){
                ListModel l = listAdapter.getListModels().get(i);

                String listId=  mDatabase.child("listDataKeluar")
                        .child(keluarId).push().getKey();

                int finalI = i;
                mDatabase.child("listDataKeluar")
                        .child(keluarId)
                        .child(listId)
                        .setValue(l)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (finalI == listAdapter.getListModels().size()-1){
                                    Toast.makeText(LitsAddOutActivity.this, "berhasil menyimpan", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
            }
        }


    }
}