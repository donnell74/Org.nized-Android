package android.nized.org.api;

import android.nized.org.domain.Person;
import android.nized.org.domain.Role;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
                        Log.i("getForAllRoles", "obj for " + objClass.toString());
                        addToView(obj, objClass);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                        // Pull out the first one
                        try {
                            Log.i("getForAllRoles", "array for " + objClass.toString());
                            Log.i("getForAllRoles", "array: " + all_objs.toString());
                            for (int i = 0; i < all_objs.length(); i++) {
                                addToView(all_objs.getJSONObject(i), objClass);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.i("getForAllRoles", "string");
                        JsonFactory jsonFactory = new JsonFactory();
                        try {
                            addToView(new JSONObject(responseString.replaceAll("\n", "\\n")), objClass);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        // pass
                        Log.e("getForAllRoles fail", responseString);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                        // pass
                        Log.e("getForAllRoles fail", response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray response) {
                        // pass
                        try {
                            Log.e("getForAllRoles fail", response.getJSONObject(0).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (NullPointerException e) {
            Log.e("getForALlRoles fail", "Null pointer");
            return;
        }
    }
}
