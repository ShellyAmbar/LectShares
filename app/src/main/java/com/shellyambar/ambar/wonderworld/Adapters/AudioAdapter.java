package com.shellyambar.ambar.wonderworld.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shellyambar.ambar.wonderworld.Models.AudioModel;
import com.shellyambar.ambar.wonderworld.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.myViewHolder> {

    private  Context context;
    private List<AudioModel> audioModelList;
    private MediaPlayer CurrentPlayer;


    private DatabaseReference databaseReferenceOfRecord;
    private int indexOfModel;




    public AudioAdapter(Context activityContext, List<AudioModel> audioModelList) {
        context = activityContext;
        this.audioModelList = audioModelList;

        CurrentPlayer=null;




    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.audio_item,viewGroup,false);
        return new AudioAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder myViewHolder, final int i) {



        final AudioModel audioModel=audioModelList.get(i);









        databaseReferenceOfRecord=FirebaseDatabase.getInstance().getReference("Countries")
                .child(audioModel.getCountryName())
                .child(audioModel.getSchoolName())
                .child(audioModel.getClassName())
                .child(audioModel.getLectureId())
                .child("records")
                .child(audioModel.getRecordId());








        myViewHolder.player_description.setText(audioModel.getRecordName());


        myViewHolder.pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(CurrentPlayer!=null){
                  if(CurrentPlayer.isPlaying()){
                      startPause();
                  }else{
                      stopPause();
                  }
              }
            }
        });


        myViewHolder.player_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(audioModel.getIs_playing().equals("false")){

                    audioModel.setIs_playing("true");

                    startPlaying(audioModel.getRecordURI());

                    myViewHolder.player_button.setBackgroundResource(R.drawable.playing_btn);



                }else if(audioModel.getIs_playing().equals("true")) {

                    audioModel.setIs_playing("false");

                    stopPlaying();

                    myViewHolder.player_button.setBackgroundResource(R.drawable.play_icon);


                }


            }
        });

        myViewHolder.player_description.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                if(Objects.requireNonNull(audioModel).getPublisherName().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    AlertDialog alertDialog=new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Are you sure you want to delete your audio file?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference("Lectures")
                                    .child(audioModel.getCountryName()).child(audioModel.getSchoolName())
                                    .child(audioModel.getClassName()).child(audioModel.getLectureId()).child("records").child(audioModel.getRecordId())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(context, "Your audio has been deleted successfully!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(context, "Error occurred, try to delete again later.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            dialog.dismiss();
                        }
                    });


                    try{
                        alertDialog.show();}
                    catch (Exception e){
                        Toast.makeText(context, "Something went wrong..", Toast.LENGTH_SHORT).show();
                    }

                }

                return true;
            }
        });


    }






    private void startPlaying(String fileNameOfAudio) {


            CurrentPlayer=new MediaPlayer();

            try {
                CurrentPlayer.setDataSource(fileNameOfAudio);
                CurrentPlayer.prepare();
                CurrentPlayer.start();

            } catch (IOException e) {
                e.printStackTrace();
            }


    }

    private void startPause(){
        if(CurrentPlayer!=null) {
            CurrentPlayer.pause();

        }
    }
    private void stopPause(){
        if(CurrentPlayer!=null){
            CurrentPlayer.start();
        }
    }

    private void stopPlaying() {
        if(CurrentPlayer!=null){
        CurrentPlayer.stop();
        CurrentPlayer.release();
        CurrentPlayer=null;
        }

    }


    @Override
    public int getItemCount() {
        return audioModelList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {

        private android.support.v7.widget.AppCompatButton player_button;
        private TextView player_description;
        private android.support.v7.widget.AppCompatButton pause_btn;



        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            player_button=itemView.findViewById(R.id.player_button);
            player_description=itemView.findViewById(R.id.player_description);
            pause_btn=itemView.findViewById(R.id.pause_btn);



        }
    }


}





