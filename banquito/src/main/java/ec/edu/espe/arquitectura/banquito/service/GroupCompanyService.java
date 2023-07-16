package ec.edu.espe.arquitectura.banquito.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.espe.arquitectura.banquito.dto.GroupCompanyMemberRQ;
import ec.edu.espe.arquitectura.banquito.dto.GroupCompanyMemberRS;
import ec.edu.espe.arquitectura.banquito.dto.GroupCompanyRS;
import ec.edu.espe.arquitectura.banquito.model.ClientAddress;
import ec.edu.espe.arquitectura.banquito.model.GroupCompany;
import ec.edu.espe.arquitectura.banquito.model.GroupCompanyMember;
import ec.edu.espe.arquitectura.banquito.repository.GroupCompanyRepository;

@Service
public class GroupCompanyService {
    private final GroupCompanyRepository groupCompanyRepository;

    public GroupCompanyService(GroupCompanyRepository groupCompanyRepository) {
        this.groupCompanyRepository = groupCompanyRepository;
    }

    public Boolean hasDuplicates(List<?> list) {
        Set<Object> set = new HashSet<>(list);
        return set.size() < list.size();
    }

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
        // Client clientTmp =
        // this.groupCompanyRepository.findFirstByUniqueKey(companyTmp.getMembers())
        if (companyTmp == null) {
            throw new RuntimeException("No existe la compañia");
        } else {
            List<GroupCompanyMember> newMembers = this.transformMembersRQ(membersRQ);
            List<GroupCompanyMember> members = companyTmp.getMembers();
            List<String> allClientIds = new ArrayList<>();
            if (members == null) {
                for (GroupCompanyMember member : newMembers) {
                    member.setCreationDate(new Date());
                    member.setLastModifiedDate(new Date());
                    member.setState("ACT");
                    allClientIds.add(member.getClientId());
                }
                if (this.hasDuplicates(allClientIds)) {
                    throw new RuntimeException(
                            "Existen clientes que ya perteneces a la organización, volver a intentar");
                } else {
                    companyTmp.setMembers(newMembers);
                }
                
            }else {
                List<GroupCompanyMember> allMembers = new ArrayList<>();
                for (GroupCompanyMember member : newMembers) {
                    member.setCreationDate(new Date());
                    member.setLastModifiedDate(new Date());
                    member.setState("ACT");
                    allMembers.add(member);
                    allClientIds.add(member.getClientId());
                }
                for (GroupCompanyMember member : members) {
                    allMembers.add(member);
                    allClientIds.add(member.getClientId());
                }
                if (this.hasDuplicates(allClientIds)) {
                    throw new RuntimeException("Existen clientes que ya perteneces a la organizaci\u00F3n, volver a intentar");
                } else {
                    companyTmp.setMembers(allMembers);
                }
            }
            return this.groupCompanyRepository.save(companyTmp);
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

}
