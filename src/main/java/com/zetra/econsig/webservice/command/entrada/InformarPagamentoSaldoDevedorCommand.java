package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OBS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.SaldoDevedorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.entidade.Anexo;

/**
 * <p>Title: InformarPagamentoSaldoDevedorCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de info. pagamento saldo devedor</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class InformarPagamentoSaldoDevedorCommand extends RequisicaoExternaCommand {

    public InformarPagamentoSaldoDevedorCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final boolean anexoObrigatorio = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_COMPROVANTE_PAGAMENTO_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        final SaldoDevedorDelegate sdvDelegate = new SaldoDevedorDelegate();

        @SuppressWarnings("unchecked")
        final
        List<TransferObject> autList = (List<TransferObject>) parametros.get(CONSIGNACAO);
        final Long adeNumero = (Long) parametros.get(ADE_NUMERO);

        TransferObject autdes = null;
        if ((autList != null) && (autList.size() == 1)) { // Otimização caso o contrato venha pela validação
            autdes = autList.get(0);
        } else {
            autdes = adeDelegate.findAutDescontoByAdeNumero(adeNumero, responsavel);
        }

        final String adeCodigo = (String) autdes.getAttribute(Columns.ADE_CODIGO);

        final List<String> listStc = new ArrayList<>();
        listStc.add(CodedValues.STC_AGUARD_PG_SDV.toString());
        boolean cicloVidaFixo = ParamSist.paramEquals(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, CodedValues.TPC_SIM, responsavel);
        boolean avancaFluxoSemCicloFixo = !cicloVidaFixo && ParamSist.paramEquals(CodedValues.TPC_AVANCA_FLUXO_COMPRA_SEM_CICLO_FIXO, CodedValues.TPC_SIM, responsavel);
        if (avancaFluxoSemCicloFixo) {
            listStc.add(CodedValues.STC_AGUARD_INFO_SDV.toString());
        }
        final List<TransferObject> relacionamentos = adeDelegate.pesquisarConsignacaoRelacionamento(adeCodigo, null, null, responsavel.getCsaCodigo(), CodedValues.TNT_CONTROLE_COMPRA, listStc, responsavel);

        if ((relacionamentos == null) || relacionamentos.isEmpty()) {
            throw new ZetraException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
        } else if (relacionamentos.size() > 1) {
            throw new ZetraException("mensagem.erro.mais.de.um.relacionamento.compra.contrato", responsavel);
        }

        final String obs = (String) parametros.get(OBS);

        // Salva anexo para ser vinculado ao contrato posteriormente
        final Anexo anexo = getAnexo(parametros.get(ANEXO));
        File file = null;
        if (anexo != null) {
            try {
                final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                if (TextHelper.isNull(diretorioRaizArquivos)) {
                    throw new ZetraException("mensagem.erro.diretorio.conf.inexistente", responsavel);
                }

                final String path = diretorioRaizArquivos + File.separatorChar + UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS + File.separatorChar + "anexo" + File.separatorChar + adeCodigo;
                file = salvarAnexo(path, anexo, UploadHelper.EXTENSOES_PERMITIDAS_UPLOAD_GENERICO);
            } catch (final IOException ex) {
                throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }

        if (file == null) {
            if (anexoObrigatorio) {
                throw new ZetraException("mensagem.informar.pagamento.saldo.devedor.anexo.obrigatorio", responsavel);
            }
            sdvDelegate.informarPagamentoSaldoDevedor(adeCodigo, obs, responsavel);
        } else {
            sdvDelegate.informarPagamentoSaldoDevedor(adeCodigo, obs, adeCodigo, file.getName(), file.getName(), responsavel);
        }

        final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
        // Verifica se a consignatária pode ser desbloqueada automaticamente
        if (responsavel.isCsaCor() && csaDelegate.verificarDesbloqueioAutomaticoConsignataria((String) parametros.get(CSA_CODIGO), responsavel)) {
            parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel) + ". " + ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.desbloqueada.automaticamente", responsavel));
        }
    }
}
