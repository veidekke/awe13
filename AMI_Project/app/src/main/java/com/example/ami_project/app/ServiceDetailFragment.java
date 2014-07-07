package com.example.ami_project.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.ami_project.app.dummy.DummyContent;

import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.Service;

/**
 * A fragment representing a single Service detail screen.
 * This fragment is either contained in a {@link ServiceListActivity}
 * in two-pane mode (on tablets) or a {@link ServiceDetailActivity}
 * on handsets.
 */
public class ServiceDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String SERVICE_ID = "service_id";
    /**
     * The dummy content this fragment is presenting.
     */
    private Service currentService;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ServiceDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(SERVICE_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            currentService = ServiceListFragment.listAdapter.getItem(getArguments().getInt(SERVICE_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_service_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (currentService != null) {
            String ActionString = getActions(currentService);
            ((TextView) rootView.findViewById(R.id.service_detail)).setText(ActionString);
        }

        return rootView;
    }

    private String getActions(Service service){
        String actionString = "";
        Action[] actions = service.getActions();
        for(int i = 0; i < actions.length; i++){
            if(i != 0)
                actionString += ", ";
            actionString += actions[i].getName();
        }

        return actionString;
    }
}
