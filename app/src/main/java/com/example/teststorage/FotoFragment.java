package com.example.teststorage;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class FotoFragment extends Fragment {
    View view;
    EditText judul,isi;
    CardView simpan;
    Button upload;
    ArrayList<Uri> listUrl = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST = 234;
    private StorageReference mStorageRef;

    boolean statusupload;

    public FotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_foto, container, false);


        simpan = view.findViewById(R.id.simpan);
        upload = view.findViewById(R.id.ambil);
        judul = view.findViewById(R.id.judul);
        isi = view.findViewById(R.id.isi);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ProgressDialog progressDialog = new ProgressDialog(view.getContext());
                progressDialog.setTitle("Uploading");
                progressDialog.show();

                for(int d=0; d < listUrl.size(); d++){
                    if (listUrl.get(d) != null) {
                        //Toast.makeText(TambahProdukActivity.this,"Nama File : "+listUrl.get(d),Toast.LENGTH_LONG).show();
                        //displaying a progress dialog while upload is going on

                        Random rand = new Random();
                        int n = rand.nextInt(20);
                        final StorageReference riversRef = mStorageRef.child("images/"+n+".jpg");
                        Uri url = listUrl.get(d);
                        riversRef.putFile(url)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(final Uri uri) {
                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                DatabaseReference myRef = database.getReference("kegiatan");
                                                Map mParentG = new HashMap();
                                                mParentG.put("url", ""+uri.toString());
                                                mParentG.put("title", ""+judul.getText());
                                                mParentG.put("isi", ""+isi.getText());
                                                myRef.push().setValue(mParentG).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        statusupload = true;
//                                                        Toast.makeText(view.getContext(), "File Uploaded, URL :"+uri.toString(), Toast.LENGTH_LONG).show();
                                                        Toast.makeText(view.getContext(), "Data berhasil di upload", Toast.LENGTH_LONG).show();
                                                        judul.setText("");
                                                        isi.setText("");
                                                        listUrl.clear();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        statusupload = false;
                                                    }
                                                });
                                            }
                                        });
                                        progressDialog.dismiss();


                                          }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        //if the upload is not successfull
                                        //hiding the progress dialog
//                                progressDialog.dismiss();

                                        //and displaying error message
                                        Toast.makeText(view.getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        //calculating progress percentage
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                        //displaying percentage in progress dialog
                                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                                    }
                                });
                    }

                }


            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            if(data.getClipData() != null){
                int totalItem = data.getClipData().getItemCount();
                for(int i=0; i < totalItem; i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    listUrl.add(uri);
                    Toast.makeText(view.getContext(),"File ke : "+i,Toast.LENGTH_LONG).show();
                }
                Toast.makeText(view.getContext(),"Multipel Upload",Toast.LENGTH_LONG).show();
            }
            if(data.getData() != null){
                Uri uri = data.getData();
                listUrl.add(uri);
                Toast.makeText(view.getContext(),"Singgel Upload",Toast.LENGTH_LONG).show();
            }
        }
    }
}
