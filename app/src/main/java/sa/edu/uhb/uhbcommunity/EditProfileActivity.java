package sa.edu.uhb.uhbcommunity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import sa.edu.uhb.uhbcommunity.Model.User;

public class EditProfileActivity extends AppCompatActivity {

    private TextView tv_cancel;
    private ImageView iv_profile;
    private TextView tv_change_image;
    private EditText et_name;
    private MaterialEditText et_bio;
    private Button btn_apply;

    private Uri imageUri;
    private String keepImage;

    // Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference firebaseDatabase;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        tv_cancel = findViewById(R.id.tv_cancel);
        iv_profile = findViewById(R.id.iv_profile);
        tv_change_image = findViewById(R.id.tv_change_image);
        et_name = findViewById(R.id.et_name);
        et_bio = findViewById(R.id.et_bio);
        btn_apply = findViewById(R.id.btn_apply);

        // Firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference().child("ProfilePicture");

        // To display the profile information
        profileInfo();

        // When the user click on Cancel button
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // When the user click on change photo to change the profile picture
        tv_change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setActivityTitle(getString(R.string.crop_image_title))
                        .setCropMenuCropButtonTitle(getString(R.string.crop_image_btn))
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1,1) // To set the shape as circle
                        .start(EditProfileActivity.this);
            }
        });

        // When the user click on apply button to save the changes
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
                finish();
            }
        });
    }

    // To display the profile information
    private void profileInfo() {
        firebaseDatabase.child("Users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        User user = snapshot.getValue(User.class);

                        et_name.setText(user.getFullname());
                        et_bio.setText(user.getBio());

                        if(user.getImage().equals("none")) {
                            iv_profile.setImageResource(R.drawable.ic_tmp_profile);
                        }
                        else {
                            Picasso.get().load(user.getImage()).into(iv_profile);
                            keepImage = user.getImage();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    // To update the profile information
    private void updateProfile() {
        String name = et_name.getText().toString();
        String bio = et_bio.getText().toString();

        HashMap<String,Object> map = new HashMap<>();
        map.put("fullname",name);
        map.put("bio",bio);

        firebaseDatabase.child("Users").child(firebaseUser.getUid()).updateChildren(map);
    }

    // To update the profile picture
    private void updateImage() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.uploading));
        dialog.show();

        if(imageUri != null) {

            // To delete the previous image if there
            if(!(keepImage == null)) {
                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(keepImage);
                ref.delete();
            }

            final StorageReference fileRef = storageRef.child(System.currentTimeMillis() +".jpeg");

            StorageTask uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful()) {
                        task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();

                        // Upload the image to the firebase
                        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid())
                                .child("image").setValue(url);
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            updateImage();
        }
        else {
            Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }
    }
}