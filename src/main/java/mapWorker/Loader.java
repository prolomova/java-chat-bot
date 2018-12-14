package mapWorker;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Properties;

public class Loader {

    private static final String OPENSTREETMAP_API_06 =
            "https://www.openstreetmap.org/api/0.6/map?bbox=";

    public static void loadOSM(double lon, double lat, double vicinityRange) {
        try{
            var format = new DecimalFormat("##0.0000000", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            var left = format.format(lat - vicinityRange);
            var bottom = format.format(lon - vicinityRange);
            var right = format.format(lat + vicinityRange);
            var top = format.format(lon + vicinityRange);

            var url = OPENSTREETMAP_API_06 + left + "," + bottom + "," + right + "," + top;
            System.setProperty("java.net.useSystemProxies", "true");
            var osmUrl = new URL(url);
            var connection = (HttpURLConnection) osmUrl.openConnection();
            var input = connection.getInputStream();
            var outputStream = new FileOutputStream(new File(".\\tmp\\map.osm"));
            outputStream.write(input.readAllBytes());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
