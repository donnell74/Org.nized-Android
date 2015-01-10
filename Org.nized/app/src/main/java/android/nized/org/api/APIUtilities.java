package android.nized.org.api;

import android.app.Activity;
import android.nized.org.domain.Person;
import android.nized.org.domain.Role;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Iterator;

/**
 * Created by greg on 1/7/15.
 */
public abstract class APIUtilities {
    public abstract void addToView(JSONObject obj, Class objClass);

    public void getForAllRoles(String url, Person person, final Class objClass) {
        try {
            for (Iterator<Role> role = person.getRoles().iterator(); role.hasNext(); ) {
                RequestParams requestParams = new RequestParams("role_id", role.next().getRole_id());
                Log.i("get", url);
                APIWrapper.get(url, requestParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject obj) {
                        // If the response is JSONObject instead of expected JSONArray
                        Log.i("getForAllRoles", "obj");
                        addToView(obj, objClass);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                        // Pull out the first one
                        try {
                            for (int i = 0; i < all_objs.length(); i++) {
                                Log.i("getForAllRoles", "test");
                                addToView(all_objs.getJSONObject(i), objClass);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        // pass
                        Log.e("getForAllRoles", responseString);
                    }
                });
            }
        } catch (NullPointerException e) {
            Log.e("getForALlRoles", "Null pointer");
            e.printStackTrace();
            return;
        }
    }
}
