package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.envite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class SingleProfileFragment extends Fragment {
    private String requestId;
    private String tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        requestId = (String) arguments.get("requestId");
        tag = (String) arguments.get("tag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_single_profile, container, false);

        // INITIALIZE VIEWS
        initializeViews(rootView);

        // HANDLE TOOLBAR
        handleToolbar(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initializeViews (View view) {
        //INITIALIZE VIEWS
//        singleEnviteInfoBox = view.findViewById(R.id.singleEnviteInfoTextView);
//        singleEnviteScrollView = view.findViewById(R.id.singleEnviteScrollView);
//        singleEnvitePostedBy = view.findViewById(R.id.singleEnvitePostedByContainer);
//        singleEnviteRequestedBy = view.findViewById(R.id.singleEnviteRequestedByContainer);
//        singleEnviteButton = view.findViewById(R.id.singleEnviteButton);
//        singleEnviteTitleTextView = view.findViewById(R.id.singleEnviteTitleTextView);
//        singleEnvitePriceTextView = view.findViewById(R.id.singleEnvitePriceTextView);
//        singleEnviteLocationTextView = view.findViewById(R.id.singleEnviteLocationTextView);
//        singleEnviteNoteTextView = view.findViewById(R.id.singleEnviteNoteTextView);
//
//        if(tag == "my_envites"){
//            singleEnviteRequestedBy.setVisibility(View.GONE);
//            singleEnvitePostedBy.setVisibility(View.GONE);
//            singleEnviteButton.setVisibility(View.GONE);
//        }
//
        if(tag == "sent_envites"){
            TabLayout tabLayout = getActivity().findViewById(R.id.enviteTabLayout);
            tabLayout.setVisibility(View.GONE);
        }
        if(tag == "received_envites"){
            TabLayout tabLayout = getActivity().findViewById(R.id.enviteTabLayout);
            tabLayout.setVisibility(View.GONE);
        }

        // HANDLE HIDE NAVBAR
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_view);
        navBar.setVisibility(View.GONE);

    }

    private void handleToolbar (View rootView) {
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.singleProfileToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( tag == "received_envites"){
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.receivedEnviteFragment);
                    return;
                }
                if(tag == "sent_envites"){
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.sentEnviteFragment);
                    return;
                }
                getActivity().onBackPressed();

            }
        });
    }
}