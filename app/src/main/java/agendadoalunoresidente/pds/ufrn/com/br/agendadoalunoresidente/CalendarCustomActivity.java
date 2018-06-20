package agendadoalunoresidente.pds.ufrn.com.br.agendadoalunoresidente;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.utils.DateUtils;

import java.util.*;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import agendadoalunoresidente.pds.ufrn.com.br.agendadoalunoresidente.service.GraduateStudentCalendarService;
import agendadoalunoresidente.pds.ufrn.com.br.agendadoalunoresidente.service.GraduateStudentService;
import agendaufrnfw.ufrn.imd.pds.model.calendar.CalendarUFRN;
import agendaufrnfw.ufrn.imd.pds.model.calendar.Commitment;
import agendaufrnfw.ufrn.imd.pds.model.calendar.DayWithoutWork;
import agendaufrnfw.ufrn.imd.pds.model.calendar.Evaluation;
import agendaufrnfw.ufrn.imd.pds.model.calendar.Holiday;
import agendaufrnfw.ufrn.imd.pds.model.calendar.InLocoLesson;
import agendaufrnfw.ufrn.imd.pds.model.calendar.Meeting;
import agendaufrnfw.ufrn.imd.pds.model.calendar.OrientationMeeting;
import agendaufrnfw.ufrn.imd.pds.model.calendar.StudyGroup;
import agendaufrnfw.ufrn.imd.pds.model.calendar.Task;
import agendaufrnfw.ufrn.imd.pds.model.user.GraduateStudent;

public class CalendarCustomActivity extends AppCompatActivity {
    TextView tvNome;
    TextView tvCurso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_custom);

        tvNome = (TextView) findViewById(R.id.tvNome);
        tvCurso = (TextView) findViewById(R.id.tvCurso);

        criarBarraMenu();

        CalendarView calendarView = (CalendarView) findViewById(R.id.idCustomCalendarView);

        List<EventDay> allEvents = new ArrayList<EventDay>();
        allEvents = populaEvents();

        try {
            criarCalendario(calendarView, allEvents);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        calendarView.setOnDayClickListener(eventDay ->
                Toast.makeText(getApplicationContext(),
                        eventDay.getCalendar().getTime().toString() + " "
                                + eventDay.isEnabled(),
                        Toast.LENGTH_SHORT).show());
    }

    private List<EventDay> populaEvents(){
        CalendarUFRN cDto = null;
        GraduateStudent gsDto = null;
        if(getIntent().hasExtra("token")){
            String token = getIntent().getStringExtra("token");
            GraduateStudentService graduateStudentService = new GraduateStudentService(token);
            try {
                GraduateStudentCalendarService graduateStudentCalendarService = new GraduateStudentCalendarService();
                cDto = graduateStudentCalendarService.execute().get();
                gsDto = graduateStudentService.execute().get();
                tvNome.setText(gsDto.getNome_discente());
                tvCurso.setText(gsDto.getNome_curso());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        List<EventDay> allEvents = new ArrayList<EventDay>();
        List<Commitment> allCommitments = new ArrayList<>();
        allCommitments.addAll(gsDto.getCommitments());
        allCommitments.addAll(Arrays.asList(cDto.getHolidays()));

        allEvents = criaListaEventos(allCommitments);

        return allEvents;
    }

    private List<EventDay> criaListaEventos(List<Commitment> commitments){
        List<EventDay> events = new ArrayList<>();

        for(Commitment commitment : commitments){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(commitment.getFinalDate());
            EventDay eventDay = null;
            if(commitment instanceof Task){
                eventDay = new EventDay(c, R.drawable.task_icon);
            } else if (commitment instanceof Evaluation) {
                eventDay = new EventDay(c, R.drawable.evaluation_icon);
            } else if (commitment instanceof Holiday) {
                eventDay = new EventDay(c, R.drawable.holiday);
            } else if (commitment instanceof Meeting){
                eventDay = new EventDay(c, R.drawable.meeting);
            } else if (commitment instanceof OrientationMeeting){
                eventDay = new EventDay(c, R.drawable.orientation_meeting);
            } else if(commitment instanceof StudyGroup){
                eventDay = new EventDay(c, R.drawable.study_group);
            } else if(commitment instanceof DayWithoutWork){
                eventDay = new EventDay(c, R.drawable.brazilday);
            } else if(commitment instanceof InLocoLesson){
                eventDay = new EventDay(c, R.drawable.inlocolesson);
            } else {
                eventDay = new EventDay(c, R.drawable.sample_icon_1);
            }
            events.add(eventDay);
        }

        return events;
    }

    private void criarCalendario(CalendarView calendarView, List<EventDay> events) throws OutOfDateRangeException {


        java.util.Calendar min = java.util.Calendar.getInstance();
        min.add(java.util.Calendar.MONTH, -1);

        java.util.Calendar max = java.util.Calendar.getInstance();
        max.add(java.util.Calendar.MONTH, 2);

        //calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);

        calendarView.setEvents(events);

        java.util.Calendar today = java.util.Calendar.getInstance();
        calendarView.setDate(today);

        //calendarView.setDisabledDays(getDisabledDays());
    }

    private List<java.util.Calendar> getDisabledDays() {
        java.util.Calendar firstDisabled = DateUtils.getCalendar();
        firstDisabled.add(java.util.Calendar.DAY_OF_MONTH, 2);

        java.util.Calendar secondDisabled = DateUtils.getCalendar();
        secondDisabled.add(java.util.Calendar.DAY_OF_MONTH, 1);

        java.util.Calendar thirdDisabled = DateUtils.getCalendar();
        thirdDisabled.add(java.util.Calendar.DAY_OF_MONTH, 18);

        List<java.util.Calendar> calendars = new ArrayList<>();
        calendars.add(firstDisabled);
        calendars.add(secondDisabled);
        calendars.add(thirdDisabled);
        return calendars;
    }

    public void criarBarraMenu() {
        Toolbar menuSIGAgenda = (Toolbar) findViewById(R.id.menu);
        setSupportActionBar(menuSIGAgenda);
        menuSIGAgenda.setLogo(R.mipmap.ic_launcher);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        String token = "";
        int id = item.getItemId();
        switch (id){
            case R.id.item_calendario_compromissos:
                intent = new Intent();
                intent.setClass(this, CalendarCustomActivity.class);
                token = getIntent().getStringExtra("token");
                intent.putExtra("token", token);
                startActivity(intent);
                return true;
            case R.id.item_tarefas_avaliacoes:
                intent = new Intent();
                intent.setClass(this, StudentActivity.class);
                token = getIntent().getStringExtra("token");
                intent.putExtra("token", token);
                startActivity(intent);
                return true;
            case R.id.item_calendario:
                intent = new Intent();
                intent.setClass(this, CalendarActivity.class);
                token = getIntent().getStringExtra("token");
                intent.putExtra("token", token);
                startActivity(intent);
                return true;
            case R.id.item_emprestimos:
                intent = new Intent();
                intent.setClass(this, LoanActivity.class);
                token = getIntent().getStringExtra("token");
                intent.putExtra("token", token);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
