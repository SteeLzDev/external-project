package com.zetra.econsig.job.process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONValue;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dto.SinteticoDataSourceBean;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ProcessaRelatorio</p>
 * <p>Description: Classe para processamento de relatorios
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class ProcessaRelatorio extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorio.class);

    public static final String LOG_OBSERVACAO = "LOG_OBSERVACAO";

    public static final int TAMANHO_RODAPE_MAXIMO_PORTRAIT = 140;

    public static final int TAMANHO_RODAPE_MAXIMO_LANDSCAPE = 179;

    public static ProcessaRelatorio newInstance(String classe, Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, AcessoSistema responsavel) {
        ProcessaRelatorio pr = null;
        try {
            final Object[] argumentos = { relatorio, parameterMap, session, false, responsavel };
            pr = (ProcessaRelatorio) Class.forName(classe).getConstructor(Relatorio.class, Map.class, HttpSession.class, Boolean.class, AcessoSistema.class).newInstance(argumentos);
        } catch (final Exception ex) {
            // Erro ao criar o objeto na classe solicitada.
            LOG.error(ex.getMessage(), ex);
        }
        return pr;
    }

    protected final Relatorio relatorio;

    protected final Map<String, String[]> parameterMap;

    protected final CustomTransferObject criterio;

    protected final HttpSession session;

    protected final boolean agendado;

    private String nomeArqRelatorio;

    private String formatoRelatorio;

    private boolean preVisualizacao;

    protected final AcessoSistema responsavel;

    public ProcessaRelatorio(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, boolean agendado, AcessoSistema responsavel) {
        this.relatorio = relatorio;
        this.parameterMap = parameterMap;
        this.session = session;
        this.agendado = agendado;
        this.responsavel = responsavel;
        criterio = new CustomTransferObject();

        // Salva em uma variável local o formato original do relatório, caso seja HTML para tratamento diferenciado
        if (this.parameterMap != null) {
            formatoRelatorio = this.parameterMap.containsKey("formato") ? this.parameterMap.get("formato")[0] : "HTML";
            // Se for formato HTML, gera o relatório formato TEXT para recuperar as informações e exibir na página
            if ("HTML".equals(formatoRelatorio)) {
                preVisualizacao = true;
                formatoRelatorio = "TEXT";
                this.parameterMap.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, new String[] { formatoRelatorio });
            }
        }

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        if (relatorio != null) {
            setDescricao(relatorio.getTitulo());
        }

        // Gera observação do log
        if ((parameterMap != null) && !TextHelper.isNull(parameterMap.get(LOG_OBSERVACAO))) {
            criterio.setAttribute(LOG_OBSERVACAO, getParametro(LOG_OBSERVACAO, parameterMap));
        }
    }

    public String getNomeArqRelatorio() {
        return nomeArqRelatorio;
    }

    public void setNomeArqRelatorio(String nomeArqRelatorio) {
        this.nomeArqRelatorio = nomeArqRelatorio;
    }

    public boolean isPreVisualizacao() {
        return preVisualizacao;
    }

    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    protected List<SinteticoDataSourceBean> agrupador(List<SinteticoDataSourceBean> lista, List<SinteticoDataSourceBean> selecionados) {
        if (selecionados == null) {
            Collections.sort(lista, (a, b) -> ((int) (b.getValor() - a.getValor())));

            if (lista.size() >= 6) {
                final String nome = "Outros";
                Long valor = (long) 0;

                for (int i = 5; i < lista.size(); i++) {
                    valor += lista.get(i).getValor();
                }

                final SinteticoDataSourceBean outros = new SinteticoDataSourceBean(nome, valor);
                final List<SinteticoDataSourceBean> subList = new ArrayList<>(lista.subList(0, 5));
                subList.add(outros);

                return subList;
            } else {
                return lista;
            }
        } else {
            final List<SinteticoDataSourceBean> subList = new ArrayList<>();
            int contador = 0;

            for (final SinteticoDataSourceBean bean : lista) {
                final String nome = bean.getNome();
                final String periodo = bean.getPeriodo();
                final Long valor = bean.getValor();

                if (contemNome(selecionados, nome)) {
                    if (contemNome(subList, nome)) {
                        if (contemPeriodo(subList, nome, periodo)) {
                            final SinteticoDataSourceBean subBean = subList.get(getIndex(subList, nome, periodo));
                            final Long subValor = subBean.getValor();
                            subList.get(getIndex(subList, nome, periodo)).setValor(valor + subValor);
                        } else {
                            final SinteticoDataSourceBean subBean = new SinteticoDataSourceBean(nome, valor, periodo);
                            subList.add(subBean);
                        }
                    } else if (contador < 5) {
                        final SinteticoDataSourceBean subBean = new SinteticoDataSourceBean(nome, valor, periodo);
                        subList.add(subBean);
                        contador++;
                    }
                } else if (contemPeriodo(subList, "Outros", periodo)) {
                    final SinteticoDataSourceBean subBean = subList.get(getIndex(subList, "Outros", periodo));
                    final Long subValor = subBean.getValor();
                    subList.get(getIndex(subList, "Outros", periodo)).setValor(valor + subValor);
                } else {
                    final SinteticoDataSourceBean subBean = new SinteticoDataSourceBean("Outros", valor, periodo);
                    subList.add(subBean);
                }
            }

            Collections.sort(subList, (a, b) -> {
                final String[] periodoA = a.getPeriodo().split("/");
                final String[] periodoB = b.getPeriodo().split("/");
                if (Integer.parseInt(periodoA[1]) != Integer.parseInt(periodoB[1])) {
                    return Integer.parseInt(periodoA[1]) - Integer.parseInt(periodoB[1]);
                } else {
                    return Integer.parseInt(periodoA[0]) - Integer.parseInt(periodoB[0]);
                }
            });

            return subList;
        }
    }

    protected boolean containKey(Map<SinteticoDataSourceBean, Long> map, SinteticoDataSourceBean bean) {
        for (final SinteticoDataSourceBean key : map.keySet()) {
            if (key.equals(bean)) {
                return true;
            }
        }
        return false;
    }

    protected boolean contemNome(List<SinteticoDataSourceBean> lista, String nome) {
        for (final SinteticoDataSourceBean bean : lista) {
            if (bean.getNome().equalsIgnoreCase(nome)) {
                return true;
            }
        }
        return false;
    }

    protected boolean contemPeriodo(List<SinteticoDataSourceBean> lista, String nome, String periodo) {
        for (final SinteticoDataSourceBean bean : lista) {
            if (bean.getNome().equalsIgnoreCase(nome) && bean.getPeriodo().equalsIgnoreCase(periodo)) {
                return true;
            }
        }
        return false;
    }

    protected List<Object[]> DTOToList(List<TransferObject> DTO, String[] fields) {
        final List<Object[]> conteudo = new ArrayList<>();

        for (final TransferObject to : DTO) {
            final List<Object> listaTemp = new ArrayList<>();
            for (final String field : fields) {
                if (Columns.ADE_NUMERO.equalsIgnoreCase(field) || Columns.ADE_PRAZO.equalsIgnoreCase(field)) {
                    if (to.getAttribute(field) != null) {
                        if (to.getAttribute(field) instanceof Double) {
                            listaTemp.add(Math.round(Double.parseDouble(to.getAttribute(field).toString())));
                        } else {
                            listaTemp.add(to.getAttribute(field));
                        }
                    } else {
                        listaTemp.add(ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel));
                    }
                } else if (to.getAttribute(field) != null) {
                    listaTemp.add(to.getAttribute(field));
                } else if ("tb_aut_desconto.ade_ano_mes_fim".equalsIgnoreCase(field)) {
                    listaTemp.add(ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel));
                } else {
                    listaTemp.add("-");
                }
            }
            conteudo.add(listaTemp.toArray());
        }

        return conteudo;
    }

    protected void enviaEmail(String reportName) {
        try {
            if (!agendado) {
                return;
            }

            final String tituloRelatorio = relatorio.getTitulo();
            final String[] emailsDestinatario = getFiltroEmailDestinatario();
            if ((emailsDestinatario != null) && (emailsDestinatario.length > 0)) {
                EnviaEmailHelper.enviarEmailRelatorio(reportName, tituloRelatorio, emailsDestinatario, responsavel);

                final String fileName = reportName.substring(reportName.lastIndexOf(File.separator) + 1);

                final LogDelegate log = new LogDelegate(responsavel, Log.RELATORIO, Log.SEND_FILE, Log.LOG_INFORMACAO);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.envio.relatorio.email", responsavel, fileName, tituloRelatorio, TextHelper.join(emailsDestinatario, ",")));
                log.write();
            }
        } catch (final ViewHelperException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public String formatarPeriodo(String periodo) {
        try {
            return DateHelper.format(DateHelper.parsePeriodString(periodo), "yyyy-MM-dd");
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            return periodo;
        }
    }

    protected String geraZip(String nomeRelatorio, String report) throws IOException {
        String fileZip = null;
        if (!TextHelper.isNull(report)) {
            final String tipo = "transf_contratos".equals(relatorio.getTipo()) ? "consignacoes" : relatorio.getTipo();

            fileZip = report.substring(0, report.lastIndexOf(File.separator)) + File.separatorChar + nomeRelatorio + ".zip";

            FileHelper.zip(report, fileZip);
            FileHelper.delete(report);

            setMensagem(fileZip, tipo, relatorio.getTitulo(), session);
        }
        return fileZip;
    }

    public String getCaminhoLogoCse(AcessoSistema responsavel) {
        final File dir = new File(getPath(responsavel) + "/conf/relatorio/imagem/");
        if (!dir.exists() && !dir.mkdirs()) {
            LOG.debug("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
            return null;
        }
        final String path = dir.getAbsolutePath() + "/logo_topo.png";
        if (!new File(path).exists()) {
            final InputStream input = getClass().getResourceAsStream("/com/zetra/econsig/report/jasper/imagem/logo_topo.png");
            try {
                FileHelper.saveStreamToFile(input, path);
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
                return null;
            }
        }
        return path;
    }

    public String getCaminhoLogoEConsig(AcessoSistema responsavel) {
        final File dir = new File(getPath(responsavel) + "/conf/relatorio/imagem/");
        if (!dir.exists() && !dir.mkdirs()) {
            LOG.debug("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
            return null;
        }
        final String path = dir.getAbsolutePath() + "/logo_capa.png";
        if (!new File(path).exists()) {
            final InputStream input = getClass().getResourceAsStream("/com/zetra/econsig/report/jasper/imagem/logo_capa.png");
            try {
                FileHelper.saveStreamToFile(input, path);
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
                return null;
            }
        }
        return path;
    }

    public String getCaminhoLogoEntidade(AcessoSistema responsavel) {
        final File file = new File(getPath(responsavel), "/imagem/login/logo_cse.gif");
        if (!file.exists() && !file.mkdirs()) {
            LOG.error("Arquivo '" + file.getAbsolutePath() + "' não existe.");
            return null;
        }

        return file.getAbsolutePath();
    }

    public String getCseNome(AcessoSistema responsavel) {

        String cseNome = LoginHelper.getCseNome(responsavel);

        if (cseNome == null) {
            try {
                final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                final ConsignanteTransferObject consignante = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                cseNome = consignante.getCseNome();
            } catch (final ConsignanteControllerException e) {
                LOG.info("Não foi possível encontrar o nome do consignante.");
            }
        }

        return cseNome;

    }

    public String getEntidade(AcessoSistema responsavel) {
        if (responsavel.isCsa()) {
            return "csa";
        } else if (responsavel.isCseSup() || responsavel.isSistema()) {
            return "cse";
        } else if (responsavel.isCor()) {
            return "cor";
        } else if (responsavel.isOrg()) {
            /*
             *  Serão criados sub-diretórios por org_codigo.
             *  Usuários de consginante poderão ver os relatórios gerados por usuários de órgão.
             */
            return "cse";
        } else if (responsavel.isSer()) {
            return "ser";
        } else {
            return "";
        }
    }

    public String getExtFormato() {
        String extensao = null;

        final String strFormato = getStrFormato();
        if (!TextHelper.isNull(strFormato)) {
            if ("PDF".equals(strFormato)) {
                extensao = ".pdf";
            } else if ("XLS".equals(strFormato)) {
                extensao = ".xls";
            } else if ("XLSX".equals(strFormato)) {
                extensao = ".xlsx";
            } else if ("TEXT".equals(strFormato)) {
                extensao = ".txt";
            } else if ("CSV".equals(strFormato)) {
                extensao = ".csv";
            } else if ("XML".equals(strFormato)) {
                extensao = ".xml";
            } else if ("DOC".equals(strFormato)) {
                extensao = ".doc";
            }
        }

        return extensao;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "chkCAMPOS" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de campos.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, atualmente não é alterado por este método
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroCampos(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> campos = null;
        final String[] campo = parameterMap.containsKey("chkCAMPOS") ? (String[]) parameterMap.get("chkCAMPOS") : null;
        if (campo != null) {
            campos = Arrays.asList(campo);
        }

        return campos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "plaCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" incluindo as informações
     * sobre o filtro de plano do permissionário.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do endereço do permissionário
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroCnvCodVerba(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String cnvCodVerba = null;
        if (parameterMap.containsKey("CNV_COD_VERBA")) {
            final String values[] = parameterMap.get("CNV_COD_VERBA");
            if ((values.length == 0) || "".equals(values[0])) {
                subTitulo.append(System.getProperty("line.separator")).append(com.zetra.econsig.helper.texto.ApplicationResourcesHelper.getMessage("rotulo.convenio.codigo.verba.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
            } else {
                cnvCodVerba = new String();
                subTitulo.append(System.getProperty("line.separator")).append(com.zetra.econsig.helper.texto.ApplicationResourcesHelper.getMessage("rotulo.convenio.codigo.verba.arg0", responsavel, ""));
                for (int i = 0; i < values.length; i++) {
                    final String[] aux = values[i].split(";");
                    cnvCodVerba = aux[0];
                    subTitulo.append(" ").append(aux[0]);
                    if ((i + 1) != values.length) {
                        subTitulo.append(", ");
                    }
                }
            }
        }
        return cnvCodVerba;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "corCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de correspondente.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de correspondente
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroCorCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String corCodigo = responsavel.getCorCodigo();

        if (responsavel.isCsa() || (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) || responsavel.isCseSupOrg()) {
            if (parameterMap.containsKey("corCodigo")) {
                String values[] = parameterMap.get("corCodigo");
                if ((values.length == 0) || "".equals(values[0])) {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                    corCodigo = null;
                } else {
                    values = values[0].split(";");
                    corCodigo = values[0];
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, values[2]));
                }
            }
        } else if (responsavel.isCor()) {
            corCodigo = responsavel.getCorCodigo();
            final String descricao = responsavel.getNomeEntidade();
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, descricao));
        }

        return corCodigo;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "CPF" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de CPF do servidor.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de CPF do servidor
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroCpf(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        final String cpf = parameterMap.containsKey("CPF") ? getParametro("CPF", parameterMap) : null;

        if (!TextHelper.isNull(cpf)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.cpf.arg0", responsavel, cpf));
        }

        return cpf;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "csaCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de consignatária.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de consignatária
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroCsaCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String csaCodigo = responsavel.getCsaCodigo();

        if (responsavel.isCseSupOrg()) {
            if (parameterMap.containsKey("csaCodigo")) {
                String values[] = parameterMap.get("csaCodigo");
                if ((values.length == 0) || "".equals(values[0])) {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
                } else {
                    values = values[0].split(";");
                    csaCodigo = values[0];
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, values[2]));
                }
            }
        } else if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCsaCodigo();
            final String descricao = responsavel.getNomeEntidade();
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, descricao));
        } else if (responsavel.isCor()) {
            csaCodigo = responsavel.getCsaCodigo();
            final String descricao = responsavel.getNomeEntidadePai();
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, descricao));
        }

        return csaCodigo;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "csaCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de consignatária.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de consignatária
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroCsaCodigoSaldoDevedorServidor(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String csaCodigo = responsavel.getCsaCodigo();

        if (responsavel.isCseSupOrg()) {
            if (parameterMap.containsKey("csaCodigoSaldoDevedor")) {
                String values[] = parameterMap.get("csaCodigoSaldoDevedor");
                if ((values.length == 0) || "".equals(values[0])) {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
                } else {
                    values = values[0].split(";");
                    csaCodigo = values[0];
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, values[2]));
                }
            }
        } else if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCsaCodigo();
            final String descricao = responsavel.getNomeEntidade();
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, descricao));
        } else if (responsavel.isCor()) {
            csaCodigo = responsavel.getCsaCodigo();
            final String descricao = responsavel.getNomeEntidadePai();
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, descricao));
        }

        return csaCodigo;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina os "csaCodigos" no caso de seleção múltipla de consignatárias a serem utilizados para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de consignatária.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de consignatária
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroCsaCodigos(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> csaCodigos = new ArrayList<>();

        if (responsavel.isCseSupOrg()) {
            if (parameterMap.containsKey("csaCodigo")) {
                final String svcs[] = parameterMap.get("csaCodigo");
                if (!"".equals(svcs[0])) {
                    final Set<String> csaCodigosSet = new HashSet<>();
                    final List<String> csaDescricoes = new ArrayList<>();
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ""));
                    for (final String svc : svcs) {
                        final String[] values = svc.split(";");
                        if ((values.length > 2) && !csaCodigosSet.contains(values[0])) {
                            csaCodigosSet.add(values[0]);
                            csaDescricoes.add(values[2]);
                        }
                    }
                    csaCodigos = new ArrayList<>(csaCodigosSet);
                    subTitulo.append(" ").append(TextHelper.join(csaDescricoes, ","));
                } else {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
                }
            }
        } else if (responsavel.isCsa()) {
            csaCodigos.add(responsavel.getCsaCodigo());
            final String descricao = responsavel.getNomeEntidade();
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, descricao));
        } else if (responsavel.isCor()) {
            csaCodigos.add(responsavel.getCsaCodigo());
            final String descricao = responsavel.getNomeEntidadePai();
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, descricao));
        }

        return csaCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "cseCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de consignante.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do consignante
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroCseCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String cseCodigo = responsavel.getCseCodigo();
        if (parameterMap.containsKey("cseCodigo")) {
            String values[] = parameterMap.get("cseCodigo");
            subTitulo.append(System.getProperty("line.separator"));
            if ((values.length == 0) || "".equals(values[0])) {
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            } else if ("NENHUM".equalsIgnoreCase(values[0])) {
                cseCodigo = values[0];
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.nenhum.simples", responsavel).toUpperCase()));
            } else {
                values = values[0].split(";");
                cseCodigo = values[0];
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular.arg0", responsavel, values[2]));

            }
        }

        return cseCodigo;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "ecoCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de empresa correspondente.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação da empresa correspondente
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroEcoCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String ecoCodigo = "";
        if (parameterMap.containsKey("ecoCodigo")) {
            String[] values = parameterMap.get("ecoCodigo");
            if ((values.length == 0) || "".equals(values[0])) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.empresacorrespondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            } else {
                values = TextHelper.split(values[0], ";");
                ecoCodigo = values[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.empresacorrespondente.singular.arg0", responsavel, values[2]));
            }
        }

        return ecoCodigo;
    }

    protected String[] getFiltroEmailDestinatario() {
        String[] emailsDestinatario = null;
        if (parameterMap.containsKey("email_destinatario")) {
            final String values[] = parameterMap.get("email_destinatario");
            if ((values != null) && (values.length > 0) && !TextHelper.isNull(values[0])) {
                emailsDestinatario = values[0].replace(" ", "").split(",|;");
            }
        }
        return emailsDestinatario;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "echCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" incluindo as informações
     * sobre o filtro de endereço do permissionário.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do endereço do permissionário
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroEnderecoCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String echCodigo = null;
        if (parameterMap.containsKey("ECH_CODIGO")) {
            final String values[] = parameterMap.get("ECH_CODIGO");
            if ((values.length == 0) || "".equals(values[0])) {
                subTitulo.append(System.getProperty("line.separator")).append(com.zetra.econsig.helper.texto.ApplicationResourcesHelper.getMessage("rotulo.endereco.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            } else {
                echCodigo = new String();
                subTitulo.append(System.getProperty("line.separator")).append(com.zetra.econsig.helper.texto.ApplicationResourcesHelper.getMessage("rotulo.endereco.singular.arg0", responsavel, ""));
                for (int i = 0; i < values.length; i++) {
                    final String[] aux = values[i].split(";");
                    echCodigo = aux[0];
                    subTitulo.append(" ").append(aux[1]);
                    if ((i + 1) != values.length) {
                        subTitulo.append(", ");
                    }
                }
            }
        }
        return echCodigo;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "estCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de estabelecimento.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do estabelecimento
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroEstCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String estCodigo = responsavel.getEstCodigo();

        if (responsavel.isOrg()) {
            estCodigo = responsavel.getEstCodigo();
            final String descricao = responsavel.getNomeEntidadePai();
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular.arg0", responsavel, descricao));
        } else if (parameterMap.containsKey("estCodigo")) {
            String values[] = parameterMap.get("estCodigo");
            if ((values.length == 0) || "".equals(values[0])) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            } else {
                values = values[0].split(";");
                estCodigo = values[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular.arg0", responsavel, values[2]));
            }
        }

        return estCodigo;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "funCodigo" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, atualmente não é alterado por este método
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroFunCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> funCodigos = new ArrayList<>();

        if (parameterMap.get("funCodigo") != null) {
            final String[] fun = parameterMap.get("funCodigo");
            if (!TextHelper.isNull(fun[0])) {
                funCodigos = Arrays.asList(fun);
            }
        }

        return funCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "tarCodigo" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, atualmente não é alterado por este método
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroTarCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> tarCodigos = new ArrayList<>();

        if (parameterMap.get("tarCodigo") != null) {
            final String[] tar = parameterMap.get("tarCodigo");
            if (!TextHelper.isNull(tar[0])) {
                tarCodigos = Arrays.asList(tar);
            }
        }

        return tarCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "OP_LOGIN" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de login responsável.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de login responsável
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroLoginResponsavel(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        final String usuLogin = parameterMap.containsKey("OP_LOGIN") ? getParametro("OP_LOGIN", parameterMap) : null;

        if (!TextHelper.isNull(usuLogin)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.login.responsavel.arg0", responsavel, usuLogin));
        }

        return usuLogin;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "marCodigo" a ser utilizado para a geração do relatório.
     *
     * @return
     */
    protected List<String> getFiltroMarCodigo() {
        List<String> marCodigos = null;
        if (parameterMap.containsKey("MAR_CODIGO")) {
            final String values[] = parameterMap.get("MAR_CODIGO");
            if ((values != null) && (values.length > 0) && !TextHelper.isNull(values[0])) {
                marCodigos = new ArrayList<>();
                Collections.addAll(marCodigos, values);
            }
        }

        final List<String> retorno = new ArrayList<>();
        if (TextHelper.isNull(marCodigos)) {
            return retorno;
        }
        // Remove se não incide na margem
        marCodigos.remove(CodedValues.INCIDE_MARGEM_NAO.toString());

        String incideMargem = CodedValues.INCIDE_MARGEM_SIM.toString();

        if (marCodigos.remove(incideMargem)) {
            retorno.add(incideMargem);
        }

        incideMargem = CodedValues.INCIDE_MARGEM_SIM_2.toString();
        if (marCodigos.remove(incideMargem)) {
            retorno.add(incideMargem);
        }

        incideMargem = CodedValues.INCIDE_MARGEM_SIM_3.toString();
        if (marCodigos.remove(incideMargem)) {
            retorno.add(incideMargem);
        }

        Collections.sort(marCodigos);
        retorno.addAll(marCodigos);

        return retorno;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "menCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" incluindo as informações
     * sobre o filtro de plano do permissionário.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do endereço do permissionário
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroMenCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String menCodigo = null;
        if (parameterMap.containsKey("menCodigo")) {
            final String value[] = parameterMap.get("menCodigo");
            if ((value.length == 0) || "".equals(value[0])) {
                subTitulo.append(System.getProperty("line.separator")).append(com.zetra.econsig.helper.texto.ApplicationResourcesHelper.getMessage("rotulo.mensagem.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
            } else {
                menCodigo = new String();
                subTitulo.append(System.getProperty("line.separator")).append(com.zetra.econsig.helper.texto.ApplicationResourcesHelper.getMessage("rotulo.mensagem.arg0", responsavel, ""));

                final String[] aux = value[0].split(";");
                menCodigo = aux[0];
                subTitulo.append(" ").append(aux[1]);
            }
        }
        return menCodigo;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "nseCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de serviço.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de serviço
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroNseCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> nseCodigos = null;

        if (parameterMap.containsKey("nseCodigo")) {
            final String[] nses = parameterMap.get("nseCodigo");
            if (!"".equals(nses[0])) {
                nseCodigos = new ArrayList<>();
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.natureza.servico.arg0", responsavel, ""));
                for (int i = 0; i < nses.length; i++) {
                    final String[] values = nses[i].split(";");
                    nseCodigos.add(values[0]);
                    if (i == (nses.length - 1)) {
                        subTitulo.append(" ").append(values[1]);
                    } else {
                        subTitulo.append(" ").append(values[1]).append(",");
                    }
                }
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.natureza.servico.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
            }
        }

        return nseCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "ORDENACAO" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de ordenação.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, atualmente não é alterado por este método
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroOrdenacao(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String order = "";

        // Ordenação
        final String ordenacao[] = parameterMap.containsKey("ORDENACAO") ? (String[]) parameterMap.get("ORDENACAO") : null;
        if ((ordenacao != null) && !TextHelper.isNull(ordenacao[0])) {
            order = ordenacao[0];
        }

        return order;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "orgCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de órgão.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de órgão
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroOrgCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> orgCodigos = null;
        List<String> orgNames = null;

        if (!responsavel.isOrg() || (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO))) {
            if (parameterMap.containsKey("orgCodigo")) {
                final String[] values = parameterMap.get("orgCodigo");
                if ("".equals(values[0])) {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                } else {
                    orgCodigos = new ArrayList<>();
                    orgNames = new ArrayList<>();
                    try {
                        for(final String value : values){
                            final String[] separ = value.split(";");
                            orgCodigos.add(separ[0]);
                            orgNames.add(separ[2] + " ");
                        }
                        subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, String.valueOf(orgNames).replace("[", "").replace("]", "")));
                    } catch (final Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        } else {
        	orgCodigos = new ArrayList<>();
            orgCodigos.add(responsavel.getOrgCodigo());
            final String descricao = responsavel.getNomeEntidade();
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, descricao));
        }

        return orgCodigos;
    }

    protected List<String> getFiltroOrgCodigoIn(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> orgCodigos = null;
        List<String> orgNames = null;

        if (!responsavel.isOrg() || (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO))) {
            if (parameterMap.containsKey("orgCodigo")) {
                final String[] values = parameterMap.get("orgCodigo");
                if ("".equals(values[0])) {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                } else {
                    try {
                        orgCodigos = new ArrayList<>();
                        orgNames = new ArrayList<>();
                        for(final String value : values){
                            final String[] separ = value.split(";");
                            orgCodigos.add("'" + separ[0] + "'");
                            orgNames.add(separ[2] + " ");
                        }
                        subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, String.valueOf(orgNames).replace("[", "").replace("]", "")));
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        } else {
            orgCodigos = new ArrayList<>();
            orgCodigos.add(responsavel.getOrgCodigo());
            final String descricao = responsavel.getNomeEntidade();
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, descricao));
        }

        return orgCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "orgCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de órgão.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de órgão
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroSboCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String sboCodigo = "";
        if (parameterMap.containsKey("sboCodigo")) {
            String values[] = parameterMap.get("sboCodigo");
            if ((values.length == 0) || "".equals(values[0])) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.suborgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                sboCodigo = null;
            } else {
                values = values[0].split(";");
                sboCodigo = values[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.suborgao.singular.arg0", responsavel, values[2]));
            }
        }

        return sboCodigo;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "orgCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de órgão.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de órgão
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroUniCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String uniCodigo = "";
        if (parameterMap.containsKey("uniCodigo")) {
            String values[] = parameterMap.get("uniCodigo");
            if ((values.length == 0) || "".equals(values[0])) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.unidade.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                uniCodigo = null;
            } else {
                values = values[0].split(";");
                uniCodigo = values[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.unidade.singular.arg0", responsavel, values[2]));
            }
        }

        return uniCodigo;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "chkOrigem" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de origem do contrato.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de origem do contrato
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroOrigemContrato(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        // Origem contrato  parameterMap.get("chkOrigem")
        final String[] origemAde = parameterMap.containsKey("chkOrigem") ? (String[]) parameterMap.get("chkOrigem") : null;
        final List<String> origensAdes = origemAde != null ? Arrays.asList(origemAde) : null;
        if ((origensAdes != null) && !origensAdes.isEmpty()) {
            subTitulo.append(System.getProperty("line.separator"));

            for (int i = 0; i < origemAde.length; i++) {
                if (CodedValues.ORIGEM_ADE_NOVA.equals(origemAde[i])) {
                    if (i == 0) {
                        subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.origem.contrato.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.novo.contrato", responsavel).toUpperCase()));
                    } else {
                        subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.novo.contrato", responsavel).toUpperCase());
                    }
                } else if (CodedValues.ORIGEM_ADE_COMPRADA.equals(origemAde[i])) {
                    if (i == 0) {
                        subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.origem.contrato.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.compra.contrato.abreviado", responsavel).toUpperCase()));
                    } else {
                        subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.compra.contrato.abreviado", responsavel).toUpperCase());
                    }

                } else if (CodedValues.ORIGEM_ADE_RENEGOCIADA.equals(origemAde[i])) {
                    if (i == 0) {
                        subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.origem.contrato.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.renegociacao", responsavel).toUpperCase()));
                    } else {
                        subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.renegociacao", responsavel).toUpperCase());
                    }

                }
                subTitulo.append(",");
            }
            subTitulo.deleteCharAt(subTitulo.length() - 1);

        }

        return origensAdes;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "pendenciaVencida" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de pendência vencida.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de pendências vencidas
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected Boolean getFiltroPendenciaVencida(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        // Pendência Vencida
        Boolean pendenciaVencida = null;
        if (parameterMap.containsKey("pendenciaVencida")) {
            pendenciaVencida = Boolean.valueOf(getParametro("pendenciaVencida", parameterMap));
            subTitulo.append(System.getProperty("line.separator"));
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.pendencias.somente.ja.vencidas.arg0", responsavel, ApplicationResourcesHelper.getMessage(pendenciaVencida ? "rotulo.sim" : "rotulo.nao", responsavel)));
        }

        return pendenciaVencida;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "periodicidade" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de periodicidade.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, atualmente não é alterado por este método
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroPeriodicidade(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String periodicidade = "";
        final String per[] = parameterMap.containsKey("periodicidade") ? (String[]) parameterMap.get("periodicidade") : null;
        if ((per != null) && !TextHelper.isNull(per[0])) {
            periodicidade = per[0];
        }

        return periodicidade;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "periodoIni" e o "periodoFim" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" incluindo as informações
     * sobre o filtro do período
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do período
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected Map<String, String> getFiltroPeriodo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        final Map<String, String> datas = new HashMap<>();

        String strPeriodo = "";
        String strIniPeriodo = "";
        String strFimPeriodo = "";
        String paramPeriodo = "";
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";
        if (parameterMap.containsKey("periodoIni")) {
            strIniPeriodo = getParametro("periodoIni", parameterMap);
            if (!TextHelper.isNull(strIniPeriodo)) {
                paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
            }
        }
        if (parameterMap.containsKey("periodoFim")) {
            strFimPeriodo = getParametro("periodoFim", parameterMap);
            if (!TextHelper.isNull(strFimPeriodo)) {
                paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
            }
        }
        if (parameterMap.containsKey("periodo")) {
            strPeriodo = getParametro("periodo", parameterMap);
            if (!TextHelper.isNull(strPeriodo)) {
                paramPeriodo = formatarPeriodo(strPeriodo);
            }
        }

        if (!TextHelper.isNull(paramPeriodo) || !TextHelper.isNull(paramIniPeriodo) || !TextHelper.isNull(paramFimPeriodo)) {
            subTitulo.append(System.getProperty("line.separator"));
        }

        if (!TextHelper.isNull(paramPeriodo)) {
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, strPeriodo));
        } else if (!TextHelper.isNull(paramIniPeriodo) && !TextHelper.isNull(paramFimPeriodo)) {
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo));
        } else if (!TextHelper.isNull(paramIniPeriodo) && TextHelper.isNull(paramFimPeriodo)) {
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.periodo.a.partir.de.arg0", responsavel, strIniPeriodo));
        } else if (TextHelper.isNull(paramIniPeriodo) && !TextHelper.isNull(paramFimPeriodo)) {
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.periodo.ate.arg0", responsavel, strFimPeriodo));
        }

        datas.put("PERIODO", paramPeriodo);
        datas.put("PERIODO_INICIAL", paramIniPeriodo);
        datas.put("PERIODO_FINAL", paramFimPeriodo);

        return datas;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "periodoIni" e o "periodoFim" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @return
     */
    protected Map<String, String> getFiltroPeriodoIniFim(Map<String, String[]> parameterMap) {
        final Map<String, String> datas = new HashMap<>();

        String strIniPeriodo = "";
        String strFimPeriodo = "";
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";
        if (parameterMap.containsKey("periodoIni")) {
            strIniPeriodo = getParametro("periodoIni", parameterMap);
            if (!TextHelper.isNull(strIniPeriodo)) {
                paramIniPeriodo = formatarPeriodo(strIniPeriodo);
            }
        }
        if (parameterMap.containsKey("periodoFim")) {
            strFimPeriodo = getParametro("periodoFim", parameterMap);
            if (!TextHelper.isNull(strFimPeriodo)) {
                paramFimPeriodo = formatarPeriodo(strFimPeriodo);
            }
        }

        datas.put("PERIODO_INICIAL", paramIniPeriodo);
        datas.put("PERIODO_FINAL", paramFimPeriodo);

        return datas;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "plaCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" incluindo as informações
     * sobre o filtro de plano do permissionário.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do endereço do permissionário
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroPlanoCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String plaCodigo = null;
        if (parameterMap.containsKey("PLA_CODIGO")) {
            final String values[] = parameterMap.get("PLA_CODIGO");
            if ((values.length == 0) || "".equals(values[0])) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.plano.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            } else {
                plaCodigo = new String();
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.plano.singular.arg0", responsavel, ""));
                for (int i = 0; i < values.length; i++) {
                    final String[] aux = values[i].split(";");
                    plaCodigo = aux[0];
                    subTitulo.append(" ").append(aux[1]);
                    if ((i + 1) != values.length) {
                        subTitulo.append(", ");
                    }
                }
            }
        }
        return plaCodigo;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "RSE_MATRICULA" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de matrícula do servidor.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de matrícula do servidor
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroRseMatricula(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        final String rseMatricula = parameterMap.containsKey("RSE_MATRICULA") ? getParametro("RSE_MATRICULA", parameterMap) : null;

        if (!TextHelper.isNull(rseMatricula)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.arg0", responsavel, rseMatricula));
        }

        return rseMatricula;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "sadCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de status autorização.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de status autorização
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroSadCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = null;

        if (parameterMap.containsKey("SAD_CODIGO")) {
            final String[] situacoes = parameterMap.get("SAD_CODIGO");
            subTitulo.append(System.getProperty("line.separator"));
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.status.arg0", responsavel, "").toUpperCase());
            if ((situacoes != null) && (situacoes.length > 0) && !TextHelper.isNotNumeric(situacoes[0])) {
                sadCodigos = new ArrayList<>();
                try {
                    final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
                    final Map<String, String> status = adeDelegate.selectStatusAutorizacao(responsavel);
                    for (final String situacoe : situacoes) {
                        sadCodigos.add(situacoe);
                        subTitulo.append(status.get(situacoe).toString().toUpperCase()).append(",");
                    }
                    subTitulo.setLength(subTitulo.length() - 1);
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            } else {
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase());
            }
        }

        return sadCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "scvCodigo" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, atualmente não é alterado por este método
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroScvCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> scvCodigos = new ArrayList<>();
        if (parameterMap.containsKey("scvCodigo")) {
            final String scvs[] = parameterMap.get("scvCodigo");
            if (!TextHelper.isNull(scvs[0])) {
                scvCodigos = Arrays.asList(scvs);
            }
        }

        return scvCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "SPD_CODIGO" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, atualmente não é alterado por este método
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroSpdCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        final String[] spd = parameterMap.containsKey("SPD_CODIGO") ? (String[]) parameterMap.get("SPD_CODIGO") : null;
        return spd != null ? Arrays.asList(spd) : null;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "srsCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de status de servidor.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de status do servidor
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroSrsCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> srsCodigos = null;
        if (parameterMap.containsKey("SRS_CODIGO")) {
            final String values[] = parameterMap.get("SRS_CODIGO");
            if ((values.length == 0) || "".equals(values[0])) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.situacao.servidor.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            } else {
                srsCodigos = new ArrayList<>();
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.situacao.servidor.arg0", responsavel, ""));
                for (int i = 0; i < values.length; i++) {
                    final String[] aux = values[i].split(";");
                    srsCodigos.add(aux[0]);
                    subTitulo.append(" ").append(aux[1]);
                    if ((i + 1) != values.length) {
                        subTitulo.append(", ");
                    }
                }
            }
        }
        return srsCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "STU_CODIGO" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, atualmente não é alterado por este método
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroStuCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> stuCodigos = new ArrayList<>();
        if (parameterMap.containsKey("STU_CODIGO")) {
            final String stus[] = parameterMap.get("STU_CODIGO");
            if (!TextHelper.isNull(stus[0])) {
                stuCodigos = Arrays.asList(stus);
            }
        }

        return stuCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "svcCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de serviço.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de serviço
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroSvcCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> svcCodigos = null;

        if (parameterMap.containsKey("svcCodigo")) {
            final String svcs[] = parameterMap.get("svcCodigo");
            if (!"".equals(svcs[0])) {
                final Set<String> svcCodigosSet = new HashSet<>();
                final List<String> svcDescricoes = new ArrayList<>();
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ""));
                for (final String svc : svcs) {
                    final String[] values = svc.split(";");
                    if ((values.length > 2) && !svcCodigosSet.contains(values[0])) {
                        svcCodigosSet.add(values[0]);
                        svcDescricoes.add(values[2]);
                    }
                }
                svcCodigos = new ArrayList<>(svcCodigosSet);
                subTitulo.append(" ").append(TextHelper.join(svcDescricoes, ","));
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }
        }

        return svcCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "ncaCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de serviço.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de serviço
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroNcaCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> ncaCodigos = null;

        if (parameterMap.containsKey("ncaCodigo")) {
            final String ncas[] = parameterMap.get("ncaCodigo");
            if (!"".equals(ncas[0])) {
                final Set<String> ncaCodigosSet = new HashSet<>();
                final List<String> ncaDescricoes = new ArrayList<>();
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ""));
                for (final String nca : ncas) {
                    final String[] values = nca.split(";");
                    if (!ncaCodigosSet.contains(values[0])) {
                        if (values.length > 2) {
                            ncaCodigosSet.add(values[0]);
                            ncaDescricoes.add(values[2]);
                        } else if (values.length == 2) {
                            ncaCodigosSet.add(values[0]);
                            ncaDescricoes.add(values[1]);
                        }
                    }
                }
                ncaCodigos = new ArrayList<>(ncaCodigosSet);
                subTitulo.append(" ").append(TextHelper.join(ncaDescricoes, ","));
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }
        }

        return ncaCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "tagCodigo" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, atualmente não é alterado por este método
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroTagCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> tagCodigos = new ArrayList<>();

        if (parameterMap.get("tagCodigo") != null) {
            final String[] tag = parameterMap.get("tagCodigo");
            if (!TextHelper.isNull(tag[0])) {
                tagCodigos = Arrays.asList(tag);
            }
        }

        return tagCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "Tem anexo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" incluindo as informações
     * sobre o filtro de "Tem anexo".
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do "Tem anexo"
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected Boolean getFiltroTemAnexo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        Boolean temAnexo = null;

        if (parameterMap.containsKey("anexado")) {
            temAnexo = Boolean.valueOf(getParametro("anexado", parameterMap));
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.possuem.anexo.arg0", responsavel, temAnexo ? ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel)));
        }

        return temAnexo;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "chkTermino" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de termino.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, atualmente não é alterado por este método
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroTermino(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        final String[] motivoTerminoAde = parameterMap.containsKey("chkTermino") ? (String[]) parameterMap.get("chkTermino") : null;
        return motivoTerminoAde != null ? Arrays.asList(motivoTerminoAde) : null;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "estCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de grupo serviço.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do grupo serviço
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroTgsCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String grupoServico = null;

        if (parameterMap.containsKey("grupoServico")) {
            final String[] tgs = parameterMap.get("grupoServico");
            if (!TextHelper.isNull(tgs[0])) {
                final String[] values = tgs[0].split(";");
                grupoServico = values[0];
                final String nomeGrupoServico = values[1];

                if (!TextHelper.isNull(nomeGrupoServico)) {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.grupo.servico.arg0", responsavel, nomeGrupoServico));
                }
            }
        }

        return grupoServico;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "Tipo período" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" incluindo as informações
     * sobre o filtro de "Tipo período".
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do "Tipo período"
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroTipoPeriodo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        String tipoPeriodo = parameterMap.containsKey("tipoPeriodo") ? getParametro("tipoPeriodo", parameterMap) : null;

        if (!TextHelper.isNull(tipoPeriodo)) {
            String values[] = parameterMap.get("tipoPeriodo");
            values = values[0].split(";");
            tipoPeriodo = values[0];
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.periodo.arg0", responsavel, values[1]));
        }

        return tipoPeriodo;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "tmoCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" incluindo as informações
     * sobre o filtro de tipo motivo operação.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do tipo motivo operação
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroTmoCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        final List<String> tmoCodigos = new ArrayList<>();
        // tipo motivo operacao
        if (parameterMap.containsKey("tmoCodigo")) {
            final String tmos[] = parameterMap.get("tmoCodigo");
            if (!"".equals(tmos[0])) {
                String values[];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.motivo.operacao.arg0", responsavel, ""));
                for (int i = 0; i < tmos.length; i++) {
                    values = tmos[i].split(";");
                    tmoCodigos.add(values[0]);
                    if (i == (tmos.length - 1)) {
                        subTitulo.append(" ").append(values[1]);
                    } else {
                        subTitulo.append(" ").append(values[1]).append(",");
                    }
                }
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.motivo.operacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }
        }

        return tmoCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "tmrCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" incluindo as informações
     * sobre o filtro de tipo motivo operação.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do tipo motivo operação
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroTmrCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        final List<String> tmrCodigos = new ArrayList<>();
        // Tipo motivo reclamação
        if (parameterMap.containsKey("TMR_CODIGO")) {
            subTitulo.append(System.getProperty("line.separator"));
            final String tmr[] = parameterMap.get("TMR_CODIGO");
            if (!"".equals(tmr[0])) {
                String values[];
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.tipo.motivo.reclamacao.singular.arg0", responsavel, ""));
                for (int i = 0; i < tmr.length; i++) {
                    values = tmr[i].split(";");
                    tmrCodigos.add(values[0]);
                    if (i == (tmr.length - 1)) {
                        subTitulo.append(" ").append(values[1]);
                    } else {
                        subTitulo.append(" ").append(values[1]).append(",");
                    }
                }
                criterio.setAttribute(Columns.TMR_CODIGO, tmrCodigos);
            } else {
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.tipo.motivo.reclamacao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }
        }
        return tmrCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "tocCodigo" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de tipo ocorrência.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do tipo de ocorrência
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroTocCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        List<String> tocCodigos = new ArrayList<>();
        // tipo ocorrencia
        if (parameterMap.containsKey("tocCodigo")) {
            final String tocs[] = parameterMap.get("tocCodigo");
            if (!"".equals(tocs[0])) {
                String values[];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.ocorrencia.arg0", responsavel, ""));
                for (int i = 0; i < tocs.length; i++) {
                    values = tocs[i].split(";");
                    tocCodigos.add(values[0]);
                    if (i == (tocs.length - 1)) {
                        subTitulo.append(" ").append(values[1]);
                    } else {
                        subTitulo.append(" ").append(values[1]).append(",");
                    }
                }
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.ocorrencia.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                tocCodigos = CodedValues.TOC_CODIGOS_AUTORIZACAO;
            }
        }

        return tocCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "TPE_CODIGO" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de tipo penalidade.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, atualmente não é alterado por este método
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroTpeCodigo(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        final String[] tpe = parameterMap.containsKey("TPE_CODIGO") ? (String[]) parameterMap.get("TPE_CODIGO") : null;
        List<String> tpeCodigos = null;

        if (tpe != null) {
            tpeCodigos = new ArrayList<>();
            for (final String element : tpe) {
                if (!TextHelper.isNull(element)) {
                    tpeCodigos.add(element);
                }
            }
        }

        return tpeCodigos;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "LOG_CANAL" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo"
     * sobre o filtro de canal de acesso.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de matrícula do servidor
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroCanal(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        final String canal = parameterMap.containsKey("canal") ? getParametro("canal", parameterMap) : null;

        if (!TextHelper.isNull(canal)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.canal.arg0", responsavel, CanalEnum.get(canal).name()));
        }

        return canal;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "somenteFuncoesSensiveis" a serem utilizadas para a geração do relatório.
     * Atualiza também o "subTitulo"
     * sobre o filtro de somente funções sensíveis.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de somente funções sensíveis
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroSomenteFuncoesSensiveis(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        final String somenteFuncoesSensiveis = parameterMap.containsKey("somenteFuncoesSensiveis") ? getParametro("somenteFuncoesSensiveis", parameterMap) : null;

        if (!TextHelper.isNull(somenteFuncoesSensiveis)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.somente.funcoes.sensiveis.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel)));
        }

        return somenteFuncoesSensiveis;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "chkPrdRealizado" a ser utilizado para a geração do relatório.
     * Atualiza também o "subTitulo" e "nomeArquivo" incluindo as informações
     * sobre o filtro de realizado da parcela.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de origem do contrato
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected List<String> getFiltroParcelaRealizado(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        // Parcela realizado  parameterMap.get("chkPrdRealizado")
        final String[] prdRealizado = parameterMap.containsKey("chkPrdRealizado") ? (String[]) parameterMap.get("chkPrdRealizado") : null;
        final List<String> lstPrdRealizado = prdRealizado != null ? Arrays.asList(prdRealizado) : null;
        if ((lstPrdRealizado != null) && !lstPrdRealizado.isEmpty()) {
            subTitulo.append(System.getProperty("line.separator"));

            for (int i = 0; i < prdRealizado.length; i++) {
                String message = "";
                if (CodedValues.REL_FILTRO_VLR_REALIZADO_MENOR_PREVISTO.equals(prdRealizado[i])) {
                    message = ApplicationResourcesHelper.getMessage("rotulo.relatorio.movimentofinanceiro.valor.menor.previsto", responsavel).replace(".", "").toLowerCase();
                    if (i > 0) {
                        subTitulo.append(message);
                    }
                }
                if (CodedValues.REL_FILTRO_VLR_REALIZADO_IGUAL_PREVISTO.equals(prdRealizado[i])) {
                    message = ApplicationResourcesHelper.getMessage("rotulo.relatorio.movimentofinanceiro.valor.igual.previsto", responsavel).replace(".", "").toLowerCase();
                    if (i > 0) {
                        subTitulo.append(message);
                    }
                }
                if (CodedValues.REL_FILTRO_VLR_REALIZADO_MAIOR_PREVISTO.equals(prdRealizado[i])) {
                    message = ApplicationResourcesHelper.getMessage("rotulo.relatorio.movimentofinanceiro.valor.maior.previsto", responsavel).replace(".", "").toLowerCase();
                    if (i > 0) {
                        subTitulo.append(message);
                    }

                }

                if (i == 0) {
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.prd.realizado.arg0", responsavel, message));
                }

                subTitulo.append(",");
            }
            subTitulo.deleteCharAt(subTitulo.length() - 1).append(".");
        }

        return lstPrdRealizado;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "agrupamento" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação de matrícula do servidor
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected boolean getFiltroAgrupamento(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        return !parameterMap.containsKey("agrupamento") || (parameterMap.containsKey("agrupamento") && Boolean.valueOf(getParametro("agrupamento", parameterMap)));
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "Tipo justiça" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do "Tipo período"
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroTipoJustica(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        return parameterMap.containsKey("tjuCodigo") ? getParametro("tjuCodigo", parameterMap) : null;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "Tipo justiça" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do "Tipo período"
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroEstadoJustica(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        return parameterMap.containsKey("djuEstado") ? getParametro("djuEstado", parameterMap) : null;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "Tipo justiça" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do "Tipo período"
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroComarcaJustica(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        return parameterMap.containsKey("djuComarca") ? getParametro("djuComarca", parameterMap) : null;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "Tipo justiça" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do "Tipo período"
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroNumeroProcessoJustica(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        return parameterMap.containsKey("djuNumProcesso") ? getParametro("djuNumProcesso", parameterMap) : null;
    }

    public String getHoje(String formato) {
        return DateHelper.format(DateHelper.getSystemDatetime(), formato);
    }

    protected int getIndex(List<SinteticoDataSourceBean> lista, String nome, String periodo) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getNome().equalsIgnoreCase(nome) && lista.get(i).getPeriodo().equalsIgnoreCase(periodo)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * A partir do tipo, do responsável e dos parâmetros de requisição,
     * determina o nome do arquivo com os devidos filtros presentes no nome
     *
     */
    protected String getNomeArquivo(String tipo, AcessoSistema responsavel, Map<String, String[]> parameterMap, String append) {
        final StringBuilder nome = new StringBuilder();

        nome.append(tipo).append("_");

        if (parameterMap.containsKey("periodoIni") && !TextHelper.isNull(parameterMap.get("periodoIni")[0]) && parameterMap.containsKey("periodoFim") && !TextHelper.isNull(parameterMap.get("periodoFim")[0]) && !parameterMap.get("periodoIni")[0].equals(parameterMap.get("periodoFim")[0])) {
            final String ini = getParametro("periodoIni", parameterMap);
            final String fim = getParametro("periodoFim", parameterMap);

            nome.append((ini + "_a_" + fim + "_").replace('/', '-'));
        } else if (parameterMap.containsKey("periodo") && !TextHelper.isNull(parameterMap.get("periodo"))) {
            final String periodo = getParametro("periodo", parameterMap).replace("/", "");
            nome.append(periodo).append("_");
        } else if (parameterMap.containsKey("periodoIni") && !TextHelper.isNull(parameterMap.get("periodoIni")[0]) && parameterMap.containsKey("periodoFim") && !TextHelper.isNull(parameterMap.get("periodoFim")[0]) && parameterMap.get("periodoIni")[0].equals(parameterMap.get("periodoFim")[0])) {
            final String ini = getParametro("periodoIni", parameterMap);

            nome.append((ini + "_").replace('/', '-'));
        }

        /* Filtro de Consignante */
        if (parameterMap.containsKey("cseCodigo")) {
            String values[] = parameterMap.get("cseCodigo");
            if ((values.length != 0) && !"".equals(values[0]) && !"NENHUM".equalsIgnoreCase(values[0])) {
                values = values[0].split(";");
                nome.append(values[1].replaceAll("[^\\dA-Za-z0-9 ]", "")).append("_");
            }
        }

        /* Filtro de Estabelecimento*/
        if (parameterMap.containsKey("estCodigo")) {
            String values[] = parameterMap.get("estCodigo");
            if ((values.length != 0) && !"".equals(values[0])) {
                values = values[0].split(";");
                nome.append(values[1].replaceAll("[^\\dA-Za-z0-9 ]", "")).append("_");
            }
        }

        /* Fitro de Órgão */
        if (parameterMap.containsKey("orgCodigo")) {
            final String orgCodigo = responsavel.getOrgCodigo();

            String values[] = parameterMap.get("orgCodigo");
            if (((values.length == 0) && "".equals(values[0])) || ((orgCodigo != null) && values[0].equals(orgCodigo))) {
                if (!TextHelper.isNull(orgCodigo)) {
                    final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                    OrgaoTransferObject orgTransferObject;
                    try {
                        orgTransferObject = cseDelegate.findOrgao(orgCodigo, responsavel);
                        nome.append(orgTransferObject.getOrgIdentificador().replaceAll("[^\\dA-Za-z0-9 ]", "")).append("_");
                    } catch (final ConsignanteControllerException ex) {
                        codigoRetorno = ERRO;
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel) + ".<br>";
                        LOG.error(mensagem, ex);
                    }
                }
            } else {
                values = values[0].split(";");
                if (values.length > 1) {
                    nome.append(values[1].replaceAll("[^\\dA-Za-z0-9 ]", "")).append("_");
                }
            }
        }

        /* Fitro Consignatária */
        if (parameterMap.containsKey("csaCodigo")) {
            final String csaCodigo = responsavel.getCsaCodigo();

            String values[] = parameterMap.get("csaCodigo");
            if ((values.length == 0) || "".equals(values[0]) || ((csaCodigo != null) && values[0].equals(csaCodigo))) {
                if (!TextHelper.isNull(csaCodigo)) {
                    try {
                        final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                        final ConsignatariaTransferObject csaTransferObject = csaDelegate.findConsignataria(csaCodigo, responsavel);
                        nome.append(csaTransferObject.getCsaIdentificador().replaceAll("[^\\dA-Za-z0-9 ]", "")).append("_");
                    } catch (final ConsignatariaControllerException ex) {
                        codigoRetorno = ERRO;
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel) + ".<br>";
                        LOG.error(mensagem, ex);
                    }
                }
            } else {
                for (final String valor : values) {
                    if (!TextHelper.isNull(valor)) {
                        values = valor.split(";");
                        if (values.length > 1) {
                            nome.append(values[1].replaceAll("[^\\dA-Za-z0-9 ]", "")).append("_");
                        }
                    }
                }
            }
        }

        /* Filtro de Correspondente */
        if (parameterMap.containsKey("corCodigo")) {
            final String corCodigo = responsavel.getCorCodigo();
            String values[] = parameterMap.get("corCodigo");

            if ((values.length == 0) || "".equals(values[0]) || ((corCodigo != null) && values[0].equals(corCodigo))) {
                if (!TextHelper.isNull(corCodigo)) {
                    try {
                        final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                        final CorrespondenteTransferObject corTransferObject = csaDelegate.findCorrespondente(corCodigo, responsavel);
                        nome.append(corTransferObject.getCorIdentificador().replaceAll("[^\\dA-Za-z0-9 ]", "")).append("_");
                    } catch (final ConsignatariaControllerException ex) {
                        codigoRetorno = ERRO;
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel) + ".<br>";
                        LOG.error(mensagem, ex);
                    }
                }
            } else {
                values = values[0].split(";");
                if (values.length > 1) {
                    nome.append(values[2].replaceAll("[^\\dA-Za-z0-9 ]", "")).append("_");
                }
            }
        }

        /* Empresa Correspondente */
        if (parameterMap.containsKey("ecoCodigo")) {
            String[] values = parameterMap.get("ecoCodigo");
            if ((values.length != 0) && !"".equals(values[0])) {
                values = TextHelper.split(values[0], ";");
                if (!"".equals(values[1])) {
                    nome.append(values[1].replaceAll("[^\\dA-Za-z0-9 ]", "")).append("_");
                }
            }
        }

        /* Append opcional */
        if (!TextHelper.isNull(append)) {
            nome.append(append).append("_");
        }

        nome.append(getHoje("ddMMyyHHmmss"));

        return nome.toString();
    }

    protected Boolean getFiltroDecisaoJudicial(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        Boolean tmoDecisaoJudicial = false;

        if (parameterMap.containsKey("tmoDecisaoJudicial")) {
        	tmoDecisaoJudicial = Boolean.valueOf(getParametro("tmoDecisaoJudicial", parameterMap));
        }

        return tmoDecisaoJudicial;
    }

    public String getParametro(String parametro, Map<String, String[]> parameterMap) {
        return !TextHelper.isNull(parameterMap.get(parametro)) ? parameterMap.get(parametro)[0] : null;
    }

    public String getPath(AcessoSistema responsavel) {
        return ParamSist.getDiretorioRaizArquivos();
    }

    public String getStrFormato() {
        return formatoRelatorio;
    }

    public String getTextoRodape(AcessoSistema responsavel) {
        return getTextoRodape(false, null, responsavel);
    }

    public String getTextoRodape(boolean portrait, HttpSession session, AcessoSistema responsavel) {
        final String nomeSistema = JspHelper.getNomeSistema(responsavel);
        final String cseNome = getCseNome(responsavel);
        final String versaoAtual = ApplicationResourcesHelper.getMessage("release.tag", responsavel);
        final String dataGeracao = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());
        String responsavelGeracao = ApplicationResourcesHelper.getMessage("rotulo.gerado.por.arg0", responsavel, responsavel.getUsuLogin());

        if (responsavel.isSistema()) {
            responsavelGeracao = ApplicationResourcesHelper.getMessage("rotulo.gerado.pelo.sistema", responsavel);
        }

        final int cseNomeMaxSize = (portrait ? TAMANHO_RODAPE_MAXIMO_PORTRAIT : TAMANHO_RODAPE_MAXIMO_LANDSCAPE) - (nomeSistema.length() + dataGeracao.length() + versaoAtual.length() + responsavelGeracao.length() + 12);
        final int cseNomeSize = cseNome.length() < cseNomeMaxSize ? cseNome.length() : cseNomeMaxSize;

        return nomeSistema + " - " + cseNome.substring(0, cseNomeSize) + " - " + dataGeracao + " - " + versaoAtual + " - " + responsavelGeracao;
    }

    public String getTextoRodape(HttpSession session, AcessoSistema responsavel) {
        return getTextoRodape(false, null, responsavel);
    }

    public String reformat(String periodo, String patternIn, String patternOut) {
        try {
            return DateHelper.reformat(periodo, patternIn, patternOut);
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            return periodo;
        }
    }

    public String setMensagem(String nome, String strTipo, String titulo, HttpSession session) {
        nomeArqRelatorio = nome;

        try {
            if (session != null) {
                final Map<String, String> map = new HashMap<>();
                map.put("nome", nome.substring(Math.max(nome.lastIndexOf(File.separator), 0)));
                map.put("strTipo", strTipo);
                map.put("titulo", titulo);
                session.setAttribute("arquivoRelatorio", JSONValue.toJSONString(map));
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return mensagem;
    }

    /**
     * A partir do usuário responsável e dos parâmetros de requisição,
     * determina o "Nome" a ser utilizado para a geração do relatório.
     * @param parameterMap : Mapa de parâmetros de requisição
     * @param subTitulo    : Sub-Título do relatório, será atualizado com a informação do "Tipo período"
     * @param nomeArquivo  : Nome do arquivo do relatório, atualmente não é alterado por este método
     * @param session      : Sessão atual do usuário responsável
     * @param responsavel  : Usuário responsável pela geração do relatório
     * @return
     */
    protected String getFiltroNome(Map<String, String[]> parameterMap, StringBuilder subTitulo, StringBuilder nomeArquivo, HttpSession session, AcessoSistema responsavel) {
        return parameterMap.containsKey("nome") ? getParametro("nome", parameterMap) : null;
    }

    public String getCaminhoSetaCimaLegenda(AcessoSistema responsavel) {
        final File dir = new File(getPath(responsavel) + "/conf/relatorio/imagem/");
        if (!dir.exists() && !dir.mkdirs()) {
            LOG.debug("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
            return null;
        }
        final String path = dir.getAbsolutePath() + "/seta_cima.png";
        if (!new File(path).exists()) {
            final InputStream input = getClass().getResourceAsStream("/com/zetra/econsig/report/jasper/imagem/seta_cima.png");
            try {
                FileHelper.saveStreamToFile(input, path);
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
                return null;
            }
        }
        return path;
    }

    public String getCaminhoSetaBaixoLegenda(AcessoSistema responsavel) {
        final File dir = new File(getPath(responsavel) + "/conf/relatorio/imagem/");
        if (!dir.exists() && !dir.mkdirs()) {
            LOG.debug("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
            return null;
        }
        final String path = dir.getAbsolutePath() + "/seta_baixo.png";
        if (!new File(path).exists()) {
            final InputStream input = getClass().getResourceAsStream("/com/zetra/econsig/report/jasper/imagem/seta_baixo.png");
            try {
                FileHelper.saveStreamToFile(input, path);
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
                return null;
            }
        }
        return path;
    }

    public String getCaminhoRetanguloLegenda(AcessoSistema responsavel) {
        final File dir = new File(getPath(responsavel) + "/conf/relatorio/imagem/");
        if (!dir.exists() && !dir.mkdirs()) {
            LOG.debug("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
            return null;
        }
        final String path = dir.getAbsolutePath() + "/retangulo.png";
        if (!new File(path).exists()) {
            final InputStream input = getClass().getResourceAsStream("/com/zetra/econsig/report/jasper/imagem/retangulo.png");
            try {
                FileHelper.saveStreamToFile(input, path);
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
                return null;
            }
        }
        return path;
    }
}
