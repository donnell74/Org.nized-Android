package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */

import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Person;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
    private View fragmentView = null;

    public ProfileFragment(  ) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        fragmentView = rootView;

        // get arguments
        Bundle args = getArguments();
        boolean showLastScanned = args.getBoolean("showLastScanned", false);

        showProfile(showLastScanned);

        return rootView;
    }

    private void showProfile(boolean showLastScanned) {
        if ( fragmentView == null ) {
            return;
        }

        if (showLastScanned) {
            // show last scanned
            Person lastScannedPerson = APIWrapper.getLastScannedPerson();
            if ( lastScannedPerson == null ) {
                return;
            } else {
                TextView textView = (TextView) fragmentView.findViewById(R.id.name);
                textView.setText("Hello " + lastScannedPerson.getFirst_name() +
                        " " + lastScannedPerson.getLast_name());
            }
        } else {
            // show currently logged in
            Person loggedInPerson = APIWrapper.getLoggedInPerson();
            if (loggedInPerson == null) {
                //TODO: possibly try to restart here
                TextView textView = (TextView) fragmentView.findViewById(R.id.name);
                textView.setText("Unable to load profile.");
            } else {
                TextView textView = (TextView) fragmentView.findViewById(R.id.name);
                textView.setText("Hello " + loggedInPerson.getFirst_name() +
                        " " + loggedInPerson.getLast_name());
            }
        }
    }
}