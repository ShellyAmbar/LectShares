package com.shellyambar.ambar.wonderworld;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.shellyambar.ambar.wonderworld.Adapters.HeadersAdapter;
import com.shellyambar.ambar.wonderworld.Models.HeaderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView lactur_recycler;
    private Button add_btn;
    private HeadersAdapter headersAdapter;
    private List<HeaderModel> headerModelList;
    private String Country;
    private String School;
    private String Class;

    private String photoUrl;
    private final static int SELECT_GALLERY_IMAGE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lactur_recycler=findViewById(R.id.lactur_recycler);
        add_btn=findViewById(R.id.add_btn);
        headerModelList=new ArrayList<>();
        Country=getIntent().getStringExtra("country");
        School=getIntent().getStringExtra("school");
        Class=getIntent().getStringExtra("class");


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Country +" "+ School +" "+ Class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_goback));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        headersAdapter=new HeadersAdapter(MainActivity.this,headerModelList);

        lactur_recycler.setHasFixedSize(true);
        lactur_recycler.setAdapter(headersAdapter);
        lactur_recycler.setLayoutManager(new GridLayoutManager(this,3));


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addheaderItem();
            }
        });

        refreshData();

    }


    private void refreshData(){

        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Lectures").child(Country).child(School).child(Class);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot!=null){

                    headerModelList.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        HeaderModel headerModel=snapshot.getValue(HeaderModel.class);


                        headerModelList.add(headerModel);
                    }
                    headersAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void addheaderItem() {

        final DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Lectures").child(Country).child(School).child(Class);


        android.app.AlertDialog.Builder alertDialog= new android.app.AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Add new category");
        final EditText editText=new EditText(MainActivity.this);

        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT

        );


        editText.setLayoutParams(layoutParams);

        alertDialog.setView(editText);




        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {


              final String lectureName= editText.getText().toString().toLowerCase();
              if(lectureName.contains("#") || lectureName.contains(".")||
                      lectureName.contains("]") || lectureName.contains("[") || lectureName.contains("$")){
                  Toast.makeText(MainActivity.this, "lecture name must not contain any : '#', '.', '$',']','[' ", Toast.LENGTH_SHORT).show();
                  dialog.dismiss();

              }else{

                  String lectureIdKey=  FirebaseDatabase.getInstance().getReference("Lectures")
                          .child(Country).child(School).child(Class).child(lectureName).push().getKey();
                  HashMap<String,Object> hashMap=new HashMap<>();
                  hashMap.put("lectureName", lectureName);
                  hashMap.put("countryName",Country );
                  hashMap.put("schoolName",School );
                  hashMap.put("className",Class );
                  hashMap.put("lectureId",lectureIdKey );
                  hashMap.put("publisherName", FirebaseAuth.getInstance().getCurrentUser().getUid());


                  reference.child(lectureIdKey).setValue(hashMap)
                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  Toast.makeText(MainActivity.this, lectureName + " added successfully", Toast.LENGTH_SHORT).show();
                              }
                          }).addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          finish();
                      }
                  });
              }

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }




}
