package br.org.rh.repository;

import br.org.rh.domain.Locacao;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Locacao entity.
 */
@SuppressWarnings("unused")
public interface LocacaoRepository extends JpaRepository<Locacao,Long> {

}
