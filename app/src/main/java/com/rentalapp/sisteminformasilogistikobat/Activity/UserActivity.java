package com.rentalapp.sisteminformasilogistikobat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rentalapp.sisteminformasilogistikobat.Adapter.UserAdapter;
import com.rentalapp.sisteminformasilogistikobat.Model.UserModel;
import com.rentalapp.sisteminformasilogistikobat.R;
import com.rentalapp.sisteminformasilogistikobat.Util.Constant;

import java.io.File;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import jrizani.jrspinner.JRSpinner;

public class UserActivity extends AppCompatActivity implements UserAdapter.CallBack {
    private String TAG ="UserActivityTAG";
    private static final int PERMISSION_REQUEST_STORAGE = 2;
    private static final int PICK_IMAGE = 1;
    private DatabaseReference mDatabase;
    private  CircleImageView circleImg;
    private Uri photoProfil =null;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private ArrayList<UserModel> userModels;
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private Constant constant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        constant = new Constant(this);
        userModels = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userAdapter = new UserAdapter(this, userModels);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
        userAdapter.setCallBack(this);

        if (constant.getLevel(this)==2){
            findViewById(R.id.fabAdd).setVisibility(View.GONE);
        }
        getData();
    }

    @Override
    public void onClick(int position) {
        View view1 = LayoutInflater.from(this).inflate(R.layout.form_user,null  );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah User");
        builder.setView(view1);
        JRSpinner mySpinner = view1.findViewById(R.id.mySpinner);
        mySpinner.setItems(getResources().getStringArray(R.array.level));
        mySpinner.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //do what you want to the selected position
            }
        });
        mySpinner.setText(String.valueOf(userModels.get(position).getLevel()));
        circleImg= view1.findViewById(R.id.circleImg);
        CircleImageView imgBtn= view1.findViewById(R.id.imgBtn);
        TextInputEditText edtUsername = view1.findViewById(R.id.edtUsername);
        TextInputEditText edtPassword = view1.findViewById(R.id.edtPassword);
        TextInputEditText editname = view1.findViewById(R.id.editname);
        mySpinner.setEnabled(false);
        edtPassword.setFocusable(false);
        editname.setFocusable(false);
        ImageButton btnEdit = view1.findViewById(R.id.btnEdit);
        edtUsername.setText(userModels.get(position).getUsername());
        edtPassword.setText(userModels.get(position).getPassword());
        editname.setText(userModels.get(position).getName());
        Glide.with(this).load(userModels.get(position).getPhoto()).into(circleImg);
        edtUsername.setEnabled(false);
        btnEdit.setVisibility(View.VISIBLE);
        if (constant.getLevel(this)==2){
            btnEdit.setVisibility(View.GONE);
        }
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEdit.setVisibility(View.GONE);
                imgBtn.setVisibility(View.VISIBLE);
                mySpinner.setEnabled(true);
                edtPassword.setEnabled(true);
                editname.setEnabled(true);

                edtPassword.setFocusableInTouchMode(true);
                editname.setFocusableInTouchMode(true);
            }
        });
        builder.setPositiveButton("Ok", null);
        builder.setNegativeButton("Cancel", null);
        AlertDialog mAlertDialog = builder.create();
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtPassword.getText().length()==0){
                            edtPassword.setError("Tidak Boleh Kosong");
                            Toast.makeText(UserActivity.this, "Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!mySpinner.getText().toString().equals("1")&& !mySpinner.getText().toString().equals("2")) {
                            mySpinner.setError("Pilih Level");
                            Toast.makeText(UserActivity.this, "Pilih Level", Toast.LENGTH_SHORT).show();
                            return;

                        }

                        if (photoProfil==null){
                            UserModel userModel = new UserModel();
                            userModel.setLevel(Integer.valueOf(mySpinner.getText().toString().trim()));
                            userModel.setName(editname.getText().toString().trim());
                            userModel.setUsername(edtUsername.getText().toString().trim());
                            userModel.setPassword(edtPassword.getText().toString().trim());
                            userModel.setPhoto(userModels.get(position).getPhoto());
                            mDatabase.child("user")
                                    .child(userModels.get(position).getUserId())
                                    .setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mAlertDialog.dismiss();
                                    Toast.makeText(UserActivity.this, "berhasil menyimpan", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            save(userModels.get(position).getUserId(),mAlertDialog, edtUsername, edtPassword, editname, mySpinner.getText().toString().trim());
                        }

                    }
                });

                Button b2 = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

        });

        mAlertDialog.show();
    }

    private void getData() {
        mDatabase.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModels.clear();
                for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                    UserModel u = dataSnapshot.getValue(UserModel.class);
                    u.setUserId(dataSnapshot.getKey());
                    userModels.add(u);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addUser(View view) {
        View view1 = LayoutInflater.from(this).inflate(R.layout.form_user,null  );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah User");
        builder.setView(view1);
        JRSpinner mySpinner = view1.findViewById(R.id.mySpinner);
        mySpinner.setItems(getResources().getStringArray(R.array.level));
        mySpinner.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //do what you want to the selected position
            }
        });
        circleImg= view1.findViewById(R.id.circleImg);
        CircleImageView imgBtn= view1.findViewById(R.id.imgBtn);
        imgBtn.setVisibility(View.VISIBLE);
        TextInputEditText edtUsername = view1.findViewById(R.id.edtUsername);
        TextInputEditText edtPassword = view1.findViewById(R.id.edtPassword);
        TextInputEditText editname = view1.findViewById(R.id.editname);
        builder.setPositiveButton("Ok", null);
        builder.setNegativeButton("Cancel", null);
        AlertDialog mAlertDialog = builder.create();
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (photoProfil==null){
                            Toast.makeText(UserActivity.this, "Photo Masih Belum Ada", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (edtUsername.getText().length()==0){
                            edtUsername.setError("Tidak Boleh Kosong");
                            Toast.makeText(UserActivity.this, "Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (edtPassword.getText().length()==0){
                            edtPassword.setError("Tidak Boleh Kosong");
                            Toast.makeText(UserActivity.this, "Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!mySpinner.getText().toString().trim().equals("1")&& !mySpinner.getText().toString().trim().equals("2")) {
                            Log.d(TAG, "onClick: "+mySpinner.getText().toString());
                            mySpinner.setError("Pilih Level");
                            Toast.makeText(UserActivity.this, "Pilih Level", Toast.LENGTH_SHORT).show();
                            return;

                        }
                        mDatabase.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                                    UserModel u = dataSnapshot.getValue(UserModel.class);
                                    u.setUserId(dataSnapshot.getKey());
                                    if (u.getUsername().equals(edtUsername.getText().toString().trim())){
                                        Toast.makeText(UserActivity.this, "Username sudah ada yang pakai", Toast.LENGTH_SHORT).show();
                                        return;
                                    }else {

                                        save(null, mAlertDialog, edtUsername, edtPassword, editname,
                                                mySpinner.getText().toString().trim());
                                        break;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                Button b2 = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

        });

        mAlertDialog.show();

    }

    private void save(String userId, AlertDialog mAlertDialog, TextInputEditText edtUsername,
                      TextInputEditText edtPassword, TextInputEditText editname, String userLevel) {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        UserModel userModel = new UserModel();
        String id = null;
        if (userId==null){
            id  = mDatabase.child("user").push().getKey();
        }else {
            id =userId;
        }


        final StorageReference ref = storageRef.child("user/"+id);
        UploadTask uploadTask = ref.putFile(Uri.fromFile(new File(getRealPathFromURIPath(photoProfil))));

        String finalId = id;
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    userModel.setLevel(Integer.valueOf(userLevel));
                    userModel.setName(editname.getText().toString().trim());
                    userModel.setUsername(edtUsername.getText().toString().trim());
                    userModel.setPassword(edtPassword.getText().toString().trim());
                    userModel.setPhoto(String.valueOf(downloadUri));
                    mDatabase.child("user").child(finalId)
                            .setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            photoProfil=null;
                            mAlertDialog.dismiss();
                            pDialog.dismissWithAnimation();
                            Toast.makeText(UserActivity.this, "Berhasil Menyimpan", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d(TAG, "onComplete: "+task.getResult().toString());
                }
            }
        });


    }

    private void choosePhoto() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_STORAGE);

        }else{
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openGallery();
                }

                return;
            }
        }
    }

    @SuppressLint("IntentReset")
    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }


    private String getRealPathFromURIPath(Uri contentURI ) {
        Cursor cursor =  getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri returnUri = data.getData();
                Log.d("Filedadadada", returnUri.toString());
                Glide.with(this).load(returnUri).into(circleImg);
                photoProfil = returnUri;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}