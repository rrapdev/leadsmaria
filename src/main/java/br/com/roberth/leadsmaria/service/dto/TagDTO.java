package br.com.roberth.leadsmaria.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.roberth.leadsmaria.domain.Tag} entity.
 */
public class TagDTO implements Serializable {

    private Long id;

    private String nomeTag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeTag() {
        return nomeTag;
    }

    public void setNomeTag(String nomeTag) {
        this.nomeTag = nomeTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagDTO)) {
            return false;
        }

        TagDTO tagDTO = (TagDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tagDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TagDTO{" +
            "id=" + getId() +
            ", nomeTag='" + getNomeTag() + "'" +
            "}";
    }
}
