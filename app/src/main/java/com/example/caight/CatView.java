package com.example.caight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

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

@Layout(R.layout.view_cat)
public class CatView extends EntityListItemViewBase
{
    private Cat cat = null;
    private CatGroup group = null;

    @View(R.id.rootLayout)
    private ConstraintLayout rootLayout;
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
    @View(R.id.deleteImageButton)
    private ImageButton deleteButton = null;
    @View(R.id.editButton)
    private ImageButton editButton = null;

    @ParentPosition
    private int mParentPosition;

    @ChildPosition
    private int mChildPosition;

    private final OnCatAttributeChangedListener attributeChangedListener = new OnCatAttributeChangedListener()
    {
        @Override
        public void changed(int id, Object newValue)
        {
            updateCatAttr(id, newValue);
        }
    };

    private OnEntityListItemTouchListener onTouchListener = null;
    private OnEntityListItemTouchListener onDeleteListener = null;
    private OnEntityListItemTouchListener onEditListener = null;

    public CatView(Context context, Cat cat, CatGroup group)
    {
        super(context);
        setCat(cat);
        this.group = group;
    }

    public CatGroup getGroup()
    {
        return group;
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

    public void setOnTouchListener(OnEntityListItemTouchListener l)
    {
        onTouchListener = l;
    }

    public void setOnDeleteListener(OnEntityListItemTouchListener l)
    {
        onDeleteListener = l;
    }

    public void setOnEditListener(OnEntityListItemTouchListener l)
    {
        onEditListener = l;
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
                        getContext().getResources().getDrawable(
                                this.cat.isMale()
                                        ? R.drawable.ic_gender_male
                                        : R.drawable.ic_gender_female,
                                getContext().getTheme()));
                break;

            case OnCatAttributeChangedListener.__ID_BIRTHDAY__:
                int[] age = this.cat.getAge();
                StringBuilder ageBuilder = new StringBuilder();
                ageBuilder.append('(');
                if (age[0] == 0)
                {
                    ageBuilder.append(age[1]);
                    ageBuilder.append(getContext().getResources().getString(R.string.unit_old_month));
                }
                else
                {
                    ageBuilder.append(age[0]);
                    ageBuilder.append(getContext().getResources().getString(R.string.unit_old_year));
                }
                ageBuilder.append(')');
                ageTextView.setText(ageBuilder.toString());
                break;

            case OnCatAttributeChangedListener.__ID_WEIGHTS__:
                if (this.cat.getAllWeights().size() > 0)
                {
                    StringBuilder weightBuilder = new StringBuilder();
                    Map.Entry<Date, Float> lastEntry = this.cat.getLastWeight();
                    weightBuilder.append(lastEntry.getValue());
                    weightBuilder.append(getContext().getResources().getString(R.string.unit_kg));
                    weightTextView.setText(weightBuilder.toString());

                    StringBuilder dateBuilder = new StringBuilder();
                    dateBuilder.append('(');
                    dateBuilder.append(lastEntry.getKey().getYear());
                    dateBuilder.append('.');
                    dateBuilder.append(lastEntry.getKey().getMonth());
                    dateBuilder.append('.');
                    dateBuilder.append(lastEntry.getKey().getDay());
                    dateBuilder.append(')');
                    dateTextView.setText(dateBuilder.toString());

                    Collection<Float> wCollections = this.cat.getAllWeights().values();
                    float[] weights = new float[wCollections.size()];
                    Iterator<Float> iterator = wCollections.iterator();
                    int idx = 0;
                    while (iterator.hasNext())
                    {
                        weights[idx++] = iterator.next();
                    }

                    weightChart.setValues(weights.length == 1 ? new float[] { weights[0], weights[0] } : weights);
                }
                else
                {
                    weightTextView.setText(null);
                    dateTextView.setText(null);
                    weightChart.setValues(new float[0]);
                }
                break;

            case OnCatAttributeChangedListener.__ID_SPECIES__:
                break;

            default:
                break;
        }
    }

    @Override
    protected void setGuiComponents()
    {
        // weightChart
        weightChart.setIndicatorVisible(false);

        // editButton
        editButton.setFocusable(false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setEventListeners()
    {
        rootLayout.setOnTouchListener(new android.view.View.OnTouchListener()
        {
            @Override
            public boolean onTouch(android.view.View v, MotionEvent event)
            {
                if (onTouchListener != null)
                {
                    onTouchListener.onTouch(getThisEntity(), event);
                }
                return false;
            }
        });

        deleteButton.setOnTouchListener(new android.view.View.OnTouchListener()
        {
            @Override
            public boolean onTouch(android.view.View v, MotionEvent event)
            {
                if (onDeleteListener != null)
                {
                    onDeleteListener.onTouch(getThisEntity(), event);
                }
                return false;
            }
        });

        editButton.setOnTouchListener(new android.view.View.OnTouchListener()
        {
            @Override
            public boolean onTouch(android.view.View v, MotionEvent event)
            {
                if (onEditListener != null)
                {
                    onEditListener.onTouch(getThisEntity(), event);
                }
                return false;
            }
        });
    }

    @Override
    @Resolve
    protected void onResolve()
    {
        super.onResolve();

        updateCatAttr(OnCatAttributeChangedListener.__ID_COLOR__, null);
        updateCatAttr(OnCatAttributeChangedListener.__ID_NAME__, null);
        updateCatAttr(OnCatAttributeChangedListener.__ID_GENDER__, null);
        updateCatAttr(OnCatAttributeChangedListener.__ID_BIRTHDAY__, null);
        updateCatAttr(OnCatAttributeChangedListener.__ID_WEIGHTS__, null);
    }

    @Override
    protected void onExpand()
    {

    }

    @Override
    protected void onCollapse()
    {

    }
}