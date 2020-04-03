package com.example.mongoconnect;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import org.bson.Document;

import java.util.ArrayList;

public class MongoConnection extends AsyncTask<Void, Void, ArrayList<String>> {
    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        for(int i=0;i<strings.size();i++)
        {
            Toast.makeText(context,(i+1)+". "+collection_names.get(i), Toast.LENGTH_LONG).show();
        }

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
    protected ArrayList<String> doInBackground(Void... voids) {
        MongoClient mongoClient = MongoClients.create("mongodb://vaavhomes.ddns.net:27017");
        MongoDatabase database = mongoClient.getDatabase("newdb");
        //database.createCollection("bhagwan");
        int k = 1;
        for (String name1 : database.listCollectionNames())
        {
            collection_names.add(name1);
        }



            /*MongoCollection < Document > collection = database.getCollection("users");

            try (MongoCursor < Document > cur = collection.find().iterator()) {

                while (cur.hasNext()) {

                     Document doc=cur.next();

                     ArrayList<Object>users = new ArrayList < > (doc.values());

                    System.out.printf("%s: %s%n", users.get(1), users.get(2));
                }
            }*/

        return collection_names;
    }


}
