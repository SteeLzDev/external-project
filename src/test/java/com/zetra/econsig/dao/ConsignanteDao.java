package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Consignante;

public interface ConsignanteDao extends JpaRepository<Consignante, String> {
	
	Consignante findByCseCodigo(@Param("cse_codigo") String cseCodigo);
}
