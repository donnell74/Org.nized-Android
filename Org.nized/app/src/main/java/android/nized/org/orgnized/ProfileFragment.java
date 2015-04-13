package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */

import android.content.Intent;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Checkins;
import android.nized.org.domain.Person;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {
    private View fragmentView = null;
    private Person mPerson = null;
    public static final String PERSON_TO_SHOW = "person_to_show";
    private TextView nameTv;
    private TextView emailTV;
    private TextView localPaidTV;
    private TextView memberTv;


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

        if ( mPerson == null ) {
            Bundle args = getArguments();
            mPerson = (Person) args.getSerializable(PERSON_TO_SHOW);
        }

        getUpdatedProfile();
        showProfile();

        return rootView;
    }

    private void getUpdatedProfile() {
        RequestParams requestParams = new RequestParams();
        if ( mPerson != null ) {
            requestParams.put("email", mPerson.getEmail());
        } else {
            return;
        }

        APIWrapper.get(APIWrapper.FIND_PERSON, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject checkin) {
                // If the response is JSONObject instead of expected JSONArray
                mPerson = (Person) APIWrapper.parseJSONOjbect(
                        checkin,
                        Person.class);

                update();
                Toast.makeText(getActivity().getApplicationContext(),
                        "Updated Profile",
                        Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                // Pull out the first one
                try {
                    mPerson = (Person) APIWrapper.parseJSONOjbect(
                            all_objs.getJSONObject(0),
                            Person.class);

                    update();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Updated Profile",
                            Toast.LENGTH_SHORT)
                            .show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Unable to update data.",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Unable to update data.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

    }

    private void showProfile() {
        if ( fragmentView == null ) {
            return;
        }

        update();
    }

    public void update() {
        if (mPerson == null) {
            //TODO: possibly try to restart here
            TextView textView = (TextView) fragmentView.findViewById(R.id.nameTV);
            textView.setText("Unable to load profile.");
        } else if (mPerson.getFirst_name() == null) { // haven't been set yet
            TextView textView = (TextView) fragmentView.findViewById(R.id.nameTV);
            textView.setText("Loading profile...");
        } else {
            nameTv = (TextView) fragmentView.findViewById(R.id.nameTV);
            nameTv.setText(mPerson.toString());

            emailTV = (TextView) fragmentView.findViewById(R.id.emailTV);
            emailTV.setText(mPerson.getEmail());

            localPaidTV = (TextView) fragmentView.findViewById(R.id.localPaidTV);
            localPaidTV.setText(mPerson.getIs_local_paid());

            memberTv = (TextView) fragmentView.findViewById(R.id.memberTV);
            memberTv.setText(mPerson.isIs_member() ? "Is member" : "Is not member");
        }
    }
}