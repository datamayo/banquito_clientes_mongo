package ec.edu.espe.arquitectura.banquito.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupCompanyMemberRS {
    private String groupRole;
    private String clientId;
    private String state;
    private Date creationDate;
    private Date lastModifiedDate;
}
