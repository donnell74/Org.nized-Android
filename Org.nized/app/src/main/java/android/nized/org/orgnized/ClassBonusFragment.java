package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */

import android.nized.org.api.APIWrapper;
import android.nized.org.domain.ClassBonus;
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

public class ClassBonusFragment extends Fragment {

    private ListView listView;
    private ArrayList<String> bonusStrings = new ArrayList<String>();
    private ArrayList<ClassBonus> bonuses = new ArrayList<ClassBonus>();
    private View main_layout;

    public ClassBonusFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        main_layout = inflater.inflate(R.layout.fragment_attendance, container, false);

        listView = (ListView) main_layout.findViewById(R.id.attendanceListView);

        bonusStrings.add("Loading...");

        APIWrapper.get(APIWrapper.FIND_CLASS_BONUSES, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONArray jsonRows) {
                bonusStrings = new ArrayList();
                bonuses = new ArrayList<ClassBonus>();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        Bundle args = new Bundle();
                        args.putString("course_code", bonuses.get(i).getCourse_code());
                        args.putString("semester", bonuses.get(i).getSemester());
                        mainActivity.changeFragment(MainActivity.PEOPLEFRAGMENT, args);
                    }
                });

                new AddRowsTask().execute(jsonRows);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("class bonuses failure", responseString);
                Toast.makeText(getView().getContext(),
                        "Unable to gather people.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                main_layout.getContext(), android.R.layout.simple_list_item_1, bonusStrings);
        listView.setAdapter(adapter);

        return main_layout;
    }

    public class AddRowsTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object[] objects) {
            JSONArray jsonDates = (JSONArray) objects[0];
            for (int i = 0; i < jsonDates.length(); i++) {
                try {
                    Log.i("class bonus", jsonDates.getJSONObject(i).toString());
                    ClassBonus classBonus = (ClassBonus) APIWrapper.parseJSONOjbect(
                            (JSONObject) jsonDates.getJSONObject(i), ClassBonus.class);
                    bonusStrings.add(classBonus.toString());
                    bonuses.add(classBonus);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if ( bonusStrings.isEmpty() ) {
                bonusStrings.add("No person data.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void Result) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    main_layout.getContext(), android.R.layout.simple_list_item_1, bonusStrings);
            listView.setAdapter(adapter);
        }
    }

}