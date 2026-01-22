package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.DadosAutorizacaoDesconto;

public interface DadosAutorizacaoDescontoDao extends JpaRepository<DadosAutorizacaoDesconto, String> {

    Long removeByAdeCodigo(String adeCodigo);

	DadosAutorizacaoDesconto findByAdeCodigoAndTdaCodigo(@Param("ade_codigo") String adeCodigo, @Param("tda_codigo") String tdaCodigo);
}
