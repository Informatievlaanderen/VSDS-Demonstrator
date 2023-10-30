package be.informatievlaanderen.vsds.demonstrator.member.presentation.rest;

import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberService;
import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberValidator;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.IngestedMemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.custom.MeetPuntRepository;
import be.informatievlaanderen.vsds.demonstrator.member.presentation.dtos.MapBoundsDto;
import org.apache.jena.rdf.model.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api")
public class MembersController {
    private final MemberService service;
    private final MemberValidator validator;
    private final MeetPuntRepository meetPuntRepository;

    public MembersController(MemberService service, MemberValidator validator, MeetPuntRepository meetPuntRepository) {
        this.service = service;
        this.validator = validator;
        this.meetPuntRepository = meetPuntRepository;
    }

    @GetMapping(value = "/geometry/{memberId}")
    public MemberDto retrieveLdesFragment(@PathVariable String memberId) {
        return service.getMemberById(memberId);
    }

    @PostMapping(value = "/{collectionName}/in-rectangle", consumes = {"application/json"})
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public List<MemberDto> getMembersInRectangle(@PathVariable String collectionName, @RequestBody MapBoundsDto mapBoundsDto, @RequestParam LocalDateTime timestamp) {
        return service.getMembersInRectangle(mapBoundsDto.getGeometry(), collectionName, timestamp);
    }

    @PostMapping(value = "/{collectionName}/members")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public void ingestLdesMember(@PathVariable String collectionName, @RequestBody Model model) {
        validator.validate(model, collectionName);
        if (collectionName.equals("verkeersmeting")) {
            addMeetpuntLocation(model); // CUSTOM CODE
        }
        service.ingestMember(new IngestedMemberDto(collectionName, model));
    }

    private void addMeetpuntLocation(Model model) {
        String[] split = model.listObjectsOfProperty(model.createProperty("https://data.vlaanderen.be/ns/verkeersmetingen#verkeersmeetpunt")).next().toString().split("/");
        model.add(meetPuntRepository.get(split[split.length - 1]));
    }
}
