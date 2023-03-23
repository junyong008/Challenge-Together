package com.yjy.challengetogether.etc;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.yjy.challengetogether.R;

public class CustomDayDecorator implements DayViewDecorator {

    private CalendarDay date;
    private final Drawable drawable;

    public CustomDayDecorator(Activity context, CalendarDay date, boolean isStart) {
        this.date = date;;
        if (isStart) {
            drawable = context.getResources().getDrawable(R.drawable.calender_start);
        } else {
            drawable = context.getResources().getDrawable(R.drawable.calender_end);
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(this.date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
