package android.nized.org.orgnized;

import android.app.Activity;
import android.net.Uri;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Person;
import android.nized.org.domain.Question;
import android.nized.org.domain.Survey;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SurveysFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurveysFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View main_layout;
    private LinearLayout surveysContainer;
    public List<Survey> mSurveysList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private View surveyItemLayout;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SurveysFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SurveysFragment newInstance(String param1, String param2) {
        SurveysFragment fragment = new SurveysFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SurveysFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_layout = inflater.inflate(R.layout.fragment_surveys, container, false);
        mLayoutInflater = inflater;

        surveysContainer = (LinearLayout) main_layout.findViewById(R.id.surveysContainer);

        APIWrapper.get(APIWrapper.FIND_SURVEYS, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray surveys) {
                surveysContainer.removeAllViews();
                for (int i = 0; i < surveys.length(); i++ ) {
                    JSONObject objToParse = null;
                    try {
                        objToParse = (JSONObject) surveys.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Survey thisSurvey = (Survey) APIWrapper.parseJSONOjbect(objToParse, Survey.class);
                    if ( thisSurvey.getQuestions().size() == 0 ) {
                        continue;
                    }

                    mSurveysList.add(thisSurvey);

                    surveyItemLayout = mLayoutInflater.inflate(R.layout.survey_list_item, surveysContainer, false);
                    TextView surveyNameTV = (TextView) surveyItemLayout.findViewById(R.id.surveyName);
                    TextView expiresDateTV = (TextView) surveyItemLayout.findViewById(R.id.expiresDate);
                    //TextView takenDateTV = (TextView) surveyItemLayout.findViewById(R.id.takenTV);

                    java.util.Date endDate = thisSurvey.getEnd_date();
                    surveyNameTV.setText(thisSurvey.getName());
                    expiresDateTV.setText((endDate.getMonth() + 1) + "/" + endDate.getDate() );
                    surveyItemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = ((ViewGroup) view.getParent()).indexOfChild(view);
                        }
                    });
                    surveysContainer.addView(surveyItemLayout);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("Surveys get failure", responseString);
                Toast.makeText(getView().getContext(),
                        "Unable to gather surveys.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

        return main_layout;
    }
}
