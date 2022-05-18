package br.com.roberth.leadsmaria.service;

import br.com.roberth.leadsmaria.domain.*; // for static metamodels
import br.com.roberth.leadsmaria.domain.Lead;
import br.com.roberth.leadsmaria.repository.LeadRepository;
import br.com.roberth.leadsmaria.service.criteria.LeadCriteria;
import br.com.roberth.leadsmaria.service.dto.LeadDTO;
import br.com.roberth.leadsmaria.service.mapper.LeadMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Lead} entities in the database.
 * The main input is a {@link LeadCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LeadDTO} or a {@link Page} of {@link LeadDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LeadQueryService extends QueryService<Lead> {

    private final Logger log = LoggerFactory.getLogger(LeadQueryService.class);

    private final LeadRepository leadRepository;

    private final LeadMapper leadMapper;

    public LeadQueryService(LeadRepository leadRepository, LeadMapper leadMapper) {
        this.leadRepository = leadRepository;
        this.leadMapper = leadMapper;
    }

    /**
     * Return a {@link List} of {@link LeadDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LeadDTO> findByCriteria(LeadCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Lead> specification = createSpecification(criteria);
        return leadMapper.toDto(leadRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link LeadDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LeadDTO> findByCriteria(LeadCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Lead> specification = createSpecification(criteria);
        return leadRepository.findAll(specification, page).map(leadMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LeadCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Lead> specification = createSpecification(criteria);
        return leadRepository.count(specification);
    }

    /**
     * Function to convert {@link LeadCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Lead> createSpecification(LeadCriteria criteria) {
        Specification<Lead> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Lead_.id));
            }
            if (criteria.getNomeLead() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNomeLead(), Lead_.nomeLead));
            }
            if (criteria.getTelefone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTelefone(), Lead_.telefone));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Lead_.email));
            }
            if (criteria.getDataNascimento() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataNascimento(), Lead_.dataNascimento));
            }
            if (criteria.getDataCadastro() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataCadastro(), Lead_.dataCadastro));
            }
            if (criteria.getTagsId() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getTagsId(), root -> root.join(Lead_.tags, JoinType.LEFT).get(Tag_.id)));
            }
            if (criteria.getListaId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getListaId(), root -> root.join(Lead_.listas, JoinType.LEFT).get(Lista_.id))
                    );
            }
        }
        return specification;
    }
}
