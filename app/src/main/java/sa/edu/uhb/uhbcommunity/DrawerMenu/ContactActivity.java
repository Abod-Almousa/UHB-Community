package sa.edu.uhb.uhbcommunity.DrawerMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import sa.edu.uhb.uhbcommunity.R;
import sa.edu.uhb.uhbcommunity.SendEmailActivity;

public class ContactActivity extends AppCompatActivity {

    private MaterialEditText et_email, et_subject, et_body;
    private Button btn_send_email;
    private String email, subject, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        et_email = findViewById(R.id.et_email);
        et_subject = findViewById(R.id.et_subject);
        et_body = findViewById(R.id.et_body);
        btn_send_email = findViewById(R.id.btn_send_email);

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

        // To set the email
        et_email.setText("uhb.community.help@gmail.com");

        // When click on send button, the user will be redirected to an mail application to send the message
        btn_send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get the subject, message, email
                subject = et_subject.getText().toString();
                message = et_body.getText().toString();
                email = et_email.getText().toString();

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
                    Toast.makeText(ContactActivity.this, getString(R.string.send_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}