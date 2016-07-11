package br.org.rh.repository;

import br.org.rh.domain.Cargo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Cargo entity.
 */
@SuppressWarnings("unused")
public interface CargoRepository extends JpaRepository<Cargo,Long> {

}
