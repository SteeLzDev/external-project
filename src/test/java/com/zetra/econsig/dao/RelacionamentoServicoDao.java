package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.RelacionamentoServico;

public interface RelacionamentoServicoDao extends JpaRepository<RelacionamentoServico, String> {

    Long removeBySvcCodigoOrigem(String svcCodigoOrigem);

    Long removeBySvcCodigoDestino(String svcCodigoDestino);

	List<RelacionamentoServico> findByTntCodigo(@Param("tnt_codigo") String tntCodigo);

	RelacionamentoServico findBySvcCodigoOrigemAndSvcCodigoDestinoAndTntCodigo(
			@Param("svc_codigo_origem") String svcCodigoOrigem, @Param("svc_codigo_destino") String svcCodigoDestino,
			@Param("tnt_codigo") String tntCodigo);
}
