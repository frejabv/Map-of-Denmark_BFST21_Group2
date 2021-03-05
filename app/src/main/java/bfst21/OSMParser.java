package bfst21;

import java.io.*;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class OSMParser {
    public static void readMapElements(String filepath, Model model) throws IOException, XMLStreamException {
        if (filepath.endsWith(".osm")) {
            loadOSM(new FileInputStream(filepath), model);
        } else if (filepath.endsWith(".zip")) {
            loadZIP(new FileInputStream(filepath), model);
        } else if (filepath.endsWith(".obj")) {

        } else {
            String[] splitFileName = filepath.split(".");
            throw new UnsupportedFileTypeException("." + splitFileName[1]);
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

    public static void loadZIP(InputStream inputStream, Model model) throws IOException, XMLStreamException {
        var zip = new ZipInputStream(inputStream);
        zip.getNextEntry();
        loadOSM(zip,model);
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
