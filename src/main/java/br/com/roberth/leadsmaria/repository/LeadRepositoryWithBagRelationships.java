package br.com.roberth.leadsmaria.repository;

import br.com.roberth.leadsmaria.domain.Lead;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface LeadRepositoryWithBagRelationships {
    Optional<Lead> fetchBagRelationships(Optional<Lead> lead);

    List<Lead> fetchBagRelationships(List<Lead> leads);

    Page<Lead> fetchBagRelationships(Page<Lead> leads);
}
