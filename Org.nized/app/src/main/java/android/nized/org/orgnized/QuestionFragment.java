package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */

import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Answer;
import android.nized.org.domain.Note;
import android.nized.org.domain.PossibleAnswer;
import android.nized.org.domain.Question;
import android.nized.org.domain.Role;
import android.nized.org.domain.Survey;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class QuestionFragment extends Fragment {

    public static final String SURVEY_TO_TAKE = "survey_to_take";
    View main_layout = null;
    protected Survey mSurvey;
    private int mQuestionIndex = -1;
    private Question mCurrQuestion;
    private LinearLayout mTextLL;
    private LinearLayout mCheckboxLL;
    private LinearLayout mRadioLL;
    private TextView mQuestionTV;
    private EditText mTextET;
    private Button mTextNextBtn;
    private Button mRadioNextBtn;
    private Button mCheckboxNextBtn;
    private List<RadioButton> currRadioButtons = new ArrayList<>();
    private List<CheckBox> mCurrCheckBoxes = new ArrayList<>();
    private List<Answer> mPastAnswers = new ArrayList<>();
    private ArrayList<Answer> pastAnswersToCurr;
    private LinearLayout mRadioContainerLL;
    private LinearLayout mCheckboxContainerLL;

    public QuestionFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_layout = inflater.inflate(R.layout.question_fragment, container, false);

        Bundle args = getArguments();
        mSurvey = (Survey) args.getSerializable(SURVEY_TO_TAKE);

        Log.i("Taking survey: ", mSurvey.toString());
        for ( Question each : mSurvey.getQuestions() ) {
            Log.i("eachQuestion", each.toString());
        }

        initLayoutVars();
        getPossibleAnswers();

        return main_layout;
    }

    private void initLayoutVars() {
        mQuestionTV = (TextView) main_layout.findViewById(R.id.questionTV);

        mTextLL = (LinearLayout) main_layout.findViewById(R.id.textLL);
        mTextET = (EditText) main_layout.findViewById(R.id.textET);
        mTextNextBtn = (Button) main_layout.findViewById(R.id.textNextBtn);

        mRadioLL = (LinearLayout) main_layout.findViewById(R.id.radioLL);
        mRadioContainerLL = (LinearLayout) main_layout.findViewById(R.id.radioContainer);
        mRadioNextBtn = (Button) main_layout.findViewById(R.id.radioNextBtn);

        mCheckboxLL = (LinearLayout) main_layout.findViewById(R.id.checkboxLL);
        mCheckboxContainerLL = (LinearLayout) main_layout.findViewById(R.id.checkboxContainer);
        mCheckboxNextBtn = (Button) main_layout.findViewById(R.id.checkboxNextBtn);

        setOnClickHandlers();
    }


    private void nextQuestion() {
        mQuestionIndex += 1;
        if ( mQuestionIndex >= mSurvey.getQuestions().size() ) {
            Toast.makeText(getView().getContext(),
                    "Survey completed.",
                    Toast.LENGTH_LONG)
                    .show();

            // load surveysfragment
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.changeFragment(MainActivity.SURVEYSFRAGMENT);
        } else {
            mCurrQuestion = mSurvey.getQuestions().get(mQuestionIndex);

            List<Role> personRoles = APIWrapper.getLoggedInPerson().getRoles();
            List<Role> questionRoles = mCurrQuestion.getRoles();
            if ( questionRoles.size() == 0 ) {
                loadQuestion();
            } else {
                Boolean hasRights = false;
                for ( Role eachQRole : questionRoles ) {
                    hasRights = false;
                    for ( Role eachPRole : personRoles ) {
                        if ( eachQRole.getRole_id() == eachPRole.getRole_id() ) {
                            hasRights = true;
                            break;
                        }
                    }

                    if ( ! hasRights ) {
                        // person does not have rights for question, next question
                        nextQuestion();
                        break;
                    }
                }

                if ( hasRights ) {
                    loadQuestion();
                }
            }
        }
    }


    private void setOnClickHandlers() {
        mTextNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String val = mTextET.getText().toString().trim();
                Log.i("Saving text", val);
                // submit answer here

                if (!val.equals("")) {
                    if (pastAnswersToCurr.size() == 0) {
                        sendAnswer(mCurrQuestion.getId(), val);
                    } else {
                        updateAnswer(pastAnswersToCurr.get(0).getId(), val);
                    }
                }

                nextQuestion();
            }
        });
        mCheckboxNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Saving", "checkbox");

                for (CheckBox cb : mCurrCheckBoxes) {
                    if (cb.isChecked()) {
                        Log.i("selected", cb.getText().toString());

                        int pastAnswerIndex = -1;
                        for ( int p = 0; p < pastAnswersToCurr.size(); p++ ) {
                            if ( pastAnswersToCurr.get(p).getText()
                                    .equals(cb.getText().toString()) ) {
                                pastAnswerIndex = p;
                            }
                        }

                        if ( pastAnswerIndex == -1 ) {
                            sendAnswer(mCurrQuestion.getId(), cb.getText().toString());
                        } else {
                            updateAnswer(pastAnswersToCurr.get(pastAnswerIndex).getId(),
                                    cb.getText().toString());

                            pastAnswersToCurr.remove(pastAnswerIndex);
                        }
                    }
                }

                // delete left over ones because they have been deselected
                if ( pastAnswersToCurr.size() != 0 ) {
                    for ( Answer pastAnswer : pastAnswersToCurr ) {
                        deleteAnswer(pastAnswer);
                    }
                }

                // submit answer here

                nextQuestion();
            }
        });
        mRadioNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Saving", "radio");
                // submit answer here

                for (RadioButton rb : currRadioButtons) {
                    if (rb.isChecked()) {
                        Log.i("selected", rb.getText().toString());

                        if ( pastAnswersToCurr.size() == 0 ) {
                            sendAnswer(mCurrQuestion.getId(), rb.getText().toString());
                        } else {
                            updateAnswer(pastAnswersToCurr.get(0).getId(),
                                    rb.getText().toString());
                        }
                    }
                }

                nextQuestion();
            }
        });

    }

    private void deleteAnswer(Answer pastAnswer) {
        String url = APIWrapper.DELETE_ANSWER + pastAnswer.getId();

        APIWrapper.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray questions) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("Answer create failure", responseString);
                Toast.makeText(getView().getContext(),
                        "Unable to send answer, try again.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void updateAnswer(int question_id, String text) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("text", text);

        String url = APIWrapper.UPDATE_ANSWER + question_id;

        APIWrapper.post(url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray questions) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("Answer create failure", responseString);
                Toast.makeText(getView().getContext(),
                        "Unable to send answer, try again.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void sendAnswer(int question_id, String text) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("question_id", question_id);
        requestParams.put("text", text);
        requestParams.put("email", APIWrapper.getLoggedInPerson().getEmail());

        APIWrapper.post(APIWrapper.CREATE_ANSWER, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray questions) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("Answer create failure", responseString);
                Toast.makeText(getView().getContext(),
                        "Unable to send answer, try again.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

    }

    private void loadQuestion() {
        pastAnswersToCurr = new ArrayList<>();
        for ( Answer answer : mPastAnswers ) {
            if ( answer.getQuestion_id() == mCurrQuestion.getId() ) {
                pastAnswersToCurr.add(answer);
            }
        }

        mQuestionTV.setText(mCurrQuestion.getQuestionText());

        mTextLL.setVisibility(View.GONE);
        mCheckboxLL.setVisibility(View.GONE);
        mRadioLL.setVisibility(View.GONE);

        switch ( mCurrQuestion.getType() ) {
            case CHECKBOX:
                Log.i("checkbox", "");
                mCheckboxLL.setVisibility(View.VISIBLE);

                for ( PossibleAnswer eachAnswer : mCurrQuestion.getPossibleAnswers() ) {
                    CheckBox cb = new CheckBox(getActivity().getApplicationContext());
                    mCurrCheckBoxes.add(cb);
                    cb.setText(eachAnswer.getText());
                    cb.setTextColor(getResources().getColor(R.color.black));
                    for ( Answer pastAnswers : pastAnswersToCurr ) {
                        if ( pastAnswers.getText().equals(eachAnswer.getText()) ) {
                            cb.setChecked(true);
                        }
                    }

                    mCheckboxContainerLL.addView(cb);

                }

                break;
            case RADIO:
                Log.i("radio", "");
                mRadioLL.setVisibility(View.VISIBLE);

                RadioGroup radioGroup = new RadioGroup(getActivity().getApplicationContext());
                for ( PossibleAnswer eachAnswer : mCurrQuestion.getPossibleAnswers() ) {
                    RadioButton rb = new RadioButton(getActivity());
                    currRadioButtons.add(rb);
                    radioGroup.addView(rb); //the RadioButtons are added to the radioGroup instead of the layout
                    rb.setText(eachAnswer.getText());
                    rb.setTextColor(getResources().getColor(R.color.black));
                    for ( Answer pastAnswers : pastAnswersToCurr ) {
                        if ( pastAnswers.getText().equals(eachAnswer.getText()) ) {
                            rb.setChecked(true);
                        }
                    }
                }
                mRadioContainerLL.addView(radioGroup);

                break;
            case TEXT:
            default:
                Log.i("text", "");
                mTextLL.setVisibility(View.VISIBLE);
                if ( pastAnswersToCurr.size() != 0 ) {
                    mTextET.setText(pastAnswersToCurr.get(0).getText());
                }
        }
    }

    private void getPastAnswers() {
        // make get request to questions with role ids
        String url = APIWrapper.FIND_ANSWERS + "?";
        url += "email=" + APIWrapper.getLoggedInPerson().getEmail();
        for ( Question eachQuestion : mSurvey.getQuestions() ) {
            url += "&question_id=" + eachQuestion.getId();
        }

        APIWrapper.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray questions) {
                for (int i = 0; i < questions.length(); i++) {
                    JSONObject objToParse = null;
                    try {
                        objToParse = (JSONObject) questions.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Answer thisAnswer = (Answer) APIWrapper.parseJSONOjbect(objToParse, Answer.class);
                    mPastAnswers.add(thisAnswer);
                }

                nextQuestion();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("Questions get failure", responseString);
                Toast.makeText(getView().getContext(),
                        "Unable to gather questions.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

    }

    private void getPossibleAnswers() {
        // make get request to questions with role ids
        RequestParams requestParams = new RequestParams("survey_id", mSurvey.getId());
        APIWrapper.get(APIWrapper.FIND_QUESTIONS, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray questions) {
                ArrayList<Question> updatedQuestions = new ArrayList<Question>();
                for (int i = 0; i < questions.length(); i++) {
                    JSONObject objToParse = null;
                    try {
                        objToParse = (JSONObject) questions.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Question thisQuestion = (Question) APIWrapper.parseJSONOjbect(objToParse, Question.class);
                    updatedQuestions.add(thisQuestion);
                }

                mSurvey.setQuestions(updatedQuestions);
                getPastAnswers();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("Questions get failure", responseString);
                Toast.makeText(getView().getContext(),
                        "Unable to gather questions.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

    }
}