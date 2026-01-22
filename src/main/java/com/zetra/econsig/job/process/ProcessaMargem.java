package com.zetra.econsig.job.process;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessaMargem</p>
 * <p>Description: Classe para processamento de arquivos de margem</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaMargem extends Processo {

    protected final String nomeArquivoEntrada;

    protected final String tipoEntidade;

    protected final String codigoEntidade;

    protected final boolean margemTotal;

    protected final boolean gerarTransferidos;

    protected final AcessoSistema responsavel;

    public ProcessaMargem(String nomeArquivoEntrada, String tipoEntidade, String codigoEntidade, boolean margemTotal, boolean gerarTransferidos, AcessoSistema responsavel) {
        this.nomeArquivoEntrada = nomeArquivoEntrada;
        this.tipoEntidade = tipoEntidade;
        this.codigoEntidade = codigoEntidade;
        this.margemTotal = margemTotal;
        this.gerarTransferidos = gerarTransferidos;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        try {
            // Cria os delegates necessários
            ParametroDelegate parDelegate = new ParametroDelegate();

            String margemTotalStr = margemTotal ? "S" : "N";
            String gerarTransferidosStr = gerarTransferidos ? "S" : "N";

            // Altera os parâmetro no banco de dados
            parDelegate.updateParamSistCse(margemTotalStr, CodedValues.TPC_IMP_MARGEM_TOTAL, CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            parDelegate.updateParamSistCse(gerarTransferidosStr, CodedValues.TPC_GERA_ARQUIVO_TRANSFERIDOS, CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            // Altera o cache de parâmetros em memória
            ParamSist.getInstance().setParam(CodedValues.TPC_IMP_MARGEM_TOTAL, margemTotalStr);
            ParamSist.getInstance().setParam(CodedValues.TPC_GERA_ARQUIVO_TRANSFERIDOS, gerarTransferidosStr);

            // Executa rotina de importação de margem
            ServidorDelegate serDelegate = new ServidorDelegate();
            serDelegate.importaCadastroMargens(nomeArquivoEntrada, tipoEntidade, codigoEntidade, margemTotal, gerarTransferidos, responsavel);
            // Seta mensagem de sucesso na sessão do usuário
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.importacao.cadastro.margens.sucesso", responsavel);

        } catch (ServidorControllerException | ParametroControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.arquivo.margens", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        }
    }
}
