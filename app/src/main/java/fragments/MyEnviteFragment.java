package fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.envite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import activities.MainActivity;
import adapters.EnviteListAdapter;
import adapters.MyEnvitesListAdapter;
import entities.Envite;
import interfaces.VolleyCallbackForAdapters;
import viewmodels.EnviteViewModel;

public class MyEnviteFragment extends Fragment {

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }
    private EnviteViewModel enviteViewModel;
    protected RecyclerView mRecyclerView;
    protected MyEnvitesListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected MyEnviteFragment.LayoutManagerType mCurrentLayoutManagerType;
    private boolean isLoading = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_envite, container, false);

        // HANDLE HIDE NAVBAR
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_view);
        navBar.setVisibility(View.GONE);

        // INITIALIZE TOOLBAR
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.myEnvitesToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.myEnviteRecyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = MyEnviteFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (MyEnviteFragment.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }

        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new MyEnvitesListAdapter(new MyEnvitesListAdapter.EnviteDiff(), getContext());

        mRecyclerView.setAdapter(mAdapter);

        enviteViewModel = new ViewModelProvider(this).get(EnviteViewModel.class);

        enviteViewModel.deleteAllEnvites();

        enviteViewModel.getMyEnvites().observe(this, envites -> {
            // Update the cached copy of the words in the adapter.
            mAdapter.submitList(envites);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        enviteViewModel.getMyEnvitesFromAPI(new VolleyCallbackForAdapters() {
            @Override
            public void onSuccess(String status) {
                Log.i("IN MY ENVITESS", status);
            }

            @Override
            public void onError(String message, String type, String status) {
                Log.i("IN MY ENVITEM", message);
                Log.i("IN MY ENVITET", type);
                Log.i("IN MY ENVITES", status);
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

                    if(!isLoading){
                        int itemCount = enviteViewModel.getCountOfMyEnvites();
                        if(linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition()
                                == (itemCount - 1)){

                            // TODO - HANDLE LOAD MORE
                            handleLoadMoreEnvites();
                        }
                    }



                }
            }
        });
    }

    public void handleLoadMoreEnvites (){
        isLoading = true;
        enviteViewModel.loadMoreEnvitesFromAPI(new VolleyCallbackForAdapters() {
            @Override
            public void onSuccess(String status) {
                isLoading = false;
                Log.i("HIN MY ENVITESS", status);
            }

            @Override
            public void onError(String message, String type, String status) {
                isLoading = false;
                Log.i("HIN MY ENVITEM", message);
                Log.i("HIN MY ENVITET", type);
                Log.i("HIN MY ENVITES", status);
                if(type.equals("FORBIDDEN")){
                    ((MainActivity)getActivity()).goToSignIn();
                }
            }
        });
    }

    public void setRecyclerViewLayoutManager(MyEnviteFragment.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = MyEnviteFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

}