package br.com.roberth.leadsmaria.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.roberth.leadsmaria.IntegrationTest;
import br.com.roberth.leadsmaria.domain.Lead;
import br.com.roberth.leadsmaria.domain.Lista;
import br.com.roberth.leadsmaria.repository.ListaRepository;
import br.com.roberth.leadsmaria.service.criteria.ListaCriteria;
import br.com.roberth.leadsmaria.service.dto.ListaDTO;
import br.com.roberth.leadsmaria.service.mapper.ListaMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ListaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ListaResourceIT {

    private static final String DEFAULT_NOME_LISTA = "AAAAAAAAAA";
    private static final String UPDATED_NOME_LISTA = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATA_CADASTRO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_CADASTRO = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATA_CADASTRO = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/listas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ListaRepository listaRepository;

    @Autowired
    private ListaMapper listaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restListaMockMvc;

    private Lista lista;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lista createEntity(EntityManager em) {
        Lista lista = new Lista().nomeLista(DEFAULT_NOME_LISTA).dataCadastro(DEFAULT_DATA_CADASTRO);
        return lista;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lista createUpdatedEntity(EntityManager em) {
        Lista lista = new Lista().nomeLista(UPDATED_NOME_LISTA).dataCadastro(UPDATED_DATA_CADASTRO);
        return lista;
    }

    @BeforeEach
    public void initTest() {
        lista = createEntity(em);
    }

    @Test
    @Transactional
    void createLista() throws Exception {
        int databaseSizeBeforeCreate = listaRepository.findAll().size();
        // Create the Lista
        ListaDTO listaDTO = listaMapper.toDto(lista);
        restListaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(listaDTO)))
            .andExpect(status().isCreated());

        // Validate the Lista in the database
        List<Lista> listaList = listaRepository.findAll();
        assertThat(listaList).hasSize(databaseSizeBeforeCreate + 1);
        Lista testLista = listaList.get(listaList.size() - 1);
        assertThat(testLista.getNomeLista()).isEqualTo(DEFAULT_NOME_LISTA);
        assertThat(testLista.getDataCadastro()).isEqualTo(DEFAULT_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void createListaWithExistingId() throws Exception {
        // Create the Lista with an existing ID
        lista.setId(1L);
        ListaDTO listaDTO = listaMapper.toDto(lista);

        int databaseSizeBeforeCreate = listaRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restListaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(listaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Lista in the database
        List<Lista> listaList = listaRepository.findAll();
        assertThat(listaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllListas() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList
        restListaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lista.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomeLista").value(hasItem(DEFAULT_NOME_LISTA)))
            .andExpect(jsonPath("$.[*].dataCadastro").value(hasItem(DEFAULT_DATA_CADASTRO.toString())));
    }

    @Test
    @Transactional
    void getLista() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get the lista
        restListaMockMvc
            .perform(get(ENTITY_API_URL_ID, lista.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lista.getId().intValue()))
            .andExpect(jsonPath("$.nomeLista").value(DEFAULT_NOME_LISTA))
            .andExpect(jsonPath("$.dataCadastro").value(DEFAULT_DATA_CADASTRO.toString()));
    }

    @Test
    @Transactional
    void getListasByIdFiltering() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        Long id = lista.getId();

        defaultListaShouldBeFound("id.equals=" + id);
        defaultListaShouldNotBeFound("id.notEquals=" + id);

        defaultListaShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultListaShouldNotBeFound("id.greaterThan=" + id);

        defaultListaShouldBeFound("id.lessThanOrEqual=" + id);
        defaultListaShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllListasByNomeListaIsEqualToSomething() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where nomeLista equals to DEFAULT_NOME_LISTA
        defaultListaShouldBeFound("nomeLista.equals=" + DEFAULT_NOME_LISTA);

        // Get all the listaList where nomeLista equals to UPDATED_NOME_LISTA
        defaultListaShouldNotBeFound("nomeLista.equals=" + UPDATED_NOME_LISTA);
    }

    @Test
    @Transactional
    void getAllListasByNomeListaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where nomeLista not equals to DEFAULT_NOME_LISTA
        defaultListaShouldNotBeFound("nomeLista.notEquals=" + DEFAULT_NOME_LISTA);

        // Get all the listaList where nomeLista not equals to UPDATED_NOME_LISTA
        defaultListaShouldBeFound("nomeLista.notEquals=" + UPDATED_NOME_LISTA);
    }

    @Test
    @Transactional
    void getAllListasByNomeListaIsInShouldWork() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where nomeLista in DEFAULT_NOME_LISTA or UPDATED_NOME_LISTA
        defaultListaShouldBeFound("nomeLista.in=" + DEFAULT_NOME_LISTA + "," + UPDATED_NOME_LISTA);

        // Get all the listaList where nomeLista equals to UPDATED_NOME_LISTA
        defaultListaShouldNotBeFound("nomeLista.in=" + UPDATED_NOME_LISTA);
    }

    @Test
    @Transactional
    void getAllListasByNomeListaIsNullOrNotNull() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where nomeLista is not null
        defaultListaShouldBeFound("nomeLista.specified=true");

        // Get all the listaList where nomeLista is null
        defaultListaShouldNotBeFound("nomeLista.specified=false");
    }

    @Test
    @Transactional
    void getAllListasByNomeListaContainsSomething() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where nomeLista contains DEFAULT_NOME_LISTA
        defaultListaShouldBeFound("nomeLista.contains=" + DEFAULT_NOME_LISTA);

        // Get all the listaList where nomeLista contains UPDATED_NOME_LISTA
        defaultListaShouldNotBeFound("nomeLista.contains=" + UPDATED_NOME_LISTA);
    }

    @Test
    @Transactional
    void getAllListasByNomeListaNotContainsSomething() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where nomeLista does not contain DEFAULT_NOME_LISTA
        defaultListaShouldNotBeFound("nomeLista.doesNotContain=" + DEFAULT_NOME_LISTA);

        // Get all the listaList where nomeLista does not contain UPDATED_NOME_LISTA
        defaultListaShouldBeFound("nomeLista.doesNotContain=" + UPDATED_NOME_LISTA);
    }

    @Test
    @Transactional
    void getAllListasByDataCadastroIsEqualToSomething() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where dataCadastro equals to DEFAULT_DATA_CADASTRO
        defaultListaShouldBeFound("dataCadastro.equals=" + DEFAULT_DATA_CADASTRO);

        // Get all the listaList where dataCadastro equals to UPDATED_DATA_CADASTRO
        defaultListaShouldNotBeFound("dataCadastro.equals=" + UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllListasByDataCadastroIsNotEqualToSomething() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where dataCadastro not equals to DEFAULT_DATA_CADASTRO
        defaultListaShouldNotBeFound("dataCadastro.notEquals=" + DEFAULT_DATA_CADASTRO);

        // Get all the listaList where dataCadastro not equals to UPDATED_DATA_CADASTRO
        defaultListaShouldBeFound("dataCadastro.notEquals=" + UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllListasByDataCadastroIsInShouldWork() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where dataCadastro in DEFAULT_DATA_CADASTRO or UPDATED_DATA_CADASTRO
        defaultListaShouldBeFound("dataCadastro.in=" + DEFAULT_DATA_CADASTRO + "," + UPDATED_DATA_CADASTRO);

        // Get all the listaList where dataCadastro equals to UPDATED_DATA_CADASTRO
        defaultListaShouldNotBeFound("dataCadastro.in=" + UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllListasByDataCadastroIsNullOrNotNull() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where dataCadastro is not null
        defaultListaShouldBeFound("dataCadastro.specified=true");

        // Get all the listaList where dataCadastro is null
        defaultListaShouldNotBeFound("dataCadastro.specified=false");
    }

    @Test
    @Transactional
    void getAllListasByDataCadastroIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where dataCadastro is greater than or equal to DEFAULT_DATA_CADASTRO
        defaultListaShouldBeFound("dataCadastro.greaterThanOrEqual=" + DEFAULT_DATA_CADASTRO);

        // Get all the listaList where dataCadastro is greater than or equal to UPDATED_DATA_CADASTRO
        defaultListaShouldNotBeFound("dataCadastro.greaterThanOrEqual=" + UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllListasByDataCadastroIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where dataCadastro is less than or equal to DEFAULT_DATA_CADASTRO
        defaultListaShouldBeFound("dataCadastro.lessThanOrEqual=" + DEFAULT_DATA_CADASTRO);

        // Get all the listaList where dataCadastro is less than or equal to SMALLER_DATA_CADASTRO
        defaultListaShouldNotBeFound("dataCadastro.lessThanOrEqual=" + SMALLER_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllListasByDataCadastroIsLessThanSomething() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where dataCadastro is less than DEFAULT_DATA_CADASTRO
        defaultListaShouldNotBeFound("dataCadastro.lessThan=" + DEFAULT_DATA_CADASTRO);

        // Get all the listaList where dataCadastro is less than UPDATED_DATA_CADASTRO
        defaultListaShouldBeFound("dataCadastro.lessThan=" + UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllListasByDataCadastroIsGreaterThanSomething() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        // Get all the listaList where dataCadastro is greater than DEFAULT_DATA_CADASTRO
        defaultListaShouldNotBeFound("dataCadastro.greaterThan=" + DEFAULT_DATA_CADASTRO);

        // Get all the listaList where dataCadastro is greater than SMALLER_DATA_CADASTRO
        defaultListaShouldBeFound("dataCadastro.greaterThan=" + SMALLER_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllListasByLeadsIsEqualToSomething() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);
        Lead leads;
        if (TestUtil.findAll(em, Lead.class).isEmpty()) {
            leads = LeadResourceIT.createEntity(em);
            em.persist(leads);
            em.flush();
        } else {
            leads = TestUtil.findAll(em, Lead.class).get(0);
        }
        em.persist(leads);
        em.flush();
        lista.addLeads(leads);
        listaRepository.saveAndFlush(lista);
        Long leadsId = leads.getId();

        // Get all the listaList where leads equals to leadsId
        defaultListaShouldBeFound("leadsId.equals=" + leadsId);

        // Get all the listaList where leads equals to (leadsId + 1)
        defaultListaShouldNotBeFound("leadsId.equals=" + (leadsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultListaShouldBeFound(String filter) throws Exception {
        restListaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lista.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomeLista").value(hasItem(DEFAULT_NOME_LISTA)))
            .andExpect(jsonPath("$.[*].dataCadastro").value(hasItem(DEFAULT_DATA_CADASTRO.toString())));

        // Check, that the count call also returns 1
        restListaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultListaShouldNotBeFound(String filter) throws Exception {
        restListaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restListaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLista() throws Exception {
        // Get the lista
        restListaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLista() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        int databaseSizeBeforeUpdate = listaRepository.findAll().size();

        // Update the lista
        Lista updatedLista = listaRepository.findById(lista.getId()).get();
        // Disconnect from session so that the updates on updatedLista are not directly saved in db
        em.detach(updatedLista);
        updatedLista.nomeLista(UPDATED_NOME_LISTA).dataCadastro(UPDATED_DATA_CADASTRO);
        ListaDTO listaDTO = listaMapper.toDto(updatedLista);

        restListaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, listaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(listaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Lista in the database
        List<Lista> listaList = listaRepository.findAll();
        assertThat(listaList).hasSize(databaseSizeBeforeUpdate);
        Lista testLista = listaList.get(listaList.size() - 1);
        assertThat(testLista.getNomeLista()).isEqualTo(UPDATED_NOME_LISTA);
        assertThat(testLista.getDataCadastro()).isEqualTo(UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void putNonExistingLista() throws Exception {
        int databaseSizeBeforeUpdate = listaRepository.findAll().size();
        lista.setId(count.incrementAndGet());

        // Create the Lista
        ListaDTO listaDTO = listaMapper.toDto(lista);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restListaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, listaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(listaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lista in the database
        List<Lista> listaList = listaRepository.findAll();
        assertThat(listaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLista() throws Exception {
        int databaseSizeBeforeUpdate = listaRepository.findAll().size();
        lista.setId(count.incrementAndGet());

        // Create the Lista
        ListaDTO listaDTO = listaMapper.toDto(lista);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restListaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(listaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lista in the database
        List<Lista> listaList = listaRepository.findAll();
        assertThat(listaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLista() throws Exception {
        int databaseSizeBeforeUpdate = listaRepository.findAll().size();
        lista.setId(count.incrementAndGet());

        // Create the Lista
        ListaDTO listaDTO = listaMapper.toDto(lista);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restListaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(listaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lista in the database
        List<Lista> listaList = listaRepository.findAll();
        assertThat(listaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateListaWithPatch() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        int databaseSizeBeforeUpdate = listaRepository.findAll().size();

        // Update the lista using partial update
        Lista partialUpdatedLista = new Lista();
        partialUpdatedLista.setId(lista.getId());

        partialUpdatedLista.nomeLista(UPDATED_NOME_LISTA);

        restListaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLista.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLista))
            )
            .andExpect(status().isOk());

        // Validate the Lista in the database
        List<Lista> listaList = listaRepository.findAll();
        assertThat(listaList).hasSize(databaseSizeBeforeUpdate);
        Lista testLista = listaList.get(listaList.size() - 1);
        assertThat(testLista.getNomeLista()).isEqualTo(UPDATED_NOME_LISTA);
        assertThat(testLista.getDataCadastro()).isEqualTo(DEFAULT_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void fullUpdateListaWithPatch() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        int databaseSizeBeforeUpdate = listaRepository.findAll().size();

        // Update the lista using partial update
        Lista partialUpdatedLista = new Lista();
        partialUpdatedLista.setId(lista.getId());

        partialUpdatedLista.nomeLista(UPDATED_NOME_LISTA).dataCadastro(UPDATED_DATA_CADASTRO);

        restListaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLista.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLista))
            )
            .andExpect(status().isOk());

        // Validate the Lista in the database
        List<Lista> listaList = listaRepository.findAll();
        assertThat(listaList).hasSize(databaseSizeBeforeUpdate);
        Lista testLista = listaList.get(listaList.size() - 1);
        assertThat(testLista.getNomeLista()).isEqualTo(UPDATED_NOME_LISTA);
        assertThat(testLista.getDataCadastro()).isEqualTo(UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void patchNonExistingLista() throws Exception {
        int databaseSizeBeforeUpdate = listaRepository.findAll().size();
        lista.setId(count.incrementAndGet());

        // Create the Lista
        ListaDTO listaDTO = listaMapper.toDto(lista);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restListaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, listaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(listaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lista in the database
        List<Lista> listaList = listaRepository.findAll();
        assertThat(listaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLista() throws Exception {
        int databaseSizeBeforeUpdate = listaRepository.findAll().size();
        lista.setId(count.incrementAndGet());

        // Create the Lista
        ListaDTO listaDTO = listaMapper.toDto(lista);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restListaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(listaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lista in the database
        List<Lista> listaList = listaRepository.findAll();
        assertThat(listaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLista() throws Exception {
        int databaseSizeBeforeUpdate = listaRepository.findAll().size();
        lista.setId(count.incrementAndGet());

        // Create the Lista
        ListaDTO listaDTO = listaMapper.toDto(lista);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restListaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(listaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lista in the database
        List<Lista> listaList = listaRepository.findAll();
        assertThat(listaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLista() throws Exception {
        // Initialize the database
        listaRepository.saveAndFlush(lista);

        int databaseSizeBeforeDelete = listaRepository.findAll().size();

        // Delete the lista
        restListaMockMvc
            .perform(delete(ENTITY_API_URL_ID, lista.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Lista> listaList = listaRepository.findAll();
        assertThat(listaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
