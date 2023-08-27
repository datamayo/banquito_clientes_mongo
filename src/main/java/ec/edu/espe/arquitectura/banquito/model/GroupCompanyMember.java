package ec.edu.espe.arquitectura.banquito.model;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupCompanyMember {
    private String groupRole;
    private String clientId;
    private String state;
    private Date creationDate;
    private Date lastModifiedDate;
}
