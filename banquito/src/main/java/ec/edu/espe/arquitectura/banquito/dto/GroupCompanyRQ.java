package ec.edu.espe.arquitectura.banquito.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupCompanyRQ {
    private String branchId;
    private String locationId;
    private String groupName;
    private String emailAddress;
    private String phoneNumber;
    private String line1;
    private String line2;
    private Float latitude;
    private Float longitude;
    private String state;
    private String comments;
}
