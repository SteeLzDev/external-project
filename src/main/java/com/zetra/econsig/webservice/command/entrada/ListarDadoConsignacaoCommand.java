package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.TDA_CODIGO;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ListarDadoConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de listar dado consignação</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarDadoConsignacaoCommand extends RequisicaoExternaCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarDadoConsignacaoCommand.class);

    public ListarDadoConsignacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        TransferObject autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);

        if (autorizacao != null) {
            String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();
            String tdaCodigo = (String) parametros.get(TDA_CODIGO);

            try {
                AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
                List<TransferObject> dadosConsignacao = adeDelegate.lstDadoAutDesconto(adeCodigo, tdaCodigo, VisibilidadeTipoDadoAdicionalEnum.HOST_A_HOST, responsavel);
                parametros.put(DADOS_CONSIGNACAO, dadosConsignacao);
            } catch (AutorizacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }
}
