package br.com.roberth.leadsmaria.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LeadMapperTest {

    private LeadMapper leadMapper;

    @BeforeEach
    public void setUp() {
        leadMapper = new LeadMapperImpl();
    }
}
