package com.pronuxtech.slynk;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.reactivex.annotations.NonNull;
import static java.lang.Integer.parseInt;

public class Signup_part_3_activity extends AppCompatActivity {

    //    WIDGETS
    private EditText editText_Day, editText_Year;
    private Spinner spinner_GraduationYear, spinner_Gender, spinner_Month;
    private Button signUpButton;

    //    STRING VARIABLES
    private String firstNamePart3, lastNamePart3, emailPart3, userNamePart3, phoneNoPart3, passwordPart3,
            universityNamePart3, coursePart3, levelOfCoursePart3, graduationYearPart3, genderPart3,
            dateMonthPart3, dateDayPart3, dateYearPart3, dateOfBirth, currentYear;
    private int dateDay;

    //    PROGRESS DIALOG
    private ProgressDialog progressDialog;

    //    DATABASE REFRENCES
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_part_3);

//        DATABASE REFRENCES
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

//        ACTION BAR HIDE
        getSupportActionBar().hide();


//        WIDGETS CASTING
        editText_Day = (EditText) findViewById(R.id.editText_Day);
        editText_Year = (EditText) findViewById(R.id.editText_Year);
        spinner_GraduationYear = (Spinner) findViewById(R.id.spinner_GraduationYear);
        spinner_Gender = (Spinner) findViewById(R.id.spinner_Gender);
        spinner_Month = (Spinner) findViewById(R.id.spinner_Month);
        signUpButton = (Button) findViewById(R.id.signUpButton);

        //        PROGRESS DIALOG INITIALIZE
        progressDialog = new ProgressDialog(this);
//========================================================================================
//        RCIEVING DATA FROM SIGNUP PART 2
//        gettingValueFromSignUpPart2();


//        SPINNERS ADAPTER AND ARRAYLIST
        spinnerArraysMenusMethod();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//           PASSING VALUES TO VARIABLES AND RESTRICTIONS
                //        VARIABLES GETTING VALUES
                graduationYearPart3 = spinner_GraduationYear.getSelectedItem().toString().trim();
                genderPart3 = spinner_Gender.getSelectedItem().toString().trim();
                dateMonthPart3 = spinner_Month.getSelectedItem().toString().trim();
                dateDayPart3 = editText_Day.getText().toString().trim();

                dateYearPart3 = editText_Year.getText().toString().trim();
                dateOfBirth = dateDayPart3 + " " + dateMonthPart3 + " " + dateYearPart3;

//                RESTRICTIONS
                if (dateDayPart3.isEmpty()) {
                    editText_Day.setError("required field");
                    editText_Day.requestFocus();
                    return;
                }
                if (!dateDayPart3.isEmpty()) {
                    dateDay = parseInt(dateDayPart3);
                }
                if (dateDay > 31 || dateDay < 1) {
                    editText_Day.setError("Invalid");
                    editText_Day.requestFocus();
                    return;
                }
                if (dateYearPart3.length() != 4) {
                    editText_Year.setError("Invalid");
                    editText_Year.requestFocus();
                    return;

                }

//                DATA SAVED IN FAIREBASE
                firebasePart3();
            }
        });
    }


    public void signUpPart3Back(View view) {
        onBackPressed();
        Toast.makeText(this, "signUpPart3Back clicked", Toast.LENGTH_SHORT).show();
    }


    private void firebasePart3() {

        progressDialog.show();
//        SignUpPart3Class add = new SignUpPart3Class(
//                firstNamePart3, lastNamePart3, emailPart3, userNamePart3, phoneNoPart3, passwordPart3,
//                universityNamePart3, coursePart3, levelOfCoursePart3,
//                graduationYearPart3, genderPart3, dateOfBirth);

        Map<String, Object> signUpPart3 = new HashMap<>();

        signUpPart3.put("graduation_year", graduationYearPart3);
        signUpPart3.put("gender", genderPart3);
        signUpPart3.put("date_of_birth", dateOfBirth);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(signUpPart3)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.hide();
                            Toast.makeText(Signup_part_3_activity.this, "All parts Data Added", Toast.LENGTH_SHORT).show();
                        } else {
                            String msg = task.getException().getMessage();
                            Toast.makeText(Signup_part_3_activity.this, "error: " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
//
//    private void retrieveDataofSignUpPart1And2FromFirebase() {
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                firstNamePart3 = dataSnapshot.child("FirstName").getValue().toString();
//                lastNamePart3 = dataSnapshot.child("LastName").getValue().toString();
//                emailPart3 = dataSnapshot.child("Email").getValue().toString();
//                userNamePart3 = dataSnapshot.child("UserName").getValue().toString();
//                phoneNoPart3 = dataSnapshot.child("PhoneNo").getValue().toString();
//                passwordPart3 = dataSnapshot.child("password").getValue().toString();
//                universityNamePart3 = dataSnapshot.child("UniversityName").getValue().toString();
//                departmentPart3 = dataSnapshot.child("Department").getValue().toString();
//                coursePart3 = dataSnapshot.child("Course").getValue().toString();
//                levelOfCoursePart3 = dataSnapshot.child("LevelOfCourse").getValue().toString();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void spinnerArraysMenusMethod() {

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime());
        String[] date = currentDate.split("/");

        currentYear = date[2];
        int intCurrentYear = Integer.parseInt(currentYear);
        //        GRADUATION YEAR ARRAY
        List<String> graduationYearArray = new ArrayList<>();

        for (int i = intCurrentYear; i >= 1970; i--) {

            graduationYearArray.add(String.valueOf(i));
        }

        ArrayAdapter<String> graduationYearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, graduationYearArray);
        graduationYearAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner_GraduationYear.setAdapter(graduationYearAdapter);
        spinner_GraduationYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals(currentYear)) {

                } else {
                    graduationYearPart3 = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        GENDER ARRAY
        List<String> genderArray = new ArrayList<>();
        genderArray.add(0, "Male");
        genderArray.add("Female");
        genderArray.add("Others");
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, genderArray);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner_Gender.setAdapter(genderAdapter);
        spinner_Gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Male")) {

                } else {
                    genderPart3 = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        MONTHS ARRAY
        List<String> monthsArray = new ArrayList<>();
        monthsArray.add(0, "Jan");
        monthsArray.add("Feb");
        monthsArray.add("Mar");
        monthsArray.add("Apr");
        monthsArray.add("May");
        monthsArray.add("Jun");
        monthsArray.add("Jul");
        monthsArray.add("Aug");
        monthsArray.add("Sep");
        monthsArray.add("Oct");
        monthsArray.add("Nov");
        monthsArray.add("Dec");
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, monthsArray);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner_Month.setAdapter(monthsAdapter);
        spinner_Month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Jan")) {

                } else {
                    dateMonthPart3 = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

//    private void gettingValueFromSignUpPart2() {
//        firstNamePart3 = getIntent().getStringExtra("firstNamePart2");
//        lastNamePart3 = getIntent().getStringExtra("lastNamePart2");
//        emailPart3 = getIntent().getStringExtra("emailPart2");
//        userNamePart3 = getIntent().getStringExtra("userNamePart2");
//        phoneNoPart3 = getIntent().getStringExtra("phoneNoPart2");
//        passwordPart3 = getIntent().getStringExtra("passwordPart2");
//        universityNamePart3 = getIntent().getStringExtra("universityNamePart2");
//        coursePart3 = getIntent().getStringExtra("coursePart2");
//        levelOfCoursePart3 = getIntent().getStringExtra("levelOfCoursePart2");
//
//    }

}
