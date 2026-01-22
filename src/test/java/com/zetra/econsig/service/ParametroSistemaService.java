package com.zetra.econsig.service;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.ParamConsignatariaDao;
import com.zetra.econsig.dao.ParamConsignatariaRegistroSerDao;
import com.zetra.econsig.dao.ParamConvenioRegistroSerDao;
import com.zetra.econsig.dao.ParamNseRegistroSerDao;
import com.zetra.econsig.dao.ParamServicoRegistroSerDao;
import com.zetra.econsig.dao.ParamSistConsignanteDao;
import com.zetra.econsig.dao.ParamSvcConsignanteDao;
import com.zetra.econsig.dao.ParamSvcConsignatariaDao;
import com.zetra.econsig.dao.ServicoDao;
import com.zetra.econsig.dao.UsuarioCsaDao;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.ParamConsignataria;
import com.zetra.econsig.persistence.entity.ParamConsignatariaRegistroSer;
import com.zetra.econsig.persistence.entity.ParamConvenioRegistroSer;
import com.zetra.econsig.persistence.entity.ParamNseRegistroSer;
import com.zetra.econsig.persistence.entity.ParamServicoRegistroSer;
import com.zetra.econsig.persistence.entity.ParamSistConsignante;
import com.zetra.econsig.persistence.entity.ParamSvcConsignante;
import com.zetra.econsig.persistence.entity.ParamSvcConsignataria;

@Service
public class ParametroSistemaService {

    private final SecureRandom random = new SecureRandom();

	@Autowired
	private ServicoDao servicoDao;

	@Autowired
	private ParamSistConsignanteDao paramSistCseDao;

	@Autowired
	private ParamSvcConsignanteDao paramSvcConsignanteDao;

	@Autowired
	private ParamSvcConsignatariaDao paramSvcConsignatariaDao;

	@Autowired
	private ParamConvenioRegistroSerDao paramCnvRegistroSerDao;

	@Autowired
	private ParamServicoRegistroSerDao paramSvcRegistroSerDao;

	@Autowired
	private ParamNseRegistroSerDao paramNseRegistroSerDao;

	@Autowired
	private ParamConsignatariaDao paramConsignatariaDao;

	@Autowired
	private ParamConsignatariaRegistroSerDao paramConsignatariaRegistroSerDao;

	@Autowired
	private UsuarioCsaDao usuarioCsaDao;

	public void configurarParametroSistemaCse(String tpcCodigo, String psiVlr) {
		ParamSistConsignante paramSistCse = paramSistCseDao.findByTpcCodigo(tpcCodigo);
		if (paramSistCse == null) {
			paramSistCse = new ParamSistConsignante();
			paramSistCse.setTpcCodigo(tpcCodigo);
			paramSistCse.setCseCodigo("1");
		}
		paramSistCse.setPsiVlr(psiVlr);
		paramSistCseDao.save(paramSistCse);
		paramSistCseDao.flush();
	}

	public void DeletarParametroSistemaCse(String tpcCodigo, String psiVlr) {
		ParamSistConsignante paramSistCse = paramSistCseDao.findByTpcCodigo(tpcCodigo);
		if (paramSistCse == null) {
			paramSistCse = new ParamSistConsignante();
			paramSistCse.setTpcCodigo(tpcCodigo);
			paramSistCse.setCseCodigo("1");
		}
		paramSistCse.setPsiVlr(psiVlr);
		paramSistCseDao.delete(paramSistCse);
	}

	public void configurarParametroServicoCse(String tpsCodigo, String pseVlr) {
	    servicoDao.findAll().forEach(svc -> configurarParametroServicoCse(svc.getSvcCodigo(), tpsCodigo, pseVlr));
	}

	public void configurarParametroServicoCse(String svcCodigo, String tpsCodigo, String pseVlr) {
		long pseCodigo = random.nextLong();

		ParamSvcConsignante paramSvcConsignante = paramSvcConsignanteDao
				.getPseCodigoByTpsCodigoAndSvcCodigo(tpsCodigo, svcCodigo);

		if (paramSvcConsignante == null) {
			paramSvcConsignante = new ParamSvcConsignante();
			paramSvcConsignante.setPseCodigo("AUTO" + pseCodigo);
			paramSvcConsignante.setSvcCodigo(svcCodigo);
			paramSvcConsignante.setTpsCodigo(tpsCodigo);
			paramSvcConsignante.setCseCodigo("1");
			paramSvcConsignante.setPseVlr(pseVlr);
		} else if (pseVlr == null || !paramSvcConsignante.getPseVlr().matches(pseVlr)) {
			paramSvcConsignante.setPseVlr(pseVlr);
		}
		paramSvcConsignanteDao.save(paramSvcConsignante);
	}
	
	public void ativaParametroServicoServ(String tpsCodigo, String svcCodigo, String pseVlr) {
        ParamSvcConsignante paramSvcConsignante = paramSvcConsignanteDao
                .getPseCodigoByTpsCodigoAndSvcCodigo(tpsCodigo, svcCodigo);

        paramSvcConsignante.setPseVlr(pseVlr);
        paramSvcConsignanteDao.save(paramSvcConsignante);
    }


