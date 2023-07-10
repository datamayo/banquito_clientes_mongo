package ec.edu.espe.arquitectura.banquito.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.espe.arquitectura.banquito.model.Client;
import ec.edu.espe.arquitectura.banquito.model.ClientAddress;
import ec.edu.espe.arquitectura.banquito.model.ClientPhone;
import ec.edu.espe.arquitectura.banquito.model.GroupCompanyMember;
import ec.edu.espe.arquitectura.banquito.repository.ClientRepository;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Gestión de Clientes Persona
    public Client listByDocumentTypeAndDocumentId(String documentType, String documentId) {
        Client clientTmp = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(documentType, documentId);
        if (clientTmp == null) {
            throw new RuntimeException("Parametros de búsqueda incorrectos");
        } else {
            return clientTmp;
        }

    }

    @Transactional
    public Client clientCreate(Client client) {
        Client clientTmp = this.clientRepository.findFirstByTypeDocumentIdAndDocumentId(client.getTypeDocumentId(),
                client.getDocumentId());
        if (clientTmp == null) {
            if (client.getBirthDate().after(new Date())) {
                throw new RuntimeException("La fecha de nacimiento es incorrecta");
            }
            client.setCreationDate(new Date());
            client.setLastModifiedDate(new Date());
            client.setActivationDate(new Date());
            client.setState("ACT");
            List<ClientAddress> addresses = client.getAddresses();
            Iterator<ClientAddress> addressIterator = addresses.iterator();
            while(addressIterator.hasNext()){
                addressIterator.next().setState("ACT");
            }
            List<ClientPhone> phones = client.getPhoneNumbers();
            Iterator<ClientPhone> phonesIterator = phones.iterator();
            while(phonesIterator.hasNext()){
                phonesIterator.next().setState("ACT");
            }
            List<GroupCompanyMember> groups = client.getGroupCompanyMember();
            if(!groups.isEmpty()){
                //Iterator<GroupCompanyMember> groupsIterator = groups.iterator();
                for(GroupCompanyMember groupsIterator : groups){
                    groupsIterator.setCreationDate(new Date());
                    groupsIterator.setLastModifiedDate(new Date());
                    groupsIterator.setState("ACT");
                }
                /* 
                while(groupsIterator.hasNext()){
                    groupsIterator.next().setCreationDate(new Date());
                    groupsIterator.next().setLastModifiedDate(new Date());
                    groupsIterator.next().setState("ACT");
                }
                */
            }

            return this.clientRepository.save(client);
        } else {
            throw new RuntimeException("Cliente con ID " + client.getId() + " ya existe");
        }

    }
    
}
