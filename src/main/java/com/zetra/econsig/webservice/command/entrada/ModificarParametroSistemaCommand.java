package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CSE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PSI_VLR;
import static com.zetra.econsig.webservice.CamposAPI.TPC_CODIGO;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.TipoParamSistConsignante;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ModificarParametroSistemaCommand</p>
 * <p>Description: Classe command que trata requisição externa ao eConsig para modificar parametro de sistema</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ModificarParametroSistemaCommand extends RequisicaoExternaFolhaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ModificarParametroSistemaCommand.class);

    public ModificarParametroSistemaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        ConsignanteDelegate consignanteDelegate = new ConsignanteDelegate();

        ConsignanteTransferObject cse = consignanteDelegate.findConsignanteByIdn(parametros.get(CSE_IDENTIFICADOR).toString(), responsavel);
        String tpcCodigo = parametros.get(TPC_CODIGO).toString();
        String valorAtual = "";

        try {
            valorAtual = parametroController.findParamSistCse(parametros.get(TPC_CODIGO).toString(), cse.getCseCodigo(), responsavel);
        } catch (ParametroControllerException ex) {
            //caso não encontre o valor parametro continua o processo normalmente;
        }

        String valorNovo = (String) parametros.get(PSI_VLR);

        TipoParamSistConsignante tipoParam = parametroController.findTipoParamSistConsignante(tpcCodigo, responsavel);

        if (tipoParam == null) {
            throw new ZetraException("mensagem.erro.parametro.sistema.incorreto", responsavel);
        }

        // Para remover o sistema atualiza o valor para vazio
        if (TextHelper.isNull(valorNovo) && tipoParam.getTpcDominio().equals("SN")) {
            valorNovo = "";
        }

        if (!TextHelper.isNull(valorNovo) && (tipoParam.getTpcDominio().equals("MONETARIO") || tipoParam.getTpcDominio().equals("FLOAT"))) {
            try {
                valorNovo = NumberHelper.reformat(valorNovo, NumberHelper.getLang(), "en");
            } catch (java.text.ParseException ex) {
              LOG.error(ex.getMessage(), ex);
            }
        }
        // código customizado para o dia de corte
        if (tpcCodigo.equals(CodedValues.TPC_DIA_CORTE)) {
            if (!TextHelper.isNull(valorNovo) && StringUtils.isNumeric(valorNovo)) {
                Integer diaCorte;
                try {
                    diaCorte = Integer.valueOf(valorNovo);
                    CalendarioController calendarioController = ApplicationContextProvider.getApplicationContext().getBean(CalendarioController.class);
                    calendarioController.updateTodosCalendarioFolha(diaCorte, AcessoSistema.ENTIDADE_CSE, cse.getCseCodigo(), false, responsavel);
                } catch (Exception e){
                    throw new ZetraException("mensagem.erro.valor.incorreto", responsavel, e);
                }
            } else {
                throw new ZetraException("mensagem.erro.valor.incorreto", responsavel);
            }
        } else if (tpcCodigo.equals(CodedValues.TPC_DIA_PREVISAO_RETORNO)) {
            if (!TextHelper.isNull(valorNovo) && StringUtils.isNumeric(valorNovo)) {
                Integer dataPrevistaRetorno;
                try {
                    dataPrevistaRetorno = Integer.valueOf(valorNovo);
                    CalendarioController calendarioController = ApplicationContextProvider.getApplicationContext().getBean(CalendarioController.class);
                    calendarioController.updateCalendarioFolhaRetorno(dataPrevistaRetorno, AcessoSistema.ENTIDADE_CSE, cse.getCseCodigo(), responsavel);
                } catch (Exception e){
                    throw new ZetraException("mensagem.erro.valor.incorreto", responsavel, e);
                }
            } else {
                throw new ZetraException("mensagem.erro.valor.incorreto", responsavel);
            }
        } else if (!valorNovo.equals(valorAtual)) {
            parametroController.updateParamSistCse(valorNovo, tpcCodigo, cse.getCseCodigo(), responsavel);
            ParamSist.getInstance().setParam(tpcCodigo, valorNovo);
        }
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
    }
}
