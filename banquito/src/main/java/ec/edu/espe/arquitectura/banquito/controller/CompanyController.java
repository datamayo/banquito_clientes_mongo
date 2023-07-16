package ec.edu.espe.arquitectura.banquito.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.espe.arquitectura.banquito.dto.GroupCompanyMemberRQ;
import ec.edu.espe.arquitectura.banquito.dto.GroupCompanyRS;
import ec.edu.espe.arquitectura.banquito.service.GroupCompanyService;

@RestController
@RequestMapping("/api/v2/companies")
public class CompanyController {
    private final GroupCompanyService groupCompanyService;
    
    
     public CompanyController(GroupCompanyService groupCompanyService) {
        this.groupCompanyService = groupCompanyService;
    }

    @GetMapping("/{groupName}")
    public ResponseEntity<GroupCompanyRS> obtainByGroupName(
            @PathVariable(name = "groupName") String groupName) {
        try {
            GroupCompanyRS company = this.groupCompanyService.obtainCompanyByGroupName(groupName);
            return ResponseEntity.ok(company);
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/addMember/{groupName}")
    public ResponseEntity<String> addMembers(@RequestBody List<GroupCompanyMemberRQ> membersRQ,
            @PathVariable(name = "groupName") String groupName) {
        try {
            //GroupCompany companyRS = this.groupCompanyService.addMember(groupName, membersRQ);
            this.groupCompanyService.addMember(groupName, membersRQ);
            return ResponseEntity.ok("Operación exitosa");
        } catch (RuntimeException rte) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(rte.getMessage());
        }
    }
}
