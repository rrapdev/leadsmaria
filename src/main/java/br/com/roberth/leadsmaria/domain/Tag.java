package br.com.roberth.leadsmaria.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Tag.
 */
@Entity
@Table(name = "tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "nome_tag")
    private String nomeTag;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnoreProperties(value = { "tags", "listas" }, allowSetters = true)
    private Set<Lead> leads = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tag id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeTag() {
        return this.nomeTag;
    }

    public Tag nomeTag(String nomeTag) {
        this.setNomeTag(nomeTag);
        return this;
    }

    public void setNomeTag(String nomeTag) {
        this.nomeTag = nomeTag;
    }

    public Set<Lead> getLeads() {
        return this.leads;
    }

    public void setLeads(Set<Lead> leads) {
        if (this.leads != null) {
            this.leads.forEach(i -> i.removeTags(this));
        }
        if (leads != null) {
            leads.forEach(i -> i.addTags(this));
        }
        this.leads = leads;
    }

    public Tag leads(Set<Lead> leads) {
        this.setLeads(leads);
        return this;
    }

    public Tag addLeads(Lead lead) {
        this.leads.add(lead);
        lead.getTags().add(this);
        return this;
    }

    public Tag removeLeads(Lead lead) {
        this.leads.remove(lead);
        lead.getTags().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        return id != null && id.equals(((Tag) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tag{" +
            "id=" + getId() +
            ", nomeTag='" + getNomeTag() + "'" +
            "}";
    }
}
