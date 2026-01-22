package com.zetra.econsig.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.TipoMotivoOperacaoDao;
import com.zetra.econsig.persistence.entity.TipoMotivoOperacao;

@Service
public class TipoMotivoOperacaoService {

	@Autowired
	private TipoMotivoOperacaoDao tipoMotivoOperacaoDao;

	public void setTmoObsObrigatorio(String tmoCodigo, boolean obrigatorio) {

		Optional<TipoMotivoOperacao> tmo = tipoMotivoOperacaoDao.findById(tmoCodigo);

		tmo.ifPresent(o -> {
			o.setTmoExigeObs(obrigatorio ? "S" : "N");

			tipoMotivoOperacaoDao.save(o);
		});

	}

}
