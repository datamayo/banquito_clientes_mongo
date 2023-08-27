package ec.edu.espe.arquitectura.banquito.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientPhone {
    private String phoneType;
    private String phoneNumber;
    private Boolean isDefault;
    private String state;
}
