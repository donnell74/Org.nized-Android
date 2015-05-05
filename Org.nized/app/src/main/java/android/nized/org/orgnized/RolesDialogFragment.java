package android.nized.org.orgnized;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.ClassBonus;
import android.nized.org.domain.Person;
import android.nized.org.domain.Role;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by greg on 4/22/15.
 */
public class RolesDialogFragment extends DialogFragment {
    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    final ArrayList<String> roles = new ArrayList<>();
    private ArrayList<Role> roleObjs = new ArrayList<>();

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, Role role);
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_roles, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(layout)
                // Add action buttons
                .setPositiveButton(R.string.submit_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        int spinnerIndex = ((Spinner) layout.findViewById(R.id.rolesSpinner)).getSelectedItemPosition();
                        int selectedId = roleObjs.get(spinnerIndex).getId();
                        String selectedName = roleObjs.get(spinnerIndex).getName();
                        Role role = new Role();
                        role.setEmail(ProfileFragment.mPerson.getEmail());
                        role.setId(selectedId);
                        List<String> personRoles = ProfileFragment.mPerson.get_roles();
                        for ( String eachRole : personRoles ) {
                            Log.i("role cmp", eachRole + ":" + selectedName);
                            if ( eachRole.equals(selectedName) ) {
                                RolesDialogFragment.this.getDialog().cancel();
                                return;
                            }
                        }

                        mListener.onDialogPositiveClick(RolesDialogFragment.this, role);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RolesDialogFragment.this.getDialog().cancel();
                    }
                });

        final Spinner spinner = (Spinner) layout.findViewById(R.id.rolesSpinner);
        APIWrapper.post(APIWrapper.FIND_ROLES, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject person) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                for (int i = 0; i < all_objs.length(); i++) {
                    try {
                        Role eachRole = (Role) APIWrapper.parseJSONOjbect(all_objs.getJSONObject(i), Role.class);
                        roles.add(eachRole.getName());
                        roleObjs.add(eachRole);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                            android.R.layout.simple_spinner_item,
                            roles);
                    spinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }
        });

        return builder.create();
    }
}