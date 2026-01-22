package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;

public interface OcorrenciaAutorizacaoDao extends JpaRepository<OcorrenciaAutorizacao, String> {

    Long removeByAdeCodigo(String adeCodigo);

	@Query(value = "SELECT oca.* from tb_ocorrencia_autorizacao oca "
			+ "WHERE oca.toc_codigo = ?1 "
			+ "AND oca.USU_CODIGO = ?2 ORDER BY oca.oca_data ASC",
	    nativeQuery = true
	)
	List<OcorrenciaAutorizacao> findByTocCodigoAndUsuCodigo(@Param("toc_codigo") String tocCodigo, @Param("usu_codigo") String usuCodigo);

	OcorrenciaAutorizacao findByTocCodigoAndAdeCodigo(@Param("toc_codigo") String tocCodigo, @Param("ade_codigo") String adeCodigo);

	OcorrenciaAutorizacao findByTocCodigoAndAdeCodigoAndOcaObs(@Param("toc_codigo") String tocCodigo, @Param("ade_codigo") String adeCodigo,
			@Param("oca_obs") String ocaObs);

	List<OcorrenciaAutorizacao> findByTocCodigo(@Param("toc_codigo") String tocCodigo);

	List<OcorrenciaAutorizacao> findByAdeCodigo(@Param("ade_codigo") String adeCodigo);
}
