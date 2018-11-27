package mapWorker;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

        var osmWayList = OSMWrapper.getNodes(Loader.getXML(49, 8.3, 0.005));
        for (var osmNode : osmWayList) {
            System.out.println(osmNode.getId() + ":" + osmNode.getLat() + ":" + osmNode.getLon() + " " + osmNode.getTags());        }
    }
}