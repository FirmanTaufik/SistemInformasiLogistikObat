package com.rentalapp.sisteminformasilogistikobat.Util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Model.KeluarModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ListModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MasukModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MutasiModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.R;

import java.util.ArrayList;

public class SortingData {
    private String TAG = "SortingDataTAG";
    private Context context;
    private ArrayList<ObatModel> obatList;
    private ArrayList<MutasiModel> mutasiList;
    private final DatabaseReference mDatabase;

    private long startDate;
    private long endtDate;
    private int sumberId;

    public SortingData(Context context, ArrayList<ObatModel> obatList,
                       DatabaseReference mDatabase, long startDate, long endtDate, int sumberId) {
        this.context = context;
        this.obatList = obatList;
        this.mDatabase = mDatabase;
        this.startDate = startDate;
        this.endtDate = endtDate;
        this.sumberId = sumberId;
        sortData();
    }

    public void sortData(){
        mutasiList = new ArrayList<>();
        MutasiModel mutasiModel = new MutasiModel();
       // mutasiList.add(mutasiModel);
        for (int i = 0;i <obatList.size();i++){
            mutasiList.add(new MutasiModel(obatList.get(i).getObatId(),obatList.get(i).getName(), obatList.get(i).getPack(),
                    0,0,0,
                   0,0,0,
                    0,0,0,
                    0,
                    "" ));
        }
    }



    public ArrayList<MutasiModel> getMutasiList(){
        return mutasiList;
    }

    public void setLayout(TableLayout tableLayout) {
        for (int i=0;i<mutasiList.size();i++){
            View view = LayoutInflater.from(context).inflate(R.layout.list_mutasi_body, null, false);
            TextView txtName = view.findViewById(R.id.txtName);
            TextView txtPack = view.findViewById(R.id.txtPack);
            TextView txtStockAwal = view.findViewById(R.id.txtStockAwal);
            TextView txtMasuk = view.findViewById(R.id.txtMasuk);

            TextView txtIGD = view.findViewById(R.id.txtIGD);
            TextView txtKBS = view.findViewById(R.id.txtKBS);
            TextView txtGNG = view.findViewById(R.id.txtGNG);
            TextView txtKTK = view.findViewById(R.id.txtKTK);
            TextView txtRSUD = view.findViewById(R.id.txtRSUD);
            TextView txtLAIN = view.findViewById(R.id.txtLAIN);

            TextView txtJumlah = view.findViewById(R.id.txtJumlah);
            TextView txtSisa = view.findViewById(R.id.txtSisa);

            txtName.setText(mutasiList.get(i).getName());
            txtPack.setText(mutasiList.get(i).getPack());
            getStockAwal(txtStockAwal, mutasiList.get(i).getObatId(), i);
            getMasuk(txtMasuk,mutasiList.get(i).getObatId(), i);

            getPengeluaranFaskes(txtIGD, mutasiList.get(i).getObatId(), 1, i);
            getPengeluaranFaskes(txtKBS, mutasiList.get(i).getObatId(), 2, i);
            getPengeluaranFaskes(txtGNG, mutasiList.get(i).getObatId(), 3, i);
            getPengeluaranFaskes(txtKTK, mutasiList.get(i).getObatId(), 4, i);
            getPengeluaranFaskes(txtRSUD, mutasiList.get(i).getObatId(), 5, i);
            getPengeluaranFaskes(txtLAIN, mutasiList.get(i).getObatId(), 6, i);

            getTotalKel(txtJumlah, mutasiList.get(i).getObatId(), i);
            getSisaStock(txtSisa, mutasiList.get(i).getObatId(),i);
            tableLayout.addView(view);
        }
    }

    private void getSisaStock(TextView txtSisa, String obatId, int pos) {
        Query query =  mDatabase.child("listKeluar")
                .orderByChild("tglKeluar")
                .startAt(0)
                .endAt(endtDate);

        Query query1 =  mDatabase.child("listMasuk")
                .orderByChild("tglMasuk")
                .startAt(0)
                .endAt(endtDate);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            int totalMasuk=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MasukModel masukModel = dataSnapshot.getValue(MasukModel.class);
                    masukModel.setMasukId(dataSnapshot.getKey());
                    if (sumberId==0){
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

                                                                        if (sumberId==0){
                                                                            if (obatId.equals(  listModel.getObatId())){
                                                                                totalKeluar = totalKeluar+listModel.getJumlah();
                                                                            }
                                                                        }else {
                                                                            if (sumberId==listModel.getSumberId()) {
                                                                                if (obatId.equals(  listModel.getObatId())){
                                                                                    totalKeluar = totalKeluar+listModel.getJumlah();
                                                                                }
                                                                            }

                                                                        }

                                                                    }
                                                                    int m = totalMasuk - totalKeluar;
                                                                    txtSisa.setText( String.valueOf(m));
                                                                    MutasiModel w = mutasiList.get(pos);
                                                                    w.setSisaStock(m);
                                                                    mutasiList.set(pos, w);
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
                    }else {
                        if (sumberId==masukModel.getSumberId()){
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

                                                                            if (sumberId==0){
                                                                                if (obatId.equals(  listModel.getObatId())){
                                                                                    totalKeluar = totalKeluar+listModel.getJumlah();
                                                                                }
                                                                            }else {
                                                                                if (sumberId==listModel.getSumberId()) {
                                                                                    if (obatId.equals(  listModel.getObatId())){
                                                                                        totalKeluar = totalKeluar+listModel.getJumlah();
                                                                                    }
                                                                                }

                                                                            }

                                                                        }
                                                                        int m = totalMasuk - totalKeluar;
                                                                        txtSisa.setText( String.valueOf(m));
                                                                        MutasiModel w = mutasiList.get(pos);
                                                                        w.setSisaStock(m);
                                                                        mutasiList.set(pos, w);
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getStockAwal(TextView txtStockAwal, String obatId, int pos) {
        Query query1 =  mDatabase.child("listMasuk")
                .orderByChild("tglMasuk")
                .startAt(0)
                .endAt(startDate);

        Query query =  mDatabase.child("listKeluar")
                .orderByChild("tglKeluar")
                .startAt(0)
                .endAt(startDate);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            int totalMasuk=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MasukModel masukModel = dataSnapshot.getValue(MasukModel.class);
                    masukModel.setMasukId(dataSnapshot.getKey());
                    if (sumberId==0){
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
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }else {
                        if (sumberId==masukModel.getSumberId()){
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
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }
                    }

                }
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

                                                if (sumberId==0){
                                                    if (obatId.equals(  listModel.getObatId())){
                                                        totalKeluar = totalKeluar+listModel.getJumlah();
                                                    }
                                                }else {
                                                    if (sumberId==listModel.getSumberId()) {
                                                        if (obatId.equals(  listModel.getObatId())){
                                                            totalKeluar = totalKeluar+listModel.getJumlah();
                                                        }
                                                    }

                                                }

                                            }
                                            int y = totalMasuk-totalKeluar;
                                            txtStockAwal.setText( String.valueOf(y));
                                            MutasiModel m = mutasiList.get(pos);
                                            m.setStockAwal(y);
                                            mutasiList.set(pos, m);
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

