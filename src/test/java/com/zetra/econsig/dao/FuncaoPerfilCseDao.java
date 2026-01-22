package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.FuncaoPerfilCse;

public interface FuncaoPerfilCseDao extends JpaRepository<FuncaoPerfilCse, String> {
	
	FuncaoPerfilCse findByFunCodigoAndUsuCodigo(@Param("fun_codigo") String funCodigo, @Param("usu_codigo") String usuCodigo);

}
