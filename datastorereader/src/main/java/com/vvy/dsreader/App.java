package com.vvy.dsreader;


//import com.google.cloud.datastore.Datastore;
//import com.google.datastore.v1.client.DatastoreOptions;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

/**
 * @author Vasilii Emelin
 */
public class App {

    static String credPath;// = "/home/vvy/tmp/gcp/gcp-tut-1f1d75ccc257.json";

    public static void main(String[] args) throws IOException {
        credPath = System.getProperty("credpath");
        Credentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream(credPath));

        DatastoreOptions options = DatastoreOptions.newBuilder()
                .setProjectId("gcp-tut")
                .setHost("https://datastore.googleapis.com")
                .setCredentials(credentials).build();
        Datastore datastore = options.getService();

        Query<Entity>query = Query.newEntityQueryBuilder().setKind("Book3").build();
        QueryResults<Entity> ret = datastore.run(query);

        Entity entity;
        while (ret.hasNext()) {// && (key = ret.next()).hasId()) {
            entity = ret.next();
            System.out.println(entity);
        }
    }
}
