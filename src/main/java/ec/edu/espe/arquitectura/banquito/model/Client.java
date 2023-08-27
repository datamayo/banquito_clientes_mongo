package ec.edu.espe.arquitectura.banquito.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Document(collection = "clients")
@CompoundIndex(name = "idx_clientes_typeDoc_DocId", def = "{'typeDocumentId':1, 'documentId':1 }", unique = true)
@Builder
public class Client {
    @Id
    private String id;
    private String branchId;// fk
    @Indexed(unique = true)
    private String uniqueKey;
    private String typeDocumentId;
    private String documentId;
    private String firstName;
    private String lastName;
    private String gender;
    private Date birthDate;
    private String emailAddress;
    private Date creationDate;
    private Date activationDate;
    private Date lastModifiedDate;
    private String role;
    private String state;
    private Date closedDate;
    private String comments;
    @Version
    private Long version;

    private List<ClientPhone> phoneNumbers;
    private List<ClientAddress> addresses;

}
