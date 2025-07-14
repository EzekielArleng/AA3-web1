package br.ufscar.dc.dsw.com.gametester.service;// src/main/java/.../service/DashboardService.java

import br.ufscar.dc.dsw.com.gametester.dto.DashboardStatsDTO;
import br.ufscar.dc.dsw.com.gametester.repository.ProjetoRepository;
import br.ufscar.dc.dsw.com.gametester.repository.SessaoTesteRepository;
import br.ufscar.dc.dsw.com.gametester.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.LocalDateTime;

@Service
public class DashboardService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProjetoRepository projetoRepository;
    @Autowired
    private SessaoTesteRepository sessaoTesteRepository;

    public DashboardStatsDTO getStats() {
        long totalUsuarios = usuarioRepository.count();
        long totalProjetos = projetoRepository.count();

        // Lógica para calcular o início e o fim do mês atual
        YearMonth mesAtual = YearMonth.now();
        LocalDateTime inicioDoMes = mesAtual.atDay(1).atStartOfDay();
        LocalDateTime fimDoMes = mesAtual.atEndOfMonth().atTime(23, 59, 59);

        // Chama o novo método do repositório com as datas calculadas
        long sessoesEsteMes = sessaoTesteRepository.countSessoesNoPeriodo(inicioDoMes, fimDoMes);

        return new DashboardStatsDTO(totalUsuarios, totalProjetos, sessoesEsteMes);
    }
}