package com.example.estudir;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.estudir.cadUIs.cadClassroomActivity;
import com.example.estudir.cadUIs.CadMassStudent;
import com.example.estudir.cadUIs.cadCourseActivity;
import com.example.estudir.cadUIs.cadExerciseActivity;
import com.example.estudir.cadUIs.cadStudentActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private Uri imageUri;
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        helper = new DatabaseHelper(this);

        Log.i("PATH ","teste");

        if (Intent.ACTION_SEND.equals(action) && type != null) {

            Log.i("PATH ",type);
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }else if (type.startsWith("application/pdf")) {
                handleSendPDF(intent); // Handle multiple images being sent
            }else if (type.startsWith("application/vnd")) {
                Log.i("PATH","xls recebido");
                handleSendXLS(intent); // Handle multiple images being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen

        }
    }

    private void handleSendXLS(Intent intent){
        Uri xlsUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        String fullPath = checkDirectory(xlsUri,"planilha.xls");

        Log.i("PATH: ",fullPath);

        Intent cadMassAct = new Intent(this, CadMassStudent.class);
        cadMassAct.putExtra("listPath",fullPath);
        startActivity(cadMassAct);

    }

    public String checkDirectory(Uri fileUri,String nameFile){
        String pathFile = Environment.getExternalStorageDirectory() + "/" + getString(R.string.directoryName) + "/";

        //Log.i("PATH file", pathFile);
        File edDirectory = new File(pathFile);

        if (!edDirectory.exists()) {
            Log.i("e-studir", edDirectory.toString() + "Directory doesn't create. Creating " + getString(R.string.directoryName) + " directory");
            edDirectory.mkdirs();
        }

        if (edDirectory.exists()) {
            Log.i("e-studir", edDirectory.toString() + " directory was create");
            copyFile(fileUri, pathFile + nameFile);
        }else{
            Log.i("e-studir", edDirectory.toString() + " directory wasn't create");
        }

        return pathFile+nameFile;
    }

    private void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }

    private void handleSendImage(Intent intent){
        imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (imageUri != null) {
            // Update UI to reflect image being shared
            Toast toast = Toast.makeText(this,imageUri.getPath(),Toast.LENGTH_SHORT);
            toast.show();
            Log.i("PATH interno",imageUri.getPath());

            Dialog(findViewById(R.id.main_view).getRootView());
        }
    }

    private void handleSendText(Intent intent){
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText != null) {
            // Update UI to reflect text being shared
            Toast toast = Toast.makeText(this,sharedText,Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void handleSendPDF(Intent intent){
        Uri pdfUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Toast toast = Toast.makeText(this,pdfUri.getPath(),Toast.LENGTH_SHORT);
        toast.show();
    }

    public void copyFile(Uri inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;

        try {
            in = getContentResolver().openInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

            Log.i("PATH","Copied file to " + outputPath);

        } catch (FileNotFoundException fnfe1) {
            Log.i("PATH",fnfe1.getMessage());
        } catch (Exception e) {
            Log.i("PATH", e.getMessage());
        }
    }

    public void mostrarPath(View v){
        File wallpaperDirectory = new File( Environment.getExternalStorageDirectory() + "/" + getString(R.string.directoryName) + "/");
        // have the object build the directory structure, if needed.
        if(!wallpaperDirectory.exists())
        {
            if(wallpaperDirectory.mkdirs()){
                Log.i("PATH OK" , "Diretorio criado");
            }else{
                Log.i("PATH ERROR" , "Diretorio nao criado");
            }
        }
        else{
            Log.i("PATH ERROR", wallpaperDirectory.toString() + " CREATED");
        }
    }

    public void menuOption(View v){
        switch(v.getId()){
            case R.id.crudCourse:   startActivity(new Intent(MainActivity.this, cadCourseActivity.class));
                                    break;
            case R.id.crudStudent:  startActivity(new Intent(MainActivity.this, cadStudentActivity.class));
                                    break;
            case R.id.crudClassroom: startActivity(new Intent(MainActivity.this, cadClassroomActivity.class));
                                    break;
            case R.id.crudExercise: startActivity(new Intent(MainActivity.this, cadExerciseActivity.class));
                                    break;
        }
    }

    public void Dialog(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this.getApplicationContext());
        View view = inflater.inflate(R.layout.data_student, null, false);

        builder.setView(view)
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if(!((AutoCompleteTextView)view.findViewById(R.id.alunos)).getText().toString().isEmpty()) {
                            String pathFileTemp = getDirectoryPath(view);
                            String namePath = getNamePath(view);

                            //Toast toast = Toast.makeText(getBaseContext(), pathFileTemp + namePath, Toast.LENGTH_LONG);
                            //Log.i("PATH file", pathFileTemp + namePath);
                            //toast.show();
                            String pathFile = Environment.getExternalStorageDirectory() + "/" + getString(R.string.directoryName) + "/" + pathFileTemp;

                            String nameFile = "image.jpg";
                            //Log.i("PATH file", pathFile);
                            File edDirectory = new File(pathFile);

                            if (!edDirectory.exists()) {
                                edDirectory.mkdirs();
                                Log.i("PATH ERROR", edDirectory.toString() + " DOES NOT EXIST");
                            }

                            if (edDirectory.exists()) {
                                Log.i("PATH OK", edDirectory.toString() + " CRIADO");
                                   copyFile(imageUri, pathFile + nameFile);
                            }
                        }else{
                            Toast toast = Toast.makeText(getBaseContext(), "Nome de aluno inválido", Toast.LENGTH_LONG);
                            toast.show();
                            Dialog(v);
                        }

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        ArrayAdapter<String> tempAdapterS = new ArrayAdapter<>(
                view.getContext(), android.R.layout.simple_spinner_dropdown_item,
                helper.listarCursos());
        Spinner tempSpS = (Spinner) view.findViewById(R.id.cursos);
        tempSpS.setAdapter(tempAdapterS);

        /*ArrayAdapter<CharSequence> tempAdapter = ArrayAdapter.createFromResource(
                view.getContext(), R.array.cursos,
                android.R.layout.simple_spinner_dropdown_item);

        Spinner tempSp = (Spinner) view.findViewById(R.id.cursos);
        tempSp.setAdapter(tempAdapter);*/

        ArrayAdapter<CharSequence> tempAdapter;
        tempAdapter = ArrayAdapter.createFromResource(
                view.getContext(), R.array.modalidades,
                android.R.layout.simple_spinner_dropdown_item);
        Spinner tempSp;
        tempSp = (Spinner) view.findViewById(R.id.modalidades);
        tempSp.setAdapter(tempAdapter);

        tempAdapter = ArrayAdapter.createFromResource(
                view.getContext(), R.array.periodo,
                android.R.layout.simple_spinner_dropdown_item);
        tempSp = (Spinner) view.findViewById(R.id.periodos);
        tempSp.setAdapter(tempAdapter);


        tempAdapter = ArrayAdapter.createFromResource(
                view.getContext(), R.array.alunos,
                android.R.layout.simple_dropdown_item_1line);
        AutoCompleteTextView tempAT = (AutoCompleteTextView) view.findViewById(R.id.alunos);
        tempAT.setAdapter(tempAdapter);

        tempAdapter = ArrayAdapter.createFromResource(
                view.getContext(), R.array.etapa,
                android.R.layout.simple_spinner_dropdown_item);
        tempSp = (Spinner) view.findViewById(R.id.etapas);
        tempSp.setAdapter(tempAdapter);

        tempAdapter = ArrayAdapter.createFromResource(
                view.getContext(), R.array.estudo,
                android.R.layout.simple_spinner_dropdown_item);
        tempSp = (Spinner) view.findViewById(R.id.estudos_dirigidos);
        tempSp.setAdapter(tempAdapter);

        tempAdapter = ArrayAdapter.createFromResource(
                view.getContext(), R.array.semana,
                android.R.layout.simple_spinner_dropdown_item);
        tempSp = (Spinner) view.findViewById(R.id.semanas);
        tempSp.setAdapter(tempAdapter);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    public String getDirectoryPath(View view){
        Spinner temp = view.findViewById(R.id.cursos);
        String pathFileTemp = temp.getSelectedItem().toString();
        temp = view.findViewById(R.id.modalidades);
        pathFileTemp = pathFileTemp.concat("/" + temp.getSelectedItem().toString());
        temp = view.findViewById(R.id.periodos);
        pathFileTemp = pathFileTemp.concat("/" + temp.getSelectedItem().toString().replace("/","-"));
        AutoCompleteTextView temp2 = view.findViewById(R.id.alunos);
        pathFileTemp = pathFileTemp.concat("/" + temp2.getText().toString());

        return pathFileTemp;
    }

    public String getNamePath(View view){
        Spinner temp = view.findViewById(R.id.etapas);
        String pathNameFile = "ET0" + temp.getSelectedItem().toString();
        temp = view.findViewById(R.id.estudos_dirigidos);
        pathNameFile = pathNameFile.concat("ED0" + temp.getSelectedItem().toString());
        temp = view.findViewById(R.id.semanas);
        pathNameFile = pathNameFile.concat("SM0" + temp.getSelectedItem().toString());

        return pathNameFile;
    }


    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }

}