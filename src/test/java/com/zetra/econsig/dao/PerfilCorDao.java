package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.PerfilCor;

public interface PerfilCorDao extends JpaRepository<PerfilCor, String> {

	PerfilCor findByPerCodigo(@Param("perCodigo") String perCodigo);	
}
