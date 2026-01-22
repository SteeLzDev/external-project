package com.zetra.econsig.job.process;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dto.InadimplenciaBean;
import com.zetra.econsig.report.reports.HeadingsScriptlet;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioInadimplencia</p>
 * <p> Description: Processa o Relatório de Inadimplência</p>
 * <p> Copyright: Copyright (c) 2014 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioInadimplencia extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioInadimplencia.class);

    public ProcessaRelatorioInadimplencia(Relatorio relatorio, Map<String, String[]> parameterMap, Boolean agendado, AcessoSistema responsavel) throws AgendamentoControllerException {
        super(relatorio, parameterMap, null, agendado, responsavel);
    }

    @Override
    protected void executar() {
        StringBuilder titulo = new StringBuilder(ApplicationResourcesHelper.getMessage("rotulo.relatorio.inadimplencia.titulo", responsavel));
        String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;
        StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.inadimplencia", responsavel), responsavel, parameterMap, null));
        StringBuilder subTitulo = new StringBuilder("");
        StringBuilder subTituloPeriodo = new StringBuilder("");

        List<String> listArquiv = new ArrayList<>();
        String path = getPath(responsavel);

        if (path == null) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));

        } else{
            try {
                ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
                RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
                ConsignatariaDelegate consignatariaDelegate = new ConsignatariaDelegate();

                String entidade = getEntidade(responsavel);

                path += File.separatorChar + "relatorio" + File.separatorChar
                        + entidade + File.separatorChar + relatorio.getTipo();

                // Cria a pasta de relatório caso não exista.
                new File(path).mkdirs();

                Map<String, String> periodoMap = getFiltroPeriodo(parameterMap, subTituloPeriodo, nome, session, responsavel);
                String periodo = periodoMap.get("PERIODO");

                String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);

                List<String> codigos = new ArrayList<>();

                if(TextHelper.isNull(csaCodigo)){
                    List<Consignataria> csas = consignatariaDelegate.lstConsignatariaProjetoInadimplencia();

                    if(csas != null && !csas.isEmpty()){
                        Iterator<Consignataria> iterator = csas.iterator();
                        while(iterator.hasNext()){
                            codigos.add(iterator.next().getCsaCodigo());
                        }
                    } else{
                        throw new ReportControllerException("mensagem.erro.nenhuma.consignataria.esta.projeto.inadimplencia", responsavel);
                    }

                } else{
                    codigos.add(csaCodigo);
                }

                String fileName = path + File.separatorChar + nome.toString();

                List<String> spdCodigoRejeitada = new ArrayList<>();
                spdCodigoRejeitada.add(CodedValues.SPD_REJEITADAFOLHA);

                List<String> sadCodigos = new ArrayList<>();
                sadCodigos.add(CodedValues.SAD_SUSPENSA);
                sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);

                List<String> spdCodigoPaga = new ArrayList<>();
                spdCodigoPaga.add(CodedValues.SPD_LIQUIDADAFOLHA);
                spdCodigoPaga.add(CodedValues.SPD_LIQUIDADAMANUAL);

                if(codigos != null && !codigos.isEmpty()){
                    Iterator<String> iterator = codigos.iterator();
                    String nomeRelConsig = "";

                    while(iterator.hasNext()){
                        String csa = iterator.next();

                        ConsignatariaTransferObject consignataria = consignatariaDelegate.findConsignataria(csa, responsavel);

                        subTitulo = new StringBuilder("");
                        subTitulo.append(subTituloPeriodo);
                        subTitulo.append(System.getProperty("line.separator"));
                        subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, consignataria.getCsaNome()));

                        String nomeRelatorio =  consignataria.getCsaIdentificador() + "_" + nome.toString();
                        nomeRelConsig = path + File.separatorChar + nomeRelatorio + ".pdf";

                        HashMap<String, Object> parameters = new HashMap<>();

                        geraDadosVisaoGeral(parameters, relatorioController, csa, periodo, spdCodigoRejeitada);

                        geraDadosVisaoInadimplencia(parameters, relatorioController, csa, periodo, spdCodigoRejeitada, sadCodigos, spdCodigoPaga);

                        geraDadosSituacaoServidor(parameters, relatorioController, csa, periodo, spdCodigoRejeitada);

                        geraDadosOrgao(parameters, relatorioController, csa, periodo, spdCodigoRejeitada);

                        geraDadosAcoesFolha(parameters, relatorioController, csa, periodo);

                        geraDadosFalecidos(parameters, relatorioController, csa, periodo, spdCodigoRejeitada);

                        String strFormato = getStrFormato();

                        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
                        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
                        parameters.put(ReportManager.REPORT_FILE_NAME, ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.inadimplencia", responsavel));
                        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
                        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO_ENTIDADE, getCaminhoLogoEntidade(responsavel));
                        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO_ECONSIG, getCaminhoLogoEConsig(responsavel));
                        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(true, null, responsavel));
                        parameters.put(ReportManager.REPORT_FILE_NAME, nomeRelatorio);
                        parameters.put(ReportManager.PARAM_CSE_NOME, getCseNome(responsavel));
                        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
                        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
                        parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);
                        parameters.put(ReportManager.REPORT_SCRIPTLET, new HeadingsScriptlet());
                        parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);
                        parameters.put("nomeCsa", consignataria.getCsaNome());

                        reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

                        listArquiv.add(nomeRelConsig);
                    }

                    String fileZip = fileName + ".zip";
                    FileHelper.zip(listArquiv, fileZip);

                    for(String arqDelete: listArquiv){
                        FileHelper.delete(arqDelete);
                    }

                    // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
                    enviaEmail(fileZip);

                    setMensagem(fileZip, relatorio.getTipo(), relatorio.getTitulo(), session);
                }
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
    }

    private void geraDadosVisaoGeral(HashMap<String, Object> parameters, RelatorioController relatorioController, String csaCodigo, String periodo, List<String> spdCodigoRejeitada) throws RelatorioControllerException {
        List<InadimplenciaBean> carteira = new ArrayList<>();

        InadimplenciaBean inadimplenciaBean = relatorioController.buscaQuantidadeCarteiraInadimplencia(periodo, null, null, CodedValues.NSE_EMPRESTIMO, null, responsavel);
        inadimplenciaBean.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.carteira.singular", responsavel));
        carteira.add(inadimplenciaBean);

        inadimplenciaBean = relatorioController.buscaQuantidadeCarteiraInadimplencia(periodo, csaCodigo, null, CodedValues.NSE_EMPRESTIMO, null, responsavel);
        inadimplenciaBean.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.carteira.singular", responsavel) + " " + inadimplenciaBean.getDescricao());
        carteira.add(inadimplenciaBean);

        inadimplenciaBean = relatorioController.buscaQuantidadeCarteiraInadimplencia(periodo, null, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, null, responsavel);
        inadimplenciaBean.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.inadimplente.plural", responsavel));
        carteira.add(inadimplenciaBean);

        inadimplenciaBean = relatorioController.buscaQuantidadeCarteiraInadimplencia(periodo, csaCodigo, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, null, responsavel);
        inadimplenciaBean.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.inadimplente.plural", responsavel) + " " + inadimplenciaBean.getDescricao());
        carteira.add(inadimplenciaBean);

        parameters.put("carteira", carteira);

        List<InadimplenciaBean> carteiraValor = new ArrayList<>();

        inadimplenciaBean = relatorioController.buscaValorCarteiraInadimplencia(periodo, null, null, CodedValues.NSE_EMPRESTIMO, null, responsavel);
        inadimplenciaBean.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.carteira.singular", responsavel));
        carteiraValor.add(inadimplenciaBean);

        inadimplenciaBean = relatorioController.buscaValorCarteiraInadimplencia(periodo, csaCodigo, null, CodedValues.NSE_EMPRESTIMO, null, responsavel);
        inadimplenciaBean.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.carteira.singular", responsavel) + " " + inadimplenciaBean.getDescricao());
        carteiraValor.add(inadimplenciaBean);

        inadimplenciaBean = relatorioController.buscaValorCarteiraInadimplencia(periodo, null, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, null, responsavel);
        inadimplenciaBean.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.inadimplente.plural", responsavel));
        carteiraValor.add(inadimplenciaBean);

        inadimplenciaBean = relatorioController.buscaValorCarteiraInadimplencia(periodo, csaCodigo, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, null, responsavel);
        inadimplenciaBean.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.inadimplente.plural", responsavel) + " " + inadimplenciaBean.getDescricao());
        carteiraValor.add(inadimplenciaBean);

        parameters.put("carteiraValor", carteiraValor);
    }

    private void geraDadosVisaoInadimplencia(HashMap<String, Object> parameters, RelatorioController relatorioController, String csaCodigo, String periodo, List<String> spdCodigoRejeitada, List<String> sadCodigos, List<String> spdCodigoPaga) throws RelatorioControllerException {
        List<InadimplenciaBean> visaoInadimplencia = new ArrayList<>();
        InadimplenciaBean inadimplenciaVisaoBean = relatorioController.buscaQuantidadeCarteiraInadimplencia(periodo, csaCodigo, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, null, responsavel);
        inadimplenciaVisaoBean.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.inadimplente.plural", responsavel) + " " + inadimplenciaVisaoBean.getDescricao());
        visaoInadimplencia.add(inadimplenciaVisaoBean);
        InadimplenciaBean suspensosCSA = relatorioController.buscaQuantidadeCarteiraInadimplencia(periodo, csaCodigo, null, CodedValues.NSE_EMPRESTIMO, sadCodigos, responsavel);
        suspensosCSA.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.suspenso.plural", responsavel) + " " + suspensosCSA.getDescricao());
        visaoInadimplencia.add(suspensosCSA);
        InadimplenciaBean adimplenciaVisaoBean = relatorioController.buscaQuantidadeCarteiraInadimplencia(periodo, csaCodigo, spdCodigoPaga, CodedValues.NSE_EMPRESTIMO, null, responsavel);
        adimplenciaVisaoBean.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.adimplente.plural", responsavel) + " " + adimplenciaVisaoBean.getDescricao());
        visaoInadimplencia.add(adimplenciaVisaoBean);

        parameters.put("visaoInadimplencia", visaoInadimplencia);

        List<InadimplenciaBean> visaoValorInadimplencia = new ArrayList<>();

        InadimplenciaBean inadimplenciaValorVisaoCSA = relatorioController.buscaValorCarteiraInadimplencia(periodo, csaCodigo, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, null, responsavel);
        inadimplenciaValorVisaoCSA.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.inadimplente.plural", responsavel) + " " + inadimplenciaValorVisaoCSA.getDescricao());
        visaoValorInadimplencia.add(inadimplenciaValorVisaoCSA);
        InadimplenciaBean suspensosValorCSA = relatorioController.buscaValorCarteiraInadimplencia(periodo, csaCodigo, null, CodedValues.NSE_EMPRESTIMO, sadCodigos, responsavel);
        suspensosValorCSA.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.suspenso.plural", responsavel) + " " + suspensosValorCSA.getDescricao());
        visaoValorInadimplencia.add(suspensosValorCSA);
        InadimplenciaBean adimplenciaValorVisaoBean = relatorioController.buscaValorCarteiraInadimplencia(periodo, csaCodigo, spdCodigoPaga, CodedValues.NSE_EMPRESTIMO, null, responsavel);
        adimplenciaValorVisaoBean.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.adimplente.plural", responsavel) + " " + adimplenciaValorVisaoBean.getDescricao());
        visaoValorInadimplencia.add(adimplenciaValorVisaoBean);
        parameters.put("visaoValorInadimplencia", visaoValorInadimplencia);
    }

    private void geraDadosSituacaoServidor(HashMap<String, Object> parameters, RelatorioController relatorioController, String csaCodigo, String periodo, List<String> spdCodigoRejeitada) throws RelatorioControllerException {
        parameters.put("situacaoServidor", relatorioController.buscaQuantidadeSituacaoServidorInadimplencia(periodo, csaCodigo, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, responsavel));
        parameters.put("situacaoServidorValor", relatorioController.buscaValorSituacaoServidorInadimplencia(periodo, csaCodigo, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, responsavel));
    }

    private void geraDadosOrgao(HashMap<String, Object> parameters, RelatorioController relatorioController, String csaCodigo, String periodo, List<String> spdCodigoRejeitada) throws RelatorioControllerException {

        List<String> orgaos = relatorioController.buscaTopOrgaosInadimplencia(periodo, csaCodigo, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, responsavel);
        List<InadimplenciaBean> inadimplenteOrgao = new ArrayList<>();
        List<InadimplenciaBean> inadimplenteOrgaoValor = new ArrayList<>();

        if(orgaos != null && !orgaos.isEmpty()){
            Iterator<String> iterator = orgaos.iterator();
            while(iterator.hasNext()){
                String orgao = iterator.next();
                inadimplenteOrgao.add(relatorioController.buscaQuantidadeOrgaoInadimplencia(periodo, csaCodigo, orgao, null, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, responsavel));
            }

            if(orgaos.size() == 9){
                //demais órgãos
                InadimplenciaBean demaisOrgaos = relatorioController.buscaQuantidadeOrgaoInadimplencia(periodo, csaCodigo, null, orgaos, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, responsavel);
                demaisOrgaos.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.demais.arg0.orgaos", responsavel, demaisOrgaos.getDescricao()));
                inadimplenteOrgao.add(demaisOrgaos);
            }
        }

        orgaos = relatorioController.buscaTopValorOrgaosInadimplencia(periodo, csaCodigo, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, responsavel);

        if(orgaos != null && !orgaos.isEmpty()){
            Iterator<String> iterator = orgaos.iterator();
            while(iterator.hasNext()){
                String orgao = iterator.next();
                inadimplenteOrgaoValor.add(relatorioController.buscaValorOrgaoInadimplencia(periodo, csaCodigo, orgao, null, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, responsavel));
            }

            if(orgaos.size() == 9){
                //demais órgãos
                InadimplenciaBean demaisOrgaosValor = relatorioController.buscaValorOrgaoInadimplencia(periodo, csaCodigo, null, orgaos, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, responsavel);
                demaisOrgaosValor.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.demais.arg0.orgaos", responsavel, demaisOrgaosValor.getDescricao()));
                inadimplenteOrgaoValor.add(demaisOrgaosValor);
            }
        }

        parameters.put("inadimplenteOrgao", inadimplenteOrgao);
        parameters.put("inadimplenteOrgaoValor", inadimplenteOrgaoValor);
    }

    private void geraDadosAcoesFolha(HashMap<String, Object> parameters, RelatorioController relatorioController, String csaCodigo, String periodo) throws RelatorioControllerException{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        Date doisMesesAntes = null;
        Date umMesAntes = null;

        try {
            date = df.parse(periodo);

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, -2);

            doisMesesAntes = calendar.getTime();

            calendar.add(Calendar.MONTH, +1);

            umMesAntes = calendar.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<InadimplenciaBean> transferidos = new ArrayList<>();
        transferidos.add(relatorioController.buscaTransferidosInadimplencia(df.format(doisMesesAntes), csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));
        transferidos.add(relatorioController.buscaTransferidosInadimplencia(df.format(umMesAntes), csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));
        transferidos.add(relatorioController.buscaTransferidosInadimplencia(periodo, csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));

        parameters.put("transferidos", transferidos);

        List<InadimplenciaBean> transferidosValor = new ArrayList<>();
        transferidosValor.add(relatorioController.buscaValorTransferidosInadimplencia(df.format(doisMesesAntes), csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));
        transferidosValor.add(relatorioController.buscaValorTransferidosInadimplencia(df.format(umMesAntes), csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));
        transferidosValor.add(relatorioController.buscaValorTransferidosInadimplencia(periodo, csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));

        parameters.put("transferidosValor", transferidosValor);

        List<InadimplenciaBean> alongados = new ArrayList<>();
        alongados.add(relatorioController.buscaAlongadosInadimplencia(df.format(doisMesesAntes), csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));
        alongados.add(relatorioController.buscaAlongadosInadimplencia(df.format(umMesAntes), csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));
        alongados.add(relatorioController.buscaAlongadosInadimplencia(periodo, csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));

        parameters.put("alongados", alongados);

        List<InadimplenciaBean> alongadosValor = new ArrayList<>();
        alongadosValor.add(relatorioController.buscaValorAlongadosInadimplencia(df.format(doisMesesAntes), csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));
        alongadosValor.add(relatorioController.buscaValorAlongadosInadimplencia(df.format(umMesAntes), csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));
        alongadosValor.add(relatorioController.buscaValorAlongadosInadimplencia(periodo, csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));


        parameters.put("alongadosValor", alongadosValor);
    }

    private void geraDadosFalecidos(HashMap<String, Object> parameters, RelatorioController relatorioController, String csaCodigo, String periodo, List<String> spdCodigoRejeitada) throws RelatorioControllerException{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        Date doisMesesAntes = null;
        Date umMesAntes = null;

        try {
            date = df.parse(periodo);

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, -2);

            doisMesesAntes = calendar.getTime();

            calendar.add(Calendar.MONTH, +1);

            umMesAntes = calendar.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<InadimplenciaBean> falecidos = new ArrayList<>();
        falecidos.add(relatorioController.buscaQuantidadeCarteiraInadimplencia(periodo, csaCodigo, spdCodigoRejeitada, CodedValues.NSE_EMPRESTIMO, null, responsavel));
        falecidos.add(relatorioController.buscaFalecidosInadimplencia(df.format(doisMesesAntes), csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));
        falecidos.add(relatorioController.buscaFalecidosInadimplencia(df.format(umMesAntes), csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));
        falecidos.add(relatorioController.buscaFalecidosInadimplencia(periodo, csaCodigo, CodedValues.NSE_EMPRESTIMO, responsavel));

        parameters.put("falecidos", falecidos);

    }
}