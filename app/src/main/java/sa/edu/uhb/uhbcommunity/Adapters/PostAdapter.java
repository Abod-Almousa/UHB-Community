package sa.edu.uhb.uhbcommunity.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sa.edu.uhb.uhbcommunity.CommentActivity;
import sa.edu.uhb.uhbcommunity.Model.Post;
import sa.edu.uhb.uhbcommunity.Model.User;
import sa.edu.uhb.uhbcommunity.R;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private Context context;
    private List<Post> posts;

    private FirebaseUser firebaseUser;
    private DatabaseReference firebaseDatabase;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post post = posts.get(position);

        // To set the details of the post

        // For the post picture
        if(post.getImage().equals("none")) {
            holder.iv_post_image.setVisibility(View.GONE);
        }
        else {
            holder.iv_post_image.setVisibility(View.VISIBLE);
            Picasso.get().load(post.getImage()).into(holder.iv_post_image);
        }

        // For the post text
        holder.tv_post_description.setText(post.getDescription());

        // For the post date
        holder.tv_date.setText(post.getDate());

        // End of setting the post details

        /* ******************************************************** */

        // To set the details of the publisher
        firebaseDatabase.child("Users").child(post.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // To get the publisher information
                        User user = snapshot.getValue(User.class);

                        // For the profile picture
                        if(user.getImage().equals("none")) {
                            holder.iv_profile.setImageResource(R.drawable.ic_tmp_profile);
                        }
                        else {
                            Picasso.get().load(user.getImage()).into(holder.iv_profile);
                        }

                        // For the publisher username
                        holder.tv_username.setText(user.getUsername());

                        // For the publisher full name
                        holder.tv_fullName.setText(user.getFullname());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // To set visibility of the option button
        // This button will be visible just for your posts to edit/delete the post
        if(post.getPublisher().equals(firebaseUser.getUid())) {
            holder.iv_more.setVisibility(View.VISIBLE);
        }
        else {
            holder.iv_more.setVisibility(View.INVISIBLE);
        }

        // When the user click on like button to like/unlike the post
        holder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likePost(post.getPostid(),holder.iv_like);
            }
        });

        // When the user click on save button to save/remove save
        holder.iv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePost(post.getPostid(),holder.iv_save);
            }
        });

        // When the user click on comment button, will be redirected to the comment page
        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);

                // This data will be sent to the comment activity to view/add comments for this post
                intent.putExtra("postId",post.getPostid());
                intent.putExtra("publisherId",post.getPublisher());
                context.startActivity(intent);
            }
        });

        // When the user click on the number of comments, will be redirected to the comment page
        holder.tv_no_of_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);

                // This data will be sent to the comment activity to view/add comments for this post
                intent.putExtra("postId",post.getPostid());
                intent.putExtra("publisherId",post.getPublisher());
                context.startActivity(intent);
            }
        });

        // To check if the account is verified by the admin or not, to set the blue star
        checkVerifiedAccount(post.getPublisher(),holder.iv_verified);

        // To check if the post is liked or not
        checkLike(post.getPostid(),holder.iv_like);

        // To check if the post is saved or not
        checkSave(post.getPostid(),holder.iv_save);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // When the user like the post
    private void likePost(String postid, ImageView iv_like) {

        // If the post is not liked, will like the post
        if(iv_like.getTag().equals("NotLiked")) {
            firebaseDatabase.child("Likes").child(postid).child(firebaseUser.getUid()).setValue(true);
        }
        else {
            // If the post is liked, will remove the like
            firebaseDatabase.child("Likes").child(postid).child(firebaseUser.getUid()).removeValue();
        }
    }

    // When the user save the post
    private void savePost(String postid, ImageView iv_save) {

        // If the post is not saved, will save the post
        if(iv_save.getTag().equals("NotSaved")) {
            firebaseDatabase.child("Favorites").child(firebaseUser.getUid()).child(postid).setValue(true);
        }
        else {
            // If the post is saved, will remove the save
            firebaseDatabase.child("Favorites").child(firebaseUser.getUid()).child(postid).removeValue();
        }
    }

    // To check if the account is verified or not, to set the blue star
    private void checkVerifiedAccount(String publisher, ImageView iv_verified) {

        firebaseDatabase.child("Verified").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(publisher).exists()) {
                    iv_verified.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // To check if the post is liked by the user or not
    private void checkLike(String postid, ImageView iv_like) {

        firebaseDatabase.child("Likes").child(postid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // The post is liked
                        if(snapshot.child(firebaseUser.getUid()).exists()) {
                            iv_like.setImageResource(R.drawable.ic_liked);
                        }
                        else {
                            iv_like.setImageResource(R.drawable.ic_like);
                            iv_like.setTag("NotLiked");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    // To check if the post is saved by the user or not
    private void checkSave(String postid, ImageView iv_save) {

        firebaseDatabase.child("Favorites").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // The post is saved
                        if(snapshot.child(postid).exists()) {
                            iv_save.setImageResource(R.drawable.ic_saved);
                        }
                        else {
                            iv_save.setImageResource(R.drawable.ic_save);
                            iv_save.setTag("NotSaved");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView iv_profile;
        public ImageView iv_more;
        public TextView tv_username;
        public TextView tv_fullName;
        public ImageView iv_verified;
        public TextView tv_date;
        public ImageView iv_post_image;
        public TextView tv_post_description;
        public TextView tv_no_of_likes;
        public TextView tv_no_of_comments;
        public ImageView iv_like;
        public ImageView iv_comment;
        public ImageView iv_save;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_profile = itemView.findViewById(R.id.iv_profile);
            iv_more = itemView.findViewById(R.id.iv_more);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_fullName = itemView.findViewById(R.id.tv_fullName);
            iv_verified = itemView.findViewById(R.id.iv_verified);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_post_image = itemView.findViewById(R.id.iv_post_image);
            tv_post_description = itemView.findViewById(R.id.tv_post_description);
            tv_no_of_likes = itemView.findViewById(R.id.tv_no_of_likes);
            tv_no_of_comments = itemView.findViewById(R.id.tv_no_of_comments);
            iv_like = itemView.findViewById(R.id.iv_like);
            iv_comment = itemView.findViewById(R.id.iv_comment);
            iv_save = itemView.findViewById(R.id.iv_save);
        }
    }
}