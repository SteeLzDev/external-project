package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.FuncaoPerfilCor;

public interface FuncaoPerfilCorDao extends JpaRepository<FuncaoPerfilCor, String> {
	
	List<FuncaoPerfilCor> findByFunCodigo(@Param("fun_codigo") String funCodigo);
	
	FuncaoPerfilCor findByFunCodigoAndCorCodigo(@Param("fun_codigo") String funCodigo, @Param("cor_codigo") String corCodigo);
}