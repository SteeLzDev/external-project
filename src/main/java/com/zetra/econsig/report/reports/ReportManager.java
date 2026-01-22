package com.zetra.econsig.report.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.RelatorioDAO;
import com.zetra.econsig.persistence.entity.Subrelatorio;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.dao.ReportDAO;
import com.zetra.econsig.report.jasper.CsvExporter;
import com.zetra.econsig.report.jasper.XMLExporter;
import com.zetra.econsig.service.relatorio.SubrelatorioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOdsReportConfiguration;
import net.sf.jasperreports.export.SimpleOdtReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

/**
 * <p> Title: ReportManager</p>
 * <p> Description: Gerencia a construção dos relatórios.</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReportManager {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReportManager.class);

    public static final String PATH_JRXML = "/com/zetra/econsig/report/jasper/template/";

    public static final String BAND_PAGEHEADER     = "pageHeader";
    public static final String BAND_COLUMNHEADER   = "columnHeader";
    public static final String BAND_DETAIL         = "detail";
    public static final String BAND_COLUMNFOOTER   = "columnFooter";
    public static final String BAND_PAGEFOOTER     = "pageFooter";
    public static final String BAND_LASTPAGEFOOTER = "lastPageFooter";
    public static final String BAND_SUMMARY        = "summary";

    // Chave para indicar o parametro que representa as colunas a serem acrescentadas ao relatório.
    public static final String KEY_NAME_COLUMNS = "report.columns";
    // Chave para indicar o parametro que representa os Fields a serem acrescentados ao relatório.
    public static final String KEY_NAME_FIELDS = "report.fields";
    // Chave para indicar o parametro que informa se as colunas acrescentadas devem provocar expansão da largura do relatório.
    public static final String KEY_NAME_COLUMNS_EXPAND_WIDTH = "report.columns.expand.width";

    // Chave para personalizacao de nome do arquivo gerado
    public static final String REPORT_FILE_NAME    = "REPORT_FILE_NAME";
    public static final String REPORT_DIR_EXPORT   = "REPORT_DIR_EXPORT";
    public static final String REPORT_SCRIPTLET    = "REPORT_SCRIPTLET";

    public static final String PARAM_NAME_DELIMITADOR_CAMPOS    = "DELIMITADOR";
    public static final String PARAM_NAME_CAMINHO_LOGO          = "CAMINHO_LOGO";

    public static final String COLUMN_ADE_NUM_NOVO             = "COLUMN_ADE_NUM_NOVO";
    public static final String PARAM_NAME_CAMINHO_LOGO_ENTIDADE = "CAMINHO_LOGO_ENTIDADE";
    public static final String PARAM_NAME_CAMINHO_LOGO_ECONSIG  = "CAMINHO_LOGO_ECONSIG";
    public static final String PARAM_NAME_CRITERIO              = "CRITERIO";
    public static final String PARAM_NAME_FORMATO_ARQUIVO       = "FORMATO_ARQUIVO";
    public static final String PARAM_NAME_ORGAO                 = "ORGAO";
    public static final String PARAM_NAME_ESTABELECIMENTO       = "ESTABELECIMENTO";
    public static final String PARAM_NAME_PERIODO               = "PERIODO";
    public static final String PARAM_NAME_PERIODO_INICIO        = "PERIODO_INICIO";
    public static final String PARAM_NAME_PERIODO_FIM           = "PERIODO_FIM";
    public static final String PARAM_NAME_MES_ANO               = "MES_ANO";
    public static final String PARAM_NAME_ANO_ATUAL             = "ANO_ATUAL";
    public static final String PARAM_NAME_DATA_ATUAL            = "DATA_ATUAL";
    public static final String PARAM_NAME_SUBTITULO             = "SUBTITULO";
    public static final String PARAM_NAME_TEXTO_RODAPE          = "TEXTO_RODAPE";
    public static final String PARAM_NAME_SITUACAO              = "SITUACAO";
    public static final String PARAM_NAME_QTDE_MARGEM           = "QTDE_MARGEM";
    public static final String PARAM_NAME_MARGEM                = "MARGEM";
    public static final String PARAM_NAME_MARGEM_2              = "MARGEM_2";
    public static final String PARAM_NAME_MARGEM_3              = "MARGEM_3";
    public static final String PARAM_NAME_MARGEM_CODIGO         = "MAR_CODIGO";
    public static final String PARAM_NAME_MARGEM_CODIGO_2       = "MAR_CODIGO_2";
    public static final String PARAM_NAME_MARGEM_CODIGO_3       = "MAR_CODIGO_3";
    public static final String PARAM_NAME_MARGEM_CODIGO_4       = "MAR_CODIGO_4";
    public static final String PARAM_NAME_MARGEM_CODIGO_5       = "MAR_CODIGO_5";
    public static final String PARAM_NAME_MARGEM_CODIGO_6       = "MAR_CODIGO_6";
    public static final String PARAM_NAME_MARGEM_CODIGO_7       = "MAR_CODIGO_7";
    public static final String PARAM_NAME_MARGEM_CODIGO_8       = "MAR_CODIGO_8";
    public static final String PARAM_NAME_MARGEM_CODIGO_9       = "MAR_CODIGO_9";
    public static final String PARAM_NAME_MARGEM_CODIGO_10      = "MAR_CODIGO_10";
    public static final String PARAM_NAME_MARGEM_CODIGO_11      = "MAR_CODIGO_11";
    public static final String PARAM_NAME_MARGEM_CODIGO_12      = "MAR_CODIGO_12";
    public static final String PARAM_NAME_MARGEM_CODIGO_13      = "MAR_CODIGO_13";
    public static final String PARAM_NAME_MARGEM_CODIGO_14      = "MAR_CODIGO_14";
    public static final String PARAM_NAME_MARGEM_CODIGO_15      = "MAR_CODIGO_15";
    public static final String PARAM_NAME_MARGEM_CODIGO_16      = "MAR_CODIGO_16";
    public static final String PARAM_NAME_MARGEM_CODIGO_17      = "MAR_CODIGO_17";
    public static final String PARAM_NAME_MARGEM_CODIGO_18      = "MAR_CODIGO_18";
    public static final String PARAM_NAME_MARGEM_CODIGO_19      = "MAR_CODIGO_19";
    public static final String PARAM_NAME_MARGEM_CODIGO_20      = "MAR_CODIGO_20";
    public static final String PARAM_NAME_TITULO                = "TITULO";
    public static final String PARAM_NAME_CSE                   = "CSE";
    public static final String PARAM_NAME_CSA                   = "CSA";
    public static final String PARAM_NAME_COR                   = "COR";
    public static final String PARAM_SUBREPORT_DIR              = "SUBREPORT_DIR";
    public static final String PARAM_CSE_NOME                   = "CSE_NOME";
    public static final String PARAM_NAME_TIPO_AGRUPAMENTO      = "TIPO_AGRUPAMENTO";
    public static final String PARAM_RESPONSAVEL                = "RESPONSAVEL";
    public static final String PARAM_LEGENDA_SETA_CIMA          = "SETA_CIMA";
    public static final String PARAM_LEGENDA_SETA_BAIXO         = "SETA_BAIXO";
    public static final String PARAM_LEGENDA_RETANGULO          = "RETANGULO";
    public static final String PARAM_FUSO_HORARIO               = "FUSO_HORARIO";   

    public static final String CRITERIO_INCLUDE_SUPORTE = "INCLUDE_SUPORTE";

    //
    public static final String STYLE_CELULA = "Celula";

    public static final String JASPER_DIRECTORY = "/conf/relatorio/";

    public static final String JASPER_TEMPLATE_DIRECTORY = "/template/relatorio/";
    public static final String JASPER_TEMP = "/temp";

    //Usada na exportação de texto formatado./private static final Integer TXT_PAGE_HEIGHT = Integer.valueOf(200);

    //Usada na exportação de texto formatado./private static final Integer TXT_PAGE_WIDTH = Integer.valueOf(132);

    private static final ReportManager instance = new ReportManager();

    /*
     * Tamanho da máximo da página que será guardada em memória (virtualização)
     * Quanto menor este número maior o número de arquivos em disco
     * Quanto maior ele for mais memória será consumida
     */
    private static final int PAGE_CACHE = 40;

    private ReportManager() { }

    public static ReportManager getInstance() {
        return instance;
    }

    /**
     * Cria um JRField a partir do nome e da classe informadas.
     * @param fieldName  Nome do campo a ser criado.
     * @param fieldClass Classe a ser usada pelo Field.
     * @return o JRField criado.
     */
    protected JRField createField(String fieldName, Class<?> fieldClass) {
        final JRDesignField field = new JRDesignField();
        field.setName(fieldName);
        field.setValueClass(fieldClass);
        return field;
    }

    protected void addDesignColumn(JasperDesign jasperDesign, ReportColumn column) {
        // Titulo
        final JRDesignStaticText staticText = new JRDesignStaticText();
        staticText.setKey(column.getTitleElementKey());
        staticText.setText(column.getTitle());
        staticText.setHorizontalTextAlign(column.getTitleHorizontalAlignment());
        staticText.setVerticalTextAlign(column.getTitleVerticalAlignment());
        if (column.getTitleBox() != null) {
            staticText.getLineBox().copyTopPen(column.getTitleBox().getTopPen());
            staticText.getLineBox().copyBottomPen(column.getTitleBox().getBottomPen());
            staticText.getLineBox().copyLeftPen(column.getTitleBox().getLeftPen());
            staticText.getLineBox().copyRightPen(column.getTitleBox().getRightPen());
        }
        if (column.getTitleBackcolor() != null) {
            staticText.setBackcolor(column.getTitleBackcolor());
            staticText.setMode(ModeEnum.OPAQUE);
        }
        staticText.setX(column.getTitleX());
        staticText.setY(column.getTitleY());
        staticText.setWidth(column.getTitleWidth());
        staticText.setHeight(column.getTitleHeigth());
        if (BAND_COLUMNHEADER.equals(column.getTitleBandName())) {
            jasperDesign.getColumnHeader().getChildren().add(staticText);
        }
        // Celula
        final JRDesignExpression expression = new JRDesignExpression();
        expression.addFieldChunk(column.getFieldName());
        final JRDesignTextField textField = new JRDesignTextField();
        textField.setExpression(expression);
        textField.setStyle(column.getStyle());
        if (column.getBackcolor() != null) {
            textField.setMode(ModeEnum.OPAQUE);
            textField.setBackcolor(column.getBackcolor());
        }
        textField.setPattern(column.getPattern());
        textField.setHorizontalTextAlign(column.getHorizontalAlignment());
        textField.setVerticalTextAlign(column.getVerticalAlignment());
        if (column.getBox() != null) {
            textField.getLineBox().copyTopPen(column.getBox().getTopPen());
            textField.getLineBox().copyBottomPen(column.getBox().getBottomPen());
            textField.getLineBox().copyLeftPen(column.getBox().getLeftPen());
            textField.getLineBox().copyRightPen(column.getBox().getRightPen());
        }
        textField.setX(column.getTitleX());
        textField.setY(column.getTitleY());
        textField.setWidth(column.getTitleWidth());
        textField.setHeight(column.getTitleHeigth());
        if (BAND_DETAIL.equals(column.getBandName())) {
            jasperDesign.getDetailSection().getBands()[0].getChildren().add(textField);
        }
    }

    /**
     * Constrói o relatório de acordo com os parâmetros fornecidos.
     *
     * @param formato     Formato do arquivo a ser gerado com o relatório.
     * @param parameters  Parâmetros definos no arquivo de template do JasperReports (.jasper)
     * @param criterio    Filtros para a geração da SQL
     * @param relatorio   Classe do tipo do relatório a ser gerado.
     * @param conteudo    Lista com o conteúdo do relatório (caso tenha sido previamente executado)
     * @param responsavel Usuário que está gerando o relatório.
     * @return Nome e diretório do arquivo de relatório gerado sem o formato.
     * @throws ZetraException
     */
    public String build(String formato, Map<String, Object> parameters, CustomTransferObject criterio, Relatorio relatorio, List<Object[]> conteudo, JRDataSource myDataSource, AcessoSistema responsavel) throws ZetraException {
        final SubrelatorioController subrelatorioController = ApplicationContextProvider.getApplicationContext().getBean(SubrelatorioController.class);
        ReportDAO factory = null;
        Session session = null;
        ReportTemplate _report = null;
        String reportName = null;
        String reportPath = null;
        String reportPathName = null;

        boolean deleteCompiled = false;
        File compiledReport = null;

        try {
            _report = (ReportTemplate) Class.forName(relatorio.getClasseReport()).getDeclaredConstructor().newInstance();
            _report.setResponsavel(responsavel);
            _report.setRelatorio(relatorio);

            /*
             * A atomização dessa parte do código é necessária para que somente
             * um único relatório seja executado por vez. Não gerando conflitos
             * entre os arquivos de virtualização, facilitando o gerenciamento
             * e não sobrecarregando o servidor. Como desvantagem, todos os
             * relatórios subseqüentes entrarão na fila de execução não sendo
             * executados ao mesmo tempo.
             */
            synchronized (this) {
                // Recupera o path do sistema de acordo com o parametro
                // e acrescenta o diretório jasper
                final String diretorioRaiz = ParamSist.getDiretorioRaizArquivos();

                // Configuracao para criar arquivo .java que será compilado em um diretório externo
                DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
                JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.compiler.temp.dir", diretorioRaiz + JASPER_TEMP);

                // Recupera qual será o template do iReports (.jasper) será utilizado
                String template = retornaLayoutRelatorioEntidade(relatorio, diretorioRaiz, responsavel);

                // Verifica se o template do relatório é customizado e está localizado no diretório externo de arquivos
                final String jasperTemplate = diretorioRaiz + JASPER_TEMPLATE_DIRECTORY + template.replaceAll(".jasper", ".jrxml");
                final File arquivoJasperTemplate = new File(jasperTemplate);
                final boolean relPossuiTemplateCustomizado = CodedValues.TPC_SIM.equals(relatorio.getCustomizado()) && ((arquivoJasperTemplate != null) && arquivoJasperTemplate.isFile() && arquivoJasperTemplate.exists());

                if (relPossuiTemplateCustomizado) {
                    final Collection<Subrelatorio> subrelatorios = subrelatorioController.lstSubrelatorio(relatorio.getTipo());
                    String query = null;
                    for (final Subrelatorio subrelatorio : subrelatorios) {
                        if (!TextHelper.isNull(subrelatorio.getSreTemplateSql())) {
                            query = _report.getSqlSubrelatorio(subrelatorio.getSreTemplateSql(), criterio);
                        } else {
                            query = _report.getSql(criterio);
                        }
                        parameters.put(subrelatorio.getSreNomeParametro(), processaQuerySubrelatorioEditavel(query, responsavel));
                    }
                }

                if (parameters == null) {
                    parameters = new HashMap<>();
                }
                _report.setParameters(parameters);
                reportName = _report.getReportName();

                // Diretório onde se encontram os arquivos de layout do .jasper
                final String templatePath = diretorioRaiz + JASPER_DIRECTORY;

                // Define o processo de virtualização na geração do relatório
                // impedindo o uso excessivo de memória
                final JRFileVirtualizer virtualizer = new JRFileVirtualizer(PAGE_CACHE, templatePath);
                parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

                // Recupera factory que controla a conexão do banco enquanto o
                // relatório é gerado
                factory = DAOFactory.getDAOFactory().getReportDAO();

                // Define onde o arquivo será gravado
                if (parameters.containsKey(ReportManager.REPORT_DIR_EXPORT)) {
                    reportPath = parameters.get(ReportManager.REPORT_DIR_EXPORT).toString();
                } else {
                    reportPath = _report.getPath();
                }

                // Recupera o Data Source que será utilizado no preenchimento do relatório
                JRDataSource dataSource = null;
                if (myDataSource != null) {
                    dataSource = myDataSource;
                } else if (conteudo != null) {
                    session = SessionUtil.getSession();
                    dataSource = _report.initListReport(session, criterio, conteudo);
                } else if (!_report.hasHQuery()) {
                    dataSource = _report.initReport(factory, criterio);
                } else {
                    session = SessionUtil.getSession();
                    dataSource = _report.initHReport(session, criterio);
                }

                /*
                 * Verifica se o relatório está compilado (Template).
                 * Caso contrário o mesmo é criado usando o arquivo .jrxml com mesmo nome
                 */
                compiledReport = null;
                String pathJrxml = null;
                final String layout = template.replaceAll(".jasper", ".jrxml");
                compiledReport = new File(templatePath, template);
                File layoutReport = new File(templatePath + layout);
                if (relPossuiTemplateCustomizado) {
                    pathJrxml = arquivoJasperTemplate.getAbsolutePath();
                } else {
                    pathJrxml = PATH_JRXML + layout;
                }
                long layoutReportLastModified = layoutReport.lastModified();

                if (!layoutReport.exists()) {
                    if (relPossuiTemplateCustomizado) {
                        layoutReport = new File(pathJrxml);
                    } else {
                        final Resource jrxmlResource = new ClassPathResource(pathJrxml);
                        final URL jarURL = jrxmlResource.getURL();
                        layoutReport = new File(jarURL.getFile());

                        final URLConnection jarCon = jarURL.openConnection();
                        layoutReportLastModified = jarCon.getLastModified();

                    }
                }

                InputStream layoutReportStream = null;
                try {
                    layoutReportStream = new FileInputStream(layoutReport);
                } catch (final FileNotFoundException ex) {
                    layoutReportStream = getClass().getResourceAsStream(pathJrxml);
                }

                final List<JRField> fields = (List<JRField>) parameters.get(KEY_NAME_FIELDS);
                final List<ReportColumn> columns = (List<ReportColumn>) parameters.get(KEY_NAME_COLUMNS);
                final boolean columnsExpandWidth = (parameters.get(KEY_NAME_COLUMNS_EXPAND_WIDTH) == null) || ((Boolean) parameters.get(KEY_NAME_COLUMNS_EXPAND_WIDTH)).booleanValue();

                if(!compiledReport.isFile() || (layoutReportLastModified > compiledReport.lastModified()) ||
                        (fields != null) || (columns != null)) {
                    // Remove a versão antiga para garantir a compilação do novo.
                    compiledReport.delete();

                    final JasperDesign jasperDesign = JRXmlLoader.load(layoutReportStream);

                    if (CodedValues.TEMPLATE_REL_EDITAVEL_JASPER.equals(template)) {
                        final JRBand[] bands = jasperDesign.getAllBands();
                        if (bands != null) {
                            for (final JRBand band : bands) {
                                band.setSplitType(SplitTypeEnum.PREVENT);
                            }
                        }
                    }

                    // Adiciona os campos adicionais informados como parametro.
                    if ((fields != null) && (fields.size() > 0)) {
                        final Iterator<JRField> it = fields.iterator();
                        while (it.hasNext()) {
                            jasperDesign.addField(it.next());
                        }
                    }
                    // Adiciona os campos adicionais informados como parametro.
                    if ((columns != null) && (columns.size() > 0)) {
                        int columnsWidth = 0;
                        int columnsX = Integer.MAX_VALUE;
                        for (final ReportColumn column : columns) {
                            columnsWidth += column.getWidth();
                            if (column.getX() < columnsX) {
                                columnsX = column.getX();
                            }

                            // Field de mapeamento do database.
                            jasperDesign.addField(createField(column.getFieldName(), column.getFieldClass()));
                            // Elementos gráficos
                            addDesignColumn(jasperDesign, column);
                        }

                        if (columnsExpandWidth) {
                            final JRElement fundoLinha = ((JRElementGroup) jasperDesign.getDetailSection()).getElementByKey("fundoLinha");
                            if ((columnsX + columnsWidth) > (fundoLinha.getX() + fundoLinha.getWidth())) {
                                final int diff = (columnsX + columnsWidth) - (fundoLinha.getX() + fundoLinha.getWidth());
                                jasperDesign.setPageWidth(jasperDesign.getPageWidth() + diff);
                                fundoLinha.setWidth(fundoLinha.getWidth() + diff);
                            }
                        }
                        JasperCompileManager.verifyDesign(jasperDesign);

                        // Gera um compilado especifico para este relatorio e o apago ao final.
                        deleteCompiled = true;
                        template = reportName + ".jasper";
                        compiledReport = new File(templatePath, template);
                    }
                    LOG.info("Compilando Relatório: " + reportName );
                    JasperCompileManager.compileReportToFile(jasperDesign, compiledReport.getAbsolutePath());
                    layoutReportStream.close();
                }

                // Compilar subreports
                if(relPossuiTemplateCustomizado) {
                   compilaSubReportEditavel(relatorio.getTipo(), templatePath, responsavel);
                }else {
                    for (final String subreport : relatorio.getArraySubreport()) {
                        LOG.info("Compilando Relatório: " + subreport);
                        compilaSubReport(subreport, templatePath);
                    }
                }

                // Preenche o relatório utilizando o Datasource repassado e
                // cria o arquivo PDF ou TXT de acordo com a opção do usuário.
                switch (formato) {
                    case "PDF":
                        reportName = exportToPDF(parameters, templatePath, reportName, reportPath, template, dataSource);
                        break;
                    case "DOC":
                        reportName = exportToDOC(parameters, templatePath, reportName, reportPath, template, dataSource);
                        break;
                    case "XLS":
                        reportName = exportToXLS(parameters, templatePath, reportName, reportPath, template, dataSource);
                        break;
                    case "XLSX":
                        reportName = exportToXLSX(parameters, templatePath, reportName, reportPath, template, dataSource);
                        break;
                    case "XML":
                        reportName = exportToXML(parameters, templatePath, reportName, reportPath, template, dataSource, responsavel);
                        break;
                    case "TEXT", "CSV":
                        reportName = exportToTXT(parameters, templatePath, reportName, reportPath, template, dataSource, formato, responsavel);
                        break;
                    case "ODT":
                        reportName = exportToODT(parameters, templatePath, reportName, reportPath, template, dataSource);
                        break;
                    case "ODS":
                        reportName = exportToODS(parameters, templatePath, reportName, reportPath, template, dataSource);
                        break;
                    default:
                        break;
                }
                LOG.info("Relatório gerado: " + reportPath + File.separatorChar + reportName);

                if (CodedValues.TPC_SIM.equals(relatorio.getCustomizado())) {
                    deleteCompiled = true;
                }
            }

            reportPathName = reportPath + File.separatorChar + reportName;

        } catch (final JRException ex) {
            LOG.error("Erro ao criar relatório " + reportName, ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel);
        } catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel);
        } finally {
            if (factory != null) {
                // Executa os processos que devem ser feitos após a execução da SQL
                _report.postSqlProcess(factory.getConnection());

                // Finaliza a conexão com o banco de dados
                factory.closeConnection();
            }
            SessionUtil.closeSession(session);
            if (deleteCompiled && (compiledReport != null)) {
                compiledReport.delete();
                excluirSubrelatorio(relatorio.getTipo(), responsavel);
            }
        }

        return reportPathName;

    }

    private void compilaSubReport(String subreport, String templatePath) throws IOException, JRException {
        //        File layoutCompiled = new File(templatePath, subreport);
        final String layoutName = subreport.replaceAll(".jasper", ".jrxml");
        final String pathLayout = PATH_JRXML + layoutName;
        File layoutSubReport = new File(templatePath + layoutName);
        if (!layoutSubReport.exists()) {
            final Resource jrxmlResource = new ClassPathResource(pathLayout);
            final URL jarURL = jrxmlResource.getURL();

            layoutSubReport = new File(jarURL.getFile());
        }
        InputStream layoutSubReportStream = null;
        BufferedReader buffer = null;
        try {
            try {
                layoutSubReportStream = new FileInputStream(layoutSubReport);
                buffer = new BufferedReader(new FileReader(layoutSubReport));
                LOG.trace("Lendo arquivo!");
                String string = "";
                while((string = buffer.readLine()) != null) {
                    LOG.trace(string);
                }
                LOG.trace("Terminando leitura do arquivo!");

            } catch (final FileNotFoundException ex) {
                layoutSubReportStream = getClass().getResourceAsStream(pathLayout);
            }

            final JasperDesign jasperDesign = JRXmlLoader.load(layoutSubReportStream);
            final File layoutCompiled = new File(templatePath, subreport);
            JasperCompileManager.compileReportToFile(jasperDesign, layoutCompiled.getAbsolutePath());
        } finally {
            if (layoutSubReportStream != null) {
                layoutSubReportStream.close();
            }
            if (buffer != null) {
                buffer.close();
            }
        }
    }

    private List<TransferObject> processaQuerySubrelatorioEditavel(String query, AcessoSistema responsavel) throws DAOException{
        final RelatorioDAO relDao = DAOFactory.getDAOFactory().getRelatorioDAO();
        return relDao.executarQuerySubrelatorio(query, responsavel);
    }

    private void excluirSubrelatorio(String relCodigo, AcessoSistema responsavel) throws FindException {
        final SubrelatorioController subrelatorioController = ApplicationContextProvider.getApplicationContext().getBean(SubrelatorioController.class);
        final Collection<Subrelatorio> subrelatorios = subrelatorioController.lstSubrelatorio(relCodigo);
        final String diretorioRaiz = ParamSist.getDiretorioRaizArquivos();
        final String pathSubrelatorioCompilado = diretorioRaiz + JASPER_DIRECTORY;

        for(final Subrelatorio subrelatorio : subrelatorios) {
            final String nomeSubrelatorioCompilado = subrelatorio.getSreTemplateJasper().replaceAll(".jrxml", ".jasper");
            final File subrelatorioCompilado = new File(pathSubrelatorioCompilado, nomeSubrelatorioCompilado);
            subrelatorioCompilado.delete();
        }
    }


    private void compilaSubReportEditavel(String relCodigo, String templatePath, AcessoSistema responsavel) throws IOException, JRException, FindException {
        final SubrelatorioController subrelatorioController = ApplicationContextProvider.getApplicationContext().getBean(SubrelatorioController.class);
        final Collection<Subrelatorio> subrelatorios =  subrelatorioController.lstSubrelatorio(relCodigo);
        for (final Subrelatorio subreport : subrelatorios) {
            final String nomeRelCustomizadoCompilado = subreport.getSreTemplateJasper().replaceAll(".jrxml", ".jasper");
            LOG.info("Compilando sub-relatório editável: " + nomeRelCustomizadoCompilado);
            final String diretorioRaiz = ParamSist.getDiretorioRaizArquivos();
            final String pathRelCustomizado = diretorioRaiz + JASPER_TEMPLATE_DIRECTORY + subreport.getSreTemplateJasper().replaceAll(".jasper", ".jrxml");
            final File relCustomizadoNaoCompilado = new File(pathRelCustomizado);
            InputStream relCompiladoStream = null;
            BufferedReader buffer = null;
            try {
                try {
                    relCompiladoStream = new FileInputStream(relCustomizadoNaoCompilado);
                    buffer = new BufferedReader(new FileReader(relCustomizadoNaoCompilado));
                    LOG.trace("Lendo arquivo!");
                    String string = "";
                    while ((string = buffer.readLine()) != null) {
                        LOG.trace(string);
                    }
                    LOG.trace("Terminando leitura do arquivo!");
                } catch (final FileNotFoundException ex) {
                    relCompiladoStream = getClass().getResourceAsStream(relCustomizadoNaoCompilado.getAbsolutePath());
                }

                final JasperDesign jasperDesign = JRXmlLoader.load(relCompiladoStream);
                final File layoutCompiled = new File(templatePath, nomeRelCustomizadoCompilado);
                JasperCompileManager.compileReportToFile(jasperDesign, layoutCompiled.getAbsolutePath());
            } finally {
                if (relCompiladoStream != null) {
                    relCompiladoStream.close();
                }
                if (buffer != null) {
                    buffer.close();
                }
            }
        }
    }

    private String retornaLayoutRelatorioEntidade(Relatorio relatorio, String diretorioRaiz, AcessoSistema responsavel) throws JRException, IOException {
        final String template = relatorio.getJasperTemplate();
        final String templateDinamico = relatorio.getModeloDinamico();

        // Verifica se o template do relatório é customizado
        final String jasperTemplate = diretorioRaiz + JASPER_TEMPLATE_DIRECTORY + relatorio.getJasperTemplate().replaceAll(".jasper", ".jrxml");
        final File arquivoJasperTemplate = new File(jasperTemplate);
        final boolean relPossuiTemplateCustomizado = CodedValues.TPC_SIM.equals(relatorio.getCustomizado()) && ((arquivoJasperTemplate != null) && arquivoJasperTemplate.isFile() && arquivoJasperTemplate.exists());

        if (relPossuiTemplateCustomizado) {
            return arquivoJasperTemplate.getName().replaceAll(".jrxml", ".jasper");
        } else if (TextHelper.isNull(templateDinamico)) {
            String nomeRelatorio = template.replaceAll(".jasper", "");
            if (responsavel.isCsa()) {
                nomeRelatorio = nomeRelatorio + "_csa";
            } else if (responsavel.isCor()) {
                nomeRelatorio = nomeRelatorio + "_cor";
            } else if (responsavel.isOrg()) {
                nomeRelatorio = nomeRelatorio + "_org";
            }
            nomeRelatorio += ".jrxml";
            String pathJrxml = PATH_JRXML + nomeRelatorio;

            Resource jrxmlResource = new ClassPathResource(pathJrxml);
            if (!jrxmlResource.exists()) {
                nomeRelatorio = template.replaceAll(".jasper", ".jrxml");
                pathJrxml = PATH_JRXML + nomeRelatorio;
                jrxmlResource = new ClassPathResource(pathJrxml);

                if (!jrxmlResource.exists()) {
                    throw new JRException(ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.nenhum.layout.configurado", responsavel));
                }
            }

            // Retorna o nome do arquivo ".jasper" para verificar se o modelo já existe compilado
            return nomeRelatorio.replaceAll(".jrxml", ".jasper");

        } else {
            // Template dinâmico não tem personalização pelo tipo do usuário.
            // Retorna o nome do template normal, pois o dinâmico é a base para sua geração.
            return template;
        }
    }

    private String exportToTXT(Map<String, Object> parameters, String templatePath, String reportName,
            String reportPath, String template, JRDataSource dataSource, String formato, AcessoSistema responsavel) throws JRException {

        String tpcCodigoDelimitador = null;

        if ("CSV".equals(formato)) {
            reportName += ".csv";
            tpcCodigoDelimitador = CodedValues.TPC_DELIMITADOR_CAMPOS_RELATORIO_CSV;
        } else {
            reportName += ".txt";
            tpcCodigoDelimitador = CodedValues.TPC_DELIMITADOR_CAMPOS_RELATORIO_TXT;
        }

        String delimitador = (String) ParamSist.getInstance().getParam(tpcCodigoDelimitador, responsavel);
        if (TextHelper.isNull(delimitador)) {
            delimitador = ";";
        }
        parameters.put(PARAM_NAME_DELIMITADOR_CAMPOS, delimitador);

        // A paginação deve ser ignorada nos relatórios TXT. Caso contrário pode acontecer de ocorrer quebra
        // de linha em um campo fazendo com que o conteúdo seja mesclado com a linha seguinte.
        parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);

        final JasperPrint print = JasperFillManager.fillReport(templatePath + template, parameters, dataSource);

        final SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
        configuration.setFieldDelimiter(delimitador);
        configuration.setRecordDelimiter(System.lineSeparator());

        final CsvExporter exporter = new CsvExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleWriterExporterOutput(reportPath + File.separatorChar + reportName, "iso-8859-1"));
        exporter.setConfiguration(configuration);
        exporter.exportReport();

        return reportName;
    }

    private String exportToPDF(Map<String, Object> parameters, String templatePath, String reportName,
            String reportPath, String template, JRDataSource dataSource) throws JRException {

        reportName += ".pdf";

        JasperRunManager.runReportToPdfFile(templatePath + template, reportPath + File.separatorChar + reportName, parameters, dataSource);

        return reportName;
    }

    private String exportToDOC(Map<String, Object> parameters, String templatePath, String reportName, String reportPath, String template, JRDataSource dataSource) throws JRException {
        reportName += ".doc";

        final JasperPrint print = JasperFillManager.fillReport(templatePath + template, parameters, dataSource);

        final SimpleDocxReportConfiguration configuration = new SimpleDocxReportConfiguration();
        configuration.setFlexibleRowHeight(true);

        final JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reportPath + File.separatorChar + reportName));

        exporter.setConfiguration(configuration);
        exporter.exportReport();
        return reportName;
    }

    private String exportToXLS(Map<String, Object> parameters, String templatePath, String reportName, String reportPath, String template, JRDataSource dataSource) throws JRException {
        reportName += ".xls";

        // A paginação deve ser ignorada nos relatórios XLS. Caso contrário pode acontecer de ocorrer quebra
        // de linha em um campo fazendo com que o conteúdo seja mesclado com a linha seguinte.
        parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);

        final JasperPrint print = JasperFillManager.fillReport(templatePath + template, parameters, dataSource);

        final SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setWhitePageBackground(false);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setMaxRowsPerSheet(65535);

        final JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reportPath + File.separatorChar + reportName));
        exporter.setConfiguration(configuration);
        exporter.exportReport();
        return reportName;
    }

    private String exportToXLSX(Map<String, Object> parameters, String templatePath, String reportName, String reportPath, String template, JRDataSource dataSource) throws JRException {
        reportName += ".xlsx";

        // A paginação deve ser ignorada nos relatórios XLS. Caso contrário pode acontecer de ocorrer quebra
        // de linha em um campo fazendo com que o conteúdo seja mesclado com a linha seguinte.
        parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);

        final JasperPrint print = JasperFillManager.fillReport(templatePath + template, parameters, dataSource);

        final SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setWhitePageBackground(false);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setMaxRowsPerSheet(65535);

        final JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reportPath + File.separatorChar + reportName));
        exporter.setConfiguration(configuration);
        exporter.exportReport();
        return reportName;
    }

    private String exportToXML(Map<String, Object> parameters, String templatePath, String reportName, String reportPath, String template, JRDataSource dataSource, AcessoSistema responsavel) throws JRException {
        // Altera formato para gerar relatório CSV, sem quebra de linhas
        final String formatoCSV = "CSV";
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, formatoCSV);
        final String csvFileName = exportToTXT(parameters, templatePath, reportName, reportPath, template, dataSource, formatoCSV, responsavel);

        String delimitador = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DELIMITADOR_CAMPOS_RELATORIO_CSV, responsavel);
        if (TextHelper.isNull(delimitador)) {
            delimitador = ";";
        }

        reportName += ".xml";
        final XMLExporter xmlExporter = new XMLExporter(responsavel);
        xmlExporter.convertFile(reportPath + File.separatorChar + csvFileName, reportPath + File.separatorChar + reportName, delimitador);

        // Remove csv
        try {
            FileHelper.delete(reportPath + File.separatorChar + csvFileName);
        } catch (final IOException ex) {
            LOG.error("Erro ao excluir relatório temporário " + csvFileName, ex);
        }

        return reportName;
    }

    public void exportSimplePDF(String templateJrxml, String reportName, Map<String, Object> parameters, AcessoSistema responsavel) {
        try {
            /*
             * A atomização dessa parte do código é necessária para que somente
             * um único relatório seja executado por vez. Não gerando conflitos
             * entre os arquivos de virtualização, facilitando o gerenciamento
             * e não sobrecarregando o servidor. Como desvantagem, todos os
             * relatórios subseqüentes entrarão na fila de execução não sendo
             * executados ao mesmo tempo.
             */
            synchronized (this) {
                if (parameters == null) {
                    parameters = new HashMap<>();
                }

                // Recupera o path do sistema de acordo com o parametro
                // e acrescenta o diretório jasper
                final String diretorioRaiz = ParamSist.getDiretorioRaizArquivos();
                // Diretório onde se encontram os arquivos de layout do .jasper
                final String templatePath = diretorioRaiz + JASPER_DIRECTORY;

                // Obtém a data de modificação do modelo JRXML presente no Jar da aplicação
                final Resource jrxmlResource = new ClassPathResource(templateJrxml);
                final URL jarURL = jrxmlResource.getURL();
                final URLConnection jarCon = jarURL.openConnection();
                final long layoutReportLastModified = jarCon.getLastModified();

                // Caminho do modelo do relatório quando compilado: fica disponível no diretório de arquivos do sistema
                final String template = templatePath + templateJrxml.substring(templateJrxml.lastIndexOf('/') + 1, templateJrxml.indexOf(".jrxml")) + ".jasper";

                // Verifica se o relatório compilado existe e se a data deste é maior que a data do modelo presente no Jar
                final File compiledReport = new File(template);
                if (!compiledReport.exists() || !compiledReport.canRead() || (layoutReportLastModified > compiledReport.lastModified())) {
                    // Se deve usar o modelo presente no Jar, extrai do Jar para a compilação
                    final InputStream layoutReportStream = getClass().getResourceAsStream(templateJrxml);

                    // Remove a versão antiga para garantir a compilação do novo.
                    if (compiledReport.exists()) {
                        compiledReport.delete();
                    }

                    final JasperDesign jasperDesign = JRXmlLoader.load(layoutReportStream);

                    LOG.info("Compilando Relatório: " + compiledReport.getAbsolutePath());
                    JasperCompileManager.compileReportToFile(jasperDesign, compiledReport.getAbsolutePath());
                    layoutReportStream.close();
                }

                JasperRunManager.runReportToPdfFile(compiledReport.getAbsolutePath(), reportName, parameters, new JREmptyDataSource());
                LOG.info("Relatório gerado: " + reportName);
            }
        } catch (final JRException ex) {
            LOG.error("Erro ao criar relatório " + reportName, ex);
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public String exportToODT(Map<String, Object> parameters, String templatePath, String reportName, String reportPath, String template, JRDataSource dataSource) throws JRException {
        reportName += ".odt";
        parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);

        final JasperPrint print = JasperFillManager.fillReport(templatePath + template, parameters, dataSource);
        final SimpleOdtReportConfiguration configuration = new SimpleOdtReportConfiguration();
        configuration.setFlexibleRowHeight(true);

        final JROdtExporter exporter = new JROdtExporter();

        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reportPath + File.separator + reportName));
        exporter.setConfiguration(configuration);
        exporter.exportReport();

        return reportName;
    }

    public String exportToODS(Map<String, Object> parameters, String templatePath, String reportName, String reportPath, String template, JRDataSource dataSource) throws JRException {
        reportName += ".ods";
        parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);

        final JasperPrint print = JasperFillManager.fillReport(templatePath + template, parameters, dataSource);
        final JROdsExporter exporter = new JROdsExporter();

        final SimpleOdsReportConfiguration configuration = new SimpleOdsReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setWhitePageBackground(false);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setMaxRowsPerSheet(65535);

        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reportPath + File.separator + reportName));
        exporter.setConfiguration(configuration);
        exporter.exportReport();

        return reportName;
    }
}
