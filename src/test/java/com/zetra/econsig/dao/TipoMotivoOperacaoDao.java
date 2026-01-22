package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.TipoMotivoOperacao;

public interface TipoMotivoOperacaoDao extends JpaRepository<TipoMotivoOperacao, String> {

}
