package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */

import android.app.Activity;
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Announcement;
import android.nized.org.domain.Person;
import android.nized.org.domain.Role;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementDetailFragment extends Fragment {

    View main_layout = null;
    public static String Announcement_to_show = "announcement_to_show";
    private Announcement mAnnouncement;

    public AnnouncementDetailFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_layout = inflater.inflate(R.layout.fragment_announcement_detail, container, false);

        Bundle args = getArguments();
        mAnnouncement = (Announcement) args.getSerializable(Announcement_to_show);

        Log.i("announcement detail", mAnnouncement.toString());
        TextView titleTV = (TextView) main_layout.findViewById(R.id.titleTV);
        TextView textTV = (TextView) main_layout.findViewById(R.id.textTV);

        titleTV.setText(mAnnouncement.getTitle());
        textTV.setText(mAnnouncement.getText());

        return main_layout;
    }

}