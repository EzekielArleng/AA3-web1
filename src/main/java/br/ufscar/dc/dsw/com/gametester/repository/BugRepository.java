package br.ufscar.dc.dsw.com.gametester.repository;

import br.ufscar.dc.dsw.com.gametester.domain.Bug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BugRepository extends JpaRepository<Bug, Integer> {
    List<Bug> findBySessaoTesteIdOrderByDataRegistroAsc(Integer id);
}
