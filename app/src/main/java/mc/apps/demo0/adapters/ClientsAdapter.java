package mc.apps.demo0.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import mc.apps.demo0.R;
import mc.apps.demo0.model.Client;


public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ViewHolder> {

    private static final String TAG ="tests" ;
    private List<Client> items;
    private OnItemClickListener listener;

    public ClientsAdapter(List<Client> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Client client = items.get(position);
        holder.title.setText(client.getNom());
        holder.details.setText(client.getAdresse());
        if(listener!=null)
            holder.itemView.setOnClickListener(
                    view -> listener.onItemClick(position, items.get(position))
            );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title = itemView.findViewById(R.id.item_title);
        TextView details = itemView.findViewById(R.id.item_details);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
