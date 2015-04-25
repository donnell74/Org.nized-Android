package android.nized.org.orgnized;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Person;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

/**
 * Created by greg on 4/22/15.
 */
public class ChangePasswordDialogFragment extends DialogFragment {
    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    private EditText mConfirmPassword;
    private EditText mOldPassword;
    private EditText mNewPassword;
    private AlertDialog mBuild;
    private ChangePasswordTask mAuthTask;
    private View mLoginFormView;
    private View mProgressView;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_changepassword, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(layout)
                // Add action buttons
                .setPositiveButton(R.string.submit_button, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChangePasswordDialogFragment.this.getDialog().cancel();
                    }
                });

        mOldPassword = (EditText) layout.findViewById(R.id.prevPasswordET);
        mNewPassword = (EditText) layout.findViewById(R.id.newPasswordET);
        mConfirmPassword = (EditText) layout.findViewById(R.id.confirmPasswordET);
        mLoginFormView = layout.findViewById(R.id.change_password_form);
        mProgressView = layout.findViewById(R.id.change_password_progress);

        mConfirmPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                mConfirmPassword.setError(null);
                String strPass1 = mNewPassword.getText().toString();
                String strPass2 = mConfirmPassword.getText().toString();
                if (!strPass1.equals(strPass2)) {
                    mConfirmPassword.setError(getString(R.string.settings_pwd_not_equal));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        mBuild = builder.create();
        mBuild.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mBuild.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        String oldPassword = mOldPassword.getText().toString();
                        String newPassword = mNewPassword.getText().toString();
                        String confirmPassword = mConfirmPassword.getText().toString();

                        // reset passwords
                        mOldPassword.setError(null);
                        mNewPassword.setError(null);
                        mConfirmPassword.setError(null);

                        if (newPassword.equals(confirmPassword)) {
                            // create request to change password here

                            showProgress(true);
                            mAuthTask = new ChangePasswordTask(ProfileFragment.mPerson.getEmail(), oldPassword, newPassword);
                            mAuthTask.execute((Void) null);
                        }
                    }
                });
            }
        });

        return mBuild;
    }

    public class ChangePasswordTask extends AsyncTask<Void, Void, Boolean> {

        private final String taskEmail;
        private final String taskOldPassword;
        private final String taskNewPassword;

        ChangePasswordTask(String email, String oldPassword, String newPassword) {
            taskEmail = email;
            taskOldPassword = oldPassword;
            taskNewPassword = newPassword;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            RequestParams requestParams = new RequestParams();
            requestParams.put("email", taskEmail);
            requestParams.put("old_password", taskOldPassword);
            requestParams.put("new_password", taskNewPassword);

            Log.i("change password", requestParams.toString());
            final CountDownLatch latch = new CountDownLatch(1);
            final boolean[] result = new boolean[1];
            APIWrapper.post(APIWrapper.CHANGE_PASSWORD, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject person) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.i("Change person success", "");
                    result[0] = true;
                    APIWrapper.setLoggedInPerson((Person) APIWrapper.parseJSONOjbect(person, Person.class));
                    latch.countDown();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray people) {
                    // Pull out the first one
                    try {
                        Log.i("Change person success", "");
                        APIWrapper.setLoggedInPerson((Person) APIWrapper.parseJSONOjbect(
                                people.getJSONObject(0), Person.class));
                        result[0] = true;
                        latch.countDown();
                    } catch (JSONException e) {
                        Log.i("Change password failure", "password incorrect");
                        result[0] = false;
                        latch.countDown();
                    }
                }

                @Override
                public void onSuccess(int statusCode, org.apache.http.Header[] headers, java.lang.String responseString) {
                    Log.i("Change person success", "");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("Change person failure", responseString);
                    result[0] = false;
                    latch.countDown();
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                    if (errorResponse != null) {
                        Log.i("change person failure", "Response: " + errorResponse.toString());
                    }

                    result[0] = false;
                    latch.countDown();
                }
            });

            try {
                latch.await(); // Wait for countDown() in the UI thread.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return result[0];

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //Dismiss once everything is OK.
                Log.i("dismiss", "");
                mBuild.dismiss();
                mListener.onDialogPositiveClick(ChangePasswordDialogFragment.this);
            } else {
                mOldPassword.setError(getString(R.string.error_incorrect_password));
                mOldPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}