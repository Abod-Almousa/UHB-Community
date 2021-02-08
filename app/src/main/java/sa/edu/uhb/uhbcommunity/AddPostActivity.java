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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import org.angmarch.views.NiceSpinner;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddPostActivity extends AppCompatActivity {

    private TextView btn_cancel, btn_post;
    private EditText et_post_description;
    private ImageView iv_post_image;
    private Button btn_select_image, btn_remove_image;
    private NiceSpinner list_category;
    private TextView tv_category_error;

    private Uri imageUri;
    private String imageUrl;
    private String date;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        btn_cancel = findViewById(R.id.tv_cancel);
        btn_post = findViewById(R.id.tv_post);
        et_post_description = findViewById(R.id.et_post_description);
        iv_post_image = findViewById(R.id.iv_post_image);
        btn_select_image = findViewById(R.id.btn_select_image);
        btn_remove_image = findViewById(R.id.btn_remove_image);
        list_category = findViewById(R.id.list_category);
        tv_category_error = findViewById(R.id.tv_category_error);

        // When click on cancel button, redirected to the main activity
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddPostActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // When click on post button, upload the post
        btn_post.setOnClickListener(new View.OnClickListener() {
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
                    uploadPost();
                }
            }
        });

        /** When click on add picture button,
        allow the user to take a picture by the camera or select a picture from the gallery **/
        btn_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Using Android Image Cropper library
                CropImage.activity()
                        .setActivityTitle(getString(R.string.crop_image_title))
                        .setCropMenuCropButtonTitle(getString(R.string.crop_image_btn))
                        .start(AddPostActivity.this);
            }
        });

        // When click on remove picture button, will remove the selected picture
        btn_remove_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iv_post_image.setImageDrawable(null);

                btn_select_image.setVisibility(View.VISIBLE);
                btn_remove_image.setVisibility(View.GONE);
            }
        });

    }

    // To upload the post to the database
    private void uploadPost() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.uploading));
        dialog.show();

        // If the user select a picture
        if(imageUri != null) {
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

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = reference.push().getKey();

                    // Get the current date
                    date = getCurrentDate();

                    // Get the selected category
                    category = list_category.getSelectedItem().toString();

                    // To upload the post to the database
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("postid",postId);
                    map.put("description",et_post_description.getText().toString());
                    map.put("image",imageUrl);
                    map.put("date",date);
                    map.put("category",category);
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    reference.child(postId).setValue(map);

                    dialog.dismiss();
                    Intent intent = new Intent(AddPostActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
            String postId = reference.push().getKey();

            // Get the current date
            date = getCurrentDate();

            // Get the selected category
            category = list_category.getSelectedItem().toString();

            // To upload the post to the database
            HashMap<String,Object> map = new HashMap<>();
            map.put("postid",postId);
            map.put("description",et_post_description.getText().toString());
            map.put("image","none");
            map.put("date",date);
            map.put("category",category);
            map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

            reference.child(postId).setValue(map);

            dialog.dismiss();
            Intent intent = new Intent(AddPostActivity.this,MainActivity.class);
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
            Intent intent = new Intent(AddPostActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}