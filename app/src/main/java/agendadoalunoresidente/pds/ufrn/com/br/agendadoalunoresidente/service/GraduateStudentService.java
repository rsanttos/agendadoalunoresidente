package agendadoalunoresidente.pds.ufrn.com.br.agendadoalunoresidente.service;

import android.os.AsyncTask;
import android.util.Log;

import agendaufrnfw.ufrn.imd.pds.model.user.GraduateStudent;
import agendaufrnfw.ufrn.imd.pds.model.user.factory.GraduateStudentFactory;
import agendaufrnfw.ufrn.imd.pds.request.StudentRequest;

/**
 * Created by f202359 on 13/04/2018.
 */

public class GraduateStudentService extends AsyncTask<Void, Void, GraduateStudent> {
    private String token;
    public GraduateStudentService(String token){
        this.token = token;
    }

    @Override
    protected GraduateStudent doInBackground(Void... params) {
        StudentRequest studentRequest = new StudentRequest();
        GraduateStudentFactory graduateStudentFactory = new GraduateStudentFactory();
        String studentStr = studentRequest.getEvaluationsAndTasksGraduateStudentLoggedIn(token);
        GraduateStudent gs = (GraduateStudent) graduateStudentFactory.createUserFromJson(studentStr);
        return gs;
    }

    @Override
    protected void onPostExecute(GraduateStudent gs){
        Log.v("Student", gs.getNome_discente());
    }
}
