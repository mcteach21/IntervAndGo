package mc.apps.interv.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import mc.apps.interv.R;
import mc.apps.interv.libs.MyTools;
import mc.apps.interv.model.Message;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private static final String TAG ="tests" ;
    private List<Message> items;
    private OnItemClickListener listener;

    public MessagesAdapter(List<Message> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.item_message_layout;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, int position) {
        Message item = items.get(position);
        holder.title.setText(item.getFromUser());
        holder.details.setText(item.getMessage());
        holder.details_more.setText(MyTools.formatDateFr(item.getDateCreation()));

        if(item.getSeen()==0)
            holder.details.setTypeface(null, Typeface.BOLD);

        if (listener != null)
            holder.itemView.setOnClickListener(
                    view -> listener.onItemClick(position, items.get(position))
            );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void refresh(List<Message> images) {
        items = images;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title = itemView.findViewById(R.id.item_title);
        TextView details = itemView.findViewById(R.id.item_details);
        TextView details_more = itemView.findViewById(R.id.item_details_more);
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
