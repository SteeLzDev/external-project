package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.DAD_VALOR;
import static com.zetra.econsig.webservice.CamposAPI.TDA_CODIGO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: IncluirDadoConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de incluir dado consignação</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class IncluirDadoConsignacaoCommand extends RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(IncluirDadoConsignacaoCommand.class);

    public IncluirDadoConsignacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        // Recupera tipo de dados que podem ser alterados
        AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        List<TransferObject> lstTda = adeDelegate.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.HOST_A_HOST, null, responsavel.getCsaCodigo(), responsavel);
        List<String> tdas = new ArrayList<>();
        if (lstTda != null && !lstTda.isEmpty()) {
            for (TransferObject tda : lstTda) {
                tdas.add(tda.getAttribute(Columns.TDA_CODIGO).toString());
            }
        }

        String tdaCodigo = (String) parametros.get(TDA_CODIGO);
        if (TextHelper.isNull(tdaCodigo)) {
            throw new ZetraException("mensagem.erro.tipo.dado.autorizacao.ausente", responsavel);
        } else if (!tdas.contains(tdaCodigo)) {
            throw new ZetraException("mensagem.erro.tipo.dado.autorizacao.nao.encontrado", responsavel);
        }
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        TransferObject autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);

        if (autorizacao != null) {
            String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();

            String tdaCodigo = (String) parametros.get(TDA_CODIGO);
            String dadValor = (String) parametros.get(DAD_VALOR);

            // Caso o valor seja vazio, considera como NULL para que seja removido
            if (dadValor != null && dadValor.isBlank()) {
                dadValor = null;
            }

            try {
                AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
                adeDelegate.setDadoAutDesconto(adeCodigo, tdaCodigo, dadValor, responsavel);

                List<TransferObject> dadosConsignacao = adeDelegate.lstDadoAutDesconto(adeCodigo, null, VisibilidadeTipoDadoAdicionalEnum.HOST_A_HOST, responsavel);
                parametros.put(DADOS_CONSIGNACAO, dadosConsignacao);
            } catch (AutorizacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw ex;
            }
        }
    }
}
