package com.example.mongoconnect;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.widget.Toast;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;

public class MongoConnection extends AsyncTask<Void, Void, ArrayList<JSONObject>> {
    @Override
    protected void onPostExecute(ArrayList<JSONObject> strings) {
        super.onPostExecute(strings);
        /*for(int i=0;i<strings.size();i++)
        {
            Toast.makeText(context,"no:"+(i+1)+": "+strings.get(i).toString(), Toast.LENGTH_LONG).show();
        }*/

    }

    Context context;
    String name;
    ArrayList<String>collection_names=new ArrayList<>();

    public MongoConnection(Context context)
    {
        this.context=context;
        name="";
    }

    @Override
    protected ArrayList<JSONObject> doInBackground(Void... voids) {
        MongoClient mongoClient = MongoClients.create("mongodb://192.168.0.14:27017");
        MongoDatabase database = mongoClient.getDatabase("mongotest");
        //database.createCollection("bhagwan");
        int k = 1;
        for (String name1 : database.listCollectionNames()) {
            collection_names.add(name1);
        }


        MongoCollection<Document> collection = database.getCollection("devices");
        ArrayList<JSONObject> devices_list = new ArrayList<>();

        MongoCursor<Document> cur = collection.find().iterator();
        int z=1;
        while (cur.hasNext()) {
            Document doc = cur.next();
            try {
                JSONObject jsonObject = new JSONObject(doc.toJson());
                devices_list.add(jsonObject);
                System.err.println(""+z+": "+jsonObject);
                z++;
            }catch(Exception e){}



        }
        return devices_list;
    }



}
