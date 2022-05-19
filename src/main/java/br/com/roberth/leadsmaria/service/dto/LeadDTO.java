package br.com.roberth.leadsmaria.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link br.com.roberth.leadsmaria.domain.Lead} entity.
 */
public class LeadDTO implements Serializable {

    private Long id;

    private String nomeLead;

    private String telefone;

    private String email;

    private LocalDate dataNascimento;

    private LocalDate dataCadastro;

    private Set<TagDTO> tags = new HashSet<>();

    private Set<ListaDTO> listas = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeLead() {
        return nomeLead;
    }

    public void setNomeLead(String nomeLead) {
        this.nomeLead = nomeLead;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Set<TagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    public Set<ListaDTO> getListas() {
        return listas;
    }

    public void setListas(Set<ListaDTO> listas) {
        this.listas = listas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LeadDTO)) {
            return false;
        }

        LeadDTO leadDTO = (LeadDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, leadDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LeadDTO{" +
            "id=" + getId() +
            ", nomeLead='" + getNomeLead() + "'" +
            ", telefone='" + getTelefone() + "'" +
            ", email='" + getEmail() + "'" +
            ", dataNascimento='" + getDataNascimento() + "'" +
            ", dataCadastro='" + getDataCadastro() + "'" +
            ", tags=" + getTags() +
            ", listas=" + getListas() +
            "}";
    }
}
