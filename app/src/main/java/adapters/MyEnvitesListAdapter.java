package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.envite.R;

import entities.Envite;

public class MyEnvitesListAdapter extends ListAdapter<Envite, MyEnvitesListAdapter.EnviteViewHolder> {
    Context context;
        public MyEnvitesListAdapter(@NonNull DiffUtil.ItemCallback<Envite> diffCallback, Context ctx) {
            super(diffCallback);
            this.context = ctx;
        }

    // Create new views (invoked by the layout manager)
    @Override
    public EnviteViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.envite_card_item_v2, viewGroup, false);

        return new EnviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnviteViewHolder holder, int position) {
        Envite current = getItem(position);
        holder.getCardHeaderTextView().setText(current.getTitle());
        holder.getCardSupportingTextView().setText(current.getNote());
        holder.getCardSubheadTextView().setText(current.getFormattedPrice());
        Glide.with(context).load("https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80").into(holder.getCardThumbnailImageView());
        Glide.with(context).load("https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80").into(holder.getCardMediaImageView());
    }

    public static class EnviteDiff extends DiffUtil.ItemCallback<Envite> {

        @Override
        public boolean areItemsTheSame(@NonNull Envite oldItem, @NonNull Envite newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Envite oldItem, @NonNull Envite newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    }

    public static class EnviteViewHolder extends RecyclerView.ViewHolder {
        private final ImageView cardThumbnailImageView;
        private final TextView cardHeaderTextView;
        private final TextView cardSubheadTextView;
        private final ImageView cardMediaImageView;
        private final TextView cardSupportingTextView;

        public EnviteViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            cardThumbnailImageView = (ImageView) view.findViewById(R.id.enviteCardThumbnail);
            cardHeaderTextView = (TextView) view.findViewById(R.id.enviteCardHeaderText);
            cardSubheadTextView = (TextView) view.findViewById(R.id.enviteCardSubhead);
            cardMediaImageView = (ImageView) view.findViewById(R.id.enviteCardMedia);
            cardSupportingTextView = (TextView) view.findViewById(R.id.enviteCardSupportingText);
        }

        public ImageView getCardThumbnailImageView() {
            return cardThumbnailImageView;
        }
        public TextView getCardHeaderTextView() {
            return cardHeaderTextView;
        }
        public TextView getCardSubheadTextView() {
            return cardSubheadTextView;
        }
        public ImageView getCardMediaImageView() {
            return cardMediaImageView;
        }
        public TextView getCardSupportingTextView() {
            return cardSupportingTextView;
        }
    }
}
