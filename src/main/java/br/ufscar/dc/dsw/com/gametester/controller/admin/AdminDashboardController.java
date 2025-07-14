package br.ufscar.dc.dsw.com.gametester.controller.admin;

import br.ufscar.dc.dsw.com.gametester.dto.DashboardStatsDTO;
import br.ufscar.dc.dsw.com.gametester.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private DashboardService dashboardService; // Injeta o serviço que acabamos de criar

    /**
     * Endpoint que retorna as estatísticas para o dashboard administrativo.
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        // Chama o método do serviço para obter os dados
        DashboardStatsDTO stats = dashboardService.getStats();

        // Retorna os dados como JSON com um status 200 OK
        return ResponseEntity.ok(stats);
    }
}