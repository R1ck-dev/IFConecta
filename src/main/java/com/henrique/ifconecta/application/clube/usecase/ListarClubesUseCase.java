package com.henrique.ifconecta.application.clube.usecase;

import java.util.stream.Collectors;
import java.util.List;
import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.clube.dto.ClubeResumoDTO;
import com.henrique.ifconecta.domain.clube.enums.StatusMembro;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;
import com.henrique.ifconecta.domain.shared.Pagina;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarClubesUseCase {

    private final ClubeRepository clubeRepository;

    @Transactional
    public Pagina<ClubeResumoDTO> execute(int pagina, int tamanho) {
        var paginaDeClubes = clubeRepository.listarTodosAtivos(pagina, tamanho);

        List<ClubeResumoDTO> resumos = paginaDeClubes.itens().stream()
                .map(clube -> {
                    int qtdMembros = (int) clube.getMembros().stream()
                            .filter(member -> member.getStatus() == StatusMembro.APROVADO)
                            .count();

                    return new ClubeResumoDTO(
                            clube.getId(),
                            clube.getNome(),
                            clube.getDescricao(),
                            clube.getTipoAcesso(),
                            qtdMembros);
                })
                .collect(Collectors.toList());

        return new Pagina<>(
                resumos,
                paginaDeClubes.paginaAtual(),
                paginaDeClubes.totalPaginas(),
                paginaDeClubes.totalItens());
    }
}
