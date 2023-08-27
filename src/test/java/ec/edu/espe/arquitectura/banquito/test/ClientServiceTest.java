package ec.edu.espe.arquitectura.banquito.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ec.edu.espe.arquitectura.banquito.dto.ClientAddressRQ;
import ec.edu.espe.arquitectura.banquito.dto.ClientPhoneRQ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ec.edu.espe.arquitectura.banquito.dto.ClientRQ;
import ec.edu.espe.arquitectura.banquito.model.Client;
import ec.edu.espe.arquitectura.banquito.model.ClientAddress;
import ec.edu.espe.arquitectura.banquito.model.ClientPhone;
import ec.edu.espe.arquitectura.banquito.repository.ClientRepository;
import ec.edu.espe.arquitectura.banquito.service.ClientService;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    private ClientPhone phone;
    private ClientAddress address;
    private List<ClientPhone> phoneNumbers;
    private List<ClientAddress> addresses;

    private Client client;

    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        this.clientService = new ClientService(this.clientRepository);
        this.phone = ClientPhone.builder().phoneType("OFF").phoneNumber("1234567890").isDefault(true).state("ACT").build();
        this.address = ClientAddress.builder().locationId("1").isDefault(true).latitude(Float.parseFloat(String.valueOf(17.908736))).line1("Alcides Enriquez").line2("Chasqui").longitude(Float.parseFloat(String.valueOf(89.908736))).state("ACT").build();
        this.phoneNumbers = new ArrayList<>();
        phoneNumbers.add(this.phone);
        this.addresses = new ArrayList<>();
        addresses.add(this.address);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate;
        try {
            birthDate = dateFormat.parse("1990-01-15");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.client = Client.builder().id("123456").branchId("branch123").uniqueKey("key123").typeDocumentId("IDE").documentId("1722620489").firstName("David").lastName("Tamayo").gender("MAS").birthDate(birthDate).emailAddress("datamayo4@espe.edu.ec").creationDate(new Date()).activationDate(new Date()).lastModifiedDate(new Date()).role(null).state("ACT").closedDate(null).comments("test").password("123").addresses(addresses).phoneNumbers(phoneNumbers).build();
    }

    @Test
    void testObtainClientByDocumentTypeAndDocumentId() {
        when(this.clientRepository.findFirstByTypeDocumentIdAndDocumentId("IDE", "1722620489")).thenReturn(this.client);
        assertDoesNotThrow(() -> {
            this.clientService.obtainClientByDocumentTypeAndDocumentId("IDE", "1722620489");
        });
        assertThrows(RuntimeException.class, () -> {
            this.clientService.obtainClientByDocumentTypeAndDocumentId("IDE", "1176356789");
        });
        this.client.setState("INA");
        assertThrows(RuntimeException.class, () -> {
            this.clientService.obtainClientByDocumentTypeAndDocumentId("IDE", "1722620489");
        });
    }

    @Test
    void testObtainClientById(){
        when(this.clientRepository.findFirstByUniqueKey("key123")).thenReturn(this.client);
        assertDoesNotThrow(() -> {
            this.clientService.obtainClientById("key123");
        });
        assertThrows(RuntimeException.class, () -> {
            this.clientService.obtainClientById("key");
        });
        this.client.setState("INA");
        assertThrows(RuntimeException.class, () -> {
            this.clientService.obtainClientById("key123");
        });
    }
    @Test
    void testObtainLogin(){
        when(this.clientRepository.findFirstByUniqueKey("key123")).thenReturn(this.client);
        assertDoesNotThrow(() -> {
            this.clientService.obtainLogin("key123");
        });
        assertThrows(RuntimeException.class, () -> {
            this.clientService.obtainLogin("key");
        });
    }

    @Test
    void testClientCreate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate;
        Date badBirthDate;
        try {
            birthDate = dateFormat.parse("1999-07-20");
            badBirthDate = dateFormat.parse("2023-12-25");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ClientRQ clientRQ = ClientRQ.builder().branchId("branch123").typeDocumentId("IDE").documentId("1722620489").firstName("David").lastName("Tamayo").gender("MAS").birthDate(birthDate).emailAddress("datamayo4@espe.edu.ec").role(null).comments("test create").password("123").build();

        when(this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(clientRQ.getTypeDocumentId(), clientRQ.getDocumentId())).thenReturn(null);
        assertDoesNotThrow(() -> {
            this.clientService.clientCreate(clientRQ);
        });
        clientRQ.setBirthDate(badBirthDate);
        assertThrows(RuntimeException.class, () -> {
            this.clientService.clientCreate(clientRQ);
        });
    }

    @Test
    void testUpdateClient() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate;
        try {
            birthDate = dateFormat.parse("1999-07-20");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ClientRQ clientRQ = ClientRQ.builder().branchId("branch222").typeDocumentId("IDE").documentId("1722620489").firstName("Lucas").lastName("Hernandez").gender("MAS").birthDate(birthDate).emailAddress("gugli10@hotmail.com").role(null).comments("modificado").password("1234").state("ACT").build();

        when(this.clientRepository.findFirstByTypeDocumentIdAndDocumentId("IDE", "1722620489")).thenReturn(this.client);
        assertDoesNotThrow(() -> {
            this.clientService.updateClient(clientRQ, "IDE", "1722620489");
        });
        assertThrows(RuntimeException.class, () -> {
            this.clientService.updateClient(clientRQ, "IDE", "1787654357");
        });
    }

    @Test
    void testDeleteClient() {
        when(this.clientRepository.findFirstByTypeDocumentIdAndDocumentId("IDE", "1722620489")).thenReturn(this.client);
        assertDoesNotThrow(() -> {
            this.clientService.deleteClient("IDE", "1722620489");
        });
        assertThrows(RuntimeException.class, () -> {
            this.clientService.deleteClient("RUC", "1722620489");
        });
    }

    @Test
    void testAddPhones() {
        when(this.clientRepository.findFirstByTypeDocumentIdAndDocumentId("IDE", "1722620489")).thenReturn(this.client);
        List<ClientPhoneRQ> phonesRQ = new ArrayList<>();
        ClientPhoneRQ phoneRQ = ClientPhoneRQ.builder().phoneNumber("0999744275").phoneType("OFF").isDefault(true).build();
        phonesRQ.add(phoneRQ);
        assertDoesNotThrow(() -> {
            this.clientService.addPhones("IDE", "1722620489", phonesRQ);
        });
        assertThrows(RuntimeException.class, () -> {
            this.clientService.addPhones("RUC", "1722620489", phonesRQ);
        });
        ClientPhoneRQ duplicatedPhoneRQ = ClientPhoneRQ.builder().phoneNumber("0999744275").phoneType("HOM").isDefault(false).build();
        phonesRQ.add(duplicatedPhoneRQ);
        assertThrows(RuntimeException.class, () -> {
            this.clientService.addPhones("IDE", "1722620489", phonesRQ);
        });
    }

    @Test
    void testUpdatePhone() {
        ClientPhoneRQ phoneRQ = ClientPhoneRQ.builder().phoneNumber("0999744275").phoneType("OFF").isDefault(true).state("INA").build();
        when(this.clientRepository.findFirstByTypeDocumentIdAndDocumentId("IDE", "1722620489")).thenReturn(this.client);
        assertDoesNotThrow(() -> {
            this.clientService.updatePhone("IDE", "1722620489", "1234567890", phoneRQ);
        });
        assertThrows(RuntimeException.class, () -> {
            this.clientService.updatePhone("RUC", "1722620489", "1234567890", phoneRQ);
        });
        assertThrows(RuntimeException.class, () -> {
            this.clientService.updatePhone("IDE", "1722620489", "0987865122", phoneRQ);
        });
    }

    @Test
    void testAddAddresses() {
        when(this.clientRepository.findFirstByTypeDocumentIdAndDocumentId("IDE", "1722620489")).thenReturn(this.client);
        List<ClientAddressRQ> addressesRQ = new ArrayList<>();
        ClientAddressRQ addressRQ = ClientAddressRQ.builder().typeAddress("HOM").isDefault(true)
                .latitude(Float.parseFloat("78.9890")).longitude(Float.parseFloat("-98.98"))
                .locationId("address1").line1("calle1").line2("calle2").build();
        addressesRQ.add(addressRQ);
        assertDoesNotThrow(() -> {
            this.clientService.addAddresses("IDE", "1722620489", addressesRQ);
        });
        assertThrows(RuntimeException.class, () -> {
            this.clientService.addAddresses("RUC", "1722620489", addressesRQ);
        });
        ClientAddressRQ duplicatedAddressRQ = ClientAddressRQ.builder().typeAddress("OFF").isDefault(false)
                .latitude(Float.parseFloat("78.9890")).longitude(Float.parseFloat("-98.98"))
                .locationId("address1").line1("calle1").line2("calle2").build();
        addressesRQ.add(duplicatedAddressRQ);
        assertThrows(RuntimeException.class, () -> {
            this.clientService.addAddresses("IDE", "1722620489", addressesRQ);
        });
    }

    @Test
    void testUpdateAddress() {
        ClientAddressRQ addressRQ = ClientAddressRQ.builder().typeAddress("HOM").isDefault(true)
                .latitude(Float.parseFloat("78.9890")).longitude(Float.parseFloat("-98.98"))
                .locationId("address1").line1("calle1").line2("calle2").build();
        when(this.clientRepository.findFirstByTypeDocumentIdAndDocumentId("IDE", "1722620489")).thenReturn(this.client);
        assertDoesNotThrow(() -> {
            this.clientService.updateAddress("IDE", "1722620489", "Alcides Enriquez", "Chasqui", addressRQ);
        });
        assertThrows(RuntimeException.class, () -> {
            this.clientService.updateAddress("RUC", "1722620489", "Alcides Enriquez", "Chasqui", addressRQ);
        });
        assertThrows(RuntimeException.class, () -> {
            this.clientService.updateAddress("IDE", "1722620489", "calle8", "calle6", addressRQ);
        });
    }
}
