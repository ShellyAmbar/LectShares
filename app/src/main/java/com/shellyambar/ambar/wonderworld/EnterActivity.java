package com.shellyambar.ambar.wonderworld;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.shellyambar.ambar.wonderworld.Models.ClassModel;
import com.shellyambar.ambar.wonderworld.Models.CountryModel;
import com.shellyambar.ambar.wonderworld.Models.SchoolModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EnterActivity extends AppCompatActivity implements View.OnClickListener {
    private AutoCompleteTextView new_country_provided;
    private AutoCompleteTextView new_school_provided;
    private AutoCompleteTextView new_class_provided;
    private ImageView Arrow_down_country;
    private ImageView Arrow_down_school;
    private ImageView Arrow_down_class;


    private static final String EMAIL = "email";
    //private static List<String[]> ClassesIsrael;

    private String countrySelected;
    private String schoolSelected;
    private String classSelected;
    private int indexCountry;
    private int indexSchool;
    private Button nextBTN;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private boolean isLoggedIn;
    private FirebaseAuth mAuth;
    private DataOrgenizer dataOrgenizer;
    private Button addNewSection;
    private DatabaseReference databaseReferenceOfCountries;
    private DatabaseReference databaseReferenceOfSchools;
    private DatabaseReference databaseReferenceOfClasses;
    private AccessToken accessToken;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_enter);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        new_country_provided=findViewById(R.id.new_country_provided);
        new_school_provided=findViewById(R.id.new_school_provided);
        new_class_provided=findViewById(R.id.new_class_provided);
        nextBTN=findViewById(R.id.nextBTN);
        addNewSection=findViewById(R.id.addNewSection);

        Arrow_down_country=findViewById(R.id.Arrow_down_country);
        Arrow_down_school=findViewById(R.id.Arrow_down_school);
        Arrow_down_class=findViewById(R.id.Arrow_down_class);

        Arrow_down_country.setOnClickListener( this);
        Arrow_down_school.setOnClickListener(this);
        Arrow_down_class.setOnClickListener(this);
        addNewSection.setOnClickListener(this);
        schoolSelected="None";
        new_school_provided.setText(schoolSelected);
        countrySelected="None";
        new_country_provided.setText(countrySelected);
        classSelected="None";
        new_class_provided.setText(classSelected);
        indexCountry=0;
        indexSchool=0;
        isLoggedIn=false;

        databaseReferenceOfCountries=FirebaseDatabase.getInstance().getReference("Countries");
        databaseReferenceOfSchools=FirebaseDatabase.getInstance().getReference("Schools");
        databaseReferenceOfClasses=FirebaseDatabase.getInstance().getReference("Classes");

        dataOrgenizer=new DataOrgenizer(getApplicationContext());



        mAuth=FirebaseAuth.getInstance();



        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {


                        String AccessToken = loginResult.getAccessToken().getToken();
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                            }
                        });

                        Bundle Parameters = new Bundle();
                        Parameters.putString("fields","email");
                        request.setParameters(Parameters);
                        request.executeAsync();
                        // App code
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(EnterActivity.this, "Problem occurred ,try again later.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
            }
        });




        nextBTN.setOnClickListener(this);



    }



    private void setAllDatabaseDataOrginized() throws InterruptedException {


        // 3 listeners of database changes

        databaseReferenceOfCountries.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                String countryName=snapshot.getValue(CountryModel.class).getCountryName();

                                dataOrgenizer.addNewCountry(countryName);

                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        databaseReferenceOfSchools.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      if(dataSnapshot!=null){
                          for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                              dataOrgenizer.addNewSchoolOfCountry
                                      (snapshot.getValue(SchoolModel.class).getCountryName(),
                                              snapshot.getValue(SchoolModel.class).getSchoolName()  );
                          }
                      }

                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });

        databaseReferenceOfClasses.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null) {

                            for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                                String countryName=snapshot.getValue(ClassModel.class).getCountryName();
                                String schoolName=snapshot.getValue(ClassModel.class).getSchoolName();
                                String className=snapshot.getValue(ClassModel.class).getClassName();

                                dataOrgenizer.addNewSchoolClass(countryName,schoolName,className);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.Arrow_down_country:
                databaseReferenceOfCountries.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                String countryName=snapshot.getValue(CountryModel.class).getCountryName();

                                dataOrgenizer.addNewCountry(countryName);

                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                databaseReferenceOfSchools.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){
                            for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                dataOrgenizer.addNewSchoolOfCountry
                                        (snapshot.getValue(SchoolModel.class).getCountryName(),
                                                snapshot.getValue(SchoolModel.class).getSchoolName()  );
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                databaseReferenceOfClasses.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null) {

                            for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                                String countryName=snapshot.getValue(ClassModel.class).getCountryName();
                                String schoolName=snapshot.getValue(ClassModel.class).getSchoolName();
                                String className=snapshot.getValue(ClassModel.class).getClassName();

                                dataOrgenizer.addNewSchoolClass(countryName,schoolName,className);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                countryChoice();
                break;

            case R.id.Arrow_down_school:

                schoolchoice();
                break;
            case R.id.nextBTN:
                accessToken = AccessToken.getCurrentAccessToken();
                isLoggedIn = accessToken != null && !accessToken.isExpired();
                if(countrySelected.equals("None")|| schoolSelected.equals("None") || !isLoggedIn || classSelected.equals("None")){
                    Toast.makeText(this, "Choose Country,School and connect to facebook please", Toast.LENGTH_SHORT).show();

                }else {
                    Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                    intent.putExtra("country", countrySelected);
                    intent.putExtra("school", schoolSelected);
                    intent.putExtra("class", classSelected);

                    startActivity(intent);

                }
                break;

            case R.id.Arrow_down_class:



                classChoise();
                break;

            case R.id.addNewSection:

                alerDialogAddNewSection();
                break;

        }

    }


    private void alerDialogAddNewSection() {


        AlertDialog.Builder builder=new AlertDialog.Builder(EnterActivity.this);
        CharSequence[] options=new CharSequence[]{
                "Create new country field",
                "Create new school field",
                "Create new class field"

        };






       //LinearLayout.LayoutParams linearLayoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout=new LinearLayout(this);

        builder.setView(linearLayout);




        builder.setTitle("What do you wish to create?");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which==0){
                    alertDialogAddNewSectionCountry();

                } else if (which == 1) {
                    alertDialogAddNewSectionSchool();

                }else if(which==2){
                    alertDialogAddNewSectionClass();
                }

            }
        });

        builder.show();
    }

    private void alertDialogAddNewSectionClass() {
        android.app.AlertDialog.Builder alertDialog= new android.app.AlertDialog.Builder(EnterActivity.this);
        alertDialog.setTitle("Add a new class");
        final EditText addCountryEditText=new EditText(EnterActivity.this);
        final EditText addSchoolEditText=new EditText(EnterActivity.this);
        final EditText addClassEditText=new EditText(EnterActivity.this);
        addCountryEditText.setHint("Enter country name ");
        addSchoolEditText.setHint("Enter school name ");
        addClassEditText.setHint("Enter class name ");
        LinearLayout linearLayout=new LinearLayout(this);

        linearLayout.setOrientation(LinearLayout.VERTICAL);


        linearLayout.addView(addCountryEditText, new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0));
        linearLayout.addView(addSchoolEditText, new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0));

        linearLayout.addView(addClassEditText, new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0));


        alertDialog.setView(linearLayout);
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String countryName=addCountryEditText.getText().toString().toLowerCase();
                String schoolName=addSchoolEditText.getText().toString().toLowerCase();
                final String className=addClassEditText.getText().toString().toLowerCase();

                if(countryName.contains("#") || countryName.contains(".")||
                        countryName.contains("]") || countryName.contains("[") || countryName.contains("$")){
                    Toast.makeText(EnterActivity.this, "Country name must not contain any : '#', '.', '$',']','[' ", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                if(schoolName.contains("#") || schoolName.contains(".")||
                        schoolName.contains("]") || schoolName.contains("[") || schoolName.contains("$")){
                    Toast.makeText(EnterActivity.this, "School name must not contain any : '#', '.', '$',']','[' ", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                if(className.contains("#") || className.contains(".")||
                        className.contains("]") || className.contains("[") || className.contains("$")){
                    Toast.makeText(EnterActivity.this, "School name must not contain any : '#', '.', '$',']','[' ", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else{

                    if(!countryName.isEmpty() && !schoolName.isEmpty() && !className.isEmpty()){
                        if(!dataOrgenizer.isClassOfSchoolOfCountryExist(countryName,schoolName,className) &&
                                dataOrgenizer.isCountryExist(countryName) && dataOrgenizer.isSchoolOfCountryExist(countryName,schoolName)){
                            // dataOrgenizer.addNewSchoolClass(countryName,schoolName,className);
                            Map<String, Object> classMap= new HashMap<String, Object>();
                            classMap.put("countryName", countryName);
                            classMap.put("schoolName", schoolName);
                            classMap.put("className", className);

                            String classKey=databaseReferenceOfClasses.push().getKey();
                            databaseReferenceOfClasses.child(classKey).setValue(classMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            Toast.makeText(EnterActivity.this, className+" added successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else{
                            Toast.makeText(EnterActivity.this, "This data is not valid!", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(EnterActivity.this, "One or more fields are empty..", Toast.LENGTH_SHORT).show();
                    }

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

    private void alertDialogAddNewSectionSchool() {
        android.app.AlertDialog.Builder alertDialog= new android.app.AlertDialog.Builder(EnterActivity.this);
        alertDialog.setTitle("Add a new school");
        final EditText addCountryEditText=new EditText(EnterActivity.this);
        final EditText addSchoolEditText=new EditText(EnterActivity.this);

        addCountryEditText.setHint("Enter country name ");
        addSchoolEditText.setHint("Enter school name ");
        LinearLayout linearLayout=new LinearLayout(this);

        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(addCountryEditText, new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0));
        linearLayout.addView(addSchoolEditText, new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0));




        alertDialog.setView(linearLayout);
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String countryName=addCountryEditText.getText().toString().toLowerCase();
                String schoolName=addSchoolEditText.getText().toString().toLowerCase();


                if(countryName.contains("#") || countryName.contains(".")||
                        countryName.contains("]") || countryName.contains("[") || countryName.contains("$")){
                    Toast.makeText(EnterActivity.this, "Country name must not contain any : '#', '.', '$',']','[' ", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                if(schoolName.contains("#") || schoolName.contains(".")||
                        schoolName.contains("]") || schoolName.contains("[") || schoolName.contains("$")){
                    Toast.makeText(EnterActivity.this, "School name must not contain any : '#', '.', '$',']','[' ", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else{

                    if(!countryName.isEmpty() && !schoolName.isEmpty()){

                        if(!dataOrgenizer.isSchoolOfCountryExist(countryName,schoolName) && dataOrgenizer.isCountryExist(countryName)){
                            //  dataOrgenizer.addNewSchoolOfCountry(countryName,schoolName);
                            Map<String, Object> schoolMap= new HashMap<String, Object>();
                            schoolMap.put("countryName", countryName);
                            schoolMap.put("schoolName",schoolName);
                            String schoolKey=databaseReferenceOfSchools.push().getKey();
                            databaseReferenceOfSchools.child(schoolKey).setValue(schoolMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                        }else{
                            Toast.makeText(EnterActivity.this, "This data is not valid!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(EnterActivity.this, "One or more fields are empty..", Toast.LENGTH_SHORT).show();
                    }

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

    private void alertDialogAddNewSectionCountry() {
        android.app.AlertDialog.Builder alertDialog= new android.app.AlertDialog.Builder(EnterActivity.this);
        alertDialog.setTitle("Add a new country");
        final EditText addCountryEditText=new EditText(EnterActivity.this);


        addCountryEditText.setHint("Enter country name ");

        LinearLayout linearLayout=new LinearLayout(this);

        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(addCountryEditText, new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0));



        alertDialog.setView(linearLayout);
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String countryName=addCountryEditText.getText().toString().toLowerCase();

                if(countryName.contains("#") || countryName.contains(".")||
                        countryName.contains("]") || countryName.contains("[") || countryName.contains("$")){
                    Toast.makeText(EnterActivity.this, "Country name must not contain any : '#', '.', '$',']','[' ", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else{
                    if(!countryName.isEmpty() ){
                        if(!dataOrgenizer.isCountryExist(countryName)){

                            Map<String,Object> countryMap=new HashMap<String, Object>();
                            countryMap.put("countryName", countryName);

                            //  dataOrgenizer.addNewCountry(countryName);
                            String countryKey=databaseReferenceOfCountries.push().getKey();
                            databaseReferenceOfCountries.child(countryKey).setValue(countryMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                        }else{
                            Toast.makeText(EnterActivity.this,  countryName+" Is already exist!", Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        Toast.makeText(EnterActivity.this, "Please fill the blank field", Toast.LENGTH_SHORT).show();
                    }
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




    private void classChoise() {

        ArrayAdapter<String> classAdapter =new ArrayAdapter<>(this,R.layout
                .support_simple_spinner_dropdown_item, dataOrgenizer.getClassesPerSchoolList(schoolSelected));

        new_class_provided.setAdapter(classAdapter);
        new_class_provided.showDropDown();
        new_class_provided.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                classSelected=new_class_provided.getText().toString();

            }
        });
    }

    private void countryChoice() {
        ArrayAdapter<String> countryAdapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,dataOrgenizer.getCountriesList());
        new_country_provided.setAdapter(countryAdapter);
        new_country_provided.showDropDown();
        new_country_provided.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countrySelected= new_country_provided.getText().toString();
                new_school_provided.setText("None");
                new_class_provided.setText("None");

            }
        });






    }
    private void schoolchoice(){

        ArrayAdapter<String> schoolAdapter =new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item
                , dataOrgenizer.getSchoolsPerCountryList(countrySelected));

        new_school_provided.setAdapter(schoolAdapter);
        new_school_provided.showDropDown();
        new_school_provided.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                schoolSelected= new_school_provided.getText().toString();
                new_class_provided.setText("None");



            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void handleFacebookAccessToken(AccessToken token) {

        mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(EnterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            String eror=task.getException().toString();
                            Log.d("facebook_eror_exeption", eror );
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        try {
            setAllDatabaseDataOrginized();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null){
            isLoggedIn=true;
        }else{
            isLoggedIn=false;
        }

    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(EnterActivity.this);


        //LinearLayout.LayoutParams linearLayoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout=new LinearLayout(this);



        builder.setView(linearLayout);





        builder.setMessage("Do you sure you want to leave?");

        builder.setNeutralButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.show();

    }
}
