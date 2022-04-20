package adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.List;

import entities.Envite;
import entities.MyEnvites;

public class MyEnvitesListAdapter extends ListAdapter<MyEnvites, MyEnvitesListAdapter.EnviteViewHolder> {
    Context context;
    String tag;
        public MyEnvitesListAdapter(@NonNull DiffUtil.ItemCallback<MyEnvites> diffCallback, Context ctx, String tag) {
            super(diffCallback);
            this.context = ctx;
            this.tag = tag;
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
        MyEnvites current = getItem(position);

        holder.getCardHeaderTextView().setText(current.getTitle());
        holder.getCardSupportingTextView().setText(current.getNote());
        holder.getCardSubheadTextView().setText(current.getFormattedPrice());
        holder.getCardThumbnailImageView().setBackgroundColor(getThumbnailColor(current));
        Glide.with(context).load(current.getImageUrl()).into(holder.getCardMediaImageView());
        holder.getCardButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("enviteId", current.getId());
                bundle.putString("tag", tag);
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.singleEnviteFragment, bundle);
            }
        });
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public Integer getThumbnailColor (MyEnvites myEnvites) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.enviteUserSharedPreferencesFile), Context.MODE_PRIVATE);
        String uid = sharedPref.getString(context.getString(R.string.sharedPrefUid), "");
            if(myEnvites.getCreatedBy().equals(uid)){
                return R.color.quantum_bluegrey50;
            } else {
                return R.color.quantum_googgreen500;
            }
    }

    public static class EnviteDiff extends DiffUtil.ItemCallback<MyEnvites> {

        @Override
        public boolean areItemsTheSame(@NonNull MyEnvites oldItem, @NonNull MyEnvites newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MyEnvites oldItem, @NonNull MyEnvites newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    }

    public static class EnviteViewHolder extends RecyclerView.ViewHolder {
        private final ImageView cardThumbnailImageView;
        private final TextView cardHeaderTextView;
        private final TextView cardSubheadTextView;
        private final ImageView cardMediaImageView;
        private final TextView cardSupportingTextView;
        private final Button cardButton;


        public EnviteViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            cardThumbnailImageView = (ImageView) view.findViewById(R.id.enviteCardThumbnail);
            cardHeaderTextView = (TextView) view.findViewById(R.id.enviteCardHeaderText);
            cardSubheadTextView = (TextView) view.findViewById(R.id.enviteCardSubhead);
            cardMediaImageView = (ImageView) view.findViewById(R.id.enviteCardMedia);
            cardSupportingTextView = (TextView) view.findViewById(R.id.enviteCardSupportingText);
            cardButton = (Button) view.findViewById(R.id.enviteCardButton);

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
        public Button getCardButton() {return  cardButton; }
    }
}
