package com.zetra.econsig.helper.consignacao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: GAPHelper</p>
 * <p>Description: Helper para implementação do controle do
 * serviço tipo GAP.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GAPHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GAPHelper.class);

    public static List<TransferObject> lstMargemReservaGap(String rseCodigo, String orgCodigo, Short incMargem, Integer paramMesInicioDesconto, AcessoSistema responsavel) throws ViewHelperException {
        // Mês/Ano de início de desconto
        Integer mesInicioDesconto = null;
        Integer anoInicioDesconto = null;
        // Mês/Ano do período atual
        Integer mesPeriodoAtual = null;
        Integer anoPeriodoAtual = null;

        // Busca as margens para o registro servidor associadas ao serviço
        List<TransferObject> lstMargem = null;
        try {
            MargemController margemController = ApplicationContextProvider.getApplicationContext().getBean(MargemController.class);
            lstMargem = margemController.lstMargemReservaGap(rseCodigo, incMargem, responsavel);
            if (lstMargem != null && lstMargem.size() > 0) {
                // Calcula o período atual
                java.sql.Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                Calendar cal = Calendar.getInstance();
                cal.setTime(periodoAtual);
                mesPeriodoAtual = cal.get(Calendar.MONTH) + 1;
                anoPeriodoAtual = cal.get(Calendar.YEAR);

                // Calcula o mês/ano do inicio do desconto
                if (paramMesInicioDesconto != null) {
                    mesInicioDesconto = paramMesInicioDesconto;
                    // Se foi especificado mês de inicio de desconto, verifica
                    // se o mês já passou e calcula o ano de inicio
                    if (mesInicioDesconto.compareTo(mesPeriodoAtual) >= 0) {
                        // Se o mês de inicio ainda não chegou, então o ano de inicio é o ano atual
                        anoInicioDesconto = anoPeriodoAtual;
                    } else {
                        // Se o mês de inicio de desconto já passou, então o ano de início é o ano seguinte
                        anoInicioDesconto = Integer.valueOf(anoPeriodoAtual.intValue() + 1);
                    }
                } else {
                    // Se o mês de inicio não foi especificado, então
                    // será o mês do período atual
                    mesInicioDesconto = mesPeriodoAtual;
                    anoInicioDesconto = anoPeriodoAtual;
                }

                int i = 0;
                boolean temReserva = false;
                Iterator<TransferObject> itMargem = lstMargem.iterator();
                TransferObject margem = null;
                while (itMargem.hasNext()) {
                    margem = itMargem.next();
                    temReserva = (margem.getAttribute(Columns.ADE_CODIGO) != null && !margem.getAttribute(Columns.ADE_CODIGO).equals(""));

                    if (temReserva) {
                        // Se tem reserva, joga o ano de inicio de desconto para o ano seguinte a reserva
                        anoInicioDesconto = Integer.valueOf(Integer.parseInt(DateHelper.reformat(margem.getAttribute(Columns.ADE_ANO_MES_INI).toString(), "yyyy-MM-dd", "yyyy")) + 1);
                    }

                    if (temReserva) {
                        margem.setAttribute(Columns.ADE_VLR, "0,00");
                        margem.setAttribute(Columns.ADE_ANO_MES_INI, DateHelper.parse(margem.getAttribute(Columns.ADE_ANO_MES_INI).toString(), "yyyy-MM-01"));
                        i = 0;
                    } else {
                        margem.setAttribute(Columns.ADE_VLR, NumberHelper.reformat(margem.getAttribute(Columns.MRS_MARGEM_REST).toString(), "en", NumberHelper.getLang()));
                        Calendar dataDesconto = Calendar.getInstance();
                        dataDesconto.set(anoInicioDesconto.intValue() + i, mesInicioDesconto - 1, 1);
                        margem.setAttribute(Columns.ADE_ANO_MES_INI, dataDesconto.getTime());
                        i++;
                    }
                }

            } else {
                MargemTO margem = margemController.findMargem(new MargemTO(incMargem), responsavel);
                String marDescricao = margem.getMarDescricao();

                throw new ViewHelperException("mensagem.erro.servidor.nao.possui.margem.disponivel", responsavel, marDescricao);
            }
        } catch (Exception ex) {
            if (ex instanceof ViewHelperException) {
                throw (ViewHelperException) ex;
            } else if (ex instanceof PeriodoException) {
                throw new ViewHelperException(ex);
            } else {
                LOG.error(ex.getMessage(), ex);
                throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }

        return lstMargem;
    }

    public static List<TransferObject> lstMargemReservaGap(String rseCodigo, String orgCodigo, Short incMargem, List<Short> marCodigos, Integer paramMesInicioDesconto, AcessoSistema responsavel) throws ViewHelperException {
        List<TransferObject> lstResultado = new ArrayList<>();
        List<TransferObject> lstMargem = lstMargemReservaGap(rseCodigo, orgCodigo, incMargem, paramMesInicioDesconto, responsavel);
        Iterator<TransferObject> itMargem = lstMargem.iterator();
        Short marCodigo = null;
        CustomTransferObject margem = null;
        while (itMargem.hasNext()) {
            margem = (CustomTransferObject) itMargem.next();
            marCodigo = (Short) margem.getAttribute(Columns.MAR_CODIGO);
            if (marCodigos.contains(marCodigo)) {
                lstResultado.add(margem);
            }
        }

        return lstResultado;
    }
}
