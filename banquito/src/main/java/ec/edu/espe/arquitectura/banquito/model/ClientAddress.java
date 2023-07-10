package ec.edu.espe.arquitectura.banquito.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientAddress {
    private String locationId;//fk
    private String typeAddress;
    private String line1;
    private String line2;
    private Float latitude;
    private Float longitude;
    private Boolean isDefault;
    private String state;
}
