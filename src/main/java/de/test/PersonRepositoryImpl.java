package de.test;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(value = Transactional.TxType.REQUIRES_NEW)
public class PersonRepositoryImpl implements PersonRepository {

    @Inject
    EntityManager entityManager;

    @Inject
    EmployeeMapper mapper;

    @Override
    public void savePerson(EmployeeDto employeeDto) {
        this.entityManager.persist(this.mapper.dtoToEntity(employeeDto));
    }

    @Override
    public EmployeeDto findPerson(long id) {
        Employee entity = entityManager.find(Employee.class, id + 1);
        return this.mapper.entityToDto(entity);
    }

    @Override
    public long numberOfAllEmployees() {
        Query query = entityManager.createQuery("SELECT COUNT(*) FROM Employee e");
        return (long) query.getSingleResult();
    }

    @Override
    public void deleteEntries() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();

        // create delete
        CriteriaDelete<Employee> delete = cb.
                createCriteriaDelete(Employee.class);

        // set the root class
        Root e = delete.from(Employee.class);

        // perform update
        this.entityManager.createQuery(delete).executeUpdate();
    }
}
