package com.rentalapp.sisteminformasilogistikobat.Adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Activity.InOutActivity;
import com.rentalapp.sisteminformasilogistikobat.Activity.LitsAddOutActivity;
import com.rentalapp.sisteminformasilogistikobat.Model.KeluarModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ListModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MasukModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jrizani.jrspinner.JRSpinner;

public class KeluarAdapter extends RecyclerView.Adapter<KeluarAdapter.ViewHolder>implements Filterable {
    private String TAG ="TAGKeluarAdapter";
    private Context context;
    private List<KeluarModel> keluarModels;
    private List<KeluarModel> dataListfull = new ArrayList<>();
    private ArrayList<ObatModel> obatModels;
    private ArrayList<SupplierModel> supplierModels;
    private DatabaseReference mDatabase;
    private Constant constant;

    public KeluarAdapter(Context context, ArrayList<KeluarModel> keluarModels,
                         ArrayList<ObatModel> obatModels ) {
        this.context = context;
        this.keluarModels = keluarModels;
        this.dataListfull = keluarModels;
        this.obatModels =obatModels;
    }

    @NonNull
    @Override
    public KeluarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_in_out, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeluarAdapter.ViewHolder holder, int position) {
        constant = new Constant(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        holder.txtSumber.setVisibility(View.GONE);
        holder.txtSumber1.setVisibility(View.VISIBLE);
       // holder.txtSumber.setText("Sumber Dana : "+constant.getSumberNameById(keluarModels.get(position).getSumberId()));
       // holder.txtJmlMasuk.setText("Jumlah Keluar : "+keluarModels.get(position).getJmlKeluar());
        holder.txtTglMasuk.setText("Tanggal :  "+constant.changeFromLong(keluarModels.get(position).getTglKeluar()));
       // holder.txtTglExp.setText("Tanggal Expired :  "+constant.changeFromLong(keluarModels.get(position).getTglExp()));
        holder.txtSupplier.setText("Faskes : "+getFaskesName(keluarModels.get(position).getFaskesId()));
        setNamePack(holder, position);
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
                            editKeluar(position);
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
                                            mDatabase.child("listKeluar")
                                                    .child(keluarModels.get(position).getKeluarId())
                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mDatabase.child("listDataKeluar").child(keluarModels.get(position).getKeluarId())
                                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(context, "berhasil menghapus", Toast.LENGTH_SHORT).show();
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
                            break;
                    }
                    return true;
                });
                pop.show();
            }
        });
    }

    private String getFaskesName(int id){
        for (int i =0; i<constant.faskesModels.size(); i++){
            if (constant.faskesModels.get(i).getFaskesId()==id){
                return constant.faskesModels.get(i).getFaskesName();
            }
        }
        return null;
    }

    private void setNamePack(ViewHolder holder, int position) {
        mDatabase.child("listDataKeluar").child(keluarModels.get(position).getKeluarId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int no =1;

                        if (holder.tableLayout.getChildCount()>1){
                            return;
                        }

                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            ListModel l = snapshot1.getValue(ListModel.class);
                            View view = LayoutInflater.from(context).inflate(R.layout.list_data_tb,null, false);

                            TextView txtSumber = view.findViewById(R.id.txtSumber);
                            TextView txtName = view.findViewById(R.id.txtName);
                            TextView txtNo = view.findViewById(R.id.txtNo);
                            TextView txtJmlMasuk = view.findViewById(R.id.txtJmlMasuk);
                            TextView txtTglExp = view.findViewById(R.id.txtTglExp);
                            txtNo.setText(String.valueOf(no++));
                            txtTglExp.setText(constant.changeFromLong(l.getTglExp()));
                            txtJmlMasuk.setText(String.valueOf(l.getJumlah()));
                            txtName.setText(getNameObat(l.getObatId()));
                            txtSumber.setVisibility(View.VISIBLE);
                            txtSumber.setText(String.valueOf(constant.getSumberNameById(l.getSumberId())));
                            holder.tableLayout.addView(view);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void editKeluar(int position) {
        Intent intent = new Intent(context, LitsAddOutActivity.class);
        intent.putExtra("supplierModels",supplierModels);
        intent.putExtra("obatModels",obatModels);
        intent.putExtra("keluarId", keluarModels.get(position).getKeluarId());
      //  intent.putExtra("sumberId", keluarModels.get(position).getSumberId());
        intent.putExtra("faskesId", keluarModels.get(position).getFaskesId());
        intent.putExtra("tanggal", keluarModels.get(position).getTglKeluar());
        intent.putExtra("isIn", false);
        intent.putExtra("editOut", true);
        context.startActivity(intent);
    }



    private String getNameObat(String obatId) {
        for (int i=0; i<obatModels.size();i++){
            if (obatModels.get(i).getObatId().equals(obatId)){
                return obatModels.get(i).getName();
            }
        }
        return null;
    }


    @Override
    public int getItemCount() {
        return keluarModels.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    keluarModels = dataListfull;
                } else {
                    List<KeluarModel> filteredList = new ArrayList<>();
                    for (KeluarModel row : dataListfull) {
                        if (constant.changeFromLong(row.getTglKeluar()).startsWith(charString)) {
                            filteredList.add(row);
                        }
                    }

                    keluarModels = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = keluarModels;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                keluarModels = (ArrayList<KeluarModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView  txtSumber,txtTglMasuk,
                txtSupplier,txtSumber1;
        private ExpandableLayout expandable_layout;
        private TableLayout tableLayout;
        private ImageButton imgBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSumber1 = itemView.findViewById(R.id.txtSumber1);
            tableLayout = itemView.findViewById(R.id.tableLayout);
            txtSupplier = itemView.findViewById(R.id.txtSupplier);
            txtSumber = itemView.findViewById(R.id.txtSumber);
            txtTglMasuk = itemView.findViewById(R.id.txtTglMasuk);
            expandable_layout = itemView.findViewById(R.id.expandable_layout);
            imgBtn = itemView.findViewById(R.id.imgBtn);

        }
    }
}
