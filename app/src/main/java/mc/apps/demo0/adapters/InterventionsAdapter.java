package mc.apps.demo0.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import mc.apps.demo0.R;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.Intervention;

public class InterventionsAdapter extends RecyclerView.Adapter<InterventionsAdapter.ViewHolder> implements Filterable {
    private static final String TAG ="tests" ;
    private List<Intervention> items;
    private OnItemClickListener listener;
    private Context context;
    private boolean details=false;

    public InterventionsAdapter(List<Intervention> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }
    public InterventionsAdapter(List<Intervention> items, OnItemClickListener listener, boolean details) {
       this(items, listener);
       this.details = details;
    }

    @NonNull
    @Override
    public InterventionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layout = R.layout.item_layout_details; //this.details?R.layout.item_layout_details:R.layout.item_layout;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        context = parent.getContext();
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull InterventionsAdapter.ViewHolder holder, int position) {
        String[] status =  context.getResources().getStringArray( R.array.statuts);
        Intervention interv = items.get(position);


        Date date = MyTools.getDateOfString(interv.getDateDebutPrevue()); //MySQL Date yyyy-MM-dd...
        String datefr = MyTools.formatDateFr(interv.getDateDebutPrevue()); // dd-MM-yyyy HH:mm
        String timefr = MyTools.formatTimeFr(interv.getDateDebutPrevue()); // HH:mm

        /*Log.i(TAG, "currentDate - currentTime : "+MyTools.getCurrentDate()+" - "+MyTools.getCurrentTime());
        Log.i(TAG, "Date Debut Prevue : "+date+" => "+datefr+" - "+timefr);*/

        holder.title.setText(interv.getDescription());
        holder.details.setText(this.details?timefr:datefr);
        holder.state.setText(status[interv.getStatutId()-1]);

        holder.details_more.setText(interv.getClientId()+"..");

        holder.details.setTextColor(date.before(new Date()) ? Color.RED : Color.parseColor("#FFA000"));
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
        TextView state = itemView.findViewById(R.id.item_state);
        TextView details = itemView.findViewById(R.id.item_details);
        TextView details_more = itemView.findViewById(R.id.item_details_more);
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
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
                    List<Intervention> filterResultsData = new ArrayList<Intervention>();
                    for(Intervention item : items)
                    {
                        if(item.getDescription().toLowerCase().contains(searchText.toString().toLowerCase()))
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


}
