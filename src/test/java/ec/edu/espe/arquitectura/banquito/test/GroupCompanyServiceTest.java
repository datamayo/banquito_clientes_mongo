package ec.edu.espe.arquitectura.banquito.test;

import ec.edu.espe.arquitectura.banquito.dto.*;
import ec.edu.espe.arquitectura.banquito.model.*;
import ec.edu.espe.arquitectura.banquito.repository.ClientRepository;
import ec.edu.espe.arquitectura.banquito.repository.GroupCompanyRepository;
import ec.edu.espe.arquitectura.banquito.service.GroupCompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupCompanyServiceTest {
    private GroupCompany company;

    private Client client;
    @InjectMocks
    private GroupCompanyService groupCompanyService;

    @Mock
    private GroupCompanyRepository groupCompanyRepository;

    @Mock
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        List<GroupCompanyMember> members = new ArrayList<>();
        GroupCompanyMember member = GroupCompanyMember.builder().groupRole("Admin")
                .clientId("123456")
                .state("ACT")
                .creationDate(new Date())
                .lastModifiedDate(new Date())
                .build();
        members.add(member);
        this.groupCompanyService = new GroupCompanyService(this.groupCompanyRepository, this.clientRepository);
        this.company = GroupCompany.builder().branchId("branch123").locationId("location1").
                uniqueKey("uniqueKey123")
                .groupName("Example Group")
                .emailAddress("example@example.com")
                .phoneNumber("123-456-7890")
                .line1("123 Main St")
                .line2("Apt 4B")
                .latitude(37.7749f)
                .longitude(-122.4194f)
                .creationDate(new Date())
                .activationDate(new Date())
                .lastModifiedDate(new Date())
                .state("ACT")
                .closedDate(null)
                .comments("This is an example group")
                .members(members)
                .build();
        ClientPhone phone = ClientPhone.builder().phoneType("OFF").phoneNumber("1234567890").isDefault(true).state("ACT").build();
        ClientAddress address = ClientAddress.builder().locationId("1").isDefault(true).latitude(Float.parseFloat(String.valueOf(17.908736))).line1("Alcides Enriquez").line2("Chasqui").longitude(Float.parseFloat(String.valueOf(89.908736))).state("ACT").build();
        List<ClientPhone> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(phone);
        List<ClientAddress> addresses = new ArrayList<>();
        addresses.add(address);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate;
        try {
            birthDate = dateFormat.parse("1990-01-15");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.client = Client.builder().id("123456").branchId("branch123").uniqueKey("key123")
                .typeDocumentId("IDE").documentId("1722620489").
                firstName("David").lastName("Tamayo").gender("MAS")
                .birthDate(birthDate).emailAddress("datamayo4@espe.edu.ec")
                .creationDate(new Date()).activationDate(new Date())
                .lastModifiedDate(new Date()).role(null).state("ACT")
                .closedDate(null).comments("test").password("123")
                .addresses(addresses).phoneNumbers(phoneNumbers).build();
    }

    @Test
    void testObtainCompanyByGroupName() {
        when(this.groupCompanyRepository.findFirstByGroupName("Example Group")).thenReturn(this.company);
        assertDoesNotThrow(() -> {
            this.groupCompanyService.obtainCompanyByGroupName("Example Group");
        });
        assertThrows(RuntimeException.class, () -> {
            this.groupCompanyService.obtainCompanyByGroupName("Empresa");
        });
        this.company.setState("INA");
        assertThrows(RuntimeException.class, () -> {
            this.groupCompanyService.obtainCompanyByGroupName("Example Group");
        });
    }

    @Test
    void testObtainAllCompanies() {
        List<GroupCompany> companies = new ArrayList<>();
        companies.add(this.company);
        when(this.groupCompanyRepository.findAll()).thenReturn(companies);
        assertDoesNotThrow(() -> {
            this.groupCompanyService.obtainAllCompanies();
        });
        companies.clear();
        assertThrows(RuntimeException.class, () -> {
            this.groupCompanyService.obtainAllCompanies();
        });
    }

    @Test
    void testAddMember() {
        List<GroupCompanyMemberRQ> membersRQ = new ArrayList<>();
        GroupCompanyMemberRQ memberRQ = GroupCompanyMemberRQ.builder().groupRole("Presidente").clientId("key123").state("ACT").build();
        membersRQ.add(memberRQ);
        when(this.groupCompanyRepository.findFirstByGroupName("Example Group")).thenReturn(this.company);
        when(this.clientRepository.findFirstByUniqueKey("key123")).thenReturn(this.client);
        assertDoesNotThrow(() -> {
            this.groupCompanyService.addMember("Example Group", membersRQ);
        });
        GroupCompanyMemberRQ duplicatedMemberRQ = GroupCompanyMemberRQ.builder().groupRole("Presidente").clientId("key123").state("ACT").build();
        membersRQ.add(duplicatedMemberRQ);
        assertThrows(RuntimeException.class, () -> {
            this.groupCompanyService.addMember("Example Group", membersRQ);
        });
    }

    @Test
    void testCompanyCreate() {
        GroupCompanyRQ companyRQ = GroupCompanyRQ.builder().branchId("123")
                .locationId("456")
                .groupName("Example Company")
                .emailAddress("info@example.com")
                .phoneNumber("123-456-7890")
                .line1("123 Main St")
                .line2("Suite 200")
                .latitude(40.7128f)
                .longitude(-74.0060f)
                .state("ACT")
                .comments("This is a comment.")
                .build();
        when(this.groupCompanyRepository.findFirstByGroupName("Example Company")).thenReturn(null);
        assertDoesNotThrow(() -> {
            this.groupCompanyService.companyCreate(companyRQ);
        });

    }

    @Test
    void testUpdateCompany() {
        GroupCompanyRQ companyRQ = GroupCompanyRQ.builder().branchId("123")
                .locationId("456")
                .groupName("Example Company")
                .emailAddress("info@example.com")
                .phoneNumber("123-456-7890")
                .line1("123 Main St")
                .line2("Suite 200")
                .latitude(40.7128f)
                .longitude(-74.0060f)
                .state("ACT")
                .comments("This is a comment.")
                .build();
        when(this.groupCompanyRepository.findFirstByUniqueKey("uniqueKey123")).thenReturn(this.company);
        assertDoesNotThrow(() -> {
            this.groupCompanyService.updateCompany(companyRQ, "uniqueKey123");
        });
        assertThrows(RuntimeException.class, () -> {
            this.groupCompanyService.updateCompany(companyRQ, "123");
        });

    }

    @Test
    void testDeleteCompany() {
        when(this.groupCompanyRepository.findFirstByUniqueKey("uniqueKey123")).thenReturn(this.company);
        assertDoesNotThrow(() -> {
            this.groupCompanyService.deleteCompany("uniqueKey123");
        });
        assertThrows(RuntimeException.class, () -> {
            this.groupCompanyService.deleteCompany("123");
        });
    }

    @Test
    void testUpdateMember() {
        when(this.groupCompanyRepository.findFirstByUniqueKey("uniqueKey123")).thenReturn(this.company);
        GroupCompanyMemberRQ updateMemberRQ = GroupCompanyMemberRQ.builder().groupRole("Administrador").clientId("123456").state("INA").build();
        assertDoesNotThrow(() -> {
            this.groupCompanyService.updateMember("uniqueKey123", "123456", updateMemberRQ);
        });
        assertThrows(RuntimeException.class, () -> {
            this.groupCompanyService.updateMember("123", "123456", updateMemberRQ);
        });
        assertThrows(RuntimeException.class, () -> {
            this.groupCompanyService.updateMember("uniqueKey123", "123", updateMemberRQ);
        });

    }
    @Test
    void testListMembersByCompany(){
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
        when(this.groupCompanyRepository.findFirstByGroupName("Example Group")).thenReturn(this.company);
        when(this.clientRepository.findFirstByUniqueKey("123456")).thenReturn(this.client);
        assertDoesNotThrow(() -> {
            groupCompanyService.listMembersByCompany("Example Group");
        });

    }



}
