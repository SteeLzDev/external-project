package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.CoeficienteAtivo;

public interface CoeficienteAtivoDao extends JpaRepository<CoeficienteAtivo, String> {

    Long removeByPrzCsaCodigo(String przCsaCodigo);

    List<CoeficienteAtivo> findByPrzCsaCodigo(@Param("prz_csa_codigo") String przCsaCodigo);
}
