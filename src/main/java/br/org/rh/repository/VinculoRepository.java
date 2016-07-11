package br.org.rh.repository;

import br.org.rh.domain.Vinculo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Vinculo entity.
 */
@SuppressWarnings("unused")
public interface VinculoRepository extends JpaRepository<Vinculo,Long> {

}
