package android.nized.org.orgnized;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Announcement;
import android.nized.org.domain.Checkins;
import android.nized.org.domain.ClassBonus;
import android.nized.org.domain.Note;
import android.nized.org.domain.Person;
import android.nized.org.domain.Role;
import android.nized.org.domain.Survey;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.games.quest.Quest;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity
        implements ClassBonusDialogFragment.NoticeDialogListener,
                   ChangePasswordDialogFragment.NoticeDialogListener,
                   RolesDialogFragment.NoticeDialogListener,
                   AnnouncementsFragment.DisplayAnnouncementDetails,
                   NotesFragment.DisplayNoteDetails,
                   SurveysFragment.DisplayQuestion{
    public static final String PREFS_NAMES = "OrgnizedPrefs";
    private List<String> mNavTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private TextView mTextView;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    private static final DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();
    private String mTagID = "";

    private LinearLayout mTagContent;

    private AlertDialog mDialog;

    // used for passing position into change fragment
    public static final int HOMEFRAGMENT = 0;
    public static final int ATTENDANCEFRAGMENT = 1;
    public static final int NOTESFRAGMENT = 2;
    public static final int MYPROFILEFRAGMENT = 3;
    public static final int SURVEYSFRAGMENT = 4;
    public static final int FEEDBACKFRAGMENT = 5;
    public static final int ANNOUNCEMENTSFRAGMENT = 6;
    public static final int LASTSCANNEDFRAGMENT = 7;
    public static final int CLASSBONUSESFRAGMENT = 8;
    public static final int PEOPLEFRAGMENT = 9;
    public static final int REGISTERFRAGMENT = 10;
    public static final int PROFILEFRAGMENT = 11;
    public static final int ANNOUNCEMENTSDETAILSFRAGMENT = 12;
    public static final int NOTEDETAILSFRAGMENT = 13;
    public static final int QUESTIONFRAGMENT = 14;

    private String mEmail = "";
    private String mTitle = "Org.nized";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        APIWrapper.mContext = this;

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        BitmapDrawable background = new BitmapDrawable(BitmapFactory.decodeResource(getResources(),
                R.drawable.banner));

        actionBar.setBackgroundDrawable(background);


        mNavTitles = new ArrayList<String>(Arrays.asList( getResources().getStringArray(R.array.nav_titles_array) ));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavTitles));
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                null,
                R.string.drawer_open,
                R.string.drawer_close
        );

        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).commit();

        mDrawerLayout.closeDrawer(mDrawerList);

        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        resolveIntent(getIntent());

        mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        /*if (mAdapter == null) {
            //showMessage(R.string.error, R.string.no_nfc);
            finish();
            return;
        }*/

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true) });

        // Restore preferences
        /* Need database to be implemented first because setLoginPerson
        SharedPreferences settings = getSharedPreferences(PREFS_NAMES, 0);
        mEmail = settings.getString("email", "");

        if ( mEmail.equals("") ) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        }*/

        APIWrapper.getPermissions();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
        // then it has handled the app icon touch event
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.i("changePassword", "");
    }

    @Override
    public void DisplayAnnouncementDetails(Announcement announcement) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(AnnouncementDetailFragment.Announcement_to_show,
                (java.io.Serializable) announcement);

        changeFragment(ANNOUNCEMENTSDETAILSFRAGMENT, bundle);
    }

    @Override
    public void DisplayNoteDetails(Note note) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(NoteDetailFragment.NOTE_TO_SHOW,
                (java.io.Serializable) note);

        changeFragment(NOTEDETAILSFRAGMENT, bundle);
    }

    @Override
    public void DisplayQuestion(Survey survey) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(QuestionFragment.SURVEY_TO_TAKE,
                (java.io.Serializable) survey);

        changeFragment(QUESTIONFRAGMENT, bundle);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            changeFragment(position);
        }

    }

    public void denyToast() {
        Toast.makeText(getApplicationContext(),
                "You do not have access to this feature",
                Toast.LENGTH_SHORT)
                .show();
    }

    public void changeFragment(int position) {
        Bundle args = new Bundle();
        changeFragment(position, args);
    }

    public void changeFragment(int position, Bundle args) {
        Fragment fragment = null;
        switch (position) {
            case ATTENDANCEFRAGMENT:
                if ( ! APIWrapper.getPermission("checkins").getOther() ) {
                    denyToast();
                    return;
                }

                fragment = new AttendanceFragment();
                break;
            case NOTESFRAGMENT:
                // permissions on per note bias
                fragment = new NotesFragment();
                break;
            case MYPROFILEFRAGMENT:
                // parts of profile will be blocked
                fragment = new ProfileFragment();
                ProfileFragment.mPerson = null;
                ProfileFragment.isLoggedInPerson = true;
                args.putSerializable(ProfileFragment.PERSON_TO_SHOW,
                        (java.io.Serializable) APIWrapper.getLoggedInPerson());
                fragment.setArguments(args);
                break;
            case SURVEYSFRAGMENT:
                // permissions on per survey bias
                fragment = new SurveysFragment();
                break;
            case FEEDBACKFRAGMENT:
                sendEmail();
                break;
            case ANNOUNCEMENTSFRAGMENT:
                // permissions on per announcement bias
                fragment = new AnnouncementsFragment();
                break;
            case LASTSCANNEDFRAGMENT:
                if ( ! APIWrapper.getPermission("person").getOther() ) {
                    denyToast();
                    return;
                }

                fragment = new ProfileFragment();
                ProfileFragment.mPerson = null;
                ProfileFragment.isLoggedInPerson = false;
                args.putSerializable(ProfileFragment.PERSON_TO_SHOW,
                        (java.io.Serializable) APIWrapper.getLastScannedPerson());
                fragment.setArguments(args);
                break;
            case CLASSBONUSESFRAGMENT:
                if ( ! APIWrapper.getPermission("classbonuses").getOther() ) {
                    denyToast();
                    return;
                }

                fragment = new ClassBonusFragment();
                fragment.setArguments(args);
                break;
            case PEOPLEFRAGMENT:
                if ( ! APIWrapper.getPermission("person").getOther() ) {
                    denyToast();
                    return;
                }

                fragment = new PeopleFragment();
                fragment.setArguments(args);
                break;
            case REGISTERFRAGMENT:
                if ( ! APIWrapper.getPermission("person").getOther() ) {
                    denyToast();
                    return;
                }

                fragment = new RegisterFragment();
                args.putString("card_id", mTagID);
                mTagID = "";
                fragment.setArguments(args);
                break;
            case PROFILEFRAGMENT:
                if ( ! APIWrapper.getPermission("person").getOther() ) {
                    denyToast();
                    return;
                }

                fragment = new ProfileFragment();
                ProfileFragment.mPerson = null;
                fragment.setArguments(args);
                mTitle = "Profile";
                break;
            case ANNOUNCEMENTSDETAILSFRAGMENT:
                fragment = new AnnouncementDetailFragment();
                fragment.setArguments(args);
                break;
            case NOTEDETAILSFRAGMENT:
                fragment = new NoteDetailFragment();
                fragment.setArguments(args);
                break;
            case QUESTIONFRAGMENT:
                fragment = new QuestionFragment();
                fragment.setArguments(args);
                break;
            case HOMEFRAGMENT:
            default:
                fragment = new HomeFragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, "current").addToBackStack("home").commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            if ( position < mNavTitles.size() ) {
                setTitle(mNavTitles.get(position));
            } else {
                setTitle(mTitle);
            }

            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment or sending email");
        }
    }

    private void sendEmail() {
        String[] TO = {"reorconsultants@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Org.nized Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }

    private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
            mAdapter.disableForegroundNdefPush(this);
        }
    }

    private void showWirelessSettingsDialog() {
        Log.e("NFC", "showWirelessSettingsDialog");
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.nfc_disabled);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
        return;*/
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Long tagID = getReversed(tag.getId());
                Log.e("tagID", tagID.toString() );
                checkInPerson(tagID);
            }
        }
    }


    public void checkInPerson(final Long tagID) {
        checkInPerson(tagID.toString());
    }


    public void checkInPerson(final String tagID) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("card_id", tagID);

        APIWrapper.get(APIWrapper.CHECK_IN_PERSON, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject checkin) {
                // If the response is JSONObject instead of expected JSONArray
                Person thisPerson = (Person) APIWrapper.parseJSONOjbect(
                        checkin,
                        Person.class);
                APIWrapper.setLastScannedPerson(thisPerson);
                Toast.makeText(getApplicationContext(),
                        "Scanned " + thisPerson.getFirst_name() +
                                " " + thisPerson.getLast_name(),
                        Toast.LENGTH_LONG)
                        .show();

                Intent intent = new Intent("updateNotify");
                // add data
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                // Pull out the first one
                try {
                    Person thisPerson = (Person) APIWrapper.parseJSONOjbect(
                            all_objs.getJSONObject(0),
                            Person.class);
                    APIWrapper.setLastScannedPerson(thisPerson);
                    Toast.makeText(getApplicationContext(),
                            "Scanned " + thisPerson.getFirst_name() +
                                    " " + thisPerson.getLast_name(),
                            Toast.LENGTH_LONG)
                            .show();

                    Intent intent = new Intent("updateNotify");
                    // add data
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Unable to gather data.",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("check in person failure", responseString);
                Toast.makeText(getApplicationContext(),
                        "Person has already checked in today.",
                        Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.w("check in person failure", response.toString());
                try {
                    switch (response.getInt("code")) {
                        case Checkins.UNKNOWN_USER:
                            Toast.makeText(getApplicationContext(),
                                    "Scanned card is unknown, please ask for email.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            mTagID = tagID;
                            changeFragment(REGISTERFRAGMENT);
                            break;
                        case Checkins.ALREADY_CHECKED_IN:
                            Person tempPerson = new Person();
                            tempPerson.setEmail(response.getString("email"));
                            APIWrapper.setLastScannedPerson(tempPerson);

                            Toast.makeText(getApplicationContext(),
                                    "Person has already checked in today.",
                                    Toast.LENGTH_LONG)
                                    .show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Role role) {
        Log.i("role submit", "Role dialog: " + role.toString());

        sendRole(role);
    }

    public void sendRole(Role role) {
        RequestParams requestParams = role.getPersonRequestParams();
        final ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("current");

        APIWrapper.post(APIWrapper.CREATE_PERSON_ROLE, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                Toast.makeText(getApplicationContext(),
                        "Person role created",
                        Toast.LENGTH_SHORT)
                        .show();

                profileFragment.getUpdatedProfile();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                Toast.makeText(getApplicationContext(),
                        "Person role created",
                        Toast.LENGTH_SHORT)
                        .show();

                profileFragment.getUpdatedProfile();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(String.valueOf(statusCode), responseString);
                Toast.makeText(getApplicationContext(),
                        "Unable to delete person role",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, ClassBonus classBonus) {
        // submit data
        Log.i("Submit", "Class Bonus Dialog: " + classBonus.toString());

        sendClassBonus(classBonus);
    }

    private void sendPersonClassBonus(ClassBonus classBonus) {
        RequestParams requestParams = classBonus.getPersonRequestParams();
        final ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("current");

        APIWrapper.post(APIWrapper.CREATE_PERSON_CLASS_BONUSES, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                Toast.makeText(getApplicationContext(),
                        "Class bonus created",
                        Toast.LENGTH_SHORT)
                        .show();

                profileFragment.getUpdatedProfile();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                Toast.makeText(getApplicationContext(),
                        "Class bonus created",
                        Toast.LENGTH_SHORT)
                        .show();

                profileFragment.getUpdatedProfile();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(String.valueOf(statusCode), responseString);
                Toast.makeText(getApplicationContext(),
                        "Unable to delete class bonuses",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void sendClassBonus(final ClassBonus classBonus) {
        RequestParams requestParams = classBonus.getRequestParams();

        APIWrapper.post(APIWrapper.CREATE_CLASS_BONUSES, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                try {
                    object.put("class_bonus_id", object.get("id"));
                    object.remove("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ClassBonus newClassBonus = (ClassBonus) APIWrapper.parseJSONOjbect(
                        object,
                        ClassBonus.class);
                newClassBonus.setEmail(classBonus.getEmail());

                sendPersonClassBonus(newClassBonus);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray all_objs) {
                try {
                    JSONObject object = all_objs.getJSONObject(0);
                    object.put("class_bonus_id", object.get("id"));
                    object.remove("id");

                    ClassBonus newClassBonus = (ClassBonus) APIWrapper.parseJSONOjbect(
                            object,
                            ClassBonus.class);
                    newClassBonus.setEmail(classBonus.getEmail());

                    sendPersonClassBonus(newClassBonus);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(String.valueOf(statusCode), responseString);
                Toast.makeText(getApplicationContext(),
                        "Unable to create class bonuses",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}
