package ec.edu.espe.arquitectura.banquito.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import ec.edu.espe.arquitectura.banquito.model.Client;

public interface ClientRepository extends MongoRepository<Client, String> {
    Client findFirstByTypeDocumentIdAndDocumentId(String typeDocumentId, String documentId);

    List<Client> findByLastNameLike(String lastName);

    Client findFirstByUniqueKey(String uniqueKey);

}