    private void getTotalKel(TextView txtJumlah, String obatId, int pos) {
        Query query =  mDatabase.child("listKeluar")
                .orderByChild("tglKeluar")
                .startAt(startDate)
                .endAt(endtDate);

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

                                            if (sumberId==0){
                                                if (obatId.equals(  listModel.getObatId())){
                                                    totalKeluar = totalKeluar+listModel.getJumlah();
                                                }
                                            }else {
                                                if (sumberId==listModel.getSumberId()) {
                                                    if (obatId.equals(  listModel.getObatId())){
                                                        totalKeluar = totalKeluar+listModel.getJumlah();
                                                    }
                                                }

                                            }

                                        }
                                        txtJumlah.setText( String.valueOf(totalKeluar));
                                        MutasiModel m = mutasiList.get(pos);
                                        m.setJumlah(totalKeluar);
                                        mutasiList.set(pos, m);
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

    private void getPengeluaranFaskes(TextView textView, String obatId, int faskesId, int position) {
        Query query =  mDatabase.child("listKeluar")
                .orderByChild("tglKeluar")
                .startAt(startDate)
                .endAt(endtDate);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            int totalKeluar=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KeluarModel keluarModel = dataSnapshot.getValue(KeluarModel.class);
                    keluarModel.setKeluarId(dataSnapshot.getKey());
                    if (keluarModel.getFaskesId()==faskesId){
                        mDatabase.child("listDataKeluar").child(dataSnapshot.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot s) {
                                        for (DataSnapshot d : s.getChildren()){
                                            Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                                            ListModel listModel = d.getValue(ListModel.class);
                                            listModel.setListId(d.getKey());
                                            Log.d(TAG, "onDataChange: "+listModel.getJumlah());

                                            if (sumberId==0){
                                                if (obatId.equals(  listModel.getObatId())){
                                                    totalKeluar = totalKeluar+listModel.getJumlah();
                                                }
                                            }else {
                                                if (sumberId==listModel.getSumberId()) {
                                                    if (obatId.equals(  listModel.getObatId())){
                                                        totalKeluar = totalKeluar+listModel.getJumlah();
                                                    }
                                                }

                                            }

                                        }
                                        textView.setText( String.valueOf(totalKeluar));
                                        MutasiModel muta = mutasiList.get(position);
                                        switch (faskesId){
                                            case 1:
                                                muta.setpIGD(totalKeluar);
                                                break;
                                            case 2:
                                                muta.setpKBS(totalKeluar);
                                                break;
                                            case 3:
                                                muta.setpGNG(totalKeluar);
                                                break;
                                            case 4:
                                                muta.setpKTK(totalKeluar);
                                                break;
                                            case 5:
                                                muta.setpRSUD(totalKeluar);
                                                break;
                                            case 6:
                                                muta.setpLain(totalKeluar);
                                                break;

                                        }

                                        mutasiList.set(position, muta);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getMasuk(TextView txtMasuk, String obatId, int pos) {
        Query query =  mDatabase.child("listMasuk")
        .orderByChild("tglMasuk")
        .startAt(startDate)
        .endAt(endtDate);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            int totalMasuk=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MasukModel masukModel = dataSnapshot.getValue(MasukModel.class);
                    masukModel.setMasukId(dataSnapshot.getKey());
                    if (sumberId==0){
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
                                        txtMasuk.setText( String.valueOf(totalMasuk));
                                        MutasiModel m = mutasiList.get(pos);
                                        m.setMasuk(totalMasuk);
                                        mutasiList.set(pos, m);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }else {
                        if (sumberId==masukModel.getSumberId()){
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
                                            txtMasuk.setText( String.valueOf(totalMasuk));
                                            MutasiModel m = mutasiList.get(pos);
                                            m.setMasuk(totalMasuk);
                                            mutasiList.set(pos, m);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
