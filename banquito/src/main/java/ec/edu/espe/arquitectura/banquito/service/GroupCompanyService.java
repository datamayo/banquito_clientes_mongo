package ec.edu.espe.arquitectura.banquito.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.espe.arquitectura.banquito.dto.GroupCompanyMemberRQ;
import ec.edu.espe.arquitectura.banquito.dto.GroupCompanyMemberRS;
import ec.edu.espe.arquitectura.banquito.dto.GroupCompanyRQ;
import ec.edu.espe.arquitectura.banquito.dto.GroupCompanyRS;
import ec.edu.espe.arquitectura.banquito.model.Client;
import ec.edu.espe.arquitectura.banquito.model.GroupCompany;
import ec.edu.espe.arquitectura.banquito.model.GroupCompanyMember;
import ec.edu.espe.arquitectura.banquito.repository.ClientRepository;
import ec.edu.espe.arquitectura.banquito.repository.GroupCompanyRepository;

@Service
public class GroupCompanyService {
    private final GroupCompanyRepository groupCompanyRepository;
    private final ClientRepository clientRepository;

    public GroupCompanyService(GroupCompanyRepository groupCompanyRepository, ClientRepository clientRepository) {
        this.groupCompanyRepository = groupCompanyRepository;
        this.clientRepository = clientRepository;
    }

    public Boolean hasDuplicates(List<?> list) {
        Set<Object> set = new HashSet<>(list);
        return set.size() < list.size();
    }

    // Método para buscar una compañia
    public GroupCompanyRS obtainCompanyByGroupName(String groupName) {
        GroupCompany company = this.groupCompanyRepository.findFirstByGroupName(groupName);
        GroupCompanyRS companyTmp = this.transformCompanyRS(company);
        if (companyTmp == null) {
            throw new RuntimeException("Parametros de búsqueda incorrectos");
        } else {
            if ("INA".equals(companyTmp.getState())) {
                throw new RuntimeException("La organización ya no se encuentra disponible");
            }
            return companyTmp;
        }
    }

    // Método para agregar un cliente como miembro de una compañia
    @Transactional
    public GroupCompany addMember(String groupName, List<GroupCompanyMemberRQ> membersRQ) {
        GroupCompany companyTmp = this.groupCompanyRepository.findFirstByGroupName(groupName);
        if (companyTmp == null) {
            throw new RuntimeException("No existe la compañia");
        } else {
            List<GroupCompanyMember> newMembers = this.transformMembersRQ(membersRQ);
            List<GroupCompanyMember> members = companyTmp.getMembers();
            List<String> allClientIds = new ArrayList<>();
            if (members == null) {
                for (GroupCompanyMember member : newMembers) {
                    Client client = this.clientRepository.findFirstByUniqueKey(member.getClientId());
                    if (client == null) {
                        throw new RuntimeException("El o los clientes no existen");
                    } else {
                        member.setCreationDate(new Date());
                        member.setLastModifiedDate(new Date());
                        member.setState("ACT");
                        allClientIds.add(member.getClientId());
                    }
                }
                if (this.hasDuplicates(allClientIds)) {
                    throw new RuntimeException(
                            "Existen clientes que ya perteneces a la organización, volver a intentar");
                } else {
                    companyTmp.setMembers(newMembers);
                }

            } else {
                List<GroupCompanyMember> allMembers = new ArrayList<>();
                for (GroupCompanyMember member : newMembers) {
                    Client client = this.clientRepository.findFirstByUniqueKey(member.getClientId());
                    if (client == null) {
                        throw new RuntimeException("El o los clientes no existen");
                    } else {
                        member.setCreationDate(new Date());
                        member.setLastModifiedDate(new Date());
                        member.setState("ACT");
                        allMembers.add(member);
                        allClientIds.add(member.getClientId());
                    }
                }
                for (GroupCompanyMember member : members) {
                    allMembers.add(member);
                    allClientIds.add(member.getClientId());
                }
                if (this.hasDuplicates(allClientIds)) {
                    throw new RuntimeException(
                            "Existen clientes que ya perteneces a la organización, volver a intentar");
                } else {
                    companyTmp.setMembers(allMembers);
                }
            }
            return this.groupCompanyRepository.save(companyTmp);
        }
    }

    // Método para crear cliente juridico
    @Transactional
    public GroupCompany companyCreate(GroupCompanyRQ companyRQ) {
        GroupCompany company = this.transformCompanyRQ(companyRQ);
        GroupCompany companyTmp = this.groupCompanyRepository.findFirstByGroupName(company.getGroupName());
        if (companyTmp == null) {
            company.setUniqueKey(UUID.randomUUID().toString());
            company.setCreationDate(new Date());
            company.setLastModifiedDate(new Date());
            company.setActivationDate(new Date());
            company.setState("ACT");

            return this.groupCompanyRepository.save(company);
        } else {
            throw new RuntimeException("Compañia con ID " + company.getId() + " ya existe");
        }

    }

