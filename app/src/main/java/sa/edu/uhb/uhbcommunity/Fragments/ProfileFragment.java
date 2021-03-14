package sa.edu.uhb.uhbcommunity.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sa.edu.uhb.uhbcommunity.Adapters.PostAdapter;
import sa.edu.uhb.uhbcommunity.EditProfileActivity;
import sa.edu.uhb.uhbcommunity.Model.Post;
import sa.edu.uhb.uhbcommunity.Model.User;
import sa.edu.uhb.uhbcommunity.R;
import sa.edu.uhb.uhbcommunity.SendEmailActivity;


public class ProfileFragment extends Fragment {

    // For collapsing Toolbar
    CollapsingToolbarLayout  collapsingToolbar;

    // For profile
    private CircleImageView iv_profile;
    private TextView tv_fullName;
    private ImageView iv_verified;
    private TextView tv_no_of_posts;
    private TextView profile_btn;
    private RelativeLayout bio_card;
    private TextView tv_bio;

    // For post list
    private ImageView iv_post_list;
    private RecyclerView rv_posts;
    private List<Post> postList;
    private PostAdapter postAdapter;

    // For saved list
    private ImageView iv_saved_list;
    private RecyclerView rv_saved;
    private List<Post> savedList;
    private PostAdapter savedAdapter;

    // Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference firebaseDatabase;

    // Shared preferences
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // Shared preferences
        // This data is received from post adapter and comment adapter when click on the user to open his profile
        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                .getString("userId","none");

        if(data.equals("none")) {
            userId = firebaseUser.getUid(); // There is no dara received (open my profile)
        }
        else {
            userId = data; // open the user profile
        }

        // collapsing Toolbar
        collapsingToolbar = view.findViewById(R.id.collapsingBar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        // For profile
        iv_profile = view.findViewById(R.id.iv_profile);
        tv_fullName = view.findViewById(R.id.tv_fullName);
        iv_verified = view.findViewById(R.id.iv_verified);
        tv_no_of_posts = view.findViewById(R.id.tv_no_of_posts);
        profile_btn = view.findViewById(R.id.profile_btn);
        bio_card = view.findViewById(R.id.bio_card);
        tv_bio = view.findViewById(R.id.tv_bio);

        // For post list
        iv_post_list = view.findViewById(R.id.iv_post_list);
        rv_posts = view.findViewById(R.id.rv_posts);
        rv_posts.setHasFixedSize(true);
        rv_posts.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),postList);
        rv_posts.setAdapter(postAdapter);

        // For saved list
        iv_saved_list = view.findViewById(R.id.iv_saved_list);
        rv_saved = view.findViewById(R.id.rv_saved);
        rv_saved.setHasFixedSize(true);
        rv_saved.setLayoutManager(new LinearLayoutManager(getContext()));
        savedList = new ArrayList<>();
        savedAdapter = new PostAdapter(getContext(),savedList);
        rv_saved.setAdapter(savedAdapter);

        /* **************************************************************************** */

        // To get the user profile information
        getUserInfo();
        // To get the number of posts published by this user
        numOfPosts(view);
        // To get the number of posts published by this user
        checkVerifiedAccount();
        // To display all the posts published by this user
        getPosts();

        // When click on posts icon
        iv_post_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv_posts.setVisibility(View.VISIBLE);
                rv_saved.setVisibility(View.GONE);

                // By default will display posts
            }
        });

        // When click on saved icon
        iv_saved_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv_posts.setVisibility(View.GONE);
                rv_saved.setVisibility(View.VISIBLE);

                // To display all the posts saved by this user
                getSaved();
            }
        });

        // To set the text for the button (Edit Profile or Send Email)
        if(userId.equals(firebaseUser.getUid())) {
            // Set the button as (Edit Profile)
            profile_btn.setText(view.getContext().getString(R.string.profile_btn_edit));
        }
        else {
            // Set the button as (Send Email)
            profile_btn.setText(view.getContext().getString(R.string.profile_btn_send));
        }

        // When click on the profile button
        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = profile_btn.getText().toString();

                if(text.equals(view.getContext().getString(R.string.profile_btn_edit))) {

                    // The user will be redirected to the edit profile page
                    Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    startActivity(intent);
                }
                else {

                    // The user will be redirected to the Send email page
                    Intent intent = new Intent(getContext(), SendEmailActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    // To get the user profile information
    private void getUserInfo() {
        firebaseDatabase.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                // Profile picture
                if(user.getImage().equals("none")) {
                    iv_profile.setImageResource(R.drawable.ic_tmp_profile);
                }
                else {
                    Picasso.get().load(user.getImage()).into(iv_profile);
                }

                // Full name
                tv_fullName.setText(user.getFullname());

                // Bio
                if(user.getBio().equals("")) {
                    bio_card.setVisibility(View.GONE);
                }
                else {
                    bio_card.setVisibility(View.VISIBLE);
                    tv_bio.setText(user.getBio());
                }

                // Username
                String userName = user.getUsername();
                collapsingToolbar.setTitle(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // To get the number of posts published by this user
    private void numOfPosts(View view) {

        Query query = firebaseDatabase.child("Posts").orderByChild("publisher").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long numOfPosts = snapshot.getChildrenCount();

                tv_no_of_posts.setText(numOfPosts + " " + view.getContext().getString(R.string.post_label));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // To check if the account is verified or not, to set the blue star
    private void checkVerifiedAccount() {
        firebaseDatabase.child("Verified").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userId).exists()) {
                    iv_verified.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // To display all the posts published by this user
    private void getPosts() {
        Query query = firebaseDatabase.child("Posts").orderByChild("publisher").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                Collections.reverse(postList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // To display all the posts saved by this user
    private void getSaved() {

        final List<String> postSavedId = new ArrayList<>();

        firebaseDatabase.child("Favorites").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // To get the IDs for the saved posts
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    postSavedId.add(dataSnapshot.getKey());
                }

                firebaseDatabase.child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        savedList.clear();
                        for(DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                            Post post = dataSnapshot2.getValue(Post.class);

                            if(postSavedId.contains(post.getPostid())) {
                                savedList.add(post);
                            }

                        }
                        Collections.reverse(savedList);
                        savedAdapter.notifyDataSetChanged();
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