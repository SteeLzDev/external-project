package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.AcessoRecurso;

public interface AcessoRecursoDao extends JpaRepository<AcessoRecurso, String> {
	
	@Query(value = "SELECT acr.* FROM tb_acesso_recurso acr"
				+ " INNER JOIN tb_item_menu itm ON "
					+ "acr.itm_codigo = itm.itm_codigo"
			+ " WHERE acr.itm_codigo = :itmCodigo AND (acr.pap_codigo = :papCodigo OR acr.pap_codigo IS NULL)"
			+ " LIMIT 1",
		nativeQuery = true
	)
	AcessoRecurso getItemMenu(@Param("itmCodigo") String itmCodigo, @Param("papCodigo") String papCodigo);
	
}