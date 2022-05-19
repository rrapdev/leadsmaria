package br.com.roberth.leadsmaria.web.rest;

import br.com.roberth.leadsmaria.repository.ListaRepository;
import br.com.roberth.leadsmaria.service.ListaQueryService;
import br.com.roberth.leadsmaria.service.ListaService;
import br.com.roberth.leadsmaria.service.criteria.ListaCriteria;
import br.com.roberth.leadsmaria.service.dto.ListaDTO;
import br.com.roberth.leadsmaria.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link br.com.roberth.leadsmaria.domain.Lista}.
 */
@RestController
@RequestMapping("/api")
public class ListaResource {

    private final Logger log = LoggerFactory.getLogger(ListaResource.class);

    private static final String ENTITY_NAME = "lista";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ListaService listaService;

    private final ListaRepository listaRepository;

    private final ListaQueryService listaQueryService;

    public ListaResource(ListaService listaService, ListaRepository listaRepository, ListaQueryService listaQueryService) {
        this.listaService = listaService;
        this.listaRepository = listaRepository;
        this.listaQueryService = listaQueryService;
    }

    /**
     * {@code POST  /listas} : Create a new lista.
     *
     * @param listaDTO the listaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new listaDTO, or with status {@code 400 (Bad Request)} if the lista has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/listas")
    public ResponseEntity<ListaDTO> createLista(@RequestBody ListaDTO listaDTO) throws URISyntaxException {
        log.debug("REST request to save Lista : {}", listaDTO);
        if (listaDTO.getId() != null) {
            throw new BadRequestAlertException("A new lista cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ListaDTO result = listaService.save(listaDTO);
        return ResponseEntity
            .created(new URI("/api/listas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /listas/:id} : Updates an existing lista.
     *
     * @param id the id of the listaDTO to save.
     * @param listaDTO the listaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated listaDTO,
     * or with status {@code 400 (Bad Request)} if the listaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the listaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/listas/{id}")
    public ResponseEntity<ListaDTO> updateLista(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ListaDTO listaDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Lista : {}, {}", id, listaDTO);
        if (listaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, listaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!listaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ListaDTO result = listaService.update(listaDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, listaDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /listas/:id} : Partial updates given fields of an existing lista, field will ignore if it is null
     *
     * @param id the id of the listaDTO to save.
     * @param listaDTO the listaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated listaDTO,
     * or with status {@code 400 (Bad Request)} if the listaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the listaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the listaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/listas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ListaDTO> partialUpdateLista(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ListaDTO listaDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Lista partially : {}, {}", id, listaDTO);
        if (listaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, listaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!listaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ListaDTO> result = listaService.partialUpdate(listaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, listaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /listas} : get all the listas.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of listas in body.
     */
    @GetMapping("/listas")
    public ResponseEntity<List<ListaDTO>> getAllListas(
        ListaCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Listas by criteria: {}", criteria);
        Page<ListaDTO> page = listaQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /listas/count} : count all the listas.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/listas/count")
    public ResponseEntity<Long> countListas(ListaCriteria criteria) {
        log.debug("REST request to count Listas by criteria: {}", criteria);
        return ResponseEntity.ok().body(listaQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /listas/:id} : get the "id" lista.
     *
     * @param id the id of the listaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the listaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/listas/{id}")
    public ResponseEntity<ListaDTO> getLista(@PathVariable Long id) {
        log.debug("REST request to get Lista : {}", id);
        Optional<ListaDTO> listaDTO = listaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(listaDTO);
    }

    /**
     * {@code DELETE  /listas/:id} : delete the "id" lista.
     *
     * @param id the id of the listaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/listas/{id}")
    public ResponseEntity<Void> deleteLista(@PathVariable Long id) {
        log.debug("REST request to delete Lista : {}", id);
        listaService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
