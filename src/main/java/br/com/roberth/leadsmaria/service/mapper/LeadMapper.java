package br.com.roberth.leadsmaria.service.mapper;

import br.com.roberth.leadsmaria.domain.Lead;
import br.com.roberth.leadsmaria.domain.Lista;
import br.com.roberth.leadsmaria.domain.Tag;
import br.com.roberth.leadsmaria.service.dto.LeadDTO;
import br.com.roberth.leadsmaria.service.dto.ListaDTO;
import br.com.roberth.leadsmaria.service.dto.TagDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Lead} and its DTO {@link LeadDTO}.
 */
@Mapper(componentModel = "spring")
public interface LeadMapper extends EntityMapper<LeadDTO, Lead> {
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagNomeTagSet")
    @Mapping(target = "listas", source = "listas", qualifiedByName = "listaNomeListaSet")
    LeadDTO toDto(Lead s);

    @Mapping(target = "removeTags", ignore = true)
    @Mapping(target = "removeLista", ignore = true)
    Lead toEntity(LeadDTO leadDTO);

    @Named("tagNomeTag")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomeTag", source = "nomeTag")
    TagDTO toDtoTagNomeTag(Tag tag);

    @Named("tagNomeTagSet")
    default Set<TagDTO> toDtoTagNomeTagSet(Set<Tag> tag) {
        return tag.stream().map(this::toDtoTagNomeTag).collect(Collectors.toSet());
    }

    @Named("listaNomeLista")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomeLista", source = "nomeLista")
    ListaDTO toDtoListaNomeLista(Lista lista);

    @Named("listaNomeListaSet")
    default Set<ListaDTO> toDtoListaNomeListaSet(Set<Lista> lista) {
        return lista.stream().map(this::toDtoListaNomeLista).collect(Collectors.toSet());
    }
}
