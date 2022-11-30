package io.github.mikesaelim;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.util.regex.Pattern;

public class Main {
    static final Pattern ALPHABETIC = Pattern.compile("\\p{L}+");

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
                        if (acceptedPageCount >= 3000) { break; }
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

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                String elementName = event.asStartElement().getName().getLocalPart();

                if ("title".equals(elementName)) {
                    event = reader.nextEvent();
                    if (event.isCharacters()) {
                        title = event.asCharacters().getData();
                        if (!ALPHABETIC.matcher(title).matches()) {
                            goToEndOfPage(reader);
                            return null;
                        }
                    } else {
                        // TODO: error handling
                        throw new Exception("Could not read title!");
                    }
                } else if ("ns".equals(elementName)) {
                    event = reader.nextEvent();
                    if (!event.isCharacters() || Integer.parseInt(event.asCharacters().getData()) != 0) {
                        goToEndOfPage(reader);
                        return null;
                    }
                }
            } else if (event.isEndElement() && "page".equals(event.asEndElement().getName().getLocalPart())) {
                break;
            }
        }

        return title;
    }

    static void goToEndOfPage(XMLEventReader reader) throws Exception {
        // Assumes that the reader is not already at the end of a page
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isEndElement() && "page".equals(event.asEndElement().getName().getLocalPart())) {
                break;
            }
        }
    }
}