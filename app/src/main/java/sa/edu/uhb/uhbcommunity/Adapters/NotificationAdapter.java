package sa.edu.uhb.uhbcommunity.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import sa.edu.uhb.uhbcommunity.EditPostActivity;
import sa.edu.uhb.uhbcommunity.Model.Notification;
import sa.edu.uhb.uhbcommunity.Model.User;
import sa.edu.uhb.uhbcommunity.R;
import sa.edu.uhb.uhbcommunity.ViewNotifiedPostActivity;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context context;
    private List<Notification> notifications;

    private DatabaseReference firebaseDatabase;
    private FirebaseUser firebaseUser;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Firebase
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Notification notification = notifications.get(position);

        /* Get the notification info */

        // To get the notification text
        if(notification.getText().equals("like")) {
            holder.tv_text.setText(context.getResources().getString(R.string.notification_like));
        }
        else {
            holder.tv_text.setText(context.getResources().getString(R.string.notification_comment));
        }

        // To get the date and time of the notification
        holder.tv_date.setText(notification.getDate());
        holder.tv_time.setText(notification.getTime());

        // To check if the notification is seen or not
        if(notification.getSeen().equals(true)) {
            holder.notification_card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        }
        else {
            holder.notification_card.setCardBackgroundColor(context.getResources().getColor(R.color.not_seen_color));
        }

        /* End of notification info */

        // To get the user info
        firebaseDatabase.child("Users").child(notification.getFrom())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                // Profile picture
                if(user.getImage().equals("none")) {
                    holder.iv_profile.setImageResource(R.drawable.ic_tmp_profile);
                }
                else {
                    Picasso.get().load(user.getImage()).into(holder.iv_profile);
                }

                // Username
                holder.tv_username.setText(user.getUsername());

                // Full name
                holder.tv_fullName.setText(user.getFullname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /* Notification comes from post adapter when the user like post
           Or comes from comment activity when the user comment on post */

        // When the user click on the notification item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // To set the notification as seen
                firebaseDatabase.child("Notifications").child(firebaseUser.getUid())
                        .child(notification.getNotificationId()).child("seen").setValue(true);

                // The user will be redirected to the view post page
                Intent intent = new Intent(context, ViewNotifiedPostActivity.class);
                // To send the post information to the view post page
                intent.putExtra("postId",notification.getPostId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView notification_card;
        public CircleImageView iv_profile;
        public TextView tv_username;
        public TextView tv_fullName;
        public TextView tv_date;
        public TextView tv_time;
        public TextView tv_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notification_card = itemView.findViewById(R.id.notification_card);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_fullName = itemView.findViewById(R.id.tv_fullName);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_text = itemView.findViewById(R.id.tv_text);
        }
    }
}
