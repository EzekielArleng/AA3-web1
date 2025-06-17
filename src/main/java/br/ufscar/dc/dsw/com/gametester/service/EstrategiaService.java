// src/main/java/com/gametester/service/EstrategiaService.java
package br.ufscar.dc.dsw.com.gametester.service;

import br.ufscar.dc.dsw.com.gametester.dto.EstrategiaDTO;
import br.ufscar.dc.dsw.com.gametester.domain.Estrategia;
import br.ufscar.dc.dsw.com.gametester.repository.EstrategiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class EstrategiaService {

    @Autowired
    private final EstrategiaRepository estrategiaRepository;

    public Estrategia criarEstrategia(EstrategiaDTO dto) {
        Estrategia estrategia = new Estrategia();
        // Mapeia os campos básicos
        estrategia.setNome(dto.nome());
        estrategia.setDescricao(dto.descricao());
        estrategia.setDicas(dto.dicas());

        // Lógica para combinar exemplos e imagem que estava no servlet
        String exemplosFinais = (dto.exemplos() != null) ? dto.exemplos().trim() : "";
        if (dto.imagemPath() != null && !dto.imagemPath().trim().isEmpty()) {
            if (!exemplosFinais.isEmpty()) {
                exemplosFinais += "\n\n";
            }
            exemplosFinais += "[Imagem: " + dto.imagemPath().trim() + "]";
        }
        estrategia.setExemplos(exemplosFinais.isEmpty() ? null : exemplosFinais);

        return estrategiaRepository.save(estrategia);
    }

    public Estrategia editarEstrategia(EstrategiaDTO dto) {
        Estrategia estrategia = buscarPorId(dto.id());
        estrategia.setNome(dto.nome());
        estrategia.setDescricao(dto.descricao());
        estrategia.setDicas(dto.dicas());
        estrategia.setExemplos(dto.exemplos()); // Na edição, o campo exemplos já vem completo

        return estrategiaRepository.save(estrategia);
    }

    public void excluirEstrategia(Integer id) {
        if (!estrategiaRepository.existsById(id)) {
            throw new RuntimeException("Estratégia não encontrada para exclusão. ID: " + id);
        }
        try {
            estrategiaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // Captura o erro de chave estrangeira
            throw new RuntimeException("Não é possível excluir a estratégia, pois ela está em uso por uma ou mais sessões de teste.");
        }
    }

    @Autowired
    public EstrategiaService(EstrategiaRepository estrategiaRepository) {
        this.estrategiaRepository = estrategiaRepository;
    }

    @Transactional(readOnly = true)
    public List<Estrategia> listarTodas() {
        return estrategiaRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Estrategia buscarPorId(Integer id) {
        return estrategiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estratégia não encontrada para o ID: " + id));
    }

    public Estrategia salvar(Estrategia estrategia) {
        // Validação para não permitir nomes duplicados
        estrategiaRepository.findByNomeIgnoreCase(estrategia.getNome())
                .ifPresent(existente -> {
                    if (!Objects.equals(existente.getId(), estrategia.getId())) {
                        throw new IllegalStateException("Já existe uma estratégia com o nome: " + estrategia.getNome());
                    }
                });
        return estrategiaRepository.save(estrategia);
    }

    public void deletar(Integer id) {
        if (!estrategiaRepository.existsById(id)) {
            throw new RuntimeException("Estratégia não encontrada para o ID: " + id);
        }
        estrategiaRepository.deleteById(id);
    }
}