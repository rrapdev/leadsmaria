package br.com.roberth.leadsmaria.repository;

import br.com.roberth.leadsmaria.domain.Lead;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class LeadRepositoryWithBagRelationshipsImpl implements LeadRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Lead> fetchBagRelationships(Optional<Lead> lead) {
        return lead.map(this::fetchTags).map(this::fetchListas);
    }

    @Override
    public Page<Lead> fetchBagRelationships(Page<Lead> leads) {
        return new PageImpl<>(fetchBagRelationships(leads.getContent()), leads.getPageable(), leads.getTotalElements());
    }

    @Override
    public List<Lead> fetchBagRelationships(List<Lead> leads) {
        return Optional.of(leads).map(this::fetchTags).map(this::fetchListas).orElse(Collections.emptyList());
    }

    Lead fetchTags(Lead result) {
        return entityManager
            .createQuery("select lead from Lead lead left join fetch lead.tags where lead is :lead", Lead.class)
            .setParameter("lead", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Lead> fetchTags(List<Lead> leads) {
        return entityManager
            .createQuery("select distinct lead from Lead lead left join fetch lead.tags where lead in :leads", Lead.class)
            .setParameter("leads", leads)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }

    Lead fetchListas(Lead result) {
        return entityManager
            .createQuery("select lead from Lead lead left join fetch lead.listas where lead is :lead", Lead.class)
            .setParameter("lead", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Lead> fetchListas(List<Lead> leads) {
        return entityManager
            .createQuery("select distinct lead from Lead lead left join fetch lead.listas where lead in :leads", Lead.class)
            .setParameter("leads", leads)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
