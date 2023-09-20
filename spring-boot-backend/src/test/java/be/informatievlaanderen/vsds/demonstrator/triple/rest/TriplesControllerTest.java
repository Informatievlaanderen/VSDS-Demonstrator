package be.informatievlaanderen.vsds.demonstrator.triple.rest;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
@ContextConfiguration(classes = {TriplesController.class})
class TriplesControllerTest {

}