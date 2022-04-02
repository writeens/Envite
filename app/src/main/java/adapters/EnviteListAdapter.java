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

public class EnviteListAdapter extends RecyclerView.Adapter<EnviteListAdapter.ViewHolder> {

    private Envite[] dataSet;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cardTitleTextView;
        private final TextView cardBodyTextView;
        private final ImageView cardImageView;
        private final TextView cardPriceTextView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            cardTitleTextView = (TextView) view.findViewById(R.id.cardTitleTextView);
            cardBodyTextView = (TextView) view.findViewById(R.id.cardBodyTextView);
            cardImageView = (ImageView) view.findViewById(R.id.cardImageView);
            cardPriceTextView = (TextView) view.findViewById(R.id.cardPriceTextView);
        }

        public TextView getCardTitleTextView() {
            return cardTitleTextView;
        }
        public TextView getCardBodyTextView() {
            return cardBodyTextView;
        }
        public ImageView getCardImageView() {
            return cardImageView;
        }
        public TextView getCardPriceTextView() {
            return cardPriceTextView;
        }
    }

    public EnviteListAdapter(Envite[] dataSet, Context ctx) {
        this.dataSet = dataSet;
        this.context = ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.envite_card_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getCardTitleTextView().setText(dataSet[position].getTitle());
        holder.getCardBodyTextView().setText(dataSet[position].getNote());
        holder.getCardPriceTextView().setText(dataSet[position].getFormattedPrice());
        Glide.with(context).load("https://res.cloudinary.com/brees/image/upload/v1648084145/static/header-final_gxcnux.png").into(holder.getCardImageView());
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }
}
