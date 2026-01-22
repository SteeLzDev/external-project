package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ENTIDADE;
import static com.zetra.econsig.webservice.CamposAPI.DIA_CORTE;
import static com.zetra.econsig.webservice.CamposAPI.DIA_PREVISAO_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO_INICIAL;
import static com.zetra.econsig.webservice.CamposAPI.QUANTIDADE_PERIODOS;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ENTIDADE;

import java.util.Arrays;
import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: AtualizarCalendarioFolhaCommand</p>
 * <p>Description: Classe command que trata requisição externa ao eConsig para cadastro calendario folha</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AtualizarCalendarioFolhaCommand extends RequisicaoExternaFolhaCommand {

    public AtualizarCalendarioFolhaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String tipoEntidade = (String) parametros.get(TIPO_ENTIDADE);
        String codigoEntidade = (String) parametros.get(CODIGO_ENTIDADE);
        Integer diaCorte = (Integer) parametros.get(DIA_CORTE);
        String periodoInicial = (String) parametros.get(PERIODO_INICIAL);
        Integer quantidadePeriodos = (Integer) parametros.get(QUANTIDADE_PERIODOS);
        Integer diaMesPrevisao = (Integer) parametros.get(DIA_PREVISAO_RETORNO);

        CalendarioController calendarioController = ApplicationContextProvider.getApplicationContext().getBean(CalendarioController.class);
        calendarioController.atualizarCalendarioFolhaPorTipoEntidade(tipoEntidade, codigoEntidade, diaCorte, periodoInicial, quantidadePeriodos, diaMesPrevisao, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);


        String tipoEntidade = (String) parametros.get(TIPO_ENTIDADE);
        String codigoEntidade = (String) parametros.get(CODIGO_ENTIDADE);
        Integer diaCorte = (Integer) parametros.get(DIA_CORTE);
        String periodoInicial = (String) parametros.get(PERIODO_INICIAL);
        Integer quantidadePeriodos = (Integer) parametros.get(QUANTIDADE_PERIODOS);

        if (TextHelper.isNull(tipoEntidade) || !Arrays.asList("EST", "ORG", "CSE").contains(tipoEntidade)) {
            throw new ZetraException("mensagem.cadastrar.calendario.folha.erro.tipo.entidade", responsavel);
        }

        if (TextHelper.isNull(codigoEntidade)) {
            throw new ZetraException("mensagem.cadastrar.calendario.folha.erro.codigo.entidade", responsavel);
        }

        if (TextHelper.isNull(diaCorte)) {
            throw new ZetraException("mensagem.cadastrar.calendario.folha.erro.dia.corte", responsavel);
        }

        if (TextHelper.isNull(periodoInicial)) {
            throw new ZetraException("mensagem.cadastrar.calendario.folha.erro.periodo.inicial", responsavel);
        }

        if (TextHelper.isNull(quantidadePeriodos)) {
            throw new ZetraException("mensagem.cadastrar.calendario.folha.erro.quantidade.periodos", responsavel);
        }
    }
}
