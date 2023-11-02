package com.example.pushtest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.List;

public class ShowVideo extends AppCompatActivity {

    private ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    //static boolean calledAlready = false;

    private Uri videoUri;
    MediaController mediaController;

    FirebaseDatabase database;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    EditText editText;
    Member member;
    UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        editText = findViewById(R.id.editTextTextPersonName);

        database = FirebaseDatabase.getInstance();
        //storageReference = FirebaseStorage.getInstance().getReference("Image");
        databaseReference = FirebaseDatabase.getInstance().getReference("Image");

        listView = (ListView) findViewById(R.id.tv_listview);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_listitem, arrayList);
        listView.setAdapter(arrayAdapter);


        // Read from the database
        databaseReference.child("Video").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // 클래스 모델이 필요?
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    //하위키들의 value를 가져오기기
                   String str = fileSnapshot.getValue(String.class);
                    Log.i("TAG: value is ", str);
                    arrayAdapter.add(str);
                }
                arrayAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG: ", "Failed to read value", databaseError.toException());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int check_position = listView.getCheckedItemPosition();
                String  str = (String) parent.getAdapter().getItem(position);
                //String str = (String) listView.getItemAtPosition(0).toString();

                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("복사할 데이터", str);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ShowVideo.this, str, Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(ShowVideo.this, VideoPlay.class);

                intent.putExtra("videourl", str);

                //startActivity(intent);


            }
        });

    }


}