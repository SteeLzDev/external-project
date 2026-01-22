package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zetra.econsig.persistence.entity.HistoricoMargemFolha;
import com.zetra.econsig.persistence.entity.HistoricoMargemFolhaId;

public interface HistoricoMargemFolhaDao extends JpaRepository<HistoricoMargemFolha, HistoricoMargemFolhaId> {

	@Query(value = "SELECT hmf.* FROM tb_historico_margem_folha hmf " +
			" WHERE hmf.mar_codigo = ?1", nativeQuery = true)
	public List<HistoricoMargemFolha> findByMarCodigo(String marCodigo);

}
