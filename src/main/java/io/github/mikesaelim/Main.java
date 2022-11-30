package io.github.mikesaelim;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.util.regex.Pattern;

public class Main {
    static final Pattern ALPHABETIC = Pattern.compile("\\p{L}+");
    static final Integer MAIN_NAMESPACE = 0;
    static final String ENGLISH_HEADER = "==English==";

    public static void main(String[] args) {
        // You'll need to run this from the project folder
        String filepath = "data/enwiktionary-latest-pages-articles.xml";

        try {
            parse(filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void parse(String filepath) throws Exception {
        System.out.println("Parsing " + filepath);
        int pageCount = 0;
        int acceptedPageCount = 0;

        XMLInputFactory factory = XMLInputFactory.newInstance();
        // https://rules.sonarsource.com/java/RSPEC-2755
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        try (FileInputStream inputStream = new FileInputStream(filepath)) {
            XMLEventReader reader = factory.createXMLEventReader(inputStream);

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement() && "page".equals(event.asStartElement().getName().getLocalPart())) {
                    pageCount += 1;

                    String pageTitle = parsePage(reader);
                    if (pageTitle != null) {
                        acceptedPageCount += 1;
                        System.out.println("      [" + pageTitle + "]");
                        if (acceptedPageCount >= 100) { break; }
                    }

                    if (pageCount % 100000 == 0) {
                        System.out.println("   Parsed " + pageCount + " pages...");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error at page " + pageCount);
            throw e;
        }

        System.out.println("Parsed " + pageCount + " pages!");
    }

    // Returns the page title if accepted, null if not
    // Upon return, the reader should be at the end of the page
    static String parsePage(XMLEventReader reader) throws Exception {
        String title = null;
        String namespace = null;
        String text = null;

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                String elementName = event.asStartElement().getName().getLocalPart();

                if ("title".equals(elementName)) {
                    title = extractText(reader);
                } else if ("ns".equals(elementName)) {
                    namespace = extractText(reader);
                } else if ("revision".equals(elementName)) {
                    while (reader.hasNext()) {
                        event = reader.nextEvent();
                        if (event.isStartElement() && "text".equals(event.asStartElement().getName().getLocalPart())) {
                            text = extractText(reader);
                        } else if (event.isEndElement() && "revision".equals(event.asEndElement().getName().getLocalPart())) {
                            break;
                        }
                    }
                }
            } else if (event.isEndElement() && "page".equals(event.asEndElement().getName().getLocalPart())) {
                break;
            }
        }

        if (namespace != null && Integer.parseInt(namespace) == MAIN_NAMESPACE &&
                title != null && ALPHABETIC.matcher(title).matches() &&
                text != null && text.contains(ENGLISH_HEADER)) {
            return title;
        }

        return null;
    }

    static String extractText(XMLEventReader reader) throws Exception {
        StringBuilder sb = new StringBuilder();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isCharacters()) {
                sb.append(event.asCharacters().getData());
            } else {
                break;
            }
        }

        return sb.toString();
    }
}