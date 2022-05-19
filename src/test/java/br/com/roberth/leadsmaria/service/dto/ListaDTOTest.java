package br.com.roberth.leadsmaria.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.roberth.leadsmaria.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ListaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ListaDTO.class);
        ListaDTO listaDTO1 = new ListaDTO();
        listaDTO1.setId(1L);
        ListaDTO listaDTO2 = new ListaDTO();
        assertThat(listaDTO1).isNotEqualTo(listaDTO2);
        listaDTO2.setId(listaDTO1.getId());
        assertThat(listaDTO1).isEqualTo(listaDTO2);
        listaDTO2.setId(2L);
        assertThat(listaDTO1).isNotEqualTo(listaDTO2);
        listaDTO1.setId(null);
        assertThat(listaDTO1).isNotEqualTo(listaDTO2);
    }
}
