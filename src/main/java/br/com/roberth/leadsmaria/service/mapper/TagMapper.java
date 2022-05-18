package br.com.roberth.leadsmaria.service.mapper;

import br.com.roberth.leadsmaria.domain.Tag;
import br.com.roberth.leadsmaria.service.dto.TagDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tag} and its DTO {@link TagDTO}.
 */
@Mapper(componentModel = "spring")
public interface TagMapper extends EntityMapper<TagDTO, Tag> {}