	public void ativarParametroServicoServ(String tpsCodigo, String svcCodigo, String pseVlr) {
		final ParamSvcConsignante paramSvcConsignante = paramSvcConsignanteDao
				.getPseCodigoByTpsCodigoAndSvcCodigo(tpsCodigo, svcCodigo);
		paramSvcConsignante.setPseVlr(pseVlr);

		paramSvcConsignanteDao.save(paramSvcConsignante);
	}

	public void deletarParametroServicoCse(String svcCodigo, String tpsCodigo, String pseVlr) {
		long pseCodigo = random.nextLong();

		ParamSvcConsignante paramSvcConsignante = paramSvcConsignanteDao
				.getPseCodigoByTpsCodigoAndSvcCodigo(tpsCodigo, svcCodigo);

		if (paramSvcConsignante == null) {
			paramSvcConsignante = new ParamSvcConsignante();
			paramSvcConsignante.setPseCodigo("AUTO" + pseCodigo);
			paramSvcConsignante.setSvcCodigo(svcCodigo);
			paramSvcConsignante.setTpsCodigo(tpsCodigo);
			paramSvcConsignante.setCseCodigo("1");
			paramSvcConsignante.setPseVlr(pseVlr);
		} else if (pseVlr == null || !paramSvcConsignante.getPseVlr().matches(pseVlr)) {
			paramSvcConsignante.setPseVlr(pseVlr);
		}
		paramSvcConsignanteDao.delete(paramSvcConsignante);
	}

	public void configurarParametroServicoCsa(String svcCodigo, String tpsCodigo, String csaCodigo, String pscVlr) {
		long pseCodigo = random.nextLong();

		ParamSvcConsignataria paramSvcConsignataria = paramSvcConsignatariaDao
				.getPseCodigoByTpsCodigoAndSvcCodigo(tpsCodigo, svcCodigo, csaCodigo);

		if (paramSvcConsignataria == null) {
			paramSvcConsignataria = new ParamSvcConsignataria();
			paramSvcConsignataria.setPscCodigo("AUTO" + pseCodigo);
			paramSvcConsignataria.setTpsCodigo(tpsCodigo);
			paramSvcConsignataria.setCsaCodigo(csaCodigo);
			paramSvcConsignataria.setSvcCodigo(svcCodigo);
		}

		paramSvcConsignataria.setPscVlr(pscVlr);
		paramSvcConsignatariaDao.save(paramSvcConsignataria);
	}

	public void configurarParametroCnvRegistroSer(String rseCodigo, String cnvCodigo, String tpsCodigo,
			String psrValor) {

		ParamConvenioRegistroSer paramCnvRegistroSer = paramCnvRegistroSerDao
				.findByRseCodigoAndCnvCodigoAndTpsCodigo(rseCodigo, cnvCodigo, tpsCodigo);

		if (paramCnvRegistroSer == null) {
			paramCnvRegistroSer = new ParamConvenioRegistroSer();
			paramCnvRegistroSer.setRseCodigo(rseCodigo);
			paramCnvRegistroSer.setCnvCodigo(cnvCodigo);
			paramCnvRegistroSer.setTpsCodigo(tpsCodigo);
		}
		paramCnvRegistroSer.setPcrVlr(psrValor);
		paramCnvRegistroSerDao.save(paramCnvRegistroSer);
	}

	public ParamConsignataria configurarParametroConsignataria(String csaCodigo, String tpaCodigo, String pcsClr) {
		ParamConsignataria paramConsignataria = new ParamConsignataria();

		paramConsignataria.setCsaCodigo(csaCodigo);
		paramConsignataria.setTpaCodigo(tpaCodigo);
		paramConsignataria.setPcsVlr(pcsClr);

		paramConsignatariaDao.save(paramConsignataria);

		return paramConsignataria;
	}

	public void deletarParametroCsa(ParamConsignataria paramConsignataria) {
		if (paramConsignataria != null) {
			paramConsignatariaDao.delete(paramConsignataria);
		}
	}

	public ParamConsignatariaRegistroSer configurarParamCsaRse(String rseCodigo, String csaCodigo, String tpaCodigo, String prcVlr) {
		ParamConsignatariaRegistroSer paramConsignatariaRegistroSer = new ParamConsignatariaRegistroSer();

		paramConsignatariaRegistroSer.setRseCodigo(rseCodigo);
		paramConsignatariaRegistroSer.setCsaCodigo(csaCodigo);
		paramConsignatariaRegistroSer.setTpaCodigo(tpaCodigo);
		paramConsignatariaRegistroSer.setPrcVlr(prcVlr);
		paramConsignatariaRegistroSer.setPrcDataCadastro(DateHelper.getSystemDatetime());

		paramConsignatariaRegistroSerDao.save(paramConsignatariaRegistroSer);

		return paramConsignatariaRegistroSer;
	}

