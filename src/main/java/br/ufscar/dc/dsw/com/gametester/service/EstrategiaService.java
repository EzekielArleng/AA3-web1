// src/main/java/com/gametester/service/EstrategiaService.java
package br.ufscar.dc.dsw.com.gametester.service;

import br.ufscar.dc.dsw.com.gametester.model.Estrategia;
import br.ufscar.dc.dsw.com.gametester.repository.EstrategiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class EstrategiaService {

    private final EstrategiaRepository estrategiaRepository;

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