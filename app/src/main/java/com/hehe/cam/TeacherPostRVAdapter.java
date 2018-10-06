package com.hehe.cam;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class TeacherPostRVAdapter extends RecyclerView.Adapter<TeacherPostRVAdapter.ViewHolder> {

    Context context;
    private final RequestOptions options;
    List<HashMap<String, String>> hashMaps;

    public TeacherPostRVAdapter(Context context, List<HashMap<String, String>> hashMaps) {
        this.context = context;
        this.hashMaps = hashMaps;
        Log.d("hashsize: ", String.valueOf(hashMaps.size()));
        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        HashMap<String, String> hashMap = hashMaps.get(i);
        if (hashMap.get("imageurl") != null) {
            Glide.with(context)
                    .asBitmap()
                    .load(hashMap.get("imageurl"))
                    .apply(options)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(new RequestOptions()
                                    .placeholder(context.getResources().getDrawable(R.drawable.gradient))))
                    .transition(withCrossFade())
                    .into(viewHolder.imageView);
        } else {
            viewHolder.imageView.setVisibility(View.GONE);
        }
        if (hashMap.get("year") != null) viewHolder.year.setText(hashMap.get("year").toUpperCase());
        if (hashMap.get("branch") != null) {
            if (!hashMap.get("branch").equals("null"))
                viewHolder.branch.setText(hashMap.get("branch").toUpperCase());
        }
        viewHolder.type.setText(hashMap.get("type"));
        if (hashMap.get("filename") != null) viewHolder.filename.setText(hashMap.get("filename"));
        String date = hashMap.get("date");
        viewHolder.date.setText(String.format("%s/%s/%s", date.substring(6), date.substring(4, 6), date.substring(0, 4)));
    }

    @Override
    public int getItemCount() {
        return hashMaps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView date, year, branch, filename, type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            date = itemView.findViewById(R.id.date);
            year = itemView.findViewById(R.id.year);
            branch = itemView.findViewById(R.id.branch);
            filename = itemView.findViewById(R.id.fileName);
            type = itemView.findViewById(R.id.type);

        }
    }
}
