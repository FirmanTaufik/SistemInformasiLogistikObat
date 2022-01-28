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
import com.rentalapp.sisteminformasilogistikobat.Model.LaporanMasukModel;
import com.rentalapp.sisteminformasilogistikobat.Model.LaporanObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ListModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MasukModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;
import com.rentalapp.sisteminformasilogistikobat.R;

import java.util.ArrayList;

public class PrintSupplier {
    private String TAG = "PrintSupplierTAG";
    private Context context;
    private final DatabaseReference mDatabase;
    private long startDate;
    private long endtDate;
    private ArrayList<KaryawanModel> karyawanModels;
    private ArrayList<ObatModel> obatList;
    private ArrayList<SupplierModel> laporanSupplierModels;
    private  WebView myWebView;
    private ArrayList<LaporanMasukModel> laporanMasukModels  ;
    private int sumberId;
    public PrintSupplier(Context context, DatabaseReference mDatabase, long startDate, long endtDate,
                         ArrayList<KaryawanModel> karyawanModels, ArrayList<ObatModel> obatList,
                         ArrayList<SupplierModel> supplierModels, int sumberId) {
        this.context = context;
        this.mDatabase = mDatabase;
        this.startDate = startDate;
        this.endtDate = endtDate;
        this.karyawanModels = karyawanModels;
        this.obatList = obatList;
        this.laporanSupplierModels = supplierModels;
        this.sumberId = sumberId;
    }

    public void getMasuk(TableLayout tableLayout) {
        laporanMasukModels = new ArrayList<>();
        Log.d(TAG, "getMasuk: "+startDate);

        Query query = mDatabase.child("listMasuk")
                .orderByChild("tglMasuk")
                .startAt(startDate)
                .endAt(endtDate);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            ListModel listModel = d.getValue(ListModel.class);
                                            listModel.setListId(d.getKey());

                                            if (sumberId==0){
                                                LaporanMasukModel lm = new LaporanMasukModel();
                                                lm.setSupplierId(masukModel.getSupplierId());
                                                lm.setObatId(listModel.getObatId());
                                                if (laporanMasukModels.size()!=0){
                                                    if (!checkingAvai(lm.getObatId(),  masukModel.getSupplierId(), listModel.getJumlah()) ){
                                                        lm.setJmlObat(listModel.getJumlah());
                                                        laporanMasukModels.add(lm);
                                                    }
                                                }else {
                                                    lm.setSupplierId(masukModel.getSupplierId());
                                                    lm.setObatId(listModel.getObatId());
                                                    lm.setJmlObat(listModel.getJumlah());
                                                    laporanMasukModels.add(lm);
                                                    Log.d(TAG, "masukModel: "+masukModel.getSupplierId());
                                                }
                                                Log.d(TAG, "onDataChange: "+lm.getSupplierId());
                                            }else if (sumberId == masukModel.getSumberId()){
                                                LaporanMasukModel lm = new LaporanMasukModel();
                                                lm.setSupplierId(masukModel.getSupplierId());
                                                lm.setObatId(listModel.getObatId());
                                                if (laporanMasukModels.size()!=0){
                                                    if (!checkingAvai(lm.getObatId(), masukModel.getSupplierId(), listModel.getJumlah()) ){
                                                        laporanMasukModels.add(lm);
                                                    }
                                                }else {
                                                    laporanMasukModels.add(lm);
                                                }
                                            }


                                        }
                                        createPrint();
                                        setLayout(tableLayout);

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

    private boolean checkingAvai(String obatId, String supplierId, int jumlah) {
        for (int i = 0; i < laporanMasukModels.size(); i++) {
            if (laporanMasukModels.get(i).getObatId().equals(obatId) &&
                laporanMasukModels.get(i).getSupplierId().equals(supplierId)){
                LaporanMasukModel lm = laporanMasukModels.get(i);
                int jml = lm.getJmlObat()+jumlah;
                Log.d(TAG, "checkingAvai: "+getObatName(obatId) + " "+ jumlah);
                lm.setJmlObat(jml);
                laporanMasukModels.set(i, lm);
                return true;
            }
        }
        return false;
    }

