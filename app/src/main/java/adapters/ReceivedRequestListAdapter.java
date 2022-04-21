package adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.envite.R;

import entities.ReceivedRequest;

public class ReceivedRequestListAdapter extends ListAdapter<ReceivedRequest, ReceivedRequestListAdapter.EnviteViewHolder> {
    Context context;
    String tag;
    public ReceivedRequestListAdapter(@NonNull DiffUtil.ItemCallback<ReceivedRequest> diffCallback, Context ctx, String tag) {
        super(diffCallback);
        this.context = ctx;
        this.tag = tag;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EnviteViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.envite_card_item_v1, viewGroup, false);

        return new EnviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnviteViewHolder holder, int position) {
        ReceivedRequest current = getItem(position);

        holder.getCardTitleTextView().setText(current.getRequestedBy().getFullName());
        holder.getCardSubheadTextView().setText(current.getEnvite().getTitle());
        Glide.with(context).load(current.getRequestedBy().getProfileUrl()).into(holder.getCardThumbnailImageView());
        holder.getCardButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("requestId", current.getId());
                bundle.putString("tag", tag);
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_receivedRequestFragment_to_singleProfileFragment, bundle);
            }
        });
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public static class EnviteDiff extends DiffUtil.ItemCallback<ReceivedRequest> {

        @Override
        public boolean areItemsTheSame(@NonNull ReceivedRequest oldItem, @NonNull ReceivedRequest newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReceivedRequest oldItem, @NonNull ReceivedRequest newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    }

    public static class EnviteViewHolder extends RecyclerView.ViewHolder {
        private final ImageView cardThumbnailImageView;
        private final TextView cardTitleTextView;
        private final TextView cardSubheadTextView;
        private final TextView cardTimestampTextView;
        private final TextView cardButton;


        public EnviteViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            cardThumbnailImageView = (ImageView) view.findViewById(R.id.enviteCardThumbnailV1);
            cardTitleTextView = (TextView) view.findViewById(R.id.enviteCardTitleV1);
            cardSubheadTextView = (TextView) view.findViewById(R.id.enviteCardSubheadV1);
            cardTimestampTextView = (TextView) view.findViewById(R.id.enviteCardTimestampV1);
            cardButton = (TextView) view.findViewById(R.id.enviteCardButtonV1);

        }

        public ImageView getCardThumbnailImageView() {
            return cardThumbnailImageView;
        }
        public TextView getCardTitleTextView() { return cardTitleTextView; }
        public TextView getCardSubheadTextView() {
            return cardSubheadTextView;
        }
        public TextView getCardTimestampTextView() {
            return cardTimestampTextView;
        }
        public TextView getCardButton() {return  cardButton; }
    }
}

