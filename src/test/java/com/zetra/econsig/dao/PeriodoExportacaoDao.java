package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.PeriodoExportacao;

public interface PeriodoExportacaoDao extends JpaRepository<PeriodoExportacao, String> {

	@Modifying
	@Query(value = "update tb_periodo_exportacao pex set pex.pex_periodo = :pexPeriodo,"
			+ " pex.pex_data_ini = :pexDataIni,"
			+ " pex.pex_data_fim = :pexDataFim", nativeQuery = true)
	public void updateTodosPeriodosExportacaoDatas(@Param("pexPeriodo") String pexPeriodo, @Param("pexDataIni") String pexDataIni, @Param("pexDataFim") String pexDataFim);
	
	
	PeriodoExportacao findByOrgCodigo(@Param("org_codigo") String orgCodigo);

}
