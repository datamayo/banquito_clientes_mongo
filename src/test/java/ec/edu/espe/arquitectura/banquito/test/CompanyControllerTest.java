package ec.edu.espe.arquitectura.banquito.test;

import ec.edu.espe.arquitectura.banquito.controller.CompanyController;
import ec.edu.espe.arquitectura.banquito.dto.*;
import ec.edu.espe.arquitectura.banquito.model.GroupCompany;
import ec.edu.espe.arquitectura.banquito.model.GroupCompanyMember;
import ec.edu.espe.arquitectura.banquito.service.GroupCompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyControllerTest {
    private GroupCompany company;
    @InjectMocks
    private CompanyController companyController;

    @Mock
    private GroupCompanyService groupCompanyService;

    @BeforeEach
    void setUp() {
        this.companyController = new CompanyController(this.groupCompanyService);
        GroupCompanyMember member = GroupCompanyMember.builder().groupRole("Presidente").clientId("key123").build();
        List<GroupCompanyMember> members = new ArrayList<>();
        members.add(member);
        this.company = GroupCompany.builder().groupName("My Group")
                .emailAddress("group@example.com")
                .phoneNumber("123-456-7890")
                .line1("123 Main Street")
                .line2("Suite 456")
                .latitude(37.7749f)
                .longitude(-122.4194f)
                .creationDate(new Date())
                .activationDate(new Date())
                .lastModifiedDate(new Date())
                .state("ACT")
                .closedDate(null)
                .comments("test").members(members).build();
    }

    @Test
    void testObtainByGroupNameOk() {
        GroupCompanyMemberRS memberRS = GroupCompanyMemberRS.builder().groupRole("Presidente").clientId("key123").build();
        List<GroupCompanyMemberRS> members = new ArrayList<>();
        members.add(memberRS);
        GroupCompanyRS companyRS = GroupCompanyRS.builder().groupName("My Group")
                .emailAddress("group@example.com")
                .phoneNumber("123-456-7890")
                .line1("123 Main Street")
                .line2("Suite 456")
                .latitude(37.7749f)
                .longitude(-122.4194f)
                .creationDate(new Date())
                .activationDate(new Date())
                .lastModifiedDate(new Date())
                .state("ACT")
                .closedDate(null)
                .comments("test").members(members).build();
        String name = "My Group";
        when(this.groupCompanyService.obtainCompanyByGroupName(name)).thenReturn(companyRS);
        ResponseEntity<GroupCompanyRS> response = this.companyController.obtainByGroupName(name);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(companyRS, response.getBody());
    }

    @Test
    void testObtainByGroupNameNotFound() {
        String name = "My Group";
        when(this.groupCompanyService.obtainCompanyByGroupName(name)).thenThrow(new RuntimeException());
        ResponseEntity<GroupCompanyRS> response = this.companyController.obtainByGroupName(name);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    void testObtainAllCompaniesOk() {
        List<GroupCompanyRS> companies = new ArrayList<>();
        GroupCompanyMemberRS memberRS = GroupCompanyMemberRS.builder().groupRole("Presidente").clientId("key123").build();
        List<GroupCompanyMemberRS> members = new ArrayList<>();
        members.add(memberRS);
        GroupCompanyRS companyRS = GroupCompanyRS.builder().groupName("My Group")
                .emailAddress("group@example.com")
                .phoneNumber("123-456-7890")
                .line1("123 Main Street")
                .line2("Suite 456")
                .latitude(37.7749f)
                .longitude(-122.4194f)
                .creationDate(new Date())
                .activationDate(new Date())
                .lastModifiedDate(new Date())
                .state("ACT")
                .closedDate(null)
                .comments("test").members(members).build();
        companies.add(companyRS);
        when(this.groupCompanyService.obtainAllCompanies()).thenReturn(companies);
        ResponseEntity<List<GroupCompanyRS>> response = this.companyController.obtainAllCompanies();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(companies, response.getBody());
    }

    @Test
    void testObtainAllCompaniesNotFound() {
        when(this.groupCompanyService.obtainAllCompanies()).thenThrow(new RuntimeException());
        ResponseEntity<List<GroupCompanyRS>> response = this.companyController.obtainAllCompanies();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAddMembersOk() {
        GroupCompanyMemberRQ memberRQ = GroupCompanyMemberRQ.builder().groupRole("Admin").clientId("key123").state("INA").build();
        List<GroupCompanyMemberRQ> members = new ArrayList<>();
        members.add(memberRQ);
        when(this.groupCompanyService.addMember("My Group", members)).thenReturn(this.company);
        ResponseEntity<GroupCompany> response = this.companyController.addMembers(members, "My Group");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.company, response.getBody());
    }

    @Test
    void testAddMembersBadRequest() {
        GroupCompanyMemberRQ memberRQ = GroupCompanyMemberRQ.builder().groupRole("Admin").clientId("key123").state("INA").build();
        List<GroupCompanyMemberRQ> members = new ArrayList<>();
        members.add(memberRQ);
        when(this.groupCompanyService.addMember("My Group", members)).thenThrow(new RuntimeException());
        ResponseEntity<GroupCompany> response = this.companyController.addMembers(members, "My Group");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testClientCreateOk() {
        GroupCompanyRQ companyRQ = GroupCompanyRQ.builder().groupName("My Group")
                .emailAddress("group@example.com")
                .phoneNumber("123-456-7890")
                .line1("123 Main Street")
                .line2("Suite 456")
                .latitude(37.7749f)
                .longitude(-122.4194f)
                .state("ACT")
                .comments("test").build();
        when(this.groupCompanyService.companyCreate(eq(companyRQ))).thenReturn(this.company);
        ResponseEntity<GroupCompany> response = companyController.clientCreate(companyRQ);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.company, response.getBody());

    }

    @Test
    void testClientCreateBadRequest() {
        GroupCompanyRQ companyRQ = GroupCompanyRQ.builder().groupName("My Group")
                .emailAddress("group@example.com")
                .phoneNumber("123-456-7890")
                .line1("123 Main Street")
                .line2("Suite 456")
                .latitude(37.7749f)
                .longitude(-122.4194f)
                .state("ACT")
                .comments("test").build();
        when(this.groupCompanyService.companyCreate(eq(companyRQ))).thenThrow(new RuntimeException());
        ResponseEntity<GroupCompany> response = companyController.clientCreate(companyRQ);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void tesCompanyUpdateOk() {
        GroupCompanyRQ companyRQ = GroupCompanyRQ.builder().groupName("My Group")
                .emailAddress("group@example.com")
                .phoneNumber("123-456-7890")
                .line1("123 Main Street")
                .line2("Suite 456")
                .latitude(37.7749f)
                .longitude(-122.4194f)
                .state("ACT")
                .comments("test").build();
        when(this.groupCompanyService.updateCompany(eq(companyRQ), eq("key123"))).thenReturn(this.company);
        ResponseEntity<GroupCompany> response = companyController.companyUpdate(companyRQ, "key123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.company, response.getBody());

    }

    @Test
    void tesCompanyUpdateBadRequest(){
        GroupCompanyRQ companyRQ = GroupCompanyRQ.builder().groupName("My Group")
                .emailAddress("group@example.com")
                .phoneNumber("123-456-7890")
                .line1("123 Main Street")
                .line2("Suite 456")
                .latitude(37.7749f)
                .longitude(-122.4194f)
                .state("ACT")
                .comments("test").build();
        when(this.groupCompanyService.updateCompany(eq(companyRQ), eq("key123"))).thenThrow(new RuntimeException());
        ResponseEntity<GroupCompany> response = companyController.companyUpdate(companyRQ, "key123");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteCompanyOk() {
        String id = "key123";
        when(this.groupCompanyService.deleteCompany(id)).thenReturn(this.company);
        ResponseEntity<GroupCompany> response = this.companyController.companyDelete(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.company, response.getBody());
    }

    @Test
    void testDeleteCompanyBadRequest() {
        String id = "key123";
        when(this.groupCompanyService.deleteCompany(id)).thenThrow(new RuntimeException());
        ResponseEntity<GroupCompany> response = this.companyController.companyDelete(id);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testObtainMembersByGroupName() {
        String groupName = "groupName";
        List<ClientRS> expectedMembers = new ArrayList<>();
        when(this.groupCompanyService.listMembersByCompany(groupName)).thenReturn(expectedMembers);

        ResponseEntity<List<ClientRS>> responseEntity = this.companyController.obtainMembersByGroupName(groupName);

        verify(this.groupCompanyService).listMembersByCompany(groupName);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedMembers, responseEntity.getBody());
    }

    @Test
    public void testObtainMembersByGroupNameError() {
        String groupName = "groupName";
        when(this.groupCompanyService.listMembersByCompany(groupName)).thenThrow(new RuntimeException());

        ResponseEntity<List<ClientRS>> responseEntity = this.companyController.obtainMembersByGroupName(groupName);

        verify(this.groupCompanyService).listMembersByCompany(groupName);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
