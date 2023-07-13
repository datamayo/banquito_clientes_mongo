package ec.edu.espe.arquitectura.banquito.model;

import java.util.Date;

import lombok.Data;

@Data
public class GroupCompanyMember {
    private GroupRole groupRole; //fk
    private GroupCompany groupCompany;//fk
    private String state;
    private Date creationDate;
    private Date lastModifiedDate;
    public GroupCompanyMember() {
    }
    
    
}
