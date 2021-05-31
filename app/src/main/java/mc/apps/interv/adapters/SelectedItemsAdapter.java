package mc.apps.interv.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mc.apps.interv.R;

public class SelectedItemsAdapter<T> extends RecyclerView.Adapter<SelectedItemsAdapter<T>.ViewHolder> {
    private static final String TAG ="tests" ;
    private List<T> items;
    private OnItemClickListener listener;

    private boolean withDelete;
    public SelectedItemsAdapter(List<T> items, OnItemClickListener listener, boolean withDelete) {
        this.items = items;
        this.listener = listener;
        this.withDelete = withDelete;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.item_layout_simple ;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T item = items.get(position);
        holder.title.setText(item+"");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void refresh(List<T> selected) {
        items = selected;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title = itemView.findViewById(R.id.item_title);
        ImageButton deleteButton = itemView.findViewById(R.id.item_selected_delete);

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            if(withDelete) {
                deleteButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    T u = items.get(position);
                    items.remove(u);
                    notifyDataSetChanged();
                });
            }else{
                deleteButton.setVisibility(View.GONE);
            }
        }
    }
}
