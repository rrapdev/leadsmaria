package br.com.roberth.leadsmaria.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.roberth.leadsmaria.domain.Lista} entity.
 */
public class ListaDTO implements Serializable {

    private Long id;

    private String nomeLista;

    private LocalDate dataCadastro;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeLista() {
        return nomeLista;
    }

    public void setNomeLista(String nomeLista) {
        this.nomeLista = nomeLista;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ListaDTO)) {
            return false;
        }

        ListaDTO listaDTO = (ListaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, listaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ListaDTO{" +
            "id=" + getId() +
            ", nomeLista='" + getNomeLista() + "'" +
            ", dataCadastro='" + getDataCadastro() + "'" +
            "}";
    }
}
