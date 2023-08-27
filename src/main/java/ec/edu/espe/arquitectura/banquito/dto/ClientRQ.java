package ec.edu.espe.arquitectura.banquito.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientRQ {
    private String branchId;//fk
    private String typeDocumentId;
    private String documentId;
    private String firstName;
    private String lastName;
    private String gender;
    private Date birthDate;
    private String emailAddress;
    private String role;
    private String comments;
    private String state;
    private String password;
}
