package com.rentalapp.sisteminformasilogistikobat.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.rentalapp.sisteminformasilogistikobat.Activity.SupplierActivity;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;
import com.rentalapp.sisteminformasilogistikobat.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SupplierModel> supplierModels;
    private DatabaseReference mDatabase;

    public SupplierAdapter(Context context, ArrayList<SupplierModel> supplierModels) {
        this.context = context;
        this.supplierModels = supplierModels;
    }

    @NonNull
    @Override
    public SupplierAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_supplier, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierAdapter.ViewHolder holder, int position) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        holder.txtName.setText(supplierModels.get(position).getName());
        holder.txtPhone.setText("No Telpon : "+supplierModels.get(position).getPhone());
        holder.txtAddress.setText("Alamat : "+supplierModels.get(position).getAddress());
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
                                            mDatabase.child("supplier")
                                                    .child(supplierModels.get(position).getSupplierId())
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
        View view1 = LayoutInflater.from(context).inflate(R.layout.form_supplier,null  );
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Supplier");
        builder.setView(view1);
        TextInputEditText edtName = view1.findViewById(R.id.edtName);
        TextInputEditText edtPhone = view1.findViewById(R.id.edtPhone);
        TextInputEditText edtAddress = view1.findViewById(R.id.edtAddress);
        edtName.setText(supplierModels.get(position).getName());
        edtPhone.setText(supplierModels.get(position).getPhone());
        edtAddress.setText(supplierModels.get(position).getAddress());

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SupplierModel supplierModel = new SupplierModel();
                supplierModel.setName(edtName.getText().toString().trim());
                supplierModel.setAddress(edtAddress.getText().toString().trim());
                supplierModel.setPhone(edtPhone.getText().toString().trim());
                mDatabase.child("supplier").child(supplierModels.get(position).getSupplierId())
                        .setValue(supplierModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "sukses menyimpan", Toast.LENGTH_SHORT).show();

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Write failed
                                // ...
                            }
                        });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public int getItemCount() {
        return supplierModels.size();
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
