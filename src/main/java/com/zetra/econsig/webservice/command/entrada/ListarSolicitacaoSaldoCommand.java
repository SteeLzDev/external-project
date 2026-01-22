package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACOES;
import static com.zetra.econsig.webservice.CamposAPI.DATA_FIM_SOLICITACAO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_INICIO_SOLICITACAO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SALDO_EXCLUSAO;
import static com.zetra.econsig.webservice.CamposAPI.SALDO_INFORMACAO;
import static com.zetra.econsig.webservice.CamposAPI.SALDO_LIQUIDACAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoSolicitacaoEnum;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ListarSolicitacaoSaldoCommand</p>
 * <p>Description: Classe command para operação Listar solicitacao saldo do SOAP
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class ListarSolicitacaoSaldoCommand extends RequisicaoExternaCommand {

    public ListarSolicitacaoSaldoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        validaCodigoVerba(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final CustomTransferObject criterio = new CustomTransferObject();
        final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

        final String csaCodigo = responsavel.getCodigoEntidade();
        final String tipoEntidade = responsavel.getTipoEntidade();
        final Object adeNumero = parametros.get(ADE_NUMERO);
        final Object adeIdentificador = parametros.get(ADE_IDENTIFICADOR);
        final String rseCodigo = (String) parametros.get(RSE_CODIGO);
        final boolean saldoExclusao = (boolean) parametros.get(SALDO_EXCLUSAO);
        final boolean saldoLiquidacao = (boolean) parametros.get(SALDO_LIQUIDACAO);
        final boolean saldoInformacao = (boolean) parametros.get(SALDO_INFORMACAO);
        final boolean operacaoSOAPListarSolicitacaoSaldo = true;

        final List<String> listaTipoSolicitacaoSaldo = new ArrayList<>();

        final String estIdentificador = (String) parametros.get(EST_IDENTIFICADOR);
        String estCodigo = null;
        final String orgIdentificador = (String) parametros.get(ORG_IDENTIFICADOR);
        String orgCodigo = null;

        if (!TextHelper.isNull(estIdentificador)) {
            final TransferObject estabelecimento = cseDelegate.findEstabelecimentoByIdn(estIdentificador, responsavel);
            estCodigo = estabelecimento != null ? (String) estabelecimento.getAttribute(Columns.EST_CODIGO) : null;
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            CustomTransferObject orgao = null;
            final OrgaoTransferObject filtro = new OrgaoTransferObject();
            filtro.setOrgIdentificador(orgIdentificador);
            filtro.setAttribute(Columns.EST_IDENTIFICADOR, estIdentificador);

            final List<TransferObject> orgaos = cseDelegate.lstOrgaos(filtro, responsavel);

            // se encontrou um órgão distinto, recupera seus valores
            if ((orgaos != null) && !orgaos.isEmpty() && (orgaos.size() == 1)) {
                orgao = (CustomTransferObject) orgaos.get(0);
            } else {
                throw new ZetraException("mensagem.erro.orgao.nao.encontrado", responsavel);
            }

            orgCodigo = orgao != null ? (String) orgao.getAttribute(Columns.ORG_CODIGO) : null;
        }

        final String svcCodigo = (String) parametros.get(SVC_CODIGO);
        final String cnvCodVerba = (String) parametros.get(CNV_COD_VERBA);

        final String dataInicio = (String) parametros.get(DATA_INICIO_SOLICITACAO);
        final String dataFim = (String) parametros.get(DATA_FIM_SOLICITACAO);

        if (saldoExclusao) {
            listaTipoSolicitacaoSaldo.add(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO.getCodigo());
        }
        if (saldoLiquidacao) {
            listaTipoSolicitacaoSaldo.add(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo());
        }
        if (saldoInformacao) {
            listaTipoSolicitacaoSaldo.add(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo());
        }
        if (listaTipoSolicitacaoSaldo.isEmpty()) {
            // Se nenhum tipo de solicitação foi passado, retorna erro ao usuário
            throw new ZetraException("mensagem.solicitacao.saldo.devedor.tipo.selecionar", responsavel);
        }

        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_ESTOQUE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
        sadCodigos.add(CodedValues.SAD_EMCARENCIA);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);

        criterio.setAttribute(Columns.EST_CODIGO, estCodigo);
        criterio.setAttribute(Columns.ORG_CODIGO, orgCodigo);
        criterio.setAttribute("TIPO_OPERACAO", "solicitacao_saldo");
        criterio.setAttribute("operacaoSOAPListarSolicitacaoSaldo", operacaoSOAPListarSolicitacaoSaldo);
        criterio.setAttribute("listaTipoSolicitacaoSaldo", listaTipoSolicitacaoSaldo);
        criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
        criterio.setAttribute(Columns.CNV_COD_VERBA, cnvCodVerba);
        criterio.setAttribute("ocaDataIni", dataInicio);
        criterio.setAttribute("ocaDataFim", dataFim);
        criterio.setAttribute("diasSolicitacaoSaldo", "-1");
        criterio.setAttribute("diasSolicitacaoSaldoPagaAnexo", "-1");

        final List<TransferObject> consignacoes = adeDelegate.pesquisaAutorizacao(tipoEntidade, csaCodigo, rseCodigo, TextHelper.objectToStringList(adeNumero), TextHelper.objectToStringList(adeIdentificador), sadCodigos, TextHelper.objectToStringList(svcCodigo), criterio, responsavel);
        parametros.put(CONSIGNACOES, consignacoes);
    }
}
