package com.zetra.econsig.job.process.integracao.orientada;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessaMargem</p>
 * <p>Description: Classe para processamento orientado de arquivos de margem </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */public class ProcessaMargem extends com.zetra.econsig.job.process.ProcessaMargem {

    /**
     *
     * @param nomeArquivoEntrada
     * @param tipoEntidade
     * @param codigoEntidade
     * @param margemTotal
     * @param gerarTransferidos
     * @param responsavel
     */
    public ProcessaMargem(String nomeArquivoEntrada, String tipoEntidade, String codigoEntidade, boolean margemTotal, boolean gerarTransferidos, AcessoSistema responsavel) {
        super(nomeArquivoEntrada, tipoEntidade, codigoEntidade, margemTotal, gerarTransferidos, responsavel);
    }

    /**
     *
     */
    @Override
    protected void executar() {
        String horaInicioStr = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());
        try {
            ServidorDelegate delegate = new ServidorDelegate();

            List<String> orgCodigos = new ArrayList<String>();
            List<String> estCodigos = new ArrayList<String>();

            if (responsavel.isOrg()) {
                if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    estCodigos.add(responsavel.getEstCodigo());
                } else {
                    orgCodigos.add(responsavel.getOrgCodigo());
                }
            }

            int ativosAntes = delegate.countRegistroServidor(CodedValues.SRS_ATIVOS, orgCodigos, estCodigos, responsavel);
            int inativosAntes = delegate.countRegistroServidor(CodedValues.SRS_INATIVOS, orgCodigos, estCodigos, responsavel);

            ParametroDelegate parDelegate = new ParametroDelegate();
            String recalculaMargem = (String) ParamSist.getInstance().getParam(CodedValues.TPC_RECALCULA_MARGEM_IMP_MARGEM, responsavel);
            // Altera os parâmetro no banco de dados
            parDelegate.updateParamSistCse(CodedValues.TPC_SIM, CodedValues.TPC_RECALCULA_MARGEM_IMP_MARGEM, CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            // Altera o cache de parâmetros em memória
            ParamSist.getInstance().setParam(CodedValues.TPC_RECALCULA_MARGEM_IMP_MARGEM, CodedValues.TPC_SIM);

            super.executar();

            parDelegate.updateParamSistCse(recalculaMargem, CodedValues.TPC_RECALCULA_MARGEM_IMP_MARGEM, CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            // Altera o cache de parâmetros em memória
            ParamSist.getInstance().setParam(CodedValues.TPC_RECALCULA_MARGEM_IMP_MARGEM, recalculaMargem);

            if (codigoRetorno == SUCESSO) {
                int ativosDepois = delegate.countRegistroServidor(CodedValues.SRS_ATIVOS, orgCodigos, estCodigos, responsavel);
                int inativosDepois = delegate.countRegistroServidor(CodedValues.SRS_INATIVOS, orgCodigos, estCodigos, responsavel);

                String prefixo = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel);
                File entrada = new File(nomeArquivoEntrada);
                File critica = new File(entrada.getParent(), prefixo + entrada.getName());
                if (critica.exists()) {
                    FileHelper.rename(critica.getAbsolutePath(), critica.getAbsolutePath() + ".ok");
                }
                StringBuilder resultado = new StringBuilder();
                resultado.append(ApplicationResourcesHelper.getMessage("mensagem.sucesso.processamento.margem", responsavel, horaInicioStr));
                resultado.append("<br>\n");
                resultado.append(ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.resumo.processamento.titulo", responsavel, entrada.getName()));
                resultado.append("<br>\n");
                resultado.append(ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.resumo.processamento.margem.ativos", responsavel, String.valueOf(ativosAntes), String.valueOf(ativosDepois)));
                resultado.append("<br>\n");
                resultado.append(ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.resumo.processamento.margem.inativos", responsavel, String.valueOf(inativosAntes), String.valueOf(inativosDepois)));
                mensagem = resultado.toString();
            }
        } catch (ServidorControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.arquivo.margens", responsavel) + "<br>"
                     + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        } catch (ParametroControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.arquivo.margens", responsavel) + "<br>"
                     + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        }
    }
}
