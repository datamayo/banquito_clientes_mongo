package ec.edu.espe.arquitectura.banquito.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientPhoneRQ implements Serializable{
    private String phoneType;
    private String phoneNumber;
}
