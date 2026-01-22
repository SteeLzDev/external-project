package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.HistoricoStatusAde;
import com.zetra.econsig.persistence.entity.HistoricoStatusAdeId;

public interface HistoricoStatusAdeDao extends JpaRepository<HistoricoStatusAde, HistoricoStatusAdeId> {

    Long removeByAdeCodigo(String adeCodigo);

	List<HistoricoStatusAde> findByAdeCodigo(@Param("ADE_CODIGO") String adeCodigo);

}
