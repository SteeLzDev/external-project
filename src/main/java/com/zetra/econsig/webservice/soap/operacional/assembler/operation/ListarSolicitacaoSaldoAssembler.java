package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_FIM_SOLICITACAO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_INICIO_SOLICITACAO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SALDO_EXCLUSAO;
import static com.zetra.econsig.webservice.CamposAPI.SALDO_INFORMACAO;
import static com.zetra.econsig.webservice.CamposAPI.SALDO_LIQUIDACAO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.text.ParseException;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v8.ListarSolicitacaoSaldo;

/**
 * <p>Title: ListarSolicitacaoSaldoAssembler</p>
 * <p>Description: Assembler para ListarSolicitacaoSaldoAssembler.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Eduardo Fortes
 */

public class ListarSolicitacaoSaldoAssembler extends BaseAssembler{
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarSolicitacaoSaldoAssembler.class);

    private ListarSolicitacaoSaldoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ListarSolicitacaoSaldo listarSolicitacaoSaldo){
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, listarSolicitacaoSaldo.getUsuario());
        parametros.put(SENHA, listarSolicitacaoSaldo.getSenha());
        parametros.put(CONVENIO, getValue(listarSolicitacaoSaldo.getConvenio()));
        parametros.put(CLIENTE, getValue(listarSolicitacaoSaldo.getCliente()));

        parametros.put(ADE_NUMERO, getValue(listarSolicitacaoSaldo.getAdeNumero()));
        parametros.put(ADE_IDENTIFICADOR, getValue(listarSolicitacaoSaldo.getAdeIdentificador()));
        parametros.put(RSE_MATRICULA, getValue(listarSolicitacaoSaldo.getMatricula()));
        parametros.put(SER_CPF, getValue(listarSolicitacaoSaldo.getCpf()));

        parametros.put(ORG_IDENTIFICADOR, getValue(listarSolicitacaoSaldo.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(listarSolicitacaoSaldo.getEstabelecimentoCodigo()));
        parametros.put(SERVICO_CODIGO, getValue(listarSolicitacaoSaldo.getServicoCodigo()));
        parametros.put(CNV_COD_VERBA, getValue(listarSolicitacaoSaldo.getCodigoVerba()));

        final Date dataIni = getValueAsDate(listarSolicitacaoSaldo.getDataInicioSolicitacao());
        if (dataIni != null) {
            try {
                final String dataIniString = DateHelper.reformat(DateHelper.toDateString(dataIni), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                parametros.put(DATA_INICIO_SOLICITACAO, DateHelper.reformat(dataIniString, "yyyy-MM-dd 00:00:00", "yyyy-MM-dd 00:00:00"));
            } catch (final ParseException e) {
                LOG.warn("Formato do filtro \"Data de Inicio Solicitação\" inválido.", e);
            }
        }

        final Date dataFim = getValueAsDate(listarSolicitacaoSaldo.getDataFimSolicitacao());
        if (dataFim != null) {
            try {
                final String dataFimString = DateHelper.reformat(DateHelper.toDateString(dataFim), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                parametros.put(DATA_FIM_SOLICITACAO, DateHelper.reformat(dataFimString, "yyyy-MM-dd 00:00:00", "yyyy-MM-dd 00:00:00"));
            } catch (final ParseException e) {
                LOG.warn("Formato do filtro \"Data de Fim de Solicitação\" inválido.", e);
            }
        }

        parametros.put(SALDO_EXCLUSAO, listarSolicitacaoSaldo.isSaldoExclusao());
        parametros.put(SALDO_LIQUIDACAO, listarSolicitacaoSaldo.isSaldoLiquidacao());
        parametros.put(SALDO_INFORMACAO, listarSolicitacaoSaldo.isSaldoInformacao());

        return parametros;
    }

}
