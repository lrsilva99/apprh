package br.org.rh.repository;

import br.org.rh.domain.Instituicao;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Instituicao entity.
 */
@SuppressWarnings("unused")
public interface InstituicaoRepository extends JpaRepository<Instituicao,Long> {

}
