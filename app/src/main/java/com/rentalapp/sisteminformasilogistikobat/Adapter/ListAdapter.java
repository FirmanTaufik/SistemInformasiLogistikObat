package com.rentalapp.sisteminformasilogistikobat.Adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rentalapp.sisteminformasilogistikobat.Activity.LitsAddOutActivity;
import com.rentalapp.sisteminformasilogistikobat.Model.KeluarModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ListModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MasukModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.UserModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import jrizani.jrspinner.JRSpinner;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private String TAG="ListAdapterTAG";
    private Context context;
    private ArrayList<ListModel> listModels;
    private ArrayList<ObatModel> obatModels;
    private DatabaseReference mDatabase;
    private CallBack mCallBack;
    private Constant constant;
    private boolean isIn;
    public interface CallBack {
        void onClick(int position);
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }
    public ListAdapter(Context context, ArrayList<ListModel> listModels,
                       ArrayList<ObatModel> obatModels, boolean isIn) {
        this.context = context;
        this.listModels = listModels;
        this.obatModels = obatModels;
        this.isIn = isIn;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        constant = new Constant(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        if (!isIn){
            holder.txtPack.setText("Sumber : "+constant.getSumberNameById(listModels.get(position).getSumberId()) );
        }else {

            holder.txtPack.setVisibility(View.GONE);
        }

        holder.txtKeluar.setVisibility(View.GONE);
        holder.txtName.setText( getObatName(listModels.get(position).getObatId()));
        if (listModels.get(position).getNoBatch()!=null){
            holder.txtMasuk.setText("No Batch :  "+listModels.get(position).getNoBatch() +"\n \n");
        }
        holder.txtMasuk.append("Jumlah : "+listModels.get(position).getJumlah());
        holder.txtSisa.setText("Tanggal Expired : "+constant.changeFromLong(listModels.get(position).getTglExp()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            boolean isExpand= false;
            @Override
            public void onClick(View v) {
                // holder.expandable_layout.setExpanded(true, false);
                if (isExpand) {
                    holder.expandable_layout.collapse(true);
                    isExpand=false;
                }else {
                    holder.expandable_layout.expand(true);
                    isExpand=true;
                }
            }
        });
        holder.imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop = new PopupMenu(context, holder.imgBtn);
                pop.inflate(R.menu.menu_supplier);
                pop.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.edit:
                             editList(position);
                            break;

                        case R.id.delete:
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(true);
                            builder.setTitle("Konfirmasi");
                            builder.setMessage("Apakah Kamu Yakin Akan Menghapus");
                            builder.setPositiveButton("Iya",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                           listModels.remove(position);
                                           notifyDataSetChanged();
                                            Toast.makeText(context, "Berhasil Menghapus", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                            break;
                    }
                    return true;
                });
                pop.show();
            }
        });
    }

    private void editList(int position) {
        ListModel listModel = listModels.get(position);
        View view1 = LayoutInflater.from(context).inflate(R.layout.form_masuk,null  );
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit");
        builder.setView(view1);
        TextInputEditText edtNoBatch = view1.findViewById(R.id.edtNoBatch);
        TextInputLayout txtInputJml = view1.findViewById(R.id.txtInputJml);
        TextInputLayout txtInputSisa = view1.findViewById(R.id.txtInputSisa);
        TextInputLayout textInputLayout = view1.findViewById(R.id.textInputLayout);
        TextInputLayout txtInputLayoutBatch = view1.findViewById(R.id.txtInputLayoutBatch);
        JRSpinner spinner = view1.findViewById(R.id.spinner);
        TextInputEditText edtSisa =  view1.findViewById(R.id.edtSisa);
        TextInputEditText edtTglExp =  view1.findViewById(R.id.edtTglExp);
        TextInputEditText edtJmlMasuk = view1.findViewById(R.id.edtJmlMasuk);
        JRSpinner spinnerObat = view1.findViewById(R.id.spinnerObat);
        edtTglExp.setText(constant.changeFromLong(listModel.getTglExp()));
        edtJmlMasuk.setText(String.valueOf(listModel.getJumlah()));
        edtNoBatch.setText(listModel.getNoBatch());
        if (!isIn){
            getSisaStock(listModel.getObatId(), edtSisa);
            txtInputJml.setHint("Jumlah Keluar");
            txtInputSisa.setVisibility(View.VISIBLE);
            textInputLayout.setVisibility(View.VISIBLE);
            txtInputLayoutBatch.setVisibility(View.GONE);
            edtTglExp.setEnabled(false);
            spinner.setItems(constant.getSumberDanaNama(false));
            spinner.setText(constant.getSumberNameById(listModel.getSumberId()));
            spinner.setOnItemClickListener(new JRSpinner.OnItemClickListener() { //set it if you want the callback
                @Override
                public void onItemClick(int position) {
                    listModel.setSumberId(constant.getSumberId(position,false));
                 //   sumberId = constant.getSumberId(position,false);
                }
            });
        }

        edtTglExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
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


        constant.setListObatAlkes(obatModels);
        spinnerObat.setItems(constant.getObatNama());
        spinnerObat.setText(constant.getObatNameById(listModel.getObatId()));
        spinnerObat.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                listModel.setObatId(constant.getObatId(position));
            }
        });

        builder.setPositiveButton("ok", null);
        builder.setNegativeButton("cancel", null);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isIn){
                            int sisa = Integer.valueOf(edtSisa.getText().toString());
                            int jml = Integer.valueOf(edtJmlMasuk.getText().toString());

                            if (jml>sisa){
                                Toast.makeText(context,"Jumlah Keluar Terlalu Banyak", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        listModel.setNoBatch(edtNoBatch.getText().toString().trim());
                        listModel.setJumlah(Integer.valueOf(edtJmlMasuk.getText().toString().trim()));
                        listModels.set(position,listModel);
                        notifyDataSetChanged();
                        mAlertDialog.dismiss();
                    }
                });
            }
        });
        mAlertDialog.show();

