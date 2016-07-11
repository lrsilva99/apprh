package br.org.rh.repository;

import br.org.rh.domain.Escolaridade;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Escolaridade entity.
 */
@SuppressWarnings("unused")
public interface EscolaridadeRepository extends JpaRepository<Escolaridade,Long> {

}
