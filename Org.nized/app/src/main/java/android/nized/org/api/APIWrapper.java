package android.nized.org.api;

import android.nized.org.domain.Person;
import android.os.Looper;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by greg on 1/3/15.
 */
public class APIWrapper {
    private static final String BASE_URL = "http://www.reorconsultants.com:1337/";
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
    public static final String GET_PERSON_BY_CLASS_BONUS = "classbonus/getpersonbyclassbonus/";
    public static final String FIND_CARD_ID_TO_EMAIL = "cardidtoemail/find/";
    public static final String FIND_WITH_EXTRAS_SURVEYS = "surveys/findWithExtras/";
    public static final String FIND_WITH_EXTRAS_QUESTIONS = "questions/findWithExtras/";
    public static final String FIND_WITH_EXTRAS_ANNOUNCEMENTS = "announcements/findWithExtras/";
    public static final String FIND_ANNOUNCEMENTS_ROLES = "announcements_roles/find/";
    public static final String FIND_SURVEYS = "surveys/find/";
    public static final String FIND_SURVEYS_ROLES = "surveys_roles/find/";

    // A SyncHttpClient is an AsyncHttpClient
    public static AsyncHttpClient syncHttpClient = new SyncHttpClient();
    public static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        getClient().get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        getClient().post(getAbsoluteUrl(url), params, responseHandler);
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

    public static Object parseJSONOjbect(JSONObject response, Class domain) {
        Object modelObject = null;
        try {
            ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
            modelObject = mapper.readValue(response.toString(), domain);
            Log.w("test", modelObject.toString());
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return modelObject;
    }


    public static Object parseJSONOjbect(JsonParser response, Class domain) {
        Object modelObject = null;
        try {
            ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
            modelObject = mapper.readValue(response, domain);
            Log.w("test", modelObject.toString());
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return modelObject;
    }

}
