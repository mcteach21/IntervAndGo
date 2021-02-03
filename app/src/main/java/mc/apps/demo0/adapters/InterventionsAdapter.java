package mc.apps.demo0.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import mc.apps.demo0.MapsActivity;
import mc.apps.demo0.R;
import mc.apps.demo0.dao.AdressDao;
import mc.apps.demo0.libs.MyTools;
import mc.apps.demo0.model.Adress;
import mc.apps.demo0.model.Client;
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
        int layout = R.layout.item_layout_interv; //this.details?R.layout.item_layout_details:R.layout.item_layout;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        context = parent.getContext();
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull InterventionsAdapter.ViewHolder holder, int position) {
        Intervention interv = items.get(position);
        Date date = MyTools.getDateOfString(interv.getDateDebutPrevue()); //MySQL Date yyyy-MM-dd...

        String datefr = MyTools.formatDateFr(interv.getDateDebutPrevue()); // dd-MM-yyyy HH:mm
        String timefr = MyTools.formatTimeFr(interv.getDateDebutPrevue()); // HH:mm

        String datefrDebutR = interv.getDateDebutReelle()!=null?MyTools.formatDateFr(interv.getDateDebutReelle()):""; // dd-MM-yyyy HH:mm
        String timefrDebutR = interv.getDateDebutReelle()!=null?MyTools.formatTimeFr(interv.getDateDebutPrevue()):""; // HH:mm
        //String datefrFinR = interv.getDateFinReelle()!=null?MyTools.formatDateFr(interv.getDateFinReelle()):""; // dd-MM-yyyy HH:mm

        holder.client.setText(interv.getClientId());
        holder.title.setText(interv.getDescription()+" ["+interv.getCode()+"]");

        holder.details.setText("Prévue : "+datefr); //(this.details?timefr:datefr)
        if(!datefrDebutR.isEmpty())
           holder.details_more.setText("Début : "+datefrDebutR);

        holder.details_more.setVisibility(datefrDebutR.isEmpty()?View.GONE:View.VISIBLE);
        holder.details.setTextColor(date.before(new Date()) ? Color.RED : Color.GREEN);

        //holder.state.setText(status[interv.getStatutId()-1]);
        //holder.btn_goto_rapport.setVisibility(this.details?View.VISIBLE:View.INVISIBLE);
        /*if(interv.getDateDebutReelle()==null) {
            status = "en attente";

        }else if(interv.getDateFinReelle()==null) {
            status = "en cours";

        }else if(interv.getDateFinReelle()!=null) { //terminée
            status = "terminée";
            color = Color.RED;
        }*/

        String statut="en attente";
        int color = Color.parseColor("#FFA000");
        switch(interv.getStatutId()){
            case 2:statut="en cours"; color = Color.GREEN; break;
            case 3:statut="abandonnée"; color = Color.BLACK; break;
            case 4:statut="à poursuivre"; color = Color.YELLOW; break;
            case 5:statut="terminée"; color = Color.RED; break;
        }
        holder.state.setText(statut);
        holder.state.setTextColor(color);

        if(listener!=null)
            holder.itemView.setOnClickListener(
                    view -> listener.onItemClick(position, items.get(position))
            );

        setClientAdress(interv.getClientId(), holder.btn_goto_maps);
    }
    private void setClientAdress(String code_client, ImageView btn_goto_maps) {
        AdressDao dao = new AdressDao();
        dao.ofClient(code_client, (items, message) -> {
            List<Adress> adresses = dao.Deserialize(items, Adress.class);
            if(adresses.size()>0) {
                String adress = adresses.get(0).getVoie()+" "+adresses.get(0).getCp()+ " " + adresses.get(0).getVille();
                btn_goto_maps.setOnClickListener(
                        v-> showAdressInMaps(adress)
                );
            }
        });
    }
    private void showAdressInMaps(String adress) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra("adress", adress);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setFilter(Hashtable<String, Object> filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //filter.forEach((k,v)->{

            String value1 = (String) filter.get("codeClient");
            if(!value1.isEmpty()) {
                Log.i(TAG, "setFilter: code client = " + value1);

                Log.i(TAG, "setFilter: items 0"+items.size());
                items = items.stream().filter(i -> i.getClientId().toLowerCase().equals(value1.toLowerCase())).collect(Collectors.toList());
                Log.i(TAG, "setFilter: items 1"+items.size());
            }

            String value2 = (String) filter.get("codeSupervisor");
            if(!value2.isEmpty()) {
                Log.i(TAG, "setFilter: code supervisor = " + value2);
                items = items.stream().filter(i -> i.getSuperviseurId().toLowerCase().equals(value2.toLowerCase())).collect(Collectors.toList());

            }

            String value3 = (String) filter.get("dateDebutPrev");
            if(!value3.isEmpty())
                items = items.stream().filter(i-> MyTools.DateEquals(i.getDateDebutPrevue(), value3)).collect(Collectors.toList());
            String value4 = (String) filter.get("dateDebutReel");
            if(!value4.isEmpty())
                items = items.stream().filter( i-> MyTools.DateEquals(i.getDateDebutReelle(), value4)).collect(Collectors.toList());

            int status = (int) filter.get("status");
            Log.i(TAG, "setFilter: "+status);

            if(status==1) //en attente
                items = items.stream().filter( i-> i.getDateDebutReelle()==null).collect(Collectors.toList());
            else if(status==2) //en cours
                items = items.stream().filter( i-> i.getDateDebutReelle()!=null && i.getDateFinReelle()==null).collect(Collectors.toList());
            else if(status==5) //terminée
                items = items.stream().filter( i-> i.getDateDebutReelle()!=null && i.getDateFinReelle()!=null).collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView client = itemView.findViewById(R.id.item_interv_client);
        TextView title = itemView.findViewById(R.id.item_title);
        TextView state = itemView.findViewById(R.id.item_state);
        TextView details = itemView.findViewById(R.id.item_details);
        TextView details_more = itemView.findViewById(R.id.item_details_more);

        ImageView btn_goto_maps = itemView.findViewById(R.id.btn_maps);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            /*btn_goto_maps.setOnClickListener(v->{
                Log.i(TAG, "ViewHolder: goto maps!");
*//*                String adress = adresses.get(0).getVoie()+" "+adresses.get(0).getCp()+ " " + adresses.get(0).getVille();
                btn_maps.setOnClickListener(
                        v-> showAdressInMaps(adress)
                );*//*
            });*/
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
