package android.nized.org.orgnized;

import android.app.ActionBar;
import android.nized.org.api.APIUtilities;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Announcement;
import android.nized.org.domain.Announcements_Roles;
import android.nized.org.domain.Answer;
import android.nized.org.domain.Checkins;
import android.nized.org.domain.Person;
import android.nized.org.domain.PossibleAnswer;
import android.nized.org.domain.Question;
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

    private class MyAPI extends APIUtilities {

        @Override
        public void addToView(JSONObject objToAdd, Class objClass) {
            Log.i("addToView", objClass.toString());
            if ( objClass == Surveys_Roles.class ) {
                Log.i("addToView", "Survey");
                addSurvey((Surveys_Roles) APIWrapper.parseJSONOjbect(objToAdd, objClass));
            }
        }
    }

    MyAPI myAPI = new MyAPI();

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        getPerson();

        RequestParams requestParams = new RequestParams();
        requestParams.put("question_id", 1);

        APIWrapper.get(APIWrapper.FIND_POSSIBLE_ANSWERS, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject announcement) {
                // If the response is JSONObject instead of expected JSONArray
                PossibleAnswer test = (PossibleAnswer) APIWrapper.parseJSONOjbect(announcement, PossibleAnswer.class);
                Log.i("test", test.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                // Pull out the first one
                try {
                    Log.i("test", all_objs.toString());
                    for (int i = 0; i < all_objs.length(); i++) {
                        PossibleAnswer test = (PossibleAnswer) APIWrapper.parseJSONOjbect(all_objs.getJSONObject(i), PossibleAnswer.class);
                        Log.i("test", test.toString());
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
                myPerson = (Person) APIWrapper.parseJSONOjbect(person, Person.class);
                APIWrapper.setLoggedInPerson(myPerson);
                setPersonAttributes();
                populateView();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray people) {
                // Pull out the first one
                try {
                    myPerson = (Person) APIWrapper.parseJSONOjbect((JSONObject) people.get(0), Person.class);
                    APIWrapper.setLoggedInPerson(myPerson);
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

    private void addSurvey(Surveys_Roles survey) {
        Log.i("addSurvey", survey.getSurvey_id().toString());
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
        RequestParams requestParams = new RequestParams();
        requestParams.put("email", "donnell74@live.missouristate.edu");

        APIWrapper.get(APIWrapper.FIND_ANNOUNCEMENTS, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject announcement) {
                // If the response is JSONObject instead of expected JSONArray
                APIWrapper.parseJSONOjbect(announcement, Announcement.class);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                // Pull out the first one
                try {
                    for (int i = 0; i < all_objs.length(); i++) {
                        Log.i("getForAllRoles", "test");
                        addAnnouncements((Announcement) APIWrapper.parseJSONOjbect(all_objs.getJSONObject(i), Announcement.class));
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


    private void getSurveys() {
        myAPI.getForAllRoles(APIWrapper.FIND_SURVEYS_ROLES, myPerson, Surveys_Roles.class);
    }


    private void setPersonAttributes() {
        home_layout = getView();

        // set email
        TextView textViewPersonName = (TextView) home_layout.findViewById(R.id.person_name);
        textViewPersonName.setText("Hello " + myPerson.getFirst_name());
    }


    private void addAnnouncements(Announcement announcement) {
        Log.i("addAnnouncements", announcement.toString());
        TextView textViewAnnouncement = new TextView(getView().getContext());
        textViewAnnouncement.setText("Announcement: " + announcement.getTitle());
        textViewAnnouncement.setId(5);
        textViewAnnouncement.setLayoutParams(new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout linearLayoutHome = (LinearLayout)(home_layout.findViewById(R.id.layout_home));
        linearLayoutHome.addView(textViewAnnouncement);
    }
}