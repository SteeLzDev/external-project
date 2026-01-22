package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.PerfilCsa;

public interface PerfilCsaDao extends JpaRepository<PerfilCsa, String> {

	PerfilCsa findByPerCodigo(@Param("perCodigo") String perCodigo);	
}
