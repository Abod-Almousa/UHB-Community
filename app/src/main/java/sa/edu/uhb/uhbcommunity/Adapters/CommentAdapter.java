package sa.edu.uhb.uhbcommunity.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import sa.edu.uhb.uhbcommunity.Model.Comment;
import sa.edu.uhb.uhbcommunity.Model.User;
import sa.edu.uhb.uhbcommunity.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private Context context;
    private List<Comment> comments;
    private String postId;

    private FirebaseUser firebaseUser;
    private DatabaseReference firebaseDatabase;

    public CommentAdapter(Context context, List<Comment> comments, String postId) {
        this.context = context;
        this.comments = comments;
        this.postId = postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        Comment comment = comments.get(position);

        /* To set the details of the comment */

        // For the comment text
        holder.tv_comment.setText(comment.getComment());

        // For the date and time
        holder.tv_date.setText(comment.getDate());
        holder.tv_time.setText(comment.getTime());

        /* End of setting the comment details */

        // To set the details of the publisher
        firebaseDatabase.child("Users").child(comment.getPublisher())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // To get the publisher information
                User user =snapshot.getValue(User.class);

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

        // When the user long click on the comment to delete it
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(comment.getPublisher().equals(firebaseUser.getUid())) {
                    AlertDialog dialog = new AlertDialog.Builder(context).create();
                    dialog.setTitle(String.valueOf(R.string.delete_comment));

                    // Button "Yes" to delete the comment
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, String.valueOf(R.string.delete_comment_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteComment(comment.getCommentid());
                            dialogInterface.dismiss();
                        }
                    });

                    // Button "Cancel" to cancel the alert
                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, String.valueOf(R.string.delete_comment_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                }
                return true;
            }
        });
    }

    // To delete the comment
    private void deleteComment(String commentid) {

        firebaseDatabase.child("Comments").child(postId).child(commentid).removeValue();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView iv_profile;
        public TextView tv_username;
        public TextView tv_fullName;
        public TextView tv_date;
        public TextView tv_time;
        public TextView tv_comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_fullName = itemView.findViewById(R.id.tv_fullName);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_comment = itemView.findViewById(R.id.tv_comment);
        }
    }
}
