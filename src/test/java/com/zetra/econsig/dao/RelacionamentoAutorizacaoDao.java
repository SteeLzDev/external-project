package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;

public interface RelacionamentoAutorizacaoDao extends JpaRepository<RelacionamentoAutorizacao, String> {
	
	List<RelacionamentoAutorizacao> findByTntCodigo(@Param("tnt_codigo") String tntCodigo);
	
	RelacionamentoAutorizacao findByAdeCodigoOrigem(@Param("ADE_CODIGO_ORIGEM") String adeCodigoOrigem);
	
	List<RelacionamentoAutorizacao> findByStcCodigo(@Param("STC_CODIGO") String stcCodigo);
	
	void removeByAdeCodigoOrigem(@Param("ADE_CODIGO_ORIGEM") String adeCodigoOrigem);

}

