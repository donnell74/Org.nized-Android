package android.nized.org.orgnized;

import android.app.ActionBar;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Announcement;
import android.nized.org.domain.Announcements_Roles;
import android.nized.org.domain.Person;
import android.nized.org.domain.Role;
import android.nized.org.domain.Survey;
import android.nized.org.domain.Surveys_Roles;
import android.util.Log;
import android.view.View;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.concurrent.Callable;

public class HomeFragment extends Fragment {
    Person myPerson = null;
    View home_layout = null;
    int viewIdCount = 5;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        getPerson();

        return rootView;
    }


    private void populateView() {
        getAnnouncements();
        getSurveys();
    }


    private void getPerson() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("email", "donnell74@live.missouristate.edu");

        APIWrapper.get(APIWrapper.FIND_PERSON, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject person) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("home", person.toString());
                myPerson = (Person) APIWrapper.parseJSONOjbect(person, Person.class);
                setPersonAttributes();
                populateView();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray people) {
                // Pull out the first one
                try {
                    Log.i("home array", people.get(0).toString());
                    myPerson = (Person) APIWrapper.parseJSONOjbect((JSONObject) people.get(0), Person.class);
                    setPersonAttributes();
                    populateView();
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


    private void getForAllRoles(String url, final Class objClass) {
        for (Iterator<Role> role = myPerson.getRoles().iterator(); role.hasNext(); ) {
            RequestParams requestParams = new RequestParams("role_id", role.next().getRole_id());
            APIWrapper.get(url, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject obj) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.i("home", obj.toString());
                    addToView(obj, objClass);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                    // Pull out the first one
                    try {
                        for (int i = 0; i < all_objs.length(); i++) {
                            Log.i("home", all_objs.getJSONObject(i).toString());
                            addToView(all_objs.getJSONObject(i), objClass);
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
    }


    private void addToView(JSONObject objToAdd, Class objClass) {
        if ( objClass == Announcements_Roles.class ) {
            Log.i("addToView", "Announcements_Roles");
            addAnnouncements((Announcements_Roles) APIWrapper.parseJSONOjbect(objToAdd, objClass));
        } else if ( objClass == Surveys_Roles.class ) {
            Log.i("addToView", "Survey");
            addSurvey((Surveys_Roles) APIWrapper.parseJSONOjbect(objToAdd, objClass));
        }
    }

    private void addSurvey(Surveys_Roles survey) {
        TextView textViewSurvey = new TextView(getView().getContext());
        textViewSurvey.setText("Survey: " + survey.getSurvey_id().getName());
        textViewSurvey.setId(5);
        textViewSurvey.setLayoutParams(new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout linearLayoutHome = (LinearLayout)(home_layout.findViewById(R.id.layout_home));
        linearLayoutHome.addView(textViewSurvey);
    }


    private void getAnnouncements() {
        getForAllRoles(APIWrapper.FIND_ANNOUNCEMENTS_ROLES, Announcements_Roles.class);
    }


    private void getSurveys() {
        getForAllRoles(APIWrapper.FIND_SURVEYS_ROLES, Surveys_Roles.class);
    }


    private void setPersonAttributes() {
        home_layout = getView();

        // set email
        TextView textViewPersonName = (TextView) home_layout.findViewById(R.id.person_name);
        textViewPersonName.setText("Hello " + myPerson.getFirst_name());
    }


    private void addAnnouncements(Announcements_Roles announcement) {
        TextView textViewAnnouncement = new TextView(getView().getContext());
        textViewAnnouncement.setText("Announcement: " + announcement.getAnnouncement_id().getTitle());
        textViewAnnouncement.setId(5);
        textViewAnnouncement.setLayoutParams(new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout linearLayoutHome = (LinearLayout)(home_layout.findViewById(R.id.layout_home));
        linearLayoutHome.addView(textViewAnnouncement);
    }
}