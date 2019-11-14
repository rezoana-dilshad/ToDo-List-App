package com.rez.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listview = findViewById(R.id.listView);

        readInfo();     //call readinfo method just before setting the data
        //so that all the saved to do list shows up

        //We need an adapter for the listview
        final TextAdapter adapter = new TextAdapter();
        //set the data to the adapter
        adapter.setData(list); //first declare this list on top

        //now connect the adapter to the listview
        listview.setAdapter(adapter);

        final Button newTaskButton = findViewById(R.id.newTaskButton);
        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText taskInput = new EditText(MainActivity.this);
                //To make the input only single line
                taskInput.setSingleLine();
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add a new task")
                        .setMessage("What is your new task?")
                        .setView(taskInput) //Set the view to the task input
                        //then set the  buttons of these dialogues
                        .setPositiveButton("Add task", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.add(taskInput.getText().toString());
                                //update the adapter data
                                adapter.setData(list);
                                saveInfo();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

        //Now we have to create a way to delete the tasks
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete this task?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                adapter.setData(list);
                                saveInfo();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create();
                dialog.show();
            }
        });

        final Button deleteAllButton = findViewById(R.id.deleteAllButton);
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete all tasks?")
                        .setMessage("Are you sure you want to delete all tasks?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.clear();
                                adapter.setData(list);
                                saveInfo();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                    dialog.show();
            }
        });
    }

    //when do you want to save? on pause?
   /* @Override
    protected void onPause()
    {
        super.onPause();  //when is it paused?
        saveInfo();
    }*/


    //Now to save the items/to-do tasks that we have created
    //so that it doesn't reset when we restart the app
    private void saveInfo()
    {
        try{
            File file = new File(this.getFilesDir(), "saved");

            FileOutputStream fOut = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fOut));

            for(int i=0; i<list.size(); i++)
            {
                writer.write(list.get(i));
                writer.newLine();
            }
            writer.close();
            fOut.close();

        } catch(Exception e){
            e.printStackTrace();
    }
    }

    private void readInfo() {
        File file = new File(this.getFilesDir(), "saved");
        if(!file.exists()){
            return;
        }
        try{
            FileInputStream iS = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(iS));

            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //The adapter class to connect
    class TextAdapter extends BaseAdapter{

        //let's create a place to store the tasks
        List<String> list = new ArrayList<>();

        void setData(List<String> nList) {
            list.clear();
            list.addAll(nList);
        }
    @Override
        public int getCount(){ //number of items listview has
            return list.size();
            //the new tasks were not being saved when we returned 0
            // instead of the list size
        }
    @Override
        public Object getItem(int position) {
            return null;
    }

    @Override
        public long getItemId(int position) {
            return 0;
    }

    @Override
        public View getView(int position, View convertView, ViewGroup parent){

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item, parent, false);
            }

        final TextView textView = convertView.findViewById(R.id.task); //need to access this through rowview

        if(position%2 == 0) {
            textView.setBackgroundColor(Color.GRAY);
            textView.setTextColor(Color.WHITE);
        } else {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        //Update the textview using the data from the list
        textView.setText(list.get(position));

        return convertView;
    }

    }
}
