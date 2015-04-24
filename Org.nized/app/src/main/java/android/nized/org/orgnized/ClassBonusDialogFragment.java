package android.nized.org.orgnized;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.nized.org.domain.ClassBonus;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by greg on 4/22/15.
 */
public class ClassBonusDialogFragment extends DialogFragment {
    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, ClassBonus classBonus);
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_classbonus, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(layout)
                // Add action buttons
                .setPositiveButton(R.string.submit_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        String classBonusStr = ((EditText) layout.findViewById(R.id.classBonusET)).getText().toString();
                        classBonusStr = classBonusStr.replace(" ", "").toUpperCase();
                        String spinnerStr = ((Spinner) layout.findViewById(R.id.classBonusSpinner)).getSelectedItem().toString();
                        ClassBonus classBonus = new ClassBonus();
                        classBonus.setCourseCode(classBonusStr);
                        classBonus.setSemester(spinnerStr);
                        classBonus.setEmail(ProfileFragment.currEmail);
                        mListener.onDialogPositiveClick(ClassBonusDialogFragment.this, classBonus);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ClassBonusDialogFragment.this.getDialog().cancel();
                    }
                });

        Spinner spinner = (Spinner) layout.findViewById(R.id.classBonusSpinner);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                        android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.semesters));
        spinner.setAdapter(adapter);

        return builder.create();
    }
}