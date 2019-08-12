package com.example.teststorage;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class KegiatanFragment extends Fragment {

    View view;
    EditText judul,isi;
    CardView btn_simpan;

    public KegiatanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_kegiatan, container, false);

        judul = view.findViewById(R.id.txt_judul);
        isi = view.findViewById(R.id.txt_isi);
        btn_simpan = view.findViewById(R.id.btn_simpan);

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("notif");
                Map mParent = new HashMap();
//            mParent.put("url", "https://firebasestorage.googleapis.com/v0/b/desa-d3f44.appspot.com/o/gambar%2FIMG_4394.JPG?alt=media&token=65468fc5-fa16-4cbd-9c03-56130a56a6e1");
                mParent.put("title", ""+judul.getText());
                mParent.put("isi", ""+isi.getText());
                String kunci = myRef.push().getKey();
                //Toast.makeText(TambahProdukActivity.this, "key :"+kunci, Toast.LENGTH_SHORT).show();

                myRef.child(kunci).setValue(mParent).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(view.getContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });

        return view;
    }

}
