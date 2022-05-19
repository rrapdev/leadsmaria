package br.com.roberth.leadsmaria.service.impl;

import br.com.roberth.leadsmaria.domain.Lista;
import br.com.roberth.leadsmaria.repository.ListaRepository;
import br.com.roberth.leadsmaria.service.ListaService;
import br.com.roberth.leadsmaria.service.dto.ListaDTO;
import br.com.roberth.leadsmaria.service.mapper.ListaMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Lista}.
 */
@Service
@Transactional
public class ListaServiceImpl implements ListaService {

    private final Logger log = LoggerFactory.getLogger(ListaServiceImpl.class);

    private final ListaRepository listaRepository;

    private final ListaMapper listaMapper;

    public ListaServiceImpl(ListaRepository listaRepository, ListaMapper listaMapper) {
        this.listaRepository = listaRepository;
        this.listaMapper = listaMapper;
    }

    @Override
    public ListaDTO save(ListaDTO listaDTO) {
        log.debug("Request to save Lista : {}", listaDTO);
        Lista lista = listaMapper.toEntity(listaDTO);
        lista = listaRepository.save(lista);
        return listaMapper.toDto(lista);
    }

    @Override
    public ListaDTO update(ListaDTO listaDTO) {
        log.debug("Request to save Lista : {}", listaDTO);
        Lista lista = listaMapper.toEntity(listaDTO);
        lista = listaRepository.save(lista);
        return listaMapper.toDto(lista);
    }

    @Override
    public Optional<ListaDTO> partialUpdate(ListaDTO listaDTO) {
        log.debug("Request to partially update Lista : {}", listaDTO);

        return listaRepository
            .findById(listaDTO.getId())
            .map(existingLista -> {
                listaMapper.partialUpdate(existingLista, listaDTO);

                return existingLista;
            })
            .map(listaRepository::save)
            .map(listaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ListaDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Listas");
        return listaRepository.findAll(pageable).map(listaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ListaDTO> findOne(Long id) {
        log.debug("Request to get Lista : {}", id);
        return listaRepository.findById(id).map(listaMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Lista : {}", id);
        listaRepository.deleteById(id);
    }
}
