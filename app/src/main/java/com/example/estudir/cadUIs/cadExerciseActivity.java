package com.example.estudir.cadUIs;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.example.estudir.DatabaseHelper;
import com.example.estudir.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cadExerciseActivity extends AppCompatActivity {


    private DatabaseHelper helper;
    private Spinner sp_courses;
    private Spinner sp_year;
    private Spinner sp_classrooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_exercise);

        helper = new DatabaseHelper(this);

        sp_courses = findViewById(R.id.spinner_courses);
        sp_classrooms = findViewById(R.id.spinner_classroom);
        sp_year = findViewById(R.id.spinner_year);

        updateSpinnerCourse();

    }

    private void updateSpinnerCourse() {

        String[] titles = {"curso","modalidade"};
        int[] fields = {R.id.curso, R.id.modalidade};
        List<Map<String,Object>> courses = helper.listCourses();

        SimpleAdapter adapter = new SimpleAdapter(this,courses,R.layout.list_courses,titles,fields);
        sp_courses.setAdapter(adapter);



        sp_courses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Map<String,Object> course = courses.get(sp_courses.getSelectedItemPosition());
                String course_id = course.get("_id").toString();
                updateSpinnerYear(course_id);
                updateSpinnerClassroom(course_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateSpinnerYear(String course_id){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT ano_turma from disciplina WHERE curso_id=" + course_id +";",null);


        Log.i("e-studir",course_id);

        List<String> anos = new ArrayList<>();

        cursor.moveToFirst();

        for(int i=0; i<cursor.getCount(); i++){
            anos.add(cursor.getString(i));
            cursor.moveToNext();
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,anos);
        sp_year.setAdapter(adapter);

        cursor.close();

        db.close();

    }

    private void updateSpinnerClassroom(String course_id) {

            String[] titles = {"disciplina"};
            int[] fields = {R.id.curso};
            String year = "0";

            try{
                year = sp_year.getSelectedItem().toString();
            }catch (Exception e){

            }
            Log.i("e-studir", course_id + "\t" + year);

            SimpleAdapter adapter = new SimpleAdapter(this, helper.listClassroom(course_id, year), R.layout.list_courses, titles, fields);
            sp_classrooms.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }
}