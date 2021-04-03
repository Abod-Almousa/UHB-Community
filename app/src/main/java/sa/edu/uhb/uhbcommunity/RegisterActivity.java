package sa.edu.uhb.uhbcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sa.edu.uhb.uhbcommunity.DrawerMenu.SettingsActivity;
import sa.edu.uhb.uhbcommunity.Model.Post;
import sa.edu.uhb.uhbcommunity.Model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_username, et_fullName, et_email, et_password;
    private CheckBox cb_accept_rules;
    private Button btn_register;
    private TextView tv_login_user, cb_error, terms_of_use;

    private ProgressDialog dialog;
    private TextInputLayout inputLayout;
    private String usedUsername = "not used";
    private List<String> userList;

    private DatabaseReference firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username = findViewById(R.id.et_username);
        et_fullName = findViewById(R.id.et_fullName);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        cb_accept_rules = findViewById(R.id.cb_accept_rules);
        btn_register = findViewById(R.id.btn_register);
        tv_login_user = findViewById(R.id.tv_login_user);
        cb_error = findViewById(R.id.cb_error);
        terms_of_use = findViewById(R.id.terms_of_use);

        dialog = new ProgressDialog(this);
        inputLayout = findViewById(R.id.password_layout);
        userList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        // when the edit text is focused / unfocused
        et_username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    v.setBackgroundResource(R.drawable.focused_edit_text);
                }
                else {
                    v.setBackgroundResource(R.drawable.unfocused_edit_text);
                }
            }
        });

        et_fullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    v.setBackgroundResource(R.drawable.focused_edit_text);
                }
                else {
                    v.setBackgroundResource(R.drawable.unfocused_edit_text);
                }
            }
        });

        et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    v.setBackgroundResource(R.drawable.focused_edit_text);
                }
                else {
                    v.setBackgroundResource(R.drawable.unfocused_edit_text);
                }
            }
        });

        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    v.setBackgroundResource(R.drawable.focused_edit_text);
                }
                else {
                    v.setBackgroundResource(R.drawable.unfocused_edit_text);
                }
            }
        });

        // When click on already have an account, go to login activity
        tv_login_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // When click on terms of use button, he will be redirected to the rules page
        terms_of_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, RulesActivity.class);
                startActivity(intent);
            }
        });

        // When click on register button
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = et_username.getText().toString();
                String fullName = et_fullName.getText().toString();
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                formValidation(username,fullName,email,password);
            }
        });

    }

    private void formValidation(String username, String fullName, String email, String password) {
        // To verify the data entered
        if(TextUtils.isEmpty(username)) {
            et_username.setError(getString(R.string.required_filed));
            et_username.setFocusable(true);
            et_username.setBackgroundResource(R.drawable.error_edit_text);
        }
        else if(TextUtils.isEmpty(fullName)) {
            et_fullName.setError(getString(R.string.required_filed));
            et_fullName.setFocusable(true);
            et_fullName.setBackgroundResource(R.drawable.error_edit_text);
        }
        else if (TextUtils.isEmpty(email)) {
            et_email.setError(getString(R.string.required_filed));
            et_email.setFocusable(true);
            et_email.setBackgroundResource(R.drawable.error_edit_text);
        }
        else if (TextUtils.isEmpty(password)) {
            et_password.setError(getString(R.string.required_filed));
            et_password.setFocusable(true);
            et_password.setBackgroundResource(R.drawable.error_edit_text);
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError(getString(R.string.invalid_email));
            et_email.setFocusable(true);
            et_email.setBackgroundResource(R.drawable.error_edit_text);
        }
        else if(et_password.length() < 8) {
            inputLayout.setPasswordVisibilityToggleEnabled(false);
            et_password.setError(getString(R.string.password_length));
            et_password.setFocusable(true);
            et_password.setBackgroundResource(R.drawable.error_edit_text);

            et_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputLayout.setPasswordVisibilityToggleEnabled(true);
                    et_password.setError(null);
                }
            });
        }
        else if (!cb_accept_rules.isChecked()) {
            cb_error.setVisibility(View.VISIBLE);
        }
        else {
            validateUsername(username,fullName,email,password);
        }
    }

    private void validateUsername(String username, String fullName, String email, String password) {
        firebaseDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    if(user.getUsername() != null && user.getUsername().equals(username)) {
                        userList.add(username);
                    }
                }
                if(userList.size() >= 1) {
                    et_username.setError(getString(R.string.used_username));
                    et_username.setFocusable(true);
                    et_username.setBackgroundResource(R.drawable.error_edit_text);
                }
                else {
                    registerUser(username,fullName,email,password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // To register a new user
    private void registerUser(String username, String fullName, String email, String password) {

        dialog.setMessage(getString(R.string.please_wait));
        dialog.show();

        // To create a new user using Email and Password method
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                // Set the user info in a map to be stored in the firebase realtime
                HashMap<String,Object> map = new HashMap<>();
                map.put("username",username);
                map.put("email",email);
                map.put("fullname",fullName);
                map.put("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.put("bio","");
                map.put("image","none");
                map.put("role","user");

                // Store the new user
                firebaseDatabase.child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()) {
                                    dialog.dismiss();

                                    // Sent verification email to the user's email
                                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()) {

                                                // Display a dialog and redirected to the login page
                                                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                                                alertDialog.setTitle(getString(R.string.email_sent));
                                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(final DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                                alertDialog.show();
                                            }
                                            else {

                                                // Display errors
                                                if(task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                                    et_email.setError(getString(R.string.used_email));
                                                    et_email.setFocusable(true);
                                                    et_email.setBackgroundResource(R.drawable.error_edit_text);
                                                }

                                            }
                                        }
                                    });

                                }
                            }
                        });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();

                // Display errors
                if(e.getMessage().equals("The email address is already in use by another account.")) {
                    et_email.setError(getString(R.string.used_email));
                    et_email.setFocusable(true);
                    et_email.setBackgroundResource(R.drawable.error_edit_text);
                }
            }
        });
    }
}