package com.rentalapp.sisteminformasilogistikobat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Model.KeluarModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ListModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MasukModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private TableLayout tableLayout;
    private Constant constant;
    private ArrayList<ObatModel> obatModels;
    private TextView txtSupplier, txtSumber,txtSumber1,txtBatch;
    private Toolbar toolbar;
    private ArrayList<MasukModel> masukModels;
    private int position;

    private ArrayList<SupplierModel> supplierModels;
    private ArrayList<KeluarModel> keluarModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        constant = new Constant(this);
        obatModels = getIntent().getParcelableArrayListExtra("listObat");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        txtBatch = findViewById(R.id.txtBatch);
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

        position = getIntent().getIntExtra("position",0);
        if (getIntent().hasExtra("isMasuk")){
            supplierModels =  getIntent().getParcelableArrayListExtra("listSupplier");
            masukModels = getIntent().getParcelableArrayListExtra("listMasuk");
            getDataMasuk();
        }else {
            keluarModels= getIntent().getParcelableArrayListExtra("listKeluar");
            getDataKeluar();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_supplier, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()== R.id.edit) {
            if (getIntent().hasExtra("isMasuk")){
                Intent intent = new Intent(this, LitsAddOutActivity.class);
                intent.putExtra("supplierModels",supplierModels);
                intent.putExtra("obatModels",obatModels);
                intent.putExtra("masukId", masukModels.get(position).getMasukId());
                intent.putExtra("sumberId", masukModels.get(position).getSumberId());
                intent.putExtra("supplierId", masukModels.get(position).getSupplierId());
                intent.putExtra("tanggal", masukModels.get(position).getTglMasuk());
                intent.putExtra("isIn", true);
                intent.putExtra("editIn", true);
                startActivity(intent);
            }else {
                Intent intent = new Intent(this, LitsAddOutActivity.class);
                intent.putExtra("obatModels",obatModels);
                intent.putExtra("keluarId", keluarModels.get(position).getKeluarId());
                intent.putExtra("faskesId", keluarModels.get(position).getFaskesId());
                intent.putExtra("tanggal", keluarModels.get(position).getTglKeluar());
                intent.putExtra("isIn", false);
                intent.putExtra("editOut", true);
                startActivity(intent);
            }
        }

        if (item.getItemId()== R.id.delete){
            if (getIntent().hasExtra("isMasuk")){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Apakah Kamu Yakin Akan Menghapus");
                builder.setPositiveButton("Iya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child("listMasuk")
                                        .child(masukModels.get(position).getMasukId())
                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mDatabase.child("listDataMasuk").child(masukModels.get(position).getMasukId())
                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                finish();
                                                Toast.makeText(DetailActivity.this, "berhasil menghapus", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }else{

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Apakah Kamu Yakin Akan Menghapus");
                builder.setPositiveButton("Iya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child("listKeluar")
                                        .child(keluarModels.get(position).getKeluarId())
                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mDatabase.child("listDataKeluar").child(keluarModels.get(position).getKeluarId())
                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(DetailActivity.this, "berhasil menghapus", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDataKeluar() {
        txtBatch.setVisibility(View.GONE);
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
                            TextView txtBatch = view.findViewById(R.id.txtBatch);
                            TextView txtNo = view.findViewById(R.id.txtNo);
                            TextView txtJmlMasuk = view.findViewById(R.id.txtJmlMasuk);
                            TextView txtTglExp = view.findViewById(R.id.txtTglExp);
                            txtNo.setText(String.valueOf(no++));
                            txtTglExp.setText(constant.changeFromLong(l.getTglExp()));
                            txtJmlMasuk.setText(String.valueOf(l.getJumlah()));
                            txtName.setText(getNameObat(l.getObatId()));
                            txtSumber.setVisibility(View.VISIBLE);
                            txtBatch.setVisibility(View.GONE);
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
                            TextView txtBatch = view.findViewById(R.id.txtBatch);
                            TextView txtJmlMasuk = view.findViewById(R.id.txtJmlMasuk);
                            TextView txtTglExp = view.findViewById(R.id.txtTglExp);
                            txtNo.setText(String.valueOf(no++));
                            txtTglExp.setText(constant.changeFromLong(l.getTglExp()));
                            txtJmlMasuk.setText(String.valueOf(l.getJumlah()));
                            txtName.setText(getNameObat(l.getObatId()));
                            txtBatch.setText(l.getNoBatch());
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