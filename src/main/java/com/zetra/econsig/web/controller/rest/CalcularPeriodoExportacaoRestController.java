package com.zetra.econsig.web.controller.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.PeriodoExportacaoDTO;
import com.zetra.econsig.helper.folha.ExportaMovimentoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: CalcularPeriodoExportacaoRestController</p>
 * <p>Description: REST Controller para cálculo do período de exportação.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@RestController
public class CalcularPeriodoExportacaoRestController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CalcularPeriodoExportacaoRestController.class);

    @Autowired
    private PeriodoController periodoController;

    @RequestMapping(value = "/v3/calcularPeriodo", method = RequestMethod.POST)
    public List<PeriodoExportacaoDTO> calcularPeriodo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        String ipsAcessoLiberado = (String) ParamSist.getInstance().getParam(CodedValues.TPC_IPS_LIBERADOS_PAGINA_ADMINISTRACAO, responsavel);
        if (TextHelper.isNull(ipsAcessoLiberado)) {
            ipsAcessoLiberado = "127.0.0.1";
        }

        if (!JspHelper.validaDDNS(JspHelper.getRemoteAddr(request), ipsAcessoLiberado)) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            return null;
        }

        // Verifica se foi passado órgãos ou estabelecimentos
        String[] org = request.getParameterValues("org");
        String[] est = request.getParameterValues("est");

        List<String> orgCodigos = null;
        List<String> estCodigos = null;

        if (org != null && org.length > 0) {
            orgCodigos = Arrays.asList(org);
        }
        if (est != null && est.length > 0) {
            estCodigos = Arrays.asList(est);
        }

        // Se deve calcular o último período e não o atual
        boolean ultimoPeriodo = "true".equalsIgnoreCase(request.getParameter("ultimoPeriodo")) || "1".equals(request.getParameter("ultimoPeriodo"));
        // Se deve retornar registros distintos
        boolean distintos = "true".equalsIgnoreCase(request.getParameter("distintos")) || "1".equals(request.getParameter("distintos"));

        try {
            List<TransferObject> periodoExportacao = periodoController.obtemPeriodoExpMovimento(orgCodigos, estCodigos, false, ultimoPeriodo, responsavel);

            if (periodoExportacao != null && !periodoExportacao.isEmpty()) {
                ExportaMovimentoHelper.imprimePeriodoExportacao(periodoExportacao);

                List<PeriodoExportacaoDTO> listaPeriodo = new ArrayList<>(periodoExportacao.size());
                for (int i = 0; i < periodoExportacao.size(); i++) {
                    PeriodoExportacaoDTO periodoDTO = new PeriodoExportacaoDTO();
                    periodoDTO.setOrgCodigo((String) periodoExportacao.get(i).getAttribute(Columns.ORG_CODIGO));
                    periodoDTO.setOrgNome((String) periodoExportacao.get(i).getAttribute(Columns.ORG_NOME));
                    periodoDTO.setDiaCorte(Short.valueOf(periodoExportacao.get(i).getAttribute(Columns.PEX_DIA_CORTE).toString()));
                    periodoDTO.setPeriodo(DateHelper.format((Date) periodoExportacao.get(i).getAttribute(Columns.PEX_PERIODO),  "yyyy-MM-dd"));
                    periodoDTO.setDataIni(DateHelper.format((Date) periodoExportacao.get(i).getAttribute(Columns.PEX_DATA_INI), "yyyy-MM-dd HH:mm:ss"));
                    periodoDTO.setDataFim(DateHelper.format((Date) periodoExportacao.get(i).getAttribute(Columns.PEX_DATA_FIM), "yyyy-MM-dd HH:mm:ss"));
                    listaPeriodo.add(periodoDTO);
                }

                if (distintos) {
                    List<PeriodoExportacaoDTO> listaPeriodoDistinta = listaPeriodo.stream().distinct().collect(Collectors.toList());
                    if (listaPeriodoDistinta.size() == 1) {
                        // Se só tem um registro de mesma data ini/fim e período, então retorna este registro genérico
                        listaPeriodoDistinta.get(0).setOrgCodigo("*");
                        listaPeriodoDistinta.get(0).setOrgNome("*");
                        return listaPeriodoDistinta;
                    }
                }

                return listaPeriodo;
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            return null;
        }

        response.setStatus(Response.Status.NOT_FOUND.getStatusCode());
        return null;
    }
}
