package ec.edu.espe.arquitectura.banquito.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.espe.arquitectura.banquito.dto.ClientAddressRS;
import ec.edu.espe.arquitectura.banquito.dto.ClientPhoneRQ;
import ec.edu.espe.arquitectura.banquito.dto.ClientPhoneRS;
import ec.edu.espe.arquitectura.banquito.dto.ClientRQ;
import ec.edu.espe.arquitectura.banquito.dto.ClientRS;
import ec.edu.espe.arquitectura.banquito.model.Client;
import ec.edu.espe.arquitectura.banquito.model.ClientAddress;
import ec.edu.espe.arquitectura.banquito.model.ClientPhone;
import ec.edu.espe.arquitectura.banquito.model.GroupCompany;
import ec.edu.espe.arquitectura.banquito.model.GroupCompanyMember;
import ec.edu.espe.arquitectura.banquito.repository.ClientRepository;
import ec.edu.espe.arquitectura.banquito.repository.GroupCompanyRepository;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final GroupCompanyRepository groupCompanyRepository;

    public ClientService(ClientRepository clientRepository, GroupCompanyRepository groupCompanyRepository) {
        this.clientRepository = clientRepository;
        this.groupCompanyRepository = groupCompanyRepository;
    }

    public ClientRS obtainClientByDocumentTypeAndDocumentId(String documentType, String documentId) {
        Client client = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(documentType, documentId);
        ClientRS clientTmp = this.transformClientRS(client);
        if (clientTmp == null) {
            throw new RuntimeException("Parametros de búsqueda incorrectos");
        } else {
            return clientTmp;
        }
    }

    public Boolean hasDuplicates(List<?> list) {
        Set<Object> set = new HashSet<>(list);
        return set.size() < list.size();
    }

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

    @Transactional
    public Client addPhones(String typeDocument, String documentId, List<ClientPhoneRQ> phonesRQ) {
        Client clientTmp = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(typeDocument,
                documentId);
        if (clientTmp == null) {
            throw new RuntimeException("El cliente no existe");
        } else {
            List<ClientPhone> newPhones = this.transformClientPhoneRQ(phonesRQ);
            List<ClientPhone> phoneNumbers = clientTmp.getPhoneNumbers();
            List<String> allNumbers = new ArrayList<>();
            if (phoneNumbers == null) {
                for (ClientPhone clientPhone : newPhones) {
                    clientPhone.setState("ACT");
                    clientPhone.setIsDefault(false);
                    allNumbers.add(clientPhone.getPhoneNumber());
                }
                
                if (this.hasDuplicates(allNumbers)) {
                    throw new RuntimeException("Existen teléfonos repetidos, volver a intentar");
                } else {
                    newPhones.get(0).setIsDefault(true);
                    clientTmp.setPhoneNumbers(newPhones);
                }

            } else {
                List<ClientPhone> phones = new ArrayList<>();
                for (ClientPhone clientPhone : newPhones) {
                    clientPhone.setState("ACT");
                    clientPhone.setIsDefault(false);// revisar el isDefault
                    phones.add(clientPhone);
                    allNumbers.add(clientPhone.getPhoneNumber());
                }
                for (ClientPhone clientPhone : phoneNumbers) {
                    phones.add(clientPhone);
                    allNumbers.add(clientPhone.getPhoneNumber());
                }
                if(this.hasDuplicates(allNumbers)){
                    throw new RuntimeException("Existen teléfonos repetidos, volver a intentar");
                }else{
                    clientTmp.setPhoneNumbers(phones);
                }
            }
            return this.clientRepository.save(clientTmp);
        }
    }
/* 
    @Transactional
    public Client updatePhone(String typeDocument, String documentId, ClientPhoneRQ phoneRQ){
         Client clientTmp = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(typeDocument,
                documentId);
        if(clientTmp == null){
            throw new RuntimeException("El cliente no existe");
        }else{

        }
    }
*/
    private Client transformClientRQ(ClientRQ rq) {
        Client client = Client.builder().branchId(rq.getBranchId()).typeDocumentId(rq.getTypeDocumentId())
                .documentId(rq.getDocumentId()).firstName(rq.getFirstName()).lastName(rq.getLastName())
                .gender(rq.getGender()).birthDate(rq.getBirthDate()).emailAddress(rq.getEmailAddress())
                .role(rq.getRole()).comments(rq.getComments()).state(rq.getState()).build();
        return client;

    }

    private List<ClientPhone> transformClientPhoneRQ(List<ClientPhoneRQ> rq) {
        List<ClientPhone> clientPhones = new ArrayList<>();
        for (ClientPhoneRQ clientPhoneRQ : rq) {
            ClientPhone clientPhone = ClientPhone.builder().phoneNumber(clientPhoneRQ.getPhoneNumber())
                    .phoneType(clientPhoneRQ.getPhoneType()).build();
            clientPhones.add(clientPhone);
        }
        return clientPhones;
    }

    private List<ClientPhoneRS> transformPhoneRS(List<ClientPhone> phoneNumbers) {
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

    private List<ClientAddressRS> transformAddressRS(List<ClientAddress> addresses) {
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

    private ClientRS transformClientRS(Client client) {
        List<ClientPhoneRS> phoneNumbersRS = this.transformPhoneRS(client.getPhoneNumbers());
        List<ClientAddressRS> addressesRS = this.transformAddressRS(client.getAddresses());
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

}
// Gestión de Clientes Persona
/*
 * public Client listByDocumentTypeAndDocumentId(String documentType, String
 * documentId) {
 * Client clientTmp =
 * this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(documentType,
 * documentId);
 * if (clientTmp == null) {
 * throw new RuntimeException("Parametros de búsqueda incorrectos");
 * } else {
 * return clientTmp;
 * }
 * 
 * }
 * 
 * @Transactional
 * public Client clientCreate(Client client) {
 * Client clientTmp =
 * this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(client.
 * getTypeDocumentId(),
 * client.getDocumentId());
 * if (clientTmp == null) {
 * if (client.getBirthDate().after(new Date())) {
 * throw new RuntimeException("La fecha de nacimiento es incorrecta");
 * }
 * client.setCreationDate(new Date());
 * client.setLastModifiedDate(new Date());
 * client.setActivationDate(new Date());
 * client.setState("ACT");
 * List<ClientAddress> addresses = client.getAddresses();
 * Iterator<ClientAddress> addressIterator = addresses.iterator();
 * while(addressIterator.hasNext()){
 * addressIterator.next().setState("ACT");
 * }
 * List<ClientPhone> phones = client.getPhoneNumbers();
 * Iterator<ClientPhone> phonesIterator = phones.iterator();
 * while(phonesIterator.hasNext()){
 * phonesIterator.next().setState("ACT");
 * }
 * List<GroupCompanyMember> groups = client.getGroupCompanyMember();
 * if(groups != null ){
 * for(GroupCompanyMember groupsIterator : groups){
 * groupsIterator.setCreationDate(new Date());
 * groupsIterator.setLastModifiedDate(new Date());
 * groupsIterator.setState("ACT");
 * }
 * }
 * 
 * return this.clientRepository.save(client);
 * } else {
 * throw new RuntimeException("Cliente con ID " + client.getId() +
 * " ya existe");
 * }
 * 
 * }
 */