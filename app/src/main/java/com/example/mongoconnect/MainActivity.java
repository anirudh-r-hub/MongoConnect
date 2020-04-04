package com.example.mongoconnect;

import androidx.appcompat.app.AppCompatActivity;

import com.mongodb.Mongo;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ClusterSettings;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    String name;
    ArrayList<String>collection_names=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = (TextView) findViewById(R.id.hello);
        textView.setText("List of collections wil be displayed in the form of toast messages");
        MongoConnection mongoConnection = new MongoConnection(getApplicationContext());
        mongoConnection.execute(new Void[0]);





    }

    public void show(View view)
    {


    }


}
