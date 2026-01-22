package com.zetra.econsig.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.VerbaConvenioDao;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class VerbaConvenioService {

    @Autowired
    private VerbaConvenioDao verbaConvenioDao;

    public VerbaConvenio incluirVerbaConvenioAtivo(String cnvCodigo) {
        try {
            VerbaConvenio vco = new VerbaConvenio();

            vco.setVcoCodigo(DBHelper.getNextId());
            vco.setCnvCodigo(cnvCodigo);
            vco.setVcoDataIni(DateHelper.getSystemDate());
            vco.setVcoDataFim(DateHelper.addMonths(DateHelper.getSystemDate(), 12));
            vco.setVcoAtivo(CodedValues.STS_ATIVO);
            vco.setVcoVlrVerba(BigDecimal.valueOf(99999999999.99));
            vco.setVcoVlrVerbaRest(BigDecimal.valueOf(99999999999.99));

            return verbaConvenioDao.save(vco);
        } catch (MissingPrimaryKeyException ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public VerbaConvenio createVerbaConvenio(String vcoCodigo, String cnvCodigo, BigDecimal vcoVlrVerba, BigDecimal vcoVlrVerbaRest) {
        VerbaConvenio vco = new VerbaConvenio();

        vco.setVcoCodigo(vcoCodigo);
        vco.setCnvCodigo(cnvCodigo);
        vco.setVcoDataIni(DateHelper.getSystemDate());
        vco.setVcoDataFim(DateHelper.addMonths(DateHelper.getSystemDate(), 12));
        vco.setVcoAtivo(Short.valueOf("1"));
        vco.setVcoVlrVerba(vcoVlrVerba);
        vco.setVcoVlrVerbaRest(vcoVlrVerbaRest);

        verbaConvenioDao.save(vco);
        return vco;
    }

    public void deleteVco(VerbaConvenio vco) {
        if (vco != null) {
            verbaConvenioDao.delete(vco);
        }
    }
}
