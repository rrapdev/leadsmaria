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
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link br.com.roberth.leadsmaria.domain.Tag} entity. This class is used
 * in {@link br.com.roberth.leadsmaria.web.rest.TagResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tags?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class TagCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomeTag;

    private LongFilter leadsId;

    private Boolean distinct;

    public TagCriteria() {}

    public TagCriteria(TagCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nomeTag = other.nomeTag == null ? null : other.nomeTag.copy();
        this.leadsId = other.leadsId == null ? null : other.leadsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TagCriteria copy() {
        return new TagCriteria(this);
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

    public StringFilter getNomeTag() {
        return nomeTag;
    }

    public StringFilter nomeTag() {
        if (nomeTag == null) {
            nomeTag = new StringFilter();
        }
        return nomeTag;
    }

    public void setNomeTag(StringFilter nomeTag) {
        this.nomeTag = nomeTag;
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
        final TagCriteria that = (TagCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomeTag, that.nomeTag) &&
            Objects.equals(leadsId, that.leadsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomeTag, leadsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TagCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nomeTag != null ? "nomeTag=" + nomeTag + ", " : "") +
            (leadsId != null ? "leadsId=" + leadsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
