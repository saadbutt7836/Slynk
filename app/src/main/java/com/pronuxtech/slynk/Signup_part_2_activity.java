package com.pronuxtech.slynk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Signup_part_2_activity extends AppCompatActivity {

    //    WIDGETS
    private EditText editText_UniversityName;
    private Spinner spinner_Department, spinner_Course, spinner_LevelOfCourse;
    private Button signUpPart2Next;

    //    STRING VARIABLES
    private String firstNamePart2, lastNamePart2, emailPart2, userNamePart2, phoneNoPart2, passwordPart2,
            universityNamePart2, departmentPart2, coursePart2, levelOfCoursePart2;


    //    PROGRESS DIALOG
    private ProgressDialog progressDialog;

    //    FIREBASE REFRENCES
    private DatabaseReference databaseReference, UsersRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_part_2);

//        DATABASE REFRENCE CASTINGS
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        UsersRef = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

//      ACTION BAR HIDE
        getSupportActionBar().hide();

//        WIDGETS CASTINGS
        editText_UniversityName = (EditText) findViewById(R.id.editText_UniversityName);
        spinner_Course = (Spinner) findViewById(R.id.spinner_Course);
        spinner_LevelOfCourse = (Spinner) findViewById(R.id.spinner_LevelOfCourse);
        signUpPart2Next = (Button) findViewById(R.id.signUpPart2Next);

        //        PROGRESS DIALOG INITIALIZE
        progressDialog = new ProgressDialog(this);


//        RECIEVED VALUE FROM SIGNUP PART 1 METHOD CALL
//        gettingValueFromSignUpPart1();


//        SPINNERS ADAPTER AND ARRAYLIST
        spinnersArrayMenusMethod();


//        if (compareValue != null) {
//            int spinnerPosition = adapter.getPosition(compareValue);
//            spinner_Department.setSelection(spinnerPosition);
//    }


        signUpPart2Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//           PASSING VALUES TO VARIABLES AND RESTRICTIONS
                //                VARIABLES GETTING VALUES
                universityNamePart2 = editText_UniversityName.getText().toString().trim();
                coursePart2 = spinner_Course.getSelectedItem().toString().trim();
                levelOfCoursePart2 = spinner_LevelOfCourse.getSelectedItem().toString().trim();

//                RESTRICTIONS CHECKED
                if (universityNamePart2.isEmpty()) {
                    editText_UniversityName.setError("field required");
                    editText_UniversityName.requestFocus();
                    return;
                }

