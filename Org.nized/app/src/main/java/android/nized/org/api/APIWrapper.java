package android.nized.org.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nized.org.domain.ClassBonus;
import android.nized.org.domain.Permission;
import android.nized.org.domain.Person;
import android.nized.org.domain.Role;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.BoringLayout;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by greg on 1/3/15.
 */
public class APIWrapper {
    //change to your own public ip. can be found by googleing "my IP".
    private static final String BASE_URL = "http://reorconsultants.com:9999/";
    public static final String INSERT_PERSON = "person/insertperson/";
    public static final String FIND_PERSON = "person/find/";
    public static final String CHECK_IF_USER_EXIST = "person/checkifuserexist/";
    public static final String DELETE_PERSON = "person/deleteperson/";
    public static final String UPDATE_PERSON = "person/update/";
    public static final String LOGIN_PERSON = "person/login/";
    public static final String RESET_PASSWORD_PERSON = "person/resetPassword/";
    public static final String FIND_WITH_EXTRAS_PERSON = "person/findWithExtras/";
    public static final String UPDATE_EMAIL = "person/updateemail/";
    public static final String GET_FIRST_PERSON = "person/getfirstperson/";
    public static final String GET_CLASS_BONUSES_BY_PERSON = "person/getclassbonusesbyperson";
    public static final String GET_CHECKINS_BY_DATE = "checkins/getcheckinsbydate/";
    public static final String CHECK_IF_USER_CHECKED_IN_TODAY = "checkins/checkifusercheckedintoday/";
    public static final String CHECK_IN_PERSON = "checkins/checkinperson/";
    public static final String GET_TOTAL_CHECKINS_BY_DATE = "checkins/gettotalcheckinsbydate/";
    public static final String FIND_CHECKINS = "checkins/find/";
    public static final String GET_TODAYS_ATTENDANCE = "checkins/gettodaysattendance";
    public static final String GET_PERSON_BY_CLASS_BONUS = "classbonus/getpersonsbyclassbonus/";
    public static final String DELETE_PERSON_CLASS_BONUSES = "person_classbonus/delete/";
    public static final String FIND_CARD_ID_TO_EMAIL = "cardidtoemail/find/";
    public static final String FIND_OR_CREATE_CARD_ID_TO_EMAIL = "cardidtoemail/findOrCreate/";
    public static final String CREATE_CARD_ID_TO_EMAIL = "cardidtoemail/create/";
    public static final String FIND_WITH_EXTRAS_SURVEYS = "surveys/findWithExtras/";
    public static final String FIND_WITH_EXTRAS_QUESTIONS = "questions/findWithExtras/";
    public static final String FIND_WITH_EXTRAS_ANNOUNCEMENTS = "announcements/findWithExtras/";
    public static final String FIND_ANNOUNCEMENTS = "announcements/find/";
    public static final String FIND_ANNOUNCEMENTS_ROLES = "announcements_roles/find/";
    public static final String FIND_CURRENT_ANNOUNCEMENTS = "announcements/findCurrent/";
    public static final String FIND_SURVEYS = "surveys/find/";
    public static final String FIND_CURRENT_SURVEYS = "surveys/findCurrent/";
    public static final String FIND_SURVEYS_ROLES = "surveys_roles/find/";
    public static final String FIND_NOTES = "notes/find/";
    public static final String FIND_ANSWERS = "answers/find/";
    public static final String FIND_QUESTIONS = "questions/find/";
    public static final String FIND_POSSIBLE_ANSWERS = "possibleanswers/find/";
    public static final String FIND_OR_CREATE_PERSON = "person/findOrCreate/";
    public static final String CREATE_PERSON_CLASS_BONUSES = "person_classbonus/createIfNotExists/";
    public static final String CREATE_CLASS_BONUSES = "classbonus/createIfNotExists/";
    public static final String CHANGE_PASSWORD = "person/changePassword/";
    public static final String GET_ALL_CHECKIN_DATES = "checkins/GetAllCheckinDates/";
    public static final String FIND_CLASS_BONUSES = "classbonus/find/";
    public static final String DELETE_PERSON_ROLE = "person_role/destroy/";
    public static final String FIND_ROLES = "roles/find/";
    public static final String CREATE_PERSON_ROLE = "person_role/create/";
    public static final String UPDATE_PERSON_ROLE = "person_role/update/";
    public static final String FIND_PERMISSIONS = "permissions/find/";
    public static final int NONMEMBER_ROLE_ID = 11;
    public static final int MEMBER_ROLE_ID = 10;
    public static final String CREATE_ANSWER = "answers/create/";
    public static final String UPDATE_ANSWER = "answers/update/";
    public static final String DELETE_ANSWER = "answers/destroy/";

