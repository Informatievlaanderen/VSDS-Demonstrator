package be.informatievlaanderen.vsds.demonstrator.member.custom;

import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.IngestedMemberDto;
import jakarta.annotation.PostConstruct;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class MeetPuntRepository {

    private final Map<String, Statement> meetpuntLocaties;

    public MeetPuntRepository() {
        this.meetpuntLocaties = new HashMap<>();
    }

    @PostConstruct
    private void initializeMeetpunten() {
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        String content = WebClient.builder().exchangeStrategies(strategies).baseUrl("http://miv.opendata.belfla.be/miv/configuratie/xml").build().get().retrieve()
                .bodyToMono(String.class).block();
        Document doc = Jsoup.parse(content, "", Parser.xmlParser());
        for (Element e : doc.select("meetpunt")) {
            String meetpuntId = e.attr("unieke_id");
            String wkt = "POINT (" + e.select("lengtegraad_EPSG_4326").get(0).text().replace(",", ".") + " " + e.select("breedtegraad_EPSG_4326").get(0).text().replace(",", ".") + ")";

            meetpuntLocaties.put(meetpuntId, createStatement(createResource("http://custom"), createProperty("http://www.opengis.net/ont/geosparql#asWKT"), createTypedLiteral(wkt, TypeMapper.getInstance().getTypeByName("http://www.opengis.net/ont/geosparql#wktLiteral"))));
        }
    }


    public List<Statement> get(String s) {
        return List.of(meetpuntLocaties.get(s));
    }
}
