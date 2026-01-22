package com.zetra.econsig.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.BaseCalcRegistroServidorDao;
import com.zetra.econsig.dao.HistoricoMargemRseDao;
import com.zetra.econsig.dao.MargemRegistroServidorDao;
import com.zetra.econsig.dao.RegistroServidorDao;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.BaseCalcRegistroServidor;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorId;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.StatusRegistroServidor;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class RegistroServidorService {

    @Autowired
    private AutDescontoService autDescontoService;

	@Autowired
	private RegistroServidorDao registroServidorDao;

	@Autowired
	private BaseCalcRegistroServidorDao baseCalcRegistroServidorDao;

	@Autowired
	private MargemRegistroServidorDao margemRegistroServidorDao;

	@Autowired
	private HistoricoMargemRseDao historicoMargemRseDao;

	public RegistroServidor getRegistroServidor(String serCodigo) {
		return registroServidorDao.findBySerCodigo(serCodigo);
	}

	public RegistroServidor obterRegistroServidorPorMatriculaOrgao(String rseMatricula, String orgCodigo) {
	    return registroServidorDao.findByRseMatriculaAndOrgCodigo(rseMatricula, orgCodigo);
	}
	
	public RegistroServidor obterRegistroServidorPorMatricula(String rseMatricula) {
	    return registroServidorDao.findByRseMatricula(rseMatricula);
	}

	public RegistroServidor incluirRegistroServidor(String serCodigo, String orgCodigo, String srsCodigo, String rseMatricula, BigDecimal rseMargem, BigDecimal rseMargemRest, BigDecimal rseMargemUsada) {
	    try {
	        RegistroServidor rse = new RegistroServidor();
	        rse.setRseCodigo(DBHelper.getNextId());
	        rse.setSerCodigo(serCodigo);
	        rse.setOrgCodigo(orgCodigo);
	        rse.setSrsCodigo(srsCodigo);
	        rse.setRseMatricula(rseMatricula);
	        rse.setRseMargem(rseMargem);
	        rse.setRseMargemRest(rseMargemRest);
	        rse.setRseMargemUsada(rseMargemUsada);
	        rse.setRseAuditoriaTotal("S");

	        return registroServidorDao.save(rse);
	    } catch (MissingPrimaryKeyException ex) {
	        log.error(ex.getMessage(), ex);
	        return null;
	    }
	}

	public void excluirRegistroServidor(String rseCodigo) {
        historicoMargemRseDao.removeByRseCodigo(rseCodigo);
        margemRegistroServidorDao.removeByRseCodigo(rseCodigo);

	    List<AutDesconto> ades = autDescontoService.getAdes(rseCodigo, null);
	    if (ades != null && !ades.isEmpty()) {
	        for (AutDesconto ade : ades) {
	            autDescontoService.deleteAutDesconto(ade.getAdeCodigo());
	        }
	    }

	    registroServidorDao.deleteById(rseCodigo);
	}

	public RegistroServidor incluirRegistroServidorAtivoComMargem(String serCodigo, String orgCodigo, String rseMatricula) {
	    return incluirRegistroServidor(serCodigo, orgCodigo, CodedValues.SRS_ATIVO, rseMatricula, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.ZERO);
	}

	public RegistroServidor incluirRegistroServidorAtivoComMargem(String serCodigo, String orgCodigo, String rseMatricula, String srsCodigo) {
		return incluirRegistroServidor(serCodigo, orgCodigo, srsCodigo, rseMatricula, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.ZERO);
	}

	public void alterarRseMargemRest(String rseMatricula, BigDecimal valor) {
		RegistroServidor registroServidor = registroServidorDao.findByRseMatricula(rseMatricula);
		registroServidor.setRseMargemRest(valor);
		registroServidorDao.save(registroServidor);
	}

	public void alterarRseMargemUsada(String rseMatricula, BigDecimal valor) {
		RegistroServidor registroServidor = registroServidorDao.findByRseMatricula(rseMatricula);
		registroServidor.setRseMargemUsada(valor);
		registroServidorDao.save(registroServidor);
	}

	public void alterarRseMediaMargem(String rseMatricula, BigDecimal valor, short marCodigo) {
		RegistroServidor registroServidor = registroServidorDao.findByRseMatricula(rseMatricula);

		switch(marCodigo) {
		case 1:
			registroServidor.setRseMediaMargem(valor);
			registroServidorDao.save(registroServidor);
			break;
		case 2:
			registroServidor.setRseMediaMargem2(valor);
			registroServidorDao.save(registroServidor);
			break;
		case 3:
			registroServidor.setRseMediaMargem3(valor);
			registroServidorDao.save(registroServidor);
			break;
		default:
			MargemRegistroServidorId id = new MargemRegistroServidorId(marCodigo, registroServidor.getRseCodigo());
			Optional<MargemRegistroServidor> margemExtraOpt = margemRegistroServidorDao.findById(id);

			margemExtraOpt.ifPresent(extra -> {
				extra.setMrsMargem(valor);
				margemRegistroServidorDao.save(extra);
			});
			break;

		}
	}

	public void alterarVrsCodigo(RegistroServidor registroServidor, String vrsCodigo) {
		registroServidor.setVrsCodigo(vrsCodigo);
		registroServidorDao.save(registroServidor);
	}

	public void alterarRseBaseCalculo(RegistroServidor registroServidor, BigDecimal rseBaseCalculo) {
		registroServidor.setRseBaseCalculo(rseBaseCalculo);
		registroServidorDao.save(registroServidor);
	}

	public void alterarRseBancoSal(RegistroServidor registroServidor, String csaIdentInterno) {
		registroServidor.setRseBancoSal(csaIdentInterno);
		registroServidorDao.save(registroServidor);
	}

	public RegistroServidor alterarStatusRegistroServidor(String rseCodigo, String srsCodigo) {
		Optional<RegistroServidor> rse = registroServidorDao.findById(rseCodigo);
		if (rse != null && rse.isPresent()) {
		    RegistroServidor registroServidor = rse.get();
		    registroServidor.setStatusRegistroServidor(new StatusRegistroServidor(srsCodigo));
		    registroServidorDao.save(registroServidor);
		    return registroServidor;
		}
		return null;
	}

	public void incluirBaseCalcRegistroServidor(String rseCodigo, String tbcCodigo) {
		BaseCalcRegistroServidor baseCalcRegistroServidor = baseCalcRegistroServidorDao.findByRseCodigoAndTbcCodigo(rseCodigo, tbcCodigo);


		if(baseCalcRegistroServidor == null) {
			baseCalcRegistroServidor = new BaseCalcRegistroServidor();
			baseCalcRegistroServidor.setRseCodigo(rseCodigo);
		}
		baseCalcRegistroServidor.setTbcCodigo(tbcCodigo);
		baseCalcRegistroServidor.setBcsValor(new BigDecimal("3000"));
		baseCalcRegistroServidorDao.save(baseCalcRegistroServidor);
	}

	public String getStatusServidor(String rseMatricula) {
		return registroServidorDao.findByRseMatricula(rseMatricula).getSrsCodigo();
	}
}
