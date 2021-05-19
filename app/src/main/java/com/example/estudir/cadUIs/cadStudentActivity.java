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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.estudir.DatabaseHelper;
import com.example.estudir.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cadStudentActivity extends AppCompatActivity {


    DatabaseHelper helper;
    private Spinner sp_course;
    private Spinner sp_modalidade;
    private EditText edtxt_enrollId;
    private EditText edtxt_nameStudent;
    private ListView listView_students;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_student);

        helper = new DatabaseHelper(this);

        sp_course = findViewById(R.id.spinner_course);
        sp_modalidade = findViewById(R.id.spinner_modalidade);
        edtxt_enrollId = findViewById(R.id.editText_enrollId);
        edtxt_nameStudent = findViewById(R.id.editText_name_student);
        listView_students = findViewById(R.id.listView_student);

        updateListView();
        updateSpinners();

    }

    public void updateListView(){
        String[] de = {"matricula", "nome"};
        int[] para = {R.id.curso, R.id.modalidade};
        SimpleAdapter adapter = new SimpleAdapter(this, listStudents(),R.layout.list_courses, de, para);
        listView_students.setAdapter(adapter);

        listView_students.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.curso);
                edtxt_enrollId.setText(textView.getText());
                textView = (TextView) view.findViewById(R.id.modalidade);
                edtxt_nameStudent.setText(textView.getText());
            }
        });

    }

    public List<Map<String,Object>> listStudents(){
            SQLiteDatabase db = helper.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT curso, modalidade, matricula, nome FROM aluno INNER JOIN curso ON aluno.curso_id=curso._id;",null);

            cursor.moveToFirst();
            List<Map<String,Object>> list = new ArrayList<>();

            for(int i = 0; i < cursor.getCount(); i++){
                Map<String,Object> temp = new HashMap<>();

                for(int j= 0; j < cursor.getColumnCount(); j++){
                    temp.put(cursor.getColumnName(j),cursor.getString(j));
                }
                Log.i("e-studir",temp.toString());

                list.add(temp);
                cursor.moveToNext();
            }

            cursor.close();

            return list;
    }

    public void updateSpinners(){
        String[] de = {"curso","modalidade"};
        int[] para = {R.id.curso,R.id.modalidade};
        SimpleAdapter adapter = new SimpleAdapter(this, listCourses(),R.layout.list_courses, de, para);
        sp_course.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1  = ArrayAdapter.createFromResource(this,R.array.modalidades, android.R.layout.simple_spinner_dropdown_item);
        sp_modalidade.setAdapter(adapter1);
    }

    public List<Map<String,Object>> listCourses(){
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM curso;",null);

        cursor.moveToFirst();
        List<Map<String,Object>> list = new ArrayList<>();

        for(int i = 0; i < cursor.getCount(); i++){
            Map<String,Object> temp = new HashMap<>();

            for(int j= 0; j < cursor.getColumnCount(); j++){
                temp.put(cursor.getColumnName(j),cursor.getString(j));
            }
            Log.i("e-studir",temp.toString());

            list.add(temp);
            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }


}