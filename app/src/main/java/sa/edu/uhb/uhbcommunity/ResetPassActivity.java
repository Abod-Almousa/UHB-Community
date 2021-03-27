package sa.edu.uhb.uhbcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {

    private EditText et_email;
    private TextView tv_cancel;
    private TextView tv_recover;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        // For the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_email = findViewById(R.id.et_email);
        tv_cancel = findViewById(R.id.cancel);
        tv_recover = findViewById(R.id.recover);

        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        // When click on cancel button
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        // When click on recover button
        tv_recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = et_email.getText().toString().trim();

                // To validate the entered email address
                if(TextUtils.isEmpty(email)) {
                    et_email.setError(getString(R.string.required_filed));
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    et_email.setError(getString(R.string.invalid_email));
                }
                else {
                    recoverPassword(email);
                }
            }
        });
    }


    // To send an email message to the user to reset his password
    private void recoverPassword(String email) {

        dialog.setMessage(getString(R.string.sending_email));
        dialog.show();

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    dialog.dismiss();

                    // Display a dialog to tell the user that an email message sent to his email address
                    AlertDialog alertDialog = new AlertDialog.Builder(ResetPassActivity.this).create();
                    alertDialog.setTitle(getString(R.string.recovering_email_sent));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            Intent intent = new Intent(ResetPassActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    alertDialog.show();
                }
                else {

                    // Display a dialog to tell the user that failed to send the email
                    AlertDialog alertDialog = new AlertDialog.Builder(ResetPassActivity.this).create();
                    alertDialog.setTitle(getString(R.string.failed_to_send));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            /* Intent intent = new Intent(ResetPassActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish(); */
                        }
                    });
                    alertDialog.show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

