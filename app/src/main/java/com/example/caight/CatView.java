package com.example.caight;

import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.icu.util.Calendar;

import com.hrules.charter.CharterLine;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.ChildPosition;
import com.mindorks.placeholderview.annotations.expand.ParentPosition;

import java.time.Period;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Layout(R.layout.view_cat_view)
public class CatView
{
    private Cat cat = null;

    @View(R.id.colorView)
    private ImageView colorView = null;
    @View(R.id.genderImageView)
    private ImageView genderImageView = null;
    @View(R.id.nameTextView)
    private TextView nameTextView = null;
    @View(R.id.ageTextView)
    private TextView ageTextView = null;
    @View(R.id.weightTextView)
    private TextView weightTextView = null;
    @View(R.id.dateTextView)
    private TextView dateTextView = null;
    @View(R.id.weightChart)
    private CharterLine weightChart = null;
    @View(R.id.editButton)
    private ImageButton editButton = null;

    @ParentPosition
    private int mParentPosition;

    @ChildPosition
    private int mChildPosition;

    private Context mContext;

    private final OnCatAttributeChangedListener attributeChangedListener = new OnCatAttributeChangedListener()
    {
        @Override
        public void changed(int id, Object newValue)
        {
            updateCatAttr(id, newValue);
        }
    };

    public CatView(Context context, Cat cat)
    {
        mContext = context;
        setCat(cat);
    }

    private void init()
    {
        //
        // GUI Components
        //
        // weightChart
        weightChart.setIndicatorVisible(false);

        // editButton
        editButton.setFocusable(false);
    }

    public Cat getCat()
    {
        return cat;
    }

    public void setCat(Cat cat)
    {
        if (!cat.equals(this.cat))
        {
            if (this.cat != null)
            {
                this.cat.removeAttrChangedListener(attributeChangedListener);
            }
            cat.addAttrChangedListener(attributeChangedListener);

            this.cat = cat;
        }
    }

    private void updateCatAttr(int id, Object newValue)
    {
        switch (id)
        {
        case OnCatAttributeChangedListener.__ID_COLOR__:
            colorView.setImageTintList(ColorStateList.valueOf(this.cat.getColorInteger()));
            break;

        case OnCatAttributeChangedListener.__ID_NAME__:
            nameTextView.setText(this.cat.getName());
            break;

        case OnCatAttributeChangedListener.__ID_GENDER__:
            genderImageView.setImageDrawable(
                    mContext.getResources().getDrawable(
                            this.cat.isMale()
                                    ? R.drawable.ic_gender_male
                                    : R.drawable.ic_gender_female,
                            mContext.getTheme()));
            break;

        case OnCatAttributeChangedListener.__ID_BIRTHDAY__:
            Period age = this.cat.getAge();
            StringBuilder ageBuilder = new StringBuilder();
            ageBuilder.append('(');
            if (age.getYears() == 0)
            {
                ageBuilder.append(age.getMonths());
                ageBuilder.append("개월");
            }
            else
            {
                ageBuilder.append(age.getYears());
                ageBuilder.append("살");
            }
            ageBuilder.append(')');
            ageTextView.setText(ageBuilder.toString());
            break;

        case OnCatAttributeChangedListener.__ID_WEIGHTS__:
            StringBuilder weightBuilder = new StringBuilder();
            Map.Entry<Calendar, Float> lastEntry = this.cat.getLastWeight();
            weightBuilder.append(lastEntry.getValue());
            weightBuilder.append("Kg");
            weightTextView.setText(weightBuilder.toString());

            StringBuilder dateBuilder = new StringBuilder();
            dateBuilder.append('(');
            dateBuilder.append(lastEntry.getKey().get(Calendar.YEAR));
            dateBuilder.append('.');
            dateBuilder.append(lastEntry.getKey().get(Calendar.MONTH));
            dateBuilder.append('.');
            dateBuilder.append(lastEntry.getKey().get(Calendar.DAY_OF_MONTH));
            dateBuilder.append(')');
            dateTextView.setText(dateBuilder.toString());

            Collection<Float> wCollections = this.cat.getAllWeights();
            float[] weights = new float[wCollections.size()];
            Iterator<Float> iterator = wCollections.iterator();
            int idx = 0;
            while (iterator.hasNext())
            {
                weights[idx++] = iterator.next();
            }

            weightChart.setValues(weights.length == 1 ? new float[]{ weights[0], weights[0] } : weights);
            break;

        case OnCatAttributeChangedListener.__ID_SPECIES__:
            break;

        default:
            break;
        }
    }

    @Resolve
    private void updateInfo()
    {
        updateCatAttr(OnCatAttributeChangedListener.__ID_COLOR__, null);
        updateCatAttr(OnCatAttributeChangedListener.__ID_NAME__, null);
        updateCatAttr(OnCatAttributeChangedListener.__ID_GENDER__, null);
        updateCatAttr(OnCatAttributeChangedListener.__ID_BIRTHDAY__, null);
        updateCatAttr(OnCatAttributeChangedListener.__ID_WEIGHTS__, null);
    }
}
