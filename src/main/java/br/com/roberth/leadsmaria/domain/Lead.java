package br.com.roberth.leadsmaria.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Lead.
 */
@Entity
@Table(name = "jhi_lead")
public class Lead implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "nome_lead")
    private String nomeLead;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "email")
    private String email;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @ManyToMany
    @JoinTable(
        name = "rel_jhi_lead__tags",
        joinColumns = @JoinColumn(name = "jhi_lead_id"),
        inverseJoinColumns = @JoinColumn(name = "tags_id")
    )
    @JsonIgnoreProperties(value = { "leads" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_jhi_lead__lista",
        joinColumns = @JoinColumn(name = "jhi_lead_id"),
        inverseJoinColumns = @JoinColumn(name = "lista_id")
    )
    @JsonIgnoreProperties(value = { "leads" }, allowSetters = true)
    private Set<Lista> listas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Lead id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeLead() {
        return this.nomeLead;
    }

    public Lead nomeLead(String nomeLead) {
        this.setNomeLead(nomeLead);
        return this;
    }

    public void setNomeLead(String nomeLead) {
        this.nomeLead = nomeLead;
    }

    public String getTelefone() {
        return this.telefone;
    }

    public Lead telefone(String telefone) {
        this.setTelefone(telefone);
        return this;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return this.email;
    }

    public Lead email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return this.dataNascimento;
    }

    public Lead dataNascimento(LocalDate dataNascimento) {
        this.setDataNascimento(dataNascimento);
        return this;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDate getDataCadastro() {
        return this.dataCadastro;
    }

    public Lead dataCadastro(LocalDate dataCadastro) {
        this.setDataCadastro(dataCadastro);
        return this;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Lead tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Lead addTags(Tag tag) {
        this.tags.add(tag);
        tag.getLeads().add(this);
        return this;
    }

    public Lead removeTags(Tag tag) {
        this.tags.remove(tag);
        tag.getLeads().remove(this);
        return this;
    }

    public Set<Lista> getListas() {
        return this.listas;
    }

    public void setListas(Set<Lista> listas) {
        this.listas = listas;
    }

    public Lead listas(Set<Lista> listas) {
        this.setListas(listas);
        return this;
    }

    public Lead addLista(Lista lista) {
        this.listas.add(lista);
        lista.getLeads().add(this);
        return this;
    }

    public Lead removeLista(Lista lista) {
        this.listas.remove(lista);
        lista.getLeads().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lead)) {
            return false;
        }
        return id != null && id.equals(((Lead) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Lead{" +
            "id=" + getId() +
            ", nomeLead='" + getNomeLead() + "'" +
            ", telefone='" + getTelefone() + "'" +
            ", email='" + getEmail() + "'" +
            ", dataNascimento='" + getDataNascimento() + "'" +
            ", dataCadastro='" + getDataCadastro() + "'" +
            "}";
    }
}
