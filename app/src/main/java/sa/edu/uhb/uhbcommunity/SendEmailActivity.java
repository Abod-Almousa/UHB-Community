package sa.edu.uhb.uhbcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import sa.edu.uhb.uhbcommunity.Model.User;

public class SendEmailActivity extends AppCompatActivity {

    private MaterialEditText et_email, et_subject, et_body;
    private Button btn_send_email;

    private String userId;
    private DatabaseReference firebaseDatabase;

    private String email, subject, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        et_email = findViewById(R.id.et_email);
        et_subject = findViewById(R.id.et_subject);
        et_body = findViewById(R.id.et_body);
        btn_send_email = findViewById(R.id.btn_send_email);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

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

        // We received this data from profile fragment when click on send email
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        // To get the receiver email
        firebaseDatabase.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                email = user.getEmail();

                // To set the receiver email
                et_email.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // When click on send button, the user will be redirected to an mail application to send the message
        btn_send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get the subject and message
                subject = et_subject.getText().toString();
                message = et_body.getText().toString();

                try {
                    Intent send = new Intent(Intent.ACTION_SEND);
                    send.setData(Uri.parse("mailto:"));
                    send.setType("message/rfc822");

                    send.putExtra(Intent.EXTRA_EMAIL,new String[]{email});
                    send.putExtra(Intent.EXTRA_SUBJECT,subject);
                    send.putExtra(Intent.EXTRA_TEXT,message);

                    startActivity(Intent.createChooser(send,getString(R.string.choose_app)));
                }
                catch (Exception e) {
                    Toast.makeText(SendEmailActivity.this, getString(R.string.send_error), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}