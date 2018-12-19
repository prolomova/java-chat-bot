package mapWorker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

public class Image {

    public String Path = null;

    public void getMap(double lon, double lat, String userId) {
        Loader.loadOSM(lon, lat, 0.005, userId);
        var db = new PostGISWorker();
        db.connect();
        db.initDatabase();

        try {

            var first = Runtime.getRuntime().exec("osm2pgsql -U postgres --password --port 5436 --create --database osm .\\tmp\\map" + userId + ".osm  \n");

            TimeUnit.SECONDS.sleep(2);

            var second = Runtime.getRuntime().exec("python .\\src\\main\\java\\mapWorker\\rendering.py " + userId + " \n");

            TimeUnit.SECONDS.sleep(2);

            var input = second.getErrorStream();
            var outputStream = new FileOutputStream(new File(".\\tmp\\t.txt"));
            outputStream.write(input.readAllBytes());
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Path = ".\\tmp\\img" + userId + ".png";
    }
}