package com.yjy.challengetogether.etc;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.yjy.challengetogether.R;

public class OneDayDecorator implements DayViewDecorator {

    private CalendarDay date;
    private final Drawable drawable;

    public OneDayDecorator(Activity context, boolean isAlone) {
        date = CalendarDay.today();

        if (isAlone) {
            drawable = context.getResources().getDrawable(R.drawable.calender_end_alone);
        } else {
            drawable = context.getResources().getDrawable(R.drawable.calender_end);
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.4f));
        view.setSelectionDrawable(drawable);
    }
}