    // A SyncHttpClient is an AsyncHttpClient
    public static AsyncHttpClient syncHttpClient = new SyncHttpClient();
    public static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    // App wide person objects
    public static Person loggedInPerson = null;
    public static Person lastScannedPerson = null;

    // App Context
    public static Context mContext = null;
    private static ArrayList<Permission> permissions = new ArrayList<>();

    public static boolean isOnline() {
        if (mContext != null) {
            ConnectivityManager cm =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        } else {
            Log.e("apiwrapper", "mContext needs to be set for isOnline");
            return false;
        }
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if ( isOnline() ) {
            getClient().get(getAbsoluteUrl(url), params, responseHandler);
        } else {
            Log.e("APIWrapper get", "not online, need to implement save request");
        }
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if ( isOnline() ) {
            Log.i("getAbsoluteUrl", BASE_URL + url);
            getClient().post(getAbsoluteUrl(url), params, responseHandler);
        } else {
            Log.e("APIWrapper post", "not online, need to implement save request");
        }
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static AsyncHttpClient getClient() {
        // Return the synchronous HTTP client when the thread is not prepared
        if (Looper.myLooper() == null)
            return syncHttpClient;
        return asyncHttpClient;
    }

    public static Object parseJSONOjbect(final JSONObject response, final Class domain) {
        final CountDownLatch latch = new CountDownLatch(1);
        final Object modelObjects [] = {null};
        Thread parseThread = new HandlerThread("ParseHandler") {
            @Override
            public void run() {
                Looper.prepare();
                try
                {
                    ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
                    modelObjects[0] = mapper.readValue(response.toString(), domain);
                    Log.w("test", modelObjects[0].toString());
                } catch ( JsonMappingException e ) {
                    e.printStackTrace();
                } catch ( JsonParseException e ) {
                    e.printStackTrace();
                } catch ( IOException e ) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }

                Looper.loop();
            }
        };

        parseThread.start();

        try {
            latch.await(); // Wait for countDown() in the UI thread.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return modelObjects[0];
    }


    public static Object parseJSONOjbect(final JsonParser response, final Class domain) {
        final CountDownLatch latch = new CountDownLatch(1);
        final Object modelObjects [] = {null};
        Thread parseThread = new HandlerThread("ParseHandler") {
            @Override
            public void run() {
                Looper.prepare();
                try
                {
                    ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
                    modelObjects[0] = mapper.readValue(response, domain);
                } catch ( JsonMappingException e ) {
                    e.printStackTrace();
                } catch ( JsonParseException e ) {
                    e.printStackTrace();
                } catch ( IOException e ) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }

                Looper.loop();
            }
        };

        parseThread.start();

        try {
            latch.await(); // Wait for countDown() in the UI thread.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return modelObjects[0];
    }

    public static Person getLoggedInPerson() {
        return loggedInPerson;
    }

    public static void setLoggedInPerson(Person loggedInPerson) {
        APIWrapper.loggedInPerson = loggedInPerson;
    }

    public static Person getLastScannedPerson() {
        return lastScannedPerson;
    }

    public static void setLastScannedPerson(Person lastScannedPerson) {
        APIWrapper.lastScannedPerson = lastScannedPerson;
    }

    public static Permission getPermission(String model) {
        for ( Permission eachPerm : APIWrapper.permissions ) {
            if ( model.equals(eachPerm.getModel()) ) {
                return eachPerm;
            }
        }

        Permission defaultPerm = new Permission();
        defaultPerm.setOther(false);
        defaultPerm.setSelf(false);
        defaultPerm.setModel(model);

        return defaultPerm;
    }

    public static void getPermissions() {
        String url = APIWrapper.FIND_PERMISSIONS + "?";
        for ( Role eachRole : loggedInPerson.getRoles()) {
            url += "role_id=" + eachRole.getRole_id() + "&";
        }

        APIWrapper.post(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                permissions.clear();
                for ( int i = 0; i < all_objs.length(); i++ ) {
                    try {
                        Permission eachPerm = (Permission) parseJSONOjbect(all_objs.getJSONObject(i), Permission.class);
                        boolean havePerm = false;
                        for ( Permission currPerm : APIWrapper.permissions ) {
                            if (currPerm.getModel().equals(eachPerm.getModel())) {
                                havePerm = true;

                                // make final perm true if ever hit a true
                                currPerm.setSelf(currPerm.getSelf() || eachPerm.getSelf());
                                currPerm.setOther(currPerm.getOther() || eachPerm.getOther());
                            }
                        }

                        if ( ! havePerm ) {
                            APIWrapper.permissions.add(eachPerm);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(String.valueOf(statusCode), responseString);
            }
        });
    }
}
