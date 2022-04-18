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
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.envite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import activities.MainActivity;
import entities.Envite;
import entities.EnviteRequest;
import entities.User;
import interfaces.VolleyCallbackForEnviteDetails;
import viewmodels.EnviteViewModel;

public class SingleEnviteFragment extends Fragment {

    private String enviteId;
    private String tag;
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<Boolean>(true);
    private EnviteViewModel enviteViewModel;
    private TextView singleEnviteInfoBox;
    private ScrollView singleEnviteScrollView;
    private LinearLayout singleEnviteRequestedBy;
    private LinearLayout singleEnvitePostedBy;
    private Button singleEnviteButton;
    private TextView singleEnviteTitleTextView;
    private TextView singleEnviteLocationTextView;
    private TextView singleEnvitePriceTextView;
    private TextView singleEnviteNoteTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        enviteId = (String) arguments.get("enviteId");
        tag = (String) arguments.get("tag");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_single_envite, container, false);

        // INITIALIZE VIEWS
        initializeViews(rootView);

        // HANDLE HIDE NAVBAR
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_view);
        navBar.setVisibility(View.GONE);

        //HANDLE TOOLBAR
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.singleEnviteToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag == "my_envites"){
                    getActivity().onBackPressed();
                    return;
                }
//                NavController navController = Navigation.findNavController(v);
//
//                navController.navigate(R.id.action_singleEnviteFragment_to_homeFragment);
            }
        });

        // SETUP VIEW MODEL
        enviteViewModel = new ViewModelProvider(this).get(EnviteViewModel.class);

        // TODO - SHOW POSTED BY ACCEPTED STATE
        // TODO - SHOW POSTED BY NORMAL
        // TODO - SHOW REQUESTS BY
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        isLoadingLiveData.setValue(true);
        enviteViewModel.fetchEnviteDetails(enviteId, tag, new VolleyCallbackForEnviteDetails() {
            @Override
            public void onSuccess(String status, Envite envite, User createdByUser, EnviteRequest enviteRequest) {
                isLoadingLiveData.setValue(false);
                singleEnviteTitleTextView.setText(envite.getTitle());
                singleEnvitePriceTextView.setText(envite.getFormattedPrice());
                singleEnviteNoteTextView.setText(envite.getNote());
                singleEnviteLocationTextView.setText(envite.getLocation());
            }

            @Override
            public void onError(String message, String type, String status) {
                Log.i("MESSAGE", message);
                Log.i("TYPE", type);
                isLoadingLiveData.setValue(false);
                if(type.equals("FORBIDDEN")){
                    ((MainActivity)getActivity()).goToSignIn();
                    return;
                }
            }
        });

    }

    public void initializeViews (View view) {
        //INITIALIZE VIEWS
        singleEnviteInfoBox = view.findViewById(R.id.singleEnviteInfoTextView);
        singleEnviteScrollView = view.findViewById(R.id.singleEnviteScrollView);
        singleEnvitePostedBy = view.findViewById(R.id.singleEnvitePostedByContainer);
        singleEnviteRequestedBy = view.findViewById(R.id.singleEnviteRequestedByContainer);
        singleEnviteButton = view.findViewById(R.id.singleEnviteButton);
        singleEnviteTitleTextView = view.findViewById(R.id.singleEnviteTitleTextView);
        singleEnvitePriceTextView = view.findViewById(R.id.singleEnvitePriceTextView);
        singleEnviteLocationTextView = view.findViewById(R.id.singleEnviteLocationTextView);
        singleEnviteNoteTextView = view.findViewById(R.id.singleEnviteNoteTextView);

        if(tag == "my_envites"){
            singleEnviteRequestedBy.setVisibility(View.GONE);
            singleEnvitePostedBy.setVisibility(View.GONE);
            singleEnviteButton.setVisibility(View.GONE);
        }
    }

}