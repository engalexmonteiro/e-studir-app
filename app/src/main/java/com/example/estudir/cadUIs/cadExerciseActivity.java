package com.example.estudir.cadUIs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.HardwarePropertiesManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.example.estudir.DatabaseHelper;
import com.example.estudir.R;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cadExerciseActivity extends AppCompatActivity {


    private DatabaseHelper helper;
    private Spinner sp_courses;
    private Spinner sp_year;
    private Spinner sp_classrooms;

    private EditText edtxt_edirigido;
    private EditText edtxt_etapa;
    private EditText edtxt_semanas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_exercise);

        helper = new DatabaseHelper(this);

        sp_courses = findViewById(R.id.spinner_courses);
        sp_classrooms = findViewById(R.id.spinner_classroom);
        sp_year = findViewById(R.id.spinner_year);
        edtxt_edirigido = findViewById(R.id.editx_edirigido);
        edtxt_etapa = findViewById(R.id.editx_etapa);
        edtxt_semanas = findViewById(R.id.editx_semanas);

        updateSpinnerCourse();

    }

    private void updateSpinnerCourse() {

        String[] titles = {"curso","modalidade"};
        int[] fields = {R.id.curso, R.id.modalidade};
        List<Map<String,Object>> courses = listCourses();

        SimpleAdapter adapter = new SimpleAdapter(this,courses,R.layout.list_courses,titles,fields);
        sp_courses.setAdapter(adapter);

        sp_courses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Map<String,Object> course = courses.get(sp_courses.getSelectedItemPosition());
                String course_id = course.get("_id").toString();
                updateSpinnerYear(course_id);
                updateSpinnerClassroom(course_id);
                updateListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private List<Map<String, Object>> listCourses() {
            SQLiteDatabase db = helper.getReadableDatabase();

            String SQL = "SELECT DISTINCT * from disciplina INNER JOIN curso ON curso._id=disciplina.curso_id;";
            Cursor cursor = db.rawQuery(SQL,null);

            cursor.moveToFirst();

            List<Map<String,Object>> list = new ArrayList<>();

            for(int i=0; i<cursor.getCount(); i++){
                Map<String,Object> course = new HashMap<>();
                for(int j=0; j<cursor.getColumnCount(); j++){
                        course.put(cursor.getColumnName(j),cursor.getString(j));
                }
                list.add(course);
                cursor.moveToNext();
            }

            cursor.close();
            db.close();

            return list;
    }

    private void updateSpinnerYear(String course_id){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT ano_turma from disciplina INNER JOIN curso ON curso_id=" + course_id +";",null);

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

            SimpleAdapter adapter = new SimpleAdapter(this, helper.listClassroom(course_id, year), R.layout.list_courses, titles, fields);
            sp_classrooms.setAdapter(adapter);

    }

    private void updateListView(){
            List<Map<String, Object>> courses = listCourses();
            Map<String, Object> course = courses.get(sp_classrooms.getSelectedItemPosition());


            Log.i("e-studir",course.toString());
            SQLiteDatabase db = helper.getReadableDatabase();

            String SQL = "SELECT * from edirigido WHERE cod_turma=\"" + course.get("cod_turma").toString() +"\" and ano_turma=" + course.get("ano_turma").toString() + ";";
            Log.i("e-studir",SQL);
            Cursor cursor = db.rawQuery(SQL,null);
            cursor.moveToFirst();

            List<Map<String, Object>> listEDs = new ArrayList<>();

            for(int i=0 ; i< cursor.getCount(); i++){
                Map<String,Object> eds = new HashMap<>();
                for(int j=0; j< cursor.getColumnCount(); j++){
                    eds.put(cursor.getColumnName(j),cursor.getString(j));
                }
                listEDs.add(eds);
                cursor.moveToNext();
            }

            String[] titles = {"etapa","edirigido","semanas"};
            int[] views = {R.id.id_curso,R.id.curso,R.id.modalidade};

            SimpleAdapter adapter = new SimpleAdapter(this,listEDs,R.layout.list_courses,titles,views);
            ListView listView = findViewById(R.id.listView);
            listView.setAdapter(adapter);

            cursor.close();

    }

    public void addEDirigido(View v){

        String curso_id;
        String ano_turma;

        List<Map<String, Object>> courses = listCourses();
        Map<String, Object> course = courses.get(sp_classrooms.getSelectedItemPosition());

        SQLiteDatabase db = helper.getWritableDatabase();

        if(!edtxt_edirigido.getText().toString().isEmpty() && !edtxt_etapa.getText().toString().isEmpty() && !edtxt_semanas.getText().toString().isEmpty()){
            ContentValues values = new ContentValues();
            values.put("cod_turma",course.get("cod_turma").toString());
            values.put("ano_turma",course.get("ano_turma").toString());
            values.put("edirigido",edtxt_edirigido.getText().toString());
            values.put("etapa",edtxt_etapa.getText().toString());
            values.put("semanas",edtxt_semanas.getText().toString());
            Log.i("e-studir",values.toString());

            long rowid = db.insert("edirigido",null, values);

        }

     //   db.close();

    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }
}