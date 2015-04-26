package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */

import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Person;
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

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AttendanceFragment extends Fragment {

    private ListView listView;
    private ArrayList<String> dates = new ArrayList<String>();
    private View main_layout;

    public AttendanceFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        main_layout = inflater.inflate(R.layout.fragment_attendance, container, false);

        listView = (ListView) main_layout.findViewById(R.id.attendanceListView);

        dates.add("Loading...");

        APIWrapper.get(APIWrapper.GET_ALL_CHECKIN_DATES, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONArray jsonDates) {
                dates = new ArrayList();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        Bundle args = new Bundle();
                        args.putString("date", dates.get(i));
                        mainActivity.changeFragment(MainActivity.PEOPLEFRAGMENT, args);
                    }
                });


                new AddDatesTask().execute(jsonDates);
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
                main_layout.getContext(), android.R.layout.simple_list_item_1, dates);
        listView.setAdapter(adapter);

        return main_layout;
    }

    public class AddDatesTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object[] objects) {
            JSONArray jsonDates = (JSONArray) objects[0];
            for (int i = 0; i < jsonDates.length(); i++) {
                try {
                    dates.add(jsonDates.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if ( dates.isEmpty() ) {
                dates.add("No person data.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void Result) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    main_layout.getContext(), android.R.layout.simple_list_item_1, dates);
            listView.setAdapter(adapter);
        }
    }

}