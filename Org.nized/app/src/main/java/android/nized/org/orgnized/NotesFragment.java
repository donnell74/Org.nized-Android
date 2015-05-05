package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Announcement;
import android.nized.org.domain.Note;
import android.nized.org.domain.Permission;
import android.nized.org.domain.Person;
import android.nized.org.domain.Role;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {
    View main_layout = null;
    ArrayList<String> notes = new ArrayList();
    ListView listView = null;
    private String mUrl = APIWrapper.FIND_NOTES;
    private DisplayNoteDetails mCallback;
    private ArrayList<Note> notesList = new ArrayList<>();
    private Permission notePermission = null;

    public NotesFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_layout = inflater.inflate(R.layout.fragment_announcements, container, false);

        listView = (ListView) main_layout.findViewById(R.id.announcementsListView);

        notePermission = APIWrapper.getPermission("notes");

        notes.add("Loading...");

        String personEmail = APIWrapper.getLoggedInPerson().getEmail();
        RequestParams requestParams = new RequestParams("person_email", personEmail);

        APIWrapper.get(mUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONArray all_objs) {
                notes.clear();
                notesList.clear();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mCallback.DisplayNoteDetails(notesList.get(i));
                    }
                });


                new AddAnnouncementsTask().execute(all_objs);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("Announcements get fail", responseString);
                Toast.makeText(getView().getContext(),
                        "Unable to gather people.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                main_layout.getContext(), android.R.layout.simple_list_item_1, notes);
        listView.setAdapter(adapter);

        return main_layout;
    }

    public class AddAnnouncementsTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object[] objects) {
            JSONArray all_objs = (JSONArray) objects[0];
            for (int i = 0; i < all_objs.length(); i++) {
                Note thisNote = null;
                try {
                    JSONObject objToParse = all_objs.getJSONObject(i);

                    thisNote = (Note) APIWrapper.parseJSONOjbect(objToParse,
                            Note.class);

                    if ( thisNote.getPublicToPerson() || notePermission.getOther() ) {
                        notes.add(thisNote.getTitle());
                        notesList.add(thisNote);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if ( notes.isEmpty() ) {
                notes.add("No notes data.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void Result) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    main_layout.getContext(), android.R.layout.simple_list_item_1, notes);
            listView.setAdapter(adapter);
        }
    }

    public interface DisplayNoteDetails{
        public void DisplayNoteDetails(Note note);
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (DisplayNoteDetails) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}