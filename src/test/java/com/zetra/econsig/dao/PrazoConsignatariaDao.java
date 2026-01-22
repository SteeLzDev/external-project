package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.PrazoConsignataria;

public interface PrazoConsignatariaDao extends JpaRepository<PrazoConsignataria, String> {

    Long removeByPrzCodigo(String przCodigo);

	List<PrazoConsignataria> findByPrzCodigo(@Param("prz_codigo") String przCodigo);

	@Query(value =
	        "SELECT pzc.* " +
            " FROM tb_prazo_consignataria pzc" +
			" INNER JOIN tb_prazo prz ON pzc.prz_codigo = prz.prz_codigo" +
			" WHERE prz.svc_codigo = ?1" +
			" AND pzc.csa_codigo = ?2" +
			" AND prz.prz_vlr = ?3",
			nativeQuery = true)
	PrazoConsignataria getPrzCsaCodigoBySvcCodigoAndCsaCodigo(String svcCodigo, String csaCodigo, Short przVlr);
}
