package sa.edu.uhb.uhbcommunity.Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sa.edu.uhb.uhbcommunity.Adapters.PostAdapter;
import sa.edu.uhb.uhbcommunity.Adapters.UserAdapter;
import sa.edu.uhb.uhbcommunity.Model.Post;
import sa.edu.uhb.uhbcommunity.Model.User;
import sa.edu.uhb.uhbcommunity.R;


public class SearchFragment extends Fragment {

    // For the users
    private RecyclerView rv_users;
    private List<User> userList;
    private UserAdapter userAdapter;

    // For the posts
    private RecyclerView rv_posts;
    private List<Post> postList;
    private PostAdapter postAdapter;

    // For Search view
    private SearchView search_bar;

    // For the animations
    private LottieAnimationView anim_search;
    private LottieAnimationView anim_no_result;

    // For radio buttons
    private RadioGroup rg_search;
    private RadioButton rb_search_user;
    private RadioButton rb_search_post;

    // Firebase
    private DatabaseReference firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // For the animations
        anim_search = view.findViewById(R.id.anim_search);
        anim_no_result = view.findViewById(R.id.anim_no_result);

        // Firebase
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // For radio buttons
        rg_search = view.findViewById(R.id.rg_search);
        rb_search_user = view.findViewById(R.id.rb_search_user);
        rb_search_post = view.findViewById(R.id.rb_search_post);

        // For the users
        rv_users = view.findViewById(R.id.rv_users);
        rv_users.setHasFixedSize(true);
        rv_users.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(),userList,true);
        rv_users.setAdapter(userAdapter);

        // For the posts
        rv_posts = view.findViewById(R.id.rv_posts);
        rv_posts.setHasFixedSize(true);
        rv_posts.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),postList,true);
        rv_posts.setAdapter(postAdapter);

        // Default visibility is (User)
        rv_users.setVisibility(View.VISIBLE);
        rv_posts.setVisibility(View.GONE);
        anim_search.setVisibility(View.VISIBLE);
        anim_search.playAnimation();

        // To allow the user to search for a user or for a post
        rg_search.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {

                    case R.id.rb_search_user:
                        rv_users.setVisibility(View.VISIBLE);
                        rv_posts.setVisibility(View.GONE);
                        break;

                    case R.id.rb_search_post:
                        rv_posts.setVisibility(View.VISIBLE);
                        rv_users.setVisibility(View.GONE);
                        break;
                }
            }
        });

        // For Search view
        search_bar = view.findViewById(R.id.search_bar);
        search_bar.setMaxWidth(Integer.MAX_VALUE);
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(query.isEmpty()) {
                    userList.clear();
                    postList.clear();
                }

                searchUser(query.toLowerCase());
                searchPost(query.toLowerCase());

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.isEmpty()) {
                    userList.clear();
                    postList.clear();
                }

                searchUser(newText.toLowerCase());
                searchPost(newText.toLowerCase());

                return true;
            }
        });

        search_bar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                userList.clear();
                postList.clear();

                anim_search.setVisibility(View.VISIBLE);
                anim_search.playAnimation();

                return false;
            }
        });

        return view;
    }

    // To search for a user by (Username or full name)
    private void searchUser(String s) {

        anim_search.cancelAnimation();
        anim_search.setVisibility(View.GONE);

        firebaseDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    if(user.getUsername().toLowerCase().contains(s) || user.getFullname().toLowerCase().contains(s)){
                        userList.add(user);
                    }
                }
                if(s.isEmpty()) {
                    userList.clear();
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // To search for a post by (keyword)
    private void searchPost(String s) {

        anim_search.cancelAnimation();
        anim_search.setVisibility(View.GONE);

        firebaseDatabase.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if(post.getDescription().toLowerCase().contains(s)){
                        postList.add(post);
                    }
                }
                if(s.isEmpty()) {
                    postList.clear();
                }
                Collections.reverse(postList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}