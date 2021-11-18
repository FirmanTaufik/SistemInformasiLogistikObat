package com.rentalapp.sisteminformasilogistikobat.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Model.KeluarModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MasukModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MutasiModel;
import com.rentalapp.sisteminformasilogistikobat.R;

import java.util.ArrayList;

public class MutasiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG ="MutasiAdapterTAG";
    private static final int LAYOUT_ONE = 0;
    private static final int LAYOUT_TWO = 1;
    private Context context;
    private ArrayList<MutasiModel> mutasiModels;
    private DatabaseReference mDatabase;
    private long startDate;
    private long endtDate;
    private int sumberId;

    @Override
    public int getItemViewType(int position)
    {
        if(position==0)
            return LAYOUT_ONE;
        else
            return LAYOUT_TWO;
    }

    public MutasiAdapter(Context context, ArrayList<MutasiModel> mutasiModels,
                         long startDate, long endtDate,int sumberId ) {
        this.context = context;
        this.mutasiModels = mutasiModels;
        this.startDate = startDate;
        this.endtDate= endtDate;
        this.sumberId = sumberId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        if(viewType==LAYOUT_ONE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_mutasi_header,parent,false);
            viewHolder = new ViewHolderOne(view);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_mutasi_body,parent,false);
            viewHolder= new ViewHolderTwo(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(holder.getItemViewType() == LAYOUT_ONE) {

        }else {
            ViewHolderTwo viewHolderTwo = (ViewHolderTwo) holder;
            viewHolderTwo.txtName.setText(mutasiModels.get(position).getName());
            viewHolderTwo.txtPack.setText(mutasiModels.get(position).getPack());

        }
//        getMasuk(holder, position);
//        getKeluar(holder,position);
//
//        getPengeluaranFaskes(holder.txtIGD, position, 1);
//        getPengeluaranFaskes(holder.txtKBS, position, 2);
//        getPengeluaranFaskes(holder.txtGNG, position, 3);
//        getPengeluaranFaskes(holder.txtKTK, position, 4);
//        getPengeluaranFaskes(holder.txtRSUD, position, 5);
//        getPengeluaranFaskes(holder.txtLAIN, position, 6);
//
//        getStockAwal(holder, position);


    }

//    private void getStockAwal(ViewHolder holder, int position) {
//        Query query1 =  mDatabase.child("stockMasuk")
//                .orderByChild("tglMasuk")
//                .startAt(0)
//                .endAt(startDate);
//
//        Query query =  mDatabase.child("stockKeluar")
//                .orderByChild("tglKeluar")
//                .startAt(0)
//                .endAt(startDate);
//
//        query1.addListenerForSingleValueEvent(new ValueEventListener() {
//            int totalMasuk=0;
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    MasukModel masukModel = dataSnapshot.getValue(MasukModel.class);
//                    masukModel.setMasukId(dataSnapshot.getKey());
////                    if (masukModel.getObatId().equals(mutasiModels.get(position).getObatId())){
////                        if (sumberId==0){
////                            totalMasuk=totalMasuk+masukModel.getJmlMasuk();
////                        }else  if (sumberId == masukModel.getSumberId()){
////                            totalMasuk=totalMasuk+masukModel.getJmlMasuk();
////                        }
////                    }
//                }
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    int totalKeluar=0;
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                            KeluarModel keluarModel = dataSnapshot.getValue(KeluarModel.class);
//                            keluarModel.setKeluarId(dataSnapshot.getKey());
////                            if (keluarModel.getObatId().equals(mutasiModels.get(position).getObatId())){
////                                if (sumberId==0){
////                                    totalKeluar = totalKeluar+keluarModel.getJmlKeluar();
////                                }else  if (sumberId == keluarModel.getSumberId()){
////                                    totalKeluar = totalKeluar+keluarModel.getJmlKeluar();
////                                }
////
////                            }
//                        }
//                        Log.d(TAG, "onDataChangeddd: " +"dari "+totalMasuk +" hingga "+totalKeluar);
//                        int hasil = totalMasuk - totalKeluar;
//                        holder.txtStockAwal.setText(String.valueOf(hasil));
//                        MutasiModel muta = mutasiModels.get(position);
//                        muta.setStockAwal(hasil);
//                        mutasiModels.set(position, muta);
//                        getSisaStock(holder.txtSisa, position);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//
//
//    }

    private void getSisaStock(TextView textView, int position) {
        Log.d(TAG, "getSisaStock: "+mutasiModels.get(position).getStockAwal());
        int sisa = (mutasiModels.get(position).getStockAwal()+ mutasiModels.get(position).getMasuk()) - mutasiModels.get(position).getJumlah();
        textView.setText(String.valueOf(sisa));
    }

