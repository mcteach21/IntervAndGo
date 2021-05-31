package mc.apps.interv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mc.apps.interv.R;
import mc.apps.interv.dao.MessageDao;
import mc.apps.interv.dao.UserDao;
import mc.apps.interv.libs.MyTools;
import mc.apps.interv.model.Message;
import mc.apps.interv.model.User;
import mc.apps.interv.viewmodels.MainViewModel;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> implements Filterable {
    private static final String TAG ="tests" ;
    private List<User> items;
    private OnItemClickListener listener;

    private boolean select = false;
    public UsersAdapter(List<User> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    private MainViewModel mainViewModel;
    public UsersAdapter(List<User> items, OnItemClickListener listener, boolean select, MainViewModel mainViewModel) {
        this(items, listener);
        this.select = select;
        this.mainViewModel = mainViewModel;
    }

    public List<User> getItems() {
        return items;
    }

    //Drawable item_logo;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = this.select?R.layout.item_layout_select:R.layout.item_layout;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(itemView);
    }

    int[] logos = {
            R.drawable.ic_profil_admin,
            R.drawable.ic_profil_superv,
            R.drawable.ic_profil_tech,
            R.drawable.ic_account_icon_red
    };
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = items.get(position);
        holder.title.setText(user.getFirstname()+" "+user.getLastname() + " ["+user.getCode()+"]");

        if(this.select) {
            List<User> selected = mainViewModel.getSelected().getValue();
            if(selected != null)
                if(selected.contains(user))
                    holder.select_switch.setChecked(true);
        }
        else{
            int profilid = user.getProfilId();
            holder.img.setImageResource(logos[profilid - 1]);
            holder.details.setText(user.getEmail());
        }

        if (listener != null)
            holder.itemView.setOnClickListener(
                    view -> listener.onItemClick(position, items.get(position))
            );

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img = itemView.findViewById(R.id.item_img);
        TextView title = itemView.findViewById(R.id.item_title);
        TextView details = itemView.findViewById(R.id.item_details);

        Switch select_switch = itemView.findViewById(R.id.item_select);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            if(select) {
                itemView.findViewById(R.id.item_select).setOnClickListener(
                        v-> {
                            User user = items.get(getAdapterPosition());
                            mainViewModel.updateSelected(user, ((Switch)v).isChecked());
                            listener.onItemClick(getAdapterPosition(), user);
                        }
                );
            }else{
                itemView.findViewById(R.id.item_btn_delete).setVisibility(View.GONE);
                itemView.findViewById(R.id.item_btn_msg).setVisibility(View.GONE);

                if(MyTools.GetCurrentProfil()==1) { //admin
                   /* itemView.findViewById(R.id.item_btn_delete).setVisibility(View.VISIBLE);
                    itemView.findViewById(R.id.item_btn_delete).setOnClickListener(
                            v -> ConfirmDelete(itemView.getContext(), getAdapterPosition())
                    );*/
                }else if(MyTools.GetCurrentProfil()==2){ //superv
                    itemView.findViewById(R.id.item_btn_msg).setVisibility(View.VISIBLE);
                    itemView.findViewById(R.id.item_btn_msg).setOnClickListener(
                            v -> SendMessage(itemView.getContext(), getAdapterPosition())
                    );
                }
            }
        }
    }
    private void SendMessage(Context context, final int position) {
        User user = items.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Message pour : "+user.getFirstname()+" "+user.getLastname()+"\n");

        final View customLayout = LayoutInflater.from(context).inflate(R.layout.message_input_layout, null);
        builder.setView(customLayout);

        builder.setPositiveButton("Envoyer", (dialog, which) -> {
            EditText edtMsg = customLayout.findViewById(R.id.edtMessage);
            String message = edtMsg.getText().toString();

            Message msg = new Message(0, message, MyTools.GetUserInSession().getCode(), user.getCode(), (byte) 0);
            MessageDao dao = new MessageDao();
            dao.add(msg, (items, mess)->{
                Toast.makeText(context, "Message envoyé à "+user.getFirstname(), Toast.LENGTH_SHORT).show();
            });
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void ConfirmDelete(Context context, final int position) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg.setMessage("Vous allez supprimer ce compte définitivement..")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    //Toast.makeText(context, "delete.."+position, Toast.LENGTH_SHORT).show();

                    //TODO : si user utilisé!!

                    User user = items.get(position);
                    UserDao dao = new UserDao();
                    dao.delete(user.getCode(),
                            (items, message) -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    );

                    items.remove(position);
                    notifyDataSetChanged();
                })
                .setNegativeButton("Annuler", null).show();
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
                    List<User> filterResultsData = new ArrayList<User>();
                    for(User item : items)
                    {
                        if(item.getFirstname().toLowerCase().contains(searchText.toString().toLowerCase())
                                || item.getLastname().toLowerCase().contains(searchText.toString().toLowerCase()))
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
                items = (List<User>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
