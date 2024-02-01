package app.geocodingservice.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class MainControllerTest {
    @Autowired
    private MainController controller;

    @Test
    public void isValidIPForAddress() {
        Assertions.assertEquals(controller.getIP("Казань").getBody(), "49.106414 55.796127");
    }

    @Test
    public void isValidAddressForIP() {
        Assertions.assertEquals(controller.getAddress("37.617698 55.755864").getBody(), "Москва");
    }
}