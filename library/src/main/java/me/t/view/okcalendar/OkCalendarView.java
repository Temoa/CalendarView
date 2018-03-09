package me.t.view.okcalendar;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by lai
 * on 2018/2/25.
 */

public class OkCalendarView extends LinearLayout implements View.OnClickListener {

    private CalendarAdapter mCalendarAdapter;
    private TextView mDateTv;

    private Mode mMode = Mode.SINGLE;
    private Calendar mCalendar;
    private int mMonthCount = 0;

    private CalendarRangeSelectListener mCalendarRangeSelectListener;
    private CalendarSingleSelectListener mCalendarSingleSelectListener;

    public enum Mode {
        SINGLE,
        MULTI,
        RANGE
    }

    public void setMode(Mode mode) {
        mMode = mode;
    }

    public void setCalendarRangeSelectListener(
            CalendarRangeSelectListener calendarRangeSelectListener) {

        mCalendarRangeSelectListener = calendarRangeSelectListener;
    }

    public void setCalendarSingleSelectListener(
            CalendarSingleSelectListener calendarSingleSelectListener) {

        mCalendarSingleSelectListener = calendarSingleSelectListener;
    }

    public List<Calendar> getMultiSelectedCalendars() {
        return mCalendarAdapter.mMultiSelectedCalendars;
    }

    public void reset() {
        showMonth();
    }

    public OkCalendarView(Context context) {
        super(context);
        init(context);
    }

    public OkCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OkCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mCalendar = Calendar.getInstance();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        LinearLayout root = (LinearLayout) layoutInflater.inflate(R.layout.ok_calendar_month, this, true);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        mDateTv = root.findViewById(R.id.tv_date);
        ImageView nextMonthIv = root.findViewById(R.id.iv_next_month);
        ImageView prvMonthIv = root.findViewById(R.id.iv_prv_month);
        nextMonthIv.setOnClickListener(this);
        prvMonthIv.setOnClickListener(this);
        LinearLayout weeksLayout = root.findViewById(R.id.layout_weeks);
        for (int i = 0; i < 7; i++) {
            TextView weekTv = (TextView) weeksLayout.getChildAt(i);
            int weekStringResourceId;
            switch (i) {
                case 1:
                    weekStringResourceId = R.string.ok_calendar_week_mon;
                    break;
                case 2:
                    weekStringResourceId = R.string.ok_calendar_week_tue;
                    break;
                case 3:
                    weekStringResourceId = R.string.ok_calendar_week_web;
                    break;
                case 4:
                    weekStringResourceId = R.string.ok_calendar_week_thu;
                    break;
                case 5:
                    weekStringResourceId = R.string.ok_calendar_week_fir;
                    break;
                case 6:
                    weekStringResourceId = R.string.ok_calendar_week_sat;
                    break;
                default:
                    weekStringResourceId = R.string.ok_calendar_week_sun;
                    break;
            }
            weekTv.setText(weekStringResourceId);
            weekTv.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        recyclerView.setLayoutManager(new GridLayoutManager(context, 7));
        recyclerView.getItemAnimator().setAddDuration(0);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.getItemAnimator().setMoveDuration(0);
        recyclerView.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mCalendarAdapter = new CalendarAdapter();
        recyclerView.setAdapter(mCalendarAdapter);
        showMonth();
    }

