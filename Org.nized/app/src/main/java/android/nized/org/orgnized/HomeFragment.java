package android.nized.org.orgnized;

import android.app.ActionBar;
import android.nized.org.api.APIUtilities;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Announcement;
import android.nized.org.domain.Announcements_Roles;
import android.nized.org.domain.Checkins;
import android.nized.org.domain.Person;
import android.nized.org.domain.Survey;
import android.nized.org.domain.Surveys_Roles;
import android.os.Bundle;
import android.os.Debug;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class HomeFragment extends Fragment {
    Person myPerson = null;
    View home_layout = null;

    private class MyAPI extends APIUtilities {

        @Override
        public void addToView(JSONObject objToAdd, Class objClass) {
            Log.i("addToView", objClass.toString());
            if ( objClass == Survey.class ) {
                Log.i("addToView", "Survey");
                addSurvey((Survey) APIWrapper.parseJSONOjbect(objToAdd, objClass));
            } else if ( objClass == Announcements_Roles.class ) {
                Log.i("addToView", "Announcements");
                addAnnouncements((Announcement) APIWrapper.parseJSONOjbect(objToAdd, objClass));
            }
        }
    }

    MyAPI myAPI = new MyAPI();

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        home_layout = rootView;
        myPerson = APIWrapper.getLoggedInPerson();
        Log.e("myperson", myPerson.toString());

        // setup view using myPerson
        setPersonAttributes();
        populateView();

        return rootView;
    }


    private boolean acquireView() {
        if ( home_layout != null )
        {
            return true;
        }

        home_layout = getView();
        if ( home_layout != null )
        {
            return true;
        }

        return false;
    }


    private void populateView() {
        getAnnouncements();
        getSurveys();
        getAttendances();
    }


    private void getAttendances() {
        APIWrapper.get(APIWrapper.GET_TODAYS_ATTENDANCE, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject attendance) {
                addAttendances(attendance);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("check in person failure", responseString);
                Toast.makeText(getView().getContext(),
                        "Unable to gather attendance data.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }


    private void addTextView(String text, int id) {
        addTextView(text, id, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }


    private void addTextView(String text, int id, ActionBar.LayoutParams params) {
        TextView textViewSurvey = new TextView(home_layout.getContext());
        textViewSurvey.setText(text);
        textViewSurvey.setId(5);
        textViewSurvey.setTextSize(16);
        textViewSurvey.setLayoutParams(params);

        LinearLayout linearLayoutHome = (LinearLayout) (home_layout.findViewById(id));
        linearLayoutHome.addView(textViewSurvey);
    }


    private TextView createTextView(String text, ViewGroup.LayoutParams params, int gravity) {
        TextView textView = new TextView(home_layout.getContext());
        textView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setId(5);
        textView.setTextSize(16);
        textView.setSingleLine(true);
        textView.setGravity(gravity);

        return textView;
    }



    private void addRow(String leftText, String rightText, int id)
    {
        LinearLayout row = new LinearLayout(home_layout.getContext());

        // add textLeft to tableRow
        TextView textViewLeft = createTextView(leftText, new TableLayout.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                .25f
        ), Gravity.LEFT);
        row.addView(textViewLeft);

        // add textRight to tableRow
        TextView textViewRight = createTextView(rightText, new TableLayout.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                0.75f
        ), Gravity.RIGHT);
        row.addView(textViewRight);

        // add tablerow to tablelayout
        LinearLayout linearLayout = (LinearLayout) home_layout.findViewById(id);
        linearLayout.addView(row);
    }


    private void addAttendances(JSONObject attendance) {
        if ( acquireView() ) {
            try {
                addRow("Total:   ", String.valueOf(attendance.getInt("total")), R.id.attendances);
                addRow("Members: ", String.valueOf(attendance.getInt("member")), R.id.attendances);
                addRow("General: ", String.valueOf(attendance.getInt("general")), R.id.attendances);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void addSurvey(Survey survey) {
        if ( acquireView() ) {
            addRow(survey.getName(), String.valueOf(survey.getEnd_date()), R.id.surveys);
        }
    }


    private void getAnnouncements() {
        myAPI.getForAllRoles(APIWrapper.FIND_CURRENT_ANNOUNCEMENTS, myPerson, Announcement.class);
    }


    private void getSurveys() {
        myAPI.getForAllRoles(APIWrapper.FIND_CURRENT_SURVEYS, myPerson, Survey.class);
    }


    private void setPersonAttributes() {
        if ( acquireView() ) {
            // set email
            TextView textViewPersonName = (TextView) home_layout.findViewById(R.id.person_name);
            textViewPersonName.setText("Hello " + myPerson.getFirst_name());
        }
    }


    private void addAnnouncements(Announcement announcement) {
        if ( acquireView() ) {
            addRow(announcement.getTitle(),
                    String.valueOf(announcement.getStart_date()),
                    R.id.announcements);
        }
    }

}