package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.OcorrenciaDadosAde;

public interface OcorrenciaDadosAdeDao extends JpaRepository<OcorrenciaDadosAde, String> {

    Long removeByAdeCodigo(String adeCodigo);
}
