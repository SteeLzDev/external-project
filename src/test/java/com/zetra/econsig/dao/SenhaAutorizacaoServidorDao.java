package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.SenhaAutorizacaoServidor;
import com.zetra.econsig.persistence.entity.SenhaAutorizacaoServidorId;

public interface SenhaAutorizacaoServidorDao extends JpaRepository<SenhaAutorizacaoServidor, SenhaAutorizacaoServidorId> {

    Long removeByUsuCodigo(String usuCodigo);
}
