package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.PerfilSup;

public interface PerfilSupDao extends JpaRepository<PerfilSup, String> {

	PerfilSup findByPerCodigo(@Param("perCodigo") String perCodigo);	
}