    public void setLayout(TableLayout tableLayout) {
        tableLayout.removeAllViews();
        View v  = LayoutInflater.from(context).inflate(R.layout.list_supplier_header, null,false);
        tableLayout.addView(v);
        int baris=1;
        for (int i = 0; i < laporanSupplierModels.size(); i++) {

            ArrayList <LaporanMasukModel> lm =  getListById(laporanSupplierModels.get(i).getSupplierId());
           if (lm.size()!=0){
               Log.d(TAG, "setLayout: "+laporanSupplierModels.get(i).getSupplierId());
                View view  = LayoutInflater.from(context).inflate(R.layout.list_supplier_body_1, null,false);
                TextView txtNo = view.findViewById(R.id.txtNo);
                TextView txtName = view.findViewById(R.id.txtName);
                TableLayout tableLayout1 = view.findViewById(R.id.tableLayout);
                txtNo.setText(String.valueOf(baris++));
                txtName.setText(laporanSupplierModels.get(i).getName());

                int baris2 = 1;
                for (int j = 0; j <lm.size() ; j++) {
                    View view2= LayoutInflater.from(context).inflate(R.layout.list_supplier_body_2, null,false);
                    TextView txtNo2 = view2.findViewById(R.id.txtNo);
                    TextView txtName2 = view2.findViewById(R.id.txtName);
                    TextView txtPack = view2.findViewById(R.id.txtPack);
                    TextView txtJmlObat = view2.findViewById(R.id.txtJmlObat);

                    txtNo2.setText(String.valueOf(baris2++));
                    txtName2.setText(getObatName(lm.get(j).getObatId()));
                    txtPack.setText(getObatPack(lm.get(j).getObatId()));
                    txtJmlObat.setText(String.valueOf(getJumlahObat(laporanSupplierModels.get(i).getSupplierId(),
                            lm.get(j).getObatId()) ));
                    tableLayout1.addView(view2);
                }
                tableLayout.addView(view);
            }
        }
    }

    private int getJumlahObat(String supplierId, String obatId) {
        for (int i = 0; i < laporanMasukModels.size(); i++) {
            if (laporanMasukModels.get(i).getSupplierId().equals(supplierId)){
                if (obatId.equals(laporanMasukModels.get(i).getObatId())){
                    Log.d(TAG, "getJumlahObat: "+laporanMasukModels.get(i).getJmlObat());
                    return laporanMasukModels.get(i).getJmlObat();
                }
            }
        }
        return 0;
    }

