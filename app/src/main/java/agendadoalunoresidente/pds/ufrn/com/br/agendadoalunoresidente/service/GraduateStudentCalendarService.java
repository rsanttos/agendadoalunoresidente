package agendadoalunoresidente.pds.ufrn.com.br.agendadoalunoresidente.service;

import android.os.AsyncTask;
import android.util.Log;

import agendaufrnfw.ufrn.imd.pds.dto.GraduateStudentCalendarDTO;
import agendaufrnfw.ufrn.imd.pds.model.calendar.GraduateStudentCalendar;
import agendaufrnfw.ufrn.imd.pds.request.CalendarRequest;

/**
 * Created by root on 24/04/18.
 */

public class GraduateStudentCalendarService extends AsyncTask<Void, Void, GraduateStudentCalendar> {

    private GraduateStudentCalendar calendar;

    public GraduateStudentCalendar getCalendar(){
        CalendarRequest calendarRequest = new CalendarRequest();
        GraduateStudentCalendarDTO cDto = new GraduateStudentCalendarDTO();
        return cDto.toObject(calendarRequest.getCalendar());
    }
    @Override
    protected GraduateStudentCalendar doInBackground(Void... params) {
        calendar = getCalendar();
        return calendar;
    }

    @Override
    protected void onPostExecute(GraduateStudentCalendar calendar){
        Log.v("Calendar", String.valueOf(calendar.getAno()));
    }

}
