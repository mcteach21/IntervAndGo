package mc.apps.demo0.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import mc.apps.demo0.R;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.User;


public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ViewHolder> implements Filterable {

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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_client, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Client client = items.get(position);

        holder.title.setText(client.getNom()+" ["+client.getCode()+"]");
        holder.details.setText(client.getEmail()+" - Tél. : "+client.getTelephone());
        holder.details_more.setText("Contact : "+client.getContact());

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
        TextView details_more = itemView.findViewById(R.id.item_details_more);

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            itemView.setOnClickListener(view -> {
                listener.onItemClick(getAdapterPosition(), items.get(getAdapterPosition()));
            });
        }
    }

    /**
     * Filtrer (après Recherche dans Liste)
     * @return
     */
    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence searchText)
            {
                FilterResults results = new FilterResults();
                //If there's nothing to filter on, return the original data for your list
                if(searchText == null || searchText.length() == 0)
                {
                    results.values = items;
                    results.count = items.size();
                }
                else
                {
                    List<Client> filterResultsData = new ArrayList<Client>();
                    for(Client item : items)
                    {
                        if(item.getNom().toLowerCase().contains(searchText.toString().toLowerCase()))
                            filterResultsData.add(item);
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                items = (List<Client>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
