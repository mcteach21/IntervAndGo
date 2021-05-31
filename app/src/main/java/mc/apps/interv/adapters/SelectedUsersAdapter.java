package mc.apps.interv.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mc.apps.interv.R;
import mc.apps.interv.model.User;


public class SelectedUsersAdapter extends RecyclerView.Adapter<SelectedUsersAdapter.ViewHolder> {
    private static final String TAG ="tests" ;
    private List<User> items;
    private OnItemClickListener listener;

    private boolean withDelete;
    public SelectedUsersAdapter(List<User> items, OnItemClickListener listener, boolean withDelete) {
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

    int[] logos = {
            R.drawable.ic_profil_admin,
            R.drawable.ic_profil_superv,
            R.drawable.ic_profil_tech,
            R.drawable.ic_account_icon_red
    };
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = items.get(position);
        Log.i(TAG, "onBindViewHolder: "+user);
        holder.title.setText(user.getFirstname()+" "+user.getLastname());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void refresh(List<User> selected) {
        items = selected;
        notifyDataSetChanged();
    }

    public List<User> getItems() {
        return items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title = itemView.findViewById(R.id.item_title);
        ImageButton deleteButton = itemView.findViewById(R.id.item_selected_delete);

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            if(withDelete) {
                deleteButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    User u = items.get(position);
                    items.remove(u);
                    notifyDataSetChanged();
                });
            }else{
                deleteButton.setVisibility(View.GONE);
            }
        }
    }
}
