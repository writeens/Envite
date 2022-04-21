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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import activities.MainActivity;
import adapters.MyEnvitesListAdapter;
import adapters.SentRequestListAdapter;
import interfaces.VolleyCallbackForAdapters;
import viewmodels.EnviteViewModel;

public class SentRequestFragment extends Fragment {
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    private static final String SENT_ENVITES = "sent_envites";

    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }

    private EnviteViewModel enviteViewModel;
    protected RecyclerView mRecyclerView;
    protected SentRequestListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected SentRequestFragment.LayoutManagerType mCurrentLayoutManagerType;
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<Boolean>(true);
    private Integer itemCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sent_envite, container, false);

        // INITIALIZE VIEWS
        initializeViews();

        // HANDLE LOADING STATE
        handleLoadingState(rootView);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.sentEnviteRecyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = SentRequestFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (SentRequestFragment.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }

        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new SentRequestListAdapter(new SentRequestListAdapter.EnviteDiff(), getContext(), SENT_ENVITES);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(null);

        enviteViewModel = new ViewModelProvider(this).get(EnviteViewModel.class);

        enviteViewModel.getSentRequests().observe(this, requests -> {
            mAdapter.submitList(requests);
            itemCount = enviteViewModel.getCountSentRequests();
            isLoadingLiveData.setValue(false);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enviteViewModel.getSentRequestsFromAPI(new VolleyCallbackForAdapters() {
            @Override
            public void onSuccess(String status) {
                isLoadingLiveData.setValue(false);
            }

            @Override
            public void onError(String message, String type, String status) {
                if(type.equals("FORBIDDEN")){
                    ((MainActivity)getActivity()).goToSignIn();
                    return;
                }
                isLoadingLiveData.setValue(false);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { //check for scroll down
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                    if(!isLoadingLiveData.getValue()){
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
        enviteViewModel.loadMoreSentRequestsFromAPI(new VolleyCallbackForAdapters() {
            @Override
            public void onSuccess(String status) {
                isLoadingLiveData.setValue(false);
            }

            @Override
            public void onError(String message, String type, String status) {

                if(type.equals("FORBIDDEN")){
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            message, Snackbar.LENGTH_LONG).show();
                    ((MainActivity)getActivity()).goToSignIn();
                    return;
                }
                isLoadingLiveData.setValue(false);
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        message, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void initializeViews () {
        TabLayout tabLayout = getActivity().findViewById(R.id.enviteTabLayout);
        tabLayout.setVisibility(View.VISIBLE);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_view);
        navBar.setVisibility(View.VISIBLE);
    }

    private void handleLoadingState (View rootView) {
        TextView infoTextView = (TextView) rootView.findViewById(R.id.sentEnvitesInfoTextView);
        isLoadingLiveData.observe(this, isLoading -> {

            if(isLoading && itemCount <= 0){
                mRecyclerView.setVisibility(View.GONE);
                infoTextView.setVisibility(View.VISIBLE);
                infoTextView.setText("Please wait while we fetch your envites");
                return;
            }

            if(isLoading && itemCount >= 0){
                mRecyclerView.setVisibility(View.VISIBLE);
                infoTextView.setVisibility(View.GONE);
                return;
            }

            if(itemCount <= 0){
                mRecyclerView.setVisibility(View.GONE);
                infoTextView.setVisibility(View.VISIBLE);
                infoTextView.setText("There are no envites here, come back later");
                return;
            }

            if(itemCount > 0){
                mRecyclerView.setVisibility(View.VISIBLE);
                infoTextView.setVisibility(View.GONE);
                return;
            }
        });
    }

    public void setRecyclerViewLayoutManager(SentRequestFragment.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = SentRequestFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }


}