package com.rentalapp.sisteminformasilogistikobat.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.rentalapp.sisteminformasilogistikobat.Adapter.JabatanModel;
import com.rentalapp.sisteminformasilogistikobat.Model.FaskesModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SumberDanaModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Constant {
    private Context context;

    public Constant(Context context) {
        this.context = context;
        getSumberDana();
        getFaskesModels();
        addJabatan();
    }

    public static String about_app="about app";
    public static int maxStockSisa = 17;
    private static String userId= "userId";
    private static String level= "level";
    private static SharedPreferences mySharedPreferences;
    private static String PREF = "pref";

    public static void setLevel(Context context, int l){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putInt(level, l);
        myEditor.commit();
    }

    public static int getLevel(Context context){
        mySharedPreferences = context.getSharedPreferences(PREF, 0);
        return mySharedPreferences.getInt(level,0);
    }


    public static void setUserId(Context context, String url){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putString(userId, url);
        myEditor.commit();
    }

    public static String getUserId(Context context){
        mySharedPreferences = context.getSharedPreferences(PREF, 0);
        return mySharedPreferences.getString(userId,null);
    }


    public long changeYyyyMMDDtoMili(String tgl){
        SimpleDateFormat sf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sf.parse (tgl);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public String changeFromLong(long date){
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormater.format(date);
    }
    public String changeFromLong2(long date){
        SimpleDateFormat dateFormater = new SimpleDateFormat("dd MMMM yyyy");
        return dateFormater.format(date);
    }

    public String changeFromDate(Date date){
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormater.format(date);
    }

    private ArrayList<SumberDanaModel> sumberDanaModels = new ArrayList<>();
    public ArrayList<ObatModel> obatModels ;

    public void setListObatAlkes(ArrayList<ObatModel> obatModels){
        this.obatModels = obatModels;
    }


    public String getObatId(int pos){
        return obatModels.get(pos).getObatId();
    }

    public String[] getObatNama(){
        String[] namaObat   = new String[obatModels.size()];
        for (int i =0; i<obatModels.size(); i++){
            namaObat[i] =obatModels.get(i).getName();
        }
        return namaObat;
    }

    public String getObatPack(int pos){
        return obatModels.get(pos).getPack();
    }

    public ArrayList<SumberDanaModel> getSumberDana(){
        sumberDanaModels.add(new SumberDanaModel(1, "APBD I"));
        sumberDanaModels.add(new SumberDanaModel(2, "APBD II"));
        sumberDanaModels.add(new SumberDanaModel(3, "PROGRAM"));
        sumberDanaModels.add(new SumberDanaModel(4, "DAK"));
        return sumberDanaModels;
    }

    public String[]  getSumberDanaNama(boolean isMutasi){
        String[] namaSumber;

        if (isMutasi){
            namaSumber   = new String[sumberDanaModels.size()+1];
            namaSumber[0] = "Semua Sumber";
            for (int i =1; i<sumberDanaModels.size()+1; i++){
                namaSumber[i] =sumberDanaModels.get(i-1).getName();
            }
        }else {
            namaSumber   = new String[sumberDanaModels.size()];
            for (int i =0; i<sumberDanaModels.size(); i++){
                namaSumber[i] =sumberDanaModels.get(i).getName();
            }
        }

        return namaSumber;
    }

    public int getSumberId(int pos,boolean isMutasi){
        if (isMutasi) {
            if (pos==0) {
                return 0;
            }else {
                return  sumberDanaModels.get(pos-1).getSumberId();
            }
        }
        return sumberDanaModels.get(pos).getSumberId();
    }

    public String getSumberNameById(int id){
        for (int i =0; i<sumberDanaModels.size(); i++){
            if (sumberDanaModels.get(i).getSumberId()==id){
                return sumberDanaModels.get(i).getName();
            }
        }
        return null;
    }

    public String getObatNameById(String obatId) {
        for (int i =0; i<obatModels.size(); i++){
            if (obatModels.get(i).getObatId().equals(obatId)){
                return obatModels.get(i).getName();
            }
        }

        return null;
    }

    public ArrayList <SupplierModel> supplierModels;

    public void setlistSupplier(ArrayList<SupplierModel> supplierModels) {
        this.supplierModels = supplierModels;
    }

    public String[] getSupplierNama(){
        String[] namaSupplier   = new String[supplierModels.size()];
        for (int i =0; i<supplierModels.size(); i++){
            namaSupplier[i] =supplierModels.get(i).getName();
        }
        return namaSupplier;
    }

    public String getSupplierNameById(String id) {
        for (int i =0; i<supplierModels.size(); i++){
            if (supplierModels.get(i).getSupplierId().equals(id)){
                return supplierModels.get(i).getName();
            }
        }

        return null;
    }

    public String getSupplierId(int pos){
        return supplierModels.get(pos).getSupplierId();
    }

    public ArrayList<FaskesModel> faskesModels;

    public ArrayList<FaskesModel> getFaskesModels(){
        faskesModels = new ArrayList<>();
        faskesModels.add(new FaskesModel(1,"IGD" ));
        faskesModels.add(new FaskesModel(2,"KBS" ));
        faskesModels.add(new FaskesModel(3,"GNG" ));
        faskesModels.add(new FaskesModel(4,"KTK" ));
        faskesModels.add(new FaskesModel(5,"RSUD" ));
        faskesModels.add(new FaskesModel(6,"LAIN2" ));
        return faskesModels;
    }


    public String[] getFaskesName(){
        String[] namaFaskes   = new String[faskesModels.size()];
        for (int i =0; i<faskesModels.size(); i++){
            namaFaskes[i] =faskesModels.get(i).getFaskesName();
        }
        return namaFaskes;
    }

    public String getFaskesNameById(int id) {
        for (int i =0; i<faskesModels.size(); i++){
            if (faskesModels.get(i).getFaskesId()==id){
                return faskesModels.get(i).getFaskesName();
            }
        }

        return null;
    }

    private ArrayList<JabatanModel> jabatanModels = new ArrayList<>();

    private void addJabatan(){
        jabatanModels.add(new JabatanModel(1,"KEPALA UPTD INSTALASI FARMASI"));
        jabatanModels.add(new JabatanModel(2,"BAGIAN PENYIMPANAN I"));
        jabatanModels.add(new JabatanModel(3,"STAFF"));
    }

    public String[] getJabatanName(){
        String[] jabatan   = new String[jabatanModels.size()];
        for (int i =0; i<jabatanModels.size(); i++){
            jabatan[i] =jabatanModels.get(i).getJabatan();
        }
        return jabatan;
    }

    public int getJabatanId(int pos){
        return jabatanModels.get(pos).getJabatanId();
    }

    public String getJabatanNameById(double id){
        for (int i =0; i<jabatanModels.size(); i++){
            if (id==jabatanModels.get(i).getJabatanId()) {
                return jabatanModels.get(i).getJabatan();
            }

        }
        return null;
    }
}
