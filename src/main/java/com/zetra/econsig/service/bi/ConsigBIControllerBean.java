package com.zetra.econsig.service.bi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.exception.ConsigBIControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.dao.ConsigBIDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ConsigBIControllerBean</p>
 * <p>Description: Session Bean para operações de BI</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ConsigBIControllerBean implements ConsigBIController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsigBIControllerBean.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Override
    public void atualizarBaseBI(int tipo, boolean populaDados, AcessoSistema responsavel) throws ConsigBIControllerException {
        try {
            LOG.info("INÍCIO DA ATUALIZAÇÃO DA BASE DE BI (" + tipo + ", " + populaDados + ")");
            final ConsigBIDAO dao = DAOFactory.getDAOFactory().getConsigBIDAO();
            if (tipo == 0) {
                LOG.info("ATUALIZAÇÃO GERAL");
                dao.atualizarBI(populaDados);
            } else if (tipo == 1) {
                LOG.info("ATUALIZAÇÃO DAS DIMENSÕES");
                dao.atualizarDimensoes();
            } else if (tipo == 2) {
                LOG.info("ATUALIZAÇÃO DAS TABELAS AUXILIARES");
                dao.atualizarTabelasAuxiliares();
            } else if (tipo == 3) {
                LOG.info("ATUALIZAÇÃO DO FATO DE CONTRATOS");
                dao.atualizarFatoContrato(populaDados);
            } else if (tipo == 4) {
                LOG.info("ATUALIZAÇÃO DO FATO DE PARCELAS");
                dao.atualizarFatoParcela(populaDados);
            } else if (tipo == 5) {
                LOG.info("ATUALIZAÇÃO DO FATO DE MARGENS");
                dao.atualizarFatoMargem(populaDados);
            } else {
                LOG.error("TIPO INCORRETO");
            }
            LOG.info("FIM DA ATUALIZAÇÃO DA BASE DE BI");

            // Inclui ocorrência de atualização
            consignanteController.createOcorrenciaCse(CodedValues.TOC_ATUALIZACAO_BASE_BI, responsavel);
        } catch (DAOException | ConsignanteControllerException ex) {
            throw new ConsigBIControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
