package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.FuncaoPerfilSup;

public interface FuncaoPerfilSupDao extends JpaRepository<FuncaoPerfilSup, String> {

	List<FuncaoPerfilSup> findByFunCodigo(@Param("fun_codigo") String funCodigo);

	FuncaoPerfilSup findByFunCodigoAndCseCodigoAndUsuCodigo(@Param("fun_codigo") String funCodigo, @Param("cse_codigo") String cseCodigo, @Param("usu_codigo") String usuCodigo);
}