package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.LeituraMensagemUsuario;
import com.zetra.econsig.persistence.entity.LeituraMensagemUsuarioId;

public interface LeituraMensagemUsuarioDao extends JpaRepository<LeituraMensagemUsuario, LeituraMensagemUsuarioId> {
	
	LeituraMensagemUsuario findByMenCodigoAndUsuCodigo(@Param("menCodigo") String menCodigo, @Param("usuCodigo") String usuCodigo);
	
}