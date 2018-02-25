package me.t.okcalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.t.view.okcalendar.OkCalendarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkCalendarView calendarView = findViewById(R.id.calendar);
        calendarView.setCalendarListener(new OkCalendarView.CalendarListener() {
            @Override
            public void onDateRangeSelected(Calendar startCalendar, Calendar endCalendar) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String startDate = simpleDateFormat.format(startCalendar.getTime());
                String endDate = simpleDateFormat.format(endCalendar.getTime());
                Toast.makeText(MainActivity.this, startDate + " " + endDate, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
