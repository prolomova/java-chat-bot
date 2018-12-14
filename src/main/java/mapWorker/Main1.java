package mapWorker;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main1 {

    public static void main(String[] args) throws IOException {

        Loader.loadOSM(49, 8.3, 0.005);
        var db = new PostGISWorker();
        db.connect();
        db.initDatabase();
        Runtime.getRuntime().exec("osm2pgsql -U postgres --password --port 5436 --create --database osm -S .\\default.style .\\tmp\\map.osm \n");

    }
}