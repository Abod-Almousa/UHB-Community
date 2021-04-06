package sa.edu.uhb.uhbcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sa.edu.uhb.uhbcommunity.Adapters.PostAdapter;
import sa.edu.uhb.uhbcommunity.Model.Post;

public class ViewNotifiedPostActivity extends AppCompatActivity {

    private PostAdapter postAdapter;
    private List<Post> postList;
    private RecyclerView rv_posts;

    private String postId;
    private LottieAnimationView anim_deleted_post;

    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notified_post);

        // We got this data from the notification adapter
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        // Firebase
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

        anim_deleted_post = findViewById(R.id.anim_deleted_post);

        rv_posts = findViewById(R.id.rv_posts);
        rv_posts.setHasFixedSize(true);
        rv_posts.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this,postList,false);
        rv_posts.setAdapter(postAdapter);

        //To view the notified post
        firebaseDatabase.child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                // To check first if the post is still exist or not
                if (!snapshot.hasChildren()) {
                    anim_deleted_post.setVisibility(View.VISIBLE);
                    anim_deleted_post.playAnimation();
                    Toast.makeText(ViewNotifiedPostActivity.this, getString(R.string.view_deleted_post) , Toast.LENGTH_LONG).show();
                }
                else {
                    Post post = snapshot.getValue(Post.class);
                    postList.add(post);

                    postAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}