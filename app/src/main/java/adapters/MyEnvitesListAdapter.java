package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.envite.R;

import entities.Envite;

public class MyEnvitesListAdapter extends RecyclerView.Adapter<MyEnvitesListAdapter.ViewHolder> {

    private Envite[] dataSet;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView cardThumbnailImageView;
        private final TextView cardHeaderTextView;
        private final TextView cardSubheadTextView;
        private final ImageView cardMediaImageView;
        private final TextView cardSupportingTextView;



        public ViewHolder(View view) {
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

    public MyEnvitesListAdapter(Envite[] dataSet, Context ctx) {
        this.dataSet = dataSet;
        this.context = ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.envite_card_item_v2, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getCardHeaderTextView().setText(dataSet[position].getTitle());
        holder.getCardSupportingTextView().setText(dataSet[position].getNote());
        holder.getCardSubheadTextView().setText(dataSet[position].getFormattedPrice());
        Glide.with(context).load("https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80").into(holder.getCardThumbnailImageView());
        Glide.with(context).load("https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80").into(holder.getCardMediaImageView());
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }
}
