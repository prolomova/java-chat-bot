package mapWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OSMWrapper {


    public static List<OSMNode> getNodes(Document xmlDocument) {
        List<OSMNode> osmNodes = new ArrayList<OSMNode>();

        Node osmRoot = xmlDocument.getFirstChild();
        NodeList osmXMLNodes = osmRoot.getChildNodes();

        for (var i = 0; i < osmXMLNodes.getLength() - 1; i++) {
            Node item = osmXMLNodes.item(i + 1);
            if (item.getNodeName().equals("node")) {
                NamedNodeMap attributes = item.getAttributes();
                NodeList tagXMLNodes = item.getChildNodes();
                var tags = new HashMap<String, String>();
                for (int j = 0; j < tagXMLNodes.getLength() - 1; j++) {
                    Node tagItem = tagXMLNodes.item(j + 1);
                    NamedNodeMap tagAttributes = tagItem.getAttributes();
                    if (tagAttributes != null)
                        tags.put(tagAttributes
                                    .getNamedItem("k")
                                    .getNodeValue(),
                                tagAttributes.getNamedItem("v")
                                    .getNodeValue());
                }
                Node namedItemID = attributes.getNamedItem("id");
                Node namedItemLat = attributes.getNamedItem("lat");
                Node namedItemLon = attributes.getNamedItem("lon");
                Node namedItemVersion = attributes.getNamedItem("version");

                String id = namedItemID.getNodeValue();
                String latitude = namedItemLat.getNodeValue();
                String longitude = namedItemLon.getNodeValue();
                var version = "0";
                if (namedItemVersion != null)
                    version = namedItemVersion.getNodeValue();
                osmNodes.add(new OSMNode(id, latitude, longitude, version, tags));
            }
        }
        return osmNodes;
    }
}