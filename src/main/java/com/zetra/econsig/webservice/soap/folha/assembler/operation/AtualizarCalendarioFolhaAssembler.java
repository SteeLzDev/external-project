package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ENTIDADE;
import static com.zetra.econsig.webservice.CamposAPI.DIA_CORTE;
import static com.zetra.econsig.webservice.CamposAPI.DIA_PREVISAO_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO_INICIAL;
import static com.zetra.econsig.webservice.CamposAPI.QUANTIDADE_PERIODOS;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ENTIDADE;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.AtualizarCalendarioFolha;

/**
 * <p>Title: AtualizarCalendarioFolhaAssembler</p>
 * <p>Description: Assembler para AtualizarCalendarioFolha.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class AtualizarCalendarioFolhaAssembler extends BaseAssembler {

    private AtualizarCalendarioFolhaAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(AtualizarCalendarioFolha atualizarCalendarioFolha) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        parametros.put(USUARIO, atualizarCalendarioFolha.getUsuario());
        parametros.put(SENHA, atualizarCalendarioFolha.getSenha());

        parametros.put(CODIGO_ENTIDADE, atualizarCalendarioFolha.getCodigoEntidade());
        parametros.put(DIA_CORTE, atualizarCalendarioFolha.getDiaCorte());
        parametros.put(PERIODO_INICIAL, atualizarCalendarioFolha.getPeriodoInicial());
        parametros.put(QUANTIDADE_PERIODOS, atualizarCalendarioFolha.getQuantidadePeriodos());
        parametros.put(TIPO_ENTIDADE, atualizarCalendarioFolha.getTipoEntidade());
        parametros.put(DIA_PREVISAO_RETORNO, atualizarCalendarioFolha.getDiaPrevisaoRetorno());

        return parametros;
    }
}