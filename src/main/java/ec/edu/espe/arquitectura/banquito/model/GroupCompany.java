package ec.edu.espe.arquitectura.banquito.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Document(collection = "companies")
@Builder
public class GroupCompany {
    @Id
    private String id;
    private String branchId;
    private String locationId;
    @Indexed(unique = true)
    private String uniqueKey;
    @Indexed(unique = true)
    private String groupName;
    private String emailAddress;
    private String phoneNumber;
    private String line1;
    private String line2;
    private Float latitude;
    private Float longitude;
    private Date creationDate;
    private Date activationDate;
    private Date lastModifiedDate;
    private String state;
    private Date closedDate;
    private String comments;
    @Version
    private Long version;

    private List<GroupCompanyMember> members;


}
