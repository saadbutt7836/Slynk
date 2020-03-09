package com.pronuxtech.slynk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;


public class signup_part_1 extends Fragment {

    //    WIDGETS
    private Button signUpPart1Next;
    private TextView textView_UserName, signUpPart1Back;
    private EditText editText_FirstName, editText_LastName, editText_Email, editText_UserName, editText_PhoneNo, editText_Password, editText_ConfirmPassword;
    private CountryCodePicker spinner_CountryCode;

    //    STRINGS VARIABLES
    private String firstName, lastName, email, userName, phoneWithoutCode, phoneNo, password, confirmPassword, uniqueUserId;
    private boolean userNameBool;
    //    SPLITTING STRING ARRAY
    private String[] splitEmail;

    //    PROGRESS DIALOG
    private ProgressDialog progressDialog;

    //    FIREBASE REFRENCES
    private DatabaseReference UsersRef;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View signUpPart1Layout = inflater.inflate(R.layout.fragment_signup_part_1, container, false);

        UsersRef = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        signUpPart1Back = (TextView) signUpPart1Layout.findViewById(R.id.signUpPart1Back);
        signUpPart1Next = (Button) signUpPart1Layout.findViewById(R.id.signUpPart1Next);
        editText_FirstName = (EditText) signUpPart1Layout.findViewById(R.id.editText_FirstName);
        editText_LastName = (EditText) signUpPart1Layout.findViewById(R.id.editText_LastName);
        editText_Email = (EditText) signUpPart1Layout.findViewById(R.id.editText_Email);
        editText_UserName = (EditText) signUpPart1Layout.findViewById(R.id.editText_UserName);
        spinner_CountryCode = (CountryCodePicker) signUpPart1Layout.findViewById(R.id.spinner_CountryCode);
        editText_PhoneNo = (EditText) signUpPart1Layout.findViewById(R.id.editText_PhoneNo);
        editText_Password = (EditText) signUpPart1Layout.findViewById(R.id.editText_Password);
        editText_ConfirmPassword = (EditText) signUpPart1Layout.findViewById(R.id.editText_ConfirmPassword);

//        PROGRESS DIALOG INITIALIZE
        progressDialog = new ProgressDialog(getActivity());

//        signUpPart1Next.setEnabled(false);

        editText_Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Patterns.EMAIL_ADDRESS.matcher(editText_Email.getText()).matches()) {
                    splitEmail = editText_Email.getText().toString().split("@");
//                    editText_UserName.setText(splitEmail[0] + "1");

                }
            }
        });


//        editText_UserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                userName = editText_UserName.getText().toString().trim();
//                UserNameChecked();
//                if (editText_UserName.hasFocus()) {
//                    userName = editText_UserName.getText().toString().trim();
//                    UserNameChecked();
//                    return;
//                }
//            }
//        });


//        textView_UserName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                splitEmail = editText_Email.getText().toString().split("@");
//                textView_UserName.setText(splitEmail[0]);
//            }
//        });

//        SIGN UP PART 1 BACK BUTTON CLICK LISTENER
        signUpPart1Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
//        SIGN UP PART 1 NEXT BUTTON CLICK LISTENER
        signUpPart1Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//           PASSING VALUES TO VARIABLES AND RESTRICTIONS

                //        VARIABLES GETTING VALUES
                spinner_CountryCode.registerCarrierNumberEditText(editText_PhoneNo);
                firstName = editText_FirstName.getText().toString().trim();
                lastName = editText_LastName.getText().toString().trim();
                email = editText_Email.getText().toString().trim();
//                splitEmail = editText_Email.getText().toString().split("@");
//                editText_UserName.setText(splitEmail[0]);
                userName = editText_UserName.getText().toString().trim();
                phoneWithoutCode = editText_PhoneNo.getText().toString().trim();
                phoneNo = spinner_CountryCode.getFullNumberWithPlus();
                password = editText_Password.getText().toString().trim();
                confirmPassword = editText_ConfirmPassword.getText().toString().trim();
//                userNameChecked();

