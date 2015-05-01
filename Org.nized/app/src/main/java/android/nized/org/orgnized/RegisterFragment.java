package android.nized.org.orgnized;

import android.content.Context;
import android.nized.org.api.APIWrapper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CARDID = "card_id";

    // TODO: Rename and change types of parameters
    private String cardID = "";
    private View mainView = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CARDID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardID = "";
        if (getArguments() != null) {
            cardID = getArguments().getString(ARG_CARDID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        mainView = rootView;

        if ( mainView != null ) {
            Button button = (Button) mainView.findViewById(R.id.checkEmailButton);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    checkEmail(v);
                }
            });

            button = (Button) mainView.findViewById(R.id.createPersonButton);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    createPerson(v);
                }
            });

        }

        return rootView;
    }

    private boolean acquireView() {
        if ( mainView != null )
        {
            return true;
        }

        mainView = getView();
        if ( mainView != null )
        {
            return true;
        }

        return false;
    }


    public void checkEmail(View view) {
        if ( acquireView() ) {
            // hide keyboard
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mainView.getWindowToken(), 0);

            TextView emailTextView = (TextView) mainView.findViewById(R.id.email);
            final String email = emailTextView.getText().toString();

            RequestParams requestParams = new RequestParams();
            requestParams.put("email", email);

            APIWrapper.post(APIWrapper.FIND_PERSON, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject person) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.i("Check Email Object", person.toString());
                    linkEmailToCardID(email);
                }


                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray people) {
                    // Pull out the first one
                    try {
                        Log.i("Check Email Array", people.get(0).toString());
                        linkEmailToCardID(email);
                    } catch (JSONException e) {
                        // no results, get info
                        Toast.makeText(getView().getContext(),
                                "Email not found, please enter more data.",
                                Toast.LENGTH_LONG)
                                .show();

                        getPersonInfo();
                    }
                }

                @Override
                public void onSuccess(int statusCode, org.apache.http.Header[] headers, java.lang.String responseString) {
                    Log.i("Check Email String", responseString);
                    linkEmailToCardID(email);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.w("Check Email Fail String", responseString);
                    getPersonInfo();
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                    if (errorResponse != null) {
                        Log.i("Check Email Fail Object", errorResponse.toString());
                    }

                    getPersonInfo();
                }
            });
        }
    }


    private void linkEmailToCardID( String email ) {
        Log.i("linkEmailToCardID", "Entered");
        Log.i("linkEmailToCardID", email);
        RequestParams requestParams = new RequestParams();
        requestParams.put("email", email);
        if ( ! cardID.equals("") ) {
            requestParams.put("card_id", cardID);
        } else {
            requestParams.put("card_id", email);
            cardID = email;
        }
        Log.i("linkEmailToCardID", requestParams.toString());

        final MainActivity mainActivity = (MainActivity) getActivity();

        APIWrapper.post(APIWrapper.FIND_OR_CREATE_CARD_ID_TO_EMAIL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject result) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("Link Email Object", result.toString());
                mainActivity.checkInPerson(cardID);
                mainActivity.changeFragment(MainActivity.HOMEFRAGMENT);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray result) {
                // Pull out the first one
                try {
                    Log.i("Link Email Array", result.get(0).toString());
                    mainActivity.checkInPerson(cardID);
                    mainActivity.changeFragment(MainActivity.HOMEFRAGMENT);
                } catch (JSONException e) {
                    // no results, get info
                    getPersonInfo();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("Link Email Fail String", responseString);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                if (errorResponse != null) {
                    Log.i("Link Email Fail Object", errorResponse.toString());
                }
            }
        });
    }


    private void showTextView(int id) {
        TextView textView = (TextView) mainView.findViewById(id);
        textView.setVisibility(1);
    }


    private void showEditText(int id) {
        EditText editText = (EditText) mainView.findViewById(id);
        editText.setVisibility(1);
    }


    private void showButton(int id) {
        Button button = (Button) mainView.findViewById(id);
        button.setVisibility(1);
    }


    private void getPersonInfo() {
        Log.i("getPersonInfo", "Entered");

        if (acquireView()) {
            showTextView(R.id.first_name_label);
            showEditText(R.id.first_name);
            showTextView(R.id.last_name_label);
            showEditText(R.id.last_name);
            showButton(R.id.createPersonButton);
        }
    }


    private String getEditText(int id) {
        EditText editText = (EditText) mainView.findViewById(id);
        return String.valueOf(editText.getText());
    }


    public void createPerson(View view) {
        if (acquireView()) {
            String first_name = getEditText(R.id.first_name);
            String last_name = getEditText(R.id.last_name);
            final String email = getEditText(R.id.email);
            RequestParams requestParams = new RequestParams();
            requestParams.put("email", email);
            requestParams.put("password", "Aitp2015");
            requestParams.put("first_name", first_name);
            requestParams.put("last_name", last_name);

            final MainActivity mainActivity = (MainActivity) getActivity();

            APIWrapper.post(APIWrapper.FIND_OR_CREATE_PERSON, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject result) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.i("Create Person Object", result.toString());
                    linkEmailToCardID(email);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray result) {
                    // Pull out the first one
                    try {
                        Log.i("Create Person Array", result.get(0).toString());
                        linkEmailToCardID(email);
                    } catch (JSONException e) {
                        // no results, get info
                        getPersonInfo();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.w("Create Person Fail", responseString);
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                    if (errorResponse != null) {
                        Log.i("Create Person Fail", errorResponse.toString());
                    }
                }
            });

        }
    }
}