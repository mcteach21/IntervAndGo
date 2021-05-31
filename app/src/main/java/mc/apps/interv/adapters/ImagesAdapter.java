package mc.apps.interv.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mc.apps.interv.R;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    private static final String TAG ="tests" ;
    private List<Uri> items;
    private OnItemClickListener listener;

    private boolean select = false;
    public ImagesAdapter(List<Uri> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public List<Uri> getItems() {
        return items;
    }
    public void clearItems() {
        items = new ArrayList();
        notifyDataSetChanged();
    }

    /*    private MainViewModel mainViewModel;
    public ImagesAdapter(List<String> items, OnItemClickListener listener, MainViewModel mainViewModel) {
        this(items, listener);
        this.select = select;
        this.mainViewModel = mainViewModel;
    }*/

    //Drawable item_logo;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.item_image_layout;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, int position) {
        Uri image = items.get(position);
        holder.img.setImageURI(image);
        //holder.img.setRotation(90);
        if (listener != null)
            holder.itemView.setOnClickListener(
                    view -> listener.onItemClick(position, items.get(position))
            );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void refresh(List<Uri> images) {
        items = images;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img = itemView.findViewById(R.id.item_img);
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
