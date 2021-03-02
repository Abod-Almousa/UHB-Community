package sa.edu.uhb.uhbcommunity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.angmarch.views.NiceSpinner;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

import sa.edu.uhb.uhbcommunity.Model.Post;

public class EditPostActivity extends AppCompatActivity {

    private TextView btn_cancel;
    private Button btn_apply_changes;
    private EditText et_post_description;
    private ImageView iv_post_image;
    private ImageView btn_select_image, btn_remove_image;
    private NiceSpinner list_category;
    private TextView tv_category_error;

    private Uri imageUri;
    private String imageUrl;
    private String date;
    private String category;
    private String postId;

    private String keepImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        btn_cancel = findViewById(R.id.tv_cancel);
        btn_apply_changes = findViewById(R.id.btn_apply);
        et_post_description = findViewById(R.id.et_post_description);
        iv_post_image = findViewById(R.id.iv_post_image);
        btn_select_image = findViewById(R.id.btn_select_image);
        btn_remove_image = findViewById(R.id.btn_remove_image);
        list_category = findViewById(R.id.list_category);
        tv_category_error = findViewById(R.id.tv_category_error);

        // We got this data from the post adapter
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        // To display the post information
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post = snapshot.getValue(Post.class);

                        if(!post.getImage().equals("none")) {
                            Picasso.get().load(post.getImage()).into(iv_post_image);
                            keepImage = post.getImage();

                            btn_select_image.setVisibility(View.GONE);
                            btn_remove_image.setVisibility(View.VISIBLE);
                        }

                        et_post_description.setText(post.getDescription());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // When click on cancel button, redirected to the main activity
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPostActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /* When click on add picture button,
         allow the user to take a picture by the camera or select a picture from the gallery */
        btn_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Using Android Image Cropper library
                CropImage.activity()
                        .setActivityTitle(getString(R.string.crop_image_title))
                        .setCropMenuCropButtonTitle(getString(R.string.crop_image_btn))
                        .start(EditPostActivity.this);
            }
        });

        // To set the visibility of select/remove image
        iv_post_image.setTag("notRemoved");
        if(iv_post_image.getTag().equals("notRemoved")) {
            btn_select_image.setVisibility(View.VISIBLE);
            btn_remove_image.setVisibility(View.GONE);
        }

        // When click on remove picture button, will remove the selected picture
        btn_remove_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iv_post_image.setImageDrawable(null);

                // To set the visibility of select/remove image
                iv_post_image.setTag("removed");
                btn_select_image.setVisibility(View.VISIBLE);
                btn_remove_image.setVisibility(View.GONE);
            }
        });

        // When click on post button, upload the post
        btn_apply_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // To validate the description edit text and the category list

                if(TextUtils.isEmpty(et_post_description.getText().toString())) {
                    et_post_description.setError(getString(R.string.required_filed));
                }
                else if(list_category.getSelectedItem().equals("Select Category")
                        || list_category.getSelectedItem().equals("إختر فئة")) {
                    tv_category_error.setVisibility(View.VISIBLE);
                }
                else {
                    updatePost();
                }
            }
        });
    }

    // To update the post
    private void updatePost() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.uploading));
        dialog.show();

        // If the user select a picture
        if(imageUri != null) {

            // To delete the previous image if there
            if(!(keepImage == null)) {
                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(keepImage);
                ref.delete();
            }

            final StorageReference filePath = FirebaseStorage.getInstance().getReference("PostPicture")
                    .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            StorageTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();

                    // Get the current date
                    date = getCurrentDate();

                    // Get the selected category
                    category = list_category.getSelectedItem().toString();

                    // To update the post to the database
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("postid",postId);
                    map.put("description",et_post_description.getText().toString());
                    map.put("image",imageUrl);
                    map.put("date",date);
                    map.put("category",category);
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    FirebaseDatabase.getInstance().getReference("Posts").child(postId).updateChildren(map);

                    dialog.dismiss();
                    Intent intent = new Intent(EditPostActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {

            String image;

            // The post already has an image but the user removed it
            if(!(keepImage == null) && iv_post_image.getTag().equals("removed")) {
                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(keepImage);
                ref.delete();
                image = "none";
            }

            // The post already has an image and the user did not changed/removed it
            else if(!(keepImage == null)) {
                image = keepImage;
            }
            // The post already does not have an image and the user did not add an image
            else {
                image = "none";
            }

            // Get the current date
            date = getCurrentDate();

            // Get the selected category
            category = list_category.getSelectedItem().toString();

            // To upload the post to the database
            HashMap<String,Object> map = new HashMap<>();
            map.put("postid",postId);
            map.put("description",et_post_description.getText().toString());
            map.put("image",image);
            map.put("date",date);
            map.put("category",category);
            map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

            FirebaseDatabase.getInstance().getReference("Posts").child(postId).updateChildren(map);

            dialog.dismiss();
            Intent intent = new Intent(EditPostActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // TO get the extension of the picture
    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // TO get the current date of the post
    private String getCurrentDate() {

        Calendar calendar = Calendar.getInstance();
        String date = DateFormat.getDateInstance().format(calendar.getTime());

        return date;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            iv_post_image.setImageURI(imageUri);

            btn_select_image.setVisibility(View.GONE);
            btn_remove_image.setVisibility(View.VISIBLE);
        }
        else {
            Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditPostActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}