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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText et_email, et_password;
    private CheckBox cb_remember_me;
    private Button btn_login;
    private TextView tv_forgot_pass, tv_register_user;

    private TextInputLayout inputLayout;
    private ProgressDialog dialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        cb_remember_me = findViewById(R.id.cb_remember_me);
        btn_login = findViewById(R.id.btn_login);
        tv_forgot_pass = findViewById(R.id.tv_forgot_pass);
        tv_register_user = findViewById(R.id.tv_register_user);

        inputLayout = findViewById(R.id.password_layout);
        dialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        // when the edit text is focused / unfocused
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

        // When click on do not have an account, go to Register activity
        tv_register_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // When click on forgot password, redirected to reset password page
        tv_forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ResetPassActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // When click on login button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                // To validate the entered data
                if (TextUtils.isEmpty(email)) {
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
                else {
                    loginUser(email,password);
                }

                // This shared Preferences used to keep the user logged in the application.
                // Used in the Start Activity.
                if(cb_remember_me.isChecked())
                {
                    getSharedPreferences("REMEMBER",MODE_PRIVATE).edit().putString("rememberMe","rememberMe").apply();
                }
                else {
                    getSharedPreferences("REMEMBER",MODE_PRIVATE).edit().clear().commit();
                }
            }
        });


    }

    // To allow the user to login
    private void loginUser(String email, String password) {

        dialog.setMessage(getString(R.string.please_wait));
        dialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    dialog.dismiss();

                    // To check if the user verified his account or not.
                    if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    else {

                        // Display a dialog to tell the user to verify his account
                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                        alertDialog.setTitle(getString(R.string.please_verify));
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {

                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();

                // Display errors
                if(e.getMessage().equals("The password is invalid or the user does not have a password.")) {

                    // Display an error to tell the user the password is not correct
                    et_password.setError(getString(R.string.incorrect_pass));
                    et_password.setFocusable(true);
                    et_password.setBackgroundResource(R.drawable.error_edit_text);
                }
                else if(e.getMessage().equals("The user account has been disabled by an administrator.")) {

                    // Display a dialog to tell the user his account is banned
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setTitle(getString(R.string.banned_account));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                else if(e.getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {

                    // Display an error to tell the user there is no account match created by this email
                    et_email.setError(getString(R.string.no_account_email));
                    et_email.setFocusable(true);
                    et_email.setBackgroundResource(R.drawable.error_edit_text);
                }
            }
        });
    }
}