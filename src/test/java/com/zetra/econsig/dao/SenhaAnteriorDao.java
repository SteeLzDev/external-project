package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.SenhaAnterior;
import com.zetra.econsig.persistence.entity.SenhaAnteriorId;

public interface SenhaAnteriorDao extends JpaRepository<SenhaAnterior, SenhaAnteriorId> {

    Long removeByUsuCodigo(String usuCodigo);
}
