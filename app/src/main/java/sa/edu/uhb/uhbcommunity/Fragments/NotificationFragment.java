package sa.edu.uhb.uhbcommunity.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sa.edu.uhb.uhbcommunity.Adapters.NotificationAdapter;
import sa.edu.uhb.uhbcommunity.Model.Notification;
import sa.edu.uhb.uhbcommunity.R;


public class NotificationFragment extends Fragment {

    private RecyclerView rv_notifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    // Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // For the notification RecyclerView
        rv_notifications = view.findViewById(R.id.rv_notifications);
        rv_notifications.setHasFixedSize(true);
        rv_notifications.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(),notificationList);
        rv_notifications.setAdapter(notificationAdapter);

        // To get all the notifications for this user
        getNotifications();

        return view;
    }

    // To get all the notifications for this user
    private void getNotifications() {

        firebaseDatabase.child("Notifications").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        notificationList.clear();

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            Notification notification = dataSnapshot.getValue(Notification.class);
                            notificationList.add(notification);
                        }

                        // To view the latest notifications first
                        Collections.reverse(notificationList);

                        notificationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}