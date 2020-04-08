package com.example.mongoconnect;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;

public class MongoConnection extends AsyncTask<String, Void, ArrayList<JSONObject>> {
    @Override
    protected void onPostExecute(ArrayList<JSONObject> strings) {
        super.onPostExecute(strings);
        /*for(int i=0;i<strings.size();i++)
        {

            try {
                Toast.makeText(context, ""+strings.get(i).getJSONObject("_id").get("type"), Toast.LENGTH_LONG).show();

            }catch(Exception e){}
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
    protected ArrayList<JSONObject> doInBackground(String... DeviceName) {
        MongoClient mongoClient = MongoClients.create("mongodb://192.168.43.43:27017");
        MongoDatabase database = mongoClient.getDatabase("mongotest");
        //database.createCollection("bhagwan");

        /*for (String name1 : database.listCollectionNames()) {
            collection_names.add(name1);
        }*/



        MongoCollection<Document> collection = database.getCollection("statistics");
        ArrayList<JSONObject> devices_list = new ArrayList<>();

        BasicDBObject query=new BasicDBObject();
        query.put("_id.type",DeviceName[0]);

        MongoCursor<Document> cur = collection.find(query).iterator();

        while (cur.hasNext()) {
            Document doc = cur.next();
            try {
                JSONObject jsonObject = new JSONObject(doc.toJson());
                devices_list.add(jsonObject);

            }catch(Exception e){}



        }
        return devices_list;
    }



}
