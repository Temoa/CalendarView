package me.t.okcalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.t.view.okcalendar.OkCalendarView;

public class MainActivity extends AppCompatActivity {

    private SimpleDateFormat mFormatter;

    private RadioGroup mRadioGroup;
    private OkCalendarView mOkCalendarView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        mRadioGroup = findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_single:
                        mOkCalendarView.setMode(OkCalendarView.Mode.SINGLE);
                        mOkCalendarView.reset();
                        mButton.setVisibility(View.GONE);
                        break;
                    case R.id.radio_multi:
                        mOkCalendarView.setMode(OkCalendarView.Mode.MULTI);
                        mOkCalendarView.reset();
                        mButton.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radio_range:
                        mOkCalendarView.setMode(OkCalendarView.Mode.RANGE);
                        mOkCalendarView.reset();
                        mButton.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        });
        mButton = findViewById(R.id.button);
        mOkCalendarView = findViewById(R.id.calendar);
        mOkCalendarView.setMode(OkCalendarView.Mode.SINGLE);
        mOkCalendarView.setCalendarRangeSelectListener(new OkCalendarView.CalendarRangeSelectListener() {
            @Override
            public void onDateRangeSelected(Calendar startCalendar, Calendar endCalendar) {
                String startDate = formatCalendar2Date(startCalendar);
                String endDate = formatCalendar2Date(endCalendar);
                Toast.makeText(MainActivity.this, startDate + " " + endDate, Toast.LENGTH_SHORT).show();
            }
        });
        mOkCalendarView.setCalendarSingleSelectListener(new OkCalendarView.CalendarSingleSelectListener() {
            @Override
            public void onDateSingleSelected(Calendar calendar) {
                String date = formatCalendar2Date(calendar);
                Toast.makeText(MainActivity.this, date, Toast.LENGTH_SHORT).show();
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Calendar> calendars = mOkCalendarView.getMultiSelectedCalendars();
                StringBuilder msgBuilder = new StringBuilder();
                for (Calendar calendar : calendars) {
                    msgBuilder.append(formatCalendar2Date(calendar));
                    msgBuilder.append(" ");
                }
                Toast.makeText(MainActivity.this, msgBuilder, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatCalendar2Date(Calendar calendar) {
        return mFormatter.format(calendar.getTime());
    }
}
