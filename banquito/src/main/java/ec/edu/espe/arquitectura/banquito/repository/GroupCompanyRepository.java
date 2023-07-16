package ec.edu.espe.arquitectura.banquito.repository;



import org.springframework.data.mongodb.repository.MongoRepository;

import ec.edu.espe.arquitectura.banquito.model.GroupCompany;

public interface GroupCompanyRepository extends MongoRepository<GroupCompany, String>{
   GroupCompany findFirstByUniqueKey(String uniqueKey);
   GroupCompany findFirstByGroupName(String groupName);
}
