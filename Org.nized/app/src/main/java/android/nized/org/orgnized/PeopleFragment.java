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

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {
    View main_layout = null;
    ArrayList names = new ArrayList();
    ArrayList<Person> peopleList = new ArrayList<Person>();
    ListView listView = null;

    public PeopleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_layout = inflater.inflate(R.layout.fragment_people, container, false);

        listView = (ListView) main_layout.findViewById(R.id.peopleListView);

        names.add("Loading...");

        APIWrapper.get(APIWrapper.FIND_PERSON, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONArray people) {
                names = new ArrayList();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.i("test", peopleList.get(i).toString());
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
                Log.w("check in person failure", responseString);
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
            for (int i = 0; i < people.length(); i++) {
                Person thisPerson = null;
                try {
                    thisPerson = (Person) APIWrapper.parseJSONOjbect((JSONObject) people.getJSONObject(i), Person.class);
                    names.add(thisPerson.toString());
                    peopleList.add(thisPerson);
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
