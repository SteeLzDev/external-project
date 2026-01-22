package com.zetra.econsig.job.process;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

import br.com.nostrum.simpletl.util.TextHelper;

/**
 * <p> Title: ProcessaArquivamentoServidores</p>
 * <p> Description: Classe para processamento da rotina de arquivamento de servidores.</p>
 * <p> Copyright: Copyright (c) 2002-2017</p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaArquivamentoServidores extends ProcessoAgendadoEventual {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaArquivamentoServidores.class);

    public ProcessaArquivamentoServidores(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        try {
            final Map<String, String[]> parameterMap = getParametrosAgendamento(getAgdCodigo());

            List<String> orgCodigos = null;
            List<String> estCodigos = null;

            if (parameterMap != null && !parameterMap.isEmpty()) {
                if (!TextHelper.isNull(parameterMap.get(Columns.ORG_CODIGO))) {
                    orgCodigos = Arrays.asList(parameterMap.get(Columns.ORG_CODIGO));
                }

                if (!TextHelper.isNull(parameterMap.get(Columns.EST_CODIGO))) {
                    estCodigos = Arrays.asList(parameterMap.get(Columns.EST_CODIGO));
                }
            }

            final ServidorController serController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);

            final List<TransferObject> servidores = serController.lstServidoresArquivamento(orgCodigos, estCodigos, getResponsavel());

            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.info.arquivamento.servidor.quantidade.encontrados", getResponsavel(), String.valueOf(servidores == null ? 0 : servidores.size())));

            if (servidores != null && !servidores.isEmpty()) {
                // DESENV-18636 : Faz a limpeza da session (por causa da incapacidade do hibernate).
                final BatchManager batman = new BatchManager(SessionUtil.getSession());
                for (final TransferObject servidor : servidores) {
                    try {
                        final String serCodigo = servidor.getAttribute(Columns.SER_CODIGO).toString();
                        final String rseCodigo = servidor.getAttribute(Columns.RSE_CODIGO).toString();

                        serController.arquivarServidor(serCodigo, rseCodigo, getResponsavel());
                    } catch (final ServidorControllerException ex) {
                        // Se não foi possível realizar o arquivamento do servidor, o mesmo será ignorado
                        LOG.error(ex.getMessage(), ex);
                    }
                    batman.iterate();
                }
                batman.finish();
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);

            // Determina mensagem de erro
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processo.arquivamento.servidor", getResponsavel()) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", getResponsavel(), ex.getMessage());
        }
    }
}
