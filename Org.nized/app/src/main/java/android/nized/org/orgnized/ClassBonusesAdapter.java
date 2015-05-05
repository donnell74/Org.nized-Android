package android.nized.org.orgnized;

import android.content.Context;
import android.nized.org.domain.ClassBonus;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by greg on 4/14/15.
 */
public class ClassBonusesAdapter extends ArrayAdapter<ClassBonus> {
    private final Context context;
    private List<ClassBonus> listData;
    private final LayoutInflater inflater;

    // View lookup cache
    private static class ViewHolder {
        TextView classCode;
        TextView semester;
    }

    public ClassBonusesAdapter(Context context, List<ClassBonus> classbonuses) {
        super(context, R.layout.classbonus_list_item, classbonuses);
        this.context = context;
        this.listData = classbonuses;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListData(List<ClassBonus> classBonuses) {
        this.listData = classBonuses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ClassBonus child = listData.get(position);

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

        convertView.setBackgroundColor(convertView.getResources().getColor(R.color.background_material_light));
        // Return the completed view to render on screen
        return convertView;
    }

    public void remove(ClassBonus classBonus) {
        listData.remove(classBonus);
    }
}
