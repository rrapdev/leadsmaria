package br.com.roberth.leadsmaria.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link br.com.roberth.leadsmaria.domain.Lead} entity. This class is used
 * in {@link br.com.roberth.leadsmaria.web.rest.LeadResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /leads?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class LeadCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomeLead;

    private StringFilter telefone;

    private StringFilter email;

    private LocalDateFilter dataNascimento;

    private LocalDateFilter dataCadastro;

    private LongFilter tagsId;

    private LongFilter listaId;

    private Boolean distinct;

    public LeadCriteria() {}

    public LeadCriteria(LeadCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nomeLead = other.nomeLead == null ? null : other.nomeLead.copy();
        this.telefone = other.telefone == null ? null : other.telefone.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.dataNascimento = other.dataNascimento == null ? null : other.dataNascimento.copy();
        this.dataCadastro = other.dataCadastro == null ? null : other.dataCadastro.copy();
        this.tagsId = other.tagsId == null ? null : other.tagsId.copy();
        this.listaId = other.listaId == null ? null : other.listaId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public LeadCriteria copy() {
        return new LeadCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNomeLead() {
        return nomeLead;
    }

    public StringFilter nomeLead() {
        if (nomeLead == null) {
            nomeLead = new StringFilter();
        }
        return nomeLead;
    }

    public void setNomeLead(StringFilter nomeLead) {
        this.nomeLead = nomeLead;
    }

    public StringFilter getTelefone() {
        return telefone;
    }

    public StringFilter telefone() {
        if (telefone == null) {
            telefone = new StringFilter();
        }
        return telefone;
    }

    public void setTelefone(StringFilter telefone) {
        this.telefone = telefone;
    }

    public StringFilter getEmail() {
        return email;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public LocalDateFilter getDataNascimento() {
        return dataNascimento;
    }

    public LocalDateFilter dataNascimento() {
        if (dataNascimento == null) {
            dataNascimento = new LocalDateFilter();
        }
        return dataNascimento;
    }

    public void setDataNascimento(LocalDateFilter dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDateFilter getDataCadastro() {
        return dataCadastro;
    }

    public LocalDateFilter dataCadastro() {
        if (dataCadastro == null) {
            dataCadastro = new LocalDateFilter();
        }
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateFilter dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LongFilter getTagsId() {
        return tagsId;
    }

    public LongFilter tagsId() {
        if (tagsId == null) {
            tagsId = new LongFilter();
        }
        return tagsId;
    }

    public void setTagsId(LongFilter tagsId) {
        this.tagsId = tagsId;
    }

    public LongFilter getListaId() {
        return listaId;
    }

    public LongFilter listaId() {
        if (listaId == null) {
            listaId = new LongFilter();
        }
        return listaId;
    }

    public void setListaId(LongFilter listaId) {
        this.listaId = listaId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LeadCriteria that = (LeadCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomeLead, that.nomeLead) &&
            Objects.equals(telefone, that.telefone) &&
            Objects.equals(email, that.email) &&
            Objects.equals(dataNascimento, that.dataNascimento) &&
            Objects.equals(dataCadastro, that.dataCadastro) &&
            Objects.equals(tagsId, that.tagsId) &&
            Objects.equals(listaId, that.listaId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomeLead, telefone, email, dataNascimento, dataCadastro, tagsId, listaId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LeadCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nomeLead != null ? "nomeLead=" + nomeLead + ", " : "") +
            (telefone != null ? "telefone=" + telefone + ", " : "") +
            (email != null ? "email=" + email + ", " : "") +
            (dataNascimento != null ? "dataNascimento=" + dataNascimento + ", " : "") +
            (dataCadastro != null ? "dataCadastro=" + dataCadastro + ", " : "") +
            (tagsId != null ? "tagsId=" + tagsId + ", " : "") +
            (listaId != null ? "listaId=" + listaId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
