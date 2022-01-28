package com.rentalapp.sisteminformasilogistikobat.Util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.print.PageRange;
import android.print.PdfPrint;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.rentalapp.sisteminformasilogistikobat.Model.KaryawanModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MutasiModel;
import com.rentalapp.sisteminformasilogistikobat.R;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.google.firebase.storage.StorageTaskScheduler.sInstance;

public class PrintArea {
    private String TAG ="PrintAreaTAG";
    private Context context;
    private ArrayList<MutasiModel> mutasiModels;
    private int sumberId;
    private long startDate;
    private long endDate;
    private ArrayList<KaryawanModel> karyawanModels;
    public PrintArea(Context context, ArrayList<MutasiModel> mutasiModels,
                     int sumberId, long startDate, long endDate, ArrayList<KaryawanModel> karyawanModels) {
        this.context = context;
        this.mutasiModels = mutasiModels;
        this.sumberId =sumberId;
        this.startDate =startDate;
        this.endDate =endDate;
        this.karyawanModels = karyawanModels;
        createPrint();
    }

    private void createPrint() {
        Constant constant = new Constant(context);
       WebView myWebView = new WebView(context);
        //add webview client to handle event of loading
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
                "    table{ width: 30%; }\n" +
                "    th, td{ text-align:center; }\n" +
                "  </style>\n" +
                " </head>\n" +
                "<body>\n" ;
        stringBuilder.append(top);

                if (sumberId==0){
                   String m =  "<center> <h3> DAFTAR MUTASI OBAT/ALKES   </center> <h3> \n"  ;
                   stringBuilder.append(m);
                    String p;
                    if (endDate ==0){
                        p = "<center><h3> TANGGAL " + constant.changeFromLong(System.currentTimeMillis()) + "<h3>  </center> ";
                    }else {

                        p = "<center><h3> TANGGAL " + constant.changeFromLong(startDate) + "/  " + constant.changeFromLong(endDate) + "<h3>  </center> ";
                    }
                    stringBuilder.append(p);
                }else {
                 String m =    "<center> <h3> DAFTAR MUTASI OBAT SUMBER  "+constant.getSumberNameById(sumberId)+" </center> <h3> \n" ;
                    stringBuilder.append(m);
                    String  p = "<center><h3> TANGGAL " + constant.changeFromLong(startDate) + "  /  " + constant.changeFromLong(endDate) + "<h3>  </center> ";
                    stringBuilder.append(p);
                }


              String body=  " <table>\n" +
                "    <thead>\n" +
                "        <tr>\n" +
                "            <th rowspan=\"2\">NO</th>\n" +
                "            <th rowspan=\"2\">NAMA OBAT/ALKES </th>\n" +
                "            <th rowspan=\"2\">KEMASAN </th>\n" +
                "            <th rowspan=\"2\">STOCK AWAL </th>\n" +
                "            <th rowspan=\"2\">MASUK</th>\n" +
                "            <th colspan=\"6\">PENGELUARAN</th>\n" +
                "            <th rowspan=\"2\">JUMLAH</th>\n" +
                "            <th rowspan=\"2\">SISA STOCK</th>\n" +
                "            <th rowspan=\"2\">KET</th>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <th>IGD </th>\n" +
                "            <th>KBS</th>\n" +
                "            <th>GNG</th>\n" +
                "            <th>KTK</th>\n" +
                "            <th>RSUD</th>\n" +
                "            <th>LAIN2</th>\n" +
                "          </tr>\n" +
                "    </thead>\n" +
                "    <tbody>";
        stringBuilder.append(body);

        int baris=1;
        for (int i = 0; i<mutasiModels.size(); i ++){
            String content ="  <tr>\n" +
                    "            <td>"+ baris++ +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getName() +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getPack() +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getStockAwal() +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getMasuk() +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getpIGD() +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getpKBS() +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getpGNG() +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getpKTK() +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getpRSUD() +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getpLain() +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getJumlah() +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getSisaStock() +"</td>\n" +
                    "            <td> "+mutasiModels.get(i).getKet() +"</td>\n" +
                    "        </tr>";
            stringBuilder.append(content);
        }

        String bot = "</tbody>\n" +
                "   \n" +
                " </table>\n" +
                " \n" +
                "<br>\n" +
                "<br>\n" +
                "<br> \n" +
                " <div  style=\"float:left\">\n" +
                "  <center>\n" +
                "    <small>MENGETAHUI</small>\n" +
                "    <br>\n" +
                "    <small>KEPALA UPTD INSTALASI FARMASI</small>\n" +
                "    <br>\n" +
                "    <small>KOTA PADANG PANJANG\t   </small>\n" +
                "\n" +
                "    <br>\n" +
                "    <br>\n" +
                "    <br>\n" +
                "    <small>"+getNamaKepUptd()+"\t     </small>\n" +
                "    <br>\n" +
                "    <small>NIP. "+getNipKepUptd()+"\t </small>\n" +
                "  </center>\n" +
                "\n" +
                " </div>\n" +
                "\n" +
                " \n" +
                " <div style=\"float:right\">\n" +
                "  <center>\n" +
                "    <small>PADANG PANJANG, "+constant.changeFromLong2(System.currentTimeMillis())+"</small>\n" +
                "    <br>\n" +
                "    <small>BAGIAN PENYIMPANAN I\t\t   </small> \n" +
                "    <br>\n" +
                "    <br>\n" +
                "    <br>\n" +
                "    <br>\n" +
                "    <small>"+getNamaBag()+" \t\t     </small>\n" +
                "    <br>\n" +
                "    <small>NIP. "+getNipBag()+"\t     </small>\n" +
                "  \n" +
                "  </center>\n" +
                "   </div></body>\n" +
                "</html>";

        stringBuilder.append(bot);
         //load your html to webview

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



    private void createWebPrintJob(WebView webView) {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);

        String jobName = context.getString(R.string.app_name) ;
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setMediaSize(PrintAttributes.MediaSize.NA_GOVT_LETTER);
        printManager.print(jobName, printAdapter,builder.build());
    }
}
