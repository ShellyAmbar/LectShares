package com.shellyambar.ambar.wonderworld.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shellyambar.ambar.wonderworld.AudioActivity;
import com.shellyambar.ambar.wonderworld.Models.HeaderModel;
import com.shellyambar.ambar.wonderworld.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HeadersAdapter extends RecyclerView.Adapter<HeadersAdapter.myViewHolder> {


    private List<HeaderModel> headerModelList;
    private Context context;

    public HeadersAdapter( Context context,List<HeaderModel> headerModelList) {
        this.headerModelList = headerModelList;
        this.context = context;
    }



    @NonNull
    @Override
    public HeadersAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.header_item,viewGroup,false);
        return new HeadersAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadersAdapter.myViewHolder myViewHolder, int i) {

        final HeaderModel headerModel=headerModelList.get(i);
        if(headerModel!=null){

            myViewHolder.Header_Name.setText(headerModel.getLectureName());

                Glide.with(context).load(R.drawable.folder).into( myViewHolder.profile_image);


        }else{
            myViewHolder.Header_Name.setText("none");
            Glide.with(context).load(R.drawable.folder).into( myViewHolder.profile_image);

        }

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent=new Intent(context,AudioActivity.class);
               intent.putExtra("lecture",headerModel.getLectureName() );
                intent.putExtra("country", headerModel.getCountryName());
                intent.putExtra("school",headerModel.getSchoolName());
                intent.putExtra("class", headerModel.getClassName());
                intent.putExtra("lectureKey", headerModel.getLectureId());

                context.startActivity(intent);

            }
        });

        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                if(Objects.requireNonNull(headerModel).getPublisherName().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    AlertDialog alertDialog=new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Are you sure you want to delete your Lecture?");
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
                                    .child(headerModel.getCountryName()).child(headerModel.getSchoolName())
                                    .child(headerModel.getClassName()).child(headerModel.getLectureId())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(context, "Your lecture has been deleted successfully!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(context, "Error occurred, try to delete again later.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            dialog.dismiss();
                        }
                    });


                    alertDialog.show();
                }

                return true;
            }
        });




    }

    @Override
    public int getItemCount() {
        return headerModelList.size();
    }


    public static class myViewHolder extends RecyclerView.ViewHolder{


        private CircleImageView profile_image;
        private TextView Header_Name;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);


            profile_image=itemView.findViewById(R.id.profile_image);
            Header_Name=itemView.findViewById(R.id.Header_Name);


        }
    }
}
