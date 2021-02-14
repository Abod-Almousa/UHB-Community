package sa.edu.uhb.uhbcommunity.Adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import sa.edu.uhb.uhbcommunity.R;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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