    private void showMonth() {
        Calendar temp = (Calendar) mCalendar.clone();
        temp.set(mCalendar.get(Calendar.YEAR), (mCalendar.get(Calendar.MONTH) + mMonthCount), 1);
        int firstDay = temp.get(Calendar.DAY_OF_WEEK) - 1;
        int year = temp.get(Calendar.YEAR);
        int month = temp.get(Calendar.MONTH);
        int daysInMonth = getDaysInMonth(month, year);
//        int daysInLastMonth = getDaysInMonth(month - 1, year);
//        int daysInNextMonth = getDaysInMonth(month + 1, year);
        List<DayItem> days = new ArrayList<>();
        for (int i = 0; i < firstDay; i++) {
            days.add(new DayItem("", /*isSelect*/ false, /*isThisMonth*/ false));
        }
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(new DayItem(String.valueOf(i), /*isSelect*/ false, /*isThisMonth*/ true));
        }
        mCalendarAdapter.reset();
        mCalendarAdapter.setNewData(days);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(temp.getTime());
        mDateTv.setText(dateString);
    }

    public void nextMonth() {
        mMonthCount++;
        showMonth();
    }

    public void prvMonth() {
        mMonthCount--;
        showMonth();
    }

    public int getDaysInMonth(int month, int year) {
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                return 31;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                return 30;
            case Calendar.FEBRUARY:
                return (year % 4 == 0) ? 29 : 28;
            default:
                throw new IllegalArgumentException("Invalid Month");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_next_month) {
            nextMonth();
        } else if (id == R.id.iv_prv_month) {
            prvMonth();
        }
    }

    class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

        private List<DayItem> mItems;
        private int mSelectCount = 0;
        private int mLastSelectPosition = -1;

        List<Calendar> mMultiSelectedCalendars = new ArrayList<>();

        void reset() {
            mSelectCount = 0;
            mLastSelectPosition = -1;
            mMultiSelectedCalendars.clear();
        }

        void setNewData(List<DayItem> newData) {
            mItems = newData;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ok_calendar_item_day_of_month, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            DayItem item = mItems.get(position);
            holder.tv.setTextColor(Color.BLACK);
            if (item.isThisMonth) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMode == Mode.SINGLE) {
                            singleSelectEvent(holder);
                        } else if (mMode == Mode.MULTI) {
                            multiSelectEvent(holder);
                        } else {
                            rangeSelectEvent(holder);
                        }
                    }
                });
                if (item.isSelect()) {
                    holder.tv.setBackgroundResource(R.drawable.ok_calendar_shape_calendar_selected);
                    holder.tv.setTextColor(Color.WHITE);
                } else {
                    holder.tv.setBackground(null);
                    holder.tv.setTextColor(Color.BLACK);
                }
            } else {
                holder.tv.setTextColor(Color.GRAY);
                holder.tv.setBackground(null);
                holder.tv.setTextColor(Color.BLACK);
                holder.tv.setOnClickListener(null);
            }
            holder.tv.setText(item.getDay());
        }

        private void singleSelectEvent(ViewHolder holder) {
            if (mLastSelectPosition != -1) {
                mItems.get(mLastSelectPosition).setSelect(false);
                notifyItemChanged(mLastSelectPosition);
            }
            int curPosition = holder.getAdapterPosition();
            mItems.get(curPosition).setSelect(true);
            notifyItemChanged(curPosition);
            if (mCalendarSingleSelectListener != null) {
                int day = Integer.valueOf(mItems.get(curPosition).getDay());
                Calendar selectedCalendar = getCalendarByDay(day);
                mCalendarSingleSelectListener.onDateSingleSelected(selectedCalendar);
            }
            mLastSelectPosition = curPosition;
        }

        private void multiSelectEvent(ViewHolder holder) {
            int curPosition = holder.getAdapterPosition();
            DayItem item = mItems.get(curPosition);
            int day = Integer.valueOf(mItems.get(curPosition).getDay());
            Calendar selectedCalendar = getCalendarByDay(day);
            if (item.isSelect()) {
                mItems.get(curPosition).setSelect(false);
                notifyItemChanged(curPosition);
                mMultiSelectedCalendars.remove(selectedCalendar);
                return;
            }
            mItems.get(curPosition).setSelect(true);
            notifyItemChanged(curPosition);
            mMultiSelectedCalendars.add(selectedCalendar);
        }

        private void rangeSelectEvent(ViewHolder holder) {
            if (mSelectCount == 0) {
                mSelectCount++;
                mLastSelectPosition = holder.getAdapterPosition();
                mItems.get(holder.getAdapterPosition()).setSelect(true);
                notifyItemChanged(holder.getAdapterPosition());
            } else if (mSelectCount == 1) {
                mSelectCount++;
                int curPosition = holder.getAdapterPosition();
                if (curPosition > mLastSelectPosition) {
                    for (int i = mLastSelectPosition; i <= curPosition; i++) {
                        mItems.get(i).setSelect(true);
                    }
                    notifyItemRangeChanged(
                            mLastSelectPosition,
                            curPosition - mLastSelectPosition + 1);

                    if (mCalendarRangeSelectListener != null) {
                        int startDay = Integer.valueOf(mItems.get(mLastSelectPosition).getDay());
                        Calendar startCalendar = getCalendarByDay(startDay);
                        int endDay = Integer.valueOf(mItems.get(curPosition).getDay());
                        Calendar endCalendar = getCalendarByDay(endDay);
                        mCalendarRangeSelectListener.onDateRangeSelected(startCalendar, endCalendar);
                    }
                } else {
                    cancelAllSelect();
                }
            } else {
                cancelAllSelect();
            }
        }

        private Calendar getCalendarByDay(int day) {
            Calendar calendar = (Calendar) mCalendar.clone();
            calendar.set(
                    mCalendar.get(Calendar.YEAR),
                    (mCalendar.get(Calendar.MONTH) + mMonthCount),
                    day);
            return calendar;
        }

        private void cancelAllSelect() {
            mSelectCount = 0;
            for (DayItem day : mItems) {
                day.setSelect(false);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mItems == null ? 0 : mItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tv;

            ViewHolder(View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.item_tv);
            }
        }
    }

    static class DayItem {
        private String day;
        private boolean isSelect;
        private boolean isThisMonth;

        DayItem(String day, boolean isSelect, boolean isThisMonth) {
            this.day = day;
            this.isSelect = isSelect;
            this.isThisMonth = isThisMonth;
        }

        String getDay() {
            return day;
        }

        boolean isSelect() {
            return isSelect;
        }

        void setSelect(boolean select) {
            isSelect = select;
        }
    }

    public interface CalendarSingleSelectListener {
        void onDateSingleSelected(Calendar calendar);
    }

    public interface CalendarRangeSelectListener {
        void onDateRangeSelected(Calendar startCalendar, Calendar endCalendar);
    }
}
