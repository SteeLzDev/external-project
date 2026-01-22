package com.zetra.econsig.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.ConvenioDao;
import com.zetra.econsig.dao.CorrespondenteConvenioDao;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.CorrespondenteConvenio;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class ConvenioService {

	@Autowired
	private ConvenioDao convenioDao;

	@Autowired
	private CorrespondenteConvenioDao correspondenteConvenioDao;

    @Deprecated
	public Convenio getConvenio(String identificador, String csaCodigo, String orgIdentificador) {
		return convenioDao.getCnvBySvcIdentAndCsaCodigoAndOrgIdent(identificador, csaCodigo, orgIdentificador);
	}

	public Convenio findBySvcCodigoAndOrgCodigoAndCsaCodigo(String svcCodigo, String orgCodigo, String csaCodigo) {
		return convenioDao.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigo, orgCodigo, csaCodigo);
	}
	
	public Convenio findBySvcCodigoAndCsaCodigo(String svcCodigo, String csaCodigo) {
		return convenioDao.findBySvcCodigoAndCsaCodigo(svcCodigo, csaCodigo);
	}

	public Convenio incluirConvenioAtivo(String svcCodigo, String orgCodigo, String csaCodigo, String cnvCodVerba) {
	    try {
	        Convenio cnv = new Convenio();

	        cnv.setCnvCodigo(DBHelper.getNextId());
	        cnv.setSvcCodigo(svcCodigo);
	        cnv.setOrgCodigo(orgCodigo);
	        cnv.setCsaCodigo(csaCodigo);
	        cnv.setCnvDataIni(DateHelper.getSystemDate());
	        cnv.setVceCodigo("1");
	        cnv.setScvCodigo(CodedValues.SCV_ATIVO);
	        cnv.setCnvIdentificador("TESTE");
	        cnv.setCnvCodVerba(cnvCodVerba);

	        return convenioDao.save(cnv);
	    } catch (MissingPrimaryKeyException ex) {
	        log.error(ex.getMessage(), ex);
	        return null;
	    }
	}

	public Convenio createConvenio(String cnvCodigo, String svcCodigo, String orgCodigo, String csaCodigo, String cnvIdentificador) {
		Convenio cnv = new Convenio();

		cnv.setCnvCodigo(cnvCodigo);
		cnv.setSvcCodigo(svcCodigo);
		cnv.setOrgCodigo(orgCodigo);
		cnv.setCsaCodigo(csaCodigo);
		cnv.setCnvDataIni(DateHelper.getSystemDate());
		cnv.setVceCodigo("1");
		cnv.setScvCodigo(CodedValues.SCV_ATIVO);
		cnv.setCnvIdentificador(cnvIdentificador);

		convenioDao.save(cnv);

		return cnv;
	}

	public List<Convenio> getConvenios(String nseCodigo, String csaCodigo) {
		return convenioDao.getCnvByNseCodigoAndCsaCodigo(nseCodigo, csaCodigo);
	}

	public CorrespondenteConvenio getConvenioCorrespondente(String corCodigo, String cnvCodigo) {
		return correspondenteConvenioDao.getCnvByCorCodigoAndCnvCodigo(corCodigo, cnvCodigo);
	}

	public void alterarScvCodigoConvenio(String identificador, String csaCodigo, String orgIdentificador, String scvCodigo) {
		Convenio convenio = getConvenio(identificador, csaCodigo, orgIdentificador);

		convenio.setScvCodigo(scvCodigo);
		convenioDao.save(convenio);
	}

	public void alterarScvCodigoCorrespondenteConvenio(String corCodigo, String cnvCodigo, String scvCodigo) {
		CorrespondenteConvenio correspondenteConvenio = getConvenioCorrespondente(corCodigo, cnvCodigo);

		correspondenteConvenio.setScvCodigo(scvCodigo);
		correspondenteConvenioDao.save(correspondenteConvenio);
	}
}
