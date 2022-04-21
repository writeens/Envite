package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.envite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import activities.MainActivity;
import entities.HomeEnvite;
import entities.ReceivedRequest;
import entities.SentRequest;
import interfaces.VolleyCallbackForAdapters;
import viewmodels.EnviteViewModel;

public class SingleProfileFragment extends Fragment {

    private String requestId;
    private String enviteId;
    private String tag;
    private LinearLayout singleProfileActionContainer;
    private Button singleProfileStatusButton;
    private Button singleProfileAcceptButton;
    private Button singleProfileDeclineButton;
    private TextView singleProfileFirstNameTextView;
    private TextView singleProfileLastNameTextView;
    private TextView singleProfileEmailTextView;
    private LinearLayout singleProfileEmailContainer;
    private TextView singleProfileQ1TextView;
    private TextView singleProfileQ2TextView;
    private LinearLayout singleProfileEnviteContainer;
    private TextView singleProfileEnviteTextView;
    private MutableLiveData<Boolean> isAcceptingLiveData = new MutableLiveData<Boolean>(false);
    private MutableLiveData<Boolean> isDecliningLiveData = new MutableLiveData<Boolean>(false);
    private EnviteViewModel enviteViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        requestId = (String) arguments.get("requestId");
        enviteId = (String) arguments.get("enviteId");
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

        // HANDLE ACCEPT BUTTON LOADING STATES
        handleAcceptButtonLoadingState();

        // HANDLE Decline BUTTON LOADING STATES
        handleDeclineButtonLoadingState();

