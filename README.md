# CalendarView

![example](art/1.png)

### example

```xml
<me.t.view.okcalendar.OkCalendarView
            android:id="@+id/calendar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />
```

```java
// single select mode
mOkCalendarView.setMode(OkCalendarView.Mode.SINGLE);
mOkCalendarView.setCalendarSingleSelectListener(new OkCalendarView.CalendarSingleSelectListener() {
	@Override
	public void onDateSingleSelected(Calendar calendar) {
		
	}
});

// range select mode
mOkCalendarView.setMode(OkCalendarView.Mode.RANGE);
mOkCalendarView.setCalendarRangeSelectListener(new OkCalendarView.CalendarRangeSelectListener() {
	@Override
	public void onDateRangeSelected(Calendar startCalendar, Calendar endCalendar) {
		
	}
});

// multi select mode
mOkCalendarView.setMode(OkCalendarView.Mode.MULTI);
List<Calendar> calendars = mOkCalendarView.getMultiSelectedCalendars();

// reset
mOkCalendarView.reset();
```