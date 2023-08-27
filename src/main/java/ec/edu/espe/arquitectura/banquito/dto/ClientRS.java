package ec.edu.espe.arquitectura.banquito.dto;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientRS {
    private String branchId;// fk
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
    private String password;
    private List<ClientPhoneRS> phoneNumbers;
    private List<ClientAddressRS> addresses;
    //private List<GroupCompanyMember> groupCompanyMember;
    
}