//    private void getKeluar(ViewHolder holder, int position) {
//        Query query =  mDatabase.child("stockKeluar")
//                .orderByChild("tglKeluar")
//                .startAt(startDate)
//                .endAt(endtDate);
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            int totalKeluar=0;
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    KeluarModel keluarModel = dataSnapshot.getValue(KeluarModel.class);
//                    keluarModel.setKeluarId(dataSnapshot.getKey());
////                    if (keluarModel.getObatId().equals(mutasiModels.get(position).getObatId())){
////                        if (sumberId==0){
////                            totalKeluar=totalKeluar+keluarModel.getJmlKeluar();
////                        }else if (sumberId == keluarModel.getSumberId()){
////                            totalKeluar=totalKeluar+keluarModel.getJmlKeluar();
////                        }
////                    }
//                }
//                holder.txtJumlah.setText(String.valueOf(totalKeluar));
//                MutasiModel muta = mutasiModels.get(position);
//                muta.setJumlah(totalKeluar);
//                Log.d(TAG, "onDataChangeJKum: "+mutasiModels.get(position).getName());
//                mutasiModels.set(position, muta);
//                getSisaStock(holder.txtSisa, position);
//                Log.d(TAG, "onDataChange: "+totalKeluar);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

//    public void getMasuk(ViewHolder holder, int position) {
//        Query query =  mDatabase.child("stockMasuk")
//                .orderByChild("tglMasuk")
//                .startAt(startDate)
//                .endAt(endtDate);
//
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            int totalMasuk=0;
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    MasukModel masukModel = dataSnapshot.getValue(MasukModel.class);
//                    masukModel.setMasukId(dataSnapshot.getKey());
////                    if (masukModel.getObatId().equals(mutasiModels.get(position).getObatId())){
////                        if (sumberId==0){
////                            totalMasuk=totalMasuk+masukModel.getJmlMasuk();
////                        }else if (sumberId == masukModel.getSumberId()){
////                            totalMasuk=totalMasuk+masukModel.getJmlMasuk();
////                        }
////
////                    }
//                }
//                holder.txtMasuk.setText(String.valueOf(totalMasuk));
//                MutasiModel muta = mutasiModels.get(position);
//                muta.setMasuk(totalMasuk);
//                getSisaStock(holder.txtSisa, position);
//                Log.d(TAG, "onDataChange: "+totalMasuk);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void getPengeluaranFaskes(TextView textView, int position, int faskesId) {
        Query query =  mDatabase.child("stockKeluar")
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
                    //if (keluarModel.getFaskesId() ==faskesId &&
//                            keluarModel.getObatId().equals(mutasiModels.get(position).getObatId())){
//                        if (sumberId==0){
//                            totalKeluar=totalKeluar+keluarModel.getJmlKeluar();
//                        }else if (sumberId == keluarModel.getSumberId()){
//                            totalKeluar=totalKeluar+keluarModel.getJmlKeluar();
//                        }

                 //   }
                }
                textView.setText(String.valueOf(totalKeluar));
                MutasiModel muta = mutasiModels.get(position);
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

                mutasiModels.set(position, muta);
                Log.d(TAG, "onDataChange: "+totalKeluar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public ArrayList<MutasiModel> getRealMutasi(){
        return mutasiModels;
    }

    @Override
    public int getItemCount() {
        return mutasiModels.size();
    }
    public class ViewHolderOne extends RecyclerView.ViewHolder {

        public TextView name;

        public ViewHolderOne(View itemView) {
            super(itemView);
        }
    }


    public class ViewHolderTwo extends RecyclerView.ViewHolder {

        private TextView txtName, txtPack,txtSisa,txtJumlah,txtLAIN,txtRSUD,
                txtKTK,txtGNG,txtKBS,txtIGD,txtMasuk,txtStockAwal;
        public ViewHolderTwo(View itemView) {
            super(itemView);

            txtMasuk = itemView.findViewById(R.id.txtMasuk);
            txtIGD = itemView.findViewById(R.id.txtIGD);
            txtKBS = itemView.findViewById(R.id.txtKBS);
            txtGNG = itemView.findViewById(R.id.txtGNG);
            txtKTK = itemView.findViewById(R.id.txtKTK);
            txtRSUD = itemView.findViewById(R.id.txtRSUD);
            txtLAIN = itemView.findViewById(R.id.txtLAIN);
            txtJumlah = itemView.findViewById(R.id.txtJumlah);
            txtSisa = itemView.findViewById(R.id.txtSisa);
            txtStockAwal = itemView.findViewById(R.id.txtStockAwal);
            txtPack = itemView.findViewById(R.id.txtPack);
            txtName = itemView.findViewById(R.id.txtName);
        }
    }

}
