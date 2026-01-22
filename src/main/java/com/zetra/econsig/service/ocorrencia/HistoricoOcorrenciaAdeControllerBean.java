package com.zetra.econsig.service.ocorrencia;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.HistoricoOcorrenciaAde;
import com.zetra.econsig.persistence.entity.HistoricoOcorrenciaAdeHome;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HistoricoOcorrenciaAdeControllerBean implements HistoricoOcorrenciaAdeController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(HistoricoOcorrenciaAdeControllerBean.class);

    @Override
    public List<HistoricoOcorrenciaAde> buscarHistoricoOcorrenciaAdeByOcaCodigo(String ocaCodigo, AcessoSistema responsavel) throws ZetraException {
        List<HistoricoOcorrenciaAde> historico;
        try {
            historico = HistoricoOcorrenciaAdeHome.findHistoricoOcorrenciaAde(ocaCodigo);
        } catch (ZetraException ex){
            LOG.error(ex.getMessage());
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return historico;
    }
}
