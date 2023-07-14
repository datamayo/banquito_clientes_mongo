package ec.edu.espe.arquitectura.banquito.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.espe.arquitectura.banquito.dto.ClientPhoneRQ;
import ec.edu.espe.arquitectura.banquito.dto.ClientRQ;
import ec.edu.espe.arquitectura.banquito.dto.ClientRS;
import ec.edu.espe.arquitectura.banquito.model.Client;
import ec.edu.espe.arquitectura.banquito.service.ClientService;

@RestController
@RequestMapping("/api/v2/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/{documentType}/{documentId}")
    public ResponseEntity<ClientRS> obtainByDocumentTypeAndDocumentId(
            @PathVariable(name = "documentType") String documentType,
            @PathVariable(name = "documentId") String documentId) {
        ClientRS client = this.clientService.obtainClientByDocumentTypeAndDocumentId(documentType, documentId);
        return ResponseEntity.ok(client);

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
    /*
     * @PutMapping("/phones/{typeDocument}/{documentId}")
     * public ResponseEntity<Client> clientAddPhones(@PathVariable(name =
     * "typeDocument") String typeDocument,
     * 
     * @PathVariable(name = "documentId") String documentId,
     * 
     * @RequestBody ClientRQ client) {
     * try {
     * // List<ClientPhoneRQ> clientPhoneRQ = this.clientService.
     * Client clientRS = this.clientService.createPhoneClient(typeDocument,
     * documentId, client);
     * return ResponseEntity.ok(clientRS);
     * } catch (RuntimeException rte) {
     * return ResponseEntity.badRequest().build();
     * }
     * }
     */

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
}
