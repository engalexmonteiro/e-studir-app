package com.example.estudir.cadUIs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import com.example.estudir.DatabaseHelper;
import com.example.estudir.R;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class CadMassStudent extends AppCompatActivity {


    EditText yearClassroomET;
    EditText codeClassroomET;
    EditText classroomEditText;

    ListView listView;
    List<Map<String,Object>> alunos = new ArrayList<>();
    List<Map<String,Object>> cursos = new ArrayList<>();
    DatabaseHelper helper;

    String[] de = {"matricula", "nome"};
    int[] para = {R.id.curso, R.id.modalidade};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_mass_student);

        helper = new DatabaseHelper(this);
        Intent intent = getIntent();

        spinnerCourses();

        classroomEditText = findViewById(R.id.editText_disciplina);
        yearClassroomET = findViewById(R.id.editText_ano_turma);
        codeClassroomET = findViewById(R.id.editText_cod_turma);

        if(!intent.getStringExtra("listPath").isEmpty()) {
            listView = findViewById(R.id.listView);
            Log.i("e-studir",intent.getStringExtra("listPath"));
            listStudents(intent.getStringExtra("listPath"));

            SimpleAdapter adapter = new SimpleAdapter(this,
                    listStudents(intent.getStringExtra("listPath")), R.layout.list_courses, de, para);
            listView.setAdapter(adapter);
        }
    }

    public List<Map<String,Object>> listStudents(String fullPath){

        File listStudents = new File(fullPath);

        WorkbookSettings conf = new WorkbookSettings();
        conf.setEncoding("Cp1252");
        Workbook wb = null;
        try
        {
            wb = Workbook.getWorkbook(listStudents,conf);
        } catch(IOException | BiffException e)
        {
            e.printStackTrace();
        }

        assert wb != null;
        Sheet sheet = wb.getSheet(0);

        int row = 11, colns = 1;
        Cell cell = sheet.getCell(1, 2);
        String turma = cell.getContents();

        String[] classroom = turma.replace("(","-").replace(")","-").split("-");

        codeClassroomET.setText(classroom[0]);
        classroomEditText.setText(classroom[2]);
        yearClassroomET.setText(classroom[6].replace(".",""));

        Log.i("PATH ",turma);

        cell =sheet.getCell(colns,row);
        String matricula = cell.getContents();


        while(!matricula.isEmpty())
        {
            cell = sheet.getCell(colns + 1, row);
            String nome = cell.getContents();

            Map<String,Object> student  = new HashMap<>();
            student.put("matricula",matricula);
            student.put("nome",nome);
            alunos.add(student);

            row++;
            cell = sheet.getCell(colns, row);
            matricula = cell.getContents();
        }

        return alunos;

    }

    public void cadastrar(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tem certeza que deseja cadastrar os alunos na disciplina de " + classroomEditText.getText().toString());
        builder.setPositiveButton("Sim", (dialog, id) -> {

              if(!checkFieldsEmpty()){
                  Log.i("e-studir","cadastrando");
                  storeClassRoomDB();
                  storeStudentsDB();
                  enrollStudentsDB();
              }
              else
                  Log.i("e-studir","campos devem ser preenchidos");

        });
        builder.setNegativeButton("Não", (dialog, id) -> {

        });
        builder.show();

    }

    private void storeClassRoomDB(){
        Spinner tempSpS;
        try (SQLiteDatabase db = helper.getWritableDatabase()) {

            tempSpS = findViewById(R.id.spinner_courses);

            ContentValues values = new ContentValues();
            values.put("cod_turma", codeClassroomET.getText().toString());
            values.put("ano_turma", yearClassroomET.getText().toString());
            values.put("disciplina", classroomEditText.getText().toString());
            values.put("curso_id", getList("id").get(tempSpS.getSelectedItemPosition()));
            Log.i("e-studir", values.toString());

            long newRowId = db.insert("disciplina", null, values);

            if(newRowId != -1)
                Log.i("e-studir","Cadastrado " + values.toString());
            else
                Log.i("e-studir","Erro ao cadastrar " + values.toString());
        }

    }


    private void storeStudentsDB(){
        SQLiteDatabase db = helper.getWritableDatabase();

        Spinner tempSpS = findViewById(R.id.spinner_courses);


        for(Map<String,Object> student : alunos){

            ContentValues values = new ContentValues();
            values.put("matricula", (String) student.get("matricula"));
            values.put("nome", (String) student.get("nome"));
            values.put("curso_id", getList("id").get(tempSpS.getSelectedItemPosition()));

            Log.i("e-studir", values.toString());
            long newRowId = db.insert("aluno", null, values);

            if(newRowId != -1)
                Log.i("e-studir","Aluno cadastrados " + values.toString());
            else
                Log.i("e-studir","Erro ao cadastrar alunos" + values.toString());
        }
    }

    private void enrollStudentsDB(){
        SQLiteDatabase db = helper.getWritableDatabase();

        Spinner tempSpS = findViewById(R.id.spinner_courses);

        for(Map<String,Object> student : alunos){

            ContentValues values = new ContentValues();
            values.put("cod_turma", codeClassroomET.getText().toString());
            values.put("ano_turma", yearClassroomET.getText().toString());
            values.put("aluno_id",  student.get("matricula").toString());

            Log.i("e-studir", values.toString());
            long newRowId = db.insert("matricula", null, values);

            if(newRowId != -1)
                Log.i("e-studir","Aluno matriculado " + values.toString());
            else
                Log.i("e-studir","Erro ao matricular alunos" + values.toString());
        }
    }


    public void spinnerCourses(){

        listCourses();
        Log.i("e-studir",getList("id").toString());
        Log.i("e-studir",getList("course").toString());

        ArrayAdapter<String> tempAdapterS = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item,
                getListCourse("course"));

        Spinner tempSpS = findViewById(R.id.spinner_courses);

        tempSpS.setAdapter(tempAdapterS);

        Log.i("e-studir",listCourses().toString());
    }

    public List<Map<String,Object>> listCourses() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM curso",null);

        cursor.moveToFirst();

        for(int i=0; i < cursor.getCount();i++){
            Map<String,Object> course = new HashMap<>();
            course.put("id",cursor.getString(0));
            course.put("course",cursor.getString(1));
            course.put("modalidade",cursor.getString(2));
            cursos.add(course);
            cursor.moveToNext();
        }

        cursor.close();
        return cursos;
    }

    public List<String> getListCourse(String name){
        ArrayList<String> list = new ArrayList<>();

        for(Map<String,Object> field : cursos){
            list.add(field.get(name).toString() + " " +field.get("modalidade").toString() );
        }

        return list;
    }

    public List<String> getList(String name){
        ArrayList<String> list = new ArrayList<>();

        for(Map<String,Object> field : cursos){
            list.add(field.get(name).toString());
        }

        return list;
    }

    public boolean checkFieldsEmpty(){

        return yearClassroomET.getText().toString().isEmpty() || codeClassroomET.getText().toString().isEmpty() || classroomEditText.getText().toString().isEmpty();
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }
}
