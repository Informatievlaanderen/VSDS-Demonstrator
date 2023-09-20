package be.informatievlaanderen.vsds.demonstrator.member.rest;

import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberService;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.IngestedMemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.rest.dtos.MapBoundsDto;
import org.apache.jena.rdf.model.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class MembersController {
    private final MemberService service;

    public MembersController(MemberService service) {
        this.service = service;
    }

    @GetMapping(value = "/geometry/{memberId}")
    public MemberDto retrieveLdesFragment(@PathVariable String memberId) {
        return service.getMemberById(memberId);
    }

    @PostMapping(value = "/in-rectangle", consumes = {"application/json"})
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public List<MemberDto> getMembersInRectangle(@RequestBody MapBoundsDto mapBoundsDto, @RequestParam LocalDateTime timestamp) {
        return service.getMembersInRectangle(mapBoundsDto.getGeometry(), timestamp);
    }

    @PostMapping(value = "/members")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public void ingestLdesMember(@RequestBody Model model) {
        service.ingestMember(new IngestedMemberDto(model));
    }
}
