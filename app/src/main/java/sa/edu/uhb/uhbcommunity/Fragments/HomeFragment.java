package sa.edu.uhb.uhbcommunity.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
import sa.edu.uhb.uhbcommunity.DrawerMenu.AboutUsActivity;
import sa.edu.uhb.uhbcommunity.DrawerMenu.ContactActivity;
import sa.edu.uhb.uhbcommunity.DrawerMenu.LinksActivity;
import sa.edu.uhb.uhbcommunity.DrawerMenu.SettingsActivity;
import sa.edu.uhb.uhbcommunity.Model.Post;
import sa.edu.uhb.uhbcommunity.R;
import sa.edu.uhb.uhbcommunity.StartActivity;

public class HomeFragment extends Fragment {

    private RecyclerView rv_posts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private NiceSpinner list_category;
    private String category = "Questions";

    private LottieAnimationView anim_page_loading;

    // Drawer navigation layout
    private DrawerLayout drawer_layout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private NavigationView nav_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /* Drawer navigation layout */
        drawer_layout = view.findViewById(R.id.drawer_layout);
        toolbar = view.findViewById(R.id.toolbar);
        nav_view = view.findViewById(R.id.nav_view);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.setTitle("");
        toggle = new ActionBarDrawerToggle(getActivity(),drawer_layout,toolbar,R.string.open,R.string.close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        // To set the visibility of the control panel
        /* Menu menu = nav_view.getMenu();
        menu.findItem(R.id.panel).setVisible(false); */

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.links:
                        Intent links = new Intent(getContext(), LinksActivity.class);
                        startActivity(links);
                        break;

                    case R.id.contact:
                        Intent contact = new Intent(getContext(), ContactActivity.class);
                        startActivity(contact);
                        break;

                    case R.id.about:
                        Intent about = new Intent(getContext(), AboutUsActivity.class);
                        startActivity(about);
                        break;

                    case R.id.settings:
                        Intent settings = new Intent(getContext(), SettingsActivity.class);
                        startActivity(settings);
                        break;

                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getContext(), StartActivity.class);
                        startActivity(intent);
                        activity.finish();
                        break;
                }
                return false;
            }
        });
        /* End of Drawer navigation layout */

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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.drawer_menu, menu);
    }

    @Override // drawer button
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}