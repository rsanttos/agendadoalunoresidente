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
import java.util.concurrent.ExecutionException;

import agendadoalunoresidente.pds.ufrn.com.br.agendadoalunoresidente.service.CalendarService;
import agendadoalunoresidente.pds.ufrn.com.br.agendadoalunoresidente.service.ProfessorService;
import agendadoalunoresidente.pds.ufrn.com.br.agendadoalunoresidente.service.StudentService;
import agendaufrnfw.ufrn.imd.pds.dto.CalendarDTO;
import agendaufrnfw.ufrn.imd.pds.dto.ClassDTO;
import agendaufrnfw.ufrn.imd.pds.dto.EvaluationDTO;
import agendaufrnfw.ufrn.imd.pds.dto.HolidayDTO;
import agendaufrnfw.ufrn.imd.pds.dto.TaskDTO;
import agendaufrnfw.ufrn.imd.pds.model.Professor;
import agendaufrnfw.ufrn.imd.pds.model.Student;

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
        List<TaskDTO> allTasks = new ArrayList<TaskDTO>();
        List<EvaluationDTO> allEvaluations = new ArrayList<EvaluationDTO>();
        CalendarDTO cDto = null;
        if(getIntent().hasExtra("token")){
            String token = getIntent().getStringExtra("token");
            StudentService studentService = new StudentService(token);
            try {
                CalendarService calendarService = new CalendarService();
                cDto = calendarService.execute().get();
                Student sDto = studentService.execute().get();
                tvNome.setText(sDto.getNome_discente());
                tvCurso.setText(sDto.getNome_curso());
                for(ClassDTO classe : sDto.getClasses()){
                    allTasks.addAll(classe.getTasks());
                    allEvaluations.addAll(classe.getEvaluations());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        List<EventDay> allEvents = new ArrayList<EventDay>();
        allEvents.addAll(criaListaTasks(allTasks));
        allEvents.addAll(criaListaEvaluations(allEvaluations));
        allEvents.addAll(criaListaEventosCalendario(cDto));

        return allEvents;
    }

    private List<EventDay> criaListaEvaluations(List<EvaluationDTO> evaluations){
        List<EventDay> eventsTasks = new ArrayList<EventDay>();
        for(EvaluationDTO e : evaluations){
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTimeInMillis(e.getData());
            EventDay eventDay = new EventDay(c, R.drawable.evaluation_icon);
            eventsTasks.add(eventDay);
        }
        return eventsTasks;
    }

    private List<EventDay> criaListaTasks(List<TaskDTO> tasks){
        List<EventDay> eventsTasks = new ArrayList<EventDay>();
        for(TaskDTO t : tasks){
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTimeInMillis(t.getData_entrega());
            EventDay eventDay = new EventDay(c, R.drawable.task_icon);
            eventsTasks.add(eventDay);
        }
        return eventsTasks;
    }

    private List<EventDay> criaListaEventosCalendario(CalendarDTO calendarDTO){
        List<EventDay> eventsCalendar = new ArrayList<EventDay>();
        for(HolidayDTO h : calendarDTO.getHolidays()){
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTimeInMillis(h.getDate());
            EventDay eventDay = new EventDay(c, R.drawable.holiday);
            eventsCalendar.add(eventDay);
        }

        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTimeInMillis(calendarDTO.getEndPeriod());
        EventDay eventDay = new EventDay(c, R.drawable.end_period);
        eventsCalendar.add(eventDay);

        return eventsCalendar;
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
