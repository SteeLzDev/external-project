package com.zetra.econsig.job.process;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;


/**
 * <p>Title: ProcessaRelatorioMovSer</p>
 * <p>Description: Classe para processamento de relatorio de movimento financeiro
 * do servidor</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public final class ProcessaRelatorioMovFinSer extends ProcessaRelatorio {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioMovFinSer.class);

    public ProcessaRelatorioMovFinSer(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {

        LOG.debug("RELATORIO DE MOVIMENTO FINANCEIRO DO SERVIDOR");

        final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);

        String reportName = null;

        String strIniPeriodo = "";
        String paramIniPeriodo = "";
        String strFimPeriodo = "";
        String paramFimPeriodo = "";

        String tipoEntidade = responsavel.getTipoEntidade();
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO) &&
                parameterMap.containsKey("orgCodigo")) {
            tipoEntidade = "EST";
        }

        if (parameterMap.containsKey("periodoIni") && parameterMap.containsKey("periodoFim")) {
            strIniPeriodo = getParametro("periodoIni", parameterMap);
            strFimPeriodo = getParametro("periodoFim", parameterMap);
            paramIniPeriodo = formatarPeriodo(strIniPeriodo);
            paramFimPeriodo = formatarPeriodo(strFimPeriodo);
        } else {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel);
            return;
        }


        String prd = "";

        if (!strIniPeriodo.equals(strFimPeriodo)) {
            prd = strIniPeriodo + " a " + strFimPeriodo;
        } else {
            prd = strIniPeriodo;
        }

        Date dataIni = null;
        Date dataFim = null;
        try {
            dataIni = DateHelper.parsePeriodString(strIniPeriodo);
            dataFim = DateHelper.parsePeriodString(strFimPeriodo);
        } catch (final ParseException ex) {
            LOG.debug(ex.getMessage());
        }

        final int mesesDiff = DateHelper.monthDiff(dataFim, dataIni) + 1;

        if (mesesDiff <= 0){
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.ini.menor.fim", responsavel, strFimPeriodo, strIniPeriodo);
            return;
        }

        if (mesesDiff > 24){
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.dentro.dois.anos", responsavel);
            return;
        }

        final String titulo = relatorio.getTitulo() + " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, prd);

        final StringBuilder nome = new StringBuilder((getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio." + relatorio.getTipo().replace("_", "."), responsavel), responsavel, parameterMap, null)));

        final String[] sad = (parameterMap.get("SAD_CODIGO"));
        final List<String> sadCodigos = sad != null ? Arrays.asList(sad) : null;
        final String[] spd = (parameterMap.get("SPD_CODIGO"));
        final List<String> spdCodigos = spd != null ? Arrays.asList(spd) : null;

        final StringBuilder subTitulo = new StringBuilder();

        final String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final String sboCodigo = getFiltroSboCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final String uniCodigo = getFiltroUniCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> nseCodigos = getFiltroNseCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final String matricula = getFiltroRseMatricula(parameterMap, subTitulo, nome, session, responsavel);
        final String cpf = getFiltroCpf(parameterMap, subTitulo, nome, session, responsavel);
        Servidor servidor = null;
        RegistroServidor rseTipo = null;

        try {
            servidor = servidorController.findByCpfMatricula(matricula, cpf, responsavel);
        } catch (final ServidorControllerException ex) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
        }

        if(TextHelper.isNull(servidor)){
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel);
            return;
        }

        try {
            rseTipo = servidorController.findRseTipo(matricula, servidor.getSerCodigo(), responsavel);
        } catch (final ServidorControllerException ex) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
        }

        String nomeServidor = null;
        if (!TextHelper.isNull(servidor)) {
            nomeServidor = servidor.getSerNome() + " CPF: " + servidor.getSerCpf() + " CAT. servidor: " + rseTipo.getRseTipo();
        } else {
            nomeServidor = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel);
        }

        final String estCodigo = (tipoEntidade.equals("EST")) ? responsavel.getEstCodigo() : getFiltroEstCodigo(parameterMap, subTitulo, nome, session, responsavel);


        String nomeEntidade = "";

        if (!responsavel.isCseSup()) {
            nomeEntidade = ((tipoEntidade == null) || !tipoEntidade.equals("EST")) ? responsavel.getNomeEntidade() : responsavel.getNomeEntidadePai();
        }

        final List<String> lstPrdRealizado = getFiltroParcelaRealizado(parameterMap, subTitulo, nome, session, responsavel);
        // Se usuário csa e não agrupa, exibe por consignatária
        final boolean agrupamento = getFiltroAgrupamento(parameterMap, subTitulo, nome, session, responsavel);
        final String order = (responsavel.isCseSupOrg() && (csaCodigo == null)) ? "CONSIGNATARIA" : (agrupamento ? "ORGAO" : "CONSIGNATARIA");
        final String tituloGrupo = (responsavel.isCseSupOrg() && (csaCodigo == null)) ? ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel).toUpperCase() : (agrupamento ? ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel).toUpperCase());
        final String formatoArquivo = getStrFormato();
        subTitulo.append(System.lineSeparator());

        // Correspondente
        final List<String> corCodigos = new ArrayList<>();
        if (responsavel.isCor()) {
            corCodigos.add(getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel));
        }

        criterio.setAttribute("TIPO_ENTIDADE", tipoEntidade);
        criterio.setAttribute("ORG_CODIGO", orgCodigo);
        criterio.setAttribute("SBO_CODIGO", sboCodigo);
        criterio.setAttribute("UNI_CODIGO", uniCodigo);
        criterio.setAttribute("EST_CODIGO", estCodigo);
        criterio.setAttribute("CSA_CODIGO", csaCodigo);
        criterio.setAttribute("RSE_MATRICULA", matricula);
        criterio.setAttribute("CPF", cpf);

        if (parameterMap.containsKey("corCodigo")) {
            final String[] correspondentes = (parameterMap.get("corCodigo"));
            if (!correspondentes[0].equals("")) {
                if ((correspondentes.length == 0) || correspondentes[0].substring(0, 2).equals("-1")) {
                    corCodigos.add("-1");
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                } else {
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel)).append(": ");
                    for (final String cor : correspondentes) {
                        final String[] correspondente = cor.split(";");
                        corCodigos.add(correspondente[0]);
                        subTitulo.append(correspondente[2]).append(", ");
                    }
                    subTitulo.deleteCharAt(subTitulo.length() - 2);
                }
                subTitulo.append(System.lineSeparator());
                criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
            }
        }
        criterio.setAttribute("SVC_CODIGO", svcCodigos);
        criterio.setAttribute("NSE_CODIGO", nseCodigos);
        criterio.setAttribute("PERIODOINI", paramIniPeriodo);
        criterio.setAttribute("PERIODOFIM", paramFimPeriodo);
        criterio.setAttribute("SAD_CODIGO", sadCodigos);
        criterio.setAttribute("SPD_CODIGO", spdCodigos);
        criterio.setAttribute("ORDER", order);
        criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
        criterio.setAttribute("PRD_REALIZADO", lstPrdRealizado);
        if (responsavel.isCor() && TextHelper.isNull(parameterMap.get("corCodigo"))) {
            criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
        }

        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("NOME_ENTIDADE", nomeEntidade);
        parameters.put("TIPO_ENTIDADE", tipoEntidade);
        parameters.put("ORDER", order);
        parameters.put("SERVIDOR", nomeServidor);
        parameters.put("TITULO_GRUPO", tituloGrupo);
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, formatoArquivo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
        parameters.put("RESPONSAVEL", responsavel);

        try {
            final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(formatoArquivo, criterio, parameters, relatorio, responsavel);

            final String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (final Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }


    }
}
