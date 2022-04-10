package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.envite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;


public class EnviteFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_envite, container, false);

        // HANDLE SHOW NAVBAR
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_view);
        navBar.setVisibility(View.VISIBLE);

        NavHostFragment navHostFragment =
                (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.enviteFragmentContainer);
        NavController navController = navHostFragment.getNavController();

        TabLayout enviteTabLayout = (TabLayout) rootView.findViewById(R.id.enviteTabLayout);

        enviteTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        navController.navigate(R.id.action_receivedEnviteFragment2_to_sentEnviteFragment2);
                        break;
                    case 1:
                        navController.navigate(R.id.action_sentEnviteFragment2_to_receivedEnviteFragment2);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return rootView;
    }
}