import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.AttachedDisk;
import com.google.api.services.compute.model.AttachedDiskInitializeParams;
import com.google.api.services.compute.model.Image;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstanceList;
import com.google.api.services.compute.model.NetworkInterface;
import com.google.api.services.compute.model.Operation;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Vasilii Emelin
 */
public class App {


    private static final String F1_MICRO = "https://www.googleapis.com/compute/v1/projects/gcp-tut/zones/europe-west1-b/machineTypes/f1-micro";
    private static final String EU_W1B = "europe-west1-b";
    private static final String PROJECT = "gcp-tut";
    private static final String NAME = "test-instance";

    private static Compute compute;
    public static Operation create() throws IOException {
        Image image = compute.images().get("debian-cloud", "debian-9-stretch-v20171025").execute();
        String imageLink = image.getSelfLink();
        System.out.println(image);

        AttachedDisk disk = new AttachedDisk()
                .setBoot(true)
                .setAutoDelete(true)
                .setInitializeParams(new AttachedDiskInitializeParams().setSourceImage(imageLink));
        Instance instance = new Instance().setName(NAME)
                .setMachineType(F1_MICRO)
                .setZone(EU_W1B)
                .setDisks(Collections.singletonList(disk))
                .setNetworkInterfaces(Collections.singletonList(new NetworkInterface()));
        Compute.Instances.Insert request = compute.instances().insert(PROJECT, EU_W1B, instance);
        Operation op = request.execute();
        System.out.println(op);
        return op;
    }

    public static Operation start() throws IOException {
        Compute.Instances.Start start = compute.instances().start(PROJECT, EU_W1B, NAME);
        return start.execute();
    }

    public static Operation stop() throws IOException {
        Compute.Instances.Stop stop = compute.instances().stop(PROJECT, EU_W1B, NAME);
        return stop.execute();
    }

    private static Operation remove() throws IOException {
        Compute.Instances.Delete delete = compute.instances().delete(PROJECT, EU_W1B, NAME);
        return delete.execute();
    }

    public static Instance getExistingInstance() {

        Instance op = null;
        try {
            op = compute.instances().get(PROJECT, EU_W1B, NAME).execute();
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println(((GoogleJsonResponseException)e).getStatusMessage());
        }

        return op;
    }

    public static Compute createComputeService() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleCredential credential = GoogleCredential.getApplicationDefault();
        if (credential.createScopedRequired()) {
            credential =
                    credential.createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
        }

        return new Compute.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Google-ComputeSample/0.1")
                .build();
    }
//
//    public static boolean validate(String[] args) {
//        if (args.length = 1 && (args[0] == "start" || args[0] == "stop" || args[0] == "remove" || args[0] =="status") )
//    }


    public static boolean valid (String[] args) {
        return (args.length == 1 &&
                (args[0].equals("start") || args[0].equals("stop") || args[0].equals("remove") || args[0].equals("status")));
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {

        if (!(valid(args))) {
            System.out.println("valid commands: start, stop, remove, status");
            System.exit(0);
        }

        String action = null;
        compute = createComputeService();
        Instance instance = getExistingInstance();
        if (instance == null) {
            if (args[0].equals("start")) {
                action = "create";
            }
            else {
                System.out.println("instance not found. run start.");
                System.exit(0);
            }
        }
        else  {
            action = args[0];
        }

        Operation op = null;
        switch (action) {
            case "create":
                System.out.println(op = create()); break;
            case "start":
                if (instance.getStatus().equals("RUNNING")) {
                    System.out.println("already running");
                }
                else
                    System.out.println(op = start()); break;
            case "stop":
                if (instance.getStatus().equals("TERMINATED")) {
                    System.out.println("already stopped");
                }
                else {
                    System.out.println(op = stop());
                    break;
                }
            case "remove":
                System.out.println(op = remove()); break;
            case "status":
                System.out.println(instance.getStatus()); break;
        }

//        System.out.println(op);

        InstanceList instances = compute.instances().list(PROJECT, EU_W1B).execute();
        for (Instance i : instances.getItems()) {
            System.out.println(i);
        }

    }

}