//             DATA SAVED IN FIREBASE OF SIGNUP PART2
                firebasePart2();
            }
        });


    }

    public void signUpPart2Back(View view) {
        deletingTheDataAndCurrentEmail();
        onBackPressed();

    }

    private void deletingTheDataAndCurrentEmail() {
        databaseReference = UsersRef.child(firebaseAuth.getCurrentUser().getUid());
//
////        CURRENT USER EMAIL REFRENCE
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    databaseReference.removeValue();
                    Toast.makeText(Signup_part_2_activity.this, "data deleted successfully", Toast.LENGTH_SHORT).show();
                    Log.d("registered account: ", "deleted successfully");
                } else {
                    String msg = task.getException().getMessage();
                    Toast.makeText(Signup_part_2_activity.this, "error: " + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void spinnersArrayMenusMethod() {

//        COURSE ARRAY
        List<String> courseArray = new ArrayList<>();
        courseArray.add(0, "Accounting & Finance");
        courseArray.add("Aeronautical & \n" +
                "Manufacturing Engineering");
        courseArray.add("Agriculture & Forestry");
        courseArray.add("American Studies");
        courseArray.add("Anatomy & Physiology");
        courseArray.add("Anthropology");
        courseArray.add("Archaeology");
        courseArray.add("Architecture");
        courseArray.add("Art & Design");
        courseArray.add("Aural & Oral Sciences");
        courseArray.add("Biological Sciences");
        courseArray.add("Building");
        courseArray.add("Business & \n" +
                "Management Studies");
        courseArray.add("Celtic Studies");
        courseArray.add("Chemical Engineering");
        courseArray.add("Chemistry");
        courseArray.add("Civil Engineering");
        courseArray.add("Classics & Ancient History");
        courseArray.add("Communication & \n" +
                " Media Studies");
        courseArray.add("Complementary Medicine");
        courseArray.add("Computer Science");
        courseArray.add("Counselling");
        courseArray.add("Creative Writing");
        courseArray.add("Criminology");
        courseArray.add("Dentistry");
        courseArray.add("Drama, Dance & Cinematic");
        courseArray.add("East & \n" +
                "South Asian Studies");
        courseArray.add("Economics");
        courseArray.add("Education");
        courseArray.add("Electrical & \n" +
                "Electronic Engineering");
        courseArray.add("English");
        courseArray.add("Fashion");
        courseArray.add("Film Making");
        courseArray.add("Food Science");
        courseArray.add("Forensic Science");
        courseArray.add("French");
        courseArray.add("Geography & \n" +
                "Environmental Sciences");
        courseArray.add("Geology");
        courseArray.add("General Engineering");
        courseArray.add("German");
        courseArray.add("History");
        courseArray.add("History of Art, \n" +
                "Architecture & Design");
        courseArray.add("Hospitality, Leisure,\n " +
                "Recreation & Tourism");
        courseArray.add("Iberian Languages/\n" +
                "Hispanic Studies");
        courseArray.add("Italian");
        courseArray.add("Land & Property Management");
        courseArray.add("Law");
        courseArray.add("Librarianship &\n" +
                " Information Management");
        courseArray.add("Linguistics");
        courseArray.add("Marketing");
        courseArray.add("Materials Technology");
        courseArray.add("Mathematics");
        courseArray.add("Mechanical Engineering");
        courseArray.add("Medical Technology");
        courseArray.add("Medicine");
        courseArray.add("Middle Eastern &\n" +
                " African Studies");
        courseArray.add("Music");
        courseArray.add("Nursing");
        courseArray.add("Occupational Therapy");
        courseArray.add("Optometry, Ophthalmology &\n" +
                " Orthoptics");
        courseArray.add("Pharmacology & Pharmacy");
        courseArray.add("Philosophy");
        courseArray.add("Physics and Astronomy");
        courseArray.add("Physiotherapy");
        courseArray.add("Politics");
        courseArray.add("Psychology");
        courseArray.add("Robotics");
        courseArray.add("Russian & \n" +
                "East European Languages");
        courseArray.add("Social Policy");
        courseArray.add("Social Work");
        courseArray.add("Sociology");
        courseArray.add("Sports Science");
        courseArray.add("Theology & \n" +
                "Religious Studies");
        courseArray.add("Town & Country Planning \n" +
                "and Landscape Design");
        courseArray.add("Veterinary Medicine");
        courseArray.add("Youth Work");

        ArrayAdapter<String> courseAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, courseArray);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner_Course.setAdapter(courseAdapter);
        spinner_Course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("BSCS")) {

                } else {
                    coursePart2 = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //        COURSE LEVEL ARRAY
        List<String> levelOfCourseArray = new ArrayList<>();
        levelOfCourseArray.add(0, "1st Year");
        levelOfCourseArray.add("2nd Year");
        levelOfCourseArray.add("3rd Year");
        levelOfCourseArray.add("4th Year");
        levelOfCourseArray.add("Masters");
        levelOfCourseArray.add("Diploma");
        levelOfCourseArray.add("PhD");
        levelOfCourseArray.add("Foundation Degree");

        ArrayAdapter<String> levelOfCourseAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, levelOfCourseArray);
        levelOfCourseAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner_LevelOfCourse.setAdapter(levelOfCourseAdapter);
        spinner_LevelOfCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("1st")) {

                } else {
                    levelOfCoursePart2 = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void firebasePart2() {

        progressDialog.show();
//        sendingDataMethod();
        Map<String, Object> signUpPart2 = new HashMap<>();
        String defaultProfileLink = "https://firebasestorage.googleapis.com/v0/b/flash-chat-4f598.appspot.com/o/users%2Fprofiles%2FPeUhnZj2OkdHi2C77uTzAchdLXD3?alt=media&token=996877bd-3c84-4c87-9607-162b9befe783";
        signUpPart2.put("university_name", universityNamePart2);
        signUpPart2.put("current_course", coursePart2);
        signUpPart2.put("course_level", levelOfCoursePart2);
        signUpPart2.put("profile_img", defaultProfileLink.trim());

        UsersRef.child(firebaseAuth.getCurrentUser().getUid())
                .updateChildren(signUpPart2)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Signup_part_2_activity.this, Signup_part_3_activity.class);
                            progressDialog.hide();
                            startActivity(intent);
                            Toast.makeText(Signup_part_2_activity.this, "Part 1 & 2 Data Added", Toast.LENGTH_SHORT).show();
                        } else {
                            String msg = task.getException().getMessage();
                            Toast.makeText(Signup_part_2_activity.this, "error: " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

//    private void retrieveDataOfSignUpPrt1FromFireBase() {
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                firstNamePart2 = dataSnapshot.child("FirstName").getValue().toString();
//                lastNamePart2 = dataSnapshot.child("LastName").getValue().toString();
//                emailPart2 = dataSnapshot.child("Email").getValue().toString();
//                userNamePart2 = dataSnapshot.child("UserName").getValue().toString();
//                phoneNoPart2 = dataSnapshot.child("PhoneNo").getValue().toString();
//                passwordPart2 = dataSnapshot.child("password").getValue().toString();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void sendingDataMethod() {
//
//        Intent intent = new Intent(Signup_part_2_activity.this, Signup_part_3_activity.class);
//
//
////        SENDING DATA TO SIGNUP PART 3
//        intent.putExtra("firstNamePart2", firstNamePart2);
//        intent.putExtra("lastNamePart2", lastNamePart2);
//        intent.putExtra("emailPart2", emailPart2);
//        intent.putExtra("userNamePart2", userNamePart2);
//        intent.putExtra("phoneNoPart2", phoneNoPart2);
//        intent.putExtra("passwordPart2", passwordPart2);
//        intent.putExtra("universityNamePart2", universityNamePart2);
//        intent.putExtra("coursePart2", coursePart2);
//        intent.putExtra("levelOfCoursePart2", levelOfCoursePart2);
//
//        progressDialog.hide();
//        startActivity(intent);
//    }

//    private void gettingValueFromSignUpPart1() {
//
//        firstNamePart2 = getIntent().getStringExtra("firstName");
//        lastNamePart2 = getIntent().getStringExtra("lastName");
//        emailPart2 = getIntent().getStringExtra("email");
//        userNamePart2 = getIntent().getStringExtra("userName");
//        phoneNoPart2 = getIntent().getStringExtra("phoneNo");
//        passwordPart2 = getIntent().getStringExtra("password");
//    }


}