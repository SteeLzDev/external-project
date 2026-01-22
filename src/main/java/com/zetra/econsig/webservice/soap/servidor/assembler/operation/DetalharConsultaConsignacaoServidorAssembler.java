package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.COR_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.FILTRO_AVANCADO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SAD_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;

import java.text.ParseException;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.DetalharConsultaConsignacaoServidor;

/**
 * <p>Title: DetalharConsultaConsignacaoServidorAssembler</p>
 * <p>Description: Assembler para DetalharConsultaConsignacaoServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class DetalharConsultaConsignacaoServidorAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DetalharConsultaConsignacaoServidorAssembler.class);

    private DetalharConsultaConsignacaoServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(DetalharConsultaConsignacaoServidor detalharConsultaConsignacaoServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(SENHA, detalharConsultaConsignacaoServidor.getSenha());
        parametros.put(RSE_MATRICULA, detalharConsultaConsignacaoServidor.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(detalharConsultaConsignacaoServidor.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(detalharConsultaConsignacaoServidor.getEstabelecimentoCodigo()));
        parametros.put(CSA_IDENTIFICADOR, getValue(detalharConsultaConsignacaoServidor.getConsignatariaCodigo()));
        parametros.put(SER_LOGIN, getValue(detalharConsultaConsignacaoServidor.getLoginServidor()));
        parametros.put(ADE_IDENTIFICADOR, getValue(detalharConsultaConsignacaoServidor.getAdeIdentificador()));
        final Long adeNumero = getValue(detalharConsultaConsignacaoServidor.getAdeNumero());
        if ((adeNumero == null) || (adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(COR_IDENTIFICADOR, getValue(detalharConsultaConsignacaoServidor.getCorrespondenteCodigo()));
        parametros.put(SERVICO_CODIGO, getValue(detalharConsultaConsignacaoServidor.getServicoCodigo()));

        final CustomTransferObject criterio = new CustomTransferObject();

        criterio.setAttribute(Columns.CNV_COD_VERBA, getValue(detalharConsultaConsignacaoServidor.getCodigoVerba()));

        if (Boolean.TRUE.equals(getValue(detalharConsultaConsignacaoServidor.getSdvSolicitadoNaoCadastrado()))) {
            criterio.setAttribute("infSaldoDevedor", "2");
        } else if (Boolean.TRUE.equals(getValue(detalharConsultaConsignacaoServidor.getSdvSolicitadoCadastrado()))) {
            criterio.setAttribute("infSaldoDevedor", "3");
        } else if (Boolean.TRUE.equals(getValue(detalharConsultaConsignacaoServidor.getSdvSolicitado()))) {
            criterio.setAttribute("infSaldoDevedor", "1");
        } else if (Boolean.TRUE.equals(getValue(detalharConsultaConsignacaoServidor.getSdvNaoSolicitado()))) {
            criterio.setAttribute("infSaldoDevedor", "4");
        }

        final Date periodo = getValueAsDate(detalharConsultaConsignacaoServidor.getPeriodo());
        if (periodo != null) {
            criterio.setAttribute(Columns.ADE_ANO_MES_INI, DateHelper.format(DateHelper.toPeriodDate(periodo), "yyyy-MM-dd"));
        }

        final Date dataIni = getValueAsDate(detalharConsultaConsignacaoServidor.getDataInclusaoInicio());
        if (dataIni != null) {
            try {
                final String dataIniString = DateHelper.reformat(DateHelper.toDateString(dataIni), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoIni", DateHelper.reformat(dataIniString, "yyyy-MM-dd 00:00:00", "yyyy-MM-dd 00:00:00"));
            } catch (final ParseException e) {
                LOG.warn("Formato do filtro \"Data de Inclusão\" inválido.", e);
            }
        }

        final Date dataFim = getValueAsDate(detalharConsultaConsignacaoServidor.getDataInclusaoFim());
        if (dataFim != null) {
            try {
                final String dataFimString = DateHelper.reformat(DateHelper.toDateString(dataFim), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoFim", DateHelper.reformat(dataFimString, "yyyy-MM-dd 00:00:00", "yyyy-MM-dd 23:59:59"));
            } catch (final ParseException e) {
                LOG.warn("Formato do filtro \"Data de Inclusão\" inválido.", e);
            }
        }

        final Short valorIntFolha = getValue(detalharConsultaConsignacaoServidor.getIntegraFolha());
        if ((valorIntFolha == null) || (valorIntFolha == Short.MIN_VALUE)) {
            criterio.setAttribute(Columns.ADE_INT_FOLHA, null);
        } else {
            criterio.setAttribute(Columns.ADE_INT_FOLHA, valorIntFolha > 0 ? "1" : "0");
        }

        final Short incMargem = getValue(detalharConsultaConsignacaoServidor.getCodigoMargem());
        if ((incMargem != null) && (incMargem != Short.MIN_VALUE) && (incMargem >= 0)) {
            criterio.setAttribute(Columns.ADE_INC_MARGEM, incMargem);
        }

        criterio.setAttribute(Columns.ADE_INDICE, getValue(detalharConsultaConsignacaoServidor.getIndice()));

        parametros.put(FILTRO_AVANCADO, criterio);
        parametros.put(SAD_CODIGO, getValue(detalharConsultaConsignacaoServidor.getSituacaoContrato()));

        return parametros;
    }
}