package com.zetra.econsig.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.FuncaoDao;
import com.zetra.econsig.dao.FuncaoPerfilCorDao;
import com.zetra.econsig.dao.FuncaoPerfilCsaDao;
import com.zetra.econsig.dao.FuncaoPerfilCseDao;
import com.zetra.econsig.dao.FuncaoPerfilDao;
import com.zetra.econsig.dao.FuncaoPerfilSupDao;
import com.zetra.econsig.dao.PapelFuncaoDao;
import com.zetra.econsig.persistence.entity.Funcao;
import com.zetra.econsig.persistence.entity.FuncaoPerfil;
import com.zetra.econsig.persistence.entity.FuncaoPerfilCor;
import com.zetra.econsig.persistence.entity.FuncaoPerfilCsa;
import com.zetra.econsig.persistence.entity.FuncaoPerfilCse;
import com.zetra.econsig.persistence.entity.FuncaoPerfilSup;
import com.zetra.econsig.persistence.entity.PapelFuncao;

@Service
@Transactional
public class FuncaoService {

	@Autowired
	private  FuncaoPerfilCsaDao  funcaoPerfilCsaDao ;

	@Autowired
	private FuncaoPerfilDao funcaoPerfilDao;

	@Autowired
	private  FuncaoPerfilCseDao  funcaoPerfilCseDao ;

	@Autowired
	private FuncaoPerfilSupDao funcaoPerfilSupDao;

	@Autowired
    private FuncaoPerfilCorDao funcaoPerfilCorDao;

	@Autowired
	private PapelFuncaoDao papelFuncaoDao;

	@Autowired
	private FuncaoDao funcaoDao;

	public void criarFuncaoPerfilCsa(String usuCodigo, String funCodigo, String csaCodigo) {
		final FuncaoPerfilCsa funcaoPerfil = new FuncaoPerfilCsa();
		funcaoPerfil.setUsuCodigo(usuCodigo);
		funcaoPerfil.setFunCodigo(funCodigo);
		funcaoPerfil.setCsaCodigo(csaCodigo);
		funcaoPerfilCsaDao.save(funcaoPerfil);
	}

	public void criarFuncaoPerfilCse(String usuCodigo, String funCodigo, String cseCodigo) {
		final FuncaoPerfilCse funcaoPerfil = new FuncaoPerfilCse();
		funcaoPerfil.setUsuCodigo(usuCodigo);
		funcaoPerfil.setFunCodigo(funCodigo);
		funcaoPerfil.setCseCodigo(cseCodigo);
		funcaoPerfilCseDao.save(funcaoPerfil);
	}

	public void criarFuncaoPerfilSup(String usuCodigo, String funCodigo, String cseCodigo) {
		final FuncaoPerfilSup funcaoPerfil = new FuncaoPerfilSup();
		funcaoPerfil.setUsuCodigo(usuCodigo);
		funcaoPerfil.setFunCodigo(funCodigo);
		funcaoPerfil.setCseCodigo(cseCodigo);
		funcaoPerfilSupDao.save(funcaoPerfil);
	}

	public void criarFuncaoPerfilCor(String usuCodigo, String funCodigo, String corCodigo) {
        final FuncaoPerfilCor funcaoPerfil = new FuncaoPerfilCor();
        funcaoPerfil.setUsuCodigo(usuCodigo);
        funcaoPerfil.setFunCodigo(funCodigo);
        funcaoPerfil.setCorCodigo(corCodigo);
        funcaoPerfilCorDao.save(funcaoPerfil);
    }


	public void criarFuncaoPerfil(String perCodigo, String funCodigo) {
		final FuncaoPerfil funcaoPerfil = new FuncaoPerfil();
		funcaoPerfil.setPerCodigo(perCodigo);
		funcaoPerfil.setFunCodigo(funCodigo);
		funcaoPerfilDao.save(funcaoPerfil);
	}

	public void criarPapelFuncao(String papCodigo, String funCodigo) {
		final PapelFuncao papelFuncao = new PapelFuncao();
		papelFuncao.setPapCodigo(papCodigo);
		papelFuncao.setFunCodigo(funCodigo);
		papelFuncaoDao.save(papelFuncao);
	}

	public void deletarFuncaoPerfilCse(String usuCodigo, String funCodigo) {
		final FuncaoPerfilCse funcaoPerfilCse = funcaoPerfilCseDao.findByFunCodigoAndUsuCodigo(funCodigo,  usuCodigo);

		if (funcaoPerfilCse != null) {
			funcaoPerfilCseDao.delete(funcaoPerfilCse);
		}
	}

	public void deletarFuncaoPerfilCsa(String usuCodigo, String funCodigo, String csaCodigo) {
		final FuncaoPerfilCsa funcaoPerfilCsa = funcaoPerfilCsaDao.findByFunCodigoAndCsaCodigoAndUsuCodigo(funCodigo, csaCodigo, usuCodigo);

		if (funcaoPerfilCsa != null) {
			funcaoPerfilCsaDao.delete(funcaoPerfilCsa);
		}
	}

	public void deletarFuncaoPerfilSup(String usuCodigo, String funCodigo, String cseCodigo) {
		final FuncaoPerfilSup funcaoPerfilSup = funcaoPerfilSupDao.findByFunCodigoAndCseCodigoAndUsuCodigo(funCodigo, cseCodigo, usuCodigo);

		if (funcaoPerfilSup != null) {
			funcaoPerfilSupDao.delete(funcaoPerfilSup);
		}
	}

	public void alteraExigeSegundaSenhaFuncao(String funCodigo, String funExigeSegundaSenhaCse, String funExigeSegundaSenhaSup, String funExigeSegundaSenhaOrg, String funExigeSegundaSenhaCsa,String funExigeSegundaSenhaCor) {
	    final Funcao  funcao = funcaoDao.findByFunCodigo(funCodigo);
	    funcao.setFunExigeSegundaSenhaCor(funExigeSegundaSenhaCor);
	    funcao.setFunExigeSegundaSenhaCsa(funExigeSegundaSenhaCsa);
	    funcao.setFunExigeSegundaSenhaCse(funExigeSegundaSenhaCse);
	    funcao.setFunExigeSegundaSenhaOrg(funExigeSegundaSenhaOrg);
	    funcao.setFunExigeSegundaSenhaSup(funExigeSegundaSenhaSup);
	    funcaoDao.save(funcao);
	}
}