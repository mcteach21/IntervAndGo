package mc.apps.demo0.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mc.apps.demo0.R;
import mc.apps.demo0.model.Intervention;

public class InterventionsAdapter extends RecyclerView.Adapter<InterventionsAdapter.ViewHolder> implements Filterable {

    private static final String TAG ="tests" ;
    private List<Intervention> items;
    private OnItemClickListener listener;

    public InterventionsAdapter(List<Intervention> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InterventionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InterventionsAdapter.ViewHolder holder, int position) {

        Intervention interv = items.get(position);
        holder.title.setText(interv.getDescription());

        SimpleDateFormat enDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat frDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        Date date = null;

        Date datejour = new Date();

        try {
            date = enDateFormat.parse(interv.getDateDebutPrevue());

            holder.details.setText(frDateFormat.format(date));
            holder.details.setTextColor(date.before(datejour)? Color.RED:Color.parseColor("#FFA000"));
        } catch (ParseException e) {}

        if(listener!=null)
            holder.itemView.setOnClickListener(
                    view -> listener.onItemClick(position, items.get(position))
            );
    }

    @Override
    public int getItemCount() {
        return items.size();
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
                    List<Intervention> filterResultsData = new ArrayList<Intervention>();
                    for(Intervention item : items)
                    {
                        if(item.getDescription().contains(searchText))
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
                items = (List<Intervention>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title = itemView.findViewById(R.id.item_title);
        TextView details = itemView.findViewById(R.id.item_details);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
