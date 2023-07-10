package ec.edu.espe.arquitectura.banquito.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientPhone {
    private String phoneType;
    private String phoneNumber;
    private Boolean isDefault;
    private String state;
}
