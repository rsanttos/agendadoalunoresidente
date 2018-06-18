package agendadoalunoresidente.pds.ufrn.com.br.agendadoalunoresidente.service;

import android.os.AsyncTask;
import android.util.Log;

import agendaufrnfw.ufrn.imd.pds.model.Student;
import agendaufrnfw.ufrn.imd.pds.model.factory.StudentFactory;
import agendaufrnfw.ufrn.imd.pds.request.StudentRequest;

/**
 * Created by f202359 on 13/04/2018.
 */

public class StudentService extends AsyncTask<Void, Void, Student> {
    private String token;
    public StudentService(String token){
        this.token = token;
    }

    @Override
    protected Student doInBackground(Void... params) {
        StudentRequest studentRequest = new StudentRequest();
        StudentFactory studentFactory = new StudentFactory();
        String studentStr = studentRequest.getEvaluationsAndTasksGraduateStudentLoggedIn(token);
        Student student = (Student) studentFactory.createUserFromJson(studentStr);
        return student;
    }

    @Override
    protected void onPostExecute(Student student){
        Log.v("Student", student.getNome_discente());
    }
}
