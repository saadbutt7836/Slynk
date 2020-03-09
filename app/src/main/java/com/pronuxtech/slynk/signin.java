package com.pronuxtech.slynk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pronuxtech.slynk.HomePageActivity;
import com.pronuxtech.slynk.R;

public class signin extends Fragment {

    //    WIDGETS
    private EditText editText_SignInEmail, editText_SignInPassword;
    private Button Btn_SignIn;
    private TextView textView_ForgotPassword;

    //    STRINGS VARIABLES
    private String signInEmail, signInPasword;

    //    PROGRESS DIALOG
    ProgressDialog progressDialog;

    //    FIREBASE REFRENCES
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View signInFragmentLayout = inflater.inflate(R.layout.fragment_signin, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

//        WIDGETS CASTING
        editText_SignInEmail = (EditText) signInFragmentLayout.findViewById(R.id.editText_SignInEmail);
        editText_SignInPassword = (EditText) signInFragmentLayout.findViewById(R.id.editText_SignInPassword);
        textView_ForgotPassword = (TextView) signInFragmentLayout.findViewById(R.id.textView_ForgotPassword);
        Btn_SignIn = (Button) signInFragmentLayout.findViewById(R.id.Btn_SignIn);

        progressDialog = new ProgressDialog(getActivity());
//=========================================================================================================================================================


//        SIGN IN BUTTON CLICK LISTENER
        Btn_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signInEmail = editText_SignInEmail.getText().toString().trim();
                signInPasword = editText_SignInPassword.getText().toString().trim();

//                RESTRICTIONS
                if (signInEmail.isEmpty()) {
                    editText_SignInEmail.setError("Email Required");
                    editText_SignInEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(signInEmail).matches()) {
                    editText_SignInEmail.setError("Invalid Email");
                    editText_SignInEmail.requestFocus();
                    return;
                }
                if (signInPasword.isEmpty()) {
                    editText_SignInPassword.setError("Password Required");
                    editText_SignInPassword.requestFocus();
                    return;
                }

                signInStart();
            }
        });

//        FORGOT PASSWORD CLICKLISTENER
        textView_ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });

        return signInFragmentLayout;
    }

    private void forgotPassword() {
        View forgotDialog = getLayoutInflater().inflate(R.layout.forgot_passsword_dialog, null);

        final EditText editText_ForgotPassEmail = (EditText) forgotDialog.findViewById(R.id.editText_ForgotPassEmail);
        final TextView textView_Send = (TextView) forgotDialog.findViewById(R.id.textView_Send);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setView(forgotDialog);
        final AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();


        textView_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String forgotEmail = editText_ForgotPassEmail.getText().toString().trim();

                if (forgotEmail.isEmpty()) {
                    editText_ForgotPassEmail.setError("Email Required");
                    editText_ForgotPassEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(forgotEmail).matches()) {
                    editText_ForgotPassEmail.setError("Invalid Format");
                    editText_ForgotPassEmail.requestFocus();
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(forgotEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    alertDialog1.dismiss();
                                    Toast.makeText(getActivity(), "Check your email account", Toast.LENGTH_SHORT).show();
                                } else {
                                    alertDialog1.dismiss();
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private void signInStart() {

        firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPasword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressDialog.show();
                        if (task.isSuccessful()) {
                            progressDialog.setTitle("Signing In");
                            progressDialog.setMessage(signInEmail);
//                            currentUserInfo();


                            Intent intent = new Intent(getActivity(), HomePageActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            progressDialog.setTitle("Signing In");
                            progressDialog.setMessage(task.getException().getMessage());
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }
                    }
                });
    }

    private void currentUserInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String activeUserFirstName = dataSnapshot.child("first_name").getValue().toString();
                String activeUserLastName = dataSnapshot.child("last_name").getValue().toString();
                String activeUserEmail = dataSnapshot.child("email").getValue().toString();
                String activeUserName = dataSnapshot.child("user_name").getValue().toString();


                getActivity().finish();
                Intent intent = new Intent(getActivity(), HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("sign_fName", activeUserFirstName);
                intent.putExtra("sign_lName", activeUserLastName);
                intent.putExtra("sign_u_email", activeUserEmail);
                intent.putExtra("sign_uName", activeUserName);

                startActivity(intent);
                Toast.makeText(getActivity(), "fName: " + activeUserFirstName, Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "lName: " + activeUserLastName, Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "email: " + activeUserEmail, Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "username: " + activeUserName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
