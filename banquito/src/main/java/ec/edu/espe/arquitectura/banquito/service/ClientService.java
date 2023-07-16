package ec.edu.espe.arquitectura.banquito.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.espe.arquitectura.banquito.dto.ClientAddressRQ;
import ec.edu.espe.arquitectura.banquito.dto.ClientAddressRS;
import ec.edu.espe.arquitectura.banquito.dto.ClientPhoneRQ;
import ec.edu.espe.arquitectura.banquito.dto.ClientPhoneRS;
import ec.edu.espe.arquitectura.banquito.dto.ClientRQ;
import ec.edu.espe.arquitectura.banquito.dto.ClientRS;
import ec.edu.espe.arquitectura.banquito.model.Client;
import ec.edu.espe.arquitectura.banquito.model.ClientAddress;
import ec.edu.espe.arquitectura.banquito.model.ClientPhone;
import ec.edu.espe.arquitectura.banquito.repository.ClientRepository;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Boolean hasDuplicates(List<?> list) {
        Set<Object> set = new HashSet<>(list);
        return set.size() < list.size();
    }

    // Métodos Get para Clientes
    public ClientRS obtainClientByDocumentTypeAndDocumentId(String documentType, String documentId) {
        Client client = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(documentType, documentId);
        ClientRS clientTmp = this.transformClientRS(client);
        if (clientTmp == null) {
            throw new RuntimeException("Parametros de búsqueda incorrectos");
        } else {
            if ("INA".equals(clientTmp.getState())) {
                throw new RuntimeException("El cliente ya no se encuentra disponible");
            }
            return clientTmp;
        }
    }

    // Método create para clientes
    @Transactional
    public Client clientCreate(ClientRQ clientRQ) {
        Client client = this.transformClientRQ(clientRQ);
        Client clientTmp = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(client.getTypeDocumentId(),
                client.getDocumentId());
        if (clientTmp == null) {
            if (client.getBirthDate().after(new Date())) {
                throw new RuntimeException("La fecha de nacimiento es incorrecta");
            }
            client.setUniqueKey(UUID.randomUUID().toString());
            client.setCreationDate(new Date());
            client.setLastModifiedDate(new Date());
            client.setActivationDate(new Date());
            client.setState("ACT");

            return this.clientRepository.save(client);
        } else {
            throw new RuntimeException("Cliente con ID " + client.getId() + " ya existe");
        }

    }

    // Método update clientes
    @Transactional
    public Client updateClient(ClientRQ clientRQ, String typeDocument, String documentId) {
        Client client = this.transformClientRQ(clientRQ);
        Client clientTmp = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(typeDocument,
                documentId);
        if (clientTmp == null) {
            throw new RuntimeException("El cliente no existe");
        } else {
            clientTmp.setBranchId(client.getBranchId());
            clientTmp.setFirstName(client.getFirstName());
            clientTmp.setLastName(client.getLastName());
            clientTmp.setGender(client.getGender());
            clientTmp.setEmailAddress(client.getEmailAddress());
            clientTmp.setRole(client.getRole());
            clientTmp.setComments(client.getComments());
            clientTmp.setLastModifiedDate(new Date());
            clientTmp.setState(client.getState());
            if ("ACT".equals(client.getState())) {
                clientTmp.setActivationDate(new Date());
                clientTmp.setClosedDate(null);
            }

            return this.clientRepository.save(clientTmp);
        }
    }

    // Método delete para clientes
    @Transactional
    public Client deleteClient(String typeDocument, String documentId) {
        Client clientTmp = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(typeDocument,
                documentId);
        if (clientTmp == null) {
            throw new RuntimeException("El cliente no existe");
        } else {
            clientTmp.setState("INA");
            clientTmp.setClosedDate(new Date());
            clientTmp.setLastModifiedDate(new Date());
            clientTmp.setActivationDate(null);
            return this.clientRepository.save(clientTmp);
        }
    }

    // Método para agreagr números telefónicos a un cliente
    @Transactional
    public Client addPhones(String typeDocument, String documentId, List<ClientPhoneRQ> phonesRQ) {
        Client clientTmp = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(typeDocument,
                documentId);
        if (clientTmp == null) {
            throw new RuntimeException("El cliente no existe");
        } else {
            List<ClientPhone> newPhones = this.transformClientPhonesRQ(phonesRQ);
            List<ClientPhone> phoneNumbers = clientTmp.getPhoneNumbers();
            List<String> allNumbers = new ArrayList<>();
            if (phoneNumbers == null) {
                for (ClientPhone clientPhone : newPhones) {
                    clientPhone.setState("ACT");
                    allNumbers.add(clientPhone.getPhoneNumber());

                }

                if (this.hasDuplicates(allNumbers)) {
                    throw new RuntimeException("Existen teléfonos repetidos, volver a intentar");
                } else {
                    clientTmp.setPhoneNumbers(newPhones);
                }

            } else {
                List<ClientPhone> phones = new ArrayList<>();
                for (ClientPhone clientPhone : newPhones) {
                    clientPhone.setState("ACT");
                    phones.add(clientPhone);
                    allNumbers.add(clientPhone.getPhoneNumber());
                }
                for (ClientPhone clientPhone : phoneNumbers) {
                    phones.add(clientPhone);
                    allNumbers.add(clientPhone.getPhoneNumber());
                }
                if (this.hasDuplicates(allNumbers)) {
                    throw new RuntimeException("Existen teléfonos repetidos, volver a intentar");
                } else {
                    clientTmp.setPhoneNumbers(phones);
                }
            }
            return this.clientRepository.save(clientTmp);
        }
    }

    // Método para modificar/borrar un teléfono de un cliente
    @Transactional
    public void updatePhone(String typeDocument, String documentId, String phoneNumber, ClientPhoneRQ phoneRQ) {
        Client clientTmp = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(typeDocument,
                documentId);
        if (clientTmp == null) {
            throw new RuntimeException("El cliente no existe");
        } else {
            ClientPhone phoneUpdate = this.transformUpdatePhoneRQ(phoneRQ);
            List<ClientPhone> phoneNumbers = clientTmp.getPhoneNumbers();
            if (phoneNumbers == null) {
                throw new RuntimeException("No existen números telefónicos para modificar");
            } else {
                Boolean phoneNumberExists = false;
                for (ClientPhone clientPhone : phoneNumbers) {
                    if (phoneNumber.equals(clientPhone.getPhoneNumber())) {
                        clientPhone.setIsDefault(phoneUpdate.getIsDefault());
                        clientPhone.setState(phoneUpdate.getState());
                        phoneNumberExists = true;
                        break;
                    }

                }
                if (!phoneNumberExists) {
                    throw new RuntimeException("No existe el número de teléfono " + phoneNumber);
                }
            }
            this.clientRepository.save(clientTmp);
        }
    }

    // Método para agregar direcciones
    @Transactional
    public Client addAddresses(String typeDocument, String documentId, List<ClientAddressRQ> addressesRQ) {
        Client clientTmp = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(typeDocument,
                documentId);
        if (clientTmp == null) {
            throw new RuntimeException("El cliente no existe");
        } else {
            List<ClientAddress> newAddresses = this.transformClientAddressesRQ(addressesRQ);
            List<ClientAddress> addresses = clientTmp.getAddresses();
            List<String> allLine1 = new ArrayList<>();
            List<String> allLine2 = new ArrayList<>();
            if (addresses == null) {
                for (ClientAddress clientAddress : newAddresses) {
                    clientAddress.setState("ACT");
                    allLine1.add(clientAddress.getLine1());
                    allLine2.add(clientAddress.getLine2());
                }

                if (this.hasDuplicates(allLine1) == true && this.hasDuplicates(allLine2) == true) {
                    throw new RuntimeException("Existen direcciones repetidas, volver a intentar");
                } else {
                    clientTmp.setAddresses(newAddresses);
                }

            } else {
                List<ClientAddress> allAddresses = new ArrayList<>();
                for (ClientAddress clientAddress : newAddresses) {
                    clientAddress.setState("ACT");
                    allAddresses.add(clientAddress);
                    allLine1.add(clientAddress.getLine1());
                    allLine2.add(clientAddress.getLine2());
                }
                for (ClientAddress clientAddress : addresses) {
                    allAddresses.add(clientAddress);
                    allLine1.add(clientAddress.getLine1());
                    allLine2.add(clientAddress.getLine2());
                }
                if (this.hasDuplicates(allLine1) == true && this.hasDuplicates(allLine2)) {
                    throw new RuntimeException("Existen direcciones repetidas, volver a intentar");
                } else {
                    clientTmp.setAddresses(allAddresses);
                }
            }
            return this.clientRepository.save(clientTmp);
        }
    }

    // Método para modificar/borrar una dirección de un cliente
    @Transactional
    public void updateAddress(String typeDocument, String documentId, String line1, String line2,
            ClientAddressRQ addressRQ) {
        Client clientTmp = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(typeDocument,
                documentId);
        if (clientTmp == null) {
            throw new RuntimeException("El cliente no existe");
        } else {
            ClientAddress addressUpdate = this.transformUpdateAddressRQ(addressRQ);
            List<ClientAddress> addresses = clientTmp.getAddresses();
            if (addresses == null) {
                throw new RuntimeException("No existen direcciones para modificar");
            } else {
                Boolean addressExists = false;
                for (ClientAddress clientAddress : addresses) {
                    if (clientAddress.getLine1().equals(line1) && clientAddress.getLine2().equals(line2)) {
                        clientAddress.setTypeAddress(addressUpdate.getTypeAddress());
                        clientAddress.setIsDefault(addressUpdate.getIsDefault());
                        clientAddress.setState(addressUpdate.getState());
                        addressExists = true;
                        break;
                    }
                }
                if (!addressExists) {
                    throw new RuntimeException("No existe la dirección " + line1 + " " + line2);
                }

            }
            this.clientRepository.save(clientTmp);
        }
    }

    // funciones para la gestión de clientes
    private Client transformClientRQ(ClientRQ rq) {
        Client client = Client.builder().branchId(rq.getBranchId()).typeDocumentId(rq.getTypeDocumentId())
                .documentId(rq.getDocumentId()).firstName(rq.getFirstName()).lastName(rq.getLastName())
                .gender(rq.getGender()).birthDate(rq.getBirthDate()).emailAddress(rq.getEmailAddress())
                .role(rq.getRole()).comments(rq.getComments()).state(rq.getState()).build();
        return client;

    }

    private List<ClientPhone> transformClientPhonesRQ(List<ClientPhoneRQ> rq) {
        List<ClientPhone> clientPhones = new ArrayList<>();
        for (ClientPhoneRQ clientPhoneRQ : rq) {
            ClientPhone clientPhone = ClientPhone.builder().phoneNumber(clientPhoneRQ.getPhoneNumber())
                    .phoneType(clientPhoneRQ.getPhoneType()).state(clientPhoneRQ.getState())
                    .isDefault(clientPhoneRQ.getIsDefault()).build();
            clientPhones.add(clientPhone);
        }
        return clientPhones;
    }

    private ClientRS transformClientRS(Client client) {
        List<ClientPhoneRS> phoneNumbersRS = this.transformPhonesRS(client.getPhoneNumbers());
        List<ClientAddressRS> addressesRS = this.transformAddressesRS(client.getAddresses());
        ClientRS rs = ClientRS.builder().branchId(client.getBranchId())
                .uniqueKey(client.getUniqueKey()).typeDocumentId(client.getTypeDocumentId())
                .documentId(client.getDocumentId()).firstName(client.getFirstName())
                .lastName(client.getLastName()).gender(client.getGender()).birthDate(client.getBirthDate())
                .emailAddress(client.getEmailAddress())
                .creationDate(client.getCreationDate()).activationDate(client.getActivationDate())
                .lastModifiedDate(client.getLastModifiedDate()).role(client.getRole())
                .state(client.getState()).closedDate(client.getClosedDate()).comments(client.getComments())
                .phoneNumbers(phoneNumbersRS).addresses(addressesRS).build();
        return rs;

    }

    // funciones para la gestión de direcciones
    private List<ClientAddress> transformClientAddressesRQ(List<ClientAddressRQ> rq) {
        List<ClientAddress> clientAddresses = new ArrayList<>();
        for (ClientAddressRQ clientAddressRQ : rq) {
            ClientAddress clientAddress = ClientAddress.builder().locationId(clientAddressRQ.getLocationId())
                    .typeAddress(clientAddressRQ.getTypeAddress())
                    .line1(clientAddressRQ.getLine1()).line2(clientAddressRQ.getLine2())
                    .latitude(clientAddressRQ.getLatitude()).longitude(clientAddressRQ.getLongitude())
                    .isDefault(clientAddressRQ.getIsDefault()).state(clientAddressRQ.getState()).build();
            clientAddresses.add(clientAddress);
        }
        return clientAddresses;
    }

    private ClientAddress transformUpdateAddressRQ(ClientAddressRQ rq) {
        ClientAddress clientAddress = ClientAddress.builder().state(rq.getState()).isDefault(rq.getIsDefault())
                .typeAddress(rq.getTypeAddress()).build();
        return clientAddress;
    }

    private List<ClientAddressRS> transformAddressesRS(List<ClientAddress> addresses) {
        List<ClientAddressRS> clientAddressRS = new ArrayList<>();
        if (addresses == null) {
            clientAddressRS = null;
        } else {
            for (ClientAddress clientAddress : addresses) {
                ClientAddressRS rs = ClientAddressRS.builder().locationId(clientAddress.getLocationId())
                        .typeAddress(clientAddress.getTypeAddress()).line1(clientAddress.getLine1())
                        .line2(clientAddress.getLine2()).latitude(clientAddress.getLatitude())
                        .longitude(clientAddress.getLongitude()).isDefault(clientAddress.getIsDefault())
                        .state(clientAddress.getState()).build();
                clientAddressRS.add(rs);
            }
        }
        return clientAddressRS;
    }

    // funciones para la gestió de teléfonos
    private ClientPhone transformUpdatePhoneRQ(ClientPhoneRQ rq) {
        ClientPhone clientPhone = ClientPhone.builder().state(rq.getState()).isDefault(rq.getIsDefault()).build();
        return clientPhone;
    }

    private List<ClientPhoneRS> transformPhonesRS(List<ClientPhone> phoneNumbers) {
        List<ClientPhoneRS> clientPhoneRS = new ArrayList<>();
        if (phoneNumbers == null) {
            clientPhoneRS = null;
        } else {
            for (ClientPhone clientPhone : phoneNumbers) {
                ClientPhoneRS rs = ClientPhoneRS.builder().phoneNumber(clientPhone.getPhoneNumber())
                        .phoneType(clientPhone.getPhoneType())
                        .isDefault(clientPhone.getIsDefault()).state(clientPhone.getState()).build();
                clientPhoneRS.add(rs);
            }
        }
        return clientPhoneRS;
    }
}
