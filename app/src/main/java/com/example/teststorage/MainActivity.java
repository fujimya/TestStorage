package com.example.teststorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    ArrayList<Uri> listUrl = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    public void ambil(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    public void upload(View view) {
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
                                //if the upload is successfull
                                //hiding the progress dialog
//                                progressDialog.dismiss();
                                //final String downloadUrl ;
//                                        riversRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Uri> task) {
//                                                Map mParentG = new HashMap();
//                                                mParentG.put("url", ""+task.toString());
//                                                mParentG.put("title", ""+judul.getText());
//                                                mParentG.put("isi", ""+isi.getText());
//                                                refGambar.push().setValue(mParentG).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        statusupload = true;
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        statusupload = false;
//                                                    }
//                                                });
//                                            }
//                                        });
                                //and displaying a success toast
                                //Toast.makeText(getApplicationContext(), "File Uploaded, URL :"+downloadUrl, Toast.LENGTH_LONG).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
//                                progressDialog.dismiss();

                                //and displaying error message
                                Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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
        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if(data.getClipData() != null){
                int totalItem = data.getClipData().getItemCount();
                for(int i=0; i < totalItem; i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    listUrl.add(uri);
                    Toast.makeText(MainActivity.this,"File ke : "+i,Toast.LENGTH_LONG).show();
                }
                Toast.makeText(MainActivity.this,"Multipel Upload",Toast.LENGTH_LONG).show();
            }
            if(data.getData() != null){
                Uri uri = data.getData();
                listUrl.add(uri);
                Toast.makeText(MainActivity.this,"Singgel Upload",Toast.LENGTH_LONG).show();
            }
        }
    }
}
