package com.rentalapp.sisteminformasilogistikobat.Util;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Model.KaryawanModel;
import com.rentalapp.sisteminformasilogistikobat.Model.KeluarModel;
import com.rentalapp.sisteminformasilogistikobat.Model.LaporanObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ListModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MasukModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.R;

import java.util.ArrayList;

public class PrintObat {
    private String TAG = "PrintObatTAG";
    private Context context;
    private ArrayList<ObatModel> obatList;
    private ArrayList<LaporanObatModel> laporanObatModels;
    private final DatabaseReference mDatabase;
    private long startDate;
    private long endtDate;
    private ArrayList<KaryawanModel> karyawanModels;
    private int sumberId;
    public PrintObat(Context context, ArrayList<ObatModel> obatList,
                     DatabaseReference mDatabase, long startDate, long endtDate,
                     ArrayList<KaryawanModel> karyawanModels, int sumberId) {
        this.context = context;
        this.obatList = obatList;
        this.mDatabase = mDatabase;
        this.startDate = startDate;
        this.endtDate = endtDate;
        this.karyawanModels = karyawanModels;
        this.sumberId = sumberId;
        sortData();
    }

    private void sortData() {
        laporanObatModels= new ArrayList<>();
        for (int i = 0;i <obatList.size();i++){
            laporanObatModels.add(new LaporanObatModel(obatList.get(i).getObatId(),
                    obatList.get(i).getName(), 0,0,0,0));
        }
      //
    }

    public void setLayout(TableLayout tableLayout){
        int baris =1;
        for (int i = 0; i < laporanObatModels.size(); i++) {
            View view  = LayoutInflater.from(context).inflate(R.layout.list_obat_header, null,false);
            TextView txtNo = view.findViewById(R.id.txtNo);
            TextView txtName = view.findViewById(R.id.txtName);
            TextView txtJmlMasuk = view.findViewById(R.id.txtJmlMasuk);
            txtJmlMasuk.setText("0");
            TextView txtJmlKeluar = view.findViewById(R.id.txtJmlKeluar);
            txtJmlKeluar.setText("0");
            TextView txtSisaExp = view.findViewById(R.id.txtSisaExp);
            txtSisaExp.setText("0");
            TextView txtSisaStock = view.findViewById(R.id.txtSisaStock);
            txtSisaStock.setText("0");
            txtName.setText(laporanObatModels.get(i).getName());
            txtNo.setText(String.valueOf(baris++));
            getMasuk(txtJmlMasuk, laporanObatModels.get(i).getObatId(), i);
            getKeluar(txtJmlKeluar,laporanObatModels.get(i).getObatId() ,i);
            getSisaStock(txtSisaStock,laporanObatModels.get(i).getObatId(),i ) ;
            getExp(txtSisaExp, laporanObatModels.get(i).getObatId(),i);

            tableLayout.addView(view);
        }
    }



    private void getKeluar(TextView txtJmlKeluar, String obatId, int pos) {
//        for (int i = 0; i < laporanObatModels.size(); i++) {
//            LaporanObatModel lap = laporanObatModels.get(i);
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
                                            }else if (sumberId==listModel.getSumberId()){
                                                Log.d(TAG, "getKeluar: "+listModel.getSumberId());
                                                if (obatId.equals(listModel.getObatId())){
                                                    totalKeluar = totalKeluar+listModel.getJumlah();
                                                }
                                            }


                                        }
                                        LaporanObatModel lap = laporanObatModels.get(pos);
                                        lap.setJmlKeluar(totalKeluar);
                                        laporanObatModels.set(pos, lap);
                                            txtJmlKeluar.setText(String.valueOf(totalKeluar));
//                                        lap.setJmlKeluar(totalKeluar);
//                                        laporanObatModels.set(finalI, lap);
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
      //  }
    }

    public void getMasuk(TextView txtJmlMasuk, String obatId, int pos) {
//        for (int i = 0; i < laporanObatModels.size(); i++) {
//            LaporanObatModel lap = laporanObatModels.get(i);
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
                                                ListModel listModel = d.getValue(ListModel.class);
                                                listModel.setListId(d.getKey());
                                                if (obatId.equals(listModel.getObatId())){
                                                    totalMasuk = totalMasuk+listModel.getJumlah();
                                                }

                                            }

                                            LaporanObatModel lap  = laporanObatModels.get(pos);
                                            lap.setJmlMasuk(totalMasuk);
                                            laporanObatModels.set(pos, lap);
                                            txtJmlMasuk.setText(String.valueOf(totalMasuk));
