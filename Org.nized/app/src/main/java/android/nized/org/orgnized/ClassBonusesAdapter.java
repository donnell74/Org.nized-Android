package android.nized.org.orgnized;

import android.content.Context;
import android.nized.org.domain.ClassBonus;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by greg on 4/14/15.
 */
public class ClassBonusesAdapter extends ArrayAdapter<ClassBonus> {
    private final Context context;
    private final List<ClassBonus> classbonuses;
    private final LayoutInflater inflater;

    // View lookup cache
    private static class ViewHolder {
        TextView classCode;
        TextView semester;
    }

    public ClassBonusesAdapter(Context context, List<ClassBonus> classbonuses) {
        super(context, R.layout.classbonus_list_item, classbonuses);
        this.context = context;
        this.classbonuses = classbonuses;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ClassBonus child = classbonuses.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.classbonus_list_item, null);
            viewHolder.classCode = (TextView) convertView.findViewById(R.id.classCode);
            viewHolder.semester = (TextView) convertView.findViewById(R.id.semester);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.classCode.setText(child.getCourse_code());
        viewHolder.semester.setText(child.getSemester());
        // Return the completed view to render on screen
        return convertView;
    }
}
