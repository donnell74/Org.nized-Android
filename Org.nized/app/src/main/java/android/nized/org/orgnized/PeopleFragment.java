package android.nized.org.orgnized;


import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Person;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {
    private static final String DATE_TO_SHOW = "date_to_show";
    View main_layout = null;
    ArrayList names = new ArrayList();
    ArrayList<Person> peopleList = new ArrayList<Person>();
    ListView listView = null;
    private String mDate = null;
    private String mCourseCode = null;
    private String mSemester = null;
    private String mUrl = APIWrapper.FIND_PERSON;

    public PeopleFragment() {
        // Required empty public constructor
    }

    public static PeopleFragment newInstance(String dateToShow) {
        PeopleFragment fragment = new PeopleFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATE_TO_SHOW, dateToShow);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_layout = inflater.inflate(R.layout.fragment_people, container, false);

        listView = (ListView) main_layout.findViewById(R.id.peopleListView);

        names.add("Loading...");

        Bundle args = getArguments();
        RequestParams requestParams = new RequestParams();
        if ( args != null ) {
            mCourseCode = args.getString("course_code");
            mSemester = args.getString("semester");
            if ( mCourseCode != null ) {
                mUrl = APIWrapper.GET_PERSON_BY_CLASS_BONUS;
                requestParams.put("course_code", mCourseCode);
                requestParams.put("semester", mSemester);
            }

            mDate = args.getString("date");
            if ( mDate != null ) {
                mDate = mDate.split("T")[0];
            }
        }

        APIWrapper.get(mUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONArray people) {
                names = new ArrayList();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        Bundle args = new Bundle();
                        args.putSerializable(ProfileFragment.PERSON_TO_SHOW,
                                (java.io.Serializable) peopleList.get(i));
                        mainActivity.changeFragment(MainActivity.PROFILEFRAGMENT, args);
                    }
                });


                new AddPeopleTask().execute(people);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("People get failure", responseString);
                Toast.makeText(getView().getContext(),
                        "Unable to gather people.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                main_layout.getContext(), android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);

        return main_layout;
    }

    public class AddPeopleTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object[] objects) {
            JSONArray people = (JSONArray) objects[0];
            DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            for (int i = 0; i < people.length(); i++) {
                Person thisPerson = null;
                try {
                    JSONObject objToParse = (JSONObject) people.getJSONObject(i);
                    if ( mCourseCode != null ) {
                        objToParse = objToParse.getJSONObject("email");
                    }

                    thisPerson = (Person) APIWrapper.parseJSONOjbect(objToParse, Person.class);
                    if ( mDate == null ) {
                        names.add(thisPerson.toString());
                        peopleList.add(thisPerson);
                        continue;
                    }

                    if ( thisPerson.get_checkins() != null ) {
                        for (String eachDateStr : thisPerson.get_checkins()) {
                            if ( eachDateStr.split("T")[0].equals(mDate) ) {
                                names.add(thisPerson.toString());
                                peopleList.add(thisPerson);
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if ( names.isEmpty() ) {
                names.add("No person data.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void Result) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    main_layout.getContext(), android.R.layout.simple_list_item_1, names);
            listView.setAdapter(adapter);
        }
    }


}
