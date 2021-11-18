package com.rentalapp.sisteminformasilogistikobat.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rentalapp.sisteminformasilogistikobat.Activity.KaryawanActivity;
import com.rentalapp.sisteminformasilogistikobat.Model.KaryawanModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import jrizani.jrspinner.JRSpinner;

public class KaryawanAdapter extends RecyclerView.Adapter<KaryawanAdapter.ViewHolder> {
    private String TAG ="KaryawanAdapterTAG";
    private Context context;
    private ArrayList<KaryawanModel> karyawanModels;
    private DatabaseReference mDatabase;
    private Constant constant;

    public KaryawanAdapter(Context context, ArrayList<KaryawanModel> karyawanModels) {
        this.context = context;
        this.karyawanModels = karyawanModels;
    }

    @NonNull
    @Override
    public KaryawanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_supplier, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KaryawanAdapter.ViewHolder holder, int position) {
        constant= new Constant(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        holder.txtName.setText(karyawanModels.get(position).getNama());
        holder.txtPhone.setText("NIP : "+karyawanModels.get(position).getNip());
        holder.txtAddress.setText("Jabatan : "+constant.getJabatanNameById( karyawanModels.get(position).getJabatan()));
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
                             editSupplier(position);
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
                                            mDatabase.child("karyawan")
                                                    .child(karyawanModels.get(position).getKaryawanId())
                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context, "berhasil menghapus", Toast.LENGTH_SHORT).show();
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

    private void editSupplier(int position) {
        View view1 = LayoutInflater.from(context).inflate(R.layout.form_karyawan,null  );
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Karyawan");
        builder.setView(view1);
        KaryawanModel karyawanModel = karyawanModels.get(position);
        TextInputEditText edtNama = view1.findViewById(R.id.edtNama);
        edtNama.setText(karyawanModel.getNama());
        TextInputEditText editNip = view1.findViewById(R.id.editNip);
        editNip.setText(String.valueOf(karyawanModel.getNip()));
        JRSpinner mySpinner = view1.findViewById(R.id.mySpinner);
        mySpinner.setText(constant.getJabatanNameById(karyawanModel.getJabatan()));
        mySpinner.setEnabled(false);
        mySpinner.setItems(constant.getJabatanName());
        mySpinner.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                karyawanModel.setJabatan(constant.getJabatanId(position));
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

                        if (edtNama.getText().length()==0){
                            edtNama.setError("Masih Kosong");
                            return;
                        }
                        if (editNip.getText().length()==0){
                            editNip.setError("Masih Kosong");
                            return;
                        }

                        if (karyawanModel.getJabatan()==0){
                            mySpinner.setError("Pilih Jabatan");
                            return;
                        }

                        karyawanModel.setNama(edtNama.getText().toString().trim());
                        karyawanModel.setNip(Integer.valueOf(editNip.getText().toString().trim()));

                        mDatabase.child("karyawan").child(karyawanModels.get(position).getKaryawanId())
                                .setValue(karyawanModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mAlertDialog.dismiss();
                                Toast.makeText(context, "sukses menyimpan", Toast.LENGTH_SHORT).show();

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: "+e.getMessage());
                                        // Write failed
                                        // ...
                                    }
                                });

                    }
                });
            }
        });
        mAlertDialog.show();
    }

    @Override
    public int getItemCount() {
        return karyawanModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName,txtAddress,txtPhone;
        private ExpandableLayout expandable_layout;
        private ImageButton imgBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtName = itemView.findViewById(R.id.txtName);
            expandable_layout = itemView.findViewById(R.id.expandable_layout);
            imgBtn = itemView.findViewById(R.id.imgBtn);

        }
    }
}
