package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.VerbaConvenio;

public interface VerbaConvenioDao extends JpaRepository<VerbaConvenio, String> {

    Long removeByCnvCodigo(String cnvCodigo);

	VerbaConvenio findByCnvCodigo(@Param("cnv_codigo") String cnvCodigo);
}
