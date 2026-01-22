package com.zetra.econsig.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.CoeficienteAtivoDao;
import com.zetra.econsig.dao.PrazoConsignatariaDao;
import com.zetra.econsig.dao.PrazoDao;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.entity.CoeficienteAtivo;
import com.zetra.econsig.persistence.entity.Prazo;
import com.zetra.econsig.persistence.entity.PrazoConsignataria;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class PrazoService {

	@Autowired
	private PrazoDao prazoDao;

	@Autowired
	private PrazoConsignatariaDao prazoConsignatariaDao;

	@Autowired
	private CoeficienteAtivoDao coeficienteAtivoDao;

	public void excluirPrazo(String svcCodigo) {
		List<Prazo> prazos =  prazoDao.findBySvcCodigo(svcCodigo);

		if (!prazos.isEmpty()) {
			prazoDao.deleteAll(prazos);
		}
	}

	public void excluirPrazoConsignataria() {
		List<PrazoConsignataria> prazosConsignataria =  prazoConsignatariaDao.findAll();

		if (!prazosConsignataria.isEmpty()) {
			prazoConsignatariaDao.deleteAll(prazosConsignataria);
		}
	}

	public Prazo incluirPrazo(String przCodigo, String svcCodigo, short przVlr, short przAtivo) {
		Prazo novoPrz = new Prazo();

		novoPrz.setPrzCodigo(przCodigo);
		novoPrz.setSvcCodigo(svcCodigo);
		novoPrz.setPrzVlr(przVlr);
		novoPrz.setPrzAtivo(przAtivo);

		return prazoDao.save(novoPrz);
	}

	public PrazoConsignataria inserirPrzCsa(String przCsaCodigo, String csaCodigo, String przCodigo, short przCsaAtivo) {
		PrazoConsignataria przCsa = new PrazoConsignataria();

		przCsa.setPrzCsaCodigo(przCsaCodigo);
		przCsa.setCsaCodigo(csaCodigo);
		przCsa.setPrzCodigo(przCodigo);
		przCsa.setPrzCsaAtivo(przCsaAtivo);

		return prazoConsignatariaDao.save(przCsa);
	}

	private void deletarPrzCsa(PrazoConsignataria prazoConsignataria) {
		prazoConsignatariaDao.delete(prazoConsignataria);
	}

	public CoeficienteAtivo inserirCft(String cftCodigo, String przCsaCodigo, short cftDia, BigDecimal cftVlr, Date cftDatainiVig, Date cftDataFimVig, Date cftDataCadastro) {
		CoeficienteAtivo cft =  new CoeficienteAtivo();

		cft.setCftCodigo(cftCodigo);
		cft.setPrzCsaCodigo(przCsaCodigo);
		cft.setCftDia(cftDia);
		cft.setCftVlr(cftVlr);
		cft.setCftDataIniVig(cftDatainiVig);
		cft.setCftDataFimVig(cftDataFimVig);
		cft.setCftDataCadastro(cftDataCadastro);
		cft.setCftVlrRef(cftVlr);

		return coeficienteAtivoDao.save(cft);

	}

	private void deletarCfts(List<CoeficienteAtivo> cfts) {
		if (!cfts.isEmpty()) {
			for (CoeficienteAtivo cft : cfts) {
				coeficienteAtivoDao.delete(cft);
			}
		}
	}

	public String incluirPrazoConsignatariaServico(String csaCodigo, String svcCodigo, Short przVlr) {
	    try {
	        Prazo prazo = prazoDao.findBySvcCodigoAndPrzVlr(svcCodigo, przVlr);
	        if (prazo == null) {
	            prazo = incluirPrazo(DBHelper.getNextId(), svcCodigo, przVlr, CodedValues.STS_ATIVO);
	        }
	        PrazoConsignataria prazoConsignataria = prazoConsignatariaDao.getPrzCsaCodigoBySvcCodigoAndCsaCodigo(svcCodigo, csaCodigo, przVlr);
	        if (prazoConsignataria == null) {
	            prazoConsignataria = inserirPrzCsa(DBHelper.getNextId(), csaCodigo, prazo.getPrzCodigo(), CodedValues.STS_ATIVO);
	        }
	        return prazoConsignataria.getPrzCsaCodigo();
	    } catch (MissingPrimaryKeyException ex) {
	        log.error(ex.getMessage(), ex);
	        return null;
	    }
	}

	public void incluirCoeficienteAtivo(String csaCodigo, String svcCodigo, Short przVlr, BigDecimal cftVlr) {
	    try {
	        String przCsaCodigo = incluirPrazoConsignatariaServico(csaCodigo, svcCodigo, przVlr);
	        List<CoeficienteAtivo> cfts = coeficienteAtivoDao.findByPrzCsaCodigo(przCsaCodigo);
	        if (cfts == null || cfts.isEmpty()) {
	            inserirCft(DBHelper.getNextId(), przCsaCodigo, (short) 0, cftVlr, LocalDate.now().minusDays(2).toDate(), null, LocalDate.now().minusDays(3).toDate());
	        }
	    } catch (MissingPrimaryKeyException ex) {
	        log.error(ex.getMessage(), ex);
	    }
	}

	public void deletarCoeficienteAtivo(String csaCodigo, String svcCodigo, Short przVlr) {
		final PrazoConsignataria prazoConsignataria = prazoConsignatariaDao.getPrzCsaCodigoBySvcCodigoAndCsaCodigo(svcCodigo, csaCodigo, przVlr);

		if (prazoConsignataria != null) {
			final List<CoeficienteAtivo> cfts = coeficienteAtivoDao.findByPrzCsaCodigo(prazoConsignataria.getPrzCsaCodigo());

			deletarCfts(cfts);
			deletarPrzCsa(prazoConsignataria);
		}
	}
}
