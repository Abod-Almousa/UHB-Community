package sa.edu.uhb.uhbcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import sa.edu.uhb.uhbcommunity.Model.User;

public class CommentActivity extends AppCompatActivity {

    private CircleImageView iv_profile;
    private EditText et_add_comment;
    private ImageView iv_send_comment;
    private String date;
    private String time;

    // This data will be received from the Post Adapter
    private String postId;
    private String publisherId;

    private FirebaseUser firebaseUser;
    private DatabaseReference firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        iv_profile = findViewById(R.id.iv_profile);
        et_add_comment = findViewById(R.id.et_add_comment);
        iv_send_comment = findViewById(R.id.iv_send_comment);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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

        // This data are sent from Post Adapter
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        publisherId = intent.getStringExtra("authorId");

        // To get the user profile image (sender)
        getUserInfo();

        // When click on send button
        iv_send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String comment =et_add_comment.getText().toString();

                // To check if there is comment added or not
                if(TextUtils.isEmpty(comment)) {
                    Toast.makeText(CommentActivity.this, getString(R.string.no_comment), Toast.LENGTH_SHORT).show();
                }
                else {
                    // To send the comment
                    sendComment();
                }
            }
        });

    }

    // To get the user profile image (sender)
    private void getUserInfo() {
        firebaseDatabase.child("Users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);

                        if(user.getImage().equals("none")) {
                            iv_profile.setImageResource(R.drawable.ic_tmp_profile);
                        }
                        else {
                            Picasso.get().load(user.getImage()).into(iv_profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    // To send the comment
    private void sendComment() {

        date = getCurrentDate();
        time = getCurrentTime();

        HashMap<String,Object> map = new HashMap<>();

        DatabaseReference reference = firebaseDatabase.child("Comments").child(postId);

        // We used push method to generate an ID for each comment
        String commentId = reference.push().getKey();

        map.put("commentid",commentId);
        map.put("comment",et_add_comment.getText().toString());
        map.put("publisher",firebaseUser.getUid());
        map.put("date",date);
        map.put("time",time);

        reference.child(commentId).setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            et_add_comment.setText("");
                        }
                        else {
                            Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // To get the current date of the comment "Only in EN"
    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        String date = dateFormat.format(Calendar.getInstance().getTime());

        return date;
    }

    // To get the current time of the comment "Only in EN"
    private String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
        String time = dateFormat.format(Calendar.getInstance().getTime());

        return time;
    }
}