    // Método update cliente juridico
    @Transactional
    public GroupCompany updateCompany(GroupCompanyRQ companyRQ, String uniqueKey) {
        GroupCompany company = this.transformCompanyRQ(companyRQ);
        GroupCompany companyTmp = this.groupCompanyRepository.findFirstByUniqueKey(uniqueKey);
        if (companyTmp == null) {
            throw new RuntimeException("La compañia no existe");
        } else {
            companyTmp.setBranchId(company.getBranchId());
            companyTmp.setLocationId(company.getLocationId());
            companyTmp.setGroupName(company.getGroupName());
            companyTmp.setEmailAddress(company.getEmailAddress());
            companyTmp.setPhoneNumber(company.getPhoneNumber());
            companyTmp.setLine1(company.getLine1());
            companyTmp.setLine2(company.getLine2());
            companyTmp.setLatitude(company.getLatitude());
            companyTmp.setLongitude(company.getLongitude());
            companyTmp.setComments(company.getComments());
            companyTmp.setState(company.getState());
            companyTmp.setLastModifiedDate(new Date());
            if ("ACT".equals(company.getState())) {
                companyTmp.setActivationDate(new Date());
                companyTmp.setClosedDate(null);
            }

            return this.groupCompanyRepository.save(companyTmp);
        }
    }

    // Método delete cliente Juridico
    @Transactional
    public GroupCompany deleteCompany(String uniqueKey) {
        GroupCompany companyTmp = this.groupCompanyRepository.findFirstByUniqueKey(uniqueKey);
        if (companyTmp == null) {
            throw new RuntimeException("La compañia no existe");
        } else {
            companyTmp.setState("INA");
            companyTmp.setClosedDate(new Date());
            companyTmp.setLastModifiedDate(new Date());
            companyTmp.setActivationDate(null);
            return this.groupCompanyRepository.save(companyTmp);
        }
    }

    @Transactional
    public void updateMember(String companyId, String clientId, GroupCompanyMemberRQ companyRQ) {
        GroupCompany companyTmp = this.groupCompanyRepository.findFirstByUniqueKey(companyId);
        if (companyTmp == null) {
            throw new RuntimeException("La compañia no existe");
        } else {
            GroupCompanyMember memberUpdate = this.transformUpdateMemberRQ(companyRQ);
            List<GroupCompanyMember> members = companyTmp.getMembers();
            if (members == null) {
                throw new RuntimeException("No existen miembros en la empresa para modificar");
            } else {
                Boolean memberExists = false;
                for (GroupCompanyMember member : members) {
                    if (clientId.equals(member.getClientId())){ 
                        member.setState(memberUpdate.getState());
                        member.setGroupRole(memberUpdate.getGroupRole());
                        member.setLastModifiedDate(new Date());
                        memberExists= true;
                        break;
                    }

                }
                if (!memberExists) {
                    throw new RuntimeException("No existe el miembro con id " + clientId);
                }
            }
            this.groupCompanyRepository.save(companyTmp);
        }
    }

    // funciones gestión de grupos
    private List<GroupCompanyMember> transformMembersRQ(List<GroupCompanyMemberRQ> rq) {
        List<GroupCompanyMember> members = new ArrayList<>();
        for (GroupCompanyMemberRQ memberRQ : rq) {
            GroupCompanyMember member = GroupCompanyMember.builder().clientId(memberRQ.getClientId())
                    .groupRole(memberRQ.getGroupRole()).state(memberRQ.getState()).build();
            members.add(member);
        }
        return members;
    }

    private GroupCompanyRS transformCompanyRS(GroupCompany company) {
        List<GroupCompanyMemberRS> membersRS = this.transformMembersRS(company.getMembers());
        GroupCompanyRS rs = GroupCompanyRS.builder().branchId(company.getBranchId()).locationId(company.getLocationId())
                .uniqueKey(company.getUniqueKey()).groupName(company.getGroupName())
                .emailAddress(company.getEmailAddress())
                .phoneNumber(company.getPhoneNumber()).line1(company.getLine1()).line2(company.getLine2())
                .latitude(company.getLatitude())
                .longitude(company.getLongitude()).creationDate(company.getCreationDate())
                .activationDate(company.getActivationDate())
                .lastModifiedDate(company.getLastModifiedDate()).state(company.getState())
                .closedDate(company.getClosedDate()).comments(company.getComments())
                .members(membersRS).build();
        return rs;

    }

    private List<GroupCompanyMemberRS> transformMembersRS(List<GroupCompanyMember> members) {
        List<GroupCompanyMemberRS> membersRS = new ArrayList<>();
        if (members == null) {
            membersRS = null;
        } else {
            for (GroupCompanyMember member : members) {
                GroupCompanyMemberRS rs = GroupCompanyMemberRS.builder().clientId(member.getClientId())
                        .groupRole(member.getGroupRole())
                        .creationDate(member.getCreationDate()).lastModifiedDate(member.getLastModifiedDate())
                        .state(member.getState()).build();
                membersRS.add(rs);
            }
        }
        return membersRS;
    }

    private GroupCompany transformCompanyRQ(GroupCompanyRQ rq) {
        GroupCompany company = GroupCompany.builder().branchId(rq.getBranchId()).locationId(rq.getLocationId())
                .groupName(rq.getGroupName())
                .emailAddress(rq.getEmailAddress())
                .phoneNumber(rq.getPhoneNumber()).line1(rq.getLine1()).line2(rq.getLine2())
                .latitude(rq.getLatitude())
                .longitude(rq.getLongitude()).state(rq.getState()).comments(rq.getComments()).build();
        return company;
    }

    private GroupCompanyMember transformUpdateMemberRQ(GroupCompanyMemberRQ rq) {
        GroupCompanyMember member = GroupCompanyMember.builder().state(rq.getState()).groupRole(rq.getGroupRole())
                .build();
        return member;
    }

}
