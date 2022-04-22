package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.envite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import activities.MainActivity;
import adapters.HomeEnviteListAdapter;
import adapters.MyEnvitesListAdapter;
import entities.Envite;
import interfaces.VolleyCallbackForAdapters;
import viewmodels.EnviteViewModel;

public class HomeFragment extends Fragment {
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final String HOME_ENVITES = "home_envites";


    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }

    protected RecyclerView mRecyclerView;
    protected HomeEnviteListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected LayoutManagerType mCurrentLayoutManagerType;
    private EnviteViewModel enviteViewModel;
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // HANDLE LOADING STATE
        handleLoadingState(rootView);

        // HANDLE SHOW NAVBAR
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_view);
        navBar.setVisibility(View.VISIBLE);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.closeToYouRecyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, true);

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }

        mAdapter = new HomeEnviteListAdapter(new HomeEnviteListAdapter.EnviteDiff(), getContext(), HOME_ENVITES);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(mLayoutManager);

        enviteViewModel = new ViewModelProvider(this).get(EnviteViewModel.class);

        enviteViewModel.getHomeEnvites().observe(this, homeEnvites -> {
            itemCount = enviteViewModel.getCountHomeEnvites();
            mAdapter.submitList(homeEnvites);
            isLoadingLiveData.setValue(false);
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        //HANDLE FAB CLICKS
        FloatingActionButton fab = getView().findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_homeFragment_to_createEnviteFragment);
            }
        });
        enviteViewModel.getHomeEnvitesFromAPI(new VolleyCallbackForAdapters() {
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
                if (dy < 0) { //check for scroll down
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
        enviteViewModel.loadMoreHomeEnvitesFromAPI(new VolleyCallbackForAdapters() {
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
        TextView infoTextView = (TextView) rootView.findViewById(R.id.homeEnvitesInfoTextView);
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
                infoTextView.setText("No envites available");
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