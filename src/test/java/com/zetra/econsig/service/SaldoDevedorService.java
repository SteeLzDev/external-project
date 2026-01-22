package com.zetra.econsig.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.SaldoDevedorDao;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.SaldoDevedor;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class SaldoDevedorService {

	@Autowired
	private SaldoDevedorDao saldoDevedorDao;

	public void incluirSaldoDevedor(String adeCodigo, String usuCodigo, String valor) {
		SaldoDevedor saldoDevedor = new SaldoDevedor();
		saldoDevedor.setAdeCodigo(adeCodigo);
		saldoDevedor.setUsuCodigo(usuCodigo);
		saldoDevedor.setSdvValor(new BigDecimal(valor));
		saldoDevedor.setSdvDataMod(DateHelper.getSystemDatetime());
		saldoDevedor.setSdvAgencia("");
		saldoDevedor.setSdvConta("");
		saldoDevedor.setSdvNomeFavorecido("");
		saldoDevedor.setSdvCnpj("");
		saldoDevedorDao.save(saldoDevedor);
	}

	public SaldoDevedor retornarSaldoDevedor(String adeCodigo) {
		return saldoDevedorDao.findByAdeCodigo(adeCodigo);
	}

}