    public void createPrint() {
        Constant constant = new Constant(context);
          myWebView = new WebView(context);
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
        String m = ""   ;
        if (sumberId==0){
            m = "<center> <h3> LAPORAN SUPPLIER TANGGAL "
                    + constant.changeFromLong2(startDate) + " - " + constant.changeFromLong2(endtDate) + "</center> <h3> \n";
        } else {
            m = "<center> <h3> LAPORAN SUPPLIER SUMBER " + constant.getSumberNameById(sumberId) + " TANGGAL "
                    + constant.changeFromLong2(startDate) + " - " + constant.changeFromLong2(endtDate) + "</center> <h3> \n";

        }
        stringBuilder.append(m);
        String body=  " <table>\n" +
                "        <tr>\n" +
                "            <td rowspan=\"2\">No</td>\n" +
                "            <td rowspan=\"2\">DISTRIBUTOR/PENYEDIA</td>\n" +
                "            <td colspan=\"4\">RICIAN KONTRAK</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td>No</td>\n" +
                "            <td>Nama Obat</td>\n" +
                "            <td>Satuan</td>\n" +
                "            <td>Jumlah Obat</td>\n" +
                "        </tr>\n";
        stringBuilder.append(body);
        int baris=1;
        for (int i = 0; i < laporanSupplierModels.size(); i++) {
            Log.d(TAG, "createPrintwwww: "+laporanSupplierModels.get(i).getSupplierId());
            ArrayList <LaporanMasukModel> lm =  getListById(laporanSupplierModels.get(i).getSupplierId());

            if (lm.size()!=0){



                // Log.d(TAG, "onDataChangeo: "+laporanSupplierModels.get(i).getObatModels().size());

                String content = "<tr class=\"baru\">\n" +
                        "            <td rowspan=\""+lm.size()+"\">"+baris+++"</td>\n" +
                        "            <td rowspan=\""+lm.size()+"\">"+laporanSupplierModels.get(i).getName()+"</td>\n"  ;

                stringBuilder.append(content);
                //StringBuilder stringBuilders = new StringBuilder();
                int bariss=2;
                for (int j = 0; j <lm.size() ; j++) {

                    if (j==0){
                        stringBuilder.append(" <td>1</td>\n" +
                                "            <td>"+ getObatName(lm.get(0).getObatId())+"</td>\n" +
                                "            <td>"+getObatPack(lm.get(0).getObatId())+"</td> \n" +
                                "            <td>"+getJumlahObat(laporanSupplierModels.get(i).getSupplierId(),  lm.get(0).getObatId()) +"</td> \n" +
                                "        </tr>\n" );
                    }else {
                        stringBuilder.append(   "<tr>\n" +
                                "           <td>"+bariss+++"</td>\n" +
                                "            <td>"+ getObatName(lm.get(j).getObatId())+"</td>\n" +
                                "          <td>"+getObatPack(lm.get(j).getObatId())+"</td> \n" +
                                "            <td>"+getJumlahObat(laporanSupplierModels.get(i).getSupplierId(),  lm.get(j).getObatId()) +"</td> \n" +
                                "        </tr>\n");
                    }

                }
            }

        }


        String bot = "" +
                "    </table>\n" +
                "    <br>\n" +
                "    <br>\n" +
                "    <br>\n" +
                "    <div style=\"float:right\">\n" +
                "        <center>\n" +
                "            <small>Padang Panjang, "+constant.changeFromLong2(System.currentTimeMillis())+"</small>\n" +
                "            <br>\n" +
                "            <small>Penginput</small>\n" +
                "            <br> \n" +
                "            <small>Ka. IFK Padang Panjang</small>\n" +
                "            <br> \n" +
                "            <br>\n" +
                "            <br>\n" + "    <br>\n" +
                "    <small> Muhammad Arfani Ssi.Apt  </small>\n" +
                "    <br>\n" +
                "    <small>NIP. 196810101998031006 </small>\n" +
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
        Log.d(TAG, "createPrintttt: "+stringBuilder.toString());
        myWebView.loadData(stringBuilder.toString(), "text/HTML", "UTF-8");
    }

    private ArrayList<LaporanMasukModel> getListById(String supplierId){
        Log.d(TAG, "getListById: "+supplierId);
        ArrayList<LaporanMasukModel> laporanMasuk =  new ArrayList<>();
        for (int i = 0; i < laporanMasukModels.size(); i++) {
            if (laporanMasukModels.get(i).getSupplierId().equals(supplierId)){
                 laporanMasuk.add(laporanMasukModels.get(i));
            }

        }
        return laporanMasuk;
    }

    private String getIsi() {
        return  "   <td></td>\n" +
                "            <td>-</td>\n" +
                "            <td>-</td> \n" +
                "        </tr>";
    }

    public void createWebPrintJob() {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);

        String jobName = context.getString(R.string.app_name) ;
        PrintDocumentAdapter printAdapter = myWebView.createPrintDocumentAdapter(jobName);
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

    private String getObatPack(String idObat){
        for (int i = 0; i < obatList.size(); i++) {
            if (idObat.equals(obatList.get(i).getObatId())){
                return obatList.get(i).getPack();
            }
        }
        return null;
    }

    private String getObatName(String idObat){
        for (int i = 0; i < obatList.size(); i++) {
            if (idObat.equals(obatList.get(i).getObatId())){
                return obatList.get(i).getName();
            }
        }
        return null;
    }


}
