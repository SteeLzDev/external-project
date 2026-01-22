package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.PerfilCse;

public interface PerfilCseDao extends JpaRepository<PerfilCse, String> {

	PerfilCse findByPerCodigo(@Param("perCodigo") String perCodigo);	
}
