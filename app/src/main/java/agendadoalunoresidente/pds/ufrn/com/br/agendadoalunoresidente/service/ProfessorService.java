package agendadoalunoresidente.pds.ufrn.com.br.agendadoalunoresidente.service;

import android.os.AsyncTask;
import android.util.Log;

import agendaufrnfw.ufrn.imd.pds.model.Professor;
import agendaufrnfw.ufrn.imd.pds.model.factory.ProfessorFactory;
import agendaufrnfw.ufrn.imd.pds.request.ProfessorRequest;

/**
 * Created by root on 18/06/18.
 */

public class ProfessorService extends AsyncTask<Void, Void, Professor> {
    private String token;
    public ProfessorService(String token){
        this.token = token;
    }

    @Override
    protected Professor doInBackground(Void... params) {
        ProfessorRequest professorRequest = new ProfessorRequest();
        ProfessorFactory professorFactory = new ProfessorFactory();
        String professorStr = professorRequest.getEvaluationsAndTasksProfessorLoggedIn(token);
        Professor p = (Professor) professorFactory.createUserFromJson(professorStr);
        return p;
    }

    @Override
    protected void onPostExecute(Professor p){
        Log.v("Professor", p.getNome());
    }

}