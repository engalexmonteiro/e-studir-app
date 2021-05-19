package com.example.estudir.cadUIs;

import android.content.ContentValues;

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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.estudir.DatabaseHelper;
import com.example.estudir.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class cadCourseActivity extends AppCompatActivity {

    private DatabaseHelper helper;
    private ListView listView;
    private List<Map<String, Object>> courses;

    String[] de = {"curso", "modalidade"};
    int[] para = {R.id.curso, R.id.modalidade};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadcurso);

        helper = new DatabaseHelper(this);

        listView = findViewById(R.id.listView);

        SimpleAdapter adapter = new SimpleAdapter(this, listCourses(),R.layout.list_courses, de, para);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> Log.i("PATH", ((TextView)view.findViewById(R.id.curso)).getText().toString()));

        Spinner modalidade = findViewById(R.id.modalidades);
        modalidade.setAdapter(ArrayAdapter.createFromResource(getBaseContext(), R.array.modalidades,android.R.layout.simple_spinner_dropdown_item));


    }

    public void cadCurso(View v){
        EditText editText = this.findViewById(R.id.cadCurso);
        Spinner  spinner = this.findViewById(R.id.modalidades);
        SQLiteDatabase db = helper.getWritableDatabase();

        Log.i("PATH ",editText.getText().toString());
        Log.i("PATH ",spinner.getSelectedItem().toString());


        if(!editText.getText().toString().isEmpty() && !spinner.getSelectedItem().toString().isEmpty()){
            ContentValues values = new ContentValues();
            values.put("curso",editText.getText().toString());
            values.put("modalidade",spinner.getSelectedItem().toString());

            long resultado = db.insert("curso", null, values);
            if(resultado != -1 ){
                Toast.makeText(this, getString(R.string.registro_salvo),
                Toast.LENGTH_SHORT).show();
                listView.setAdapter(new SimpleAdapter(this, listCourses(),R.layout.list_courses, de, para));
            }else{
                Toast.makeText(this, getString(R.string.erro_salvar),
                Toast.LENGTH_SHORT).show();
            }

        }
    }

    private List<Map<String, Object>> listCourses() {


        SQLiteDatabase db = helper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT * FROM curso", null)) {

            courses = new ArrayList<>();

            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("curso", cursor.getString(1));
                item.put("modalidade", cursor.getString(2));
                courses.add(item);
                cursor.moveToNext();
            }

            Log.i("PATH", courses.toString());

            cursor.close();
        }

        return courses;
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }

}
