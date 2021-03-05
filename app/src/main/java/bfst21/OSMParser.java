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
    public void readMapElemetnsFromFile(String filepath, Model model) throws FileNotFoundException {
        if (filepath.endsWith(".osm")) {
            loadOSM(new FileInputStream(filepath));
        } else if (filepath.endsWith(".zip")) {

        } else if (filepath.endsWith(".obj")) {

        } else {
            // throw a unsupported file exception
        }
    }

    private void loadOSM(InputStream inputStream) throws XMLStreamException, FactoryConfigurationError {
        XMLStreamReader xmlReader = XMLInputFactory.newInstance()
                .createXMLStreamReader(new BufferedInputStream(inputStream));

        while (xmlReader.hasNext()) {
            switch (xmlReader.next()) {

            }
        }
    }

    private void generateMapObjects(XMLStreamReader xmlReader, int xmlTagType) {
        switch (xmlTagType) {
        case XMLStreamReader.START_ELEMENT:
            switch (xmlReader.getLocalName()) {
            case "node":
                var id = Long.parseLong(xmlReader.getAttributeValue(null, "id"));
                var lon = Float.parseFloat(xmlReader.getAttributeValue(null, "lon"));
                var lat = Float.parseFloat(xmlReader.getAttributeValue(null, "lat"));
                idToNode.put(new Node(id, lat, lon));
                break;
            }
            break;
        case XMLStreamReader.END_ELEMENT:
            break;
        }
    }
}
