package br.com.roberth.leadsmaria.service;

import br.com.roberth.leadsmaria.domain.*; // for static metamodels
import br.com.roberth.leadsmaria.domain.Lista;
import br.com.roberth.leadsmaria.repository.ListaRepository;
import br.com.roberth.leadsmaria.service.criteria.ListaCriteria;
import br.com.roberth.leadsmaria.service.dto.ListaDTO;
import br.com.roberth.leadsmaria.service.mapper.ListaMapper;
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
 * Service for executing complex queries for {@link Lista} entities in the database.
 * The main input is a {@link ListaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ListaDTO} or a {@link Page} of {@link ListaDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ListaQueryService extends QueryService<Lista> {

    private final Logger log = LoggerFactory.getLogger(ListaQueryService.class);

    private final ListaRepository listaRepository;

    private final ListaMapper listaMapper;

    public ListaQueryService(ListaRepository listaRepository, ListaMapper listaMapper) {
        this.listaRepository = listaRepository;
        this.listaMapper = listaMapper;
    }

    /**
     * Return a {@link List} of {@link ListaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ListaDTO> findByCriteria(ListaCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Lista> specification = createSpecification(criteria);
        return listaMapper.toDto(listaRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ListaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ListaDTO> findByCriteria(ListaCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Lista> specification = createSpecification(criteria);
        return listaRepository.findAll(specification, page).map(listaMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ListaCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Lista> specification = createSpecification(criteria);
        return listaRepository.count(specification);
    }

    /**
     * Function to convert {@link ListaCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Lista> createSpecification(ListaCriteria criteria) {
        Specification<Lista> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Lista_.id));
            }
            if (criteria.getNomeLista() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNomeLista(), Lista_.nomeLista));
            }
            if (criteria.getDataCadastro() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataCadastro(), Lista_.dataCadastro));
            }
            if (criteria.getLeadsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getLeadsId(), root -> root.join(Lista_.leads, JoinType.LEFT).get(Lead_.id))
                    );
            }
        }
        return specification;
    }
}