//                                            lap.setJmlMasuk(totalMasuk);
//                                            laporanObatModels.set(finalI, lap);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }else {


                            Log.d(TAG, "getMasuk: "+masukModel.getSumberId() + " "+sumberId);
                            if (sumberId==masukModel.getSumberId()){

                                mDatabase.child("listDataMasuk").child(dataSnapshot.getKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot s) {
                                                for (DataSnapshot d : s.getChildren()){
                                                    ListModel listModel = d.getValue(ListModel.class);
                                                    listModel.setListId(d.getKey());
                                                    Log.d(TAG, "onDataChange22: "+listModel.getJumlah());
                                                    if (obatId.equals(listModel.getObatId())){
                                                        totalMasuk = totalMasuk+listModel.getJumlah();
                                                    }

                                                }
                                                LaporanObatModel lap  = laporanObatModels.get(pos);
                                                lap.setJmlMasuk(totalMasuk);
                                                laporanObatModels.set(pos, lap);
                                                txtJmlMasuk.setText(String.valueOf(totalMasuk));
//                                            lap.setJmlMasuk(totalMasuk);
//                                            laporanObatModels.set(finalI, lap);

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

      //  }
    }

    private void getSisaStock(TextView txtSisaStock, String obatId, int pos) {
//        for (int i = 0; i < laporanObatModels.size(); i++) {
//            LaporanObatModel lap = laporanObatModels.get(i);
        Query query =  mDatabase.child("listKeluar")
                .orderByChild("tglKeluar")
                .startAt(startDate)
                .endAt(endtDate);

        Query query1 =  mDatabase.child("listMasuk")
                .orderByChild("tglMasuk")
                .startAt(startDate)
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

                                                                        if (obatId.equals(  listModel.getObatId())){
                                                                            totalKeluar = totalKeluar+listModel.getJumlah();
                                                                        }

                                                                    }
                                                                    int m = totalMasuk - totalKeluar;

                                                                    LaporanObatModel lap  = laporanObatModels.get(pos);
                                                                    lap.setSisaStock(m);
                                                                    laporanObatModels.set(pos, lap);
                                                                    txtSisaStock.setText(String.valueOf(m));
//                                                                        lap.setSisaStock(m);
//                                                                        laporanObatModels.set(finalI, lap);
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
                    }else if (sumberId== masukModel.getSumberId()){
                        mDatabase.child("listDataMasuk").child(dataSnapshot.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot s) {
                                        for (DataSnapshot d : s.getChildren()){
                                            Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                                            ListModel listModel = d.getValue(ListModel.class);
                                            listModel.setListId(d.getKey());
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
                                                                        if (sumberId==listModel.getSumberId()){
                                                                            if (obatId.equals(  listModel.getObatId())){
                                                                                totalKeluar = totalKeluar+listModel.getJumlah();
                                                                            }
                                                                        }


                                                                    }
                                                                    int m = totalMasuk - totalKeluar;
                                                                    LaporanObatModel lap  = laporanObatModels.get(pos);
                                                                    lap.setSisaStock(m);
                                                                    laporanObatModels.set(pos, lap);
                                                                    txtSisaStock.setText(String.valueOf(m));
//                                                                        lap.setSisaStock(m);
//                                                                        laporanObatModels.set(finalI, lap);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //  }
    }
    private void getExp(TextView txtSisaExp, String obatId, int pos) {
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
                                            ListModel listModel = d.getValue(ListModel.class);
                                            listModel.setListId(d.getKey());
                                            if (listModel.getTglExp()<=endtDate) {
                                                if (obatId.equals(listModel.getObatId())){
                                                    totalMasuk = totalMasuk+listModel.getJumlah();
                                                }
                                            }


                                        }
//                                        txtSisaExp.setText(String.valueOf(totalMasuk));
//                                            lap.setJmlMasuk(totalMasuk);
//                                            laporanObatModels.set(finalI, lap);
                                        getExp2(txtSisaExp, totalMasuk,obatId, pos);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }else if (sumberId==masukModel.getSumberId()){
                        mDatabase.child("listDataMasuk").child(dataSnapshot.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot s) {
                                        for (DataSnapshot d : s.getChildren()){
                                            ListModel listModel = d.getValue(ListModel.class);
                                            listModel.setListId(d.getKey());
                                            Log.d(TAG, "onDataChange22: "+listModel.getJumlah());
                                            if (listModel.getTglExp()<=endtDate) {
                                                if (obatId.equals(listModel.getObatId())){
                                                    totalMasuk = totalMasuk+listModel.getJumlah();
                                                }
                                            }

                                        }
                                        //txtSisaExp.setText(String.valueOf(totalMasuk));
//                                            lap.setJmlMasuk(totalMasuk);
//                                            laporanObatModels.set(finalI, lap);

                                        getExp2(txtSisaExp, totalMasuk,obatId, pos);
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
    private void getExp2(TextView txtJmlMasuk, int totalMasuk, String obatId, int pos) {

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
                                            if (listModel.getTglExp()<=endtDate) {
                                                if (obatId.equals(  listModel.getObatId())){
                                                    totalKeluar = totalKeluar+listModel.getJumlah();
                                                }
                                            }

                                        }else if (sumberId==listModel.getSumberId()){
                                            if (listModel.getTglExp()<=endtDate) {
                                                if (obatId.equals(  listModel.getObatId())){
                                                    totalKeluar = totalKeluar+listModel.getJumlah();
                                                }
                                            }

                                        }


                                    }
                                    int m=totalMasuk -totalKeluar;
                                    LaporanObatModel lap  = laporanObatModels.get(pos);
                                    if (m>0){
                                        lap.setStockExp(totalMasuk);
                                        txtJmlMasuk.setText(String.valueOf(m));
                                    }else {
                                        lap.setStockExp(0);
                                        txtJmlMasuk.setText(String.valueOf(0));
                                    }
                                    laporanObatModels.set(pos, lap);

//                                        lap.setJmlKeluar(totalKeluar);
//                                        laporanObatModels.set(finalI, lap);
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

    public void createPrint() {
        Constant constant = new Constant(context);
        WebView myWebView = new WebView(context);
        StringBuilder stringBuilder = new StringBuilder();
        String top ="<!DOCTYPE html>\n" +
                "<html>\n" +
                " <head>\n" +
                "  <title>"+context.getString(R.string.app_name)+"</title>\n" +
                "  <style type=\"text/css\">\n" +
                "    table,th, td{\n" +
                "        padding: 5px;\n" +
                "    border: 1px solid black;\n" +
                "    border-collapse: collapse; }\n" +
                "    table{ width: 100%; }\n" +
                "    th, td{ text-align:center; }\n" +
                "  </style>\n" +
                " </head>\n" +
                "<body>\n" ;
        stringBuilder.append(top);
        String m;
        if (sumberId==0){
            m = "<center> <h3> LAPORAN OBAT TANGGAL "
                    + constant.changeFromLong2(startDate) + " - " + constant.changeFromLong2(endtDate) + "</center> <h3> \n";
        } else {
            m = "<center> <h3> LAPORAN OBAT SUMBER " + constant.getSumberNameById(sumberId) + " TANGGAL "
                    + constant.changeFromLong2(startDate) + " - " + constant.changeFromLong2(endtDate) + "</center> <h3> \n";

        }
        stringBuilder.append(m);
        String body=  " <table>\n" +
                "        <tbody>\n" +
                "            <thead>\n" +
                "                <tr>\n" +
                "                    <th>No</th> \n" +
                "                    <th>Nama Obat</th> \n" +
                "                    <th>Jumlah Masuk</th> \n" +
                "                    <th>Jumlah Keluar</th> \n" +
                "                    <th>Stock Expired</th> \n" +
                "                    <th>Sisa Stock</th>\n" +
                "                </tr>\n" +
                "            </thead>\n" +
                "            <tbody>";
        stringBuilder.append(body);
        int baris=1;
        for (int i = 0; i<laporanObatModels.size(); i ++){
            String content = "<tr>\n" +
                    "                    <td>"+baris+++"</td> \n" +
                    "                    <td>"+laporanObatModels.get(i).getName()+"</td> \n" +
                    "                    <td>"+laporanObatModels.get(i).getJmlMasuk()+"</td>\n" +
                    "                    <td>"+laporanObatModels.get(i).getJmlKeluar()+"</td>\n" +
                    "                    <td>"+laporanObatModels.get(i).getStockExp()+"</td>\n" +
                    "                    <td>"+laporanObatModels.get(i).getSisaStock()+"</td>\n" +
                    "                </tr>";
            stringBuilder.append(content);
        }

        String bot = "</tbody>\n" +
                "        </tbody>\n" +
                "    </table>\n" +
                "    <br>\n" +
                "    <br>\n" +
                "    <br>\n" +
                "    <div style=\"float:left\">\n" +
                "        <center>\n" +
                "            <small>MENGETAHUI</small>\n" +
                "            <br>\n" +
                "            <small>KEPALA UPTD INSTALASI FARMASI</small>\n" +
                "            <br>\n" +
                "            <small>KOTA PADANG PANJANG</small>\n" +
                "            <br>\n" +
                "            <br>\n" +
                "            <br>\n" +
                "    <small>"+getNamaKepUptd()+"\t     </small>\n" +
                "    <br>\n" +
                "    <small>NIP. "+getNipKepUptd()+"\t </small>\n" +
                "        </center>\n" +
                "    </div>\n" +
                "    <div style=\"float:right\">\n" +
                "        <center>\n" +
                "            <small>Padang Panjang, "+constant.changeFromLong2(System.currentTimeMillis())+"</small>\n" +
                "            <br>\n" +
                "            <small>Bagian Penyimpanan</small>\n" +
                "            <br> \n" +
                "            <br>\n" +
                "            <br>\n" + "    <br>\n" +
                "    <small>"+getNamaBag()+" \t\t     </small>\n" +
                "    <br>\n" +
                "    <small>NIP. "+getNipBag()+"\t     </small>\n" +
                "        </center>\n" +
                "    </div>\n" +
                "</html>";

        stringBuilder.append(bot);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                createWebPrintJob(myWebView);
                //printMutasi(myWebView);
//                createWebPrintJob(myWebView);
                //  printLibrary(myWebView);
                //  printMutasi(myWebView);
                //if page loaded successfully then show print button
                //   findViewById(R.id.fab).setVisibility(View.VISIBLE);
            }
        });
        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress==100){

                }
                super.onProgressChanged(view, newProgress);
            }
        });
        myWebView.loadData(stringBuilder.toString(), "text/HTML", "UTF-8");
    }

    private void createWebPrintJob(WebView webView) {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);

        String jobName = context.getString(R.string.app_name) ;
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setMediaSize(PrintAttributes.MediaSize.NA_GOVT_LETTER);
        printManager.print(jobName, printAdapter,builder.build());
    }

    private String getNipBag(){
        for (int i =0; i<karyawanModels.size();i++){
            if (karyawanModels.get(i).getJabatan()==2){
                return String.valueOf(karyawanModels.get(i).getNip());
            }
        }
        return null;
    }

    private String getNamaBag(){
        for (int i =0; i<karyawanModels.size();i++){
            if (karyawanModels.get(i).getJabatan()==2){
                return karyawanModels.get(i).getNama();
            }
        }
        return null;
    }

    private String getNamaKepUptd(){
        for (int i =0; i<karyawanModels.size();i++){
            if (karyawanModels.get(i).getJabatan()==1){
                return karyawanModels.get(i).getNama();
            }
        }
        return null;
    }

    private String getNipKepUptd(){
        for (int i =0; i<karyawanModels.size();i++){
            if (karyawanModels.get(i).getJabatan()==1){
                return String.valueOf(karyawanModels.get(i).getNip());
            }
        }
        return null;
    }
}
