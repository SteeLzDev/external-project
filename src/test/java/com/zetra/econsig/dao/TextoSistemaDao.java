package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.TextoSistema;

public interface TextoSistemaDao extends JpaRepository<TextoSistema, String> {

	public TextoSistema findByTexChave(@Param("TEX_CHAVE") String texChave);
	
}