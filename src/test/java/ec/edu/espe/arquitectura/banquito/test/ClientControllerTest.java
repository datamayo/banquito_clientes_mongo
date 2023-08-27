package ec.edu.espe.arquitectura.banquito.test;

import ec.edu.espe.arquitectura.banquito.controller.ClientController;
import ec.edu.espe.arquitectura.banquito.dto.*;
import ec.edu.espe.arquitectura.banquito.model.Client;
import ec.edu.espe.arquitectura.banquito.model.ClientAddress;
import ec.edu.espe.arquitectura.banquito.model.ClientPhone;
import ec.edu.espe.arquitectura.banquito.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {
    private ClientRS clientRS;

    private Client client;
    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    void setUp() {
        this.clientController = new ClientController(this.clientService);
        ClientPhone phone = ClientPhone.builder().phoneType("OFF").phoneNumber("1234567890")
                .isDefault(true).state("ACT").build();
        ClientAddress address = ClientAddress.builder().locationId("1")
                .isDefault(true)
                .latitude(Float.parseFloat(String.valueOf(17.908736)))
                .line1("Alcides Enriquez").line2("Chasqui").longitude(Float.parseFloat(String.valueOf(89.908736)))
                .state("ACT").build();
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
                .typeDocumentId("IDE").documentId("1722620489")
                .firstName("David").lastName("Tamayo")
                .gender("MAS").birthDate(birthDate)
                .emailAddress("datamayo4@espe.edu.ec")
                .creationDate(new Date()).activationDate(new Date())
                .lastModifiedDate(new Date()).role(null)
                .state("ACT").closedDate(null).comments("test")
                .password("123").addresses(addresses)
                .phoneNumbers(phoneNumbers).build();

    }

    @Test
    void testObtainByDocumentTypeAndDocumentIdOk() {
        ClientPhoneRS phoneRS = ClientPhoneRS.builder().phoneType("OFF").phoneNumber("1234567890").isDefault(true).state("ACT").build();
        ClientAddressRS addressRS = ClientAddressRS.builder().locationId("1").isDefault(true)
                .latitude(Float.parseFloat(String.valueOf(17.908736)))
                .line1("Alcides Enriquez").line2("Chasqui")
                .longitude(Float.parseFloat(String.valueOf(89.908736))).state("ACT").build();
        List<ClientPhoneRS> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(phoneRS);
        List<ClientAddressRS> addresses = new ArrayList<>();
        addresses.add(addressRS);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate;
        try {
            birthDate = dateFormat.parse("1990-01-15");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.clientRS = ClientRS.builder().branchId("branch123")
                .uniqueKey("key123").typeDocumentId("IDE")
                .documentId("1722620489").firstName("David")
                .lastName("Tamayo").gender("MAS").birthDate(birthDate)
                .emailAddress("datamayo4@espe.edu.ec").creationDate(new Date())
                .activationDate(new Date())
                .lastModifiedDate(new Date()).role(null).
                state("ACT").closedDate(null).comments("test").addresses(addresses).phoneNumbers(phoneNumbers).build();
        String type = "IDE";
        String id = "1722620489";
        when(this.clientService.obtainClientByDocumentTypeAndDocumentId(type, id)).thenReturn(this.clientRS);
        ResponseEntity<ClientRS> response = this.clientController.obtainByDocumentTypeAndDocumentId(type, id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.clientRS, response.getBody());
    }

    @Test
    void testObtainByDocumentTypeAndDocumentIdNotFound() {
        String type = "RUC";
        String id = "1722620489";
        when(this.clientService.obtainClientByDocumentTypeAndDocumentId(type, id)).thenThrow(new RuntimeException());
        ResponseEntity<ClientRS> response = this.clientController.obtainByDocumentTypeAndDocumentId(type, id);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    void testObtainById() {
        ClientPhoneRS phoneRS = ClientPhoneRS.builder().phoneType("OFF").phoneNumber("1234567890").isDefault(true).state("ACT").build();
        ClientAddressRS addressRS = ClientAddressRS.builder().locationId("1").isDefault(true)
                .latitude(Float.parseFloat(String.valueOf(17.908736)))
                .line1("Alcides Enriquez").line2("Chasqui")
                .longitude(Float.parseFloat(String.valueOf(89.908736))).state("ACT").build();
        List<ClientPhoneRS> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(phoneRS);
        List<ClientAddressRS> addresses = new ArrayList<>();
        addresses.add(addressRS);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate;
        try {
            birthDate = dateFormat.parse("1990-01-15");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.clientRS = ClientRS.builder().branchId("branch123")
                .uniqueKey("key123").typeDocumentId("IDE")
                .documentId("1722620489").firstName("David")
                .lastName("Tamayo").gender("MAS").birthDate(birthDate)
                .emailAddress("datamayo4@espe.edu.ec").creationDate(new Date())
                .activationDate(new Date())
                .lastModifiedDate(new Date()).role(null).
                state("ACT").closedDate(null).comments("test").addresses(addresses).phoneNumbers(phoneNumbers).build();
        when(this.clientService.obtainClientById("key123")).thenReturn(this.clientRS);
        ResponseEntity<ClientRS> response = this.clientController.obtainById("key123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.clientRS, response.getBody());

    }

    @Test
    void testObtainByIdNotFound() {
        String id = "key12";
        when(this.clientService.obtainClientById(id)).thenThrow(new RuntimeException());
        ResponseEntity<ClientRS> response = this.clientController.obtainById(id);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    void testClientCreateOk() {
        String type = "IDE";
        String id = "1722620489";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate;
        try {
            birthDate = dateFormat.parse("1990-01-15");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ClientRQ clientRQ = ClientRQ.builder().branchId("branch123")
                .typeDocumentId("IDE").documentId("1722620489")
                .firstName("David").lastName("Tamayo")
                .gender("MAS").birthDate(birthDate)
                .emailAddress("datamayo4@espe.edu.ec").role(null)
                .comments("test").password("123")
                .state("ACT").build();
        when(this.clientService.clientCreate(eq(clientRQ))).thenReturn(this.client);
        ResponseEntity<Client> response = clientController.clientCreate(clientRQ);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.client, response.getBody());

    }

    @Test
    void testClientCreateBadRequest() {
        String type = "IDE";
        String id = "1722620489";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate;
        try {
            birthDate = dateFormat.parse("1990-01-15");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ClientRQ clientRQ = ClientRQ.builder().branchId("branch123")
                .typeDocumentId("IDE").documentId("1722620489")
                .firstName("David").lastName("Tamayo")
                .gender("MAS").birthDate(birthDate)
                .emailAddress("datamayo4@espe.edu.ec").role(null)
                .comments("test").password("123")
                .state("ACT").build();
        when(this.clientService.clientCreate(eq(clientRQ))).thenThrow(new RuntimeException());
        ResponseEntity<Client> response = clientController.clientCreate(clientRQ);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testClientUpdateOk() {
        String type = "IDE";
        String id = "1722620489";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate;
        try {
            birthDate = dateFormat.parse("1999-07-20");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ClientRQ clientRQ = ClientRQ.builder().branchId("branch222")
                .typeDocumentId("IDE").documentId("1722620489")
                .firstName("Lucas").lastName("Hernandez")
                .gender("MAS").birthDate(birthDate)
                .emailAddress("gugli10@hotmail.com").role(null)
                .comments("modificado").password("1234")
                .state("ACT").build();
        when(this.clientService.updateClient(eq(clientRQ), eq(type), eq(id))).thenReturn(this.client);
        ResponseEntity<Client> response = this.clientController.clientUpdate(clientRQ, type, id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.client, response.getBody());
    }


    @Test
    void testClientUpdateBadRequest() {
        String type = "RUC";
        String id = "1722620489";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate;
        try {
            birthDate = dateFormat.parse("1999-07-20");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ClientRQ clientRQ = ClientRQ.builder().branchId("branch222")
                .typeDocumentId("IDE").documentId("1722620489")
                .firstName("Lucas").lastName("Hernandez")
                .gender("MAS").birthDate(birthDate)
                .emailAddress("gugli10@hotmail.com").role(null)
                .comments("modificado").password("1234")
                .state("ACT").build();
        when(this.clientService.updateClient(eq(clientRQ), eq(type), eq(id))).thenThrow(new RuntimeException());
        ResponseEntity<Client> response = this.clientController.clientUpdate(clientRQ, type, id);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteClientOk() {
        String type = "IDE";
        String id = "1722620489";
        when(this.clientService.deleteClient(type, id)).thenReturn(this.client);
        ResponseEntity<Client> response = this.clientController.clientDelete(type, id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.client, response.getBody());
    }

    @Test
    void testDeleteClientBadRequest() {
        String type = "RUC";
        String id = "1722620489";
        when(this.clientService.deleteClient(type, id)).thenThrow(new RuntimeException());
        ResponseEntity<Client> response = this.clientController.clientDelete(type, id);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAddPhonesOk() {
        String type = "IDE";
        String id = "1722620489";
        List<ClientPhoneRQ> phonesRQ = new ArrayList<>();
        ClientPhoneRQ phoneRQ = ClientPhoneRQ.builder()
                .phoneNumber("0999744275").phoneType("OFF")
                .isDefault(true).build();
        phonesRQ.add(phoneRQ);
        when(this.clientService.addPhones(type, id, phonesRQ)).thenReturn(this.client);
        ResponseEntity<Client> response = this.clientController.addPhones(phonesRQ, type, id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.client, response.getBody());
    }

    @Test
    void testAddPhonesBadRequest() {
        String type = "RUC";
        String id = "1722620489";
        List<ClientPhoneRQ> phonesRQ = new ArrayList<>();
        ClientPhoneRQ phoneRQ = ClientPhoneRQ.builder()
                .phoneNumber("0999744275").phoneType("OFF")
                .isDefault(true).build();
        phonesRQ.add(phoneRQ);
        when(this.clientService.addPhones(type, id, phonesRQ)).thenThrow(new RuntimeException());
        ResponseEntity<Client> response = this.clientController.addPhones(phonesRQ, type, id);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    void testUpdatePhoneOk() {
        String type = "IDE";
        String id = "1722620489";
        String phone = "1234567890";
        ClientPhoneRQ phoneRQ = ClientPhoneRQ.builder()
                .phoneNumber("0999744275").phoneType("OFF")
                .isDefault(true).build();

        doNothing().when(this.clientService).updatePhone(type, id, phone, phoneRQ);
        ResponseEntity<String> response = this.clientController.updatePhone(phoneRQ, type, id, phone);
        assertEquals("Teléfono actualizado", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdatePhoneBadRequest() {
        String type = "IDE";
        String id = "1722620489";
        String phone = "1234567890";
        ClientPhoneRQ phoneRQ = ClientPhoneRQ.builder()
                .phoneNumber("0999744275").phoneType("OFF")
                .isDefault(true).build();
        doThrow(new RuntimeException()).when(this.clientService).updatePhone(type, id, phone, phoneRQ);
        ResponseEntity<String> response = this.clientController.updatePhone(phoneRQ, type, id, phone);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAddAddressesOk() {
        String type = "IDE";
        String id = "1722620489";
        List<ClientAddressRQ> addressesRQ = new ArrayList<>();
        ClientAddressRQ addressRQ = ClientAddressRQ.builder().typeAddress("HOM").isDefault(true)
                .latitude(Float.parseFloat("78.9890")).longitude(Float.parseFloat("-98.98"))
                .locationId("address1").line1("calle1").line2("calle2").build();
        addressesRQ.add(addressRQ);
        when(this.clientService.addAddresses(type, id, addressesRQ)).thenReturn(this.client);
        ResponseEntity<Client> response = this.clientController.addAddresses(addressesRQ, type, id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.client, response.getBody());
    }

    @Test
    void testAddAddressesBadRequest() {
        String type = "RUC";
        String id = "1722620489";
        List<ClientAddressRQ> addressesRQ = new ArrayList<>();
        ClientAddressRQ addressRQ = ClientAddressRQ.builder().typeAddress("HOM").isDefault(true)
                .latitude(Float.parseFloat("78.9890")).longitude(Float.parseFloat("-98.98"))
                .locationId("address1").line1("calle1").line2("calle2").build();
        addressesRQ.add(addressRQ);
        when(this.clientService.addAddresses(type, id, addressesRQ)).thenThrow(new RuntimeException());
        ResponseEntity<Client> response = this.clientController.addAddresses(addressesRQ, type, id);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateAddressOk() {
        String type = "IDE";
        String id = "1722620489";
        String line1 = "calle1";
        String line2 = "calle2";
        ClientAddressRQ addressRQ = ClientAddressRQ.builder().typeAddress("HOM").isDefault(true)
                .latitude(Float.parseFloat("78.9890")).longitude(Float.parseFloat("-98.98"))
                .locationId("address1").line1("calle1").line2("calle2").build();

        doNothing().when(this.clientService).updateAddress(type, id, line1, line2, addressRQ);
        ResponseEntity<String> response = this.clientController.updateAddress(addressRQ, type, id, line1, line2);
        assertEquals("Dirección actualizada", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateAddressBadRequest() {
        String type = "IDE";
        String id = "1722620489";
        String line1 = "calle1";
        String line2 = "calle2";
        ClientAddressRQ addressRQ = ClientAddressRQ.builder().typeAddress("HOM").isDefault(true)
                .latitude(Float.parseFloat("78.9890")).longitude(Float.parseFloat("-98.98"))
                .locationId("address1").line1("calle1").line2("calle2").build();

        doThrow(new RuntimeException()).when(this.clientService).updateAddress(type, id, line1, line2, addressRQ);
        ResponseEntity<String> response = this.clientController.updateAddress(addressRQ, type, id, line1, line2);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}

