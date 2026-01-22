package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.PapelFuncao;
import com.zetra.econsig.persistence.entity.PapelFuncaoId;

public interface PapelFuncaoDao extends JpaRepository<PapelFuncao, PapelFuncaoId> {

}
