package mc.apps.demo0.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mc.apps.demo0.R;
import mc.apps.demo0.model.Intervention;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private static final String TAG ="tests" ;
    private List<Intervention> items;
    private OnItemClickListener listener;

    public ItemsAdapter(List<Intervention> items, OnItemClickListener listener) {
        this.items = items;
        Log.i(TAG, "ItemsAdapter: "+items);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, int position) {
        holder.title.setText(items.get(position).getDescription());
        holder.itemView.setOnClickListener(
                view -> listener.onItemClick(position, items.get(position))
        );
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: "+items.size());
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title = itemView.findViewById(R.id.item_title);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
