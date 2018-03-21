# CalendarView

![example](art/1.png)

### example

```xml
<me.t.view.calendar.CalendarView
    android:id="@+id/calendar"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

```java
// single select mode
calendarView.setMode(CalendarView.Mode.SINGLE);
calendarView.setCalendarSingleSelectListener(new CalendarView.CalendarSingleSelectListener() {
    @Override
    public void onDateSingleSelected(Calendar calendar) {
		
    }
});

// range select mode
calendarView.setMode(CalendarView.Mode.RANGE);
calendarView.setCalendarRangeSelectListener(new CalendarView.CalendarRangeSelectListener() {
    @Override
    public void onDateRangeSelected(Calendar startCalendar, Calendar endCalendar) {
		
    }
});

// multi select mode
calendarView.setMode(CalendarView.Mode.MULTI);
List<Calendar> calendars = calendarView.getMultiSelectedCalendars();

// reset
calendarView.reset();
```