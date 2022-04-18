package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.envite.R;
import com.google.android.material.snackbar.Snackbar;

import activities.MainActivity;
import adapters.MyEnvitesListAdapter;
import interfaces.VolleyCallbackForAdapters;
import viewmodels.EnviteViewModel;

public class SentEnviteFragment extends Fragment {
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }

    private EnviteViewModel enviteViewModel;
    protected RecyclerView mRecyclerView;
    protected MyEnvitesListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected SentEnviteFragment.LayoutManagerType mCurrentLayoutManagerType;
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<Boolean>(true);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sent_envite, container, false);

//         BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.sentEnviteRecyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = SentEnviteFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (SentEnviteFragment.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }

        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new MyEnvitesListAdapter(new MyEnvitesListAdapter.EnviteDiff(), getContext());

        mRecyclerView.setAdapter(mAdapter);

        enviteViewModel = new ViewModelProvider(this).get(EnviteViewModel.class);

        enviteViewModel.deleteAllEnvites();

        enviteViewModel.getSentEnvites().observe(this, envites -> {
            // Update the cached copy of the words in the adapter.
            mAdapter.submitList(envites);
        });

        TextView infoTextView = (TextView) rootView.findViewById(R.id.sentEnvitesInfoTextView);
        isLoadingLiveData.observe(this, isLoading -> {

            int itemCount = enviteViewModel.getCountEnvites("sent_envites");
            if(!isLoading && itemCount <= 0){
                mRecyclerView.setVisibility(View.GONE);
                infoTextView.setVisibility(View.VISIBLE);
                infoTextView.setText("There are no envites here, come back later");
                return;
            }

            if(!isLoading){
                mRecyclerView.setVisibility(View.VISIBLE);
                infoTextView.setVisibility(View.GONE);
                return;
            }

            if(isLoading && itemCount > 0){
                mRecyclerView.setVisibility(View.VISIBLE);
                infoTextView.setVisibility(View.GONE);
                return;
            }

            if(isLoading){
                mRecyclerView.setVisibility(View.GONE);
                infoTextView.setVisibility(View.VISIBLE);
                infoTextView.setText("Please wait while we fetch your envites");
                return;
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isLoadingLiveData.setValue(true);
        enviteViewModel.getSentEnvitesFromAPI(new VolleyCallbackForAdapters() {
            @Override
            public void onSuccess(String status) {
                isLoadingLiveData.setValue(false);

            }

            @Override
            public void onError(String message, String type, String status) {
                isLoadingLiveData.setValue(false);
                if(type.equals("FORBIDDEN")){
                    ((MainActivity)getActivity()).goToSignIn();
                }

            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { //check for scroll down
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                    if(!isLoadingLiveData.getValue()){
                        int itemCount = enviteViewModel.getCountEnvites("sent_envites");
                        if(linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition()
                                == (itemCount - 1)){
                            handleLoadMoreEnvites();
                        }
                    }
                }
            }
        });
    }

    public void handleLoadMoreEnvites (){
        isLoadingLiveData.setValue(true);
        enviteViewModel.loadMoreSentEnvitesFromAPI("sent_envites", new VolleyCallbackForAdapters() {
            @Override
            public void onSuccess(String status) {
                isLoadingLiveData.setValue(false);
            }

            @Override
            public void onError(String message, String type, String status) {
                isLoadingLiveData.setValue(false);
                if(type.equals("FORBIDDEN")){
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            message, Snackbar.LENGTH_LONG).show();
                    ((MainActivity)getActivity()).goToSignIn();
                    return;
                }
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        message, Snackbar.LENGTH_LONG).show();
            }
        });
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


}