//        RESTRICTIONS
                if (firstName.isEmpty()) {
                    editText_FirstName.setError("FirstName required");
                    editText_FirstName.requestFocus();
                    return;
                }
                if (lastName.isEmpty()) {
                    editText_LastName.setError("LastName required");
                    editText_LastName.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    editText_Email.setError("Email required");
                    editText_Email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editText_Email.setError("Invalid email");
                    editText_Email.requestFocus();
                    return;
                }
                if (userName.isEmpty()) {
                    editText_UserName.setError("UserName required");
                    editText_UserName.requestFocus();
                    return;
                }
                if (userName.length() < 3 || userName.length() > 18) {
                    editText_UserName.setError("mini 3 or maxi 18");
                    editText_UserName.requestFocus();
                    return;
                }
                if (phoneWithoutCode.isEmpty()) {
                    phoneNo = "";
                }
                if (password.isEmpty()) {
                    //                else {
//                    if (phoneWithoutCode.length() < 4 || phoneWithoutCode.length() > 15) {
//                        editText_PhoneNo.setError("minimum 4 & maximum 15 digits required");
//                        editText_PhoneNo.requestFocus();
//                        return;
//                    }
//                }
                    editText_Password.setError("Password required");
                    editText_Password.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    editText_Password.setError("Minimum 6 characters required");
                    editText_Password.requestFocus();
                    return;
                }
                if (confirmPassword.isEmpty()) {
                    editText_ConfirmPassword.setError("Confirm password required");
                    editText_ConfirmPassword.requestFocus();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    editText_ConfirmPassword.setError("Password do not match");
                    editText_ConfirmPassword.requestFocus();
                    return;
                }
                //                int intPassword = Integer.parseInt(password);
//                int intConfirmPassword = Integer.parseInt(confirmPassword);
//                if (intPassword != intConfirmPassword) {
//                    editText_ConfirmPassword.setError("Password do not match");
//                    editText_ConfirmPassword.requestFocus();
//                    return;
//                }
//                DATA SAVED IN FAIREBASE
//                else if (userNameBool) {
////                    userNameBool = false;
//                    editText_UserName.setError("username already registered");
//                    editText_UserName.requestFocus();
//                    return;
//
//                }
                firebase();


            }
        });


        return signUpPart1Layout;
    }

    @Override
    public void onStart() {
        super.onStart();

        editText_UserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userName = editable.toString();
                UserNameChecked();
            }
        });


    }

    private void UserNameChecked() {
        Query userNameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(userName);
        userNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
//                    userNameBool = true;
                    editText_UserName.setError("Username not available");
                    editText_UserName.requestFocus();
                    signUpPart1Next.setEnabled(false);
//                    signUpPart1Next.setBackgroundColor(Color.parseColor("#E7EBED"));
                    return;

                } else {
//                    signUpPart1Next.setBackgroundColor(Color.parseColor("#1592E6"));
                    signUpPart1Next.setEnabled(true);
//                    userNameBool = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void firebase() {

        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        if (task.isSuccessful()) {
//                            SEND VALUES TO SIGNUP PART 2 AND START ACTIVITY
//                            sendingValueToSignUpPart2();
                            uniqueUserId = firebaseAuth.getCurrentUser().getUid().trim();
                            Map<String, Object> signUpPart1 = new HashMap<>();
                            signUpPart1.put("uid", uniqueUserId);
                            signUpPart1.put("first_name", firstName);
                            signUpPart1.put("last_name", lastName);
                            signUpPart1.put("email", email);
                            signUpPart1.put("username", userName);
                            signUpPart1.put("phone", phoneNo);
                            signUpPart1.put("password", password);

                            UsersRef.child(uniqueUserId)
                                    .updateChildren(signUpPart1)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(getActivity(), Signup_part_2_activity.class);

                                                startActivity(intent);
                                                Toast.makeText(getActivity(), "Part 1 Data Added", Toast.LENGTH_SHORT).show();
                                            } else {
                                                String msg = task.getException().getMessage();
                                                Toast.makeText(getActivity(), "error: " + msg, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                Toast.makeText(getActivity(), "User already Registered", Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });


    }

    private void sendingValueToSignUpPart2() {
        Intent intent = new Intent(getActivity(), Signup_part_2_activity.class);

        //        SENDING VALUE TO SIGNUP PART 2
        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        intent.putExtra("email", email);
        intent.putExtra("userName", userName);
        intent.putExtra("phoneNo", phoneNo);
        intent.putExtra("password", password);

        startActivity(intent);
    }


}
