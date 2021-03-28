package sa.edu.uhb.uhbcommunity.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
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
import sa.edu.uhb.uhbcommunity.Fragments.ProfileFragment;
import sa.edu.uhb.uhbcommunity.MainActivity;
import sa.edu.uhb.uhbcommunity.Model.User;
import sa.edu.uhb.uhbcommunity.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<User> users;
    private Boolean isFragment;

    private DatabaseReference firebaseDatabase;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<User> users, Boolean isFragment) {
        this.context = context;
        this.users = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Initialize the post options menu
        PopupMenu menu = new PopupMenu(context,holder.iv_more);
        menu.getMenuInflater().inflate(R.menu.user_option_menu,menu.getMenu());

        // Firebase
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        User user = users.get(position);

        /* To get user information */

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

        firebaseDatabase.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);

                if(!currentUser.getRole().equals("user" ) && !user.getRole().equals("admin")) {
                    holder.iv_more.setVisibility(View.VISIBLE);
                    if(currentUser.getRole().equals("moderator")) {
                        menu.getMenu().findItem(R.id.add_moderator).setVisible(false);
                        menu.getMenu().findItem(R.id.delete_moderator).setVisible(false);
                    }
                }
                else {
                    holder.iv_more.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(user.getRole().equals("user")) {
            menu.getMenu().findItem(R.id.delete_moderator).setVisible(false);
        }
        else if (user.getRole().equals("moderator")) {
            menu.getMenu().findItem(R.id.add_moderator).setVisible(false);
        }
        else {
            holder.iv_more.setVisibility(View.INVISIBLE);
        }

        // When click on the user to open his profile
        holder.iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // The user comes from search fragment
                if(isFragment) {
                    MainActivity.bottomNavigation.setItemSelected(R.id.nav_profile,true);

                    context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit()
                            .putString("userId",user.getId()).commit();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
                // The user comes from like activity
                else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("userId",user.getId());
                    context.startActivity(intent);
                }
            }
        });

        holder.iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.verify_account:
                                verifyAccount(user.getId());
                                break;

                            case R.id.add_moderator:
                                addModerator(user.getId());
                                break;

                            case R.id.delete_moderator:
                                deleteModerator(user.getId());
                                break;
                        }
                        return true;
                    }
                });
                menu.show();
            }
        });

        // To check if the account is verified by the admin or not, to set the blue star
        checkVerifiedAccount(user.getId(),holder.iv_verified,menu);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // To check if the account is verified by the admin or not, to set the blue star
    private void checkVerifiedAccount(String id, ImageView iv_verified, PopupMenu menu) {

        firebaseDatabase.child("Verified").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(id).exists()) {
                    iv_verified.setVisibility(View.VISIBLE);
                    menu.getMenu().findItem(R.id.verify_account).setVisible(false);
                }
                else {
                    iv_verified.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // To verify the user account
    private void verifyAccount(String id) {
        firebaseDatabase.child("Verified").child(id).setValue(true);
        Toast.makeText(context, context.getResources().getString(R.string.toast_verified), Toast.LENGTH_SHORT).show();
    }

    // To make this user as a moderator
    private void addModerator(String id) {
        firebaseDatabase.child("Users").child(id).child("role").setValue("moderator");
        Toast.makeText(context, context.getResources().getString(R.string.toast_add_moderator), Toast.LENGTH_SHORT).show();
    }

    // To delete this moderator
    private void deleteModerator(String id) {
        firebaseDatabase.child("Users").child(id).child("role").setValue("user");
        Toast.makeText(context, context.getResources().getString(R.string.toast_delete_moderator), Toast.LENGTH_SHORT).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView iv_profile;
        public TextView tv_username;
        public TextView tv_fullName;
        public ImageView iv_verified;
        public ImageView iv_more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_profile= itemView.findViewById(R.id.iv_profile);
            tv_username= itemView.findViewById(R.id.tv_username);
            tv_fullName= itemView.findViewById(R.id.tv_fullName);
            iv_verified= itemView.findViewById(R.id.iv_verified);
            iv_more= itemView.findViewById(R.id.iv_more);
        }
    }
}