//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                int sisa = Integer.valueOf(edtSisa.getText().toString());
//                int jml = Integer.valueOf(edtJmlMasuk.getText().toString());
//
//                if (jml>sisa){
//                    Toast.makeText(context,"Jumlah Keluar Terlalu Banyak", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                listModel.setJumlah(Integer.valueOf(edtJmlMasuk.getText().toString().trim()));
//                listModels.set(position,listModel);
//                notifyDataSetChanged();
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();

    }

    private void getSisaStock(String obatId, TextInputEditText edtSisa) {
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
                                                                    ListModel listModel = d.getValue(ListModel.class);
                                                                    listModel.setListId(d.getKey());

                                                                    if (obatId.equals(  listModel.getObatId())){
                                                                        totalKeluar = totalKeluar+listModel.getJumlah();
                                                                    }

                                                                }
                                                                int m = totalMasuk - totalKeluar;
                                                                edtSisa.setEnabled(false);
                                                                edtSisa.setText(String.valueOf(m));
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

    private String getObatName(String obatId) {
        for (int i =0; i<obatModels.size(); i++){
            if (obatModels.get(i).getObatId().equals(obatId)){
                return obatModels.get(i).getName();
            }
        }
        return null;
    }

    public ArrayList<ListModel> getListModels(){
        return listModels;
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName,txtMasuk,txtSisa,txtPack,txtKeluar ;
        private ImageButton imgBtn;
        private ExpandableLayout expandable_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtKeluar = itemView.findViewById(R.id.txtKeluar);
            txtPack = itemView.findViewById(R.id.txtPack);
            expandable_layout = itemView.findViewById(R.id.expandable_layout);
            txtSisa = itemView.findViewById(R.id.txtSisa);
            txtMasuk = itemView.findViewById(R.id.txtMasuk);
            txtName = itemView.findViewById(R.id.txtName);
            imgBtn = itemView.findViewById(R.id.imgBtn);

        }
    }
}
