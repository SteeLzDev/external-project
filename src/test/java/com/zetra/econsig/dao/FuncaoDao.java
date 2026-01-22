package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Funcao;

public interface FuncaoDao extends JpaRepository<Funcao, String> {
	
	Funcao findByFunCodigo(@Param("fun_codigo") String funCodigo);	
}