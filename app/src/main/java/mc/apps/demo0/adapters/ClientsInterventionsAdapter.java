package mc.apps.demo0.adapters;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mc.apps.demo0.InterventionActivity;
import mc.apps.demo0.R;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.ClientIntervention;
import mc.apps.demo0.model.Intervention;

public class ClientsInterventionsAdapter extends RecyclerView.Adapter<ClientsInterventionsAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "tests";
    private List<ClientIntervention> items;
    private OnItemClickListener listener;

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence searchText)
            {
                FilterResults results = new FilterResults();
                if(searchText == null || searchText.length() == 0)
                {
                    results.values = items;
                    results.count = items.size();
                }
                else
                {
                    List<ClientIntervention> filterResultsData = new ArrayList<ClientIntervention>();
                    for(ClientIntervention item : items)
                        if(item.getClient().getNom().toLowerCase().contains(searchText.toString().toLowerCase())) {
                            if(!filterResultsData.contains(item))
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
                items = (List<ClientIntervention>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public ClientsInterventionsAdapter(List<ClientIntervention> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_client_intervs, parent, false);
        return new ViewHolder(itemView);
    }

    InterventionsAdapter adapter;
    //List<Intervention> interventions;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClientIntervention item = items.get(position);
        Log.i(TAG , "onBindViewHolder - ClientIntervention : "+item);

        Client client = item.getClient();
        holder.title.setText(client.getNom()+" ["+client.getCode()+"]");


        InterventionDao dao = new InterventionDao();
        dao.list((data, message) -> {
            List<Intervention> interventions = dao.Deserialize(data, Intervention.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                interventions = interventions.stream()
                        .filter(i->i.getClientId().equals(client.getCode()))
                        .filter(i->i.getStatutId()==1)
                        .sorted((o1, o2)->o1.getDateDebutPrevue().compareTo(o2.getDateDebutPrevue()))
                        .collect(Collectors.toList());
            }
            Log.i(TAG , "onBindViewHolder - interventions : "+interventions.size());

            adapter = new InterventionsAdapter(
                    interventions,
                    (pos, intervention) -> {
                        listener.onItemClick(pos, intervention);
                    }
            );
            holder.sublist.setAdapter(adapter);
        });
    }

    @Override
    public int getItemCount() {
        Log.i(TAG , "getItemCount: "+items.size());
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title = itemView.findViewById(R.id.item_title);
        RecyclerView sublist = itemView.findViewById(R.id.item_sublist);
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            InitSubList();
            /*itemView.setOnClickListener(view -> {
                listener.onItemClick(getAdapterPosition(), items.get(getAdapterPosition()));
            });*/
        }

        private void InitSubList() {
            sublist.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            sublist.setLayoutManager(layoutManager);
            sublist.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                    itemView.getContext(), R.anim.layout_fall_down_animation
            ));
        }
    }
}

