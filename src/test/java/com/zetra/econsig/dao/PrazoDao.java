package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Prazo;

public interface PrazoDao extends JpaRepository<Prazo, String> {

    Long removeBySvcCodigo(String svcCodigo);

	List<Prazo> findBySvcCodigo(@Param("svc_codigo") String svcCodigo);

	Prazo findBySvcCodigoAndPrzVlr(@Param("svc_codigo") String svcCodigo, @Param("prz_vlr") Short przVlr);

}
