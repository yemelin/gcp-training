import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.AttachedDisk;
import com.google.api.services.compute.model.AttachedDiskInitializeParams;
import com.google.api.services.compute.model.Image;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstanceList;
import com.google.api.services.compute.model.MachineType;
import com.google.api.services.compute.model.MachineTypeList;
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

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        Compute compute = createComputeService();
//        MachineTypeList mtl = compute.machineTypes().list(PROJECT, EU_W1B).execute();
//        for (MachineType mt : mtl.getItems()) {
//            System.out.println(mt);
//        }

        Image image = compute.images().get("debian-cloud", "debian-9-stretch-v20171025").execute();
        String imageLink = image.getSelfLink();
        System.out.println(image);

        AttachedDisk disk = new AttachedDisk()
                .setBoot(true)
                .setAutoDelete(true)
                .setInitializeParams(new AttachedDiskInitializeParams().setSourceImage(imageLink));
        Instance instance = new Instance().setName("test-instance")
                .setMachineType(F1_MICRO)
                .setZone(EU_W1B)
                .setDisks(Collections.singletonList(disk))
                .setNetworkInterfaces(Collections.singletonList(new NetworkInterface()));
        Compute.Instances.Insert request = compute.instances().insert(PROJECT, EU_W1B, instance);
        Operation op = request.execute();
        System.out.println(op);

        InstanceList instances = compute.instances().list(PROJECT, EU_W1B).execute();
        for (Instance i : instances.getItems()) {
            System.out.println(i);
        }

    }
}
