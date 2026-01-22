package com.zetra.econsig.service;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.RelacionamentoServicoDao;
import com.zetra.econsig.persistence.entity.RelacionamentoServico;

@Service
public class RelacionamentoServicoService {

	@Autowired
	private RelacionamentoServicoDao relacionamentoServicoDao;

	public void incluirRelacionamentoServico(String svcCodigoOrigem, String svcCodigoDestino, String tntCodigo) {
		RelacionamentoServico relacionamentoServico = relacionamentoServicoDao
				.findBySvcCodigoOrigemAndSvcCodigoDestinoAndTntCodigo(svcCodigoOrigem, svcCodigoDestino, tntCodigo);

		if (relacionamentoServico == null) {
			relacionamentoServico = new RelacionamentoServico();
			relacionamentoServico.setRelSvcCodigo("AUTO" + new SecureRandom().nextLong());
			relacionamentoServico.setSvcCodigoOrigem(svcCodigoOrigem);
			relacionamentoServico.setSvcCodigoDestino(svcCodigoDestino);
			relacionamentoServico.setTntCodigo(tntCodigo);
			relacionamentoServicoDao.save(relacionamentoServico);
		}
	}

	public void excluirRelacionamentoServico(String svcCodigoOrigem, String svcCodigoDestino, String tntCodigo) {
		RelacionamentoServico relacionamentoServico = relacionamentoServicoDao
				.findBySvcCodigoOrigemAndSvcCodigoDestinoAndTntCodigo(svcCodigoOrigem, svcCodigoDestino, tntCodigo);

		if (relacionamentoServico != null) {
			relacionamentoServicoDao.delete(relacionamentoServico);
		}
	}
}
