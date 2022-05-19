package br.com.roberth.leadsmaria.service.mapper;

import br.com.roberth.leadsmaria.domain.Lista;
import br.com.roberth.leadsmaria.service.dto.ListaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Lista} and its DTO {@link ListaDTO}.
 */
@Mapper(componentModel = "spring")
public interface ListaMapper extends EntityMapper<ListaDTO, Lista> {}
