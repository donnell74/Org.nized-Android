package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */
import android.app.ActionBar;
import android.nized.org.api.APIUtilities;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Announcements_Roles;
import android.nized.org.domain.Note;
import android.nized.org.domain.Person;
import android.nized.org.domain.Surveys_Roles;
import android.util.Log;
import android.view.View;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class NotesFragment extends Fragment {
    private Person myPerson = null;
    private View notes_layout = null;

    public NotesFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);

        myPerson = APIWrapper.getLoggedInPerson();

        populateView();

        return rootView;
    }


    private void populateView() {
        RequestParams requestParams = new RequestParams("person_email", myPerson.getEmail());
        APIWrapper.get(APIWrapper.FIND_NOTES, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject note) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("home", note.toString());
                addNote(note);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray notes) {
                // Pull out the first one
                try {
                    Log.i("home array", notes.get(0).toString());
                    for (int i = 0; i < notes.length(); i++) {
                        addNote(notes.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Unable to gather data.",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("home failure", responseString);
                Toast.makeText(getActivity(),
                        "Unable to gather data.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }


    private void addNote(JSONObject obj) {
        notes_layout = getView();

        Note each_note = (Note) APIWrapper.parseJSONOjbect(obj, Note.class);
        TextView textViewNote = new TextView(getView().getContext());
        textViewNote.setText("Note: " + each_note.getTitle());
        textViewNote.setId(5);
        textViewNote.setLayoutParams(new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout linearLayoutHome = (LinearLayout)(notes_layout.findViewById(R.id.layout_notes));
        linearLayoutHome.addView(textViewNote);
    }
}