package agendadoalunoresidente.pds.ufrn.com.br.agendadoalunoresidente;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import agendadoalunoresidente.pds.ufrn.com.br.agendadoalunoresidente.service.CalendarService;
import agendaufrnfw.ufrn.imd.pds.model.calendar.CalendarUFRN;
import agendaufrnfw.ufrn.imd.pds.model.calendar.Holiday;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);

        criarBarraMenu();

        TextView tvAno = (TextView) findViewById(R.id.tvValorAno);
        TextView tvMatricula = (TextView) findViewById(R.id.tvValorMatricula);
        TextView tvMatriculaExtraordinaria = (TextView) findViewById(R.id.tvValorMatriculaExtraordinaria);
        TextView tvRematricula = (TextView) findViewById(R.id.tvValorRematricula);
        TextView tvPeriodo = (TextView) findViewById(R.id.tvValorPeriodo);

        CalendarService calendarService = new CalendarService();
        CalendarUFRN calendarDTO = null;
        try {
            calendarDTO = calendarService.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(calendarDTO != null){
            tvAno.setText(String.valueOf(calendarDTO.getAno()));
            tvMatricula.setText(calendarDTO.getInicio_matricula_online() + " - " + calendarDTO.getFim_matricula_online());
            tvMatriculaExtraordinaria.setText(calendarDTO.getInicio_matricula_extraordinaria() + " - " + calendarDTO.getFim_matricula_extraordinaria());
            tvPeriodo.setText(calendarDTO.getInicio_periodo() + " - " + calendarDTO.getFim_periodo());
            tvRematricula.setText(calendarDTO.getInicio_rematricula() + " - " + calendarDTO.getFim_rematricula());

            ListView lvHolidays = (ListView) findViewById(R.id.lvFeriados);
            ArrayAdapter<Holiday> arrayAdapterHolidays = new ArrayAdapter<Holiday>(this,
                    android.R.layout.simple_list_item_1, calendarDTO.getHolidays());
            lvHolidays.setAdapter(arrayAdapterHolidays);
        }

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
