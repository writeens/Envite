package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.envite.R;

import adapters.EnviteListAdapter;
import adapters.SentEnviteListAdapter;
import entities.Envite;

public class SentEnviteFragment extends Fragment {
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }

    protected RecyclerView mRecyclerView;
    protected SentEnviteListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected SentEnviteFragment.LayoutManagerType mCurrentLayoutManagerType;
    protected Envite[] mDataset;
    private static final int DATASET_COUNT = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //INITIALIZE DATASET
//        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sent_envite, container, false);

        // BEGIN_INCLUDE(initializeRecyclerView)
//        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.sentEnviteRecyclerView);
//
//        mLayoutManager = new LinearLayoutManager(getActivity());
//
//        mCurrentLayoutManagerType = SentEnviteFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
//
//        if (savedInstanceState != null) {
//            // Restore saved layout manager type.
//            mCurrentLayoutManagerType = (SentEnviteFragment.LayoutManagerType) savedInstanceState
//                    .getSerializable(KEY_LAYOUT_MANAGER);
//        }
//
//        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
//
//        mAdapter = new SentEnviteListAdapter(mDataset, getContext());
//
//        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public void setRecyclerViewLayoutManager(SentEnviteFragment.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = SentEnviteFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    private void initDataset() {
        mDataset = new Envite[DATASET_COUNT];
//        for (int i = 0; i < DATASET_COUNT; i++) {
//            mDataset[i] = new Envite((i+1) + " bedroom Flat with amenities and everything you need", "Another beautiful apartment available for your perusal at " + (i+1), (i * 100));
//        }
    }
}