package br.ufscar.dc.dsw.com.gametester.repository;

import br.ufscar.dc.dsw.com.gametester.model.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BugRepository extends JpaRepository<Bug, Integer> {
    List<Bug> findBySessaoTesteIdOrderByDataRegistroAsc(Integer id);
}
