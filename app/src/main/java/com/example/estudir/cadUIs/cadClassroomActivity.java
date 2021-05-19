package com.example.estudir.cadUIs;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.example.estudir.DatabaseHelper;
import com.example.estudir.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class cadClassroomActivity extends AppCompatActivity {

    private Spinner sp_course;
    private EditText edtxt_classroom_cod;
    private EditText edtxt_classroom_name;
    private EditText edtxt_classroom_year;
    private ListView lstv_classrooms;

    List<Map<String, Object>> cursos = new ArrayList<>();

    DatabaseHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_classroom);

        helper = new DatabaseHelper(this);


        sp_course = findViewById(R.id.spinner_courses);

        lstv_classrooms = findViewById(R.id.listView);
        edtxt_classroom_cod = findViewById(R.id.editText_cod_turma);
        edtxt_classroom_year = findViewById(R.id.editText_ano_turma);
        edtxt_classroom_name = findViewById(R.id.editText_disciplina);

        updateCourse();
        updateListview();
    }

    private void updateListview() {
        SQLiteDatabase db = helper.getReadableDatabase();

        Map<String,Object> course = cursos.get(sp_course.getSelectedItemPosition());

        String id_course = Objects.requireNonNull(course.get("_id")).toString();

        Log.i("e-studir",id_course);

        List<Map<String,Object>> Classrooms = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM disciplina WHERE curso_id=" + id_course,null);

        cursor.moveToFirst();

        for(int i=0; i < cursor.getCount(); i++){
                Map<String, Object> classroom = new HashMap<>();
                for(int j = 0; j<cursor.getColumnCount();j++)
                    classroom.put(cursor.getColumnName(j),cursor.getString(j));
                Classrooms.add(classroom);
                cursor.moveToNext();
        }

        cursor.close();

        Log.i("e-studir",Classrooms.toString());

        String[] titles = {"cod_turma","ano_turma","disciplina"};
        int[] fields = {R.id.id_curso,R.id.curso,R.id.modalidade};

        SimpleAdapter adapter  = new SimpleAdapter(this,Classrooms,R.layout.list_courses,titles,fields);
       lstv_classrooms.setAdapter(adapter);

       lstv_classrooms.setOnItemClickListener(new AdapterView.OnItemClickListener(){
           @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              Map<String,Object> temp = Classrooms.get(position);
              edtxt_classroom_cod.setText(Objects.requireNonNull(temp.get("cod_turma")).toString());
              edtxt_classroom_year.setText(Objects.requireNonNull(temp.get("ano_turma")).toString());
              edtxt_classroom_name.setText(Objects.requireNonNull(temp.get("disciplina")).toString());
            }
                });

    }

    private void updateCourse() {
        String[] titles = {"curso","modalidade"};
        int[] fields = {R.id.curso, R.id.modalidade};

        SimpleAdapter adapter = new SimpleAdapter(this,listCourses(),R.layout.list_courses,titles,fields);
        sp_course.setAdapter(adapter);

       sp_course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               updateListview();
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });


    }

    public List<Map<String,Object>> listCourses() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM curso",null);

        cursor.moveToFirst();

        for(int i = 0; i < cursor.getCount(); i++){
            Map<String,Object> course = new HashMap<>();

            for(int j=0; j< cursor.getColumnCount(); j++)
                course.put(cursor.getColumnName(j),cursor.getString(j));
            cursos.add(course);
            cursor.moveToNext();
        }

        cursor.close();
        return cursos;
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }
}