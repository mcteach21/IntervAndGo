package mc.apps.demo0.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mc.apps.demo0.R;
import mc.apps.demo0.model.User;
import mc.apps.demo0.viewmodels.MainViewModel;


public class SelectedUsersAdapter extends RecyclerView.Adapter<SelectedUsersAdapter.ViewHolder> {
    private static final String TAG ="tests" ;
    private List<User> items;
    private OnItemClickListener listener;

    public SelectedUsersAdapter(List<User> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    //Drawable item_logo;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.item_layout_simple;
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
        holder.title.setText(user.getFirstname()+" "+user.getLastname());

       /* int profilid = user.getProfilId();
        holder.img.setImageResource(logos[profilid - 1]);
        holder.details.setText(user.getEmail());


        if (listener != null)
            holder.itemView.setOnClickListener(
                    view -> listener.onItemClick(position, items.get(position))
            );
*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void refresh(List<User> selected) {
        items = selected;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //ImageView img = itemView.findViewById(R.id.item_img);
        TextView title = itemView.findViewById(R.id.item_title);
        //TextView details = itemView.findViewById(R.id.item_details);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
