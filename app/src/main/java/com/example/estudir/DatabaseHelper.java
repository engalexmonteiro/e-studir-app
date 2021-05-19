package com.example.estudir;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String BANCO_DADOS = "Esdudir";
    private static final int VERSAO = 1;

    public DatabaseHelper(Context context) {

        super(context, BANCO_DADOS, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE curso (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "curso TEXT NOT NULL," +
                "modalidade TEXT NOT NULL);");

        db.execSQL("CREATE TABLE aluno (matricula INTEGER PRIMARY KEY NOT NULL," +
                "nome TEXT," +
                "curso_id INTEGER," +
                "FOREIGN KEY(curso_id) REFERENCES curso(_id) ON DELETE RESTRICT);");

        db.execSQL("CREATE TABLE disciplina (cod_turma TEXT NOT NULL," +
                "ano_turma INTEGER NOT NULL," +
                "disciplina TEXT NOT NULL," +
                "curso_id INTEGER," +
                "PRIMARY KEY(cod_turma,ano_turma)," +
                "FOREIGN KEY(curso_id) REFERENCES curso(_id) ON DELETE RESTRICT);");

        db.execSQL("CREATE TABLE matricula (_id INTEGER PRIMARY KEY NOT NULL," +
                "cod_turma TEXT," +
                "ano_turma INTEGER," +
                "aluno_id INTEGER," +
                "FOREIGN KEY(cod_turma,ano_turma) REFERENCES disciplina(cod_turma,ano_turma) ON DELETE RESTRICT," +
                "FOREIGN KEY(aluno_id) REFERENCES aluno(matricula) ON DELETE RESTRICT);");

        db.execSQL("CREATE TABLE edirigido (cod_turma TEXT,"+
                "ano_turma INTEGER," +
                "etapa INTEGER, " +
                "edirigido INTEGER," +
                "semanas INTEGER," +
                "PRIMARY KEY(cod_turma,ano_turma,etapa,edirigido,semanas)," +
                "FOREIGN KEY(cod_turma,ano_turma) REFERENCES disciplina(cod_turma,ano_turma));");

        db.execSQL("CREATE TABLE edirigido_week (id_week INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
                        "cod_turma INTEGER,"+
                        "ano_turma INTEGER, " +
                        "edirigido INTEGER," +
                        "etapa INTEGER," +
                        "semanas INTEGER," +
                        "semana INTEGER," +
                        "date_start DATE," +
                        "data_end DATE," +
                        "FOREIGN KEY(cod_turma,ano_turma,etapa,edirigido,semanas) REFERENCES edirigido(cod_turma,ano_turma,etapa,edirigido,semanas)," +
                        "FOREIGN KEY(cod_turma,ano_turma) REFERENCES disciplina(cod_turma,ano_turma)" +
                        ");");

        db.execSQL("CREATE TABLE edirigido_entregue (id_matricula INTEGER,"+
                "id_week INTEGER," +
                "date DATE," +
                "score FLOAT," +
                "PRIMARY KEY(id_matricula,id_week)" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS curso");
            db.execSQL("DROP TABLE IF EXISTS disciplina");
            db.execSQL("DROP TABLE IF EXISTS matricula");
            db.execSQL("DROP TABLE IF EXISTS edirigido");
            onCreate(db);
    }


    public List<String> listarCursos() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, curso FROM curso",null);
        List<String> cursos = new ArrayList<>();

        cursor.moveToFirst();

        for(int i=0; i < cursor.getCount();i++){
            cursos.add(cursor.getString(1));
            cursor.moveToNext();
        }

        cursor.close();
        return cursos;
    }


    public List<Map<String,Object>> listCourses() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from curso;", null);

        cursor.moveToFirst();

        List<Map<String, Object>> courses = new ArrayList<>();

        for (int i = 0; i < cursor.getCount(); i++) {
            Map<String, Object> course = new HashMap<>();
            for (int j = 0; j < cursor.getColumnCount(); j++)
                course.put(cursor.getColumnName(j), cursor.getString(j));
            courses.add(course);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return courses;

    }


    public List<Map<String,Object>> listClassroom(String course_id, String year) {

        SQLiteDatabase db = this.getReadableDatabase();




        String SQL = "SELECT * FROM disciplina where curso_id=" + course_id + " and ano_turma=" + year + ";" ;
        Log.i("e-studir",SQL);
        Cursor cursor = db.rawQuery(SQL,null);
        cursor.moveToFirst();

        List<Map<String, Object>> classrooms = new ArrayList<>();

       for(int i=0; i < cursor.getCount(); i++){
            Map<String,Object> classroom = new HashMap<>();
            for(int j=0; j < cursor.getColumnCount(); j++){
                classroom.put(cursor.getColumnName(j),cursor.getString(j));
            }
            classrooms.add(classroom);
            cursor.moveToNext();
        }

        Log.i("e-studir",classrooms.toString());
        cursor.close();
        db.close();

        return classrooms;
    }
}
