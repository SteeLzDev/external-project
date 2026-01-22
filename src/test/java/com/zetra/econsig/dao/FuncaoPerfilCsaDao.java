package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.FuncaoPerfilCsa;

public interface FuncaoPerfilCsaDao extends JpaRepository<FuncaoPerfilCsa, String> {

	List<FuncaoPerfilCsa> findByFunCodigo(@Param("fun_codigo") String funCodigo);

	FuncaoPerfilCsa findByFunCodigoAndCsaCodigoAndUsuCodigo(@Param("fun_codigo") String funCodigo, @Param("csa_codigo") String csaCodigo, @Param("usu_codigo") String usuCodigo);
}