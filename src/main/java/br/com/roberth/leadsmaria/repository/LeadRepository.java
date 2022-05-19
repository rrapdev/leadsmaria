package br.com.roberth.leadsmaria.repository;

import br.com.roberth.leadsmaria.domain.Lead;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Lead entity.
 */
@Repository
public interface LeadRepository extends LeadRepositoryWithBagRelationships, JpaRepository<Lead, Long>, JpaSpecificationExecutor<Lead> {
    default Optional<Lead> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Lead> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Lead> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
