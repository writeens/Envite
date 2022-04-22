package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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

import activities.MainActivity;
import adapters.MyEnvitesListAdapter;
import interfaces.VolleyCallbackForAdapters;
import viewmodels.EnviteViewModel;

public class MyEnviteFragment extends Fragment {

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final String MY_ENVITES = "my_envites";


    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }
    private EnviteViewModel enviteViewModel;
    protected RecyclerView mRecyclerView;
    protected MyEnvitesListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected MyEnviteFragment.LayoutManagerType mCurrentLayoutManagerType;
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<Boolean>(true);
    private Integer itemCount = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_envite, container, false);

        handleLoadingState(rootView);

        // HANDLE HIDE NAVBAR
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_view);
        navBar.setVisibility(View.GONE);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.myEnviteRecyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        mCurrentLayoutManagerType = MyEnviteFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (MyEnviteFragment.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }

        mAdapter = new MyEnvitesListAdapter(new MyEnvitesListAdapter.EnviteDiff(), getContext(), MY_ENVITES);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(mLayoutManager);

        enviteViewModel = new ViewModelProvider(this).get(EnviteViewModel.class);

        enviteViewModel.getMyEnvites().observe(this, myEnvites -> {
            mAdapter.submitList(myEnvites);
            itemCount = enviteViewModel.getCountEnvites();
            isLoadingLiveData.setValue(false);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enviteViewModel.getMyEnvitesFromAPI(new VolleyCallbackForAdapters() {
            @Override
            public void onSuccess(String status) {
                isLoadingLiveData.setValue(false);
            }

            @Override
            public void onError(String message, String type, String status) {
                if(type.equals("FORBIDDEN")){
                   ((MainActivity)getActivity()).goToSignIn("Please login to continue");
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

    private void handleLoadMoreEnvites (){
        isLoadingLiveData.setValue(true);
        enviteViewModel.loadMoreEnvitesFromAPI(new VolleyCallbackForAdapters() {
            @Override
            public void onSuccess(String status) {
                isLoadingLiveData.setValue(false);
            }

            @Override
            public void onError(String message, String type, String status) {
                if(type.equals("FORBIDDEN")){
                    ((MainActivity)getActivity()).goToSignIn("Please login to continue");
                    return;
                }
                isLoadingLiveData.setValue(false);
            }
        });
    }

    private void handleLoadingState (View rootView) {
        TextView infoTextView = (TextView) rootView.findViewById(R.id.myEnvitesInfoTextView);
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

}