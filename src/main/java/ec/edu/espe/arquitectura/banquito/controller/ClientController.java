package ec.edu.espe.arquitectura.banquito.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ec.edu.espe.arquitectura.banquito.dto.ClientAddressRQ;
import ec.edu.espe.arquitectura.banquito.dto.ClientPhoneRQ;
import ec.edu.espe.arquitectura.banquito.dto.ClientRQ;
import ec.edu.espe.arquitectura.banquito.dto.ClientRS;
import ec.edu.espe.arquitectura.banquito.model.Client;
import ec.edu.espe.arquitectura.banquito.service.ClientService;

@RestController
@RequestMapping("/api/v2/clients")
@CrossOrigin
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/{documentType}/{documentId}")
    public ResponseEntity<ClientRS> obtainByDocumentTypeAndDocumentId(
            @PathVariable(name = "documentType") String documentType,
            @PathVariable(name = "documentId") String documentId) {
        try {
            ClientRS client = this.clientService.obtainClientByDocumentTypeAndDocumentId(documentType, documentId);
            return ResponseEntity.ok(client);
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{uniqueKey}")
    public ResponseEntity<ClientRS> obtainById(
            @PathVariable(name = "uniqueKey") String uniqueKey) {
        try {
            ClientRS client = this.clientService.obtainClientById(uniqueKey);
            return ResponseEntity.ok(client);
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/login/{uniqueKey}")
    public ResponseEntity<ClientRS> obtainLogin(
            @PathVariable(name = "uniqueKey") String uniqueKey) {
        try {
            ClientRS client = this.clientService.obtainLogin(uniqueKey);
            return ResponseEntity.ok(client);
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Client> clientCreate(@RequestBody ClientRQ client) {
        try {
            Client clientRS = this.clientService.clientCreate(client);
            return ResponseEntity.ok(clientRS);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();

        }
    }

    @PutMapping("/updateClient/{typeDocument}/{documentId}")
    public ResponseEntity<Client> clientUpdate(@RequestBody ClientRQ client,
            @PathVariable(name = "typeDocument") String typeDocument,
            @PathVariable(name = "documentId") String documentId) {
        try {
            Client clientRS = this.clientService.updateClient(client, typeDocument, documentId);
            return ResponseEntity.ok(clientRS);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();

        }
    }

    @PutMapping("/deleteClient/{typeDocument}/{documentId}")
    public ResponseEntity<Client> clientDelete(@PathVariable(name = "typeDocument") String typeDocument,
            @PathVariable(name = "documentId") String documentId) {
        try {
            Client clientRS = this.clientService.deleteClient(typeDocument, documentId);
            return ResponseEntity.ok(clientRS);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();

        }
    }

    @PutMapping("/phones/{typeDocument}/{documentId}")
    public ResponseEntity<Client> addPhones(@RequestBody List<ClientPhoneRQ> phonesRQ,
            @PathVariable(name = "typeDocument") String typeDocument,
            @PathVariable(name = "documentId") String documentId) {
        try {
            Client clientRS = this.clientService.addPhones(typeDocument, documentId, phonesRQ);
            return ResponseEntity.ok(clientRS);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/updatePhone/{typeDocument}/{documentId}/{phoneNumber}")
    public ResponseEntity<String> updatePhone(@RequestBody ClientPhoneRQ phoneRQ,
            @PathVariable(name = "typeDocument") String typeDocument,
            @PathVariable(name = "documentId") String documentId,
            @PathVariable(name = "phoneNumber") String phoneNumber) {
        try {
            this.clientService.updatePhone(typeDocument, documentId, phoneNumber, phoneRQ);
            return ResponseEntity.ok("Teléfono actualizado");
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/addresses/{typeDocument}/{documentId}")
    public ResponseEntity<Client> addAddresses(@RequestBody List<ClientAddressRQ> addressesRQ,
            @PathVariable(name = "typeDocument") String typeDocument,
            @PathVariable(name = "documentId") String documentId) {
        try {
            Client clientRS = this.clientService.addAddresses(typeDocument, documentId, addressesRQ);
            return ResponseEntity.ok(clientRS);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

     @PutMapping("/updateAddress/{typeDocument}/{documentId}/{line1}/{line2}")
    public ResponseEntity<String> updateAddress(@RequestBody ClientAddressRQ addressRQ,
            @PathVariable(name = "typeDocument") String typeDocument,
            @PathVariable(name = "documentId") String documentId,
            @PathVariable(name = "line1") String line1, @PathVariable(name = "line2") String line2) {
        try {
            this.clientService.updateAddress(typeDocument, documentId, line1, line2, addressRQ);
            return ResponseEntity.ok("Dirección actualizada");
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

   
    
}
