package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */

import android.graphics.drawable.ColorDrawable;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.ClassBonus;
import android.nized.org.domain.Permission;
import android.nized.org.domain.Person;
import android.nized.org.domain.Role;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {
    public static boolean isLoggedInPerson;
    private View fragmentView = null;
    public static Person mPerson = null;
    public static final String PERSON_TO_SHOW = "person_to_show";
    private TextView nameTv;
    private TextView emailTV;
    private TextView localPaidTV;
    private TextView memberTv;
    private Button editBtn;
    private Button saveBtn;
    private Button changePasswordBtn;
    private EditText emailET;
    private Spinner localPaidSpinner;
    private EditText nameET;
    private CheckBox memberCheckBox;
    private LinearLayout editLL;
    private LinearLayout viewLL;
    private EditText mobileET;
    private TextView mobileTV;
    private Button plusClassBonusBtn;
    private Button toggleClassBonusBtn;
    private Button minusClassBonusBtn;
    private ListView classBonuses;
    private ArrayList<ClassBonus> selectedClassBonuses = new ArrayList<ClassBonus>();
    private List<ClassBonus> mClassBonusArrayList;
    private ArrayAdapter classBonusArrayAdapter;
    private List<String> mRolesArrayList;
    private ArrayAdapter mRolesArrayAdapter;
    private ListView rolesListView;
    private Button toggleRoleBtn;
    private Button minusRoleBtn;
    private Button plusRoleBtn;
    private ArrayList<Integer> selectedRoles = new ArrayList<>();
    private LinearLayout roleMenu;
    private Permission personPermission;
    private TextView localPaidEditTV;
    private TextView memberEditTv;

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
        populateLists();
        setOnClickHandlers();
        hideBasedOnPermissions();
        getUpdatedProfile();
        showProfile();

        return rootView;
    }

    private void hideBasedOnPermissions() {
        personPermission = APIWrapper.getPermission("person");
        if ( isLoggedInPerson ) { // in rare chance you can edit others and not self
            // (model person -> self)
            if (!personPermission.getOther()) {
                roleMenu.setVisibility(View.GONE);
            }
        }

        // other shouldn't happen because it can not be reached
    }

    private void populateLists() {
        if ( mPerson == null ) {
            return;
        }

        // Construct the data source
        mClassBonusArrayList = mPerson.get_class_bonuses();
        // Create the adapter to convert the array to views
        classBonusArrayAdapter = (ArrayAdapter) new ClassBonusesAdapter(getActivity(), mClassBonusArrayList);
        // Attach the adapter to a ListView0
        classBonuses.setAdapter(classBonusArrayAdapter);

        mRolesArrayList = mPerson.get_roles_safe();
        for ( String each : mRolesArrayList ) {
            Log.i("each", each);
        }

        mRolesArrayAdapter = (ArrayAdapter) new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mRolesArrayList);
        rolesListView.setAdapter(mRolesArrayAdapter);
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

    private void initViewVars() {
        // TextViews
        nameTv = (TextView) fragmentView.findViewById(R.id.nameTV);
        emailTV = (TextView) fragmentView.findViewById(R.id.emailTV);
        localPaidTV = (TextView) fragmentView.findViewById(R.id.localPaidTV);
        localPaidEditTV = (TextView) fragmentView.findViewById(R.id.localPaidEditTV);
        memberTv = (TextView) fragmentView.findViewById(R.id.memberTV);
        memberEditTv = (TextView) fragmentView.findViewById(R.id.memberEditTV);
        mobileTV = (TextView) fragmentView.findViewById(R.id.mobileTV);

        // Edit Views
        nameET = (EditText) fragmentView.findViewById(R.id.nameET);
        emailET = (EditText) fragmentView.findViewById(R.id.emailET);
        mobileET = (EditText) fragmentView.findViewById(R.id.mobileET);

        // Spinners
        localPaidSpinner = (Spinner) fragmentView.findViewById(R.id.localPaidSpinner);

        // Checkboxes
        memberCheckBox = (CheckBox) fragmentView.findViewById(R.id.memberCheckbox);

        // Linear Views
        editLL = (LinearLayout) fragmentView.findViewById(R.id.editLL);
        viewLL = (LinearLayout) fragmentView.findViewById(R.id.viewLL);
        roleMenu = (LinearLayout) fragmentView.findViewById(R.id.roleMenu);

        // Buttons
        editBtn = (Button) fragmentView.findViewById(R.id.edit);
        saveBtn = (Button) fragmentView.findViewById(R.id.save);
        changePasswordBtn = (Button) fragmentView.findViewById(R.id.changePassword);
        plusClassBonusBtn = (Button) fragmentView.findViewById(R.id.plusClassBonus);
        minusClassBonusBtn = (Button) fragmentView.findViewById(R.id.minusClassBonus);
        toggleClassBonusBtn = (Button) fragmentView.findViewById(R.id.toggleClassBonuses);
        plusRoleBtn = (Button) fragmentView.findViewById(R.id.plusRole);
        minusRoleBtn = (Button) fragmentView.findViewById(R.id.minusRole);
        toggleRoleBtn = (Button) fragmentView.findViewById(R.id.toggleRoles);

        // List Views
        classBonuses = (ListView) fragmentView.findViewById(R.id.classBonuses);
        rolesListView = (ListView) fragmentView.findViewById(R.id.roles);
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
        plusClassBonusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClassBonus();
            }
        });
        minusClassBonusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeClassBonus();
            }
        });
        toggleClassBonusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleClassBonuses();
            }
        });
        classBonuses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                classBonusesOnClick(view, position);
            }
        });
        plusRoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRole();
            }
        });
        minusRoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeRole();
            }
        });
        toggleRoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleRoles();
            }
        });
        rolesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                rolesOnClick(view, position);
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( mPerson == null ) {
                    return;
                }

                ChangePasswordDialogFragment changePasswordDialogFragment = new ChangePasswordDialogFragment();

                changePasswordDialogFragment.show(getFragmentManager(), "changePassword");
            }
        });
    }

    private void rolesOnClick(View view, int position) {
        Role role = mPerson.getRoles().get(position);
        ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
        if ( colorDrawable == null ) {
            view.setBackgroundColor(view.getResources().getColor(R.color.background_material_light));
            colorDrawable = (ColorDrawable) view.getBackground();
        }

        int selectedColor = getResources().getColor(R.color.background_material_dark);
        if ( colorDrawable.getColor() == selectedColor ) {
            selectedRoles.remove(position);
            view.setBackgroundColor(getResources().getColor(R.color.background_material_light));
        } else {
            view.setBackgroundColor(selectedColor);
            selectedRoles.add(position);
        }
    }

    private void toggleRoles() {
        if ( rolesListView.getVisibility() == View.VISIBLE) {
            rolesListView.setVisibility(View.GONE);
            toggleRoleBtn.setText(R.string.showRoles);
        } else {
            rolesListView.setVisibility(View.VISIBLE);
            toggleRoleBtn.setText(R.string.hideRoles);
        }
    }

    private void removeRole() {
        if ( mPerson == null ) {
            return;
        }

        List<Integer> idsToDelete = new ArrayList<Integer>();
        for ( Integer eachPos : selectedRoles) {
            idsToDelete.add(mPerson.getRoles().get(eachPos).getId());
        }

        deleteRoles(idsToDelete);
        selectedRoles.clear();
    }

    private void deleteRoles(List<Integer> idsToDelete) {
        RequestParams requestParams = new RequestParams();
        for ( Integer eachId : idsToDelete ) {
            requestParams.put("id", eachId);
        }

        APIWrapper.post(APIWrapper.DELETE_PERSON_ROLE, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject obj) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Roles deleted",
                        Toast.LENGTH_SHORT)
                        .show();

                getUpdatedProfile();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Roles deleted",
                        Toast.LENGTH_SHORT)
                        .show();

                getUpdatedProfile();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(String.valueOf(statusCode), responseString);
                Toast.makeText(getActivity().getApplicationContext(),
                        "Unable to delete roles",
                        Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                Log.i("Failure", String.valueOf(statusCode));
            }
        });
    }

    private void addRole() {
        if ( mPerson == null ) {
            return;
        }

        RolesDialogFragment rolesDialogFragment = new RolesDialogFragment();

        rolesDialogFragment.show(getFragmentManager(), "roles");
    }

    private void classBonusesOnClick(View view, int position) {
        ClassBonus classBonus = (ClassBonus) classBonuses.getAdapter().getItem(position);
        ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();

        int selectedColor = getResources().getColor(R.color.background_material_dark);
        if ( colorDrawable.getColor() == selectedColor ) {
            selectedClassBonuses.remove(classBonus);
            view.setBackgroundColor(getResources().getColor(R.color.background_material_light));
        } else {
            selectedClassBonuses.add(classBonus);
            view.setBackgroundColor(selectedColor);
        }
    }

    private void removeClassBonus() {
        if ( mPerson == null ) {
            return;
        }

        ClassBonusesAdapter classBonusesAdapter = (ClassBonusesAdapter) classBonuses.getAdapter();
        List<Integer> idsToDelete = new ArrayList<Integer>();
        for ( ClassBonus eachBonus : selectedClassBonuses) {
            classBonusesAdapter.remove(eachBonus);
            idsToDelete.add(eachBonus.getClass_bonus_id());
        }

        classBonusesAdapter.notifyDataSetChanged();
        deleteClassBonuses(idsToDelete);
        selectedClassBonuses.clear();
    }

    private void addClassBonus() {
        if ( mPerson == null ) {
            return;
        }

        ClassBonusDialogFragment classBonusDialogFragment = new ClassBonusDialogFragment();

        classBonusDialogFragment.show(getFragmentManager(), "classBonus");
    }

    private void toggleClassBonuses() {
        if ( classBonuses.getVisibility() == View.VISIBLE) {
            classBonuses.setVisibility(View.GONE);
            toggleClassBonusBtn.setText(R.string.showClassBonuses);
        } else {
            classBonuses.setVisibility(View.VISIBLE);
            toggleClassBonusBtn.setText(R.string.hideClassBonuses);
        }
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
        mPerson.setMobile_number(mobileET.getText().toString());
        if ( personPermission.getOther() ) {
            mPerson.setIs_local_paid(Person.localPaidEnum.valueOf(
                    localPaidSpinner.getSelectedItem().toString().toUpperCase()
            ));

            Boolean memberVal = memberCheckBox.isChecked();
            if ( mPerson.getIs_member() != memberVal ) {
                mPerson.setIs_member(memberVal);
                int oldRoleId = (! memberVal) ? APIWrapper.MEMBER_ROLE_ID : APIWrapper.NONMEMBER_ROLE_ID;
                int newRoleId = (memberVal) ? APIWrapper.MEMBER_ROLE_ID : APIWrapper.NONMEMBER_ROLE_ID;
                for ( Role eachRole : mPerson.getRoles() ) {
                    if ( eachRole.getRole_id() == oldRoleId ) {
                        eachRole.setRole_id(newRoleId);
                        updateRole(eachRole, oldEmail);
                    }
                }
            }
        }

        toggleEditView();
    }

    private void updateRole(Role role, final String oldEmail) {
        RequestParams requestParams = role.getPersonRoleRequestParams();

        APIWrapper.post(APIWrapper.UPDATE_PERSON_ROLE + role.getId(),
                requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                // save globally
                sendUpdatedProfile(oldEmail);

                update();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                // save globally
                sendUpdatedProfile(oldEmail);

                update();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(String.valueOf(statusCode), responseString);
            }
        });
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
                getUpdatedProfile();

                Toast.makeText(getActivity().getApplicationContext(),
                        "Profile saved online",
                        Toast.LENGTH_SHORT)
                        .show();

                if ( isLoggedInPerson ) {
                    APIWrapper.setLoggedInPerson(mPerson);
                    APIWrapper.getPermissions();
                    hideBasedOnPermissions();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                // Pull out the first one
                getUpdatedProfile();

                Toast.makeText(getActivity().getApplicationContext(),
                        "Profile saved online",
                        Toast.LENGTH_SHORT)
                        .show();

                if ( isLoggedInPerson ) {
                    APIWrapper.setLoggedInPerson(mPerson);
                    APIWrapper.getPermissions();
                    hideBasedOnPermissions();
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

    public void deleteClassBonuses(List<Integer> idsToDelete) {
        RequestParams requestParams = new RequestParams();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(idsToDelete);
            requestParams.put("class_bonus_ids", json);
            Log.i("requestParams", requestParams.toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        APIWrapper.post(APIWrapper.DELETE_PERSON_CLASS_BONUSES, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Class bonuses deleted",
                        Toast.LENGTH_SHORT)
                        .show();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(String.valueOf(statusCode), responseString);
                Toast.makeText(getActivity().getApplicationContext(),
                        "Unable to delete class bonuses",
                        Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                Log.i("Failure", String.valueOf(statusCode));
            }
        });
    }

    private void toggleEditView() {
        // switch visibilities
        int editState;
        int viewState;

        if ( viewLL.getVisibility() == View.VISIBLE ) {
            editState = View.VISIBLE;
            viewState = View.GONE;
            updateEditTexts();
        } else {
            editState = View.GONE;
            viewState = View.VISIBLE;
        }

        editLL.setVisibility(editState);
        viewLL.setVisibility(viewState);
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
        mobileET.setText(mPerson.getMobile_number());
        memberCheckBox.setChecked(mPerson.getIs_member());

        if ( ! personPermission.getOther() ) {
            localPaidEditTV.setVisibility(View.GONE);
            localPaidSpinner.setVisibility(View.GONE);
            memberCheckBox.setVisibility(View.GONE);
            memberEditTv.setVisibility(View.GONE);
        }
    }

    public void getUpdatedProfile() {
        RequestParams requestParams = new RequestParams();
        if ( mPerson != null ) {
            requestParams.put("email", mPerson.getEmail());
        } else {
            return;
        }

        APIWrapper.get(APIWrapper.FIND_PERSON, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject person) {
                // If the response is JSONObject instead of expected JSONArray
                mPerson = (Person) APIWrapper.parseJSONOjbect(
                        person,
                        Person.class);

                update();
                Toast.makeText(getActivity().getApplicationContext(),
                        "Updated Profile",
                        Toast.LENGTH_SHORT)
                        .show();

                if ( isLoggedInPerson ) {
                    APIWrapper.setLoggedInPerson(mPerson);
                    APIWrapper.getPermissions();
                    hideBasedOnPermissions();
                }
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

                    if ( isLoggedInPerson ) {
                        APIWrapper.setLoggedInPerson(mPerson);
                        APIWrapper.getPermissions();
                        hideBasedOnPermissions();
                    }
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
            mobileTV.setText(mPerson.getMobile_number());
            populateLists();
        }
    }

}