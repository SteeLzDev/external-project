package com.zetra.econsig.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.OcorrenciaAutorizacaoDao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;

@Service
public class OcorrenciaAutorizacaoService {

	@Autowired
	private OcorrenciaAutorizacaoDao ocorrenciaAutorizacaoDao;
	
	public List<OcorrenciaAutorizacao> getOcorrenciaAutorizacao(String tocCodigo, String usuCodigo) {
		return ocorrenciaAutorizacaoDao.findByTocCodigoAndUsuCodigo(tocCodigo, usuCodigo);
	}
	
	public List<OcorrenciaAutorizacao> getOcorrenciaAutorizacao(String tocCodigo) {
		return ocorrenciaAutorizacaoDao.findByTocCodigo(tocCodigo);
	}
	
	public OcorrenciaAutorizacao getOcorrenciaAutorizacao(String tocCodigo, String adeCodigo, String ocaObs) {
		return ocorrenciaAutorizacaoDao.findByTocCodigoAndAdeCodigoAndOcaObs(tocCodigo, adeCodigo, ocaObs);
	}
	
	public OcorrenciaAutorizacao getOcorrenciaAutorizacaoPorAde(String tocCodigo, String adeCodigo) {
		return ocorrenciaAutorizacaoDao.findByTocCodigoAndAdeCodigo(tocCodigo, adeCodigo);
	}
	
	public void deleteOcorrencia(OcorrenciaAutorizacao oca) {
		ocorrenciaAutorizacaoDao.delete(oca);
	}
}
