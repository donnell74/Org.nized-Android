package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */

import android.app.Activity;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Announcement;
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
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class AnnouncementsFragment extends Fragment {

    View main_layout = null;
    ArrayList<String> announcements = new ArrayList();
    ListView listView = null;
    private String mUrl = APIWrapper.FIND_ANNOUNCEMENTS_ROLES + "?";
    private DisplayAnnouncementDetails mCallback;
    private ArrayList<Announcement> announcementsList = new ArrayList<>();

    public AnnouncementsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_layout = inflater.inflate(R.layout.fragment_announcements, container, false);

        listView = (ListView) main_layout.findViewById(R.id.announcementsListView);

        announcements.add("Loading...");

        List<Role> loggedInRoles = APIWrapper.getLoggedInPerson().getRoles();
        if ( loggedInRoles == null ) {
            loggedInRoles = new ArrayList<>();
            mUrl += "role_id=-1";
        } else {
            for (Role eachRole : loggedInRoles) {
                mUrl += "role_id=" + eachRole.getRole_id() + "&";
            }
        }

        Log.i("mUrl", mUrl);
        APIWrapper.get(mUrl, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONArray all_objs) {
                announcements.clear();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mCallback.DisplayAnnouncementDetails(announcementsList.get(i));
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
                main_layout.getContext(), android.R.layout.simple_list_item_1, announcements);
        listView.setAdapter(adapter);

        return main_layout;
    }

    public class AddAnnouncementsTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object[] objects) {
            JSONArray all_objs = (JSONArray) objects[0];
            for (int i = 0; i < all_objs.length(); i++) {
                Announcement thisAnnouncement = null;
                try {
                    JSONObject objToParse = all_objs.getJSONObject(i).getJSONObject("announcement_id");

                    thisAnnouncement = (Announcement) APIWrapper.parseJSONOjbect(objToParse,
                            Announcement.class);

                    if ( thisAnnouncement.getEnd_date().after(new java.util.Date())) {
                        announcements.add(thisAnnouncement.getTitle());
                        announcementsList.add(thisAnnouncement);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if ( announcements.isEmpty() ) {
                announcements.add("No announcement data.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void Result) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    main_layout.getContext(), android.R.layout.simple_list_item_1, announcements);
            listView.setAdapter(adapter);
        }
    }

    public interface DisplayAnnouncementDetails{
        public void DisplayAnnouncementDetails(Announcement announcement);
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (DisplayAnnouncementDetails) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

}