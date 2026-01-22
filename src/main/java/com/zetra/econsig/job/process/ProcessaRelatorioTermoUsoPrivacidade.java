package com.zetra.econsig.job.process;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dto.TermoUsoPrivacidadeAdesaoBean;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioTermoUsoPrivacidade</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author:  $
 * $Revision:  $
 * $Date:  $
 */
public class ProcessaRelatorioTermoUsoPrivacidade extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioTermoUsoPrivacidade.class);

    public ProcessaRelatorioTermoUsoPrivacidade(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String strIniPeriodo = "";
        String strFimPeriodo = "";
        Date paramIniPeriodo;
        Date paramFimPeriodo;
        String[] origem = parameterMap.get("origem");
        String[] termo = parameterMap.get("termo");
        String[] papel = parameterMap.get("papel");
        StringBuilder nomeArquivo = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.termo.privacidade", responsavel), responsavel, parameterMap, null));
        String titulo = relatorio.getTitulo();
        StringBuilder subTitulo = new StringBuilder();
        String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;
        boolean aceiteWeb = false;
        boolean aceiteMobile = false;
        boolean aceiteTermo = false;
        boolean aceitePrivacidade = false;
        boolean aceiteTermoAdesaoAutorizado = false;
        boolean aceiteTermoAdesaoNaoAutorizado = false;
        boolean cse = false;
        boolean org = false;
        boolean csa = false;
        boolean cor = false;
        boolean ser = false;
        boolean sup = false;

        for (String i : origem) {
            aceiteWeb = aceiteWeb || i.equals("1") ? true : false;
            aceiteMobile = aceiteMobile || i.equals("2") ? true : false;
        }
        for (String i : termo) {
            aceiteTermo = aceiteTermo || i.equals("1") ? true : false;
            aceitePrivacidade = aceitePrivacidade || i.equals("2") ? true : false;
            aceiteTermoAdesaoAutorizado = aceiteTermoAdesaoAutorizado || i.equals("3") ? true : false;
            aceiteTermoAdesaoNaoAutorizado = aceiteTermoAdesaoNaoAutorizado || i.equals("4") ? true : false;
        }

        for (String i : papel) {
            cse = cse || i.equals("cse") ? true : false;
            org = org || i.equals("org") ? true : false;
            csa = csa || i.equals("csa") ? true : false;
            cor = cor || i.equals("cor") ? true : false;
            ser = ser || i.equals("ser") ? true : false;
            sup = sup || i.equals("sup") ? true : false;
        }

        String strFormato = getStrFormato();

        criterio.setAttribute("responsavel", responsavel);

        if (parameterMap.containsKey("periodoIni") && parameterMap.containsKey("periodoFim")) {
            strIniPeriodo = getParametro("periodoIni", parameterMap);
            strFimPeriodo = getParametro("periodoFim", parameterMap);
            if (!TextHelper.isNull(strIniPeriodo) && !TextHelper.isNull(strFimPeriodo)) {
                try {
                    paramIniPeriodo = DateHelper.parse(strIniPeriodo, "dd/MM/yyyy");
                    paramFimPeriodo = DateHelper.parse(strFimPeriodo + " 23:59:59", "dd/MM/yyyy HH:mm:ss");
                    criterio.setAttribute("periodoIni", paramIniPeriodo);
                    criterio.setAttribute("periodoFim", paramFimPeriodo);
                } catch (ParseException ex) {
                    codigoRetorno = ERRO;
                    mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                    LOG.error(mensagem, ex);
                }
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo));
            }
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        List<String> tocCodigo = new ArrayList<>();

        if (aceiteMobile && aceiteWeb) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.aceitacao.via.de.arg0.a.arg1", responsavel, ApplicationResourcesHelper.getMessage("rotulo.solicitacao.via.web",responsavel), ApplicationResourcesHelper.getMessage("rotulo.solicitacao.via.mobile", responsavel)));
            if (aceiteTermo && aceitePrivacidade) {
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO);
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO_MOBILE);
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO_SALARYPAY);
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_MOBILE);
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_SISTEMA);

                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.documento.aceito.de.arg0.a.arg1", responsavel, ApplicationResourcesHelper.getMessage("rotulo.termo.de.uso", responsavel), ApplicationResourcesHelper.getMessage("rotulo.politica.privacidade.titulo", responsavel)));
            } else if (!aceiteTermo && aceitePrivacidade) {
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_MOBILE);
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_SISTEMA);

                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.documento.aceito.de.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.politica.privacidade.titulo", responsavel)));
            } else {
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO);
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO_MOBILE);
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO_SALARYPAY);

                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.documento.aceito.de.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.termo.de.uso", responsavel)));
            }
        } else if (!aceiteMobile && aceiteWeb) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.aceitacao.via.de.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.solicitacao.via.web", responsavel)));
            if (aceiteTermo && aceitePrivacidade) {
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO);
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_SISTEMA);

                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.documento.aceito.de.arg0.a.arg1", responsavel, ApplicationResourcesHelper.getMessage("rotulo.termo.de.uso", responsavel), ApplicationResourcesHelper.getMessage("rotulo.politica.privacidade.titulo", responsavel)));
            } else if (!aceiteTermo && aceitePrivacidade) {
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_SISTEMA);

                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.documento.aceito.de.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.politica.privacidade.titulo", responsavel)));
            } else {
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO);

                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.documento.aceito.de.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.termo.de.uso", responsavel)));
            }
        } else {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.aceitacao.via.de.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.solicitacao.via.mobile", responsavel)));
            if (aceiteTermo && aceitePrivacidade) {
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_MOBILE);
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO_MOBILE);
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO_SALARYPAY);

                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.documento.aceito.de.arg0.a.arg1", responsavel, ApplicationResourcesHelper.getMessage("rotulo.termo.de.uso", responsavel), ApplicationResourcesHelper.getMessage("rotulo.politica.privacidade.titulo", responsavel)));
            } else if (!aceiteTermo && aceitePrivacidade) {
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_MOBILE);

                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.documento.aceito.de.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.politica.privacidade.titulo", responsavel)));
            } else {
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO_MOBILE);
                    tocCodigo.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO_SALARYPAY);

                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.documento.aceito.de.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.termo.de.uso", responsavel)));
            }
        }

        if (cse || org || csa || cor || ser || sup) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.papel.usuario", responsavel));
            if (cse) {
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignante.abreviado", responsavel) + " ");
            }
            if (org) {
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.abreviado", responsavel) + " ");
            }
            if (csa) {
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.sigla", responsavel) + " ");
            }
            if (cor) {
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.abreviado", responsavel) + " ");
            }
            if (ser) {
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.abreviado", responsavel) + " ");
            }
            if (sup) {
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.suporte.abreviado", responsavel) + " ");
            }
        }

        criterio.setAttribute("aceiteWeb", aceiteWeb);
        criterio.setAttribute("aceiteMobile", aceiteMobile);
        criterio.setAttribute("aceiteTermo", aceiteTermo);
        criterio.setAttribute("aceitePrivacidade", aceitePrivacidade);
        criterio.setAttribute("aceiteTermoAdesaoAutorizado", aceiteTermoAdesaoAutorizado);
        criterio.setAttribute("aceiteTermoAdesaoNaoAutorizado", aceiteTermoAdesaoNaoAutorizado);
        criterio.setAttribute("tocCodigo", tocCodigo);
        criterio.setAttribute("cse", cse);
        criterio.setAttribute("org", org);
        criterio.setAttribute("csa", csa);
        criterio.setAttribute("cor", cor);
        criterio.setAttribute("ser", ser);
        criterio.setAttribute("sup", sup);


        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nomeArquivo.toString());
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put("RESPONSAVEL", responsavel);
        parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);
        parameters.put("EXIBE_TERMO_ADESAO_NAO_AUTORIZADO", aceiteTermoAdesaoNaoAutorizado);
        
        geraLstTermoUsoPrivacidadeAdesaoAutorizado(criterio, parameters);
        
        if(aceiteTermoAdesaoNaoAutorizado) {
            // gera dados do termo de adesão não autorizado
            geraLstTermoAdesaoNaoAutorizado(criterio, parameters);
        }
        
        try {
            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

            // Gera Zip
            geraZip(nomeArquivo.toString(), reportName);

        } catch (ReportControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }
    
    private void geraLstTermoUsoPrivacidadeAdesaoAutorizado(CustomTransferObject criterio, HashMap<String, Object> parameters) {
        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);

            List<TermoUsoPrivacidadeAdesaoBean> lstTermoUsoPrivacidadeAdesaoAutorizado = relatorioController.lstTermoUsoPrivacidadeAdesaoAutorizado(criterio, responsavel);

            parameters.put("LISTA_TERMO_USO_PRIVACIDADE_ADESAO_AUTORIZADO", lstTermoUsoPrivacidadeAdesaoAutorizado);

        } catch (RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }
    }
    
    private void geraLstTermoAdesaoNaoAutorizado(CustomTransferObject criterio, HashMap<String, Object> parameters) {
        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);

            List<TermoUsoPrivacidadeAdesaoBean> lstTermoAdesaoNaoAutorizado = relatorioController.lstTermoAdesaoNaoAutorizado(criterio, responsavel);

            parameters.put("LISTA_TERMO_ADESAO_NAO_AUTORIZADO", lstTermoAdesaoNaoAutorizado);

        } catch (RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }
    }
}
