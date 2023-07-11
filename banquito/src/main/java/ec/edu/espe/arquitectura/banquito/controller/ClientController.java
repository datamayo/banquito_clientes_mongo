package ec.edu.espe.arquitectura.banquito.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Client> obtainByDocumentTypeAndDocumentId(
            @PathVariable(name = "documentType") String documentType,
            @PathVariable(name = "documentId") String documentId) {
        Client client = this.clientService.listByDocumentTypeAndDocumentId(documentType, documentId);
        return ResponseEntity.ok(client);

    }

    @PostMapping
    public ResponseEntity<Client> clientCreate(@RequestBody Client client) {
        try {
            Client clientRS = this.clientService.clientCreate(client);
            return ResponseEntity.ok(clientRS);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();

        }
    }

}