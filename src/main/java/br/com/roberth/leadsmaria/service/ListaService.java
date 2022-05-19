package br.com.roberth.leadsmaria.service;

import br.com.roberth.leadsmaria.service.dto.ListaDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link br.com.roberth.leadsmaria.domain.Lista}.
 */
public interface ListaService {
    /**
     * Save a lista.
     *
     * @param listaDTO the entity to save.
     * @return the persisted entity.
     */
    ListaDTO save(ListaDTO listaDTO);

    /**
     * Updates a lista.
     *
     * @param listaDTO the entity to update.
     * @return the persisted entity.
     */
    ListaDTO update(ListaDTO listaDTO);

    /**
     * Partially updates a lista.
     *
     * @param listaDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ListaDTO> partialUpdate(ListaDTO listaDTO);

    /**
     * Get all the listas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ListaDTO> findAll(Pageable pageable);

    /**
     * Get the "id" lista.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ListaDTO> findOne(Long id);

    /**
     * Delete the "id" lista.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
