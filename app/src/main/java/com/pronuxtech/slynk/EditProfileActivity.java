package com.pronuxtech.slynk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    //    WIDGETS
    private EditText update_FirstName, update_LastName, update_UserName, update_UniversityName;
    private Spinner update_Course, update_CourseLevel;
    private Button update_btn;

    //    VARIABLES
    private String fName, lName, uName, uNameRetrieve, uniName, course, courseLevel;

    //    DATABASE REFERENCES
    private DatabaseReference UserRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


//        CASTINGS
        update_FirstName = (EditText) findViewById(R.id.update_FirstName);
        update_LastName = (EditText) findViewById(R.id.update_LastName);
        update_UserName = (EditText) findViewById(R.id.update_UserName);
        update_UniversityName = (EditText) findViewById(R.id.update_UniversityName);
        update_Course = (Spinner) findViewById(R.id.update_Course);
        update_CourseLevel = (Spinner) findViewById(R.id.update_CourseLevel);
        update_btn = (Button) findViewById(R.id.update_btn);


        UserRef = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        RetrieveDataForUpdateProfile();


        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fName = update_FirstName.getText().toString().trim();
                lName = update_LastName.getText().toString().trim();
                uName = update_UserName.getText().toString().trim();
                uniName = update_UniversityName.getText().toString().trim();
                course = update_Course.getSelectedItem().toString().trim();
                courseLevel = update_CourseLevel.getSelectedItem().toString().trim();


//                RESTRICTIONS
                if (fName.isEmpty()) {
                    update_FirstName.setError("First Name Required");
                    update_FirstName.requestFocus();
                    return;
                }
                if (lName.isEmpty()) {
                    update_LastName.setError("Last Name Required");
                    update_LastName.requestFocus();
                    return;
                }
                if (uName.isEmpty()) {
                    update_UserName.setError("Username Required");
                    update_UserName.requestFocus();
                    return;
                }
                if (uName.length() < 3 || uName.length() > 18) {
                    update_UserName.setError("mini 3 or maxi 18");
                    update_UserName.requestFocus();
                    return;
                }
                if (uniName.isEmpty()) {
                    update_UniversityName.setError("University Name Required");
                    update_UserName.requestFocus();
                    return;
                }

                UpdateDataFirebase();
            }
        });


    }

    private void UpdateDataFirebase() {

        String currentUid = firebaseAuth.getCurrentUser().getUid();

        Map<String, Object> UpdateDataObject = new HashMap<>();
        UpdateDataObject.put("first_name", fName);
        UpdateDataObject.put("last_name", lName);
        UpdateDataObject.put("username", uName);
        UpdateDataObject.put("university_name", uniName);
        UpdateDataObject.put("current_course", course);
        UpdateDataObject.put("course_level", courseLevel);


        UserRef.child(currentUid).updateChildren(UpdateDataObject)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(EditProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        update_UserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                uName = editable.toString();
                if (!uName.equals(uNameRetrieve)) {
                    UserNameChecked();
                }

            }
        });
    }

    private void UserNameChecked() {
        {
            Query userNameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(uName);
            userNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        update_UserName.setError("Username not available");
                        update_UserName.requestFocus();
                        update_btn.setEnabled(false);
                        update_btn.setBackgroundColor(Color.parseColor("#E7EBED"));
                        return;
                    } else {
                        update_btn.setEnabled(true);
                        update_btn.setBackgroundColor(Color.parseColor("#1592E6"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void RetrieveDataForUpdateProfile() {


        UserRef.child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            if (dataSnapshot.hasChild("first_name") && dataSnapshot.hasChild("last_name")) {
                                fName = dataSnapshot.child("first_name").getValue().toString();
                                lName = dataSnapshot.child("last_name").getValue().toString();
                                update_FirstName.setText(fName);
                                update_LastName.setText(lName);
                            }
                            if (dataSnapshot.hasChild("username")) {
                                uNameRetrieve = dataSnapshot.child("username").getValue().toString();
                                update_UserName.setText(uNameRetrieve);
                            }
                            if (dataSnapshot.hasChild("university_name")) {
                                uniName = dataSnapshot.child("university_name").getValue().toString();
                                update_UniversityName.setText(uniName);
                            }
                            if (dataSnapshot.hasChild("current_course")) {
                                course = dataSnapshot.child("current_course").getValue().toString();
                            }
                            if (dataSnapshot.hasChild("course_level")) {
                                courseLevel = dataSnapshot.child("course_level").getValue().toString();
                            }

                            SpinnerArrayMenuAdapter();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(EditProfileActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void SpinnerArrayMenuAdapter() {
//        COURSE ARRAY
        final List<String> courseArray = new ArrayList<>();
        courseArray.add("Accounting & Finance");
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
        update_Course.setAdapter(courseAdapter);

//        CASTING ADAPTER
        ArrayAdapter castCourseAdapter = (ArrayAdapter) update_Course.getAdapter();
        int coursePosition = castCourseAdapter.getPosition(course);
        update_Course.setSelection(coursePosition);
        update_Course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (adapterView.getItemAtPosition(i).equals("BSCS")) {

                } else {
                    course = adapterView.getItemAtPosition(i).toString();
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
        update_CourseLevel.setAdapter(levelOfCourseAdapter);

        //        CASTING ADAPTER
        ArrayAdapter castCourseLevelAdapter = (ArrayAdapter) update_CourseLevel.getAdapter();
        int courseLevelPosition = castCourseLevelAdapter.getPosition(courseLevel);
        update_CourseLevel.setSelection(courseLevelPosition);

        update_CourseLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("1st")) {

                } else {
                    courseLevel = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

}
