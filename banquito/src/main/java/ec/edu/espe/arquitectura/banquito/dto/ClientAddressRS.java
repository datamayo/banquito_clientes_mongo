package ec.edu.espe.arquitectura.banquito.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientAddressRS {
    private String locationId;//fk
    private String typeAddress;
    private String line1;
    private String line2;
    private Float latitude;
    private Float longitude;
    private Boolean isDefault;
    private String state;
    
}
