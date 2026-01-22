package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.SaldoDevedor;


public interface SaldoDevedorDao extends JpaRepository<SaldoDevedor, String> {
	
	SaldoDevedor findByAdeCodigo(@Param("ade_codigo") String adeCodigo);
	
	void removeByAdeCodigo(@Param("ade_codigo") String adeCodigo);
}
