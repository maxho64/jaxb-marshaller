package net.maxho.jaxb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMSource;
import java.io.StringReader;
import java.util.Optional;

/**
 * Generic object xml unmarshaller for jaxb objects.
 * @param <T>
 */
public class CustomUnmarshaller<T> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * Method transfers xml string to object of T type.
     * @param responseString xml string.
     * @return Optional<T> optional container with parsed object of T type.
     * @see Optional
     */
    @SuppressWarnings("unchecked")
    private Optional<T> unmarshal(String responseString, Class<T> tClass) {
        try {
            StringReader stringReader = new StringReader(responseString);
            Unmarshaller jaxbUnmarshaller = getUnmarshaller(tClass);
            T result = (T) jaxbUnmarshaller.unmarshal(stringReader);
            return Optional.of(result);
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Method transfers xml dom Document to object of T type.
     * @param document xml dom Document.
     * @return Optional<T> optional container with parsed object of T type.
     * @see Optional
     */
    private Optional<T> unmarshallFromDocument(Node document, Class<T> tClass) {
        try {
            DOMSource source = new DOMSource(document);
            Unmarshaller jaxbUnmarshaller = getUnmarshaller(tClass);
            T result = jaxbUnmarshaller.unmarshal(source, tClass).getValue();
            return Optional.of(result);
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Unmarshaller getUnmarshaller(Class<T> tClass) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(tClass);
        return jaxbContext.createUnmarshaller();
    }
}
