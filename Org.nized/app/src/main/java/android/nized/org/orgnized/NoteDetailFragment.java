package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */

import android.nized.org.domain.Announcement;
import android.nized.org.domain.Note;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NoteDetailFragment extends Fragment {

    View main_layout = null;
    public static String NOTE_TO_SHOW = "note_to_show";
    private Note mNote;

    public NoteDetailFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_layout = inflater.inflate(R.layout.fragment_announcement_detail, container, false);

        Bundle args = getArguments();
        mNote = (Note) args.getSerializable(NOTE_TO_SHOW);

        Log.i("announcement detail", mNote.toString());
        TextView titleTV = (TextView) main_layout.findViewById(R.id.titleTV);
        TextView textTV = (TextView) main_layout.findViewById(R.id.textTV);

        titleTV.setText(mNote.getTitle());
        textTV.setText(mNote.getText());

        return main_layout;
    }
}