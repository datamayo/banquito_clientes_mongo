package ec.edu.espe.arquitectura.banquito.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Document(collection = "clients")
@CompoundIndex(name = "idx_clientes_typeDoc_DocId", def = "{'typeDocumentId':1, 'documentId':1 }", unique = true)
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
    private List<GroupCompanyMember> groupCompanyMember;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uniqueKey == null) ? 0 : uniqueKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Client other = (Client) obj;
        if (uniqueKey == null) {
            if (other.uniqueKey != null)
                return false;
        } else if (!uniqueKey.equals(other.uniqueKey))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Client [id=" + id + ", branchId=" + branchId + ", uniqueKey=" + uniqueKey + ", typeDocumentId="
                + typeDocumentId + ", documentId=" + documentId + ", firstName=" + firstName + ", lastName=" + lastName
                + ", gender=" + gender + ", birthDate=" + birthDate + ", emailAddress=" + emailAddress
                + ", creationDate=" + creationDate + ", activationDate=" + activationDate + ", lastModifiedDate="
                + lastModifiedDate + ", role=" + role + ", state=" + state + ", closedDate=" + closedDate
                + ", comments=" + comments + ", version=" + version + ", phoneNumbers=" + phoneNumbers + ", addresses="
                + addresses + ", groupCompanyMember=" + groupCompanyMember + "]";
    }

}
