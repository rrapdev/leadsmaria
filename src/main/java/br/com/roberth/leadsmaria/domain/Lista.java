package br.com.roberth.leadsmaria.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Lista.
 */
@Entity
@Table(name = "lista")
public class Lista implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "nome_lista")
    private String nomeLista;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @ManyToMany(mappedBy = "listas")
    @JsonIgnoreProperties(value = { "tags", "listas" }, allowSetters = true)
    private Set<Lead> leads = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Lista id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeLista() {
        return this.nomeLista;
    }

    public Lista nomeLista(String nomeLista) {
        this.setNomeLista(nomeLista);
        return this;
    }

    public void setNomeLista(String nomeLista) {
        this.nomeLista = nomeLista;
    }

    public LocalDate getDataCadastro() {
        return this.dataCadastro;
    }

    public Lista dataCadastro(LocalDate dataCadastro) {
        this.setDataCadastro(dataCadastro);
        return this;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Set<Lead> getLeads() {
        return this.leads;
    }

    public void setLeads(Set<Lead> leads) {
        if (this.leads != null) {
            this.leads.forEach(i -> i.removeLista(this));
        }
        if (leads != null) {
            leads.forEach(i -> i.addLista(this));
        }
        this.leads = leads;
    }

    public Lista leads(Set<Lead> leads) {
        this.setLeads(leads);
        return this;
    }

    public Lista addLeads(Lead lead) {
        this.leads.add(lead);
        lead.getListas().add(this);
        return this;
    }

    public Lista removeLeads(Lead lead) {
        this.leads.remove(lead);
        lead.getListas().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lista)) {
            return false;
        }
        return id != null && id.equals(((Lista) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Lista{" +
            "id=" + getId() +
            ", nomeLista='" + getNomeLista() + "'" +
            ", dataCadastro='" + getDataCadastro() + "'" +
            "}";
    }
}
