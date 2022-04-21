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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.envite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import entities.MyEnvite;
import entities.ReceivedRequest;
import entities.SentRequest;
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
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.singleEnviteToolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( tag == "received_envites"){
                    Bundle bundle = new Bundle();
                    bundle.putString("requestId", requestId);
                    bundle.putString("tag", tag);
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_singleEnviteFragment2_to_singleProfileFragment, bundle);
                    return;
                }
                if(tag == "sent_envites"){
                    Bundle bundle = new Bundle();
                    bundle.putString("requestId", requestId);
                    bundle.putString("tag", tag);
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_singleEnviteFragment2_to_singleProfileFragment, bundle);
                    return;
                }
                getActivity().onBackPressed();

            }
        });

        // SETUP VIEW MODEL
        enviteViewModel = new ViewModelProvider(this).get(EnviteViewModel.class);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleLoadingState();
        if(tag == "my_envites"){
            handleMyEnviteView();
        }
        if(tag == "received_envites"){
            handleReceivedRequestsView();
        }
        if(tag == "sent_envites"){
            handleSentRequestsView();
        }

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
        isLoadingLiveData.setValue(false);
    }

}