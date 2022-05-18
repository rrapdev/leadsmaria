package br.com.roberth.leadsmaria.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.roberth.leadsmaria.IntegrationTest;
import br.com.roberth.leadsmaria.domain.Lead;
import br.com.roberth.leadsmaria.domain.Lista;
import br.com.roberth.leadsmaria.domain.Tag;
import br.com.roberth.leadsmaria.repository.LeadRepository;
import br.com.roberth.leadsmaria.service.LeadService;
import br.com.roberth.leadsmaria.service.criteria.LeadCriteria;
import br.com.roberth.leadsmaria.service.dto.LeadDTO;
import br.com.roberth.leadsmaria.service.mapper.LeadMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LeadResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LeadResourceIT {

    private static final String DEFAULT_NOME_LEAD = "AAAAAAAAAA";
    private static final String UPDATED_NOME_LEAD = "BBBBBBBBBB";

    private static final String DEFAULT_TELEFONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATA_NASCIMENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_NASCIMENTO = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATA_NASCIMENTO = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATA_CADASTRO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_CADASTRO = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATA_CADASTRO = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/leads";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LeadRepository leadRepository;

    @Mock
    private LeadRepository leadRepositoryMock;

    @Autowired
    private LeadMapper leadMapper;

    @Mock
    private LeadService leadServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLeadMockMvc;

    private Lead lead;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lead createEntity(EntityManager em) {
        Lead lead = new Lead()
            .nomeLead(DEFAULT_NOME_LEAD)
            .telefone(DEFAULT_TELEFONE)
            .email(DEFAULT_EMAIL)
            .dataNascimento(DEFAULT_DATA_NASCIMENTO)
            .dataCadastro(DEFAULT_DATA_CADASTRO);
        return lead;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lead createUpdatedEntity(EntityManager em) {
        Lead lead = new Lead()
            .nomeLead(UPDATED_NOME_LEAD)
            .telefone(UPDATED_TELEFONE)
            .email(UPDATED_EMAIL)
            .dataNascimento(UPDATED_DATA_NASCIMENTO)
            .dataCadastro(UPDATED_DATA_CADASTRO);
        return lead;
    }

    @BeforeEach
    public void initTest() {
        lead = createEntity(em);
    }

    @Test
    @Transactional
    void createLead() throws Exception {
        int databaseSizeBeforeCreate = leadRepository.findAll().size();
        // Create the Lead
        LeadDTO leadDTO = leadMapper.toDto(lead);
        restLeadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(leadDTO)))
            .andExpect(status().isCreated());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAll();
        assertThat(leadList).hasSize(databaseSizeBeforeCreate + 1);
        Lead testLead = leadList.get(leadList.size() - 1);
        assertThat(testLead.getNomeLead()).isEqualTo(DEFAULT_NOME_LEAD);
        assertThat(testLead.getTelefone()).isEqualTo(DEFAULT_TELEFONE);
        assertThat(testLead.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testLead.getDataNascimento()).isEqualTo(DEFAULT_DATA_NASCIMENTO);
        assertThat(testLead.getDataCadastro()).isEqualTo(DEFAULT_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void createLeadWithExistingId() throws Exception {
        // Create the Lead with an existing ID
        lead.setId(1L);
        LeadDTO leadDTO = leadMapper.toDto(lead);

        int databaseSizeBeforeCreate = leadRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLeadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(leadDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAll();
        assertThat(leadList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLeads() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList
        restLeadMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lead.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomeLead").value(hasItem(DEFAULT_NOME_LEAD)))
            .andExpect(jsonPath("$.[*].telefone").value(hasItem(DEFAULT_TELEFONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].dataNascimento").value(hasItem(DEFAULT_DATA_NASCIMENTO.toString())))
            .andExpect(jsonPath("$.[*].dataCadastro").value(hasItem(DEFAULT_DATA_CADASTRO.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLeadsWithEagerRelationshipsIsEnabled() throws Exception {
        when(leadServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLeadMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(leadServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLeadsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(leadServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLeadMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(leadServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getLead() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get the lead
        restLeadMockMvc
            .perform(get(ENTITY_API_URL_ID, lead.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lead.getId().intValue()))
            .andExpect(jsonPath("$.nomeLead").value(DEFAULT_NOME_LEAD))
            .andExpect(jsonPath("$.telefone").value(DEFAULT_TELEFONE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.dataNascimento").value(DEFAULT_DATA_NASCIMENTO.toString()))
            .andExpect(jsonPath("$.dataCadastro").value(DEFAULT_DATA_CADASTRO.toString()));
    }

    @Test
    @Transactional
    void getLeadsByIdFiltering() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        Long id = lead.getId();

        defaultLeadShouldBeFound("id.equals=" + id);
        defaultLeadShouldNotBeFound("id.notEquals=" + id);

        defaultLeadShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLeadShouldNotBeFound("id.greaterThan=" + id);

        defaultLeadShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLeadShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLeadsByNomeLeadIsEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where nomeLead equals to DEFAULT_NOME_LEAD
        defaultLeadShouldBeFound("nomeLead.equals=" + DEFAULT_NOME_LEAD);

        // Get all the leadList where nomeLead equals to UPDATED_NOME_LEAD
        defaultLeadShouldNotBeFound("nomeLead.equals=" + UPDATED_NOME_LEAD);
    }

    @Test
    @Transactional
    void getAllLeadsByNomeLeadIsNotEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where nomeLead not equals to DEFAULT_NOME_LEAD
        defaultLeadShouldNotBeFound("nomeLead.notEquals=" + DEFAULT_NOME_LEAD);

        // Get all the leadList where nomeLead not equals to UPDATED_NOME_LEAD
        defaultLeadShouldBeFound("nomeLead.notEquals=" + UPDATED_NOME_LEAD);
    }

    @Test
    @Transactional
    void getAllLeadsByNomeLeadIsInShouldWork() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where nomeLead in DEFAULT_NOME_LEAD or UPDATED_NOME_LEAD
        defaultLeadShouldBeFound("nomeLead.in=" + DEFAULT_NOME_LEAD + "," + UPDATED_NOME_LEAD);

        // Get all the leadList where nomeLead equals to UPDATED_NOME_LEAD
        defaultLeadShouldNotBeFound("nomeLead.in=" + UPDATED_NOME_LEAD);
    }

    @Test
    @Transactional
    void getAllLeadsByNomeLeadIsNullOrNotNull() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where nomeLead is not null
        defaultLeadShouldBeFound("nomeLead.specified=true");

        // Get all the leadList where nomeLead is null
        defaultLeadShouldNotBeFound("nomeLead.specified=false");
    }

    @Test
    @Transactional
    void getAllLeadsByNomeLeadContainsSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where nomeLead contains DEFAULT_NOME_LEAD
        defaultLeadShouldBeFound("nomeLead.contains=" + DEFAULT_NOME_LEAD);

        // Get all the leadList where nomeLead contains UPDATED_NOME_LEAD
        defaultLeadShouldNotBeFound("nomeLead.contains=" + UPDATED_NOME_LEAD);
    }

    @Test
    @Transactional
    void getAllLeadsByNomeLeadNotContainsSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where nomeLead does not contain DEFAULT_NOME_LEAD
        defaultLeadShouldNotBeFound("nomeLead.doesNotContain=" + DEFAULT_NOME_LEAD);

        // Get all the leadList where nomeLead does not contain UPDATED_NOME_LEAD
        defaultLeadShouldBeFound("nomeLead.doesNotContain=" + UPDATED_NOME_LEAD);
    }

    @Test
    @Transactional
    void getAllLeadsByTelefoneIsEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where telefone equals to DEFAULT_TELEFONE
        defaultLeadShouldBeFound("telefone.equals=" + DEFAULT_TELEFONE);

        // Get all the leadList where telefone equals to UPDATED_TELEFONE
        defaultLeadShouldNotBeFound("telefone.equals=" + UPDATED_TELEFONE);
    }

    @Test
    @Transactional
    void getAllLeadsByTelefoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where telefone not equals to DEFAULT_TELEFONE
        defaultLeadShouldNotBeFound("telefone.notEquals=" + DEFAULT_TELEFONE);

        // Get all the leadList where telefone not equals to UPDATED_TELEFONE
        defaultLeadShouldBeFound("telefone.notEquals=" + UPDATED_TELEFONE);
    }

    @Test
    @Transactional
    void getAllLeadsByTelefoneIsInShouldWork() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where telefone in DEFAULT_TELEFONE or UPDATED_TELEFONE
        defaultLeadShouldBeFound("telefone.in=" + DEFAULT_TELEFONE + "," + UPDATED_TELEFONE);

        // Get all the leadList where telefone equals to UPDATED_TELEFONE
        defaultLeadShouldNotBeFound("telefone.in=" + UPDATED_TELEFONE);
    }

    @Test
    @Transactional
    void getAllLeadsByTelefoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where telefone is not null
        defaultLeadShouldBeFound("telefone.specified=true");

        // Get all the leadList where telefone is null
        defaultLeadShouldNotBeFound("telefone.specified=false");
    }

    @Test
    @Transactional
    void getAllLeadsByTelefoneContainsSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where telefone contains DEFAULT_TELEFONE
        defaultLeadShouldBeFound("telefone.contains=" + DEFAULT_TELEFONE);

        // Get all the leadList where telefone contains UPDATED_TELEFONE
        defaultLeadShouldNotBeFound("telefone.contains=" + UPDATED_TELEFONE);
    }

    @Test
    @Transactional
    void getAllLeadsByTelefoneNotContainsSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where telefone does not contain DEFAULT_TELEFONE
        defaultLeadShouldNotBeFound("telefone.doesNotContain=" + DEFAULT_TELEFONE);

        // Get all the leadList where telefone does not contain UPDATED_TELEFONE
        defaultLeadShouldBeFound("telefone.doesNotContain=" + UPDATED_TELEFONE);
    }

    @Test
    @Transactional
    void getAllLeadsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where email equals to DEFAULT_EMAIL
        defaultLeadShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the leadList where email equals to UPDATED_EMAIL
        defaultLeadShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllLeadsByEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where email not equals to DEFAULT_EMAIL
        defaultLeadShouldNotBeFound("email.notEquals=" + DEFAULT_EMAIL);

        // Get all the leadList where email not equals to UPDATED_EMAIL
        defaultLeadShouldBeFound("email.notEquals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllLeadsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultLeadShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the leadList where email equals to UPDATED_EMAIL
        defaultLeadShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllLeadsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where email is not null
        defaultLeadShouldBeFound("email.specified=true");

        // Get all the leadList where email is null
        defaultLeadShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllLeadsByEmailContainsSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where email contains DEFAULT_EMAIL
        defaultLeadShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the leadList where email contains UPDATED_EMAIL
        defaultLeadShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllLeadsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where email does not contain DEFAULT_EMAIL
        defaultLeadShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the leadList where email does not contain UPDATED_EMAIL
        defaultLeadShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllLeadsByDataNascimentoIsEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataNascimento equals to DEFAULT_DATA_NASCIMENTO
        defaultLeadShouldBeFound("dataNascimento.equals=" + DEFAULT_DATA_NASCIMENTO);

        // Get all the leadList where dataNascimento equals to UPDATED_DATA_NASCIMENTO
        defaultLeadShouldNotBeFound("dataNascimento.equals=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataNascimentoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataNascimento not equals to DEFAULT_DATA_NASCIMENTO
        defaultLeadShouldNotBeFound("dataNascimento.notEquals=" + DEFAULT_DATA_NASCIMENTO);

        // Get all the leadList where dataNascimento not equals to UPDATED_DATA_NASCIMENTO
        defaultLeadShouldBeFound("dataNascimento.notEquals=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataNascimentoIsInShouldWork() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataNascimento in DEFAULT_DATA_NASCIMENTO or UPDATED_DATA_NASCIMENTO
        defaultLeadShouldBeFound("dataNascimento.in=" + DEFAULT_DATA_NASCIMENTO + "," + UPDATED_DATA_NASCIMENTO);

        // Get all the leadList where dataNascimento equals to UPDATED_DATA_NASCIMENTO
        defaultLeadShouldNotBeFound("dataNascimento.in=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataNascimentoIsNullOrNotNull() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataNascimento is not null
        defaultLeadShouldBeFound("dataNascimento.specified=true");

        // Get all the leadList where dataNascimento is null
        defaultLeadShouldNotBeFound("dataNascimento.specified=false");
    }

    @Test
    @Transactional
    void getAllLeadsByDataNascimentoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataNascimento is greater than or equal to DEFAULT_DATA_NASCIMENTO
        defaultLeadShouldBeFound("dataNascimento.greaterThanOrEqual=" + DEFAULT_DATA_NASCIMENTO);

        // Get all the leadList where dataNascimento is greater than or equal to UPDATED_DATA_NASCIMENTO
        defaultLeadShouldNotBeFound("dataNascimento.greaterThanOrEqual=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataNascimentoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataNascimento is less than or equal to DEFAULT_DATA_NASCIMENTO
        defaultLeadShouldBeFound("dataNascimento.lessThanOrEqual=" + DEFAULT_DATA_NASCIMENTO);

        // Get all the leadList where dataNascimento is less than or equal to SMALLER_DATA_NASCIMENTO
        defaultLeadShouldNotBeFound("dataNascimento.lessThanOrEqual=" + SMALLER_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataNascimentoIsLessThanSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataNascimento is less than DEFAULT_DATA_NASCIMENTO
        defaultLeadShouldNotBeFound("dataNascimento.lessThan=" + DEFAULT_DATA_NASCIMENTO);

        // Get all the leadList where dataNascimento is less than UPDATED_DATA_NASCIMENTO
        defaultLeadShouldBeFound("dataNascimento.lessThan=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataNascimentoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataNascimento is greater than DEFAULT_DATA_NASCIMENTO
        defaultLeadShouldNotBeFound("dataNascimento.greaterThan=" + DEFAULT_DATA_NASCIMENTO);

        // Get all the leadList where dataNascimento is greater than SMALLER_DATA_NASCIMENTO
        defaultLeadShouldBeFound("dataNascimento.greaterThan=" + SMALLER_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataCadastroIsEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataCadastro equals to DEFAULT_DATA_CADASTRO
        defaultLeadShouldBeFound("dataCadastro.equals=" + DEFAULT_DATA_CADASTRO);

        // Get all the leadList where dataCadastro equals to UPDATED_DATA_CADASTRO
        defaultLeadShouldNotBeFound("dataCadastro.equals=" + UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataCadastroIsNotEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataCadastro not equals to DEFAULT_DATA_CADASTRO
        defaultLeadShouldNotBeFound("dataCadastro.notEquals=" + DEFAULT_DATA_CADASTRO);

        // Get all the leadList where dataCadastro not equals to UPDATED_DATA_CADASTRO
        defaultLeadShouldBeFound("dataCadastro.notEquals=" + UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataCadastroIsInShouldWork() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataCadastro in DEFAULT_DATA_CADASTRO or UPDATED_DATA_CADASTRO
        defaultLeadShouldBeFound("dataCadastro.in=" + DEFAULT_DATA_CADASTRO + "," + UPDATED_DATA_CADASTRO);

        // Get all the leadList where dataCadastro equals to UPDATED_DATA_CADASTRO
        defaultLeadShouldNotBeFound("dataCadastro.in=" + UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataCadastroIsNullOrNotNull() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataCadastro is not null
        defaultLeadShouldBeFound("dataCadastro.specified=true");

        // Get all the leadList where dataCadastro is null
        defaultLeadShouldNotBeFound("dataCadastro.specified=false");
    }

    @Test
    @Transactional
    void getAllLeadsByDataCadastroIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataCadastro is greater than or equal to DEFAULT_DATA_CADASTRO
        defaultLeadShouldBeFound("dataCadastro.greaterThanOrEqual=" + DEFAULT_DATA_CADASTRO);

        // Get all the leadList where dataCadastro is greater than or equal to UPDATED_DATA_CADASTRO
        defaultLeadShouldNotBeFound("dataCadastro.greaterThanOrEqual=" + UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataCadastroIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataCadastro is less than or equal to DEFAULT_DATA_CADASTRO
        defaultLeadShouldBeFound("dataCadastro.lessThanOrEqual=" + DEFAULT_DATA_CADASTRO);

        // Get all the leadList where dataCadastro is less than or equal to SMALLER_DATA_CADASTRO
        defaultLeadShouldNotBeFound("dataCadastro.lessThanOrEqual=" + SMALLER_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataCadastroIsLessThanSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataCadastro is less than DEFAULT_DATA_CADASTRO
        defaultLeadShouldNotBeFound("dataCadastro.lessThan=" + DEFAULT_DATA_CADASTRO);

        // Get all the leadList where dataCadastro is less than UPDATED_DATA_CADASTRO
        defaultLeadShouldBeFound("dataCadastro.lessThan=" + UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllLeadsByDataCadastroIsGreaterThanSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList where dataCadastro is greater than DEFAULT_DATA_CADASTRO
        defaultLeadShouldNotBeFound("dataCadastro.greaterThan=" + DEFAULT_DATA_CADASTRO);

        // Get all the leadList where dataCadastro is greater than SMALLER_DATA_CADASTRO
        defaultLeadShouldBeFound("dataCadastro.greaterThan=" + SMALLER_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllLeadsByTagsIsEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);
        Tag tags;
        if (TestUtil.findAll(em, Tag.class).isEmpty()) {
            tags = TagResourceIT.createEntity(em);
            em.persist(tags);
            em.flush();
        } else {
            tags = TestUtil.findAll(em, Tag.class).get(0);
        }
        em.persist(tags);
        em.flush();
        lead.addTags(tags);
        leadRepository.saveAndFlush(lead);
        Long tagsId = tags.getId();

        // Get all the leadList where tags equals to tagsId
        defaultLeadShouldBeFound("tagsId.equals=" + tagsId);

        // Get all the leadList where tags equals to (tagsId + 1)
        defaultLeadShouldNotBeFound("tagsId.equals=" + (tagsId + 1));
    }

    @Test
    @Transactional
    void getAllLeadsByListaIsEqualToSomething() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);
        Lista lista;
        if (TestUtil.findAll(em, Lista.class).isEmpty()) {
            lista = ListaResourceIT.createEntity(em);
            em.persist(lista);
            em.flush();
        } else {
            lista = TestUtil.findAll(em, Lista.class).get(0);
        }
        em.persist(lista);
        em.flush();
        lead.addLista(lista);
        leadRepository.saveAndFlush(lead);
        Long listaId = lista.getId();

        // Get all the leadList where lista equals to listaId
        defaultLeadShouldBeFound("listaId.equals=" + listaId);

        // Get all the leadList where lista equals to (listaId + 1)
        defaultLeadShouldNotBeFound("listaId.equals=" + (listaId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLeadShouldBeFound(String filter) throws Exception {
        restLeadMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lead.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomeLead").value(hasItem(DEFAULT_NOME_LEAD)))
            .andExpect(jsonPath("$.[*].telefone").value(hasItem(DEFAULT_TELEFONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].dataNascimento").value(hasItem(DEFAULT_DATA_NASCIMENTO.toString())))
            .andExpect(jsonPath("$.[*].dataCadastro").value(hasItem(DEFAULT_DATA_CADASTRO.toString())));

        // Check, that the count call also returns 1
        restLeadMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLeadShouldNotBeFound(String filter) throws Exception {
        restLeadMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLeadMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLead() throws Exception {
        // Get the lead
        restLeadMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLead() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        int databaseSizeBeforeUpdate = leadRepository.findAll().size();

        // Update the lead
        Lead updatedLead = leadRepository.findById(lead.getId()).get();
        // Disconnect from session so that the updates on updatedLead are not directly saved in db
        em.detach(updatedLead);
        updatedLead
            .nomeLead(UPDATED_NOME_LEAD)
            .telefone(UPDATED_TELEFONE)
            .email(UPDATED_EMAIL)
            .dataNascimento(UPDATED_DATA_NASCIMENTO)
            .dataCadastro(UPDATED_DATA_CADASTRO);
        LeadDTO leadDTO = leadMapper.toDto(updatedLead);

        restLeadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, leadDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(leadDTO))
            )
            .andExpect(status().isOk());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAll();
        assertThat(leadList).hasSize(databaseSizeBeforeUpdate);
        Lead testLead = leadList.get(leadList.size() - 1);
        assertThat(testLead.getNomeLead()).isEqualTo(UPDATED_NOME_LEAD);
        assertThat(testLead.getTelefone()).isEqualTo(UPDATED_TELEFONE);
        assertThat(testLead.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testLead.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
        assertThat(testLead.getDataCadastro()).isEqualTo(UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void putNonExistingLead() throws Exception {
        int databaseSizeBeforeUpdate = leadRepository.findAll().size();
        lead.setId(count.incrementAndGet());

        // Create the Lead
        LeadDTO leadDTO = leadMapper.toDto(lead);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, leadDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(leadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAll();
        assertThat(leadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLead() throws Exception {
        int databaseSizeBeforeUpdate = leadRepository.findAll().size();
        lead.setId(count.incrementAndGet());

        // Create the Lead
        LeadDTO leadDTO = leadMapper.toDto(lead);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(leadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAll();
        assertThat(leadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLead() throws Exception {
        int databaseSizeBeforeUpdate = leadRepository.findAll().size();
        lead.setId(count.incrementAndGet());

        // Create the Lead
        LeadDTO leadDTO = leadMapper.toDto(lead);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeadMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(leadDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAll();
        assertThat(leadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLeadWithPatch() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        int databaseSizeBeforeUpdate = leadRepository.findAll().size();

        // Update the lead using partial update
        Lead partialUpdatedLead = new Lead();
        partialUpdatedLead.setId(lead.getId());

        partialUpdatedLead.telefone(UPDATED_TELEFONE).email(UPDATED_EMAIL).dataCadastro(UPDATED_DATA_CADASTRO);

        restLeadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLead.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLead))
            )
            .andExpect(status().isOk());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAll();
        assertThat(leadList).hasSize(databaseSizeBeforeUpdate);
        Lead testLead = leadList.get(leadList.size() - 1);
        assertThat(testLead.getNomeLead()).isEqualTo(DEFAULT_NOME_LEAD);
        assertThat(testLead.getTelefone()).isEqualTo(UPDATED_TELEFONE);
        assertThat(testLead.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testLead.getDataNascimento()).isEqualTo(DEFAULT_DATA_NASCIMENTO);
        assertThat(testLead.getDataCadastro()).isEqualTo(UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void fullUpdateLeadWithPatch() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        int databaseSizeBeforeUpdate = leadRepository.findAll().size();

        // Update the lead using partial update
        Lead partialUpdatedLead = new Lead();
        partialUpdatedLead.setId(lead.getId());

        partialUpdatedLead
            .nomeLead(UPDATED_NOME_LEAD)
            .telefone(UPDATED_TELEFONE)
            .email(UPDATED_EMAIL)
            .dataNascimento(UPDATED_DATA_NASCIMENTO)
            .dataCadastro(UPDATED_DATA_CADASTRO);

        restLeadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLead.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLead))
            )
            .andExpect(status().isOk());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAll();
        assertThat(leadList).hasSize(databaseSizeBeforeUpdate);
        Lead testLead = leadList.get(leadList.size() - 1);
        assertThat(testLead.getNomeLead()).isEqualTo(UPDATED_NOME_LEAD);
        assertThat(testLead.getTelefone()).isEqualTo(UPDATED_TELEFONE);
        assertThat(testLead.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testLead.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
        assertThat(testLead.getDataCadastro()).isEqualTo(UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void patchNonExistingLead() throws Exception {
        int databaseSizeBeforeUpdate = leadRepository.findAll().size();
        lead.setId(count.incrementAndGet());

        // Create the Lead
        LeadDTO leadDTO = leadMapper.toDto(lead);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, leadDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(leadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAll();
        assertThat(leadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLead() throws Exception {
        int databaseSizeBeforeUpdate = leadRepository.findAll().size();
        lead.setId(count.incrementAndGet());

        // Create the Lead
        LeadDTO leadDTO = leadMapper.toDto(lead);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(leadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAll();
        assertThat(leadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLead() throws Exception {
        int databaseSizeBeforeUpdate = leadRepository.findAll().size();
        lead.setId(count.incrementAndGet());

        // Create the Lead
        LeadDTO leadDTO = leadMapper.toDto(lead);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeadMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(leadDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAll();
        assertThat(leadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLead() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        int databaseSizeBeforeDelete = leadRepository.findAll().size();

        // Delete the lead
        restLeadMockMvc
            .perform(delete(ENTITY_API_URL_ID, lead.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Lead> leadList = leadRepository.findAll();
        assertThat(leadList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
