package com.zetra.econsig.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.BloqueioRseFunDao;
import com.zetra.econsig.dao.FuncaoDao;
import com.zetra.econsig.dao.FuncaoPerfilCorDao;
import com.zetra.econsig.dao.FuncaoPerfilCsaDao;
import com.zetra.econsig.persistence.entity.BloqueioRseFun;
import com.zetra.econsig.persistence.entity.Funcao;
import com.zetra.econsig.persistence.entity.FuncaoPerfilCor;
import com.zetra.econsig.persistence.entity.FuncaoPerfilCsa;

@Service
public class FuncaoSistemaService {

	@Autowired
	private FuncaoDao funcaoDao;

	@Autowired
	private FuncaoPerfilCsaDao funcaoPerfilCsaDao;

	@Autowired
	private FuncaoPerfilCorDao funcaoPerfilCorDao;

	@Autowired
	private BloqueioRseFunDao bloqueioRseFunDao;

	public void excluirFuncaoCsa(String funCodigo, String csaCodigo, String usuCodigo) {
		FuncaoPerfilCsa funcaoPerfilCsa = funcaoPerfilCsaDao.findByFunCodigoAndCsaCodigoAndUsuCodigo(funCodigo,
				csaCodigo, usuCodigo);

		if (funcaoPerfilCsa != null) {
			funcaoPerfilCsaDao.delete(funcaoPerfilCsa);
		}
	}

	public void excluirFuncaoCor(String funCodigo, String corCodigo) {
		FuncaoPerfilCor funcaoPerfilCor = funcaoPerfilCorDao.findByFunCodigoAndCorCodigo(funCodigo, corCodigo);

		if (funcaoPerfilCor != null) {
			funcaoPerfilCorDao.delete(funcaoPerfilCor);
		}
	}

	public void incluirFuncaoCsa(String funCodigo, String csaCodigo, String usuCodigo) {
		FuncaoPerfilCsa funcaoPerfilCsa = funcaoPerfilCsaDao.findByFunCodigoAndCsaCodigoAndUsuCodigo(funCodigo,
				csaCodigo, usuCodigo);

		if (funcaoPerfilCsa == null) {
			funcaoPerfilCsa = new FuncaoPerfilCsa();
			funcaoPerfilCsa.setCsaCodigo(csaCodigo);
			funcaoPerfilCsa.setFunCodigo(funCodigo);
			funcaoPerfilCsa.setUsuCodigo(usuCodigo);
			funcaoPerfilCsaDao.save(funcaoPerfilCsa);
		}
	}

	public void incluirFuncaoCor(String funCodigo, String corCodigo, String usuCodigo) {
		FuncaoPerfilCor funcaoPerfilCor = funcaoPerfilCorDao.findByFunCodigoAndCorCodigo(funCodigo, corCodigo);

		if (funcaoPerfilCor == null) {
			funcaoPerfilCor = new FuncaoPerfilCor();
			funcaoPerfilCor.setCorCodigo(corCodigo);
			funcaoPerfilCor.setFunCodigo(funCodigo);
			funcaoPerfilCor.setUsuCodigo(usuCodigo);
			funcaoPerfilCorDao.save(funcaoPerfilCor);
		}
	}

	public void alteraExigeSegundaSenhaCsaFuncao(String funCodigo, String vrExigeSenha) {
		Funcao funcao = funcaoDao.findByFunCodigo(funCodigo);

		funcao.setFunExigeSegundaSenhaCsa(vrExigeSenha);
		funcaoDao.save(funcao);
	}

	public void alteraExigeSegundaSenhaCorFuncao(String funCodigo, String vrExigeSenha) {
		Funcao funcao = funcaoDao.findByFunCodigo(funCodigo);

		funcao.setFunExigeSegundaSenhaCor(vrExigeSenha);
		funcaoDao.save(funcao);
	}

	public void alteraDataLimiteFuncao(String funCodigo, String rseCodigo, Timestamp data) {
		BloqueioRseFun bloqueioRseFun = bloqueioRseFunDao.findByRseCodigoAndFunCodigo(rseCodigo, funCodigo);

		if (bloqueioRseFun == null) {
			bloqueioRseFun = new BloqueioRseFun();
			bloqueioRseFun.setFunCodigo(funCodigo);
			bloqueioRseFun.setRseCodigo(rseCodigo);
			bloqueioRseFun.setBrsDataLimite(data);
			bloqueioRseFunDao.save(bloqueioRseFun);
		}
	}

	public BloqueioRseFun getBloqueioRseFuncao(String rseCodigo, String funCodigo) {
		return bloqueioRseFunDao.findByRseCodigoAndFunCodigo(rseCodigo, funCodigo);
	}

	public void excluirBloqueioRseFuncao(String rseCodigo, String funCodigo) {
		BloqueioRseFun bloqueioRseFun = bloqueioRseFunDao.findByRseCodigoAndFunCodigo(rseCodigo, funCodigo);
		if (bloqueioRseFun != null) {
			bloqueioRseFunDao.delete(bloqueioRseFun);
		}
	}
}
