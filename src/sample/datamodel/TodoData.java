package sample.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import sample.NotifyConstructor;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class TodoData {
    private static TodoData instance = new TodoData();
    private static String filenamexml = "TodoItems.xml";
    private ObservableList<TodoItem> todoItems;
    private ObservableList<TodoItem> todoItemsforNotify;
    private DateTimeFormatter formatterforxml;

    public static TodoData getInstance() {
        return instance;
    }

    private TodoData() {
        formatterforxml = DateTimeFormatter.ofPattern("yyy-MM-dd");
    }

    public void saveToxml() throws ParserConfigurationException, TransformerException {
        DocumentBuilder builder;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();

        /**Запись  в XML файл*/
        Document doc = builder.newDocument();
        Element RootElement = doc.createElement("Tasks");
        doc.appendChild(RootElement);
        Iterator<TodoItem> iter = todoItems.iterator();
        for (int i = 0; i < todoItems.size(); i++) {
            Element task = doc.createElement("Task");
            RootElement.appendChild(task);
            while (iter.hasNext()) {
                TodoItem item = iter.next();

                Element Desc = doc.createElement("ShortDescription");
                Desc.appendChild(doc.createTextNode(item.getShortDescription()));
                task.appendChild(Desc);

                Element NameElementTitle = doc.createElement("Details");
                if (item.getDetails().equals(null)) {
                    NameElementTitle.appendChild(doc.createTextNode("  "));
                }
                NameElementTitle.appendChild(doc.createTextNode(item.getDetails()));
                task.appendChild(NameElementTitle);

                Element NameElementCompile = doc.createElement("Deadline");
                NameElementCompile.appendChild(doc.createTextNode(item.getDeadline().toString()));
                task.appendChild(NameElementCompile);

                Element NameElementRuns = doc.createElement("Priority");
                NameElementRuns.appendChild(doc.createTextNode(item.getPriority()));
                task.appendChild(NameElementRuns);
                break;
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filenamexml));
        transformer.transform(source, result);
    }

    public void loadFromxml() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        todoItems = FXCollections.observableArrayList();
        todoItemsforNotify = FXCollections.observableArrayList();
        if ((new File(filenamexml)).exists()) {
            File fXmlFile = new File(filenamexml);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            for (int i = 0; i < doc.getElementsByTagName("Task").getLength(); i++) {
                Node node1 = doc.getElementsByTagName("ShortDescription").item(i).getChildNodes().item(0);
                String shortDescription = node1.getNodeValue();

                Node node2 = doc.getElementsByTagName("Details").item(i).getChildNodes().item(0);
                String details = node2.getNodeValue();

                Node node3 = doc.getElementsByTagName("Deadline").item(i).getChildNodes().item(0);
                String dateString = node3.getNodeValue();

                Node node4 = doc.getElementsByTagName("Priority").item(i).getChildNodes().item(0);
                String priority = node4.getNodeValue();

                LocalDate date = LocalDate.parse(dateString, formatterforxml);
                TodoItem todoItem = new TodoItem(shortDescription, details, date, priority);
                if (LocalDate.now().isEqual(date)) {
                    todoItemsforNotify.add(todoItem);
                }
                todoItems.add(todoItem);
            }
        } else {
            saveToxml();
        }
    }

    public ObservableList<TodoItem> getTodoItemsforNotify(){
        return todoItemsforNotify;
    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void addTodoItem(TodoItem item) {
        todoItems.add(item);
    }

    public void deleteTodoItem(TodoItem item) {
        todoItems.remove(item);
    }
}
