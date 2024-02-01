package app.geocodingservice.controllers;

import app.geocodingservice.entities.Address;
import app.geocodingservice.entities.IP;
import app.geocodingservice.repositories.AddressRepository;
import app.geocodingservice.repositories.IPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/geocode")
public class MainController {

    private final RestTemplate restTemplate;
    private final AddressRepository addressRepository;
    private final IPRepository IPRepository;
    private static final String URL_YANDEX_GEOCODER = "https://geocode-maps.yandex.ru/1.x/?apikey=69006069-6e14-4dc8-b9de-331f2f8cdd66&geocode=";

    private final Logger logger = Logger.getLogger(MainController.class.getName());

    @Autowired
    MainController(RestTemplate restTemplate, AddressRepository addressRepository, IPRepository IPRepository) {

        this.restTemplate = restTemplate;
        this.addressRepository = addressRepository;
        this.IPRepository = IPRepository;
    }

    @GetMapping("/IP/{addressName}")
    public ResponseEntity<String> getIP(@PathVariable("addressName") String addressName) {

        Optional<Address> optional = addressRepository.findByName(addressName);
        if (optional.isPresent()) {
            IP response = optional.get().getIP();
            logger.info("Ответ из кэша БД на запрос " + addressName);
            return new ResponseEntity<>(response.getName(), HttpStatus.OK);
        }
        Document doc = getDocumentFromAPI(addressName);
        Node node = doc.getFirstChild().getFirstChild();
        node = findNodeByName("featureMember", node.getChildNodes());
        node = findNodeByName("Point", node.getFirstChild().getChildNodes());
        String IPName = node.getFirstChild().getTextContent();

        Address address = new Address(addressName);
        IP IP = new IP(IPName);
        address.setIP(IP);
        IP.setAddress(address);
        addressRepository.save(address);
        logger.info("Сохранение сущности Адреса = " + address.getName() + "\n" + "IP = " + IP.getName());

        return new ResponseEntity<>(IPName, HttpStatus.OK);
    }

    @GetMapping("/address/{IP}")
    public ResponseEntity<String> getAddress(@PathVariable("IP") String IPName) {

        Optional<IP> optional = IPRepository.findByName(IPName);
        if (optional.isPresent()) {
            Address response = optional.get().getAddress();
            logger.info("Ответ из кэша БД на запрос " + IPName);
            return new ResponseEntity<>(response.getName(), HttpStatus.OK);
        }

        Document doc = getDocumentFromAPI(IPName);
        Node node = doc.getFirstChild().getFirstChild();
        node = findNodeByName("featureMember", node.getChildNodes());
        node = findNodeByName("metaDataProperty", node.getFirstChild().getChildNodes());
        node = findNodeByName("GeocoderMetaData", node.getChildNodes());
        node = findNodeByName("Address", node.getChildNodes());
        node = findNodeByName("formatted", node.getChildNodes());
        String addressName = node.getTextContent();

        IP IP = new IP(IPName);
        Address address = new Address(addressName);
        IP.setAddress(address);
        address.setIP(IP);
        IPRepository.save(IP);
        logger.info("Сохранение сущности IP = " + IP.getName() + "\n" + "Адреса = " + address.getName());

        return new ResponseEntity<>(addressName, HttpStatus.OK);
    }

    private Document convertStrToXml(String str) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(str)));
        } catch (Exception e) {
            logger.info("Ошибка в парсинге XML-файла");
        }
        return null;
    }

    private Node findNodeByName(String name, NodeList list) {
        for (int i = 0; i < list.getLength(); i++) {
            Node curr = list.item(i);
            if (curr.getNodeName().equals(name)) {
                return curr;
            }
        }
        return null;
    }

    private Document getDocumentFromAPI(String request) {
        String URL = URL_YANDEX_GEOCODER + request + "&results=1";
        ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);
        logger.info("Ответ из API");
        String str = response.getBody();
        return convertStrToXml(str);
    }
}
