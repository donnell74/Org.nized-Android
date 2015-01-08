package android.nized.org.orgnized;

/**
 * Created by greg on 12/21/14.
 */
import android.nized.org.api.APIWrapper;
import android.nized.org.domain.Person;
import android.view.View;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class NotesFragment extends Fragment {
    private Person myPerson = null;

    public NotesFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);

        myPerson = APIWrapper.getLoggedInPerson();

        return rootView;
    }
}