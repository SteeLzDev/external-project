package com.zetra.econsig.service.consignacao;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.HistoricoOcorrenciaAdeHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Service
@Transactional
public class OcorrenciaAutorizacaoControllerBean implements OcorrenciaAutorizacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OcorrenciaAutorizacaoControllerBean.class);

    @Override
    public void updateOcorrenciaAutorizacaoAde(String ocaCodigo, String ocaObs, AcessoSistema responsavel) throws ZetraException {
        try {
            OcorrenciaAutorizacao oca = OcorrenciaAutorizacaoHome.findByPrimaryKey(ocaCodigo);
            String ocaObsOld = oca.getOcaObs();
            oca.setOcaObs(ocaObs);
            oca.setOcaData(DateHelper.getSystemDatetime());
            oca.setOcaIpAcesso(responsavel.getIpUsuario());
            oca.setUsuCodigo(responsavel.getUsuCodigo());
            AbstractEntityHome.update(oca);
            String hoaObs = "ORIGINAL: " + ocaObsOld + " EDITADO: " + ocaObs;
            HistoricoOcorrenciaAdeHome.create(ocaCodigo, hoaObs, responsavel);
        } catch (ZetraException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage());
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, e);
        }
    }
}
