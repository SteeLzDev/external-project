package com.zetra.econsig.job.process;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.CopiaSegurancaHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaCopiaSegurancaImportar</p>
 * <p>Description: Classe para restauração do banco de dados.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaCopiaSegurancaImportar extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaCopiaSegurancaImportar.class);

    private final String nomeArquivo;
    private final AcessoSistema responsavel;

    public ProcessaCopiaSegurancaImportar(String nomeArquivo, AcessoSistema responsavel) {
        this.nomeArquivo = nomeArquivo;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        String cseCodigo = responsavel.getCodigoEntidade();
        SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
        try {
            sistemaController.alteraStatusSistema(cseCodigo, CodedValues.STS_INDISP, ApplicationResourcesHelper.getMessage("mensagem.informacao.indisponivel.para.copia.seguranca", responsavel), responsavel);
            CopiaSegurancaHelper.importar(nomeArquivo, responsavel);
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.copia.seguranca.importar.realizado.sucesso", responsavel);

        } catch (ZetraException e) {
            LOG.error(e.getMessage(), e);
            // Determina mensagem de erro
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.copia.seguranca.importar", responsavel, nomeArquivo) + "<br>"
                          + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, e.getMessage())
                          + "<br>";
        } finally {
            try {
                sistemaController.alteraStatusSistema(cseCodigo, CodedValues.STS_ATIVO, null, responsavel);
            } catch (ZetraException e) {
                LOG.error(e.getMessage(), e);
                // Determina mensagem de erro
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.copia.seguranca.importar", responsavel, nomeArquivo) + "<br>"
                              + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, e.getMessage())
                              + "<br>";
            }
        }
    }
}
