package com.twitter.robot.write;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import twitter4j.Status;

public class WriteToXML {
    private final static String FILE_PATH = "D:\\file.tweet";

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = dateFormat.parse("15-авг-2015");
        deleteOldTweetXML(date);
    }
    public static void createXMLAndWrite(List<Status> tweets) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("tweets");
            doc.appendChild(rootElement);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            for (Status loop : tweets) {
                //tweet element
                Element tweet = doc.createElement("tweet");
                rootElement.appendChild(tweet);

                //set atribut to this element
                Attr attrId = doc.createAttribute("id");
                attrId.setValue(String.valueOf(loop.getId()));
                tweet.setAttributeNode(attrId);

                //set atribut to this element - date when this tweet recording to XML
                Attr date = doc.createAttribute("date");
                date.setValue(dateFormat.format(new Date()));
                tweet.setAttributeNode(date);

                //when this tweet created
                Element createdDate = doc.createElement("created");
                createdDate.appendChild(doc.createTextNode(dateFormat.format(loop.getCreatedAt())));
                tweet.appendChild(createdDate);

                //tweet text
                Element text = doc.createElement("text");
                text.appendChild(doc.createTextNode(loop.getText()));
                tweet.appendChild(text);

                //user element
                Element user = doc.createElement("user");
                tweet.appendChild(user);

                //parameter for user element
                Element userId = doc.createElement("id");
                user.appendChild(userId);

                //set atribut to this element
                Attr userIdAttr = doc.createAttribute("id");
                userIdAttr.setValue(String.valueOf(loop.getUser().getId()));
                userId.setAttributeNode(userIdAttr);

                Element name = doc.createElement("name");
                name.appendChild(doc.createTextNode(loop.getUser().getName()));
                user.appendChild(name);

                Element screenName = doc.createElement("screen_name");
                screenName.appendChild(doc.createTextNode(loop.getUser().getScreenName()));
                user.appendChild(screenName);

                Element location = doc.createElement("location");
                location.appendChild(doc.createTextNode(loop.getUser().getLocation()));
                user.appendChild(location);
            }
            // write the content into tweet file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(FILE_PATH));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public static Map<String, String> deleteOldTweetXML(Date anotherDate) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = docFactory.newDocumentBuilder();
            File file = new File(FILE_PATH);
            Document doc = documentBuilder.parse(file);

            NodeList list = doc.getElementsByTagName("tweet");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

            if (list != null && list.getLength() > 0) {
                for (int i = 0; i < list.getLength(); ) {
                    Element element = (Element) list.item(i);
                    //NodeList nextElement = element.getElementsByTagName("created");
                    Date oldDate = dateFormat.parse(element.getAttribute("date"));
                    System.out.println(oldDate);
                    //Date createAt = dateFormat.parse(nextElement.item(0).getChildNodes().item(0).getNodeValue());
                    if (oldDate.compareTo(anotherDate) == 0) {
                        element.getParentNode().removeChild(element);
                        System.out.println("deleted date " + oldDate);
                    } else {
                        i++;
                    }
                }
            }
            // write the content into tweet file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(FILE_PATH));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);
            System.out.println("Deleted old tweet");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int appendToXML(List<Status> tweets) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;
            documentBuilder = docFactory.newDocumentBuilder();
            File file = new File(FILE_PATH);
            Document doc = documentBuilder.parse(file);
            NodeList list = doc.getElementsByTagName("tweet");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            System.out.println("XML have " + list.getLength() + " tweets");
            if (list.getLength() <= 2000) {
                for (Status loop : tweets) {
                    //tweet element
                    Element tweet = doc.createElement("tweet");
                    //set atribut to this element
                    Attr attrId = doc.createAttribute("id");
                    attrId.setValue(String.valueOf(loop.getId()));
                    tweet.setAttributeNode(attrId);

                    //set atribut to this element - date when this tweet recording to XML
                    Attr date = doc.createAttribute("date");
                    date.setValue(dateFormat.format(new Date()));
                    tweet.setAttributeNode(date);

                    //when this tweet created
                    Element createdDate = doc.createElement("created");
                    createdDate.appendChild(doc.createTextNode("date"));
                    tweet.appendChild(createdDate);

                    //tweet text
                    Element text = doc.createElement("text");
                    text.appendChild(doc.createTextNode(loop.getText()));
                    tweet.appendChild(text);

                    //user element
                    Element user = doc.createElement("user");
                    tweet.appendChild(user);

                    //parameter for user element
                    Element userId = doc.createElement("id");
                    user.appendChild(userId);

                    //set atribut to this element
                    Attr userIdAttr = doc.createAttribute("id");
                    userIdAttr.setValue(String.valueOf(loop.getUser().getId()));
                    userId.setAttributeNode(userIdAttr);

                    Element name = doc.createElement("name");
                    name.appendChild(doc.createTextNode(loop.getUser().getName()));
                    user.appendChild(name);

                    Element screenName = doc.createElement("screen_name");
                    screenName.appendChild(doc.createTextNode(loop.getUser().getScreenName()));
                    user.appendChild(screenName);

                    Element location = doc.createElement("location");
                    location.appendChild(doc.createTextNode(loop.getUser().getLocation()));
                    user.appendChild(location);

                    list.item(list.getLength() - 1).getParentNode().appendChild(tweet);
                }


                // write the content into tweet file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(FILE_PATH));

                transformer.transform(source, result);

                System.out.println("File saved!");
            }
            else return -1;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return 1;
    }

}
