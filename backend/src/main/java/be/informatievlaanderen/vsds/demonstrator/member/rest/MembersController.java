package be.informatievlaanderen.vsds.demonstrator.member.rest;

import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberService;
import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberValidator;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.IngestedMemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.rest.dtos.MapBoundsDto;
import org.apache.jena.rdf.model.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api")
public class MembersController {
    private final MemberService service;
    private final MemberValidator validator;

    public MembersController(MemberService service, MemberValidator validator) {
        this.service = service;
        this.validator = validator;
    }

    @GetMapping(value = "/geometry/{memberId}")
    public MemberDto retrieveLdesFragment(@PathVariable String memberId) {
        return service.getMemberById(memberId);
    }

    @PostMapping(value = "/{collectionName}/in-rectangle", consumes = {"application/json"})
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public List<MemberDto> getMembersInRectangle(@PathVariable(name = "collectionName") String collectionName, @RequestBody MapBoundsDto mapBoundsDto, @RequestParam LocalDateTime timestamp, @RequestParam(defaultValue = "PT1M") String timePeriod) {
        return service.getMembersInRectangle(mapBoundsDto.getGeometry(), collectionName, timestamp, timePeriod);
    }

    @PostMapping(value = "/{collectionName}/members")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public void ingestLdesMember(@PathVariable(name = "collectionName") String collectionName, @RequestBody Model model) {
        validator.validate(model, collectionName);
        service.ingestMember(new IngestedMemberDto(collectionName, model));
    }
}
