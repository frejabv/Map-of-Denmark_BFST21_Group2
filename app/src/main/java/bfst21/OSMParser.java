package bfst21;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class OSMParser {
    public static void readMapElements(String filepath, Model model) throws FileNotFoundException, XMLStreamException {
        if (filepath.endsWith(".osm")) {
            loadOSM(new FileInputStream(filepath), model);
        } else if (filepath.endsWith(".zip")) {

        } else if (filepath.endsWith(".obj")) {

        } else {
            throw new RuntimeException("Unsupported file");
        }
    }

    private static void loadOSM(InputStream inputStream, Model model)
            throws XMLStreamException, FactoryConfigurationError {
        XMLStreamReader xmlReader = XMLInputFactory.newInstance()
                .createXMLStreamReader(new BufferedInputStream(inputStream));

        while (xmlReader.hasNext()) {
            generateMapObjects(xmlReader, xmlReader.next(), model);
        }
    }

    private static void generateMapObjects(XMLStreamReader xmlReader, int xmlTagType, Model model) {
        switch (xmlTagType) {
        case XMLStreamReader.START_ELEMENT:
            switch (xmlReader.getLocalName()) {
            case "node":
                var id = Long.parseLong(xmlReader.getAttributeValue(null, "id"));
                var lon = Float.parseFloat(xmlReader.getAttributeValue(null, "lon"));
                var lat = Float.parseFloat(xmlReader.getAttributeValue(null, "lat"));
                model.addToNodeIndex(new Node(lat, lon, id));
                break;
            }
            break;
        case XMLStreamReader.END_ELEMENT:
            break;
        }
    }
}
