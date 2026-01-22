package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Indice;

public interface IndiceDao extends JpaRepository<Indice, String> {

	Indice findBySvcCodigoAndCsaCodigoAndIndCodigo(@Param("svc_codigo") String svcCodigo,
			@Param("csa_codigo") String csaCodigo, @Param("ind_codigo") String indCodigo);

	List<Indice> findByCsaCodigo(@Param("csa_codigo") String csaCodigo);
}
