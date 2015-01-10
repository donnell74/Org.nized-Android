package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */

import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Person;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    public ProfileFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        APIWrapper.get(APIWrapper.FIND_PERSON,
                new RequestParams("email", "donnell74@live.missouristate.edu"), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject person) {
                // If the response is JSONObject instead of expected JSONArray
                Person myPerson = (Person) APIWrapper.parseJSONOjbect(person, Person.class);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray people) {
                // Pull out the first one
                try {
                    Person myPerson = (Person) APIWrapper.parseJSONOjbect((JSONObject) people.get(0), Person.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("test", responseString);
            }
        });

        return rootView;
    }
}