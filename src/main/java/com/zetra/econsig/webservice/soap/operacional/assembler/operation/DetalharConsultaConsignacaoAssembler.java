package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.COR_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.FILTRO_AVANCADO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SAD_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;

/**
 * <p>Title: DetalharConsultaConsignacaoAssembler</p>
 * <p>Description: Assembler para DetalharConsultaConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class DetalharConsultaConsignacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DetalharConsultaConsignacaoAssembler.class);

    private DetalharConsultaConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v1.DetalharConsultaConsignacao detalharConsultaConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, detalharConsultaConsignacao.getUsuario());
        parametros.put(SENHA, detalharConsultaConsignacao.getSenha());
        parametros.put(RSE_MATRICULA, detalharConsultaConsignacao.getMatricula());
        parametros.put(SER_CPF, getValue(detalharConsultaConsignacao.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(detalharConsultaConsignacao.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(detalharConsultaConsignacao.getEstabelecimentoCodigo()));
        parametros.put(ADE_IDENTIFICADOR, getValue(detalharConsultaConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(detalharConsultaConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero > 0) && (adeNumero != Long.MAX_VALUE)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(detalharConsultaConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(detalharConsultaConsignacao.getCliente()));
        parametros.put(COR_IDENTIFICADOR, getValue(detalharConsultaConsignacao.getCorrespondenteCodigo()));
        parametros.put(SERVICO_CODIGO, getValue(detalharConsultaConsignacao.getServicoCodigo()));

        final CustomTransferObject criterio = new CustomTransferObject();

        criterio.setAttribute(Columns.CNV_COD_VERBA, getValue(detalharConsultaConsignacao.getCodigoVerba()));

        if (Boolean.TRUE.equals(getValue(detalharConsultaConsignacao.getSdvSolicitadoNaoCadastrado()))) {
            criterio.setAttribute("infSaldoDevedor", "2");
        } else if (Boolean.TRUE.equals(getValue(detalharConsultaConsignacao.getSdvSolicitadoCadastrado()))) {
            criterio.setAttribute("infSaldoDevedor", "3");
        } else if (Boolean.TRUE.equals(getValue(detalharConsultaConsignacao.getSdvSolicitado()))) {
            criterio.setAttribute("infSaldoDevedor", "1");
        } else if (Boolean.TRUE.equals(getValue(detalharConsultaConsignacao.getSdvNaoSolicitado()))) {
            criterio.setAttribute("infSaldoDevedor", "4");
        }

        final Date periodo = getValueAsDate(detalharConsultaConsignacao.getPeriodo());
        if (periodo != null) {
            criterio.setAttribute(Columns.ADE_ANO_MES_INI, DateHelper.format(DateHelper.toPeriodDate(periodo), "yyyy-MM-dd"));
        }

        final Date dataIni = getValueAsDate(detalharConsultaConsignacao.getDataInclusaoInicio());
        if (dataIni != null) {
            try {
                final String dataIniString = DateHelper.reformat(DateHelper.toDateString(dataIni), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoIni", DateHelper.reformat(dataIniString, "yyyy-MM-dd 00:00:00", "yyyy-MM-dd 00:00:00"));
            } catch (final ParseException e) {
                LOG.warn("Formato do filtro \"Data de Inclusão\" inválido.", e);
            }
        }

        final Date dataFim = getValueAsDate(detalharConsultaConsignacao.getDataInclusaoFim());
        if (dataFim != null) {
            try {
                final String dataFimString = DateHelper.reformat(DateHelper.toDateString(dataFim), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoFim", DateHelper.reformat(dataFimString, "yyyy-MM-dd 00:00:00", "yyyy-MM-dd 23:59:59"));
            } catch (final ParseException e) {
                LOG.warn("Formato do filtro \"Data de Inclusão\" inválido.", e);
            }
        }

        final Short valorIntFolha = getValue(detalharConsultaConsignacao.getIntegraFolha());
        if ((valorIntFolha == null) || (valorIntFolha == Short.MIN_VALUE)) {
            criterio.setAttribute(Columns.ADE_INT_FOLHA, null);
        } else {
            criterio.setAttribute(Columns.ADE_INT_FOLHA, valorIntFolha > 0 ? "1" : "0");
        }

        final Short incMargem = getValue(detalharConsultaConsignacao.getCodigoMargem());
        if ((incMargem != null) && (incMargem != Short.MIN_VALUE) && (incMargem >= 0)) {
            criterio.setAttribute(Columns.ADE_INC_MARGEM, incMargem);
        }

        criterio.setAttribute(Columns.ADE_INDICE, getValue(detalharConsultaConsignacao.getIndice()));

        final com.zetra.econsig.webservice.soap.operacional.v1.SituacaoServidor statusServidor = getValue(detalharConsultaConsignacao.getSituacaoServidor());
        if (statusServidor != null) {
            final List<String> rseSrsCodigo = new ArrayList<>();
            if (Boolean.TRUE.equals(statusServidor.getAtivo())) {
                rseSrsCodigo.add(CodedValues.SRS_ATIVO);
            }
            if (Boolean.TRUE.equals(statusServidor.getBloqueado())) {
                rseSrsCodigo.addAll(CodedValues.SRS_BLOQUEADOS);
            }
            if (Boolean.TRUE.equals(statusServidor.getExcluido())) {
                rseSrsCodigo.add(CodedValues.SRS_EXCLUIDO);
            }
            criterio.setAttribute(Columns.RSE_SRS_CODIGO, rseSrsCodigo);
        }

        parametros.put(FILTRO_AVANCADO, criterio);
        parametros.put(SAD_CODIGO, getValue(detalharConsultaConsignacao.getSituacaoContrato()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v4.DetalharConsultaConsignacao detalharConsultaConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, detalharConsultaConsignacao.getUsuario());
        parametros.put(SENHA, detalharConsultaConsignacao.getSenha());
        parametros.put(RSE_MATRICULA, detalharConsultaConsignacao.getMatricula());
        parametros.put(SER_CPF, getValue(detalharConsultaConsignacao.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(detalharConsultaConsignacao.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(detalharConsultaConsignacao.getEstabelecimentoCodigo()));
        parametros.put(ADE_IDENTIFICADOR, getValue(detalharConsultaConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(detalharConsultaConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero > 0) && (adeNumero != Long.MAX_VALUE)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(detalharConsultaConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(detalharConsultaConsignacao.getCliente()));
        parametros.put(COR_IDENTIFICADOR, getValue(detalharConsultaConsignacao.getCorrespondenteCodigo()));
        parametros.put(SERVICO_CODIGO, getValue(detalharConsultaConsignacao.getServicoCodigo()));

        final CustomTransferObject criterio = new CustomTransferObject();

        criterio.setAttribute(Columns.CNV_COD_VERBA, getValue(detalharConsultaConsignacao.getCodigoVerba()));

        if (Boolean.TRUE.equals(getValue(detalharConsultaConsignacao.getSdvSolicitadoNaoCadastrado()))) {
            criterio.setAttribute("infSaldoDevedor", "2");
        } else if (Boolean.TRUE.equals(getValue(detalharConsultaConsignacao.getSdvSolicitadoCadastrado()))) {
            criterio.setAttribute("infSaldoDevedor", "3");
        } else if (Boolean.TRUE.equals(getValue(detalharConsultaConsignacao.getSdvSolicitado()))) {
            criterio.setAttribute("infSaldoDevedor", "1");
        } else if (Boolean.TRUE.equals(getValue(detalharConsultaConsignacao.getSdvNaoSolicitado()))) {
            criterio.setAttribute("infSaldoDevedor", "4");
        }

        final Date periodo = getValueAsDate(detalharConsultaConsignacao.getPeriodo());
        if (periodo != null) {
            criterio.setAttribute(Columns.ADE_ANO_MES_INI, DateHelper.format(DateHelper.toPeriodDate(periodo), "yyyy-MM-dd"));
        }

        final Date dataIni = getValueAsDate(detalharConsultaConsignacao.getDataInclusaoInicio());
        if (dataIni != null) {
            try {
                final String dataIniString = DateHelper.reformat(DateHelper.toDateString(dataIni), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoIni", DateHelper.reformat(dataIniString, "yyyy-MM-dd 00:00:00", "yyyy-MM-dd 00:00:00"));
            } catch (final ParseException e) {
                LOG.warn("Formato do filtro \"Data de Inclusão\" inválido.", e);
            }
        }

        final Date dataFim = getValueAsDate(detalharConsultaConsignacao.getDataInclusaoFim());
        if (dataFim != null) {
            try {
                final String dataFimString = DateHelper.reformat(DateHelper.toDateString(dataFim), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoFim", DateHelper.reformat(dataFimString, "yyyy-MM-dd 00:00:00", "yyyy-MM-dd 23:59:59"));
            } catch (final ParseException e) {
                LOG.warn("Formato do filtro \"Data de Inclusão\" inválido.", e);
            }
        }

        final Short valorIntFolha = getValue(detalharConsultaConsignacao.getIntegraFolha());
        if ((valorIntFolha == null) || (valorIntFolha == Short.MIN_VALUE)) {
            criterio.setAttribute(Columns.ADE_INT_FOLHA, null);
        } else {
            criterio.setAttribute(Columns.ADE_INT_FOLHA, valorIntFolha > 0 ? "1" : "0");
        }

        final Short incMargem = getValue(detalharConsultaConsignacao.getCodigoMargem());
        if ((incMargem != null) && (incMargem != Short.MIN_VALUE) && (incMargem >= 0)) {
            criterio.setAttribute(Columns.ADE_INC_MARGEM, incMargem);
        }

        criterio.setAttribute(Columns.ADE_INDICE, getValue(detalharConsultaConsignacao.getIndice()));

        final com.zetra.econsig.webservice.soap.operacional.v4.SituacaoServidor statusServidor = getValue(detalharConsultaConsignacao.getSituacaoServidor());
        if (statusServidor != null) {
            final List<String> rseSrsCodigo = new ArrayList<>();
            if (Boolean.TRUE.equals(statusServidor.getAtivo())) {
                rseSrsCodigo.add(CodedValues.SRS_ATIVO);
            }
            if (Boolean.TRUE.equals(statusServidor.getPendente())) {
                rseSrsCodigo.add(CodedValues.SRS_PENDENTE);
            }
            if (Boolean.TRUE.equals(statusServidor.getBloqueado())) {
                rseSrsCodigo.addAll(CodedValues.SRS_BLOQUEADOS);
            }
            if (Boolean.TRUE.equals(statusServidor.getExcluido())) {
                rseSrsCodigo.add(CodedValues.SRS_EXCLUIDO);
            }
            if (Boolean.TRUE.equals(statusServidor.getFalecido())) {
                rseSrsCodigo.add(CodedValues.SRS_FALECIDO);
            }
            criterio.setAttribute(Columns.RSE_SRS_CODIGO, rseSrsCodigo);
        }

        parametros.put(FILTRO_AVANCADO, criterio);
        parametros.put(SAD_CODIGO, getValue(detalharConsultaConsignacao.getSituacaoContrato()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.DetalharConsultaConsignacao detalharConsultaConsignacao) {
        final com.zetra.econsig.webservice.soap.operacional.v4.DetalharConsultaConsignacao detalharConsultaConsignacaoV4 = new com.zetra.econsig.webservice.soap.operacional.v4.DetalharConsultaConsignacao();
        try {
            final com.zetra.econsig.webservice.soap.operacional.v4.ObjectFactory factoryV4 = new com.zetra.econsig.webservice.soap.operacional.v4.ObjectFactory();
            final com.zetra.econsig.webservice.soap.operacional.v4.SituacaoServidor situacaoServidorV4 = new com.zetra.econsig.webservice.soap.operacional.v4.SituacaoServidor();
            final com.zetra.econsig.webservice.soap.operacional.v6.SituacaoServidor situacaoServidor = getValue(detalharConsultaConsignacao.getSituacaoServidor());
            if (situacaoServidor != null) {
                BeanUtils.copyProperties(situacaoServidorV4, situacaoServidor);
            }
            BeanUtils.copyProperties(detalharConsultaConsignacaoV4, detalharConsultaConsignacao);
            detalharConsultaConsignacaoV4.setSituacaoServidor(factoryV4.createDetalharConsultaConsignacaoSituacaoServidor(situacaoServidorV4));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        final Map<CamposAPI, Object> parametros = toMap(detalharConsultaConsignacaoV4);
        parametros.put(SAD_CODIGO, getValue(detalharConsultaConsignacao.getSituacaoContrato()));

        return parametros;
    }
}
