package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDesconto;

public interface AnexoAutorizacaoDescontoDao extends JpaRepository<AnexoAutorizacaoDesconto, String> {

	List<AnexoAutorizacaoDesconto> findByAdeCodigo(@Param("ade_codigo") String adeCodigo);	
}
	

