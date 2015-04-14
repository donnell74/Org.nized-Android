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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
    private Button editBtn;
    private Button saveBtn;
    private EditText emailET;
    private Spinner localPaidSpinner;
    private EditText nameET;
    private CheckBox memberCheckBox;
    private LinearLayout memberLL;
    private LinearLayout localPaidLL;
    private LinearLayout emailLL;
    private LinearLayout nameLL;


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

        initViewVars();
        populateSpinner();
        setOnClickHandlers();
        getUpdatedProfile();
        showProfile();

        return rootView;
    }

    private void populateSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.localPaid_spinner_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        localPaidSpinner.setAdapter(adapter);
    }

    public void initViewVars() {
        // TextViews
        nameTv = (TextView) fragmentView.findViewById(R.id.nameTV);
        emailTV = (TextView) fragmentView.findViewById(R.id.emailTV);
        localPaidTV = (TextView) fragmentView.findViewById(R.id.localPaidTV);
        memberTv = (TextView) fragmentView.findViewById(R.id.memberTV);

        // Edit Views
        nameET = (EditText) fragmentView.findViewById(R.id.nameET);
        emailET = (EditText) fragmentView.findViewById(R.id.emailET);

        // Spinners
        localPaidSpinner = (Spinner) fragmentView.findViewById(R.id.localPaidSpinner);

        // Checkboxes
        memberCheckBox = (CheckBox) fragmentView.findViewById(R.id.memberCheckbox);

        // Linear Views
        nameLL = (LinearLayout) fragmentView.findViewById(R.id.nameEditLL);
        emailLL = (LinearLayout) fragmentView.findViewById(R.id.emailEditLL);
        localPaidLL = (LinearLayout) fragmentView.findViewById(R.id.localPaidEditLL);
        memberLL = (LinearLayout) fragmentView.findViewById(R.id.memberEditLL);

        // Buttons
        editBtn = (Button) fragmentView.findViewById(R.id.edit);
        saveBtn = (Button) fragmentView.findViewById(R.id.save);
    }

    private void setOnClickHandlers() {
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEditView();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    private void save() {
        // gather data and save locally
        if ( mPerson.setFullName(nameET.getText().toString()) == -1) {
            Toast.makeText(getActivity(),
                           "Name field cannot be blank",
                           Toast.LENGTH_SHORT).show();
        }

        String oldEmail = mPerson.getEmail();
        mPerson.setEmail(emailET.getText().toString());
        mPerson.setIs_local_paid(Person.localPaidEnum.valueOf(
                localPaidSpinner.getSelectedItem().toString().toUpperCase()
        ));
        mPerson.setIs_member(memberCheckBox.isChecked());

        // save globally
        sendUpdatedProfile(oldEmail);

        update();
        toggleEditView();
    }

    private void sendUpdatedProfile(String oldEmail) {
        RequestParams requestParams = new RequestParams();
        if ( mPerson != null ) {
            requestParams = mPerson.getUpdateParams();
        } else {
            return;
        }

        String url = APIWrapper.UPDATE_PERSON + oldEmail;
        APIWrapper.post(url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject person) {
                // If the response is JSONObject instead of expected JSONArray
                mPerson = (Person) APIWrapper.parseJSONOjbect(
                        person,
                        Person.class);

                update();
                Toast.makeText(getActivity().getApplicationContext(),
                        "Profile saved online",
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
                            "Profile saved online",
                            Toast.LENGTH_SHORT)
                            .show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Unable to save data online.",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Unable to save data online.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void toggleEditView() {
        // switch visibilities
        int editState;
        int viewState;

        if ( nameTv.getVisibility() == View.VISIBLE ) {
            editState = View.VISIBLE;
            viewState = View.GONE;
            updateEditTexts();
        } else {
            editState = View.GONE;
            viewState = View.VISIBLE;
        }

        // edit views
        nameLL.setVisibility(editState);
        emailLL.setVisibility(editState);
        localPaidLL.setVisibility(editState);
        memberLL.setVisibility(editState);
        saveBtn.setVisibility(editState);

        // view views
        nameTv.setVisibility(viewState);
        emailTV.setVisibility(viewState);
        memberTv.setVisibility(viewState);
        localPaidTV.setVisibility(viewState);
        editBtn.setVisibility(viewState);
    }

    private void updateEditTexts() {
        String[] localPaidStrings = fragmentView.getResources().getStringArray(R.array.localPaid_spinner_options);
        String localPaid = mPerson.getIs_local_paid();

        for ( int position = 0; position < localPaidStrings.length; position++ ) {
            if ( localPaidStrings[position].toUpperCase().equals(localPaid) ) {
                localPaidSpinner.setSelection(position);
            }
        }

        nameET.setText(mPerson.toString());
        emailET.setText(mPerson.getEmail());
        memberCheckBox.setChecked(mPerson.getIs_member());
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
            nameTv.setText(mPerson.toString());
            emailTV.setText(mPerson.getEmail());
            localPaidTV.setText(mPerson.getIs_local_paid_Str());
            memberTv.setText(mPerson.getIs_member() ? "Is member" : "Is not member");
        }
    }
}