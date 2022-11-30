package io.github.mikesaelim;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;

public class Main {
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
}