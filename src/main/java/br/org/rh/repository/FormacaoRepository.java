package br.org.rh.repository;

import br.org.rh.domain.Formacao;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Formacao entity.
 */
@SuppressWarnings("unused")
public interface FormacaoRepository extends JpaRepository<Formacao,Long> {

}
