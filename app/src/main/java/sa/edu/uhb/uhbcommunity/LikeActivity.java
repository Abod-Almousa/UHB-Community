package sa.edu.uhb.uhbcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sa.edu.uhb.uhbcommunity.Adapters.UserAdapter;
import sa.edu.uhb.uhbcommunity.Model.Post;
import sa.edu.uhb.uhbcommunity.Model.User;

public class LikeActivity extends AppCompatActivity {

    private RecyclerView rv_users;
    private UserAdapter userAdapter;
    private List<User> userList;

    private String PostId;
    private List<String> idList;
    private LottieAnimationView anim_likes;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

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

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        anim_likes = findViewById(R.id.anim_likes);

        // This data comes from post adapter
        Intent intent = getIntent();
        PostId = intent.getStringExtra("postId");

        // For the Recycler View
        rv_users = findViewById(R.id.rv_users);
        rv_users.setHasFixedSize(true);
        rv_users.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, false);
        rv_users.setAdapter(userAdapter);

        idList = new ArrayList<>();

        // To get the users who liked this post
        getLikes();
    }

    // To get the users who liked this post
    private void getLikes() {

        firebaseDatabase.child("Likes").child(PostId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // To get the IDs for the users
                idList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    idList.add(dataSnapshot.getKey());
                }

                firebaseDatabase.child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        userList.clear();
                        for(DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                            User user = dataSnapshot2.getValue(User.class);

                            if(idList.contains(user.getId())) {
                                userList.add(user);
                            }
                        }
                        userAdapter.notifyDataSetChanged();

                        // To view the animation if there are no comments
                        if(userAdapter.getItemCount() == 0) {
                            anim_likes.setVisibility(View.VISIBLE);
                            anim_likes.playAnimation();
                        }
                        else {
                            anim_likes.cancelAnimation();
                            anim_likes.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}