        enviteViewModel = new ViewModelProvider(this).get(EnviteViewModel.class);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(tag == "received_envites"){
            handleUpdateViewForReceivedRequests();
        }
        if(tag == "sent_envites"){
            handleUpdateViewForSentRequests();
        }
        if(tag == "home_envites"){
            handleUpdateViewForHomeEnvites();
        }
    }

    private void initializeViews (View view) {
        //INITIALIZE VIEWS
        singleProfileActionContainer = view.findViewById(R.id.singleProfileActionContainer);
        singleProfileAcceptButton = view.findViewById(R.id.singleProfileAcceptButton);
        singleProfileDeclineButton = view.findViewById(R.id.singleProfileDeclineButton);
        singleProfileFirstNameTextView = view.findViewById(R.id.singleProfileFirstNameTextView);
        singleProfileLastNameTextView = view.findViewById(R.id.singleProfileLastNameTextView);
        singleProfileEmailTextView = view.findViewById(R.id.singleProfileEmailTextView);
        singleProfileQ1TextView = view.findViewById(R.id.singleProfileQ1TextView);
        singleProfileQ2TextView = view.findViewById(R.id.singleProfileQ2TextView);
        singleProfileStatusButton = view.findViewById(R.id.singleProfileStatusButton);
        singleProfileEmailContainer = view.findViewById(R.id.singleProfileEmailContainer);
        singleProfileEnviteContainer = view.findViewById(R.id.singleProfileEnviteContainer);
        singleProfileEnviteTextView = view.findViewById(R.id.singleProfileEnviteTextView);
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

    private void handleUpdateViewForReceivedRequests() {
        ReceivedRequest receivedRequest = enviteViewModel.getReceivedRequestById(requestId);
        if(receivedRequest == null){
            Snackbar.make(this.getActivity().findViewById(android.R.id.content),
                    "Request no longer available", Snackbar.LENGTH_LONG).show();
            getActivity().onBackPressed();
            return;
        }

        TabLayout tabLayout = getActivity().findViewById(R.id.enviteTabLayout);
        tabLayout.setVisibility(View.GONE);
        singleProfileFirstNameTextView.setText(receivedRequest.getRequestedBy().getFirstName());
        singleProfileLastNameTextView.setText(receivedRequest.getRequestedBy().getLastName());
        singleProfileEmailTextView.setText(receivedRequest.getRequestedBy().getEmail());
        singleProfileQ1TextView.setText(receivedRequest.getRequestedBy().getQ1());
        singleProfileQ2TextView.setText(receivedRequest.getRequestedBy().getQ2());
        singleProfileEnviteTextView.setText(receivedRequest.getEnvite().getTitle());
        singleProfileEnviteContainer.setVisibility(View.VISIBLE);
        if(receivedRequest.getStatus().equals("PENDING")){
            singleProfileActionContainer.setVisibility(View.VISIBLE);
            singleProfileStatusButton.setVisibility(View.GONE);
            //HIDE FIELDS
            singleProfileEmailContainer.setVisibility(View.GONE);
        }
        if(receivedRequest.getStatus().equals("DECLINED")){
            singleProfileActionContainer.setVisibility(View.GONE);
            singleProfileStatusButton.setText(receivedRequest.getStatus());
            singleProfileStatusButton.setVisibility(View.VISIBLE);
            singleProfileStatusButton.setEnabled(false);
            //HIDE FIELDS
            singleProfileEmailContainer.setVisibility(View.GONE);
        }
        if(receivedRequest.getStatus().equals("ACCEPTED")){
            singleProfileActionContainer.setVisibility(View.GONE);
            singleProfileStatusButton.setText(receivedRequest.getStatus());
            singleProfileStatusButton.setVisibility(View.VISIBLE);
            singleProfileStatusButton.setEnabled(false);
            //SHOW FIELDS
            singleProfileEmailContainer.setVisibility(View.VISIBLE);
        }
        handleAcceptButtonClick();
        handleDeclineButtonClick();
        singleProfileEnviteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("requestId", receivedRequest.getId());
                bundle.putString("tag", tag);
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_singleProfileFragment_to_singleEnviteFragment2, bundle);
            }
        });
    }

    private void handleUpdateViewForSentRequests() {
        SentRequest sentRequest = enviteViewModel.getSentRequestById(requestId);
        if(sentRequest == null){
            Snackbar.make(this.getActivity().findViewById(android.R.id.content),
                    "Request no longer available", Snackbar.LENGTH_LONG).show();
            getActivity().onBackPressed();
            return;
        }

        TabLayout tabLayout = getActivity().findViewById(R.id.enviteTabLayout);
        tabLayout.setVisibility(View.GONE);
        singleProfileEnviteContainer.setVisibility(View.VISIBLE);
        singleProfileFirstNameTextView.setText(sentRequest.getRequestedTo().getFirstName());
        singleProfileLastNameTextView.setText(sentRequest.getRequestedTo().getLastName());
        singleProfileQ1TextView.setText(sentRequest.getRequestedTo().getQ1());
        singleProfileQ2TextView.setText(sentRequest.getRequestedTo().getQ2());
        singleProfileEnviteTextView.setText(sentRequest.getEnvite().getTitle());
        singleProfileActionContainer.setVisibility(View.GONE);
        singleProfileStatusButton.setText(sentRequest.getStatus());
        singleProfileStatusButton.setVisibility(View.VISIBLE);
        singleProfileStatusButton.setEnabled(false);
        singleProfileEnviteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("requestId", sentRequest.getId());
                bundle.putString("tag", tag);
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_singleProfileFragment_to_singleEnviteFragment2, bundle);
            }
        });
    }

    private void handleUpdateViewForHomeEnvites () {
        HomeEnvite homeEnvite = enviteViewModel.getHomeEnviteById(enviteId);
        if(homeEnvite == null){
            Snackbar.make(this.getActivity().findViewById(android.R.id.content),
                    "Envite no longer available", Snackbar.LENGTH_LONG).show();
            getActivity().onBackPressed();
            return;
        }
        singleProfileEnviteContainer.setVisibility(View.VISIBLE);
        singleProfileFirstNameTextView.setText(homeEnvite.getCreatedByUser().getFirstName());
        singleProfileLastNameTextView.setText(homeEnvite.getCreatedByUser().getLastName());
        singleProfileQ1TextView.setText(homeEnvite.getCreatedByUser().getQ1());
        singleProfileQ2TextView.setText(homeEnvite.getCreatedByUser().getQ2());
        singleProfileEnviteContainer.setVisibility(View.GONE);
        singleProfileActionContainer.setVisibility(View.GONE);
        singleProfileStatusButton.setVisibility(View.GONE);
    }

    private void handleAcceptButtonClick () {
        singleProfileAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAcceptingLiveData.setValue(true);
                enviteViewModel.acceptRequest(requestId, new VolleyCallbackForAdapters() {
                    @Override
                    public void onSuccess(String status) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Request Successfully Accepted", Snackbar.LENGTH_LONG).show();
                        singleProfileActionContainer.setVisibility(View.GONE);
                        singleProfileActionContainer.setVisibility(View.GONE);
                        singleProfileStatusButton.setText("ACCEPTED");
                        singleProfileStatusButton.setVisibility(View.VISIBLE);
                        isAcceptingLiveData.setValue(false);
                        //SHOW FIELDS
                        singleProfileEmailContainer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(String message, String type, String status) {
                        if(type.equals("FORBIDDEN")){
                            ((MainActivity)getActivity()).goToSignIn();
                            return;
                        }
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                message, Snackbar.LENGTH_LONG).show();
                        isAcceptingLiveData.setValue(false);
                    }
                });
            }
        });
    }

    private void handleDeclineButtonClick () {
        singleProfileDeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDecliningLiveData.setValue(true);
                enviteViewModel.declineRequest(requestId, new VolleyCallbackForAdapters() {
                    @Override
                    public void onSuccess(String status) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Request Successfully Declined", Snackbar.LENGTH_LONG).show();
                        singleProfileActionContainer.setVisibility(View.GONE);
                        singleProfileActionContainer.setVisibility(View.GONE);
                        singleProfileStatusButton.setText("DECLINED");
                        singleProfileStatusButton.setVisibility(View.VISIBLE);
                        isAcceptingLiveData.setValue(false);
                    }

                    @Override
                    public void onError(String message, String type, String status) {
                        if(type.equals("FORBIDDEN")){
                            ((MainActivity)getActivity()).goToSignIn();
                            return;
                        }
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                message, Snackbar.LENGTH_LONG).show();
                        isAcceptingLiveData.setValue(false);
                    }
                });
            }
        });
    }

    private void handleAcceptButtonLoadingState () {
        isAcceptingLiveData.observe(this, isAccepting -> {
            if(isAccepting){
                singleProfileAcceptButton.setText("Please wait...");
                singleProfileAcceptButton.setEnabled(false);
                singleProfileDeclineButton.setEnabled(false);
                return;
            }
            singleProfileAcceptButton.setText("Accept");
            singleProfileAcceptButton.setEnabled(true);
            singleProfileDeclineButton.setEnabled(true);
        });

    }

    private void handleDeclineButtonLoadingState () {
        isDecliningLiveData.observe(this, isDeclining -> {
            if(isDeclining){
                singleProfileDeclineButton.setText("Please wait...");
                singleProfileAcceptButton.setEnabled(false);
                singleProfileDeclineButton.setEnabled(false);
                return;
            }
            singleProfileDeclineButton.setText("Decline");
            singleProfileAcceptButton.setEnabled(true);
            singleProfileDeclineButton.setEnabled(true);
        });

    }


}