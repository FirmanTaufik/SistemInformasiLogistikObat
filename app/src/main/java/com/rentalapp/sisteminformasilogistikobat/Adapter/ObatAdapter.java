package com.rentalapp.sisteminformasilogistikobat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rentalapp.sisteminformasilogistikobat.Activity.ObatDetailActivity;
import com.rentalapp.sisteminformasilogistikobat.Model.KeluarModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ListModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MasukModel;
import com.rentalapp.sisteminformasilogistikobat.Model.MutasiModel;
import com.rentalapp.sisteminformasilogistikobat.Model.ObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SisaStockModel;
import com.rentalapp.sisteminformasilogistikobat.Model.StockObatModel;
import com.rentalapp.sisteminformasilogistikobat.Model.SupplierModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import static com.rentalapp.sisteminformasilogistikobat.Util.Constant.maxStockSisa;

public class ObatAdapter extends RecyclerView.Adapter<ObatAdapter.ViewHolder>implements Filterable {
    private String TAG ="ObatAdapterTAG";
    private Context context;
    private List<StockObatModel> obatModels;
    private List<StockObatModel> dataListfull;
    private DatabaseReference mDatabase;
    private Constant constant;
    public ObatAdapter(Context context, ArrayList<StockObatModel> obatModels) {
        this.context = context;
        this.obatModels = obatModels;
        this.dataListfull = obatModels;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    obatModels = dataListfull;
                } else {
                    List<StockObatModel> filteredList = new ArrayList<>();
                    for (StockObatModel row : dataListfull) {
                        if ( row.getName().startsWith(charString)) {
                            filteredList.add(row);
                        }
                    }

                    obatModels = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = obatModels;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                obatModels = (ArrayList<StockObatModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public ObatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_obat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObatAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        constant = new Constant(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        holder.txtName.setText(obatModels.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            boolean isExpand= false;
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ObatDetailActivity.class);
                intent.putExtra("obatId",obatModels.get(position).getObatId() );
                intent.putExtra("kemasan","Kemasan : "+obatModels.get(position).getPack() );
                intent.putExtra("name", obatModels.get(position).getName() );
                intent.putExtra("batch", obatModels.get(position).getNoBatch());
                context.startActivity(intent);
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
                                            mDatabase.child("obatalkes")
                                                    .child(obatModels.get(position).getObatId())
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

        //GET EXPIRED
        if (obatModels.get(position).getStockExp()>0){
            holder.txtWarnExp.setVisibility(View.VISIBLE);
            holder.txtWarnExp.setText(String.valueOf(obatModels.get(position).getStockExp()));
        }

        //GET SISA
        if (obatModels.get(position).getSisaStock()<=constant.maxStockSisa){
            holder.txtWarnSisa.setVisibility(View.VISIBLE);
            holder.txtWarnSisa.setText(String.valueOf(obatModels.get(position).getSisaStock()));
        }

      //  getExp(holder, position);
     //   getSisaStock(holder,position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    private void editSupplier(int position) {
        View view1 = LayoutInflater.from(context).inflate(R.layout.form_obat,null  );
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Obat");
        builder.setView(view1);
        TextInputEditText edtName = view1.findViewById(R.id.edtName);
        TextInputEditText edtPack = view1.findViewById(R.id.edtPack);
        TextInputEditText edtNoBatch = view1.findViewById(R.id.edtNoBatch);
        edtName.setText(obatModels.get(position).getName());
        edtPack.setText(obatModels.get(position).getPack());
        edtNoBatch.setText(obatModels.get(position).getNoBatch());

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ObatModel obatModel = new ObatModel();
                obatModel.setName(edtName.getText().toString().trim());
                obatModel.setPack(edtPack.getText().toString().trim());
                obatModel.setNoBatch(edtNoBatch.getText().toString().trim());

                mDatabase.child("obatalkes").child(obatModels.get(position).getObatId())
                        .setValue(obatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        return obatModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName,
                txtWarnSisa,txtWarnExp;
        private ImageButton imgBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWarnExp = itemView.findViewById(R.id.txtWarnExp);
            txtWarnSisa = itemView.findViewById(R.id.txtWarnSisa);
            txtName = itemView.findViewById(R.id.txtName);
            imgBtn = itemView.findViewById(R.id.imgBtn);

        }
    }
}
