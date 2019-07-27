package com.shellyambar.ambar.wonderworld;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shellyambar.ambar.wonderworld.Adapters.AudioAdapter;
import com.shellyambar.ambar.wonderworld.Models.AudioModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AudioActivity extends AppCompatActivity {

    private RecyclerView Audio_recycler;
    private Button add_btn;
    private String Country;
    private String School;
    private String Class;
    private String Lecture;
    private String lectureKey;
    private Uri recordURI;
    private List<AudioModel> audioModelList;
    private AudioAdapter audioAdapter;
    private DatabaseReference databaseReference;
    private static final String RECORD_TAG="Record_log";
    private MediaRecorder recorder;
    private String fileName;
    private boolean recordButtonIndicator;
    private boolean playButtonIndicator;
    private MediaPlayer player;
    private String url;
    private Uri audioUri;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int SELECT_GALLERY_AUDIO=100;

    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;
    private StorageTask uploadTask;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        Audio_recycler=findViewById(R.id.Audio_recycler);
        add_btn=findViewById(R.id.add_btn);


        progressDialog=new ProgressDialog(this);
        Country=getIntent().getStringExtra("country");
        School=getIntent().getStringExtra("school");
        Class=getIntent().getStringExtra("class");
        Lecture=getIntent().getStringExtra("lecture");
        lectureKey=getIntent().getStringExtra("lectureKey");
        audioModelList=new ArrayList<>();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        audioAdapter=new AudioAdapter(AudioActivity.this,audioModelList );

        Audio_recycler.setAdapter(audioAdapter);
        Audio_recycler.setLayoutManager(linearLayoutManager);
        Audio_recycler.setHasFixedSize(true);
        fileName=Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName+="/recorded_audio.3gp";
        firebaseAuth=FirebaseAuth.getInstance();
        recorder=null;

        //recorder=null;
        url="";



        databaseReference=FirebaseDatabase.getInstance().getReference("Lectures")
                .child(Country).child(School).child(Class).child(lectureKey).child("records");

        mStorageRef=FirebaseStorage.getInstance()
                .getReference("Countries").child(Country).child(School)
                .child(Class).child(Lecture);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(ContextCompat.getDrawable(AudioActivity.this,R.drawable.ic_goback));
        getSupportActionBar().setTitle(Country +" "+ School +" "+ Class +" "+Lecture );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            startActivity(new Intent(this, EnterActivity.class));
            finish();
        }

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAudio();
            }
        });

        refreshData();

    }

    private void addAudio() {

        recordButtonIndicator=false;
        playButtonIndicator=false;

        AlertDialog.Builder builder=new AlertDialog.Builder(AudioActivity.this);
        CharSequence[] options=new CharSequence[]{
                "Upload audio file from gallery",
                "Record new Audio file"

        };
        builder.setTitle("Select option");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which==0){
                    //upload audio from gallery
                    addAudioAllertView();

                } else if (which == 1) {
                   //record new audio file


                    addAudioAllertViewForRecordLive();

                }

            }
        });

                builder.show();


    }

    private void addAudioAllertViewForRecordLive() {


        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        android.app.AlertDialog.Builder alertDialog= new android.app.AlertDialog.Builder(AudioActivity.this);
        alertDialog.setTitle("Add a new record");
        final EditText audioNameEditText=new EditText(AudioActivity.this);
        final Button recordBTN= new Button((AudioActivity.this));

        final Button playRecordBTN= new Button((AudioActivity.this));

        recordBTN.setBackgroundResource(R.drawable.button_background);
        recordBTN.setText("Record");
        recordBTN.setTextColor(Color.GREEN);
        playRecordBTN.setBackgroundResource(R.drawable.button_play);
        playRecordBTN.setEnabled(false);

        recordBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(recordButtonIndicator){
                    playRecordBTN.setEnabled(true);
                    recordButtonIndicator=false;
                    stopRecording();
                    recordBTN.setText("Record");
                    recordBTN.setTextColor(Color.GREEN);



                }else{

                    playRecordBTN.setEnabled(false);

                    startRecording();
                    recordButtonIndicator=true;
                    recordBTN.setText("Recording");
                    recordBTN.setTextColor(Color.RED);
                }
            }
        });

        playRecordBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(playButtonIndicator){

                    stopPlaying();
                    playButtonIndicator=false;
                    playRecordBTN.setBackgroundResource(R.drawable.button_play);

                }else{
                    startPlaying();
                    playButtonIndicator=true;
                    playRecordBTN.setBackgroundResource(R.drawable.stop_play_button);
                }
            }
        });



        LinearLayout linearLayout=new LinearLayout(this);


        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        linearLayout.addView(recordBTN, new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,0));
        linearLayout.addView(playRecordBTN, new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,0));

        linearLayout.addView(audioNameEditText, new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0));


       alertDialog.setView(linearLayout);




        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {

                progressDialog.setMessage("Uploading the audio file..");
                progressDialog.show();

                final String audioName=audioNameEditText.getText().toString().toLowerCase();
                if(audioName.contains("#") || audioName.contains(".")||
                        audioName.contains("]") || audioName.contains("[") || audioName.contains("$")){
                    Toast.makeText(AudioActivity.this, "Audio name must not contain any : '#', '.', '$',']','[' ", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }


                Calendar calForDate=Calendar.getInstance();
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                final String time=simpleDateFormat.format(calForDate.getTime());


                final String recordIdKey=  databaseReference.push().getKey();
                final StorageReference storageReference=mStorageRef.child(recordIdKey);

                uploadTask=storageReference.putFile(Uri.fromFile(new File(fileName)));

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful()) {
                            recordURI = task.getResult();

                            url=recordURI.toString();

                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("lectureName", Lecture);
                            hashMap.put("recordName",audioName);
                            hashMap.put("recordURI",url);
                            hashMap.put("recordId",recordIdKey );
                            hashMap.put("dateAudioUploaded",time);
                            hashMap.put("publisherName",FirebaseAuth.getInstance().getCurrentUser().getUid() );
                            hashMap.put("countryName",Country );
                            hashMap.put("schoolName",School );
                            hashMap.put("className",Class );
                            hashMap.put("lectureId",lectureKey);
                            hashMap.put("is_playing","false");



                            databaseReference.child(recordIdKey).setValue(hashMap);

                            progressDialog.dismiss();
                        }

                    }
                });


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


    private void addAudioAllertView(){
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        android.app.AlertDialog.Builder alertDialog= new android.app.AlertDialog.Builder(AudioActivity.this);
        alertDialog.setTitle("Choose record from gallery");
        final EditText audioNameEditText=new EditText(AudioActivity.this);
        final Button recordBTN= new Button((AudioActivity.this));

        recordBTN.setBackgroundResource(R.drawable.button_background);
        recordBTN.setText("Upload Audio File");
        recordBTN.setTextColor(Color.GREEN);

        LinearLayout linearLayout=new LinearLayout(this);

        linearLayout.setOrientation(LinearLayout.HORIZONTAL);



        linearLayout.addView(recordBTN, new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,0));

        linearLayout.addView(audioNameEditText, new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0));


        recordBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Audio "), SELECT_GALLERY_AUDIO);

            }
        });



        alertDialog.setView(linearLayout);




        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {

                final String audioName=audioNameEditText.getText().toString().toLowerCase();
                if(audioName.contains("#") || audioName.contains(".")||
                        audioName.contains("]") || audioName.contains("[") || audioName.contains("$")){
                    Toast.makeText(AudioActivity.this, "Audio name must not contain any : '#', '.', '$',']','[' ", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else if(audioUri == null){
                    dialog.dismiss();
                }else{


                    Calendar calForDate=Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                    final String time=simpleDateFormat.format(calForDate.getTime());


                    final String recordIdKey=  databaseReference.push().getKey();

                    final StorageReference storageReference=mStorageRef.child(recordIdKey);



                    uploadTask=storageReference.putFile(audioUri);

                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if(task.isSuccessful()) {
                                recordURI = task.getResult();

                                url=recordURI.toString();

                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("lectureName", Lecture);
                                hashMap.put("recordName",audioName);
                                hashMap.put("recordURI",url);
                                hashMap.put("recordId",recordIdKey );
                                hashMap.put("dateAudioUploaded",time);
                                hashMap.put("publisherName"," " );
                                hashMap.put("countryName",Country );
                                hashMap.put("schoolName",School );
                                hashMap.put("className",Class );
                                hashMap.put("lectureId",lectureKey);
                                hashMap.put("is_playing","false");



                                databaseReference.child(recordIdKey).setValue(hashMap);

                                progressDialog.dismiss();
                            }

                        }
                    });
                    dialog.dismiss();

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

    private void refreshData(){

        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Lectures").child(Country).child(School).child(Class).child(lectureKey).child("records");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot!=null){

                    audioModelList.clear();

                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        AudioModel audioModel=snapshot.getValue(AudioModel.class);

                        audioModelList.add(audioModel);

                    }
                    audioAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void startRecording() {

        if(recorder==null){ recorder=new MediaRecorder(); }


            recorder.reset();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                recorder.prepare();
            } catch (IOException e) {

            }

            recorder.start();



    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder=null;

    }



    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {

        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_GALLERY_AUDIO){

            if(resultCode == RESULT_OK){

                //the selected audio.
                audioUri = data.getData();
            }
        }

    }
}


