package com.pronuxtech.slynk;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragmentBottomNavigate extends Fragment implements View.OnClickListener {

    //    WIDGETS

    private CircleImageView imageView_AccountProfile;
    private TextView textView_AccountFullName;
    private RelativeLayout relativeLayout_ViewProfile;
    private LinearLayout linearLayout_Logout, linearLayout_ChangePass, linearLayout_MyFriends;

    String oldPassFirebase, oldPassEmail;

    //    DATABSE REFERENCES
    private DatabaseReference UsersReference;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment_bottom_navigate, container, false);

//        CASTINGS

        imageView_AccountProfile = (CircleImageView) view.findViewById(R.id.imageView_AccountProfile);
        textView_AccountFullName = (TextView) view.findViewById(R.id.textView_AccountFullName);
        relativeLayout_ViewProfile = (RelativeLayout) view.findViewById(R.id.relativeLayout_ViewProfile);
        linearLayout_MyFriends = (LinearLayout) view.findViewById(R.id.linearLayout_MyFriends);
        linearLayout_ChangePass = (LinearLayout) view.findViewById(R.id.linearLayout_ChangePass);
        linearLayout_Logout = (LinearLayout) view.findViewById(R.id.linearLayout_Logout);


        //        CLICK LISTENERS
        relativeLayout_ViewProfile.setOnClickListener(this);
        linearLayout_MyFriends.setOnClickListener(this);
        linearLayout_ChangePass.setOnClickListener(this);
        linearLayout_Logout.setOnClickListener(this);


//        FIREBASE CASTINGS
        firebaseAuth = FirebaseAuth.getInstance();
        UsersReference = FirebaseDatabase.getInstance().getReference("Users");
        RetrieveCurrentUserData();


        return view;
    }

    @Override
    public void onClick(View item) {
        switch (item.getId()) {
            case R.id.relativeLayout_ViewProfile: {
                startActivity(new Intent(getActivity(), AccountViewProfileActivity.class));
                break;
            }
            case R.id.linearLayout_MyFriends: {
                startActivity(new Intent(getActivity(), MyFriendsActivity.class));
                break;
            }
            case R.id.linearLayout_ChangePass: {
                ChangePassword();
                break;
            }
            case R.id.linearLayout_Logout: {
                Toast.makeText(getActivity(), "logout", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
                break;
            }

        }
    }

    private void ChangePassword() {
        View view = getLayoutInflater().inflate(R.layout.change_password_layout, null);


        final EditText dialog_OldPass = (EditText) view.findViewById(R.id.dialog_OldPass);
        final EditText dialog_NewPass = (EditText) view.findViewById(R.id.dialog_NewPass);
        final TextView passChange_cancel = (TextView) view.findViewById(R.id.passChange_cancel);
        final TextView passChange_confirm = (TextView) view.findViewById(R.id.passChange_confirm);


        final AlertDialog.Builder ChangePassDialog = new AlertDialog.Builder(getContext());
        ChangePassDialog.setView(view);
        final AlertDialog alertDialog = ChangePassDialog.create();
        alertDialog.show();


        passChange_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        UsersReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("password")) {
                            oldPassFirebase = dataSnapshot.child("password").getValue().toString();
                            oldPassEmail = dataSnapshot.child("email").getValue().toString();
                            Log.d("em", oldPassEmail);
//                                    dialog_OldPass.setError("Wrong Password");
//                                    dialog_OldPass.requestFocus();
//                                    passChange_confirm.setEnabled(false);
//                    signUpPart1Next.setBackgroundColor(Color.parseColor("#E7EBED"));
//                                    return;

                        }
//                                else {
//                    signUpPart1Next.setBackgroundColor(Color.parseColor("#1592E6"));
//                                    passChange_confirm.setEnabled(true);
//                                }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        passChange_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassEditText = dialog_OldPass.getText().toString().trim();
                final String newPassEditText = dialog_NewPass.getText().toString().trim();
                if (oldPassEditText.isEmpty()) {
                    dialog_OldPass.setError("old pass required");
                    dialog_OldPass.requestFocus();
                    return;
                }
                if (!oldPassEditText.equals(oldPassFirebase)) {
                    dialog_OldPass.setError("wrong password");
                    dialog_OldPass.requestFocus();
                    return;
                }
                if (oldPassEditText.isEmpty()) {
                    dialog_OldPass.setError("old pass required");
                    dialog_OldPass.requestFocus();
                    return;
                }
                if (newPassEditText.length() < 6 || oldPassEditText.length() > 15) {
                    dialog_NewPass.setError("mini 6 max 15 required");
                    dialog_NewPass.requestFocus();
                    return;
                }
                if (newPassEditText.isEmpty()) {
                    dialog_NewPass.setError("new pass required");
                    dialog_NewPass.requestFocus();
                    return;
                }

                if (!oldPassEditText.equals(newPassEditText)) {
                    Map<String, Object> updatePass = new HashMap<>();
                    updatePass.put("password", newPassEditText);

                    UsersReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(updatePass)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {


                                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        AuthCredential credential = EmailAuthProvider.getCredential(oldPassEmail, oldPassFirebase);

                                        user.reauthenticate(credential)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            user.updatePassword(newPassEditText).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(getActivity(), "pass changed successfully", Toast.LENGTH_SHORT).show();
                                                                        alertDialog.dismiss();
                                                                    } else {
                                                                        String msg = task.getException().getMessage();
                                                                        Toast.makeText(getActivity(), "error: " + msg, Toast.LENGTH_SHORT).show();

                                                                    }

                                                                }

                                                            });
                                                        } else {
                                                            String msg = task.getException().getMessage();
                                                            Toast.makeText(getActivity(), "error: " + msg, Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });


                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getActivity(), "error: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "New Password Must be different", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void RetrieveCurrentUserData() {
        UsersReference.child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("profile_img")) {
                                String profilePic = dataSnapshot.child("profile_img").getValue().toString();
                                Picasso.get().load(profilePic).into(imageView_AccountProfile);
                            }
                            if (dataSnapshot.hasChild("first_name") && dataSnapshot.hasChild("last_name")) {
                                String fName = dataSnapshot.child("first_name").getValue().toString();
                                String lName = dataSnapshot.child("last_name").getValue().toString();
                                String fullName = fName + " " + lName;

                                textView_AccountFullName.setText(fullName);
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "error: " + databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


}

