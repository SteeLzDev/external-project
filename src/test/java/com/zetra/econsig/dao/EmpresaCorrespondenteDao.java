package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.EmpresaCorrespondente;

public interface EmpresaCorrespondenteDao extends JpaRepository<EmpresaCorrespondente, String> {

	EmpresaCorrespondente findByEcoIdentificador(@Param("eco_identificador") String ecoIdentificador);
}