	public void deletarParamConsignatariaRse(ParamConsignatariaRegistroSer paramConsignatariaRegistroSer) {
		if (paramConsignatariaRegistroSer != null) {
			paramConsignatariaRegistroSerDao.delete(paramConsignatariaRegistroSer);
		}
	}

	public void excluirParametroCnvRegistroSer(String rseCodigo, String cnvCodigo, String tpsCodigo) {

		ParamConvenioRegistroSer paramCnvRegistroSer = paramCnvRegistroSerDao
				.findByRseCodigoAndCnvCodigoAndTpsCodigo(rseCodigo, cnvCodigo, tpsCodigo);

		if (paramCnvRegistroSer != null) {
			paramCnvRegistroSerDao.delete(paramCnvRegistroSer);
		}
	}

	public void configurarParametroSvcRegistroSer(String rseCodigo, String svcCodigo, String tpsCodigo,
			String psrValor) {

		ParamServicoRegistroSer paramSvcRegistroSer = paramSvcRegistroSerDao
				.findByRseCodigoAndSvcCodigoAndTpsCodigo(rseCodigo, svcCodigo, tpsCodigo);

		if (paramSvcRegistroSer == null) {
			paramSvcRegistroSer = new ParamServicoRegistroSer();
			paramSvcRegistroSer.setRseCodigo(rseCodigo);
			paramSvcRegistroSer.setSvcCodigo(svcCodigo);
			paramSvcRegistroSer.setTpsCodigo(tpsCodigo);
		}
		paramSvcRegistroSer.setPsrVlr(psrValor);
		paramSvcRegistroSerDao.save(paramSvcRegistroSer);
	}
	
	public void deleteParametroSvcRegistroSer(String rseCodigo, String svcCodigo, String tpsCodigo) {
		ParamServicoRegistroSer paramSvcRegistroSer = paramSvcRegistroSerDao
				.findByRseCodigoAndSvcCodigoAndTpsCodigo(rseCodigo, svcCodigo, tpsCodigo);

		if (paramSvcRegistroSer != null) {
			paramSvcRegistroSerDao.delete(paramSvcRegistroSer);
		}
	}

	public void deleteParametroSvcNseRse(String rseCodigo, String nseCodigo, String tpsCodigo) {
		ParamNseRegistroSer paramNseRegistroSer = paramNseRegistroSerDao.findByRseCodigoAndNseCodigoAndTpsCodigo(rseCodigo, nseCodigo, tpsCodigo);

		if (paramNseRegistroSer != null) {
			paramNseRegistroSerDao.delete(paramNseRegistroSer);
		}
	}

	public void configurarParametroNseRegistroSer(String rseCodigo, String nseCodigo, String tpsCodigo,
			String pnrValor) {

		ParamNseRegistroSer paramNseRegistroSer = paramNseRegistroSerDao
				.findByRseCodigoAndNseCodigoAndTpsCodigo(rseCodigo, nseCodigo, tpsCodigo);

		if (paramNseRegistroSer == null) {
			paramNseRegistroSer = new ParamNseRegistroSer();
			paramNseRegistroSer.setRseCodigo(rseCodigo);
			paramNseRegistroSer.setNseCodigo(nseCodigo);
			paramNseRegistroSer.setTpsCodigo(tpsCodigo);
			paramNseRegistroSer.setPnrAlteradoPeloServidor("N");
			paramNseRegistroSer.setPnrObs("Teste Automatizado");
		}
		paramNseRegistroSer.setPnrVlr(pnrValor);
		paramNseRegistroSerDao.save(paramNseRegistroSer);
	}

	public void deleteParametroNseRse(String rseCodigo, String nseCodigo, String tpsCodigo) {
		ParamNseRegistroSer paramNseRegistroSer = paramNseRegistroSerDao
				.findByRseCodigoAndNseCodigoAndTpsCodigo(rseCodigo, nseCodigo, tpsCodigo);

		if (paramNseRegistroSer != null) {
			paramNseRegistroSerDao.delete(paramNseRegistroSer);
		}
	}

	public void alterarParametroConsignataria(String usuLogin, String tpaCodigo, String pcsVlr) {
		String csaCodigo = usuarioCsaDao.getCsaCodigoByUsuLogin(usuLogin);
		ParamConsignataria paramConsignataria = paramConsignatariaDao.findByTpaCodigoAndCsaCodigo(csaCodigo,
				tpaCodigo);

		if (paramConsignataria == null) {
			paramConsignataria = new ParamConsignataria();
			paramConsignataria.setCsaCodigo(csaCodigo);
			paramConsignataria.setTpaCodigo(tpaCodigo);
		}
		paramConsignataria.setPcsVlr(pcsVlr);
		paramConsignatariaDao.save(paramConsignataria);
	}

	public ParamSistConsignante getParamSistemaConsignante(String tpcCodigo) {
		return paramSistCseDao.findByTpcCodigo(tpcCodigo);
	}
}
