package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.CriptografiaArquivos;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.boleto.BoletoServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaExclusaoArquivosAntigos</p>
 * <p>Description: Processamento de Exclusão de Arquivos Antigos</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaExclusaoArquivosAntigos extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaExclusaoArquivosAntigos.class);

    public ProcessaExclusaoArquivosAntigos(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        LOG.debug("Executa Exclusão de Arquivos Antigos");
        FileHelper.executarLimpezaArquivosAntigos(getResponsavel());
        UploadHelper.limparUploadTempDir(getResponsavel());

        if (ParamSist.paramEquals(CodedValues.TPC_CRIPTOGRAFA_ARQUIVOS, CodedValues.TPC_SIM, getResponsavel())) {
            CriptografiaArquivos.criptografarArquivosSistema(getResponsavel());
        }

        int diasRemocaoBoletos = TextHelper.isNum(ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_REMOCAO_BOLETO_SERVIDOR, getResponsavel())) ?
                Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_REMOCAO_BOLETO_SERVIDOR, getResponsavel()).toString()) : 0;
        if (diasRemocaoBoletos > 0) {
            BoletoServidorController boletoServidorController = ApplicationContextProvider.getApplicationContext().getBean(BoletoServidorController.class);
            boletoServidorController.removeBoletosExpirados(diasRemocaoBoletos, getResponsavel());
        }
    }
}
