package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */

import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Person;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
    private View fragmentView = null;
    private Person mPerson = null;
    public static final String PERSON_TO_SHOW = "person_to_show";


    public ProfileFragment(  ) {

    }

    public static ProfileFragment newInstance(Person personToShow) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PERSON_TO_SHOW, (java.io.Serializable) personToShow);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        fragmentView = rootView;

        Bundle args = getArguments();
        mPerson = (Person) args.getSerializable(PERSON_TO_SHOW);

        showProfile();

        return rootView;
    }

    private void showProfile() {
        if ( fragmentView == null ) {
            return;
        }

        if (mPerson == null) {
            //TODO: possibly try to restart here
            TextView textView = (TextView) fragmentView.findViewById(R.id.name);
            textView.setText("Unable to load profile.");
        } else {
            TextView textView = (TextView) fragmentView.findViewById(R.id.name);
            textView.setText("Hello " + mPerson.getFirst_name() +
                    " " + mPerson.getLast_name());
            Log.i("showProfile", mPerson.toString());
        }
    }
}