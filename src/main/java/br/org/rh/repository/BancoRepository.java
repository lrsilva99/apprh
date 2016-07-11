package br.org.rh.repository;

import br.org.rh.domain.Banco;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Banco entity.
 */
@SuppressWarnings("unused")
public interface BancoRepository extends JpaRepository<Banco,Long> {

}
