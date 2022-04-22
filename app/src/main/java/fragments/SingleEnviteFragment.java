package fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.envite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import activities.MainActivity;
import entities.HomeEnvite;
import entities.MyEnvite;
import entities.ReceivedRequest;
import entities.SentRequest;
import interfaces.VolleyCallbackForAdapters;
import viewmodels.EnviteViewModel;

public class SingleEnviteFragment extends Fragment {

    private String enviteId;
    private String requestId;
    private String tag;
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<Boolean>(true);
    private EnviteViewModel enviteViewModel;
    private TextView singleEnviteInfoBox;
    private ScrollView singleEnviteScrollView;
    private LinearLayout singleEnvitePostedByContainer;
    private TextView singleEnvitePostedByTextView;
    private Button singleEnviteButton;
    private TextView singleEnviteTitleTextView;
    private TextView singleEnviteLocationTextView;
    private TextView singleEnvitePriceTextView;
    private TextView singleEnviteNoteTextView;
    private ImageView singleEnviteImageView;
    private MutableLiveData<Boolean> isRequestingLiveData = new MutableLiveData<Boolean>(false);



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        enviteId = (String) arguments.get("enviteId");
        requestId = (String) arguments.get("requestId");
        tag = (String) arguments.get("tag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_single_envite, container, false);

        // INITIALIZE VIEWS
        initializeViews(rootView);

        //HANDLE TOOLBAR
//        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.singleEnviteToolbar);
//
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if( tag == "received_envites"){
//                    Bundle bundle = new Bundle();
//                    bundle.putString("requestId", requestId);
//                    bundle.putString("tag", tag);
//                    NavController navController = Navigation.findNavController(v);
//                    navController.navigate(R.id.action_singleEnviteFragment2_to_singleProfileFragment, bundle);
//                    return;
//                }
//                if(tag == "sent_envites"){
//                    Bundle bundle = new Bundle();
//                    bundle.putString("requestId", requestId);
//                    bundle.putString("tag", tag);
//                    NavController navController = Navigation.findNavController(v);
//                    navController.navigate(R.id.action_singleEnviteFragment2_to_singleProfileFragment, bundle);
//                    return;
//                }
//                getActivity().onBackPressed();
//
//            }
//        });

        // SETUP VIEW MODEL
        enviteViewModel = new ViewModelProvider(this).get(EnviteViewModel.class);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleLoadingState();

        // HANDLE REQUESTING ENVITE BUTTON LOADING STATES
        handleRequestButtonLoadingState();

        if(tag == "my_envites"){
            handleMyEnviteView();
        }
        if(tag == "received_envites"){
            handleReceivedRequestsView();
        }
        if(tag == "sent_envites"){
            handleSentRequestsView();
        }
        if(tag == "home_envites"){
            handleHomeEnvitesView();
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if( tag == "received_envites"){
                    Bundle bundle = new Bundle();
                    bundle.putString("requestId", requestId);
                    bundle.putString("tag", tag);
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_singleEnviteFragment2_to_singleProfileFragment, bundle);
                    return;
                }
                if(tag == "sent_envites"){
                    Bundle bundle = new Bundle();
                    bundle.putString("requestId", requestId);
                    bundle.putString("tag", tag);
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_singleEnviteFragment2_to_singleProfileFragment, bundle);
                    return;
                }
                this.remove();
                requireActivity().onBackPressed();
            }
        });

    }

    private void initializeViews (View view) {
        //INITIALIZE VIEWS
        singleEnviteInfoBox = view.findViewById(R.id.singleEnviteInfoTextView);
        singleEnviteScrollView = view.findViewById(R.id.singleEnviteScrollView);
        singleEnvitePostedByContainer = view.findViewById(R.id.singleEnvitePostedByContainer);
        singleEnvitePostedByTextView = view.findViewById(R.id.singleEnvitePostedByTextView);
        singleEnviteButton = view.findViewById(R.id.singleEnviteButton);
        singleEnviteTitleTextView = view.findViewById(R.id.singleEnviteTitleTextView);
        singleEnvitePriceTextView = view.findViewById(R.id.singleEnvitePriceTextView);
        singleEnviteLocationTextView = view.findViewById(R.id.singleEnviteLocationTextView);
        singleEnviteNoteTextView = view.findViewById(R.id.singleEnviteNoteTextView);
        singleEnviteImageView = view.findViewById(R.id.singleEnviteImageView);

        // HANDLE HIDE NAVBAR
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_view);
        navBar.setVisibility(View.GONE);

    }


    private void handleLoadingState () {
        isLoadingLiveData.observe(this, isLoading -> {
            if(!isLoading){
                singleEnviteInfoBox.setText("");
                singleEnviteInfoBox.setVisibility(View.GONE);
                singleEnviteScrollView.setVisibility(View.VISIBLE);
                return;
            }
            if(isLoading){
                singleEnviteInfoBox.setText("Please Wait");
                singleEnviteInfoBox.setVisibility(View.VISIBLE);
                singleEnviteScrollView.setVisibility(View.GONE);
                return;
            }
        });
    }

    private void handleMyEnviteView () {
        isLoadingLiveData.setValue(true);
        MyEnvite myEnvite = enviteViewModel.getMyEnviteById(enviteId);

        if(myEnvite == null){
          getActivity().onBackPressed();
          return;
        }
        singleEnvitePostedByContainer.setVisibility(View.GONE);
        singleEnviteButton.setVisibility(View.GONE);
        singleEnviteTitleTextView.setText(myEnvite.getTitle());
        singleEnvitePriceTextView.setText(myEnvite.getFormattedPrice());
        singleEnviteNoteTextView.setText(myEnvite.getNote());
        singleEnviteLocationTextView.setText(myEnvite.getLocation());
        Glide.with(getContext()).load(myEnvite.getImageUrl()).into(singleEnviteImageView);
        isLoadingLiveData.setValue(false);
    }

    private void handleSentRequestsView () {
        isLoadingLiveData.setValue(true);
        SentRequest sentRequest = enviteViewModel.getSentRequestById(requestId);
        if(sentRequest == null){
            getActivity().onBackPressed();
            return;
        }
        TabLayout tabLayout = getActivity().findViewById(R.id.enviteTabLayout);
        tabLayout.setVisibility(View.GONE);
        singleEnvitePostedByContainer.setVisibility(View.GONE);
        singleEnviteButton.setVisibility(View.GONE);
        singleEnviteTitleTextView.setText(sentRequest.getEnvite().getTitle());
        singleEnvitePriceTextView.setText(sentRequest.getEnvite().getFormattedPrice());
        singleEnviteNoteTextView.setText(sentRequest.getEnvite().getNote());
        singleEnviteLocationTextView.setText(sentRequest.getEnvite().getLocation());
        Glide.with(getContext()).load(sentRequest.getEnvite().getImageUrl()).into(singleEnviteImageView);

        isLoadingLiveData.setValue(false);
    }

    private void handleReceivedRequestsView () {
        isLoadingLiveData.setValue(true);
        ReceivedRequest receivedRequest = enviteViewModel.getReceivedRequestById(requestId);
        if(receivedRequest == null){
            getActivity().onBackPressed();
            return;
        }
        TabLayout tabLayout = getActivity().findViewById(R.id.enviteTabLayout);
        tabLayout.setVisibility(View.GONE);
        singleEnvitePostedByContainer.setVisibility(View.GONE);
        singleEnviteButton.setVisibility(View.GONE);
        singleEnviteTitleTextView.setText(receivedRequest.getEnvite().getTitle());
        singleEnvitePriceTextView.setText(receivedRequest.getEnvite().getFormattedPrice());
        singleEnviteNoteTextView.setText(receivedRequest.getEnvite().getNote());
        singleEnviteLocationTextView.setText(receivedRequest.getEnvite().getLocation());
        Glide.with(getContext()).load(receivedRequest.getEnvite().getImageUrl()).into(singleEnviteImageView);
        isLoadingLiveData.setValue(false);
    }

    private void handleHomeEnvitesView () {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.enviteUserSharedPreferencesFile),
                Context.MODE_PRIVATE);
        String uid = sharedPref.getString(getString(R.string.sharedPrefUid), "");
        isLoadingLiveData.setValue(true);
        HomeEnvite homeEnvite = enviteViewModel.getHomeEnviteById(enviteId);
        if(homeEnvite == null){
            getActivity().onBackPressed();
            return;
        }

        singleEnvitePostedByContainer.setVisibility(View.VISIBLE);
        singleEnviteButton.setVisibility(View.VISIBLE);
        singleEnviteButton.setEnabled(false);
        singleEnviteButton.setText(homeEnvite.getStatus());
        singleEnviteTitleTextView.setText(homeEnvite.getTitle());
        singleEnvitePriceTextView.setText(homeEnvite.getFormattedPrice());
        singleEnviteNoteTextView.setText(homeEnvite.getNote());
        singleEnviteLocationTextView.setText(homeEnvite.getLocation());
        singleEnvitePostedByTextView.setText(homeEnvite.getCreatedByUser().getFullName());
        if(uid.equals(homeEnvite.getCreatedBy())){
            singleEnviteButton.setVisibility(View.GONE);
            singleEnviteButton.setEnabled(false);
        }
        if(homeEnvite.getStatus().equals("IDLE")){
            singleEnviteButton.setEnabled(true);
            singleEnviteButton.setText("Request");
            singleEnviteButton.setBackgroundColor(getResources().getColor(R.color.quantum_googgreen800));
            handleRequestEnviteClick();
        }
        singleEnvitePostedByTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("enviteId", enviteId);
                bundle.putString("tag", tag);
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_singleEnviteFragment_to_singleProfileFragment3, bundle);
            }
        });
        isLoadingLiveData.setValue(false);
    }

    private void handleRequestEnviteClick () {
        singleEnviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRequestingLiveData.setValue(true);
                enviteViewModel.requestEnvite(enviteId, new VolleyCallbackForAdapters() {
                    @Override
                    public void onSuccess(String status) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Envite request sent successfully", Snackbar.LENGTH_LONG).show();
                        singleEnviteButton.setEnabled(false);
                        singleEnviteButton.setText("Pending");
                        singleEnviteButton.setBackgroundColor(getResources().getColor(R.color.transparent));
                        isRequestingLiveData.setValue(false);
                    }

                    @Override
                    public void onError(String message, String type, String status) {
                        if(type.equals("FORBIDDEN")){
                            ((MainActivity)getActivity()).goToSignIn("Please login to continue");
                            return;
                        }
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                message, Snackbar.LENGTH_LONG).show();
                        isRequestingLiveData.setValue(false);
                    }
                });
            }
        });
    }

    private void handleRequestButtonLoadingState () {
        isRequestingLiveData.observe(this, isRequesting -> {
            if(isRequesting){
                singleEnviteButton.setText("Please wait...");
                singleEnviteButton.setEnabled(false);
                return;
            }

        });

    }



}