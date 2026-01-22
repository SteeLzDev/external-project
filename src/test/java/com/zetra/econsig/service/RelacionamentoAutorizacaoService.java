package com.zetra.econsig.service;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.AutDescontoDao;
import com.zetra.econsig.dao.RelacionamentoAutorizacaoDao;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.StatusCompra;
import com.zetra.econsig.values.StatusCompraEnum;

@Service
public class RelacionamentoAutorizacaoService {

	@Autowired
	private RelacionamentoAutorizacaoDao relacionamentoAutorizacaoDao;

	@Autowired
	private AutDescontoDao autDescontoDao;

	public RelacionamentoAutorizacao getRelacionamentoAutorizacao(Long adeNumero) {
		AutDesconto adeCodigoOrigem = autDescontoDao.findByAdeNumero(adeNumero);

		return relacionamentoAutorizacaoDao.findByAdeCodigoOrigem(adeCodigoOrigem.getAdeCodigo());
	}

	public void alterarDataCompra(Long adeNumero, Timestamp data) {
		RelacionamentoAutorizacao relacionamentoAutorizacao = getRelacionamentoAutorizacao(adeNumero);

		if(relacionamentoAutorizacao != null) {
			relacionamentoAutorizacao.setRadData(data);
			relacionamentoAutorizacao.setRadDataRefInfSaldo(data);
			relacionamentoAutorizacaoDao.save(relacionamentoAutorizacao);
		}
	}

	public void alterarDataInfoSaldo(Long adeNumero, Timestamp data) {
		RelacionamentoAutorizacao relacionamentoAutorizacao = getRelacionamentoAutorizacao(adeNumero);

		if(relacionamentoAutorizacao != null) {
			relacionamentoAutorizacao.setRadDataInfSaldo(data);
			relacionamentoAutorizacao.setRadDataRefAprSaldo(data);
			relacionamentoAutorizacaoDao.save(relacionamentoAutorizacao);
		}
	}

	public void alterarDataAprovacaoSaldo(Long adeNumero, Timestamp data) {
		RelacionamentoAutorizacao relacionamentoAutorizacao = getRelacionamentoAutorizacao(adeNumero);

		if(relacionamentoAutorizacao != null) {
			relacionamentoAutorizacao.setRadDataAprSaldo(data);
			relacionamentoAutorizacao.setRadDataRefPgtSaldo(data);
			relacionamentoAutorizacaoDao.save(relacionamentoAutorizacao);
		}
	}

	public void alterarDataPagamentoSaldo(Long adeNumero, Timestamp data) {
		RelacionamentoAutorizacao relacionamentoAutorizacao = getRelacionamentoAutorizacao(adeNumero);

		if(relacionamentoAutorizacao != null) {
			relacionamentoAutorizacao.setRadDataPgtSaldo(data);
			relacionamentoAutorizacao.setRadDataRefLiquidacao(data);
			relacionamentoAutorizacaoDao.save(relacionamentoAutorizacao);
		}
	}

	public void definirRelacionamentoAdes(long adeNumeroOrigem, long adeNumeroDestino, String tntCodigo) {
		RelacionamentoAutorizacao relacionamentoAutorizacao = new RelacionamentoAutorizacao();

		relacionamentoAutorizacao.setAdeCodigoOrigem(autDescontoDao.findByAdeNumero(adeNumeroOrigem).getAdeCodigo());
		relacionamentoAutorizacao.setAdeCodigoDestino(autDescontoDao.findByAdeNumero(adeNumeroDestino).getAdeCodigo());
		relacionamentoAutorizacao.setTntCodigo(tntCodigo);

		relacionamentoAutorizacaoDao.save(relacionamentoAutorizacao);
	}

	public void definirRelacionamentoAdes(long adeNumeroOrigem, long adeNumeroDestino, String tntCodigo, StatusCompraEnum statusCompraEnum, Date dataPagSaldo, Date dataAprSaldo,
			Date dataInfSaldo, Date dataLiquidacao, Date radData, String csaOrigem, String csaDestino) {
		RelacionamentoAutorizacao relacionamentoAutorizacao = new RelacionamentoAutorizacao();

		relacionamentoAutorizacao.setAdeCodigoOrigem(autDescontoDao.findByAdeNumero(adeNumeroOrigem).getAdeCodigo());
		relacionamentoAutorizacao.setAdeCodigoDestino(autDescontoDao.findByAdeNumero(adeNumeroDestino).getAdeCodigo());
		relacionamentoAutorizacao.setTntCodigo(tntCodigo);
		relacionamentoAutorizacao.setStatusCompra(new StatusCompra(statusCompraEnum.getCodigo()));
		relacionamentoAutorizacao.setRadDataPgtSaldo(dataPagSaldo);
		relacionamentoAutorizacao.setRadDataAprSaldo(dataAprSaldo);
		relacionamentoAutorizacao.setRadDataInfSaldo(dataInfSaldo);
		relacionamentoAutorizacao.setRadDataLiquidacao(dataLiquidacao);
		relacionamentoAutorizacao.setRadData(radData);
		relacionamentoAutorizacao.setCsaCodigoOrigem(csaOrigem);
		relacionamentoAutorizacao.setCsaCodigoDestino(csaDestino);
		relacionamentoAutorizacao.setRadDataRefInfSaldo(radData);

		relacionamentoAutorizacaoDao.save(relacionamentoAutorizacao);
	}

	public void excluirRelacionamentoAdesPorAdeNumero(Long adeNumero) {
		relacionamentoAutorizacaoDao.delete(getRelacionamentoAutorizacao(adeNumero));
	}

	public void excluirRelacionamentoAdes(long adeNumeroOrigem, long adeNumeroDestino, String tntCodigo, StatusCompraEnum statusCompraEnum, Date dataPagSaldo, Date dataAprSaldo,
			Date dataInfSaldo, Date dataLiquidacao, Date radData, String csaOrigem, String csaDestino) {
		RelacionamentoAutorizacao relacionamentoAutorizacao = new RelacionamentoAutorizacao();

		relacionamentoAutorizacao.setAdeCodigoOrigem(autDescontoDao.findByAdeNumero(adeNumeroOrigem).getAdeCodigo());
		relacionamentoAutorizacao.setAdeCodigoDestino(autDescontoDao.findByAdeNumero(adeNumeroDestino).getAdeCodigo());
		relacionamentoAutorizacao.setTntCodigo(tntCodigo);
		relacionamentoAutorizacao.setStatusCompra(new StatusCompra(statusCompraEnum.getCodigo()));
		relacionamentoAutorizacao.setRadDataPgtSaldo(dataPagSaldo);
		relacionamentoAutorizacao.setRadDataAprSaldo(dataAprSaldo);
		relacionamentoAutorizacao.setRadDataInfSaldo(dataInfSaldo);
		relacionamentoAutorizacao.setRadDataLiquidacao(dataLiquidacao);
		relacionamentoAutorizacao.setRadData(radData);
		relacionamentoAutorizacao.setCsaCodigoOrigem(csaOrigem);
		relacionamentoAutorizacao.setCsaCodigoDestino(csaDestino);
		relacionamentoAutorizacao.setRadDataRefInfSaldo(radData);

		relacionamentoAutorizacaoDao.delete(relacionamentoAutorizacao);
	}

}
