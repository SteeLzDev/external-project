package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.DATA_DESCONTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PARCELAS_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO_PARCELA;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ListarParcelasCommand</p>
 * <p>Description: Classe command para operação Listar Parcelas do SOAP
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class ListarParcelasCommand extends RequisicaoExternaCommand {

    public ListarParcelasCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final ParcelaController parcelaController = ApplicationContextProvider.getApplicationContext().getBean(ParcelaController.class);
        final CustomTransferObject criterio = new CustomTransferObject();
        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        final String estIdentificador = (String) parametros.get(EST_IDENTIFICADOR);
        String estCodigo = null;
        final String orgIdentificador = (String) parametros.get(ORG_IDENTIFICADOR);
        String orgCodigo = null;

        if (!TextHelper.isNull(estIdentificador)) {
            final EstabelecimentoTransferObject estabelecimento = cseDelegate.findEstabelecimentoByIdn(estIdentificador, responsavel);
            estCodigo = estabelecimento != null ? estabelecimento.getEstCodigo() : null;
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            final OrgaoTransferObject filtro = new OrgaoTransferObject();
            filtro.setOrgIdentificador(orgIdentificador);
            filtro.setEstCodigo(estCodigo);

            final List<TransferObject> orgaos = cseDelegate.lstOrgaos(filtro, responsavel);

            // se encontrou um órgão distinto, recupera seus valores
            CustomTransferObject orgao = null;
            if ((orgaos != null) && !orgaos.isEmpty() && (orgaos.size() == 1)) {
                orgao = (CustomTransferObject) orgaos.get(0);
            } else {
                throw new ZetraException("mensagem.erro.orgao.nao.encontrado", responsavel);
            }

            orgCodigo = orgao != null ? (String) orgao.getAttribute(Columns.ORG_CODIGO) : null;
        }

        final Date prdDataDesconto = getDataDesconto(parametros);

        criterio.setAttribute(Columns.CSA_CODIGO, responsavel.getCodigoEntidade());
        criterio.setAttribute(Columns.PRD_DATA_DESCONTO, prdDataDesconto);
        criterio.setAttribute("SITUACAO_PARCELA", parametros.get(SITUACAO_PARCELA));
        criterio.setAttribute(Columns.ADE_NUMERO, parametros.get(ADE_NUMERO));
        criterio.setAttribute(Columns.ADE_IDENTIFICADOR, parametros.get(ADE_IDENTIFICADOR));
        criterio.setAttribute(Columns.EST_CODIGO, estCodigo);
        criterio.setAttribute(Columns.ORG_CODIGO, orgCodigo);
        criterio.setAttribute(Columns.SVC_IDENTIFICADOR, parametros.get(SVC_IDENTIFICADOR));
        criterio.setAttribute(Columns.CNV_COD_VERBA, parametros.get(CNV_COD_VERBA));
        criterio.setAttribute(Columns.SER_CPF, parametros.get(SER_CPF));
        criterio.setAttribute(Columns.RSE_MATRICULA, parametros.get(RSE_MATRICULA));

        final List<TransferObject> lstParcelas = parcelaController.listarParcelasPorCsa(criterio, responsavel);

        parametros.put(PARCELAS_CONSIGNACAO, lstParcelas);
    }

    protected Date getDataDesconto(Map<CamposAPI, Object> parametros) throws ZetraException {
        final Object prdDataDescontoStr = parametros.get(DATA_DESCONTO);

        if (prdDataDescontoStr instanceof Date) {
            return (Date) prdDataDescontoStr;
        }

        Date prdDataDesconto = null;
        if (!TextHelper.isNull(prdDataDescontoStr)) {
            try {
                if (prdDataDescontoStr.toString().matches("([0-9]{2})/([0-9]{4})")) {
                    prdDataDesconto = DateHelper.parsePeriodString(prdDataDescontoStr.toString());
                } else {
                    throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
                }
            } catch (final ParseException e) {
                throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
            }
        }
        return prdDataDesconto;
    }

}
