package sa.edu.uhb.uhbcommunity.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

import sa.edu.uhb.uhbcommunity.Adapters.PostAdapter;
import sa.edu.uhb.uhbcommunity.Model.Post;
import sa.edu.uhb.uhbcommunity.R;

public class HomeFragment extends Fragment {

    private RecyclerView rv_posts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private NiceSpinner list_category;
    private String category = "Questions";

    private LottieAnimationView anim_page_loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // To store the posts in this list
        postList = new ArrayList<>();

        // Using the post adapter
        postAdapter = new PostAdapter(getContext(),postList);

        // For the lottie animation
        anim_page_loading = view.findViewById(R.id.anim_page_loading);

        // For the Post Recycler View
        rv_posts = view.findViewById(R.id.rv_posts);
        rv_posts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true); // To display the latest posts at the top
        rv_posts.setLayoutManager(linearLayoutManager);
        rv_posts.setAdapter(postAdapter);

        // To display the Questions category first
        anim_page_loading.setVisibility(View.VISIBLE);
        anim_page_loading.playAnimation();
        getPosts(category,linearLayoutManager);

        // To get the selected category
        list_category = view.findViewById(R.id.list_category);
        list_category.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {

                switch (position) {

                    case 1:
                        rv_posts.setVisibility(View.GONE);
                        anim_page_loading.setVisibility(View.VISIBLE);
                        anim_page_loading.playAnimation();
                        category = "Questions";
                        getPosts(category,linearLayoutManager);
                        break;

                    case 2:
                        rv_posts.setVisibility(View.GONE);
                        anim_page_loading.setVisibility(View.VISIBLE);
                        anim_page_loading.playAnimation();
                        category = "Advices";
                        getPosts(category, linearLayoutManager);
                        break;

                    case 3:
                        rv_posts.setVisibility(View.GONE);
                        anim_page_loading.setVisibility(View.VISIBLE);
                        anim_page_loading.playAnimation();
                        category = "News";
                        getPosts(category, linearLayoutManager);
                        break;
                }
            }
        });

        return view;
    }

    private void getPosts(String category, LinearLayoutManager linearLayoutManager) {

        // This query to only get and display the posts for the selected category
        Query query = FirebaseDatabase.getInstance().getReference().child("Posts")
                .orderByChild("category").equalTo(category);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postList.clear();

                // To retrieve and store all the posts for this category
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }

                // To stop the loading anim
                anim_page_loading.cancelAnimation();
                anim_page_loading.setVisibility(View.GONE);

                rv_posts.setVisibility(View.VISIBLE);
                postAdapter.notifyDataSetChanged();
                linearLayoutManager.scrollToPosition(postAdapter.getItemCount()-1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}