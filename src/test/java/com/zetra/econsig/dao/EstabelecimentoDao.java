package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Estabelecimento;

public interface EstabelecimentoDao extends JpaRepository<Estabelecimento, String> {

    Estabelecimento findByEstIdentificador(@Param("est_identificador") String estIdentificador);
}
