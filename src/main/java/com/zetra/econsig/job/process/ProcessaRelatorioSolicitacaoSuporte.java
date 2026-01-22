package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.google.gson.internal.LinkedTreeMap;
import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.solicitacaosuporte.jira.SolicitacaoSuporteAPIJira;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.service.solicitacaosuporte.SolicitacaoSuporteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.jira.bean.Issue;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ProcessaRelatorioSolicitacaoSuporte extends ProcessaRelatorio{

	/** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioSolicitacaoSuporte.class);

    public ProcessaRelatorioSolicitacaoSuporte(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
		try {
			String cseIdInterno = new ConsignanteDelegate().findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel).getIdentificadorInterno();
			UsuarioDelegate usuDelegate = new UsuarioDelegate();
			List<TransferObject> cseList = usuDelegate.findUsuarioCseList(responsavel);
			Iterator<TransferObject> iterator = cseList.iterator();
			String clientes = "";

			for (int i = 0; iterator.hasNext(); i++) {
				TransferObject transferObject = iterator.next();
				String login = (String) transferObject.getAttribute(Columns.USU_LOGIN);

				if (i == 0) {
					clientes = "(";
				} else {
					clientes += " or ";
				}

				clientes += "\"Login eConsig\" ~ \"" + login + "\"";
			}

			clientes += ")";
			criterio.setAttribute("CLIENTES", clientes);
			criterio.setAttribute("CSE_ID_INTERNO", cseIdInterno);
		} catch (Exception ex) {
			codigoRetorno = ERRO;
			mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel)
					+ "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
			LOG.error(mensagem, ex);
		}

    	String strIniPeriodo = getParametro("periodoIni", parameterMap);
        String strFimPeriodo = getParametro("periodoFim", parameterMap);
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";

        if (!TextHelper.isNull(strIniPeriodo) && !TextHelper.isNull(strFimPeriodo)) {
            paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy/MM/dd");
            paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy/MM/dd");
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        StringBuilder nomeArquivo = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.solicitacao.suporte", responsavel), responsavel, parameterMap, null));
        String titulo = relatorio.getTitulo();
        StringBuilder subTitulo = new StringBuilder();
        String grupoJira = getParametro("grupoJira", parameterMap);


        if (!TextHelper.isNull(grupoJira)) {
            switch (grupoJira) {
                case ("cse"): grupoJira = ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel);break;
                case ("csa"): grupoJira = ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel);break;
                case ("org"): grupoJira = ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel);break;
                case ("ser"): grupoJira = ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel);break;
            }
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.nova.solicitacao.grupo.arg0", responsavel, grupoJira.toUpperCase()));
        } else {
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.nova.solicitacao.grupo.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
        }

        criterio.setAttribute("DATA_INI", paramIniPeriodo);
        criterio.setAttribute("DATA_FIM", paramFimPeriodo);
        criterio.setAttribute("grupoJira", grupoJira);
        String strFormato = getStrFormato();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
        parameters.put(ReportManager.REPORT_FILE_NAME, nomeArquivo.toString());
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put("RESPONSAVEL", responsavel);

        try {
            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String reportName = reportController.makeReport(getStrFormato(), parameters, relatorio, getDataSource(criterio), responsavel);

            String reportNameZip = geraZip(nomeArquivo.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

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

    private JRDataSource getDataSource(TransferObject criterio) throws ZetraException {
        SolicitacaoSuporteAPIJira jira = new SolicitacaoSuporteAPIJira();
        List<Issue> issues = null;
        try {
            String grupo = (String) criterio.getAttribute("grupoJira");
            String dataIni = criterio.getAttribute("DATA_INI").toString();
            String dataFim = criterio.getAttribute("DATA_FIM").toString();
            String cseIdInterno = criterio.getAttribute("CSE_ID_INTERNO").toString();
            issues = jira.getIssueList(grupo, dataIni, dataFim, cseIdInterno);
        } catch (Exception ex) {
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }

        List<Object> retorno = new ArrayList<>();

        SolicitacaoSuporteController solicitacaoSuporteController = ApplicationContextProvider.getApplicationContext().getBean(SolicitacaoSuporteController.class);
        List<TransferObject> lstSolicitacaoSuporte = solicitacaoSuporteController.lstTodasSolicitacaoSuporte(responsavel);

        if (issues != null && !issues.isEmpty()) {
            for (Issue issue : issues) {
                issue.getFields().setKey(issue.getKey());
                if (lstSolicitacaoSuporte !=null && !lstSolicitacaoSuporte.isEmpty()) {
                    for (TransferObject solicitacaoSuporte : lstSolicitacaoSuporte) {
                        String chave = (String) solicitacaoSuporte.getAttribute(Columns.SOS_CHAVE);
                        String usuNome = (String) solicitacaoSuporte.getAttribute(Columns.USU_NOME);
                        if (chave.equals(issue.getKey())){
                            issue.getFields().setLoginEConsig(usuNome);
                        }
                    }
                }
                String categoriaSuport = (String) issue.getFields().getCategoriaSuporteId().get("value");
                LinkedTreeMap<String,String> childCategoriaSuporte = (LinkedTreeMap<String,String>) issue.getFields().getCategoriaSuporteId().get("child");
                String complementoCategoria = childCategoriaSuporte.get("value");

                LinkedHashMap<String,String> mapa = new LinkedHashMap<>();
                mapa.put("value", categoriaSuport+" - "+complementoCategoria);

                issue.getFields().setCategoriaSuporteId(mapa);
                retorno.add(issue.getFields());
            }
        }

        return new JRBeanCollectionDataSource(retorno);
    }
}