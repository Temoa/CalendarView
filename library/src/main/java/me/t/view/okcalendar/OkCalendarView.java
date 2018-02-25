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

    private Calendar mCalendar;
    private int mMonthCount = 0;

    private CalendarListener mCalendarListener;


    public void setCalendarListener(CalendarListener calendarListener) {
        mCalendarListener = calendarListener;
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
        List<MonthItem> days = new ArrayList<>();
        for (int i = 0; i < firstDay; i++) {
            days.add(new MonthItem("", false, false));
        }
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(new MonthItem(String.valueOf(i), false, true));
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

        private List<MonthItem> mItems;
        private int mSelectCount = 0;
        private int mLastSelectPosition;
        private int mCurSelectPosition;


        void reset() {
            mSelectCount = 0;
            mLastSelectPosition = 0;
            mCurSelectPosition = 0;
        }


        void setNewData(List<MonthItem> newData) {
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
            MonthItem item = mItems.get(position);
            holder.tv.setTextColor(Color.BLACK);
            if (item.isThisMonth) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickEvent(holder);
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
            }
            holder.tv.setText(item.getDay());
        }


        private void clickEvent(ViewHolder holder) {
            if (mSelectCount == 0) {
                mSelectCount++;
                mLastSelectPosition = holder.getAdapterPosition();
                mItems.get(holder.getAdapterPosition()).setSelect(true);
                notifyItemChanged(holder.getAdapterPosition());
            } else if (mSelectCount == 1) {
                mSelectCount++;
                mCurSelectPosition = holder.getAdapterPosition();
                if (mCurSelectPosition > mLastSelectPosition) {
                    for (int i = mLastSelectPosition; i <= mCurSelectPosition; i++) {
                        mItems.get(i).setSelect(true);
                    }
                    notifyItemRangeChanged(
                            mLastSelectPosition,
                            mCurSelectPosition - mLastSelectPosition + 1);

                    Calendar startCalendar = (Calendar) mCalendar.clone();
                    startCalendar.set(
                            mCalendar.get(Calendar.YEAR),
                            (mCalendar.get(Calendar.MONTH) + mMonthCount),
                            Integer.valueOf(mItems.get(mLastSelectPosition).getDay()));

                    Calendar endCalendar = (Calendar) mCalendar.clone();
                    endCalendar.set(
                            mCalendar.get(Calendar.YEAR),
                            (mCalendar.get(Calendar.MONTH) + mMonthCount),
                            Integer.valueOf(mItems.get(mCurSelectPosition).getDay()));

                    if (mCalendarListener != null) {
                        mCalendarListener.onDateRangeSelected(startCalendar, endCalendar);
                    }
                } else {
                    mSelectCount = 0;
                    for (MonthItem day : mItems) {
                        day.setSelect(false);
                    }
                    notifyDataSetChanged();
                }
            } else {
                mSelectCount = 0;
                for (MonthItem day : mItems) {
                    day.setSelect(false);
                }
                notifyDataSetChanged();
            }
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


    static class MonthItem {
        private String day;
        private boolean isSelect;
        private boolean isThisMonth;

        MonthItem(String day, boolean isSelect, boolean isThisMonth) {
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

    public interface CalendarListener {
        void onDateRangeSelected(Calendar startCalendar, Calendar endCalendar);
    }
}
