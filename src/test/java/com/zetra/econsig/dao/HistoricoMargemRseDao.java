package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zetra.econsig.persistence.entity.HistoricoMargemRse;

public interface HistoricoMargemRseDao extends JpaRepository<HistoricoMargemRse, Integer> {

    Long removeByRseCodigo(String rseCodigo);

	@Query(value = "SELECT hmr.* FROM tb_historico_margem_rse hmr WHERE hmr.mar_codigo = ?1", nativeQuery = true)
	List<HistoricoMargemRse> findByMarCodigo(String marCodigo);
}
