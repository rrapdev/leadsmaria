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
 * Criteria class for the {@link br.com.roberth.leadsmaria.domain.Lista} entity. This class is used
 * in {@link br.com.roberth.leadsmaria.web.rest.ListaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /listas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ListaCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomeLista;

    private LocalDateFilter dataCadastro;

    private LongFilter leadsId;

    private Boolean distinct;

    public ListaCriteria() {}

    public ListaCriteria(ListaCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nomeLista = other.nomeLista == null ? null : other.nomeLista.copy();
        this.dataCadastro = other.dataCadastro == null ? null : other.dataCadastro.copy();
        this.leadsId = other.leadsId == null ? null : other.leadsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ListaCriteria copy() {
        return new ListaCriteria(this);
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

    public StringFilter getNomeLista() {
        return nomeLista;
    }

    public StringFilter nomeLista() {
        if (nomeLista == null) {
            nomeLista = new StringFilter();
        }
        return nomeLista;
    }

    public void setNomeLista(StringFilter nomeLista) {
        this.nomeLista = nomeLista;
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

    public LongFilter getLeadsId() {
        return leadsId;
    }

    public LongFilter leadsId() {
        if (leadsId == null) {
            leadsId = new LongFilter();
        }
        return leadsId;
    }

    public void setLeadsId(LongFilter leadsId) {
        this.leadsId = leadsId;
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
        final ListaCriteria that = (ListaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomeLista, that.nomeLista) &&
            Objects.equals(dataCadastro, that.dataCadastro) &&
            Objects.equals(leadsId, that.leadsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomeLista, dataCadastro, leadsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ListaCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nomeLista != null ? "nomeLista=" + nomeLista + ", " : "") +
            (dataCadastro != null ? "dataCadastro=" + dataCadastro + ", " : "") +
            (leadsId != null ? "leadsId=" + leadsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
