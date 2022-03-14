package net.maxho.jaxb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Generic object xml marshaller for jaxb objects.
 * @param <T>
 */
public class CustomMarshaller<T> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * Method transfers object of T type to xml string.
     *
     * @param object marshall object of T type.
     * @return xml string of marshalled object.
     * @see String
     */
    public String marshalToString(T object) {
        return marshal(object, "", "");
    }

    /**
     * Method transfers object of T type to xml string.
     *
     * @param object          marshall object of T type.
     * @param rootElementName Root element name.
     * @return xml string of marshalled object.
     * @see String
     */
    public String marshalToString(T object, String rootElementName) {
        return marshal(object, "", rootElementName);
    }

    /**
     * Method transfers object of T type to xml string.
     *
     * @param object          marshall object of T type.
     * @param namespaceURI    Namespace for resulted xml document.
     * @param rootElementName Root element name.
     * @return xml string of marshalled object.
     * @see String
     */
    public String marshalToString(T object, String namespaceURI, String rootElementName) {
        return marshal(object, namespaceURI, rootElementName);
    }

    /**
     * Method transfers object of T type without @XmlRootElement annotation to dom Document.
     *
     * @param object          marshall object of T type.
     * @param rootElementName Root element name.
     * @return xml dom Document of marshalled object.
     * @see Document
     * @see Optional
     */
    public Optional<Document> marshalToDocument(T object, String rootElementName) {
        return marshalToDOM(object, "", rootElementName);
    }

    /**
     * Method transfers object of T type without @XmlRootElement annotation to dom Document.
     *
     * @param object          marshall object of T type.
     * @param namespaceURI    Namespace for resulted xml document.
     * @param rootElementName Root element name.
     * @return xml dom Document of marshalled object.
     * @see Document
     * @see Optional
     */
    public Optional<Document> marshalToDocument(T object, String namespaceURI, String rootElementName) {
         return marshalToDOM(object, namespaceURI, rootElementName);
    }

    @SuppressWarnings("unchecked")
    private String marshal(T object, String namespaceURI, String rootElementName) {
        try {
            StringWriter sw = new StringWriter();
            Marshaller jaxbMarshaller = getMarshaller(object);
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
            if (namespaceURI.equals("") && rootElementName.equals("")) {
                jaxbMarshaller.marshal(object, sw);
            } else {
                jaxbMarshaller.marshal(new JAXBElement<>(new QName(namespaceURI, rootElementName),
                        (Class<T>) object.getClass(), object), sw);
            }
            return sw.toString();
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private Optional<Document> marshalToDOM(T object, String namespaceURI, String rootElementName) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Marshaller jaxbMarshaller = getMarshaller(object);
            if (namespaceURI.equals("") && rootElementName.equals("")) {
                jaxbMarshaller.marshal(object, document);
            } else {
                jaxbMarshaller.marshal(new JAXBElement<>(new QName(namespaceURI, rootElementName),
                        (Class<T>) object.getClass(), object), document);
            }
            return Optional.of(document);
        } catch (JAXBException | ParserConfigurationException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Marshaller getMarshaller(T object) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        return jaxbContext.createMarshaller();
    }
}
