package br.com.roberth.leadsmaria.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListaMapperTest {

    private ListaMapper listaMapper;

    @BeforeEach
    public void setUp() {
        listaMapper = new ListaMapperImpl();
    }
}
