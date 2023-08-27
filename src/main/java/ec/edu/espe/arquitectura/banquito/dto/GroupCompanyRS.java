package ec.edu.espe.arquitectura.banquito.dto;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupCompanyRS {
    private String branchId;
    private String locationId;
    private String uniqueKey;
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
    private List<GroupCompanyMemberRS> members;
}
