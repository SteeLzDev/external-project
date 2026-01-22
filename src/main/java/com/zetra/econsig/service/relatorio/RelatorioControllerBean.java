package com.zetra.econsig.service.relatorio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.LimiteTaxaJurosControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.MenuControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.folha.retorno.GeradorRelatorioRepasse;
import com.zetra.econsig.helper.Pair;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.periodo.RepasseHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.xml.XmlHelper;
import com.zetra.econsig.job.jobs.RelatorioEditavelJob;
import com.zetra.econsig.job.process.ProcessaRelatorioEditavel;
import com.zetra.econsig.job.process.ProcessaRelatorioIntegracaoConsignataria;
import com.zetra.econsig.job.process.ProcessaRelatorioIntegracaoMapeamentoMultiplo;
import com.zetra.econsig.job.process.ProcessaRelatorioRegrasConvenio;
import com.zetra.econsig.job.process.integracao.ProcessaEmailRelatorioIntegracao;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.LeitorBaseDeDados;
import com.zetra.econsig.parser.LeitorList;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.parser.config.DocumentoTipo;
import com.zetra.econsig.parser.config.MapeamentoTipo;
import com.zetra.econsig.parser.config.ParametroTipo;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.RelatorioDAO;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AcessoRecurso;
import com.zetra.econsig.persistence.entity.AcessoRecursoHome;
import com.zetra.econsig.persistence.entity.Estabelecimento;
import com.zetra.econsig.persistence.entity.EstabelecimentoHome;
import com.zetra.econsig.persistence.entity.Funcao;
import com.zetra.econsig.persistence.entity.FuncaoHome;
import com.zetra.econsig.persistence.entity.ItemMenu;
import com.zetra.econsig.persistence.entity.ItemMenuHome;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.PapelFuncao;
import com.zetra.econsig.persistence.entity.PapelFuncaoHome;
import com.zetra.econsig.persistence.entity.Relatorio;
import com.zetra.econsig.persistence.entity.RelatorioFiltro;
import com.zetra.econsig.persistence.entity.RelatorioFiltroHome;
import com.zetra.econsig.persistence.entity.RelatorioHome;
import com.zetra.econsig.persistence.entity.TipoAgendamento;
import com.zetra.econsig.persistence.entity.TipoAgendamentoHome;
import com.zetra.econsig.persistence.entity.TipoFiltroRelatorio;
import com.zetra.econsig.persistence.entity.TipoFiltroRelatorioHome;
import com.zetra.econsig.persistence.query.beneficios.beneficiario.ListaTipoArquivoByTarCodigoQuery;
import com.zetra.econsig.persistence.query.menu.ObtemProxItmSequenciaQuery;
import com.zetra.econsig.persistence.query.relatorio.ListaRelatorioQuery;
import com.zetra.econsig.persistence.query.relatorio.ListaRelatorioTipoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioAvaliacaoFaqAnaliticoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioAvaliacaoFaqSinteticoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioComprometimentoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioConfCadMargemQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioEstatisticoProcessamentoPeriodosQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioEstatisticoProcessamentoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialCsaQtdeContratosPorCategoriaQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialCsaQtdeContratosPorCsaQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialCsaQtdeContratosPorSvcQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialEstatiscoMargemPorNaturezaSvcQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialEstatiscoMargemQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialGeralBuscaSvcTaxasQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialGeralListaCsaSituacaoObsQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialGeralPrazoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialGeralTaxasQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialInadimplenciaCsaQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialInadimplenciaEvolucaoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialInadimplenciaQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeContratosPorCategoriaQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeContratosPorCrsQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeContratosPorCsaQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeContratosPorSvcQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeCorPorCsaQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeSerPorFaixaMargemQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeServidorPorCrsQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeServidorPorEstQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeServidorPorOrgQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeServidorPorTipoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioInadimplenciaOrgaoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioInadimplenciaStatusRegistroServidorQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioInadimplenciaTipoOcorrenciaQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioInadimplenciaTotalCarteiraQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioInclusoesPorCsaQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioPercentualCarteiraQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioPercentualRejeitoTotalQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioPrdPagasPorCsaPeriodoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioRegrasConvenioListaConveniosQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioRegrasConvenioListaServicosQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSaldoDevedorPorCsaPeriodoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioServicoOperacaoMesQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoDecisaoJudicialQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoDescontosQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaBloqueiosQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaCargosBloqueadosQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaConciliacaoOrgQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaDadosServidoresQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaInadimplenciaUltMovFinQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaIndicadorInsucessoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaMediaCetCsaQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaMedioParcelasQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaQtdeParcelasQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaQuantidadeCargosBloqueadosQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetSvcQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaUltimoMovimentoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolPortabilidadeQuantidadeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolumeAverbacaoApiGraficoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolumeAverbacaoApiQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolumeAverbacaoGraficoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolumeAverbacaoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolumeFinanceiroQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolumePortabilidadeGraficoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolumePortabilidadeTipoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoMovFinQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioTaxasEfetivasContratosQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioTaxasEfetivasQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioTermoAdesaoNaoAutorizadoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioTermoUsoPrivacidadeAdesaoAutorizadoQuery;
import com.zetra.econsig.persistence.query.retorno.ObtemConsignatariaParaRetornoQuery;
import com.zetra.econsig.persistence.query.retorno.ObtemOrgaoParaRetornoQuery;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.jasper.dto.AvaliacaoFaqBean;
import com.zetra.econsig.report.jasper.dto.ComprometimentoBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorCategoriaBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorCrsBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorCsaBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorSvcBean;
import com.zetra.econsig.report.jasper.dto.CorPorCsaBean;
import com.zetra.econsig.report.jasper.dto.EstatisticoProcessamentoBean;
import com.zetra.econsig.report.jasper.dto.GerencialEstatiscoMargemBean;
import com.zetra.econsig.report.jasper.dto.GerencialGeralTaxasEfetivasBean;
import com.zetra.econsig.report.jasper.dto.GerencialInadimplenciaBean;
import com.zetra.econsig.report.jasper.dto.GerencialInadimplenciaCsaBean;
import com.zetra.econsig.report.jasper.dto.GerencialInadimplenciaEvolucaoBean;
import com.zetra.econsig.report.jasper.dto.GerencialQtdeSerPorFaixaMargemBean;
import com.zetra.econsig.report.jasper.dto.GerencialTaxasBean;
import com.zetra.econsig.report.jasper.dto.InadimplenciaBean;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioMargensBean;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioOrgaoBean;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioParametrosBean;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioServicosBean;
import com.zetra.econsig.report.jasper.dto.RelatorioConfCadMargemBean;
import com.zetra.econsig.report.jasper.dto.ServicoOperacaoMesBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorCrsBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorEstBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorOrgBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorTipoBean;
import com.zetra.econsig.report.jasper.dto.SinteticoGerencialConsignatariaBean;
import com.zetra.econsig.report.jasper.dto.TermoUsoPrivacidadeAdesaoBean;
import com.zetra.econsig.report.reports.RelatorioEditavel;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.folha.ImpRetornoController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.juros.LimiteTaxaJurosController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.menu.MenuController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.regraconvenio.RegraConvenioController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;

import br.com.nostrum.simpletl.Translator;
import br.com.nostrum.simpletl.exception.InterpreterException;
import br.com.nostrum.simpletl.reader.DatabaseReader;
import br.com.nostrum.simpletl.writer.TextFileWriter;

/**
 * <p>Title: RelatorioControllerBean</p>
 * <p>Description: Processamento Relatorio de Integração</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class RelatorioControllerBean implements RelatorioController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioControllerBean.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private MenuController menuController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private ImpRetornoController impRetornoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ConsignanteController consignanteController;
    
    @Autowired
    private ServidorController servidorController;
    
    @Autowired
    private MargemController margemController;
    
    @Autowired
    private LimiteTaxaJurosController limiteTaxaJurosController;

    @Autowired
    private RegraConvenioController regraConvenioController;
    
    @Autowired
    private ConsignatariaController consignatariaController;

    /** **************************** INÍCIO RELATÓRIO DE INTEGRAÇÃO **************************** **/

    @Override
    public void geraRelatorioIntegracao(String estCodigo, String orgCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.SISTEMA, Log.GERAR_RELATORIO, Log.LOG_INFORMACAO);
            log.setEstabelecimento(estCodigo);
            log.setOrgao(orgCodigo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.geracao.relatorio.integracao", responsavel));
            log.write();

            final Map<String, String> confRetorno = impRetornoController.buscaArquivosConfiguracao(null, "retorno", estCodigo, orgCodigo, responsavel);
            final Map<String, String> confAtrasado = impRetornoController.buscaArquivosConfiguracao(null, "atrasado", estCodigo, orgCodigo, responsavel);
            Map<String, String> confCritica = null;

            try {
                // Importação de crítica não é obrigatório, portanto trata a exceção caso os XMLs de configuração não existam
                confCritica = impRetornoController.buscaArquivosConfiguracao(null, "critica", estCodigo, orgCodigo, responsavel);
            } catch (final ImpRetornoControllerException ex) {
                LOG.debug("Arquivos de configuração de entrada/tradutor de crítica não existem ou não podem ser lidos. Provavelmente o sistema não possui processamento de crítica.");
            }

            // Arquivos XML de configuração do leiaute de retorno
            final String confEntradaImpRet = confRetorno.get("entradaImpRetorno");
            final String confTradutorImpRet = confRetorno.get("tradutorImpRetorno");

            // Arquivos XML de configuração do leiaute de crítica
            final String confEntradaCritica = (confCritica != null ? confCritica.get("entradaImpRetorno") : null);
            final String confTradutorCritica = (confCritica != null ? confCritica.get("tradutorImpRetorno") : null);

            // Diretório onde se encontra o arquivo de retorno, retorno atrasado, e crítica
            final String pathRetorno = confRetorno.get("pathEntrada");
            final String pathAtrasado = confAtrasado.get("pathEntrada");
            final String pathCritica = (confCritica != null ? confCritica.get("pathEntrada") : null);

            // Verifica se os diretórios existem
            final File diretorioRetorno = new File(pathRetorno);
            if (!diretorioRetorno.exists() && !diretorioRetorno.mkdirs()) {
                throw new RelatorioControllerException("mensagem.erro.interno.diretorio.arquivo.retorno.nao.existe.e.nao.pode.ser.criado", responsavel);
            }

            // Verifica se os arquivo de configuração da entrada/tradutor da importação de retorno existem
            final File filXMLEntradaRetorno = new File(confEntradaImpRet);
            final File filXMLTradutorRetorno = new File(confTradutorImpRet);
            if (!filXMLEntradaRetorno.exists() || !filXMLEntradaRetorno.canRead() || !filXMLTradutorRetorno.exists() || !filXMLTradutorRetorno.canRead()) {
                throw new RelatorioControllerException("mensagem.erro.interno.arquivos.configuracao.entrada.tradutor.nao.existem.ou.nao.podem.ser.lido", responsavel);
            }

            // Cria filtro para seleção de arquivos .txt e .zip
            final FileFilter filtro = arq -> {
                final String arqNome = arq.getName().toLowerCase();
                return (arqNome.endsWith(".txt.prc") || arqNome.endsWith(".zip.prc"));
            };

            // Lista os arquivos de retorno de acordo com o filtro acima
            List<File> arquivosRetorno = null;
            File[] temp = diretorioRetorno.listFiles(filtro);
            if ((temp != null) && (temp.length > 0)) {
                arquivosRetorno = new ArrayList<>(Arrays.asList(temp));
            }

            // Lista arquivos de crítica, caso o diretório exista
            List<File> arquivosCritica = null;
            if (!TextHelper.isNull(pathCritica)) {
                final File diretorioCritica = new File(pathCritica);
                if (diretorioCritica.exists()) {
                    temp = diretorioCritica.listFiles(filtro);
                    if ((temp != null) && (temp.length > 0)) {
                        arquivosCritica = new ArrayList<>(Arrays.asList(temp));
                    }
                }
            }

            // Lista arquivos de retorno atrasado, caso o diretório exista, e inclui
            // na lista de arquivos de retorno, já que são o mesmo leiaute
            final File diretorioAtrasado = new File(pathAtrasado);
            if (diretorioAtrasado.exists()) {
                temp = diretorioAtrasado.listFiles(filtro);
                if ((temp != null) && (temp.length > 0)) {
                    if (arquivosRetorno == null) {
                        arquivosRetorno = new ArrayList<>();
                    }
                    arquivosRetorno.addAll(Arrays.asList(temp));
                }
            }

            // Imprime a localização dos arquivos que serão traduzidos
            if ((arquivosRetorno != null) && (arquivosRetorno.size() > 0)) {
                for (int i = 0; i < arquivosRetorno.size(); i++) {
                    LOG.debug("Arquivo retorno " + (i + 1) + " = " + arquivosRetorno.get(i));
                }
                LOG.debug("XML Entrada  = " + confEntradaImpRet);
                LOG.debug("XML Tradutor = " + confTradutorImpRet);
            }

            if ((arquivosCritica != null) && (arquivosCritica.size() > 0)) {
                for (int i = 0; i < arquivosCritica.size(); i++) {
                    LOG.debug("Arquivo crítica " + (i + 1) + " = " + arquivosCritica.get(i));
                }
                LOG.debug("XML Entrada  = " + confEntradaCritica);
                LOG.debug("XML Tradutor = " + confTradutorCritica);
            }

            // Atualiza o csa_codigo da tabela de arquivo retorno para utilização no relatório de integração customizado
            impRetornoController.atualizarCsaCodigoTbArqRetorno(responsavel);

            // Linhas sem mapeamento/processamento
            final StringBuilder spRetorno = new StringBuilder();
            final StringBuilder spCritica = new StringBuilder();

            // Linhas sem mapeamento/processamento traduzido
            final List<Map<String, Object>> lstSpRetorno = new ArrayList<>();
            final List<Map<String, Object>> lstSpCritica = new ArrayList<>();

            // Le os arquivos de retorno e de crítica
            final HashMap<String, HashMap<String, HashMap<String, List<Map<String, Object>>>>> saidaTraduzida = new HashMap<>();
            final Map<String, StringBuilder> saidaCor = new HashMap<>();
            final Map<String, Map<String, StringBuilder>> saidaOrg = new HashMap<>();
            final HashMap<String, Map<String, StringBuilder>> saidaRetorno = traduzArquivosIntegracao(arquivosRetorno, confEntradaImpRet, confTradutorImpRet, saidaTraduzida, spRetorno, lstSpRetorno, saidaCor, saidaOrg, estCodigo, orgCodigo, responsavel);
            final HashMap<String, Map<String, StringBuilder>> saidaCritica = traduzArquivosIntegracao(arquivosCritica, confEntradaCritica, confTradutorCritica, null, spCritica, lstSpCritica, null, null, estCodigo, orgCodigo, responsavel);

            // Diretório Raiz eConsig e dos arquivos de configuração
            final String absolutePath = ParamSist.getDiretorioRaizArquivos();

            // Determina sufixo para nome dos arquivos de integração de CSA caso o processamento seja feito por ORG/EST
            String sufixoNomeRel = "";
            if (!TextHelper.isNull(orgCodigo)) {
                final Orgao org = OrgaoHome.findByPrimaryKey(orgCodigo);
                final Estabelecimento est = EstabelecimentoHome.findByPrimaryKey(org.getEstabelecimento().getEstCodigo());
                sufixoNomeRel = "_" + est.getEstIdentificador() + "_" + org.getOrgIdentificador();
            } else if (!TextHelper.isNull(estCodigo)) {
                final Estabelecimento est = EstabelecimentoHome.findByPrimaryKey(estCodigo);
                sufixoNomeRel = "_" + est.getEstIdentificador();
            }

            final Date periodoBase = periodoController.obtemUltimoPeriodoExportado((!TextHelper.isNull(orgCodigo) ? Arrays.asList(orgCodigo) : null), (!TextHelper.isNull(estCodigo) ? Arrays.asList(estCodigo) : null), true, null, responsavel);
            final String periodoFinal = DateHelper.toPeriodString(periodoBase).replace("/", "");

            // Grava os arquivos para as consignatárias
            LOG.debug("ARQUIVOS CSA: " + DateHelper.getSystemDatetime());
            String pathSaida = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "csa" + File.separatorChar + "integracao" + File.separatorChar;

            gravaArquivoIntegracao(saidaRetorno, saidaTraduzida, saidaCor, saidaOrg, pathSaida, "retorno", sufixoNomeRel, periodoFinal, confEntradaImpRet, confTradutorImpRet, responsavel);
            gravaArquivoIntegracao(saidaCritica, null, null, null, pathSaida, "critica", sufixoNomeRel, periodoFinal, confEntradaCritica, confTradutorCritica, responsavel);

            LOG.debug("FIM ARQUIVOS CSA: " + DateHelper.getSystemDatetime());

            LOG.debug("ARQUIVOS CSE: " + DateHelper.getSystemDatetime());
            // Grava arquivo contendo as parcelas não encontradas no sistema
            if ((spRetorno.length() > 0) || (spCritica.length() > 0)) {
                pathSaida = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "integracao" + File.separatorChar;
                final String nomeArqSaida = pathSaida + "mapeamento_multiplo_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".txt";

                final File dir = new File(pathSaida);
                if (!dir.exists() && !dir.mkdirs()) {
                    throw new RelatorioControllerException("mensagem.erro.criacao.diretorio", responsavel, dir.getAbsolutePath());
                }

                final PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaida)));
                if (spRetorno.length() > 0) {
                    arqSaida.println(ApplicationResourcesHelper.getMessage("rotulo.gera.relatorio.integracao.retorno.titulo", responsavel));
                    arqSaida.print(spRetorno);
                }
                if (spCritica.length() > 0) {
                    arqSaida.println(ApplicationResourcesHelper.getMessage("rotulo.gera.relatorio.integracao.critica.titulo", responsavel));
                    arqSaida.print(spCritica);
                }
                arqSaida.close();

                if (ParamSist.paramEquals(CodedValues.TPC_GERAR_RELATORIOS_INTEGRACAO_COMPACTADOS, CodedValues.TPC_SIM, responsavel)) {
                    // Compacta o relatório TXT e remove, deixando apenas o ZIP
                    FileHelper.zipAndRemove(nomeArqSaida);
                }

                // Gera relatório em XLS
                if (ParamSist.paramEquals(CodedValues.TPC_GERA_RELATORIO_INTEGRACAO_XLS, CodedValues.TPC_SIM, responsavel)) {
                    if ((lstSpRetorno != null) && !lstSpRetorno.isEmpty()) {
                        geraRelatorioIntegracaoMapeamentoMultiploXLS(lstSpRetorno, confEntradaImpRet, confTradutorImpRet, pathSaida, ApplicationResourcesHelper.getMessage("rotulo.gera.relatorio.integracao.retorno.titulo", responsavel), ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.integracao.mapeamento.multiplo.retorno", responsavel), responsavel);
                    }

                    if ((lstSpCritica != null) && !lstSpCritica.isEmpty()) {
                        geraRelatorioIntegracaoMapeamentoMultiploXLS(lstSpCritica, confEntradaCritica, confTradutorCritica, pathSaida, ApplicationResourcesHelper.getMessage("rotulo.gera.relatorio.integracao.critica.titulo", responsavel), ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.integracao.mapeamento.multiplo.critica", responsavel), responsavel);
                    }
                }
            }
            LOG.debug("FIM ARQUIVOS CSE: " + DateHelper.getSystemDatetime());

        } catch (final ZetraException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException(ex);
        } catch (final IOException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Traduz os arquivos de integração, utilizando os arquivos de configuração de entrada e tradutor,
     * retornando um mapa onde a chave é o código da consignatária e o valor as linhas desta consigntária.
     * @param arquivos
     * @param confEntrada
     * @param confTradutor
     * @param saidaTraduzida
     * @param erro
     * @param saidaCor
     * @param saidaOrg
     * @param estCodigo
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws ParserException
     * @throws RelatorioControllerException
     * @throws ConvenioControllerException
     * @throws ParametroControllerException
     */
    private HashMap<String, Map<String, StringBuilder>> traduzArquivosIntegracao(List<File> arquivos, String confEntrada, String confTradutor, HashMap<String, HashMap<String, HashMap<String, List<Map<String, Object>>>>> saidaTraduzida, StringBuilder erro, List<Map<String, Object>> lstErro, Map<String, StringBuilder> saidaCor, Map<String, Map<String, StringBuilder>> saidaOrg, String estCodigo, String orgCodigo, AcessoSistema responsavel) throws ParserException, RelatorioControllerException, ConvenioControllerException, ParametroControllerException {
        final HashMap<String, Map<String, StringBuilder>> saida = new HashMap<>();
        if ((arquivos != null) && !arquivos.isEmpty()) {
            final boolean temEst = !TextHelper.isNull(estCodigo);
            final boolean temOrg = !TextHelper.isNull(orgCodigo);

            final HashMap<String, Object> memoria = new HashMap<>();

            LeitorArquivoTexto leitor = null;
            Escritor escritor = null;
            Tradutor tradutor = null;

            String nomeArqEntrada = null;
            StringBuilder buffer = null;
            List<Map<String, Object>> bufferTraduzido = null;
            File arqEntrada = null;
            final HashMap<String, String> csaIdCodigo = new HashMap<>();
            final HashMap<String, String> orgIdCodigo = new HashMap<>();

            final Map<String, Map<String, String>> paramCsa = new HashMap<>();
            final Map<String, Map<String, List<String>>> cacheCnvCor = new HashMap<>();

            final boolean separaMapeamentoMultiplo = ParamSist.paramEquals(CodedValues.TPC_SEPARA_LINHAS_MAPEAMENTO_MULTIPLO, CodedValues.TPC_SIM, responsavel);
            final boolean gerarRelIntegracaoCsaParaOrg = ParamSist.paramEquals(CodedValues.TPC_GERAR_REL_INTEGRACAO_CSA_PARA_ORGAO, CodedValues.TPC_SIM, responsavel);

            final boolean utilizaVerbaRef = ParamSist.getBoolParamSist(CodedValues.TPC_UTILIZA_CNV_COD_VERBA_REF, responsavel);
            final boolean temProcessamentoFerias = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, responsavel);
            final Map<String, String> mapVerbaRef = convenioController.getMapCnvCodVerbaRef();
            final Map<String, String> mapVerbaFerias = convenioController.getMapCnvCodVerbaFerias();

            for (final File element : arquivos) {
                arqEntrada = element;
                nomeArqEntrada = arqEntrada.getPath();
                if (nomeArqEntrada.toLowerCase().endsWith(".txt.prc")) {
                    leitor = new LeitorArquivoTexto(confEntrada, nomeArqEntrada);
                } else {
                    leitor = new LeitorArquivoTextoZip(confEntrada, nomeArqEntrada);
                }
                escritor = new EscritorMemoria(memoria);
                tradutor = new Tradutor(confTradutor, leitor, escritor);

                LOG.debug("TRADUÇÃO: " + nomeArqEntrada + " --> " + DateHelper.getSystemDatetime());
                tradutor.iniciaTraducao();
                while (tradutor.traduzProximo()) {
                    // Dados da linha que podem ser utilizados para localização da entidade dona do contrato
                    String cnvCodVerba = (memoria.get("CNV_COD_VERBA") != null) ? memoria.get("CNV_COD_VERBA").toString() : null;
                    String cnvCodVerbaRef = null;
                    String cnvCodVerbaFerias = null;
                    final String cnvCodVerbaArquivo = cnvCodVerba;

                    /*
                     * Se tem processamento de férias e o valor passado é uma código de verba de férias,
                     * obtém o valor do código normal através do mapeamento de verbas
                     */
                    if (temProcessamentoFerias && (mapVerbaFerias != null) && (mapVerbaFerias.get(cnvCodVerba) != null)) {
                        cnvCodVerbaFerias = cnvCodVerba;
                        cnvCodVerba = null;

                        /*
                         * Se utiliza verba de referência e o valor passado é uma código de referência,
                         * obtém o valor do código normal através do mapeamento de verbas
                         */
                    } else if (utilizaVerbaRef && (mapVerbaRef != null) && (mapVerbaRef.get(cnvCodVerba) != null)) {
                        cnvCodVerbaRef = cnvCodVerba;
                        cnvCodVerba = null;
                    }

                    final String csaIdentificador = (memoria.get("CSA_IDENTIFICADOR") != null) ? memoria.get("CSA_IDENTIFICADOR").toString() : null;
                    final String svcIdentificador = (memoria.get("SVC_IDENTIFICADOR") != null) ? memoria.get("SVC_IDENTIFICADOR").toString() : null;
                    final String orgIdentificador = (memoria.get("ORG_IDENTIFICADOR") != null) ? memoria.get("ORG_IDENTIFICADOR").toString() : null;
                    final String estIdentificador = (memoria.get("EST_IDENTIFICADOR") != null) ? memoria.get("EST_IDENTIFICADOR").toString() : null;
                    final String chaveIdentificacao = csaIdentificador + ", " + svcIdentificador + ", " + orgIdentificador + ", " + estIdentificador + ", " + cnvCodVerbaArquivo;

                    // Verifica se existe no cache a identificação do convênio
                    if (!csaIdCodigo.containsKey(chaveIdentificacao)) {
                        final List<String> csaCodigos = obtemConsignataria(csaIdentificador, svcIdentificador, orgIdentificador, estIdentificador, cnvCodVerba, cnvCodVerbaRef, cnvCodVerbaFerias, null, null, null);
                        if (csaCodigos != null) {
                            if (csaCodigos.isEmpty()) {
                                csaIdCodigo.put(chaveIdentificacao, "NAO_ENCONTRADO");
                            } else if (csaCodigos.size() > 1) {
                                csaIdCodigo.put(chaveIdentificacao, "ENCONTRADO_MAIS_DE_UM");
                            } else {
                                csaIdCodigo.put(chaveIdentificacao, csaCodigos.get(0));
                            }
                        }
                    }

                    // Busca a chave de identificação do cache
                    String csaCodigo = csaIdCodigo.get(chaveIdentificacao);

                    if (separaMapeamentoMultiplo && ("ENCONTRADO_MAIS_DE_UM".equals(csaCodigo))) {
                        // Se encontrou mais de um, então tenta localizar o contrato através da
                        // informação adicional de matrícula e índice
                        final String rseMatricula = (String) memoria.get("RSE_MATRICULA");
                        final String adeNumero = (String) memoria.get("ADE_NUMERO");
                        final String adeIndice = (String) memoria.get("ADE_INDICE");
                        final List<String> csaCodigos = obtemConsignataria(csaIdentificador, svcIdentificador, orgIdentificador, estIdentificador, cnvCodVerba, cnvCodVerbaRef, cnvCodVerbaFerias, rseMatricula, adeNumero, adeIndice);
                        if ((csaCodigos != null) && (csaCodigos.size() == 1)) {
                            // Se encontrou apenas um, então este será a consignatária para esta linha de retorno
                            csaCodigo = csaCodigos.get(0);
                        }
                    }

                    if ("NAO_ENCONTRADO".equals(csaCodigo)) {
                        // É linha sem mapeamento, então não retorna na lista de erros pois os registros
                        // sem mapeamento são gerados na fase 4 do retorno
                        continue;

                    } else if ((csaCodigo == null) || "ENCONTRADO_MAIS_DE_UM".equals(csaCodigo)) {
                        // É nulo ou encontrou mais de um, então adiciona na lista de erros para
                        // geração do relatório de mapeamento multiplo
                        erro.append(leitor.getLinha()).append(System.lineSeparator());
                        lstErro.add((Map<String, Object>) memoria.clone());

                    } else {
                        Map<String, String> parametros = paramCsa.get(csaCodigo);
                        if (parametros == null) {
                            parametros = new HashMap<>();
                            paramCsa.put(csaCodigo, parametros);
                        }

                        // Verifica parâmetro para ver se separa relatório de integração da consignatária
                        String separarRelIntegracao = parametros.get(CodedValues.TPA_SEPARAR_RELATORIO_INTEGRACAO);
                        if (TextHelper.isNull(separarRelIntegracao)) {
                            separarRelIntegracao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_SEPARAR_RELATORIO_INTEGRACAO, responsavel);
                            separarRelIntegracao = !TextHelper.isNull(separarRelIntegracao) ? separarRelIntegracao : CodedValues.SEPARA_REL_INTEGRACAO_NAO;
                            parametros.put(CodedValues.TPA_SEPARAR_RELATORIO_INTEGRACAO, separarRelIntegracao);
                        }

                        // Verifica parâmetro para ver se gera relatório de integração para correspondente
                        String geraRelIntegracaoCor = parametros.get(CodedValues.TPA_GERAR_REL_INTEGRACAO_CORRESPONDENTE);
                        if (TextHelper.isNull(geraRelIntegracaoCor)) {
                            geraRelIntegracaoCor = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_GERAR_REL_INTEGRACAO_CORRESPONDENTE, responsavel);
                            geraRelIntegracaoCor = !TextHelper.isNull(geraRelIntegracaoCor) ? geraRelIntegracaoCor : CodedValues.TPA_NAO;
                            parametros.put(CodedValues.TPA_GERAR_REL_INTEGRACAO_CORRESPONDENTE, geraRelIntegracaoCor);
                        }

                        Map<String, StringBuilder> saidaSplit = saida.get(csaCodigo);
                        if (saidaSplit == null) {
                            saidaSplit = new HashMap<>();
                            saida.put(csaCodigo, saidaSplit);
                        }

                        String chaveSeparador = CodedValues.SEPARA_REL_INTEGRACAO_NAO;
                        if (CodedValues.SEPARA_REL_INTEGRACAO_POR_ESTABELECIMENTO.equals(separarRelIntegracao) && !temEst && !temOrg) {
                            chaveSeparador = estIdentificador;
                        } else if (CodedValues.SEPARA_REL_INTEGRACAO_POR_ORGAO.equals(separarRelIntegracao) && !temEst && !temOrg) {
                            chaveSeparador = orgIdentificador;
                        } else if (CodedValues.SEPARA_REL_INTEGRACAO_POR_VERBA.equals(separarRelIntegracao)) {
                            chaveSeparador = cnvCodVerbaArquivo;
                        }

                        buffer = saidaSplit.get(chaveSeparador);
                        if (buffer == null) {
                            buffer = new StringBuilder(leitor.getLinha());
                            saidaSplit.put(chaveSeparador, buffer);
                        } else {
                            buffer.append(System.lineSeparator()).append(leitor.getLinha());
                        }

                        if (!TextHelper.isNull(geraRelIntegracaoCor) && CodedValues.TPA_SIM.equals(geraRelIntegracaoCor) && (saidaCor != null)) {
                            // Recupera os convênios dos correspondentes
                            Map<String, List<String>> corPorVerba = cacheCnvCor.get(csaCodigo);
                            if (corPorVerba == null) {
                                try {
                                    corPorVerba = convenioController.getCorrespondentePorCnvCodVerba(csaCodigo, !TextHelper.isNull(cnvCodVerbaRef), !TextHelper.isNull(cnvCodVerbaFerias), responsavel);
                                } catch (final ConvenioControllerException e) {
                                    corPorVerba = new HashMap<>();
                                    LOG.error("Não foi possível recuperar os convênios dos correspondentes para gerar o relatório de integração por correspondente.", e);
                                }
                                cacheCnvCor.put(csaCodigo, corPorVerba);
                            }

                            // Recupera todos os correspondentes por verba
                            final List<String> correspondentes = corPorVerba.get(cnvCodVerbaArquivo);
                            if ((correspondentes != null) && !correspondentes.isEmpty()) {
                                // Inclui a linha para todos os correspondentes que possuem convênio para esta verba
                                for (final String corCodigo : correspondentes) {
                                    StringBuilder bufferCor = saidaCor.get(corCodigo);
                                    if (bufferCor == null) {
                                        bufferCor = new StringBuilder(leitor.getLinha());
                                        saidaCor.put(corCodigo, bufferCor);
                                    } else {
                                        bufferCor.append(System.lineSeparator()).append(leitor.getLinha());
                                    }
                                }
                            }
                        }

                        // Verifica se deve gerar relatórios de integração de consignatárias para os órgãos
                        if (gerarRelIntegracaoCsaParaOrg && !TextHelper.isNull(orgIdentificador) && (saidaOrg != null)) {
                            // Verifica se existe no cache a identificação do órgão+estabelecimento
                            final String chaveEstOrg = estIdentificador + ", " + orgIdentificador;
                            if (!orgIdCodigo.containsKey(chaveEstOrg)) {
                                final List<String> orgCodigosLinha = obtemOrgao(estIdentificador, orgIdentificador);
                                if ((orgCodigosLinha != null) && (orgCodigosLinha.size() > 0)) {
                                    orgIdCodigo.put(chaveEstOrg, orgCodigosLinha.get(0));
                                }
                            }

                            // Busca a chave de identificação do cache
                            final String orgCodigoLinha = orgIdCodigo.get(chaveEstOrg);
                            if (!TextHelper.isNull(orgCodigoLinha)) {
                                Map<String, StringBuilder> bufferOrgCsa = saidaOrg.get(orgCodigoLinha);
                                if (bufferOrgCsa == null) {
                                    bufferOrgCsa = new HashMap<>();
                                    saidaOrg.put(orgCodigoLinha, bufferOrgCsa);
                                }
                                StringBuilder bufferCsa = bufferOrgCsa.get(csaCodigo);
                                if (bufferCsa == null) {
                                    bufferCsa = new StringBuilder(leitor.getLinha());
                                    bufferOrgCsa.put(csaCodigo, bufferCsa);
                                } else {
                                    bufferCsa.append(System.lineSeparator()).append(leitor.getLinha());
                                }
                            }
                        }

                        if (saidaTraduzida != null) {
                            HashMap<String, HashMap<String, List<Map<String, Object>>>> nomesArqEntrada = saidaTraduzida.get(csaCodigo);
                            if (nomesArqEntrada == null) {
                                nomesArqEntrada = new HashMap<>();
                                nomesArqEntrada.put(nomeArqEntrada, new HashMap<>());
                                saidaTraduzida.put(csaCodigo, nomesArqEntrada);
                            }
                            HashMap<String, List<Map<String, Object>>> hashSeparador = nomesArqEntrada.get(nomeArqEntrada);
                            if (hashSeparador == null) {
                                hashSeparador = new HashMap<>();
                                hashSeparador.put(chaveSeparador, new ArrayList<>());
                                nomesArqEntrada.put(nomeArqEntrada, hashSeparador);
                            }

                            bufferTraduzido = hashSeparador.get(chaveSeparador);
                            if (bufferTraduzido == null) {
                                bufferTraduzido = new ArrayList<>();
                                hashSeparador.put(chaveSeparador, bufferTraduzido);
                            }
                            bufferTraduzido.add((Map<String, Object>) memoria.clone());
                        }
                    }
                }
                tradutor.encerraTraducao();
                LOG.debug("FIM TRADUÇÃO: " + nomeArqEntrada + " --> " + DateHelper.getSystemDatetime());

                // Renomeia o arquivo
                arqEntrada.renameTo(new File(nomeArqEntrada + ".ok"));
            }
        }

        return saida;
    }

    private List<String> obtemConsignataria(String csaIdentificador, String svcIdentificador, String orgIdentificador, String estIdentificador, String cnvCodVerba, String cnvCodVerbaRef, String cnvCodVerbaFerias, String rseMatricula, String adeNumero, String adeIndice) throws RelatorioControllerException {
        try {
            final ObtemConsignatariaParaRetornoQuery query = new ObtemConsignatariaParaRetornoQuery();
            query.csaIdentificador = csaIdentificador;
            query.svcIdentificador = svcIdentificador;
            query.orgIdentificador = orgIdentificador;
            query.estIdentificador = estIdentificador;
            query.cnvCodVerba = cnvCodVerba;
            query.cnvCodVerbaRef = cnvCodVerbaRef;
            query.cnvCodVerbaFerias = cnvCodVerbaFerias;
            query.rseMatricula = rseMatricula;
            query.adeNumero = adeNumero;
            query.adeIndice = adeIndice;
            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private List<String> obtemOrgao(String estIdentificador, String orgIdentificador) throws RelatorioControllerException {
        try {
            final ObtemOrgaoParaRetornoQuery query = new ObtemOrgaoParaRetornoQuery();
            query.estIdentificador = estIdentificador;
            query.orgIdentificador = orgIdentificador;
            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Grava os relatórios de integração das consignatárias
     * @param saida
     * @param saidaTraduzida
     * @param saidaCor
     * saidaOrg
     * @param pathSaida
     * @param tipo
     * @param sufixoNomeRel
     * @param periodo
     * @param confEntradaXml
     * @param confTradutorXml
     * @param responsavel
     * @throws IOException
     */
    private void gravaArquivoIntegracao(HashMap<String, Map<String, StringBuilder>> saida, HashMap<String, HashMap<String, HashMap<String, List<Map<String, Object>>>>> saidaTraduzida, Map<String, StringBuilder> saidaCor, Map<String, Map<String, StringBuilder>> saidaOrg, String pathSaida, String tipo, String sufixoNomeRel, String periodo, String confEntradaXml, String confTradutorXml, AcessoSistema responsavel) throws IOException {
        File dir = new File(pathSaida);
        if (!dir.exists() && !dir.mkdirs()) {
            LOG.error("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
            return;
        }

        final String hoje = DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");

        String csaCodigo = null;
        StringBuilder buffer = null;

        PrintWriter arqSaida = null;
        for (final String element : saida.keySet()) {
            csaCodigo = element;

            final ArrayList<String> arquivosGerados = new ArrayList<>();

            dir = new File(pathSaida + csaCodigo);
            if (!dir.exists() && !dir.mkdirs()) {
                LOG.error("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
                return;
            }

            String separarRelIntegracao = CodedValues.SEPARA_REL_INTEGRACAO_NAO;
            try {
                separarRelIntegracao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_SEPARAR_RELATORIO_INTEGRACAO, responsavel);
                separarRelIntegracao = !TextHelper.isNull(separarRelIntegracao) ? separarRelIntegracao : CodedValues.SEPARA_REL_INTEGRACAO_NAO;
            } catch (final ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                separarRelIntegracao = CodedValues.SEPARA_REL_INTEGRACAO_NAO;
            }

            final Map<String, StringBuilder> saidaSplit = saida.get(csaCodigo);
            for (final String chaveSeparador : saidaSplit.keySet()) {
                buffer = saidaSplit.get(chaveSeparador);
                if (buffer != null) {
                    /**
                     * Define o nome do relatório de integração, de acordo com o tipo (retorno, atrasado, critica),
                     * e com a entidade que realiza o processamento (CSE, EST, ORG). O processamento geral por CSE
                     * também pode gerar arquivos separados por EST, ORG, VERBA, de acordo com o parâmetro de
                     * consignatária (24). Os nomes aqui definidos afetam o processo de validação de relatórios de
                     * integração (ValidaIntegracaoConsignataria.java). Exemplos de nomes:
                     *
                     * IMPORTAÇÃO GERAL (PADRÃO):      retorno_21-02-2014-103500.txt              (2 conjuntos)
                     * IMPORTAÇÃO POR ESTABELECIMENTO: retorno_001_21-02-2014-103500.txt          (3 conjuntos)
                     * IMPORTAÇÃO POR ÓRGÃO:           retorno_001_0001_21-02-2014-103500.txt     (4 conjuntos)
                     *
                     * IMPORTAÇÃO GERAL SEP/ ORG:      retorno-0001_21-02-2014-103500.txt         (2 conjuntos)
                     * IMPORTAÇÃO GERAL SEP/ EST:      retorno-001_21-02-2014-103500.txt          (2 conjuntos)
                     * IMPORTAÇÃO GERAL SEP/ VERBA:    retorno-699_21-02-2014-103500.txt          (2 conjuntos)
                     *
                     * IMPORTAÇÃO POR EST SEP/ VERBA:  retorno-699_001_21-02-2014-103500.txt      (3 conjuntos)
                     * IMPORTAÇÃO POR ORG SEP/ VERBA:  retorno-699_001_0001_21-02-2014-103500.txt (4 conjuntos)
                     *
                     * OBS: O caractere "-" separador de nome do caso de arquivo separado por EST, ORG, VERBA deve ser
                     * disinto do caractere usado quando o arquivo é gerado por entidade de EST/ORG "_", já que ele é
                     * usado no processo de validação para determinar se o leiaute do arquivo é por EST ou ORG.
                     */
                    final String separadorRel = !TextHelper.isNull(chaveSeparador) && !CodedValues.SEPARA_REL_INTEGRACAO_NAO.equals(chaveSeparador) ? "-" + FileHelper.prepararNomeArquivo(chaveSeparador) : "";
                    final String nomeArquivo = tipo + separadorRel + sufixoNomeRel + "_" + hoje + "_" + periodo;
                    String nomeArqSaida = pathSaida + csaCodigo + File.separatorChar + nomeArquivo + ".txt";

                    if (saidaTraduzida != null) {
                        // Diretório raiz dos arquivos de integração
                        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
                        // Diretório de raiz de configuração
                        final String pathConfTraducaoRetorno = absolutePath + File.separatorChar + "conf" + File.separatorChar + "retorno" + File.separatorChar + csaCodigo;

                        // Nome dos xmls de reformatação do relatório de integração
                        final String xmlEntrada = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_REL_INTEGRACAO_RETORNO, responsavel);
                        final String xmlTradutor = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_REL_INTEGRACAO_RETORNO, responsavel);
                        final String xmlSaida = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_SAIDA_REL_INTEGRACAO_RETORNO, responsavel);

                        String confEntrada = pathConfTraducaoRetorno + File.separatorChar + xmlEntrada;
                        final String confTradutor = pathConfTraducaoRetorno + File.separatorChar + xmlTradutor;
                        final String confEscritor = pathConfTraducaoRetorno + File.separatorChar + xmlSaida;

                        final File arqEntrada = new File(confEntrada);
                        final File arqTradutor = new File(confTradutor);
                        final File arqEscritor = new File(confEscritor);

                        if (!arqEntrada.exists() || !arqEntrada.canRead()) {
                            // O arquivo de entrada da reformatação não é obrigatório, pois a
                            // tradução pode ser feita apenas com os dados do arquivo.
                            confEntrada = null;
                        }

                        String confNovoRelIntegracao = null;
                        String contadorRelCustomizado = null;
                        try {
                            confNovoRelIntegracao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_ARQ_CONF_EXP_REL_INTEGRACAO_RETORNO, responsavel);
                            if (!TextHelper.isNull(confNovoRelIntegracao)) {
                                confNovoRelIntegracao = pathConfTraducaoRetorno + File.separatorChar + confNovoRelIntegracao;
                                if (!new File(confNovoRelIntegracao).exists()) {
                                    LOG.warn("Arquivo de configuração de relatório de integração está configurado mas não foi encontrado: " + confNovoRelIntegracao);
                                    confNovoRelIntegracao = null;
                                }
                            }

                            contadorRelCustomizado = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_CONTADOR_REL_INTEGRACAO_CUSTOMIZADO, responsavel);
                            if (TextHelper.isNull(contadorRelCustomizado)) {
                                contadorRelCustomizado = "0";
                            }
                        } catch (final ParametroControllerException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }

                        // Se os arquivos de configuração existem para a consignatária, traduz o retorno.
                        if ((arqTradutor.exists() && arqEscritor.exists()) || !TextHelper.isNull(confNovoRelIntegracao)) {
                            /**
                             *  Verifica se a consignatária solicitou que o relatório de integração seja separado
                             *  e caso o separador não esteja presente no arquivo e como será gerado um relatório customizado,
                             *  pesquisa na base para encontrar o separador para gerar o relatório de integração customizado.
                             */
                            List<String> listaSeparador = new ArrayList<>();
                            if (!TextHelper.isNull(confEntrada) && !TextHelper.isNull(separarRelIntegracao) && !CodedValues.SEPARA_REL_INTEGRACAO_NAO.equals(separarRelIntegracao) && (TextHelper.isNull(chaveSeparador) || CodedValues.SEPARA_REL_INTEGRACAO_NAO.equals(chaveSeparador))) {
                                try {
                                    listaSeparador = convenioController.lstConvenioRelIntegracao(csaCodigo, responsavel);
                                } catch (final ConvenioControllerException ex) {
                                    LOG.error(ex.getMessage(), ex);
                                    listaSeparador = new ArrayList<>();
                                    listaSeparador.add(chaveSeparador);
                                }
                            } else {
                                listaSeparador = new ArrayList<>();
                                listaSeparador.add(chaveSeparador);
                            }

                            final HashMap<String, HashMap<String, List<Map<String, Object>>>> arquivosTraduzidos = saidaTraduzida.get(csaCodigo);
                            final Iterator<String> itArquivos = arquivosTraduzidos.keySet().iterator();
                            int i = Integer.valueOf(contadorRelCustomizado) + 1;
                            while (itArquivos.hasNext()) {
                                final String nomeArqEntrada = itArquivos.next();

                                // Recupera o nome do arquivo com o diretório do código do órgão para evitar duplicidade pelo nome do arquivo na tabela de armazenamento do arquivo
                                final String[] partesNomeArquivo = nomeArqEntrada.split(File.separator);
                                final String nomeArqEntradaRet = (!"cse".equals(partesNomeArquivo[partesNomeArquivo.length - 2]) ? partesNomeArquivo[partesNomeArquivo.length - 2] + "-" : "") + partesNomeArquivo[partesNomeArquivo.length - 1].replace(".prc", "");

                                // Caso a lista possua mais de um separador é porque o separador não estava presente no arquivo e foi selecionado com base no convênio da consignatária
                                final Iterator<String> iteSeparador = listaSeparador.iterator();
                                while (iteSeparador.hasNext()) {
                                    String nomeArqSaidaFormatado = pathSaida + csaCodigo + File.separatorChar + "cst_";
                                    final String numero = (arquivosTraduzidos.size() > 1) ? String.valueOf(i) + "_" : "";
                                    final String separador = iteSeparador.next();
                                    String separadorNomeArq = !TextHelper.isNull(separador) && !CodedValues.SEPARA_REL_INTEGRACAO_NAO.equals(separador) ? separador + "_" : "";
                                    separadorNomeArq = FileHelper.prepararNomeArquivo(separadorNomeArq);

                                    if (!TextHelper.isNull(confNovoRelIntegracao)) {
                                        nomeArqSaidaFormatado += confNovoRelIntegracao.substring(confNovoRelIntegracao.lastIndexOf(File.separatorChar) + 1, confNovoRelIntegracao.lastIndexOf('.')) + "_" + separadorNomeArq + numero + hoje + ".txt";
                                        traduzSaidaArquivoIntegracaoNovo(csaCodigo, separarRelIntegracao, separador, confNovoRelIntegracao, nomeArqEntradaRet, nomeArqSaidaFormatado, i, responsavel);
                                    } else {
                                        nomeArqSaidaFormatado += tipo + "_" + numero + "<sufixo>" + "_" + separadorNomeArq + hoje + ".txt";
                                        nomeArqSaidaFormatado = traduzSaidaArquivoIntegracao(csaCodigo, arquivosTraduzidos.get(nomeArqEntrada), separarRelIntegracao, separador, confEntrada, confTradutor, confEscritor, nomeArqEntradaRet, nomeArqSaidaFormatado, i, responsavel);
                                    }

                                    // Compacta o relatório TXT e remove, deixando apenas o ZIP
                                    if (ParamSist.paramEquals(CodedValues.TPC_GERAR_RELATORIOS_INTEGRACAO_COMPACTADOS, CodedValues.TPC_SIM, responsavel) && !TextHelper.isNull(nomeArqSaidaFormatado)) {
                                        FileHelper.zipAndRemove(nomeArqSaidaFormatado);
                                    }

                                    if (iteSeparador.hasNext()) {
                                        i++;
                                    }
                                }

                                if (itArquivos.hasNext()) {
                                    i++;
                                }
                            }

                            // Atualiza o parâmetro de consignatária com o último contador de relatórios customizados
                            try {
                                final CustomTransferObject paramCsa = new CustomTransferObject();
                                paramCsa.setAttribute(Columns.PCS_CSA_CODIGO, csaCodigo);
                                paramCsa.setAttribute(Columns.PCS_TPA_CODIGO, CodedValues.TPA_CONTADOR_REL_INTEGRACAO_CUSTOMIZADO);
                                paramCsa.setAttribute(Columns.PCS_VLR, String.valueOf(i));

                                parametroController.updateParamCsa(paramCsa, responsavel);
                            } catch (final ParametroControllerException ex) {
                                LOG.error(ex.getMessage(), ex);
                            }
                        }
                    }

                    arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaida)));
                    arqSaida.print(buffer.toString());
                    arqSaida.close();

                    if (ParamSist.paramEquals(CodedValues.TPC_GERAR_RELATORIOS_INTEGRACAO_COMPACTADOS, CodedValues.TPC_SIM, responsavel)) {
                        // Compacta o relatório TXT e remove, deixando apenas o ZIP
                        nomeArqSaida = FileHelper.zipAndRemove(nomeArqSaida);
                    }

                    arquivosGerados.add(nomeArqSaida);

                    // Gera relatório em XLS
                    if (ParamSist.paramEquals(CodedValues.TPC_GERA_RELATORIO_INTEGRACAO_XLS, CodedValues.TPC_SIM, responsavel)) {
                        final String nomeArqSaidaXls = geraRelatorioIntegracaoConsignatariaXLS(nomeArqSaida, confEntradaXml, confTradutorXml, pathSaida + csaCodigo + File.separatorChar, nomeArquivo, responsavel);
                        if (!TextHelper.isNull(nomeArqSaidaXls)) {
                            arquivosGerados.add(nomeArqSaidaXls);
                        }
                    }

                    // Se array estiver preenchido
                    if ((arquivosGerados != null) && !arquivosGerados.isEmpty()) {
                        List<TransferObject> paramCsa = null;

                        try {
                            paramCsa = parametroController.selectParamCsa(csaCodigo, CodedValues.TPA_RECEBE_EMAIL_RELATORIO_INTEGRACAO, null, null, null, responsavel);
                        } catch (final ParametroControllerException e) {
                            LOG.error(e.getMessage(), e);
                        }

                        if ((paramCsa != null) && (paramCsa.size() > 0) && (paramCsa.get(0) != null)) {
                            final String emailRelatorioIntegracao = (String) paramCsa.get(0).getAttribute(Columns.PCS_VLR);

                            // envia e-mail para o endereço especificado para esta consignatária no parâmetro de consignatária.
                            if (!TextHelper.isNull(emailRelatorioIntegracao)) {
                                final ProcessaEmailRelatorioIntegracao processoEmailRelInt = new ProcessaEmailRelatorioIntegracao(emailRelatorioIntegracao, csaCodigo, pathSaida + csaCodigo + File.separatorChar, arquivosGerados, nomeArquivo, responsavel);
                                processoEmailRelInt.start();
                            }
                        }
                    }
                }
            }
        }

        // Grava relatório de integração para correspondente
        gravaArquivoIntegracaoCor(saidaCor, tipo, hoje, sufixoNomeRel, responsavel);

        // Grava relatório de integração de consignatária para os órgãos
        gravaArquivoIntegracaoOrg(saidaOrg, tipo, hoje, sufixoNomeRel, responsavel);
    }

    private void gravaArquivoIntegracaoCor(Map<String, StringBuilder> saidaCor, String tipo, String hoje, String sufixoNomeRel, AcessoSistema responsavel) throws IOException {
        if ((saidaCor != null) && !saidaCor.isEmpty()) {
            // Diretório raiz eConsig
            final String absolutePath = ParamSist.getDiretorioRaizArquivos();
            final String pathSaida = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cor" + File.separatorChar + "integracao" + File.separatorChar;

            File dir = new File(pathSaida);
            if (!dir.exists() && !dir.mkdirs()) {
                LOG.error("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
                return;
            }

            PrintWriter arqSaida = null;
            String nomeArqSaida = null;

            for (final String corCodigo : saidaCor.keySet()) {
                dir = new File(pathSaida + corCodigo);
                if (!dir.exists() && !dir.mkdirs()) {
                    LOG.error("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
                    return;
                }

                nomeArqSaida = pathSaida + corCodigo + File.separatorChar + tipo + sufixoNomeRel + "_" + hoje + ".txt";
                final StringBuilder buffer = saidaCor.get(corCodigo);

                arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaida)));
                arqSaida.print(buffer.toString());
                arqSaida.close();

                if (ParamSist.paramEquals(CodedValues.TPC_GERAR_RELATORIOS_INTEGRACAO_COMPACTADOS, CodedValues.TPC_SIM, responsavel)) {
                    // Compacta o relatório TXT e remove, deixando apenas o ZIP
                    FileHelper.zipAndRemove(nomeArqSaida);
                }
            }
        }
    }

    private void gravaArquivoIntegracaoOrg(Map<String, Map<String, StringBuilder>> saidaOrg, String tipo, String hoje, String sufixoNomeRel, AcessoSistema responsavel) throws IOException {
        if ((saidaOrg != null) && !saidaOrg.isEmpty()) {
            // Diretório raiz eConsig
            final String absolutePath = ParamSist.getDiretorioRaizArquivos();
            final String pathSaida = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "integracao_csa" + File.separatorChar;

            File dir = new File(pathSaida);
            if (!dir.exists() && !dir.mkdirs()) {
                LOG.error("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
                return;
            }

            PrintWriter arqSaida = null;
            String nomeArqSaida = null;

            for (final String orgCodigo : saidaOrg.keySet()) {
                dir = new File(pathSaida + orgCodigo);
                if (!dir.exists() && !dir.mkdirs()) {
                    LOG.error("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
                    return;
                }

                final Map<String, StringBuilder> saidaCsa = saidaOrg.get(orgCodigo);
                for (final String csaCodigo : saidaCsa.keySet()) {
                    dir = new File(pathSaida + orgCodigo + File.separatorChar + csaCodigo);
                    if (!dir.exists() && !dir.mkdirs()) {
                        LOG.error("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
                        return;
                    }

                    nomeArqSaida = pathSaida + orgCodigo + File.separatorChar + csaCodigo + File.separatorChar + tipo + sufixoNomeRel + "_" + hoje + ".txt";
                    final StringBuilder buffer = saidaCsa.get(csaCodigo);

                    arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaida)));
                    arqSaida.print(buffer.toString());
                    arqSaida.close();

                    if (ParamSist.paramEquals(CodedValues.TPC_GERAR_RELATORIOS_INTEGRACAO_COMPACTADOS, CodedValues.TPC_SIM, responsavel)) {
                        // Compacta o relatório TXT e remove, deixando apenas o ZIP
                        FileHelper.zipAndRemove(nomeArqSaida);
                    }
                }
            }
        }
    }

    /**
     * Reformata o relatório de integração de uma consignatária utilizando XMLs de configuração
     * específicos desta consignatária. A reformatação pode ser feita apenas com os dados do arquivo
     * de retorno (se confEntrada == null) ou utilizando os dados já carregados nas tabelas de
     * retorno (se confEntrada != null).
     * @param csaCodigo
     * @param dadosEntrada
     * @param confEntrada
     * @param confTradutor
     * @param confEscritor
     * @param nomeArqEntrada
     * @param nomeArqSaida
     */
    private String traduzSaidaArquivoIntegracao(String csaCodigo, HashMap<String, List<Map<String, Object>>> dadosEntrada, String separarRelIntegracao, String chaveSeparador, String confEntrada, String confTradutor, String confEscritor, String nomeArqEntrada, String nomeArqSaida, int sequencial, AcessoSistema responsavel) {
        try {
            if (dadosEntrada != null) {
                if (confEntrada == null) {
                    // Se não tem configuração de entrada, então realiza a tradução
                    // com os dados recuperados apenas do arquivo de entrada
                    final Leitor leitor = new LeitorList(dadosEntrada.get(chaveSeparador));
                    final EscritorArquivoTexto escritor = new EscritorArquivoTexto(confEscritor, nomeArqSaida);
                    final Tradutor tradutor = new Tradutor(confTradutor, leitor, escritor);

                    LOG.debug("TRADUÇÃO SAÍDA: " + csaCodigo + " --> " + DateHelper.getSystemDatetime());
                    tradutor.traduz();
                    LOG.debug("FIM TRADUÇÃO SAÍDA: " + csaCodigo + " --> " + DateHelper.getSystemDatetime());
                    return escritor.getNomeArquivo();

                } else {
                    // Se tem configuração de entrada, então realiza a tradução a partir
                    // das tabelas de arquivo retorno, previamente carregadas na importação
                    final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
                    final MapSqlParameterSource queryParams = new MapSqlParameterSource();

                    final RelatorioDAO relatorioDAO = DAOFactory.getDAOFactory().getRelatorioDAO();
                    final String sql = relatorioDAO.montaQueryRelatorioIntegracao(csaCodigo, chaveSeparador, separarRelIntegracao, sequencial, nomeArqEntrada, queryParams);

                    return jdbc.query(sql, queryParams, rs -> {
                        try {
                            final Leitor leitor = new LeitorBaseDeDados(confEntrada, rs);
                            final EscritorArquivoTexto escritor = new EscritorArquivoTexto(confEscritor, nomeArqSaida);
                            final Tradutor tradutor = new Tradutor(confTradutor, leitor, escritor);

                            LOG.debug("TRADUÇÃO SAÍDA: " + csaCodigo + " --> " + DateHelper.getSystemDatetime());
                            tradutor.traduz();
                            LOG.debug("FIM TRADUÇÃO SAÍDA: " + csaCodigo + " --> " + DateHelper.getSystemDatetime());

                            return escritor.getNomeArquivo();
                        } catch (final ParserException ex) {
                            LOG.error(ex.getMessage(), ex);
                            return null;
                        }
                    });
                }
            }
        } catch (final ParserException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * Reformata o relatório de integração de uma consignatária utilizando XML de configuração
     * específico desta consignatária, para nova biblioteca de geração. A reformatação será feita
     * com os dados já carregados nas tabelas de retorno.
     * @param csaCodigo
     * @param confNovoRelIntegracao
     * @param nomeArqEntrada
     * @param nomeArqSaida
     */
    private void traduzSaidaArquivoIntegracaoNovo(String csaCodigo, String separarRelIntegracao, String chaveSeparador, String confNovoRelIntegracao, String nomeArqEntrada, String nomeArqSaida, int sequencial, AcessoSistema responsavel) {
        try {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();

            final RelatorioDAO relatorioDAO = DAOFactory.getDAOFactory().getRelatorioDAO();
            final String sql = relatorioDAO.montaQueryRelatorioIntegracao(csaCodigo, chaveSeparador, separarRelIntegracao, sequencial, nomeArqEntrada, queryParams);

            jdbc.query(sql, queryParams, rs -> {
                try {
                    final DatabaseReader reader = new DatabaseReader(confNovoRelIntegracao, null, rs);
                    final TextFileWriter writer = new TextFileWriter(confNovoRelIntegracao).setDestination(new File(nomeArqSaida));
                    final Translator translator = new Translator(reader, writer, confNovoRelIntegracao);

                    LOG.debug("TRADUÇÃO SAÍDA: " + csaCodigo + " --> " + DateHelper.getSystemDatetime());
                    translator.translate();
                    LOG.debug("FIM TRADUÇÃO SAÍDA: " + csaCodigo + " --> " + DateHelper.getSystemDatetime());
                } catch (InterpreterException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
                return null;
            });
        } catch (DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private String geraRelatorioIntegracaoMapeamentoMultiploXLS(List<Map<String, Object>> lista, String entradaImpRetorno, String tradutorImpRetorno, String pathSaida, String subTitulo, String nomeArquivo, AcessoSistema responsavel) {
        final List<TransferObject> dataSetList = new ArrayList<>();

        DocumentoTipo documento;
        try {
            documento = XmlHelper.unmarshal(new FileInputStream(tradutorImpRetorno));
            String[] camposRelatorio = null;
            String[] nomeCamposRelatorio = null;
            if (documento.getParametro() != null) {
                for (final ParametroTipo param : documento.getParametro()) {
                    if ("CAMPOS_RELATORIO".equalsIgnoreCase(param.getNome())) {
                        camposRelatorio = param.getValor().split(";");
                    } else if ("NOME_CAMPOS_RELATORIO".equalsIgnoreCase(param.getNome())) {
                        nomeCamposRelatorio = param.getValor().split(";");
                    }
                }
            }

            // Transforma lista map em lista de CTO
            final Iterator<Map<String, Object>> iterator = lista.iterator();

            TransferObject cto = null;
            TransferObject ctoTmp = null;
            if ((camposRelatorio != null) && (nomeCamposRelatorio != null) && (camposRelatorio.length > 0) && (camposRelatorio.length == nomeCamposRelatorio.length)) {
                while (iterator.hasNext()) {
                    ctoTmp = new CustomTransferObject();
                    ctoTmp.setAtributos(iterator.next());
                    cto = new CustomTransferObject();

                    for (int i = 0; i < camposRelatorio.length; i++) {
                        final String value = (String) ctoTmp.getAttribute(camposRelatorio[i]);
                        if (value != null) {
                            cto.setAttribute(nomeCamposRelatorio[i], value);
                        }
                    }

                    dataSetList.add(cto);
                }
            } else {
                while (iterator.hasNext()) {
                    cto = new CustomTransferObject();
                    cto.setAtributos(iterator.next());
                    dataSetList.add(cto);
                }
            }

            // Pega as colunas que serao geradas no relatorio
            Iterator<MapeamentoTipo> atributosIterator = documento.getMapeamento().iterator();
            MapeamentoTipo atributo = null;
            List<String> campos = new ArrayList<>();
            List<String> colunas = new ArrayList<>();
            if ((camposRelatorio != null) && (nomeCamposRelatorio != null) && (camposRelatorio.length > 0) && (camposRelatorio.length == nomeCamposRelatorio.length)) {
                campos = Arrays.asList(nomeCamposRelatorio);
                colunas = campos;
            } else {
                while (atributosIterator.hasNext()) {
                    atributo = atributosIterator.next();
                    campos.add(atributo.getSaida());
                }
                atributosIterator = documento.getMapeamento().iterator();
                while (atributosIterator.hasNext()) {
                    atributo = atributosIterator.next();
                    colunas.add(atributo.getSaida());
                }
            }

            if (lista != null && !lista.isEmpty()) {
                final com.zetra.econsig.report.config.Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("integracao_mapeamento_multiplo");

                final Map<String, String[]> parameterMap = new HashMap<>();

                parameterMap.put(ReportManager.REPORT_DIR_EXPORT, new String[] { pathSaida });
                parameterMap.put(ReportManager.REPORT_FILE_NAME, new String[] { ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.integracao.prefixo.xls", responsavel) + nomeArquivo });
                parameterMap.put(ReportManager.PARAM_NAME_SUBTITULO, new String[] { subTitulo });

                final ProcessaRelatorioIntegracaoMapeamentoMultiplo processaRelatorioIntegracaoMapeamentoMultiplo = new ProcessaRelatorioIntegracaoMapeamentoMultiplo(relatorio, parameterMap, campos, colunas, dataSetList, null, responsavel);
                processaRelatorioIntegracaoMapeamentoMultiplo.run();

                String nomeArqSaidaXls = processaRelatorioIntegracaoMapeamentoMultiplo.getNomeArqRelatorio();

                if (ParamSist.paramEquals(CodedValues.TPC_GERAR_RELATORIOS_INTEGRACAO_COMPACTADOS, CodedValues.TPC_SIM, responsavel)) {
                    // Compacta o relatório TXT e remove, deixando apenas o ZIP
                    nomeArqSaidaXls = FileHelper.zipAndRemove(nomeArqSaidaXls);
                }

                return nomeArqSaidaXls;
            }
        } catch (IOException | ParserException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    private String geraRelatorioIntegracaoConsignatariaXLS(String nomeArqAbs, String entradaImpRetorno, String tradutorImpRetorno, String pathSaida, String nomeArquivo, AcessoSistema responsavel) {
        // Hash que recebe os dados do que serão lidos do arquivo de entrada
        final Map<String, Object> entrada = new HashMap<>();
        final List<TransferObject> dataSetList = new ArrayList<>();
        TransferObject dataSet = null;

        // Prepara tradução do arquivo de retorno.
        LeitorArquivoTexto leitor = null;
        if (nomeArqAbs.toLowerCase().endsWith(".zip")) {
            leitor = new LeitorArquivoTextoZip(entradaImpRetorno, nomeArqAbs, true);
        } else {
            leitor = new LeitorArquivoTexto(entradaImpRetorno, nomeArqAbs, true);
        }

        final Escritor escritor = new EscritorMemoria(entrada);
        final Tradutor tradutor = new Tradutor(tradutorImpRetorno, leitor, escritor);

        try {
            final DocumentoTipo documento = XmlHelper.unmarshal(new FileInputStream(tradutorImpRetorno));

            String[] camposRelatorio = null;
            String[] nomeCamposRelatorio = null;
            if (documento.getParametro() != null) {
                for (final ParametroTipo param : documento.getParametro()) {
                    if ("CAMPOS_RELATORIO".equalsIgnoreCase(param.getNome())) {
                        camposRelatorio = param.getValor().split(";");
                    } else if ("NOME_CAMPOS_RELATORIO".equalsIgnoreCase(param.getNome())) {
                        nomeCamposRelatorio = param.getValor().split(";");
                    }
                }
            }

            final List<MapeamentoTipo> atributos = documento.getMapeamento();

            tradutor.iniciaTraducao();
            while (tradutor.traduzProximo()) {
                MapeamentoTipo atributo = null;
                dataSet = new CustomTransferObject();
                for (final MapeamentoTipo element : atributos) {
                    atributo = element;
                    if ((camposRelatorio != null) && (nomeCamposRelatorio != null) && (camposRelatorio.length > 0) && (camposRelatorio.length == nomeCamposRelatorio.length)) {
                        int i = 0;
                        for (i = 0; i < camposRelatorio.length; i++) {
                            if (atributo.getSaida().equalsIgnoreCase(camposRelatorio[i])) {
                                dataSet.setAttribute(nomeCamposRelatorio[i], entrada.get(atributo.getSaida()));
                                break;
                            }
                        }
                    } else {
                        dataSet.setAttribute(atributo.getSaida(), entrada.get(atributo.getSaida()));
                    }
                }
                dataSetList.add(dataSet);
            }

            tradutor.encerraTraducao();

            // Pega as colunas que serao geradas no relatorio
            List<String> campos = new ArrayList<>();
            if ((camposRelatorio != null) && (nomeCamposRelatorio != null) && (camposRelatorio.length > 0) && (camposRelatorio.length == nomeCamposRelatorio.length)) {
                campos = Arrays.asList(nomeCamposRelatorio);
            } else {
                MapeamentoTipo atributo = null;
                for (final MapeamentoTipo element : atributos) {
                    atributo = element;
                    campos.add(atributo.getSaida());
                }
            }

            final com.zetra.econsig.report.config.Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("integracao_csa");
            final Map<String, String[]> parameterMap = new HashMap<>();
            parameterMap.put(ReportManager.REPORT_DIR_EXPORT, new String[] { pathSaida });
            parameterMap.put(ReportManager.REPORT_FILE_NAME, new String[] { ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.integracao.prefixo.xls", responsavel) + nomeArquivo });

            final ProcessaRelatorioIntegracaoConsignataria processaRelatorioIntegracaoConsignataria = new ProcessaRelatorioIntegracaoConsignataria(relatorio, parameterMap, campos, dataSetList, null, responsavel);
            processaRelatorioIntegracaoConsignataria.run();

            String nomeArqSaidaXls = processaRelatorioIntegracaoConsignataria.getNomeArqRelatorio();

            if (ParamSist.paramEquals(CodedValues.TPC_GERAR_RELATORIOS_INTEGRACAO_COMPACTADOS, CodedValues.TPC_SIM, responsavel)) {
                // Compacta o relatório TXT e remove, deixando apenas o ZIP
                nomeArqSaidaXls = FileHelper.zipAndRemove(nomeArqSaidaXls);
            }

            return nomeArqSaidaXls;
        } catch (ParserException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    /** **************************** FIM RELATÓRIO DE INTEGRAÇÃO **************************** **/

    @Override
    public void gerarRelatorioRepasse(AcessoSistema responsavel) throws RelatorioControllerException {

        final GeradorRelatorioRepasse gerador = new GeradorRelatorioRepasse(AcessoSistema.getAcessoUsuarioSistema());
        gerador.run();

    }

    @Override
    public List<TransferObject> lstRelatorio(CustomTransferObject filtro) throws RelatorioControllerException {
        try {
            final ListaRelatorioQuery query = new ListaRelatorioQuery();
            query.relAtivo = (Short) filtro.getAttribute(Columns.REL_ATIVO);
            query.relCodigo = (String) filtro.getAttribute(Columns.REL_CODIGO);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<TransferObject> lstRelatorioCustomizado(CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final ListaRelatorioQuery query = new ListaRelatorioQuery();

            if (filtro != null) {
                query.relTitulo = (String) filtro.getAttribute(Columns.REL_TITULO);
                query.relAtivo = (Short) filtro.getAttribute(Columns.REL_ATIVO);
                query.relCodigo = (String) filtro.getAttribute(Columns.REL_CODIGO);
            }

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            query.relCustomizado = CodedValues.TPC_SIM;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstRelatorioTipo(CustomTransferObject filtro, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final ListaRelatorioTipoQuery query = new ListaRelatorioTipoQuery();
            query.relCodigo = (String) filtro.getAttribute(Columns.REL_CODIGO);
            query.responsavel = responsavel;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Collection<TipoFiltroRelatorio> lstTipoFiltroRelatorio(AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            return TipoFiltroRelatorioHome.findAllTipoFiltroRelatorio();
        } catch (final FindException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public Collection<TipoFiltroRelatorio> lstTipoFiltroRelatorioEditavel(AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            return TipoFiltroRelatorioHome.findTipoFiltroRelatorioEditavel();
        } catch (final FindException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public Pair<String[], List<TransferObject>> geraRelatorioSinteticoConsignacoes(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioSinteticoQuery query = new RelatorioSinteticoQuery();
            query.setCriterios(criterios);

            final List<TransferObject> tranObj = query.executarDTO();
            final String[] fields = query.getFields();

            return Pair.of(fields, tranObj);
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Pair<String[], List<TransferObject>> geraRelatorioSinteticoDescontos(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioSinteticoDescontosQuery query = new RelatorioSinteticoDescontosQuery();
            query.setCriterios(criterios);

            final List<TransferObject> tranObj = query.executarDTO();
            final String[] fields = query.getFields();

            return Pair.of(fields, tranObj);
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Pair<String[], List<TransferObject>> geraRelatorioSinteticoMovFin(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioSinteticoMovFinQuery query = new RelatorioSinteticoMovFinQuery();
            query.setCriterios(criterios);

            final List<TransferObject> tranObj = query.executarDTO();
            final String[] fields = query.getFields();

            return Pair.of(fields, tranObj);
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<Object> geraRelatorioSaldoDevedorPorCsaPeriodo(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioSaldoDevedorPorCsaPeriodoQuery query = new RelatorioSaldoDevedorPorCsaPeriodoQuery();
            query.setCriterios(criterios);

            final List<TransferObject> tranObj = query.executarDTO();
            final String[] fields = query.getFields();

            final List<Object> conteudo = new ArrayList<>();
            conteudo.add(tranObj);
            conteudo.add(fields);
            conteudo.add(query.countMes);
            conteudo.add(query.countEst);
            conteudo.add(query.campos);
            conteudo.add(query.countPeriodos);
            conteudo.add(query.alias);
            conteudo.add(query.camposTotal);
            conteudo.add(query.camposSpan);

            return conteudo;

        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<Object> geraRelatorioPrdPagasCsaPeriodo(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioPrdPagasPorCsaPeriodoQuery query = new RelatorioPrdPagasPorCsaPeriodoQuery();
            query.setCriterios(criterios);

            final List<TransferObject> tranObj = query.executarDTO();
            final String[] fields = query.getFields();

            final List<Object> conteudo = new ArrayList<>();
            conteudo.add(tranObj);
            conteudo.add(fields);
            conteudo.add(query.countMes);
            conteudo.add(query.campos);

            return conteudo;

        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstTaxasEfetivasContratos(String periodo, String orgCodigo, List<String> svcCodigos, List<String> sadCodigos, boolean prazoMultiploDoze, List<Integer> prazosInformados, String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioTaxasEfetivasContratosQuery query = new RelatorioTaxasEfetivasContratosQuery();
            query.periodo = periodo;
            query.orgCodigo = orgCodigo;
            query.svcCodigos = svcCodigos;
            query.sadCodigos = sadCodigos;
            query.prazoMultiploDoze = prazoMultiploDoze;
            query.prazosInformados = prazosInformados;
            query.csaCodigo = csaCodigo;

            return query.executarDTO();
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public GerencialInadimplenciaBean geraRelatorioInadimplencia(Date periodo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioGerencialInadimplenciaQuery inadimplencia = new RelatorioGerencialInadimplenciaQuery();
            inadimplencia.periodo = periodo;
            final GerencialInadimplenciaBean bean = new GerencialInadimplenciaBean();
            for (final TransferObject to : inadimplencia.executarDTO()) {
                if (!TextHelper.isNull(to.getAttribute("SUM_TOTAL_CARTEIRA"))) {
                    bean.setTotalCarteira(new BigDecimal(to.getAttribute("SUM_TOTAL_CARTEIRA").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("COUNT_TOTAL_CARTEIRA"))) {
                    bean.setQtdeTotalCarteira(Long.valueOf(to.getAttribute("COUNT_TOTAL_CARTEIRA").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("SUM_INADIMPLENCIA_TOTAL"))) {
                    bean.setInadimplenciaTotal(new BigDecimal(to.getAttribute("SUM_INADIMPLENCIA_TOTAL").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("COUNT_INADIMPLENCIA_TOTAL"))) {
                    bean.setQtdeInadimplenciaTotal(Long.valueOf(to.getAttribute("COUNT_INADIMPLENCIA_TOTAL").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("SUM_TOTAL_CARTEIRA_EMPRESTIMO"))) {
                    bean.setTotalCarteiraEmprestimo(new BigDecimal(to.getAttribute("SUM_TOTAL_CARTEIRA_EMPRESTIMO").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("COUNT_TOTAL_CARTEIRA_EMPRESTIMO"))) {
                    bean.setQtdeTotalCarteiraEmprestimo(Long.valueOf(to.getAttribute("COUNT_TOTAL_CARTEIRA_EMPRESTIMO").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("SUM_INADIMPLENCIA_EMPRESTIMO"))) {
                    bean.setInadimplenciaEmprestimo(new BigDecimal(to.getAttribute("SUM_INADIMPLENCIA_EMPRESTIMO").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("COUNT_INADIMPLENCIA_EMPRESTIMO"))) {
                    bean.setQtdeInadimplenciaEmprestimo(Long.valueOf(to.getAttribute("COUNT_INADIMPLENCIA_EMPRESTIMO").toString()));
                }
            }

            final List<GerencialInadimplenciaCsaBean> retorno = new ArrayList<>();
            final RelatorioGerencialInadimplenciaCsaQuery inadimplenciaCsa = new RelatorioGerencialInadimplenciaCsaQuery();
            inadimplenciaCsa.periodo = periodo;
            final List<TransferObject> lista = inadimplenciaCsa.executarDTO();

            if (lista != null) {
                for (final TransferObject to : lista) {
                    final GerencialInadimplenciaCsaBean beanCsa = new GerencialInadimplenciaCsaBean();
                    if (!TextHelper.isNull(to.getAttribute("CSA_NOME"))) {
                        beanCsa.setCsaNome(to.getAttribute("CSA_NOME").toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute("CSA_NOME_ABREV"))) {
                        beanCsa.setCsaNomeAbrev(to.getAttribute("CSA_NOME_ABREV").toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute("CSA_QTDE_INADIMPLENCIA"))) {
                        beanCsa.setCsaQtdeInadimplencia(Long.valueOf(to.getAttribute("CSA_QTDE_INADIMPLENCIA").toString()));
                    }
                    if (!TextHelper.isNull(to.getAttribute("CSA_SUM_INADIMPLENCIA"))) {
                        beanCsa.setCsaSumInadimplencia(new BigDecimal(to.getAttribute("CSA_SUM_INADIMPLENCIA").toString()));
                    }
                    if (!TextHelper.isNull(to.getAttribute("CSA_SUM_ADE_VLR"))) {
                        beanCsa.setCsaSumAdeVlr(new BigDecimal(to.getAttribute("CSA_SUM_ADE_VLR").toString()));
                    }

                    // Calcula percentual de inadimplência
                    if (!TextHelper.isNull(beanCsa.getCsaQtdeInadimplencia()) && (beanCsa.getCsaQtdeInadimplencia() > 0)) {
                        beanCsa.setCsaPercInadimplencia(((beanCsa.getCsaQtdeInadimplencia() * 100) / bean.getQtdeInadimplenciaTotal().doubleValue()));
                    } else {
                        beanCsa.setCsaPercInadimplencia(0d);
                    }

                    retorno.add(beanCsa);
                }
            }
            bean.setQtdeInadimplenciaCsa(retorno);

            return bean;

        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public GerencialInadimplenciaBean geraRelatorioInadimplenciaConsignataria(Date periodo, List<String> csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioGerencialInadimplenciaQuery inadimplencia = new RelatorioGerencialInadimplenciaQuery();
            inadimplencia.periodo = periodo;
            final GerencialInadimplenciaBean bean = new GerencialInadimplenciaBean();
            for (final TransferObject to : inadimplencia.executarDTO()) {
                if (!TextHelper.isNull(to.getAttribute("SUM_TOTAL_CARTEIRA"))) {
                    bean.setTotalCarteira(new BigDecimal(to.getAttribute("SUM_TOTAL_CARTEIRA").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("COUNT_TOTAL_CARTEIRA"))) {
                    bean.setQtdeTotalCarteira(Long.valueOf(to.getAttribute("COUNT_TOTAL_CARTEIRA").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("SUM_INADIMPLENCIA_TOTAL"))) {
                    bean.setInadimplenciaTotal(new BigDecimal(to.getAttribute("SUM_INADIMPLENCIA_TOTAL").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("COUNT_INADIMPLENCIA_TOTAL"))) {
                    bean.setQtdeInadimplenciaTotal(Long.valueOf(to.getAttribute("COUNT_INADIMPLENCIA_TOTAL").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("SUM_TOTAL_CARTEIRA_EMPRESTIMO"))) {
                    bean.setTotalCarteiraEmprestimo(new BigDecimal(to.getAttribute("SUM_TOTAL_CARTEIRA_EMPRESTIMO").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("COUNT_TOTAL_CARTEIRA_EMPRESTIMO"))) {
                    bean.setQtdeTotalCarteiraEmprestimo(Long.valueOf(to.getAttribute("COUNT_TOTAL_CARTEIRA_EMPRESTIMO").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("SUM_INADIMPLENCIA_EMPRESTIMO"))) {
                    bean.setInadimplenciaEmprestimo(new BigDecimal(to.getAttribute("SUM_INADIMPLENCIA_EMPRESTIMO").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("COUNT_INADIMPLENCIA_EMPRESTIMO"))) {
                    bean.setQtdeInadimplenciaEmprestimo(Long.valueOf(to.getAttribute("COUNT_INADIMPLENCIA_EMPRESTIMO").toString()));
                }

            }

            final List<GerencialInadimplenciaCsaBean> retorno = new ArrayList<>();
            final RelatorioGerencialInadimplenciaCsaQuery inadimplenciaCsa = new RelatorioGerencialInadimplenciaCsaQuery();
            inadimplenciaCsa.periodo = periodo;
            inadimplenciaCsa.csaCodigo = csaCodigo;
            final List<TransferObject> lista = inadimplenciaCsa.executarDTO();

            if (lista != null) {
                for (final TransferObject to : lista) {
                    final GerencialInadimplenciaCsaBean beanCsa = new GerencialInadimplenciaCsaBean();
                    if (!TextHelper.isNull(to.getAttribute("CSA_NOME"))) {
                        beanCsa.setCsaNome(to.getAttribute("CSA_NOME").toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute("CSA_NOME_ABREV"))) {
                        beanCsa.setCsaNomeAbrev(to.getAttribute("CSA_NOME_ABREV").toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute("CSA_QTDE_INADIMPLENCIA"))) {
                        final Long csaQtdeInadimplencia = Long.valueOf(to.getAttribute("CSA_QTDE_INADIMPLENCIA").toString());
                        beanCsa.setCsaQtdeInadimplencia(csaQtdeInadimplencia);

                        // Calcula percentual de inadimplência
                        if (!TextHelper.isNull(bean.getQtdeTotalCarteira()) && (bean.getQtdeTotalCarteira() > 0)) {
                            beanCsa.setCsaPercInadimplencia(((csaQtdeInadimplencia * 100) / bean.getQtdeTotalCarteira().doubleValue()));
                        } else {
                            beanCsa.setCsaPercInadimplencia(0d);
                        }
                    }
                    if (!TextHelper.isNull(to.getAttribute("CSA_SUM_INADIMPLENCIA"))) {
                        beanCsa.setCsaSumInadimplencia(new BigDecimal(to.getAttribute("CSA_SUM_INADIMPLENCIA").toString()));
                    }
                    if (!TextHelper.isNull(to.getAttribute("CSA_SUM_ADE_VLR"))) {
                        beanCsa.setCsaSumAdeVlr(new BigDecimal(to.getAttribute("CSA_SUM_ADE_VLR").toString()));
                    }
                    retorno.add(beanCsa);
                }
            }
            bean.setQtdeInadimplenciaCsa(retorno);

            return bean;

        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<GerencialInadimplenciaEvolucaoBean> geraRelatorioInadimplenciaEvolucao(List<Date> periodos, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioGerencialInadimplenciaEvolucaoQuery query = new RelatorioGerencialInadimplenciaEvolucaoQuery();
            query.periodos = periodos;

            final List<GerencialInadimplenciaEvolucaoBean> retorno = new ArrayList<>();
            for (final TransferObject to : query.executarDTO()) {
                final GerencialInadimplenciaEvolucaoBean bean = new GerencialInadimplenciaEvolucaoBean();

                if (!TextHelper.isNull(to.getAttribute(Columns.PRD_DATA_DESCONTO))) {
                    final Calendar cal = Calendar.getInstance();
                    cal.setTime(DateHelper.parse(to.getAttribute(Columns.PRD_DATA_DESCONTO).toString(), "yyyy-MM-dd"));
                    bean.setPeriodoEvolucaoInadimplencia(cal.getTime());
                }

                if (!TextHelper.isNull(to.getAttribute("PORC_EVOLUCAO_INADIMPLENCIA"))) {
                    bean.setPorcEvolucaoInadimplencia(Float.valueOf(to.getAttribute("PORC_EVOLUCAO_INADIMPLENCIA").toString()));
                }

                retorno.add(bean);
            }

            return retorno;

        } catch (HQueryException | ParseException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<GerencialEstatiscoMargemBean> lstEstatiscoMargem(AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<GerencialEstatiscoMargemBean> lista = new ArrayList<>();
            final RelatorioGerencialEstatiscoMargemQuery estatisticoMargem = new RelatorioGerencialEstatiscoMargemQuery();
            for (final TransferObject to : estatisticoMargem.executarDTO()) {
                final GerencialEstatiscoMargemBean bean = new GerencialEstatiscoMargemBean();

                if (!TextHelper.isNull(to.getAttribute("MARGEM_TOTAL"))) {
                    bean.setMargemTotal(new BigDecimal(to.getAttribute("MARGEM_TOTAL").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_TOTAL"))) {
                    bean.setMargemRestTotal(new BigDecimal(to.getAttribute("MARGEM_REST_TOTAL").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_USADA_TOTAL"))) {
                    bean.setMargemUsadaTotal(new BigDecimal(to.getAttribute("MARGEM_USADA_TOTAL").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_MINIMO"))) {
                    bean.setMargemRestMinimo(new BigDecimal(to.getAttribute("MARGEM_REST_MINIMO").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_MAXIMO"))) {
                    bean.setMargemRestMaximo(new BigDecimal(to.getAttribute("MARGEM_REST_MAXIMO").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_MEDIA"))) {
                    bean.setMargemRestMedia(new BigDecimal(to.getAttribute("MARGEM_REST_MEDIA").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_DESVIO"))) {
                    bean.setMargemRestDesvio(new BigDecimal(to.getAttribute("MARGEM_REST_DESVIO").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("QTDE_SER_MARGEM_NEGATIVA"))) {
                    bean.setQtdeSerMargemNegativa(Long.valueOf(to.getAttribute("QTDE_SER_MARGEM_NEGATIVA").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("QTDE_SER_MARGEM_ZERADA"))) {
                    bean.setQtdeSerMargemZerada(Long.valueOf(to.getAttribute("QTDE_SER_MARGEM_ZERADA").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("QTDE_SER_MARGEM_POSITIVA"))) {
                    bean.setQtdeSerMargemPositiva(Long.valueOf(to.getAttribute("QTDE_SER_MARGEM_POSITIVA").toString()));
                }

                if (!TextHelper.isNull(to.getAttribute("MARGEM_TOTAL_2"))) {
                    bean.setMargemTotal2(new BigDecimal(to.getAttribute("MARGEM_TOTAL_2").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_TOTAL_2"))) {
                    bean.setMargemRestTotal2(new BigDecimal(to.getAttribute("MARGEM_REST_TOTAL_2").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_USADA_TOTAL_2"))) {
                    bean.setMargemUsadaTotal2(new BigDecimal(to.getAttribute("MARGEM_USADA_TOTAL_2").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_MINIMO_2"))) {
                    bean.setMargemRestMinimo2(new BigDecimal(to.getAttribute("MARGEM_REST_MINIMO_2").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_MAXIMO_2"))) {
                    bean.setMargemRestMaximo2(new BigDecimal(to.getAttribute("MARGEM_REST_MAXIMO_2").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_MEDIA_2"))) {
                    bean.setMargemRestMedia2(new BigDecimal(to.getAttribute("MARGEM_REST_MEDIA_2").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_DESVIO_2"))) {
                    bean.setMargemRestDesvio2(new BigDecimal(to.getAttribute("MARGEM_REST_DESVIO_2").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("QTDE_SER_MARGEM_2_NEGATIVA"))) {
                    bean.setQtdeSerMargemNegativa2(Long.valueOf(to.getAttribute("QTDE_SER_MARGEM_2_NEGATIVA").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("QTDE_SER_MARGEM_2_ZERADA"))) {
                    bean.setQtdeSerMargemZerada2(Long.valueOf(to.getAttribute("QTDE_SER_MARGEM_2_ZERADA").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("QTDE_SER_MARGEM_2_POSITIVA"))) {
                    bean.setQtdeSerMargemPositiva2(Long.valueOf(to.getAttribute("QTDE_SER_MARGEM_2_POSITIVA").toString()));
                }

                if (!TextHelper.isNull(to.getAttribute("MARGEM_TOTAL_3"))) {
                    bean.setMargemTotal3(new BigDecimal(to.getAttribute("MARGEM_TOTAL_3").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_TOTAL_3"))) {
                    bean.setMargemRestTotal3(new BigDecimal(to.getAttribute("MARGEM_REST_TOTAL_3").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_USADA_TOTAL_3"))) {
                    bean.setMargemUsadaTotal3(new BigDecimal(to.getAttribute("MARGEM_USADA_TOTAL_3").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_MINIMO_3"))) {
                    bean.setMargemRestMinimo3(new BigDecimal(to.getAttribute("MARGEM_REST_MINIMO_3").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_MAXIMO_3"))) {
                    bean.setMargemRestMaximo3(new BigDecimal(to.getAttribute("MARGEM_REST_MAXIMO_3").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_MEDIA_3"))) {
                    bean.setMargemRestMedia3(new BigDecimal(to.getAttribute("MARGEM_REST_MEDIA_3").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("MARGEM_REST_DESVIO_3"))) {
                    bean.setMargemRestDesvio3(new BigDecimal(to.getAttribute("MARGEM_REST_DESVIO_3").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("QTDE_SER_MARGEM_3_NEGATIVA"))) {
                    bean.setQtdeSerMargemNegativa3(Long.valueOf(to.getAttribute("QTDE_SER_MARGEM_3_NEGATIVA").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("QTDE_SER_MARGEM_3_ZERADA"))) {
                    bean.setQtdeSerMargemZerada3(Long.valueOf(to.getAttribute("QTDE_SER_MARGEM_3_ZERADA").toString()));
                }
                if (!TextHelper.isNull(to.getAttribute("QTDE_SER_MARGEM_3_POSITIVA"))) {
                    bean.setQtdeSerMargemPositiva3(Long.valueOf(to.getAttribute("QTDE_SER_MARGEM_3_POSITIVA").toString()));
                }

                lista.add(bean);
            }

            return lista;

        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<GerencialQtdeSerPorFaixaMargemBean> lstQtdeSerPorFaixaMargem(BigDecimal mediaMargem, BigDecimal desvioMargem, Short incideMargem, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<GerencialQtdeSerPorFaixaMargemBean> retorno = new ArrayList<>();
            final RelatorioGerencialQtdeSerPorFaixaMargemQuery qtdeSerPorFaixaMargem = new RelatorioGerencialQtdeSerPorFaixaMargemQuery(mediaMargem, desvioMargem, incideMargem);
            final List<TransferObject> lista = qtdeSerPorFaixaMargem.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final GerencialQtdeSerPorFaixaMargemBean bean = new GerencialQtdeSerPorFaixaMargemBean();
                    if (!TextHelper.isNull(to.getAttribute("DESCRICAO"))) {
                        bean.setDescricao(to.getAttribute("DESCRICAO").toString());
                    }
                    try {
                        bean.setValor1(Double.valueOf(to.getAttribute("VALOR_1").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    try {
                        bean.setValor2(Double.valueOf(to.getAttribute("VALOR_2").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;

        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<ComprometimentoBean> lstComprometimento(TransferObject criterio, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ComprometimentoBean> retorno = new ArrayList<>();

            final RelatorioComprometimentoQuery comprometimento = new RelatorioComprometimentoQuery();
            comprometimento.responsavel = responsavel;
            comprometimento.setCriterios(criterio);

            final List<TransferObject> lista = comprometimento.executarDTO();

            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ComprometimentoBean bean = new ComprometimentoBean();

                    if (!TextHelper.isNull(to.getAttribute("FAIXA"))) {
                        bean.setFaixa(to.getAttribute("FAIXA").toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute("QTDE"))) {
                        bean.setQtde(new BigDecimal(to.getAttribute("QTDE").toString()));
                    }
                    if (!TextHelper.isNull(to.getAttribute("MARGEM_USADA"))) {
                        bean.setMargemUsada(new BigDecimal(to.getAttribute("MARGEM_USADA").toString()));
                    }
                    if (!TextHelper.isNull(to.getAttribute("MARGEM_TOTAL"))) {
                        bean.setMargemTotal(new BigDecimal(to.getAttribute("MARGEM_TOTAL").toString()));
                    }
                    if (!TextHelper.isNull(to.getAttribute("PERCENTUAL"))) {
                        bean.setPercentual(new BigDecimal(to.getAttribute("PERCENTUAL").toString()));
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<ContratosPorCsaBean> lstContratosPorCsa(int maxResultados, String periodo, boolean csaAtivo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ContratosPorCsaBean> retorno = new ArrayList<>();

            final RelatorioGerencialQtdeContratosPorCsaQuery query = new RelatorioGerencialQtdeContratosPorCsaQuery(maxResultados, periodo, csaAtivo);
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ContratosPorCsaBean bean = new ContratosPorCsaBean();
                    if (!TextHelper.isNull(to.getAttribute("CONSIGNATARIA"))) {
                        bean.setConsignataria(to.getAttribute("CONSIGNATARIA").toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute("STATUS"))) {
                        bean.setStatus(to.getAttribute("STATUS").toString());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE_MENSAL").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    try {
                        bean.setVlrMensal(new BigDecimal(to.getAttribute("VLR_MENSAL").toString()));
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }
                    try {
                        bean.setQuantidadeTotal(Long.valueOf(to.getAttribute("QUANTIDADE_TOTAL").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    try {
                        bean.setVlrTotal(new BigDecimal(to.getAttribute("VLR_TOTAL").toString()));
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<ContratosPorCsaBean> lstContratosPorCsa(int maxResultados, String periodo, List<String> csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ContratosPorCsaBean> retorno = new ArrayList<>();

            final RelatorioGerencialCsaQtdeContratosPorCsaQuery query = new RelatorioGerencialCsaQtdeContratosPorCsaQuery(maxResultados, periodo);
            query.csaCodigo = csaCodigo;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ContratosPorCsaBean bean = new ContratosPorCsaBean();
                    if (!TextHelper.isNull(to.getAttribute("CSA_NOME_ABREV"))) {
                        bean.setConsignataria(to.getAttribute("CSA_NOME_ABREV").toString());
                    } else if (!TextHelper.isNull(to.getAttribute("CONSIGNATARIA"))) {
                        bean.setConsignataria(to.getAttribute("CONSIGNATARIA").toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute("STATUS"))) {
                        bean.setStatus(to.getAttribute("STATUS").toString());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    try {
                        bean.setVlrMensal(new BigDecimal(to.getAttribute("VLR_MENSAL").toString()));
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }
                    try {
                        bean.setVlrTotal(new BigDecimal(to.getAttribute("VLR_TOTAL").toString()));
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<CorPorCsaBean> lstCorPorCsa(int maxResultados, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<CorPorCsaBean> retorno = new ArrayList<>();

            final RelatorioGerencialQtdeCorPorCsaQuery query = new RelatorioGerencialQtdeCorPorCsaQuery(maxResultados);
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final CorPorCsaBean bean = new CorPorCsaBean();
                    if (!TextHelper.isNull(to.getAttribute("CONSIGNATARIA"))) {
                        bean.setConsignataria(to.getAttribute("CONSIGNATARIA").toString());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<ContratosPorCrsBean> lstContratosPorCrs(int maxResultados, String periodo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ContratosPorCrsBean> retorno = new ArrayList<>();

            final RelatorioGerencialQtdeContratosPorCrsQuery query = new RelatorioGerencialQtdeContratosPorCrsQuery(maxResultados, periodo);
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ContratosPorCrsBean bean = new ContratosPorCrsBean();
                    if (!TextHelper.isNull(to.getAttribute(Columns.CRS_DESCRICAO))) {
                        bean.setDescricao(to.getAttribute(Columns.CRS_DESCRICAO).toString());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }

            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public int qtdeContratosPorCrs(String periodo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioGerencialQtdeContratosPorCrsQuery query = new RelatorioGerencialQtdeContratosPorCrsQuery(true, periodo);
            return query.executarContador();
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<ContratosPorCategoriaBean> lstContratosPorCategoria(int maxResultados, String periodo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ContratosPorCategoriaBean> retorno = new ArrayList<>();

            final RelatorioGerencialQtdeContratosPorCategoriaQuery query = new RelatorioGerencialQtdeContratosPorCategoriaQuery(maxResultados, periodo);
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ContratosPorCategoriaBean bean = new ContratosPorCategoriaBean();
                    if (!TextHelper.isNull(to.getAttribute(Columns.RSE_TIPO))) {
                        bean.setRseTipo(to.getAttribute(Columns.RSE_TIPO).toString());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }

            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public int qtdeContratosPorCategoria(String periodo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioGerencialQtdeContratosPorCategoriaQuery query = new RelatorioGerencialQtdeContratosPorCategoriaQuery(true, periodo);
            return query.executarContador();
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<ContratosPorCategoriaBean> lstContratosPorCategoria(int maxResultados, String periodo, List<String> csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ContratosPorCategoriaBean> retorno = new ArrayList<>();

            final RelatorioGerencialCsaQtdeContratosPorCategoriaQuery query = new RelatorioGerencialCsaQtdeContratosPorCategoriaQuery(maxResultados, periodo);
            query.csaCodigo = csaCodigo;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ContratosPorCategoriaBean bean = new ContratosPorCategoriaBean();
                    if (!TextHelper.isNull(to.getAttribute(Columns.RSE_TIPO))) {
                        bean.setRseTipo(to.getAttribute(Columns.RSE_TIPO).toString());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }

            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public int qtdeContratosPorCategoria(String periodo, List<String> csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioGerencialCsaQtdeContratosPorCategoriaQuery query = new RelatorioGerencialCsaQtdeContratosPorCategoriaQuery(true, periodo);
            query.csaCodigo = csaCodigo;
            return query.executarContador();
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<ContratosPorSvcBean> lstContratosPorSvc(int maxResultados, String periodo, boolean internacional, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ContratosPorSvcBean> retorno = new ArrayList<>();
            final RelatorioGerencialQtdeContratosPorSvcQuery qtdeContratosPorCategoria = new RelatorioGerencialQtdeContratosPorSvcQuery(maxResultados, periodo, internacional);
            final List<TransferObject> lista = qtdeContratosPorCategoria.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ContratosPorSvcBean bean = new ContratosPorSvcBean();

                    if (!TextHelper.isNull(to.getAttribute(Columns.CNV_COD_VERBA))) {
                        bean.setCnvCodVerba(to.getAttribute(Columns.CNV_COD_VERBA).toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute("CONSIGNATARIA"))) {
                        bean.setConsignataria(to.getAttribute("CONSIGNATARIA").toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute(Columns.SVC_DESCRICAO))) {
                        bean.setSvcDescricao(to.getAttribute(Columns.SVC_DESCRICAO).toString());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    try {
                        bean.setVlrMensal(new BigDecimal(to.getAttribute("VLR_MENSAL").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    try {
                        bean.setVlrTotal(new BigDecimal(to.getAttribute("VLR_TOTAL").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<ContratosPorSvcBean> lstContratosPorSvcRelGerencialCsa(int maxResultados, String periodo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ContratosPorSvcBean> retorno = new ArrayList<>();

            //busca o valor total geral para calcular a porcentagem de cada serviço.
            final RelatorioGerencialCsaQtdeContratosPorSvcQuery valorTotalGeral = new RelatorioGerencialCsaQtdeContratosPorSvcQuery(maxResultados, periodo, true);
            final List<TransferObject> vlrttgrl = valorTotalGeral.executarDTO();
            final Object vlrTotalGeral = vlrttgrl.get(0).getAttribute("VLR_TOTAL_GERAL");
            final double vrReferencia = ((BigDecimal) vlrTotalGeral).doubleValue();
            final Object qtdTotalGeral = vlrttgrl.get(0).getAttribute("QTDE_TOTAL_GERAL");
            final double qtdReferencia = ((Long) qtdTotalGeral).doubleValue();

            final RelatorioGerencialCsaQtdeContratosPorSvcQuery qtdeContratosPorCategoria = new RelatorioGerencialCsaQtdeContratosPorSvcQuery(maxResultados, periodo, false);
            final List<TransferObject> lista = qtdeContratosPorCategoria.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ContratosPorSvcBean bean = new ContratosPorSvcBean();

                    if (!TextHelper.isNull(to.getAttribute(Columns.SVC_DESCRICAO))) {
                        bean.setSvcDescricao(to.getAttribute(Columns.SVC_DESCRICAO).toString());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    try {
                        //usado no gráfico de barras
                        bean.setVlrTotal(new BigDecimal(to.getAttribute("VLR_TOTAL").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    try {//VALOR %
                         //calcula a porcentagem do valor total mostrado na lista dos 10 serviços mais utilizados
                        final double vlrtotalSvc = ((BigDecimal) to.getAttribute("VLR_TOTAL")).doubleValue();
                        final double resPorc = (vlrtotalSvc * 100) / vrReferencia;
                        final BigDecimal resultadoPorcentagemValor = new BigDecimal(resPorc);
                        bean.setVlrPorcentagemSvc(new BigDecimal(resultadoPorcentagemValor.toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    try {//QUANTIDE %
                         //calcula a porcentagem do Quantidade total mostrado na lista dos 10 serviços mais utilizados
                        final double qtdeSvc = ((Long) to.getAttribute("QUANTIDADE")).doubleValue();
                        final double resPorcQtde = (qtdeSvc * 100) / qtdReferencia;
                        final BigDecimal resultadoPorcentagemQuantidade = new BigDecimal(resPorcQtde);
                        bean.setQtdPorcentagemSvc(new BigDecimal(resultadoPorcentagemQuantidade.toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<ServidorPorCrsBean> lstServidorPorCrs(int maxResultados, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ServidorPorCrsBean> retorno = new ArrayList<>();

            final RelatorioGerencialQtdeServidorPorCrsQuery qtdeSerPorCrs = new RelatorioGerencialQtdeServidorPorCrsQuery(maxResultados);
            final List<TransferObject> lista = qtdeSerPorCrs.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ServidorPorCrsBean bean = new ServidorPorCrsBean();
                    if (!TextHelper.isNull(to.getAttribute(Columns.CRS_DESCRICAO))) {
                        bean.setDescricao(to.getAttribute(Columns.CRS_DESCRICAO).toString());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<ServidorPorEstBean> lstServidorPorEst(AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ServidorPorEstBean> retorno = new ArrayList<>();

            final RelatorioGerencialQtdeServidorPorEstQuery serPorEst = new RelatorioGerencialQtdeServidorPorEstQuery();
            final List<TransferObject> lista = serPorEst.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ServidorPorEstBean bean = new ServidorPorEstBean();
                    if (!TextHelper.isNull(to.getAttribute(Columns.EST_NOME))) {
                        bean.setNome(to.getAttribute(Columns.EST_NOME).toString());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<ServidorPorOrgBean> lstServidorPorOrg(boolean somenteOrgaoServidorAtivo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ServidorPorOrgBean> retorno = new ArrayList<>();

            final RelatorioGerencialQtdeServidorPorOrgQuery serPorOrg = new RelatorioGerencialQtdeServidorPorOrgQuery();
            serPorOrg.somenteOrgaoAtivo = somenteOrgaoServidorAtivo;
            final List<TransferObject> lista = serPorOrg.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ServidorPorOrgBean bean = new ServidorPorOrgBean();
                    if (!TextHelper.isNull(to.getAttribute(Columns.ORG_NOME))) {
                        bean.setNome(to.getAttribute(Columns.ORG_NOME).toString());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<ServidorPorTipoBean> lstServidorPorTipo(int maxResultados, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ServidorPorTipoBean> retorno = new ArrayList<>();

            final RelatorioGerencialQtdeServidorPorTipoQuery qtdeSerPorTipo = new RelatorioGerencialQtdeServidorPorTipoQuery(maxResultados);
            final List<TransferObject> lista = qtdeSerPorTipo.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ServidorPorTipoBean bean = new ServidorPorTipoBean();
                    if (!TextHelper.isNull(to.getAttribute(Columns.RSE_TIPO))) {
                        bean.setRseTipo(to.getAttribute(Columns.RSE_TIPO).toString());
                    }
                    try {
                        bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public BigDecimal getTotalPrestacaoEmprestimo(AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            BigDecimal totalPrestacaoEmprestimo = BigDecimal.ZERO;
            final RelatorioGerencialEstatiscoMargemPorNaturezaSvcQuery estatisticoMargemNaturezaEmprestimo = new RelatorioGerencialEstatiscoMargemPorNaturezaSvcQuery(CodedValues.NSE_EMPRESTIMO);
            final List<TransferObject> estatistico = estatisticoMargemNaturezaEmprestimo.executarDTO();
            final Iterator<TransferObject> iteEstatistico = estatistico.iterator();
            if (iteEstatistico.hasNext()) {
                final TransferObject to = iteEstatistico.next();
                try {
                    totalPrestacaoEmprestimo = new BigDecimal(to.getAttribute("TOTAL_PRESTACAO").toString());
                } catch (final RuntimeException e) {
                    LOG.debug("Não foi possível realizar o parser do saldo e total de empréstimo");
                }
            }

            return totalPrestacaoEmprestimo;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public BigDecimal getSaldoDevedorEmprestimo(AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            BigDecimal saldoDevedorEmprestimo = BigDecimal.ZERO;
            final RelatorioGerencialEstatiscoMargemPorNaturezaSvcQuery estatisticoMargemNaturezaEmprestimo = new RelatorioGerencialEstatiscoMargemPorNaturezaSvcQuery(CodedValues.NSE_EMPRESTIMO);
            final List<TransferObject> estatistico = estatisticoMargemNaturezaEmprestimo.executarDTO();
            final Iterator<TransferObject> iteEstatistico = estatistico.iterator();
            if (iteEstatistico.hasNext()) {
                final TransferObject to = iteEstatistico.next();
                try {
                    saldoDevedorEmprestimo = new BigDecimal(to.getAttribute("SALDO_DEVEDOR").toString());
                } catch (final RuntimeException e) {
                    LOG.debug("Não foi possível realizar o parser do saldo e total de empréstimo");
                }
            }

            return saldoDevedorEmprestimo;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<TransferObject> lstPercentualCarteira(String dataIni, String dataFim, List<String> svcCodigos, List<String> orgCodigo, List<String> origensAdes, String campo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioPercentualCarteiraQuery consignatarias = new RelatorioPercentualCarteiraQuery(dataIni, dataFim, svcCodigos, orgCodigo, origensAdes, campo);
            return consignatarias.executarDTO();
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<TransferObject> lstPercentualRejeitoTotal(String periodo, List<String> orgCodigos, List<String> estCodigos, boolean integrada, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioPercentualRejeitoTotalQuery percRejeitoPeriodo = new RelatorioPercentualRejeitoTotalQuery(periodo, orgCodigos, estCodigos, integrada, false);
            return percRejeitoPeriodo.executarDTO();
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<TransferObject> lstPercentualRejeitoPeriodo(String periodo, List<String> orgCodigos, List<String> estCodigos, boolean integrada, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioPercentualRejeitoTotalQuery percRejeitoPeriodo = new RelatorioPercentualRejeitoTotalQuery(periodo, orgCodigos, estCodigos, integrada, true);
            return percRejeitoPeriodo.executarDTO();
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public String insereRelEditavel(String relCodigo, String relTitulo, String funDescricao, String itmDescricao, List<String> papCodigos, Map<String, Map<String, String>> filtros, Map<String, Integer> ordenacaoFiltros, String relTemplateSql, String relAgrupamento, String relTemplateJasper, String relAgendado, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            if (TextHelper.isNull(relCodigo) || TextHelper.isNull(relTitulo) || TextHelper.isNull(relTemplateSql)) {
                throw new RelatorioControllerException("mensagem.erro.relatorio.informacoes.ausentes", responsavel);
            }
            if ((papCodigos == null) || papCodigos.isEmpty()) {
                throw new RelatorioControllerException("mensagem.informe.relatorio.papel", responsavel);
            }

            // Se o nenhum filtro foi informado, inicializa os filtros e a ordenação
            if (filtros == null) {
                filtros = new HashMap<>();
            }
            if (ordenacaoFiltros == null) {
                ordenacaoFiltros = new HashMap<>();
            }

            try {
                final Relatorio relatorio = RelatorioHome.findByPrimaryKey(relCodigo);
                if (relatorio != null) {
                    throw new RelatorioControllerException("mensagem.erro.existe.relatorio.cadastrado.codigo.informado", responsavel);
                }
            } catch (final FindException e) {
            }

            // Se a descrição da função não foi informada, utiliza o título como descrição
            if (TextHelper.isNull(funDescricao)) {
                funDescricao = relTitulo;
            }
            // Se a descrição do item menu não foi informada, utiliza o título como descrição
            if (TextHelper.isNull(itmDescricao)) {
                itmDescricao = relTitulo;
            }

            // Insere função e papel função
            final String funCodigo = usuarioController.createFuncao(CodedValues.GRUPO_FUNCAO_RELATORIOS, funDescricao, CodedValues.TPC_NAO, CodedValues.TPC_NAO, CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM, CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM, CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM, CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM, CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM, CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO, CodedValues.TPC_NAO, papCodigos, responsavel);

            // Incluir Item Menu
            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.ITM_CODIGO_PAI, null);
            criterio.setAttribute(Columns.ITM_DESCRICAO, itmDescricao);
            criterio.setAttribute(Columns.MNU_CODIGO, CodedValues.MENU_RELATORIOS);
            criterio.setAttribute(Columns.ITM_SEPARADOR, null);
            final ObtemProxItmSequenciaQuery query = new ObtemProxItmSequenciaQuery();
            Short itmSequencia = Short.valueOf(query.executarLista().get(0).toString());
            if (itmSequencia.compareTo(Short.valueOf("10000")) < 0) {
                itmSequencia = Short.valueOf("10000");
            }
            criterio.setAttribute(Columns.ITM_SEQUENCIA, itmSequencia);
            final String itmCodigo = menuController.createItemMenu(criterio, responsavel);

            for (final String papCodigo : papCodigos) {
                // Inclui acesso recurso para listagem do relatório
                parametroController.createAcessoRecurso(funCodigo, papCodigo, "/v3/listarRelatorio", "tipo", relCodigo, CodedValues.TPC_SIM, CodedValues.TPC_SIM, CodedValues.STS_ATIVO, CodedValues.TPC_NAO, itmCodigo, responsavel);
                if (!CodedValues.TPC_SIM.equals(relAgendado)) {
                    // Inclui acesso recurso para processamento do relatório
                    parametroController.createAcessoRecurso(funCodigo, papCodigo, "/v3/executarRelatorio", "tipoRelatorio", relCodigo, CodedValues.TPC_SIM, CodedValues.TPC_SIM, CodedValues.STS_ATIVO, CodedValues.TPC_NAO, null, responsavel);
                }
                // Inclui acesso recurso para exclusão de arquivos
                parametroController.createAcessoRecurso(funCodigo, papCodigo, "/v3/excluirArquivo", "subtipo", relCodigo, CodedValues.TPC_SIM, CodedValues.TPC_SIM, CodedValues.STS_ATIVO, CodedValues.TPC_NAO, null, responsavel);
                // Inclui acesso recurso para download de arquivos
                parametroController.createAcessoRecurso(funCodigo, papCodigo, "/v3/downloadArquivo", "subtipo", relCodigo, CodedValues.TPC_SIM, CodedValues.TPC_SIM, CodedValues.STS_ATIVO, CodedValues.TPC_NAO, null, responsavel);

                if (CodedValues.TPC_SIM.equals(relAgendado)) {
                    // Inclui acesso recurso para agendamento de relatório
                    parametroController.createAcessoRecurso(funCodigo, papCodigo, "/v3/agendarRelatorio", "tipo", relCodigo, CodedValues.TPC_SIM, CodedValues.TPC_SIM, CodedValues.STS_ATIVO, CodedValues.TPC_SIM, null, responsavel);
                    // Inclui acesso recurso para cancelamento de agendamento de relatório
                    parametroController.createAcessoRecurso(funCodigo, papCodigo, "/v3/cancelarAgendamentoRelatorio", "tipo", relCodigo, CodedValues.TPC_SIM, CodedValues.TPC_SIM, CodedValues.STS_ATIVO, CodedValues.TPC_NAO, null, responsavel);
                }
            }

            // Insere Relatório
            if (TextHelper.isNull(relAgendado)) {
                relAgendado = CodedValues.TPC_NAO;
            }
            final String relClasseRelatorio = RelatorioEditavel.class.getName();
            String relClasseAgendamento = null;
            // Se relatório é agendado, seta o job que executará o processamento
            if (CodedValues.TPC_SIM.equals(relAgendado)) {
                relClasseAgendamento = RelatorioEditavelJob.class.getName();
            }
            final String relClasseProcesso = ProcessaRelatorioEditavel.class.getName();
            String relTemplateDinamico = "";
            if (TextHelper.isNull(relTemplateJasper)) {
                relTemplateJasper = CodedValues.TEMPLATE_REL_EDITAVEL_JASPER;
                relTemplateDinamico = CodedValues.TEMPLATE_REL_EDITAVEL_JRXML;
            }
            final String relTemplateSubrelatorio = null;
            final Short relQtdDiasLimpeza = Short.valueOf("30");
            final String relCustomizado = CodedValues.TPC_SIM;

            // Valida os filtros do relatório e inclui filtros obrigatórios
            verificaFiltrosRelEdt(filtros, ordenacaoFiltros, papCodigos, relTemplateSql, relAgendado);

            insereRelatorio(relCodigo, funCodigo, null, relTitulo, CodedValues.STS_ATIVO, relAgendado, relClasseRelatorio, relClasseProcesso, relClasseAgendamento, relTemplateJasper, relTemplateDinamico, relTemplateSubrelatorio, relTemplateSql, relQtdDiasLimpeza, relCustomizado, relAgrupamento, filtros, ordenacaoFiltros, responsavel);

            return relCodigo;
        } catch (NumberFormatException | UsuarioControllerException | ParametroControllerException | MenuControllerException | HQueryException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        } catch (final RelatorioControllerException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    private void verificaFiltrosRelEdt(Map<String, Map<String, String>> filtros, Map<String, Integer> ordenacaoFiltros, List<String> papCodigos, String relTemplateSql, String relAgendado) throws RelatorioControllerException {
        // Insere o campo formato relatório como filtro
        final String tfrCampoCsa = "campo_csa";
        final String tfrCampoCor = "campo_cor";
        final String tfrCampoOrg = "campo_org";
        if (papCodigos.contains(CodedValues.PAP_CONSIGNATARIA) && !filtros.containsKey(tfrCampoCsa)) {
            incluiFiltroParametro(tfrCampoCsa, filtros, papCodigos, CodedValues.REL_FILTRO_OBRIGATORIO);
            incluiOrdemFiltroParametro(tfrCampoCsa, ordenacaoFiltros);
        } else if (papCodigos.contains(CodedValues.PAP_CORRESPONDENTE) && !filtros.containsKey(tfrCampoCor)) {
            incluiFiltroParametro(tfrCampoCor, filtros, papCodigos, CodedValues.REL_FILTRO_OBRIGATORIO);
            incluiOrdemFiltroParametro(tfrCampoCor, ordenacaoFiltros);
        } else if (papCodigos.contains(CodedValues.PAP_ORGAO) && !filtros.containsKey(tfrCampoOrg)) {
            incluiFiltroParametro(tfrCampoOrg, filtros, papCodigos, CodedValues.REL_FILTRO_OBRIGATORIO);
            incluiOrdemFiltroParametro(tfrCampoOrg, ordenacaoFiltros);
        }

        for (final String filtroQuery : filtros.keySet()) {
            if (relTemplateSql.indexOf(filtroQuery) < 0) {
                throw new RelatorioControllerException("mensagem.informe.filtro.arg0.query", (AcessoSistema) null, filtroQuery);
            }
        }

        // Verifica se todos os filtros incluídos na query foram selecionados
        final Pattern pattern = Pattern.compile("<@[A-Za-z0-9_\\-\\.]+>");
        final Matcher matcher = pattern.matcher(relTemplateSql);
        while (matcher.find()) {
            String match = relTemplateSql.subSequence(matcher.start(), matcher.end()).toString().replace("<@", "").replace(">", "");

            if ("campo_data_inclusao_ini".equals(match) || "campo_data_inclusao_fim".equals(match)) {
                match = "campo_data_inclusao";
            }

            if (filtros.get(match) == null) {
                throw new RelatorioControllerException("mensagem.informe.filtro.arg0.filtros.relatorio", (AcessoSistema) null, match);
            }
        }

        // Insere campos obrigatórios para o agendamento do relatório
        if (CodedValues.TPC_SIM.equals(relAgendado)) {
            // Insere campo_data_execucao
            final String tfrCampoDataExecucaoRelatorio = "campo_data_execucao";
            incluiFiltroParametro(tfrCampoDataExecucaoRelatorio, filtros, papCodigos, CodedValues.REL_FILTRO_OBRIGATORIO);
            incluiOrdemFiltroParametro(tfrCampoDataExecucaoRelatorio, ordenacaoFiltros);

            // Insere campo_tipo_agendamento
            final String tfrCampoTipoAgendamentoRelatorio = "campo_tipo_agendamento";
            incluiFiltroParametro(tfrCampoTipoAgendamentoRelatorio, filtros, papCodigos, CodedValues.REL_FILTRO_OBRIGATORIO);
            incluiOrdemFiltroParametro(tfrCampoTipoAgendamentoRelatorio, ordenacaoFiltros);

            // Insere campo_periodicidade
            final String tfrCampoPeriodicidadeRelatorio = "campo_periodicidade";
            incluiFiltroParametro(tfrCampoPeriodicidadeRelatorio, filtros, papCodigos, CodedValues.REL_FILTRO_OBRIGATORIO);
            incluiOrdemFiltroParametro(tfrCampoPeriodicidadeRelatorio, ordenacaoFiltros);
        }

        // Insere o campo formato relatório como filtro
        final String tfrCampoFormatoRelatorio = "campo_formato_relatorio";
        incluiFiltroParametro(tfrCampoFormatoRelatorio, filtros, papCodigos, CodedValues.REL_FILTRO_OBRIGATORIO);
        incluiOrdemFiltroParametro(tfrCampoFormatoRelatorio, ordenacaoFiltros);
    }

    private void incluiOrdemFiltroParametro(String tfrCampoFormatoRelatorio, Map<String, Integer> ordenacaoFiltros) {
        ordenacaoFiltros.put(tfrCampoFormatoRelatorio, ordenacaoFiltros.size() + 1);
    }

    private void incluiFiltroParametro(String tfrCampoFormatoRelatorio, Map<String, Map<String, String>> filtros, List<String> papCodigos, String obrigatoriedade) {
        final Map<String, String> papeis = new HashMap<>();
        for (final String element : papCodigos) {
            papeis.put(element, obrigatoriedade);
        }
        filtros.put(tfrCampoFormatoRelatorio, papeis);
    }

    private String insereRelatorio(String relCodigo, String funCodigo, String tagCodigo, String relTitulo, Short relAtivo, String relAgendado, String relClasseRelatorio, String relClasseProcesso, String relClasseAgendamento, String relTemplateJasper, String relTemplateDinamico, String relTemplateSubrelatorio, String relTemplateSql, Short relQtdDiasLimpeza, String relCustomizado, String relAgrupamento, Map<String, Map<String, String>> filtros, Map<String, Integer> ordenacaoFiltros, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            try {
                final Relatorio relatorio = RelatorioHome.findByPrimaryKey(relCodigo);
                if (relatorio != null) {
                    throw new RelatorioControllerException("mensagem.erro.existe.relatorio.cadastrado.codigo.informado", responsavel);
                }
            } catch (final FindException e) {
            }

            RelatorioHome.create(relCodigo, funCodigo, tagCodigo, relTitulo, relAtivo, relAgendado, relClasseRelatorio, relClasseProcesso, relClasseAgendamento, relTemplateJasper, relTemplateDinamico, relTemplateSubrelatorio, relTemplateSql, relQtdDiasLimpeza, relCustomizado, relAgrupamento);

            // Insere filtros
            insereFiltrosRelatorio(relCodigo, filtros, ordenacaoFiltros);

            final LogDelegate log = new LogDelegate(responsavel, Log.RELATORIO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setRelatorio(relCodigo);
            log.write();

            return relCodigo;
        } catch (final com.zetra.econsig.exception.CreateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        } catch (final LogControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public void alterarStatusRelatorio(String relCodigo, Short relAtivo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final Relatorio relatorio = RelatorioHome.findByPrimaryKey(relCodigo);
            relatorio.setRelAtivo(relAtivo);
            AbstractEntityHome.update(relatorio);

            final LogDelegate log = new LogDelegate(responsavel, Log.RELATORIO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage(relAtivo.equals(CodedValues.STS_ATIVO) ? "mensagem.informacao.desbloqueia.relatorio" : "mensagem.informacao.bloqueia.relatorio", responsavel).toUpperCase());
            log.setRelatorio(relCodigo);
            log.write();
        } catch (LogControllerException | FindException e) {
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        } catch (final UpdateException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public void edtRelEditavel(TransferObject to, List<String> papCodigos, Map<String, Map<String, String>> filtros, Map<String, Integer> ordenacaoFiltros, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final String relTemplateJasper = (String) to.getAttribute(Columns.REL_TEMPLATE_JASPER);

            if (!TextHelper.isNull(relTemplateJasper)) {
                to.setAttribute(Columns.REL_TEMPLATE_JASPER, relTemplateJasper);
                if (CodedValues.TEMPLATE_REL_EDITAVEL_JASPER.equals(relTemplateJasper)) {
                    to.setAttribute(Columns.REL_TEMPLATE_DINAMICO, CodedValues.TEMPLATE_REL_EDITAVEL_JRXML);
                } else {
                    to.setAttribute(Columns.REL_TEMPLATE_DINAMICO, null);
                }
            }

            // Se o nenhum filtro foi informado, inicializa os filtros e a ordenação
            if (filtros == null) {
                filtros = new HashMap<>();
            }
            if (ordenacaoFiltros == null) {
                ordenacaoFiltros = new HashMap<>();
            }

            // Valida os filtros do relatório e inclui filtros obrigatórios
            final String relTemplateSql = to.getAttribute(Columns.REL_TEMPLATE_SQL).toString();
            final String relAgendado = !TextHelper.isNull(to.getAttribute(Columns.REL_AGENDADO)) ? to.getAttribute(Columns.REL_AGENDADO).toString() : CodedValues.TPC_NAO;
            verificaFiltrosRelEdt(filtros, ordenacaoFiltros, papCodigos, relTemplateSql, relAgendado);

            // Se relatório é agendado, seta o job que executará o processamento
            if (CodedValues.TPC_SIM.equals(relAgendado)) {
                to.setAttribute(Columns.REL_CLASSE_AGENDAMENTO, RelatorioEditavelJob.class.getName());
            }

            // Edita relatório
            final String relCodigo = updateRelatorio(to, filtros, ordenacaoFiltros, responsavel);

            // Edita item menu
            final CustomTransferObject itemMenu = new CustomTransferObject();
            final String itmCodigo = to.getAttribute(Columns.ITM_CODIGO).toString();
            itemMenu.setAttribute(Columns.ITM_CODIGO, itmCodigo);
            if (!TextHelper.isNull(to.getAttribute(Columns.ITM_DESCRICAO))) {
                itemMenu.setAttribute(Columns.ITM_DESCRICAO, to.getAttribute(Columns.ITM_DESCRICAO).toString());
            }
            menuController.updateItemMenu(itemMenu, responsavel);

            // Edita função
            final TransferObject funcaoTO = new CustomTransferObject();
            final String funCodigo = to.getAttribute(Columns.REL_FUN_CODIGO).toString();
            funcaoTO.setAttribute(Columns.FUN_CODIGO, to.getAttribute(Columns.REL_FUN_CODIGO).toString());
            funcaoTO.setAttribute(Columns.FUN_DESCRICAO, to.getAttribute(Columns.FUN_DESCRICAO).toString());
            usuarioController.updateFuncao(funcaoTO, papCodigos, responsavel);

            // Remove acessos recursos para os papeis
            parametroController.removeAcessoRecursoByFunCodigo(funCodigo, responsavel);
            for (final String papCodigo : papCodigos) {
                // Inclui acesso recurso para listagem do relatório
                parametroController.createAcessoRecurso(funCodigo, papCodigo, "/v3/listarRelatorio", "tipo", relCodigo, CodedValues.TPC_SIM, CodedValues.TPC_SIM, CodedValues.STS_ATIVO, CodedValues.TPC_NAO, itmCodigo, responsavel);
                if (!CodedValues.TPC_SIM.equals(relAgendado)) {
                    // Inclui acesso recurso para processamento do relatório
                    parametroController.createAcessoRecurso(funCodigo, papCodigo, "/v3/executarRelatorio", "tipoRelatorio", relCodigo, CodedValues.TPC_SIM, CodedValues.TPC_SIM, CodedValues.STS_ATIVO, CodedValues.TPC_NAO, null, responsavel);
                }
                // Inclui acesso recurso para exclusão de arquivos
                parametroController.createAcessoRecurso(funCodigo, papCodigo, "/v3/excluirArquivo", "subtipo", relCodigo, CodedValues.TPC_SIM, CodedValues.TPC_SIM, CodedValues.STS_ATIVO, CodedValues.TPC_NAO, null, responsavel);
                // Inclui acesso recurso para download de arquivos
                parametroController.createAcessoRecurso(funCodigo, papCodigo, "/v3/downloadArquivo", "subtipo", relCodigo, CodedValues.TPC_SIM, CodedValues.TPC_SIM, CodedValues.STS_ATIVO, CodedValues.TPC_NAO, null, responsavel);

                if (CodedValues.TPC_SIM.equals(relAgendado)) {
                    // Inclui acesso recurso para agendamento de relatório
                    parametroController.createAcessoRecurso(funCodigo, papCodigo, "/v3/agendarRelatorio", "tipo", relCodigo, CodedValues.TPC_SIM, CodedValues.TPC_SIM, CodedValues.STS_ATIVO, CodedValues.TPC_SIM, null, responsavel);
                    // Inclui acesso recurso para cancelamento de agendamento de relatório
                    parametroController.createAcessoRecurso(funCodigo, papCodigo, "/v3/cancelarAgendamentoRelatorio", "tipo", relCodigo, CodedValues.TPC_SIM, CodedValues.TPC_SIM, CodedValues.STS_ATIVO, CodedValues.TPC_NAO, null, responsavel);
                }
            }
        } catch (ParametroControllerException | UsuarioControllerException | MenuControllerException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        } catch (final RelatorioControllerException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    private String updateRelatorio(TransferObject to, Map<String, Map<String, String>> filtros, Map<String, Integer> ordenacaoFiltros, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final String relCodigo = to.getAttribute(Columns.REL_CODIGO).toString();
            final Relatorio relatorio = RelatorioHome.findByPrimaryKey(relCodigo);

            final LogDelegate log = new LogDelegate(responsavel, Log.RELATORIO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setRelatorio(relCodigo);

            /* Compara a versão do cache com a passada por parâmetro */
            final TransferObject cache = findRelatorio(relCodigo, responsavel);
            final CustomTransferObject merge = log.getUpdatedFields(to.getAtributos(), cache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.REL_TITULO)) {
                relatorio.setRelTitulo((String) merge.getAttribute(Columns.REL_TITULO));
            }
            if (merge.getAtributos().containsKey(Columns.REL_ATIVO)) {
                relatorio.setRelAtivo((Short) merge.getAttribute(Columns.REL_ATIVO));
            }
            if (merge.getAtributos().containsKey(Columns.REL_AGENDADO)) {
                relatorio.setRelAgendado((String) merge.getAttribute(Columns.REL_AGENDADO));
            }
            if (merge.getAtributos().containsKey(Columns.REL_CLASSE_RELATORIO)) {
                relatorio.setRelClasseRelatorio((String) merge.getAttribute(Columns.REL_CLASSE_RELATORIO));
            }
            if (merge.getAtributos().containsKey(Columns.REL_CLASSE_PROCESSO)) {
                relatorio.setRelClasseProcesso((String) merge.getAttribute(Columns.REL_CLASSE_PROCESSO));
            }
            if (merge.getAtributos().containsKey(Columns.REL_CLASSE_AGENDAMENTO)) {
                relatorio.setRelClasseAgendamento((String) merge.getAttribute(Columns.REL_CLASSE_AGENDAMENTO));
            }
            if (merge.getAtributos().containsKey(Columns.REL_TEMPLATE_JASPER) && !TextHelper.isNull(merge.getAttribute(Columns.REL_TEMPLATE_JASPER))) {
                relatorio.setRelTemplateJasper((String) merge.getAttribute(Columns.REL_TEMPLATE_JASPER));
            }
            if (merge.getAtributos().containsKey(Columns.REL_TEMPLATE_DINAMICO)) {
                relatorio.setRelTemplateDinamico((String) merge.getAttribute(Columns.REL_TEMPLATE_DINAMICO));
            }
            if (merge.getAtributos().containsKey(Columns.REL_TEMPLATE_SUBRELATORIO)) {
                relatorio.setRelTemplateSubrelatorio((String) merge.getAttribute(Columns.REL_TEMPLATE_SUBRELATORIO));
            }
            if (merge.getAtributos().containsKey(Columns.REL_TEMPLATE_SQL)) {
                relatorio.setRelTemplateSql((String) merge.getAttribute(Columns.REL_TEMPLATE_SQL));
            }
            if (merge.getAtributos().containsKey(Columns.REL_QTD_DIAS_LIMPEZA)) {
                relatorio.setRelQtdDiasLimpeza((Short) merge.getAttribute(Columns.REL_QTD_DIAS_LIMPEZA));
            }
            if (merge.getAtributos().containsKey(Columns.REL_CUSTOMIZADO)) {
                relatorio.setRelCustomizado((String) merge.getAttribute(Columns.REL_CUSTOMIZADO));
            }
            if (merge.getAtributos().containsKey(Columns.REL_AGRUPAMENTO)) {
                relatorio.setRelAgrupamento((String) merge.getAttribute(Columns.REL_AGRUPAMENTO));
            }
            // Editar relatório
            AbstractEntityHome.update(relatorio);

            // Insere filtros
            insereFiltrosRelatorio(relCodigo, filtros, ordenacaoFiltros);

            log.write();
            return relCodigo;

        } catch (FindException | UpdateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        } catch (final LogControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    private void insereFiltrosRelatorio(String relCodigo, Map<String, Map<String, String>> filtros, Map<String, Integer> ordenacaoFiltros) throws RelatorioControllerException {
        try {
            // <tfrCodigo, <papCodigo, rfiExibe>>
            if ((filtros != null) && !filtros.isEmpty()) {
                // Remove antigos filtros do relatório
                removeRelatorioFiltros(relCodigo);

                for (final String tfrCodigo : filtros.keySet()) {
                    String rfiExibeCse = "0";
                    String rfiExibeOrg = "0";
                    String rfiExibeCsa = "0";
                    String rfiExibeCor = "0";
                    String rfiExibeSer = "0";
                    String rfiExibeSup = "0";
                    final String rfiParametro = "0";
                    final Integer rfiSequencia = ordenacaoFiltros.get(tfrCodigo);
                    final Map<String, String> papeis = filtros.get(tfrCodigo);
                    for (final String papCodigo : papeis.keySet()) {
                        if (CodedValues.PAP_CONSIGNANTE.equals(papCodigo)) {
                            rfiExibeCse = papeis.get(papCodigo);
                        } else if (CodedValues.PAP_CONSIGNATARIA.equals(papCodigo)) {
                            rfiExibeCsa = papeis.get(papCodigo);
                        } else if (CodedValues.PAP_ORGAO.equals(papCodigo)) {
                            rfiExibeOrg = papeis.get(papCodigo);
                        } else if (CodedValues.PAP_CORRESPONDENTE.equals(papCodigo)) {
                            rfiExibeCor = papeis.get(papCodigo);
                        } else if (CodedValues.PAP_SERVIDOR.equals(papCodigo)) {
                            rfiExibeSer = papeis.get(papCodigo);
                        } else if (CodedValues.PAP_SUPORTE.equals(papCodigo)) {
                            rfiExibeSup = papeis.get(papCodigo);
                        }
                    }
                    if (!"0".equals(rfiExibeCse) || !"0".equals(rfiExibeOrg) || !"0".equals(rfiExibeCsa) || !"0".equals(rfiExibeCor) || !"0".equals(rfiExibeSer) || !"0".equals(rfiExibeSup)) {
                        RelatorioFiltroHome.create(relCodigo, tfrCodigo, rfiExibeCse, rfiExibeOrg, rfiExibeCsa, rfiExibeCor, rfiExibeSer, rfiExibeSup, rfiParametro, rfiSequencia.shortValue());
                    }
                }
            }
        } catch (final com.zetra.econsig.exception.CreateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, e);
        }
    }

    @Override
    public void removeRelEditavel(String relCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final Relatorio relatorio = RelatorioHome.findByPrimaryKey(relCodigo);
            final String templateJasper = relatorio.getRelTemplateJasper();
            final String funCodigo = relatorio.getFuncao().getFunCodigo();

            // Remove acessos recurso
            parametroController.removeAcessoRecursoByFunCodigo(funCodigo, responsavel);

            // Remove item menu
            try {
                final AcessoRecurso acesso = AcessoRecursoHome.findItmCodigoByFunCodigo(funCodigo);
                menuController.removeItemMenu(acesso.getItemMenu().getItmCodigo(), responsavel);
            } catch (final FindException ex) {
                // Não encontrou item de menu, então não previsa remover nada
            }

            // Remove relatório
            removeRelatorio(relCodigo);

            // Remove função
            usuarioController.removeFuncao(funCodigo, responsavel);

            // Remove o template do relatório editável, caso exista
            try {
                final String diretorioRaiz = ParamSist.getDiretorioRaizArquivos();
                if (!TextHelper.isNull(templateJasper) && templateJasper.contains(diretorioRaiz)) {
                    FileHelper.delete(templateJasper);
                }
            } catch (final IOException e) {
                LOG.warn("Não foi possível excluir o template do Relatório Editável: '" + relCodigo + "'.", e);
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.RELATORIO, Log.DELETE, Log.LOG_INFORMACAO);
            log.setRelatorio(relCodigo);
            log.write();

        } catch (final ZetraException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void removeRelatorio(String relCodigo) throws RelatorioControllerException {
        try {
            final Relatorio relatorio = RelatorioHome.findByPrimaryKey(relCodigo);

            // Remove filtros do relatório
            removeRelatorioFiltros(relCodigo);

            // Remove relatório
            AbstractEntityHome.remove(relatorio);
        } catch (FindException | RemoveException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, e);
        }
    }

    private void removeRelatorioFiltros(String relCodigo) throws RelatorioControllerException {
        try {
            final Collection<RelatorioFiltro> relFiltros = RelatorioFiltroHome.findByRelCodigo(relCodigo);
            if ((relFiltros != null) && !relFiltros.isEmpty()) {
                for (final RelatorioFiltro element : relFiltros) {
                    AbstractEntityHome.remove(element);
                }
            }
        } catch (FindException | RemoveException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, e);
        }
    }

    @Override
    public TransferObject findRelEditavel(String relCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final Relatorio relatorio = RelatorioHome.findByPrimaryKey(relCodigo);
            final String funCodigo = relatorio.getFuncao().getFunCodigo();
            final Funcao funcao = FuncaoHome.findByPrimaryKey(funCodigo);
            final String tagCodigo = !TextHelper.isNull(relatorio.getTipoAgendamento()) ? relatorio.getTipoAgendamento().getTagCodigo() : null;
            final AcessoRecurso acessoItm = AcessoRecursoHome.findItmCodigoByFunCodigo(funCodigo);
            final ItemMenu itemMenu = ItemMenuHome.findByPrimaryKey(acessoItm.getItemMenu().getItmCodigo());

            final List<String> papeis = findRelatorioPapel(funCodigo, responsavel);
            final Map<String, TransferObject> relatorioFiltros = findRelatorioFiltro(relCodigo, responsavel);

            final TransferObject retorno = findRelatorio(relCodigo, responsavel);
            if (!TextHelper.isNull(tagCodigo)) {
                final TipoAgendamento tipoAgendamento = TipoAgendamentoHome.findByPrimaryKey(tagCodigo);
                retorno.setAttribute(Columns.TAG_DESCRICAO, tipoAgendamento.getTagDescricao());
            }
            retorno.setAttribute(Columns.FUN_DESCRICAO, funcao.getFunDescricao());
            retorno.setAttribute(Columns.ITM_CODIGO, itemMenu.getItmCodigo());
            retorno.setAttribute(Columns.ITM_DESCRICAO, itemMenu.getItmDescricao());
            retorno.setAttribute("FILTRO_RELATORIO", relatorioFiltros);
            retorno.setAttribute("PAPEIS", papeis);

            final LogDelegate log = new LogDelegate(responsavel, Log.RELATORIO, Log.FIND, Log.LOG_INFORMACAO);
            log.setRelatorio(relCodigo);
            log.write();

            return retorno;
        } catch (LogControllerException | FindException e) {
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    private TransferObject findRelatorio(String relCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final Relatorio relatorio = RelatorioHome.findByPrimaryKey(relCodigo);
            final TransferObject retorno = new CustomTransferObject();
            retorno.setAttribute(Columns.REL_CODIGO, relatorio.getRelCodigo());
            retorno.setAttribute(Columns.REL_AGENDADO, relatorio.getRelAgendado());
            retorno.setAttribute(Columns.REL_AGRUPAMENTO, relatorio.getRelAgrupamento());
            retorno.setAttribute(Columns.REL_ATIVO, relatorio.getRelAtivo());
            retorno.setAttribute(Columns.REL_CLASSE_AGENDAMENTO, relatorio.getRelClasseAgendamento());
            retorno.setAttribute(Columns.REL_CLASSE_PROCESSO, relatorio.getRelClasseProcesso());
            retorno.setAttribute(Columns.REL_CLASSE_RELATORIO, relatorio.getRelClasseRelatorio());
            retorno.setAttribute(Columns.REL_CUSTOMIZADO, relatorio.getRelCustomizado());
            retorno.setAttribute(Columns.REL_FUN_CODIGO, relatorio.getFuncao().getFunCodigo());
            retorno.setAttribute(Columns.REL_QTD_DIAS_LIMPEZA, relatorio.getRelQtdDiasLimpeza());
            retorno.setAttribute(Columns.REL_TAG_CODIGO, relatorio.getTipoAgendamento() != null ? relatorio.getTipoAgendamento().getTagCodigo() : null);
            retorno.setAttribute(Columns.REL_TEMPLATE_DINAMICO, relatorio.getRelTemplateDinamico());
            retorno.setAttribute(Columns.REL_TEMPLATE_JASPER, relatorio.getRelTemplateJasper());
            retorno.setAttribute(Columns.REL_TEMPLATE_SQL, relatorio.getRelTemplateSql());
            retorno.setAttribute(Columns.REL_TEMPLATE_SUBRELATORIO, relatorio.getRelTemplateSubrelatorio());
            retorno.setAttribute(Columns.REL_TITULO, relatorio.getRelTitulo());
            return retorno;
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    private List<String> findRelatorioPapel(String funCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<String> papeis = new ArrayList<>();
            final Collection<PapelFuncao> papeisFuncao = PapelFuncaoHome.findByFunCodigo(funCodigo);
            if (papeisFuncao != null) {
                for (final PapelFuncao papelFuncao : papeisFuncao) {
                    papeis.add(papelFuncao.getPapCodigo());
                }
            }
            return papeis;
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public Map<String, TransferObject> findRelatorioFiltro(String relCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final Collection<RelatorioFiltro> filtros = RelatorioFiltroHome.findByRelCodigo(relCodigo);
            final Map<String, TransferObject> relatorioFiltros = new HashMap<>();
            if ((filtros != null) && !filtros.isEmpty()) {
                for (final RelatorioFiltro filtro : filtros) {
                    final TransferObject to = new CustomTransferObject();
                    final String tfrCodigo = filtro.getTfrCodigo();
                    final TipoFiltroRelatorio tipoFiltroRelatorio = TipoFiltroRelatorioHome.findByPrimaryKey(tfrCodigo);
                    to.setAttribute(Columns.RFI_REL_CODIGO, filtro.getRelCodigo());
                    to.setAttribute(Columns.TFR_CODIGO, tfrCodigo);
                    to.setAttribute(Columns.TFR_DESCRICAO, tipoFiltroRelatorio.getTfrDescricao());
                    to.setAttribute(Columns.RFI_EXIBE_CSE, filtro.getRfiExibeCse());
                    to.setAttribute(Columns.RFI_EXIBE_ORG, filtro.getRfiExibeOrg());
                    to.setAttribute(Columns.RFI_EXIBE_CSA, filtro.getRfiExibeCsa());
                    to.setAttribute(Columns.RFI_EXIBE_COR, filtro.getRfiExibeCor());
                    to.setAttribute(Columns.RFI_EXIBE_SER, filtro.getRfiExibeSer());
                    to.setAttribute(Columns.RFI_EXIBE_SUP, filtro.getRfiExibeSup());
                    to.setAttribute(Columns.RFI_PARAMETRO, filtro.getRfiParametro());
                    to.setAttribute(Columns.RFI_SEQUENCIA, filtro.getRfiSequencia());
                    relatorioFiltros.put(tfrCodigo, to);
                }
            }
            return relatorioFiltros;
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<GerencialTaxasBean> lstRakingTaxas(String svcCodigo, TransferObject criterio, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioGerencialGeralTaxasQuery taxasQuery = new RelatorioGerencialGeralTaxasQuery(svcCodigo);
            taxasQuery.maxPrazo = (int) criterio.getAttribute("maxPrazo");

            final List<TransferObject> taxasCto = taxasQuery.executarDTO();
            List<GerencialTaxasBean> taxas = null;

            if ((taxasCto != null) && !taxasCto.isEmpty()) {
                TransferObject to = null;
                GerencialTaxasBean taxaBean = null;
                taxas = new ArrayList<>();
                for (final TransferObject element : taxasCto) {
                    to = element;
                    taxaBean = new GerencialTaxasBean();
                    taxaBean.setConsignataria((String) to.getAttribute("CONSIGNATARIA"));
                    taxaBean.setVlr12((BigDecimal) to.getAttribute("cft_vlr12"));
                    taxaBean.setVlr24((BigDecimal) to.getAttribute("cft_vlr24"));
                    taxaBean.setVlr36((BigDecimal) to.getAttribute("cft_vlr36"));
                    taxaBean.setVlr48((BigDecimal) to.getAttribute("cft_vlr48"));
                    taxaBean.setVlr60((BigDecimal) to.getAttribute("cft_vlr60"));
                    taxaBean.setVlr72((BigDecimal) to.getAttribute("cft_vlr72"));
                    taxaBean.setVlr84((BigDecimal) to.getAttribute("cft_vlr84"));
                    taxaBean.setVlr96((BigDecimal) to.getAttribute("cft_vlr96"));
                    taxaBean.setVlr108((BigDecimal) to.getAttribute("cft_vlr108"));
                    taxaBean.setVlr120((BigDecimal) to.getAttribute("cft_vlr120"));
                    taxaBean.setVlr132((BigDecimal) to.getAttribute("cft_vlr132"));
                    taxaBean.setVlr144((BigDecimal) to.getAttribute("cft_vlr144"));
                    taxaBean.setMaxPrazo(criterio.getAttribute("maxPrazo").toString());
                    taxas.add(taxaBean);
                }
            }

            return taxas;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject buscaMaxPrazo(AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioGerencialGeralPrazoQuery prazoQuery = new RelatorioGerencialGeralPrazoQuery();
            TransferObject retorno = null;
            final List<TransferObject> prazoMax = prazoQuery.executarDTO();

            if ((prazoMax != null) && !prazoMax.isEmpty()) {
                retorno = prazoMax.iterator().next();
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject buscaSvcTaxasQuery(boolean internacional, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            TransferObject retorno = null;
            final RelatorioGerencialGeralBuscaSvcTaxasQuery svcQuery = new RelatorioGerencialGeralBuscaSvcTaxasQuery();
            svcQuery.internacional = internacional;

            final List<TransferObject> svc = svcQuery.executarDTO();

            if ((svc != null) && !svc.isEmpty()) {
                retorno = svc.iterator().next();
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<GerencialGeralTaxasEfetivasBean> lstTaxasEfetivas(String svcCodigo, Date periodo, List<Integer> prazos, AcessoSistema responsavel) throws RelatorioControllerException {
        try {

            final Map<String, BigDecimal> taxasMinimas = new HashMap<>();
            final Map<String, BigDecimal> taxasMaximas = new HashMap<>();
            final Map<String, BigDecimal> taxasMedias = new HashMap<>();

            final List<String> svcCodigos = new ArrayList<>();
            svcCodigos.add(svcCodigo.toString());

            // Parametros de sistema necessários
            final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

            BigDecimal taxaMinima = null;
            BigDecimal taxaMaxima = null;
            BigDecimal taxa = null;

            try {
                ParamSvcTO paramSvcCse = null;
                BigDecimal soma = new BigDecimal(0);
                int nroTaxas = 0;
                String csaCodigo = "";
                final List<TransferObject> lista = lstTaxasEfetivasContratos(periodo.toString(), null, svcCodigos, null, false, prazos, csaCodigo, responsavel);
                if ((lista != null) && !lista.isEmpty()) {
                    final Iterator<TransferObject> it = lista.iterator();
                    while (it.hasNext()) {
                        final CustomTransferObject ade = (CustomTransferObject) it.next();
                        if (!csaCodigo.equals(ade.getAttribute(Columns.CSA_CODIGO).toString())) {
                            if (nroTaxas > 0) {
                                taxasMedias.put(csaCodigo, soma.divide(new BigDecimal(nroTaxas), 2, java.math.RoundingMode.HALF_UP));
                            }
                            csaCodigo = ade.getAttribute(Columns.CSA_CODIGO).toString();
                            taxaMinima = null;
                            taxaMaxima = null;
                            svcCodigo = "";
                            soma = new BigDecimal(0);
                            nroTaxas = 0;
                        }
                        if (!svcCodigo.equals(ade.getAttribute(Columns.SVC_CODIGO).toString())) {
                            svcCodigo = ade.getAttribute(Columns.SVC_CODIGO).toString();
                            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                        }

                        taxa = SimulacaoHelper.calcularTaxaJuros(ade, simulacaoPorTaxaJuros, paramSvcCse, ade.getAttribute(Columns.ORG_CODIGO).toString(), responsavel);
                        if (taxa != null) {
                            soma = soma.add(taxa);
                            nroTaxas++;
                            if ((taxaMinima == null) || (taxa.compareTo(taxaMinima) == -1)) {
                                taxaMinima = taxa;
                            }
                            if ((taxaMaxima == null) || (taxa.compareTo(taxaMaxima) >= 0)) {
                                taxaMaxima = taxa;
                            }
                        }
                        taxasMinimas.put(csaCodigo, taxaMinima);
                        taxasMaximas.put(csaCodigo, taxaMaxima);
                    }
                }
                if (nroTaxas > 0) {
                    taxasMedias.put(csaCodigo, soma.divide(new BigDecimal(nroTaxas), 2, java.math.RoundingMode.HALF_UP));
                }
            } catch (final Exception ex) {
                throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }

            final RelatorioTaxasEfetivasQuery query = new RelatorioTaxasEfetivasQuery();
            query.periodo = periodo.toString();
            query.svcCodigos = svcCodigos;
            query.prazosInformados = prazos;

            final List<TransferObject> taxasCto = query.executarDTO();
            List<GerencialGeralTaxasEfetivasBean> taxas = null;
            if ((taxasCto != null) && !taxasCto.isEmpty()) {
                TransferObject to = null;
                GerencialGeralTaxasEfetivasBean taxaBean = null;
                taxas = new ArrayList<>();
                for (final TransferObject element : taxasCto) {
                    to = element;
                    final String csaCodigo = (String) to.getAttribute(Columns.CSA_CODIGO);
                    taxaBean = new GerencialGeralTaxasEfetivasBean();
                    taxaBean.setCsaCodigo(csaCodigo);
                    taxaBean.setCsaNomeAbrev((String) to.getAttribute(Columns.CSA_NOME_ABREV));
                    taxaBean.setCsaNome((String) to.getAttribute(Columns.CSA_NOME));
                    // a princípio, o relatório gerencial exibirá somente a taxa média praticada
                    taxaBean.setTaxaMedia(taxasMedias.get(csaCodigo));
                    taxas.add(taxaBean);
                }
            }

            return taxas;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public InadimplenciaBean buscaQuantidadeCarteiraInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigo, String naturezaServico, List<String> sadCodigos, AcessoSistema responsavel) throws RelatorioControllerException {
        return buscaTotalCarteiraInadimplencia(prdDtDesconto, csaCodigo, spdCodigo, naturezaServico, sadCodigos, true, responsavel);
    }

    @Override
    public InadimplenciaBean buscaValorCarteiraInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigo, String naturezaServico, List<String> sadCodigos, AcessoSistema responsavel) throws RelatorioControllerException {
        return buscaTotalCarteiraInadimplencia(prdDtDesconto, csaCodigo, spdCodigo, naturezaServico, sadCodigos, false, responsavel);
    }

    private InadimplenciaBean buscaTotalCarteiraInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigos, String naturezaServico, List<String> sadCodigos, boolean contador, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            InadimplenciaBean retorno = null;
            final RelatorioInadimplenciaTotalCarteiraQuery query = new RelatorioInadimplenciaTotalCarteiraQuery();

            query.prdDtDesconto = prdDtDesconto;
            query.csaCodigo = csaCodigo;
            query.spdCodigos = spdCodigos;
            query.naturezaServico = naturezaServico;
            query.count = contador;
            query.csaProjetoInadimplencia = CodedValues.TPC_SIM;
            query.sadCodigos = sadCodigos;

            final List<TransferObject> result = query.executarDTO();

            if ((result != null) && !result.isEmpty()) {
                for (final TransferObject to : result) {
                    retorno = new InadimplenciaBean();

                    if (!TextHelper.isNull(to.getAttribute("total"))) {
                        retorno.setQuantidade(Long.valueOf(to.getAttribute("total").toString()));
                    }

                    if (!TextHelper.isNull(to.getAttribute("csaNome"))) {
                        retorno.setDescricao(to.getAttribute("csaNome").toString());
                    }

                    if (!TextHelper.isNull(to.getAttribute("valor"))) {
                        retorno.setValor1(Double.valueOf(to.getAttribute("valor").toString()));
                    }
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<String> buscaTopOrgaosInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigos, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException {
        return buscaTopOrgaosInadimplencia(prdDtDesconto, csaCodigo, spdCodigos, naturezaServico, true, responsavel);
    }

    private List<String> buscaTopOrgaosInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigos, String naturezaServico, boolean contador, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            List<String> retorno = null;
            final RelatorioInadimplenciaOrgaoQuery query = new RelatorioInadimplenciaOrgaoQuery();

            query.prdDtDesconto = prdDtDesconto;
            query.csaCodigo = csaCodigo;
            query.spdCodigos = spdCodigos;
            query.count = contador;
            query.naturezaServico = naturezaServico;
            query.top = true;
            query.csaProjetoInadimplencia = CodedValues.TPC_SIM;

            final List<TransferObject> result = query.executarDTO();

            if ((result != null) && !result.isEmpty()) {
                retorno = new ArrayList<>();

                for (final TransferObject to : result) {
                    if (!TextHelper.isNull(to.getAttribute("orgCodigo"))) {
                        retorno.add((String) to.getAttribute("orgCodigo"));
                    }
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<String> buscaTopValorOrgaosInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigos, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException {
        return buscaTopOrgaosInadimplencia(prdDtDesconto, csaCodigo, spdCodigos, naturezaServico, false, responsavel);
    }

    @Override
    public InadimplenciaBean buscaQuantidadeOrgaoInadimplencia(String prdDtDesconto, String csaCodigo, String orgCodigo, List<String> notOrgCodigo, List<String> spdCodigos, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException {
        return buscaOrgaoInadimplencia(prdDtDesconto, csaCodigo, orgCodigo, notOrgCodigo, spdCodigos, naturezaServico, true, responsavel);
    }

    @Override
    public InadimplenciaBean buscaValorOrgaoInadimplencia(String prdDtDesconto, String csaCodigo, String orgCodigo, List<String> notOrgCodigo, List<String> spdCodigos, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException {
        return buscaOrgaoInadimplencia(prdDtDesconto, csaCodigo, orgCodigo, notOrgCodigo, spdCodigos, naturezaServico, false, responsavel);
    }

    private InadimplenciaBean buscaOrgaoInadimplencia(String prdDtDesconto, String csaCodigo, String orgCodigo, List<String> notOrgCodigo, List<String> spdCodigos, String naturezaServico, boolean contador, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final InadimplenciaBean retorno = new InadimplenciaBean();
            final RelatorioInadimplenciaOrgaoQuery query = new RelatorioInadimplenciaOrgaoQuery();

            query.prdDtDesconto = prdDtDesconto;
            query.csaCodigo = csaCodigo;
            query.orgCodigo = orgCodigo;
            query.notOrgCodigo = notOrgCodigo;
            query.spdCodigos = spdCodigos;
            query.naturezaServico = naturezaServico;
            query.count = contador;
            query.csaProjetoInadimplencia = CodedValues.TPC_SIM;

            final List<TransferObject> result = query.executarDTO();

            if ((result != null) && !result.isEmpty()) {
                for (final TransferObject to : result) {
                    if (!TextHelper.isNull(to.getAttribute("total"))) {
                        retorno.setQuantidade(Long.valueOf(to.getAttribute("total").toString()));
                    }

                    if (!TextHelper.isNull(to.getAttribute("orgNome"))) {
                        retorno.setDescricao(to.getAttribute("orgNome").toString());
                    }

                    if (!TextHelper.isNull(to.getAttribute("valor"))) {
                        retorno.setValor1(Double.valueOf(to.getAttribute("valor").toString()));
                    }
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<InadimplenciaBean> buscaQuantidadeSituacaoServidorInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigos, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException {
        final List<InadimplenciaBean> retorno = new ArrayList<>();
        retorno.add(buscaSituacaoServidorInadimplencia(prdDtDesconto, csaCodigo, spdCodigos, naturezaServico, Arrays.asList(CodedValues.SRS_ATIVO), true, responsavel));
        retorno.add(buscaSituacaoServidorInadimplencia(prdDtDesconto, csaCodigo, spdCodigos, naturezaServico, CodedValues.SRS_BLOQUEADOS, true, responsavel));
        retorno.add(buscaSituacaoServidorInadimplencia(prdDtDesconto, csaCodigo, spdCodigos, naturezaServico, CodedValues.SRS_INATIVOS, true, responsavel));
        return retorno;
    }

    @Override
    public List<InadimplenciaBean> buscaValorSituacaoServidorInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigos, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException {
        final List<InadimplenciaBean> retorno = new ArrayList<>();
        retorno.add(buscaSituacaoServidorInadimplencia(prdDtDesconto, csaCodigo, spdCodigos, naturezaServico, Arrays.asList(CodedValues.SRS_ATIVO), false, responsavel));
        retorno.add(buscaSituacaoServidorInadimplencia(prdDtDesconto, csaCodigo, spdCodigos, naturezaServico, CodedValues.SRS_BLOQUEADOS, false, responsavel));
        retorno.add(buscaSituacaoServidorInadimplencia(prdDtDesconto, csaCodigo, spdCodigos, naturezaServico, CodedValues.SRS_INATIVOS, false, responsavel));
        return retorno;
    }

    private InadimplenciaBean buscaSituacaoServidorInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigos, String naturezaServico, List<String> srsCodigos, boolean contador, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final InadimplenciaBean retorno = new InadimplenciaBean();
            final RelatorioInadimplenciaTotalCarteiraQuery query = new RelatorioInadimplenciaTotalCarteiraQuery();

            query.prdDtDesconto = prdDtDesconto;
            query.csaCodigo = csaCodigo;
            query.spdCodigos = spdCodigos;
            query.naturezaServico = naturezaServico;
            query.count = contador;
            query.srsCodigos = srsCodigos;
            query.csaProjetoInadimplencia = CodedValues.TPC_SIM;

            final List<TransferObject> result = query.executarDTO();

            if ((result != null) && !result.isEmpty()) {
                final Iterator<TransferObject> ite = result.iterator();

                if (ite.hasNext()) {
                    final TransferObject to = ite.next();

                    if (!TextHelper.isNull(to.getAttribute("total"))) {
                        retorno.setQuantidade(Long.valueOf(to.getAttribute("total").toString()));
                    }

                    if (!TextHelper.isNull(to.getAttribute("statusRegistroServidor"))) {
                        retorno.setDescricao(to.getAttribute("statusRegistroServidor").toString());
                    }

                    if (!TextHelper.isNull(to.getAttribute("valor"))) {
                        retorno.setValor1(Double.valueOf(to.getAttribute("valor").toString()));
                    }
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public InadimplenciaBean buscaTransferidosInadimplencia(String prdDtDesconto, String csaCodigo, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException {
        return buscaPorTipoOcorrenciaInadimplencia(prdDtDesconto, csaCodigo, CodedValues.TOC_TRANSFERENCIA_CONTRATO, naturezaServico, true, responsavel);
    }

    @Override
    public InadimplenciaBean buscaAlongadosInadimplencia(String prdDtDesconto, String csaCodigo, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException {
        return buscaPorTipoOcorrenciaInadimplencia(prdDtDesconto, csaCodigo, CodedValues.TOC_ALONGAMENTO_CONTRATO, naturezaServico, true, responsavel);
    }

    @Override
    public InadimplenciaBean buscaValorTransferidosInadimplencia(String prdDtDesconto, String csaCodigo, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException {
        return buscaPorTipoOcorrenciaInadimplencia(prdDtDesconto, csaCodigo, CodedValues.TOC_TRANSFERENCIA_CONTRATO, naturezaServico, false, responsavel);
    }

    @Override
    public InadimplenciaBean buscaValorAlongadosInadimplencia(String prdDtDesconto, String csaCodigo, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException {
        return buscaPorTipoOcorrenciaInadimplencia(prdDtDesconto, csaCodigo, CodedValues.TOC_ALONGAMENTO_CONTRATO, naturezaServico, false, responsavel);
    }

    private InadimplenciaBean buscaPorTipoOcorrenciaInadimplencia(String prdDtDesconto, String csaCodigo, String tocCodigo, String naturezaServico, boolean contador, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final InadimplenciaBean retorno = new InadimplenciaBean();
            final RelatorioInadimplenciaTipoOcorrenciaQuery query = new RelatorioInadimplenciaTipoOcorrenciaQuery();

            query.prdDtDesconto = prdDtDesconto;
            query.csaCodigo = csaCodigo;
            query.csaProjetoInadimplencia = CodedValues.TPC_SIM;
            query.naturezaServico = naturezaServico;
            query.tocCodigo = tocCodigo;
            query.count = contador;
            query.responsavel = responsavel;

            final List<TransferObject> result = query.executarDTO();

            if ((result != null) && !result.isEmpty()) {
                final TransferObject to = result.iterator().next();

                if (!TextHelper.isNull(to.getAttribute("total"))) {
                    retorno.setQuantidade(Long.valueOf(to.getAttribute("total").toString()));
                }

                if (!TextHelper.isNull(to.getAttribute("valor"))) {
                    retorno.setValor1(Double.valueOf(to.getAttribute("valor").toString()));
                }

                if (prdDtDesconto != null) {
                    final DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
                    final DateFormat saida = new SimpleDateFormat("MM/yyyy");
                    try {
                        retorno.setDescricao(saida.format(entrada.parse(prdDtDesconto)));
                    } catch (final ParseException e) {
                        e.printStackTrace();
                        throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
                    }
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public InadimplenciaBean buscaFalecidosInadimplencia(String prdDtDesconto, String csaCodigo, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException {
        return buscaPorStatusRegistroServidorInadimplencia(prdDtDesconto, csaCodigo, CodedValues.SRS_FALECIDO, naturezaServico, responsavel);
    }

    private InadimplenciaBean buscaPorStatusRegistroServidorInadimplencia(String prdDtDesconto, String csaCodigo, String srsCodigo, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final InadimplenciaBean retorno = new InadimplenciaBean();
            final RelatorioInadimplenciaStatusRegistroServidorQuery query = new RelatorioInadimplenciaStatusRegistroServidorQuery();

            query.prdDtDesconto = prdDtDesconto;
            query.csaCodigo = csaCodigo;
            query.csaProjetoInadimplencia = CodedValues.TPC_SIM;
            query.naturezaServico = naturezaServico;
            query.srsCodigo = srsCodigo;
            query.responsavel = responsavel;

            final List<TransferObject> result = query.executarDTO();

            if ((result != null) && !result.isEmpty()) {
                final TransferObject to = result.iterator().next();

                if (!TextHelper.isNull(to.getAttribute("total"))) {
                    retorno.setQuantidade(Long.valueOf(to.getAttribute("total").toString()));
                }

                if (prdDtDesconto != null) {
                    final DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
                    final DateFormat saida = new SimpleDateFormat("MM/yyyy");
                    try {
                        retorno.setDescricao(saida.format(entrada.parse(prdDtDesconto)));
                    } catch (final ParseException e) {
                        e.printStackTrace();
                        throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
                    }
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<ServicoOperacaoMesBean> lstOperacaoMes(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ServicoOperacaoMesBean> retorno = new ArrayList<>();

            final RelatorioServicoOperacaoMesQuery query = new RelatorioServicoOperacaoMesQuery();
            query.setCriterios(criterios);
            final List<TransferObject> lista = query.executarDTO();

            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ServicoOperacaoMesBean bean = new ServicoOperacaoMesBean();

                    if (!TextHelper.isNull(to.getAttribute(Columns.CSA_CODIGO))) {
                        bean.setCsaCodigo(to.getAttribute(Columns.CSA_CODIGO).toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute(Columns.CSA_NOME))) {
                        bean.setCsaNome(to.getAttribute(Columns.CSA_NOME).toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute("CSA"))) {
                        bean.setConsignataria(to.getAttribute("CSA").toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute("SERVICO"))) {
                        bean.setServico(to.getAttribute("SERVICO").toString());
                    }
                    try {
                        bean.setAtivoInicioMes((BigDecimal) to.getAttribute("ATIVO_INICIO_MES"));
                        bean.setAtivoFimMes((BigDecimal) to.getAttribute("ATIVO_FIM_MES"));
                        bean.setQuitados((BigDecimal) to.getAttribute("QUITADOS"));
                        bean.setRenegociados((BigDecimal) to.getAttribute("RENEGOCIADOS"));
                        bean.setNovos((BigDecimal) to.getAttribute("NOVOS"));
                        bean.setTotalValorDescontatoMes((BigDecimal) to.getAttribute("TOTAL_VALOR_DESCONTADO_MES"));
                        bean.setParticipacaoTotalServidores((BigDecimal) to.getAttribute("PARTICIPACAO_TOTAL_SERVIDORES"));
                        bean.setParticipacaoTotalDescontado((BigDecimal) to.getAttribute("PARTICIPACAO_TOTAL_DESCONTADO"));
                        bean.setRetencaoGoverno((BigDecimal) to.getAttribute("RETENCAO_GOVERNO"));
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    retorno.add(bean);
                }
            }
            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<RelatorioConfCadMargemBean> lstRelConfCadMargem(TransferObject criterio, AcessoSistema responsavel) throws RelatorioControllerException {
        final List<RelatorioConfCadMargemBean> retorno = new ArrayList<>();
        try {

            final RelatorioConfCadMargemQuery query = new RelatorioConfCadMargemQuery();
            query.setCriterios(criterio);
            final List<TransferObject> resultado = query.executarDTO();

            if ((resultado != null) && !resultado.isEmpty()) {
                RelatorioConfCadMargemBean pojo = null;
                for (final TransferObject to : resultado) {
                    pojo = new RelatorioConfCadMargemBean();

                    pojo.setSerNome((String) to.getAttribute("ser_nome"));
                    pojo.setSerCpf((String) to.getAttribute("ser_cpf"));
                    pojo.setRseMatricula((String) to.getAttribute("rse_matricula"));
                    pojo.setSrsDescricao((String) to.getAttribute("srs_descricao"));
                    pojo.setRseTipo((String) to.getAttribute("rse_tipo"));
                    pojo.setRseMargem1((BigDecimal) to.getAttribute("rse_margem_1"));
                    pojo.setRseMargemRest1((BigDecimal) to.getAttribute("rse_margem_rest_1"));
                    pojo.setPercCompMargem1((BigDecimal) to.getAttribute("perc_comp_margem_1"));
                    pojo.setRseMargem2((BigDecimal) to.getAttribute("rse_margem_2"));
                    pojo.setRseMargemRest2((BigDecimal) to.getAttribute("rse_margem_rest_2"));
                    pojo.setPercCompMargem2((BigDecimal) to.getAttribute("perc_comp_margem_2"));
                    pojo.setRseMargem3((BigDecimal) to.getAttribute("rse_margem_3"));
                    pojo.setRseMargemRest3((BigDecimal) to.getAttribute("rse_margem_rest_3"));
                    pojo.setPercCompMargem3((BigDecimal) to.getAttribute("perc_comp_margem_3"));
                    pojo.setRseMargem4((BigDecimal) to.getAttribute("rse_margem_4"));
                    pojo.setRseMargemRest4((BigDecimal) to.getAttribute("rse_margem_rest_4"));
                    pojo.setPercCompMargem4((BigDecimal) to.getAttribute("perc_comp_margem_4"));
                    pojo.setRseMargem5((BigDecimal) to.getAttribute("rse_margem_5"));
                    pojo.setRseMargemRest5((BigDecimal) to.getAttribute("rse_margem_rest_5"));
                    pojo.setPercCompMargem5((BigDecimal) to.getAttribute("perc_comp_margem_5"));
                    pojo.setRseMargem6((BigDecimal) to.getAttribute("rse_margem_6"));
                    pojo.setRseMargemRest6((BigDecimal) to.getAttribute("rse_margem_rest_6"));
                    pojo.setPercCompMargem6((BigDecimal) to.getAttribute("perc_comp_margem_6"));
                    pojo.setRseMargem7((BigDecimal) to.getAttribute("rse_margem_7"));
                    pojo.setRseMargemRest7((BigDecimal) to.getAttribute("rse_margem_rest_7"));
                    pojo.setPercCompMargem7((BigDecimal) to.getAttribute("perc_comp_margem_7"));
                    pojo.setRseMargem8((BigDecimal) to.getAttribute("rse_margem_8"));
                    pojo.setRseMargemRest8((BigDecimal) to.getAttribute("rse_margem_rest_8"));
                    pojo.setPercCompMargem8((BigDecimal) to.getAttribute("perc_comp_margem_8"));
                    pojo.setRseMargem9((BigDecimal) to.getAttribute("rse_margem_9"));
                    pojo.setRseMargemRest9((BigDecimal) to.getAttribute("rse_margem_rest_9"));
                    pojo.setPercCompMargem9((BigDecimal) to.getAttribute("perc_comp_margem_9"));
                    pojo.setRseMargem10((BigDecimal) to.getAttribute("rse_margem_10"));
                    pojo.setRseMargemRest10((BigDecimal) to.getAttribute("rse_margem_rest_10"));
                    pojo.setPercCompMargem10((BigDecimal) to.getAttribute("perc_comp_margem_10"));
                    pojo.setRseMargem11((BigDecimal) to.getAttribute("rse_margem_11"));
                    pojo.setRseMargemRest11((BigDecimal) to.getAttribute("rse_margem_rest_11"));
                    pojo.setPercCompMargem11((BigDecimal) to.getAttribute("perc_comp_margem_11"));
                    pojo.setRseMargem12((BigDecimal) to.getAttribute("rse_margem_12"));
                    pojo.setRseMargemRest12((BigDecimal) to.getAttribute("rse_margem_rest_12"));
                    pojo.setPercCompMargem12((BigDecimal) to.getAttribute("perc_comp_margem_12"));
                    pojo.setRseMargem13((BigDecimal) to.getAttribute("rse_margem_13"));
                    pojo.setRseMargemRest13((BigDecimal) to.getAttribute("rse_margem_rest_13"));
                    pojo.setPercCompMargem13((BigDecimal) to.getAttribute("perc_comp_margem_13"));
                    pojo.setRseMargem14((BigDecimal) to.getAttribute("rse_margem_14"));
                    pojo.setRseMargemRest14((BigDecimal) to.getAttribute("rse_margem_rest_14"));
                    pojo.setPercCompMargem14((BigDecimal) to.getAttribute("perc_comp_margem_14"));
                    pojo.setRseMargem15((BigDecimal) to.getAttribute("rse_margem_15"));
                    pojo.setRseMargemRest15((BigDecimal) to.getAttribute("rse_margem_rest_15"));
                    pojo.setPercCompMargem15((BigDecimal) to.getAttribute("perc_comp_margem_15"));
                    pojo.setRseMargem16((BigDecimal) to.getAttribute("rse_margem_16"));
                    pojo.setRseMargemRest16((BigDecimal) to.getAttribute("rse_margem_rest_16"));
                    pojo.setPercCompMargem16((BigDecimal) to.getAttribute("perc_comp_margem_16"));
                    pojo.setRseMargem17((BigDecimal) to.getAttribute("rse_margem_17"));
                    pojo.setRseMargemRest17((BigDecimal) to.getAttribute("rse_margem_rest_17"));
                    pojo.setPercCompMargem17((BigDecimal) to.getAttribute("perc_comp_margem_17"));
                    pojo.setRseMargem18((BigDecimal) to.getAttribute("rse_margem_18"));
                    pojo.setRseMargemRest18((BigDecimal) to.getAttribute("rse_margem_rest_18"));
                    pojo.setPercCompMargem18((BigDecimal) to.getAttribute("perc_comp_margem_18"));
                    pojo.setRseMargem19((BigDecimal) to.getAttribute("rse_margem_19"));
                    pojo.setRseMargemRest19((BigDecimal) to.getAttribute("rse_margem_rest_19"));
                    pojo.setPercCompMargem19((BigDecimal) to.getAttribute("perc_comp_margem_19"));
                    pojo.setRseMargem20((BigDecimal) to.getAttribute("rse_margem_20"));
                    pojo.setRseMargemRest20((BigDecimal) to.getAttribute("rse_margem_rest_20"));
                    pojo.setPercCompMargem20((BigDecimal) to.getAttribute("perc_comp_margem_20"));
                    pojo.setEstabelecimento((String) to.getAttribute("estabelecimento"));
                    pojo.setOrgao((String) to.getAttribute("orgao"));
                    pojo.setVariacaoMargem1((BigDecimal) to.getAttribute("variacaoMargem1"));
                    pojo.setVariacaoMargem2((BigDecimal) to.getAttribute("variacaoMargem2"));
                    pojo.setVariacaoMargem3((BigDecimal) to.getAttribute("variacaoMargem3"));
                    pojo.setVariacaoMargem4((BigDecimal) to.getAttribute("variacaoMargem4"));
                    pojo.setVariacaoMargem5((BigDecimal) to.getAttribute("variacaoMargem5"));
                    pojo.setVariacaoMargem6((BigDecimal) to.getAttribute("variacaoMargem6"));
                    pojo.setVariacaoMargem7((BigDecimal) to.getAttribute("variacaoMargem7"));
                    pojo.setVariacaoMargem8((BigDecimal) to.getAttribute("variacaoMargem8"));
                    pojo.setVariacaoMargem9((BigDecimal) to.getAttribute("variacaoMargem9"));
                    pojo.setVariacaoMargem10((BigDecimal) to.getAttribute("variacaoMargem10"));
                    pojo.setVariacaoMargem11((BigDecimal) to.getAttribute("variacaoMargem11"));
                    pojo.setVariacaoMargem12((BigDecimal) to.getAttribute("variacaoMargem12"));
                    pojo.setVariacaoMargem13((BigDecimal) to.getAttribute("variacaoMargem13"));
                    pojo.setVariacaoMargem14((BigDecimal) to.getAttribute("variacaoMargem14"));
                    pojo.setVariacaoMargem15((BigDecimal) to.getAttribute("variacaoMargem15"));
                    pojo.setVariacaoMargem16((BigDecimal) to.getAttribute("variacaoMargem16"));
                    pojo.setVariacaoMargem17((BigDecimal) to.getAttribute("variacaoMargem17"));
                    pojo.setVariacaoMargem18((BigDecimal) to.getAttribute("variacaoMargem18"));
                    pojo.setVariacaoMargem19((BigDecimal) to.getAttribute("variacaoMargem19"));
                    pojo.setVariacaoMargem20((BigDecimal) to.getAttribute("variacaoMargem20"));
                    pojo.setMargemAnterior1((BigDecimal) to.getAttribute("margemAnterior1"));
                    pojo.setMargemAnterior2((BigDecimal) to.getAttribute("margemAnterior2"));
                    pojo.setMargemAnterior3((BigDecimal) to.getAttribute("margemAnterior3"));
                    pojo.setMargemAnterior4((BigDecimal) to.getAttribute("margemAnterior4"));
                    pojo.setMargemAnterior5((BigDecimal) to.getAttribute("margemAnterior5"));
                    pojo.setMargemAnterior6((BigDecimal) to.getAttribute("margemAnterior6"));
                    pojo.setMargemAnterior7((BigDecimal) to.getAttribute("margemAnterior7"));
                    pojo.setMargemAnterior8((BigDecimal) to.getAttribute("margemAnterior8"));
                    pojo.setMargemAnterior9((BigDecimal) to.getAttribute("margemAnterior9"));
                    pojo.setMargemAnterior10((BigDecimal) to.getAttribute("margemAnterior10"));
                    pojo.setMargemAnterior11((BigDecimal) to.getAttribute("margemAnterior11"));
                    pojo.setMargemAnterior12((BigDecimal) to.getAttribute("margemAnterior12"));
                    pojo.setMargemAnterior13((BigDecimal) to.getAttribute("margemAnterior13"));
                    pojo.setMargemAnterior14((BigDecimal) to.getAttribute("margemAnterior14"));
                    pojo.setMargemAnterior15((BigDecimal) to.getAttribute("margemAnterior15"));
                    pojo.setMargemAnterior16((BigDecimal) to.getAttribute("margemAnterior16"));
                    pojo.setMargemAnterior17((BigDecimal) to.getAttribute("margemAnterior17"));
                    pojo.setMargemAnterior18((BigDecimal) to.getAttribute("margemAnterior18"));
                    pojo.setMargemAnterior19((BigDecimal) to.getAttribute("margemAnterior19"));
                    pojo.setMargemAnterior20((BigDecimal) to.getAttribute("margemAnterior20"));

                    retorno.add(pojo);
                }
            }
            return retorno;

        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<Object> geraRelatorioInclusoesPorCsa(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioInclusoesPorCsaQuery query = new RelatorioInclusoesPorCsaQuery();
            query.setCriterios(criterios);

            final List<TransferObject> tranObj = query.executarDTO();
            final String[] fields = query.getFields();

            final List<Object> conteudo = new ArrayList<>();
            conteudo.add(tranObj);
            conteudo.add(fields);
            conteudo.add(query.countMes);
            conteudo.add(query.countEst);
            conteudo.add(query.campos);
            conteudo.add(query.countPeriodos);
            conteudo.add(query.alias);
            conteudo.add(query.camposTotal);

            return conteudo;

        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Pair<String[], List<TransferObject>> geraRelatorioSinteticoDecisaoJudicial(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioSinteticoDecisaoJudicialQuery query = new RelatorioSinteticoDecisaoJudicialQuery();
            query.setCriterios(criterios);

            final List<TransferObject> tranObj = query.executarDTO();
            final String[] fields = query.getFields();

            return Pair.of(fields, tranObj);
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<RegrasConvenioServicosBean> listaServicosRegrasConvenio(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioRegrasConvenioListaConveniosQuery queryCnv = new RelatorioRegrasConvenioListaConveniosQuery(responsavel);
            final List<TransferObject> lstConvenios = queryCnv.executarDTO();
            final Map<String, TransferObject> mapConvenios = new HashMap<>();

            if ((lstConvenios != null) && !lstConvenios.isEmpty()) {
                for (final TransferObject convenio : lstConvenios) {
                    mapConvenios.put(convenio.getAttribute(Columns.CSA_CODIGO).toString(), convenio);
                }
            }

            final RelatorioRegrasConvenioListaServicosQuery querySvc = new RelatorioRegrasConvenioListaServicosQuery(csaCodigo, responsavel);
            final List<TransferObject> lstServicos = querySvc.executarDTO();

            final List<RegrasConvenioServicosBean> retorno =  new ArrayList<>();

            if ((lstServicos != null) && !lstServicos.isEmpty()) {
            	final String rotuloSim = ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel);
                final String rotuloNao = ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
                final String rotuloNadaEncontrado = ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel);
            	String svcCodigoAnterior = "";
            	TransferObject servicoAnterior = new CustomTransferObject();
                String rotuloRelatorioRegrasConvenioSubtituloServicosSistema = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.regras.servico.sistema", responsavel);
                
                final List<TransferObject> margens = margemController.lstMargem(responsavel);
                HashMap<Short, TransferObject> hashMargem = new HashMap<>();
                if ((margens != null) && !margens.isEmpty()) {
                    for (final TransferObject margem : margens) {
                    	final Short marCodigo = (Short) margem.getAttribute(Columns.MAR_CODIGO);
                    	hashMargem.put(marCodigo, margem);
                    }
                }
                
                for (final TransferObject servico : lstServicos) {
                    final String csaCodigoRegra = servico.getAttribute(Columns.CSA_CODIGO).toString();
                    final TransferObject convenio = mapConvenios.get(csaCodigoRegra);
                    final Boolean salaryPay = (convenio != null) && !TextHelper.isNull(convenio.getAttribute("SALARY_PAY")) ? "1".equals(convenio.getAttribute("SALARY_PAY").toString()) : false;
                    final Boolean acessaApi = (convenio != null) && !TextHelper.isNull(convenio.getAttribute("ACESSA_API")) ? "1".equals(convenio.getAttribute("ACESSA_API").toString()) || "1".equals(convenio.getAttribute("ACESSA_API_1").toString()) : false;
                    String svcCodigo = servico.getAttribute(Columns.SVC_CODIGO).toString();
                    
                    if (TextHelper.isNull(svcCodigoAnterior)) {
                    	svcCodigoAnterior = svcCodigo;
                    }
                    
                    if (!svcCodigoAnterior.equals(svcCodigo)) {
                    	String svcPrioridade = !TextHelper.isNull(servicoAnterior.getAttribute(Columns.SVC_PRIORIDADE)) ? servicoAnterior.getAttribute(Columns.SVC_PRIORIDADE).toString() : rotuloNadaEncontrado;
                    	
                    	// para manter a lógica de serviços iremos mocar o tps_codigo, tps_descricao e pse_vlr para construir os parâmetros específicos do sistema e serviço
                    	CustomTransferObject servicosEspecificosSistema = new CustomTransferObject();
                    	servicosEspecificosSistema.setAttribute(Columns.CSA_NOME, servicoAnterior.getAttribute(Columns.CSA_NOME).toString());
                    	servicosEspecificosSistema.setAttribute(Columns.SVC_CODIGO, servicoAnterior.getAttribute(Columns.SVC_CODIGO).toString());
                    	servicosEspecificosSistema.setAttribute(Columns.SVC_DESCRICAO, servicoAnterior.getAttribute(Columns.SVC_DESCRICAO).toString());
                    	servicosEspecificosSistema.setAttribute(Columns.SVC_DESCRICAO, servicoAnterior.getAttribute(Columns.SVC_DESCRICAO).toString());
                    	servicosEspecificosSistema.setAttribute(Columns.TPS_CODIGO, "99_prioridade");
                    	servicosEspecificosSistema.setAttribute(Columns.TPS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servico.prioridade.desconto", responsavel));
                    	servicosEspecificosSistema.setAttribute(Columns.PSE_VLR, svcPrioridade);
                    	servicosEspecificosSistema.setAttribute(Columns.PSE_VLR_REF, rotuloNadaEncontrado);
                    	servicosEspecificosSistema.setAttribute("SUBTITULO", rotuloRelatorioRegrasConvenioSubtituloServicosSistema);
                    	
                    	retorno.add(new RegrasConvenioServicosBean(csaCodigoRegra, servicosEspecificosSistema.getAttribute(Columns.CSA_NOME).toString(),
                    			servicosEspecificosSistema.getAttribute(Columns.SVC_CODIGO).toString(), servicosEspecificosSistema.getAttribute(Columns.SVC_DESCRICAO).toString(),
                    			servicosEspecificosSistema.getAttribute(Columns.TPS_CODIGO).toString(), servicosEspecificosSistema.getAttribute(Columns.TPS_DESCRICAO).toString(),
                    			servicosEspecificosSistema.getAttribute(Columns.PSE_VLR).toString(), (String) servicosEspecificosSistema.getAttribute(Columns.PSE_VLR_REF), servicosEspecificosSistema.getAttribute("SUBTITULO").toString(),
                                salaryPay, acessaApi));
                    	
                    	ParamSvcTO paramSvcTO = parametroController.getParamSvcCseTO(svcCodigoAnterior, responsavel);
                    	List<String> tpsCodigos = new ArrayList<>();
                    	tpsCodigos.add(CodedValues.TPS_INCIDE_MARGEM);
                        tpsCodigos.add(CodedValues.TPS_REQUER_DEFERIMENTO_RESERVAS);
                        tpsCodigos.add(CodedValues.TPS_PRAZO_ATEND_SOLICIT_SALDO_DEVEDOR);
                        
                        for (String tpsCodigo : tpsCodigos) {
                        	servicosEspecificosSistema = new CustomTransferObject();
                        	servicosEspecificosSistema.setAttribute(Columns.CSA_NOME, servicoAnterior.getAttribute(Columns.CSA_NOME).toString());
                        	servicosEspecificosSistema.setAttribute(Columns.SVC_CODIGO, servicoAnterior.getAttribute(Columns.SVC_CODIGO).toString());
                        	servicosEspecificosSistema.setAttribute(Columns.SVC_DESCRICAO, servicoAnterior.getAttribute(Columns.SVC_DESCRICAO).toString());
                        	servicosEspecificosSistema.setAttribute(Columns.SVC_DESCRICAO, servicoAnterior.getAttribute(Columns.SVC_DESCRICAO).toString());
                        	
                        	if (CodedValues.TPS_INCIDE_MARGEM.equals(tpsCodigo)) {
                            	Short pseVlr = paramSvcTO.getTpsIncideMargem();
                            	if (TextHelper.isNum(pseVlr)) {
                                	TransferObject margem = hashMargem.get(pseVlr);
                                	
                                	servicosEspecificosSistema.setAttribute(Columns.TPS_CODIGO, tpsCodigo);
                                	servicosEspecificosSistema.setAttribute(Columns.TPS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servico.indice.margem", responsavel));
                                	servicosEspecificosSistema.setAttribute(Columns.PSE_VLR, (String)  margem.getAttribute(Columns.MAR_DESCRICAO));
                                	servicosEspecificosSistema.setAttribute(Columns.PSE_VLR_REF, rotuloNadaEncontrado);
                                	servicosEspecificosSistema.setAttribute("SUBTITULO", rotuloRelatorioRegrasConvenioSubtituloServicosSistema);
                            	} else {
                            		continue;
                            	}
                            } else if (CodedValues.TPS_REQUER_DEFERIMENTO_RESERVAS.equals(tpsCodigo)) {
                                	servicosEspecificosSistema.setAttribute(Columns.TPS_CODIGO, tpsCodigo);
                                	servicosEspecificosSistema.setAttribute(Columns.TPS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servico.requer.deferimento", responsavel));
                                	servicosEspecificosSistema.setAttribute(Columns.PSE_VLR, paramSvcTO.isTpsRequerDeferimentoReservas() ? rotuloSim : rotuloNao);
                                	servicosEspecificosSistema.setAttribute(Columns.PSE_VLR_REF, rotuloNadaEncontrado);
                                	servicosEspecificosSistema.setAttribute("SUBTITULO", rotuloRelatorioRegrasConvenioSubtituloServicosSistema);
                            } else {
                            	servicosEspecificosSistema.setAttribute(Columns.TPS_CODIGO, tpsCodigo);
                            	servicosEspecificosSistema.setAttribute(Columns.TPS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servico.dias.info.saldo", responsavel));
                            	servicosEspecificosSistema.setAttribute(Columns.PSE_VLR, TextHelper.isNull(paramSvcTO.getTpsPrazoAtendSolicitSaldoDevedor()) ? rotuloNadaEncontrado : paramSvcTO.getTpsPrazoAtendSolicitSaldoDevedor());
                            	servicosEspecificosSistema.setAttribute(Columns.PSE_VLR_REF, rotuloNadaEncontrado);
                            	servicosEspecificosSistema.setAttribute("SUBTITULO", rotuloRelatorioRegrasConvenioSubtituloServicosSistema);
                            } 
                        	
                        	retorno.add(new RegrasConvenioServicosBean(csaCodigoRegra, servicosEspecificosSistema.getAttribute(Columns.CSA_NOME).toString(),
                        			servicosEspecificosSistema.getAttribute(Columns.SVC_CODIGO).toString(), servicosEspecificosSistema.getAttribute(Columns.SVC_DESCRICAO).toString(),
                        			servicosEspecificosSistema.getAttribute(Columns.TPS_CODIGO).toString(), servicosEspecificosSistema.getAttribute(Columns.TPS_DESCRICAO).toString(),
                        			servicosEspecificosSistema.getAttribute(Columns.PSE_VLR).toString(), (String) servicosEspecificosSistema.getAttribute(Columns.PSE_VLR_REF), servicosEspecificosSistema.getAttribute("SUBTITULO").toString(),
                                    salaryPay, acessaApi));
                        }
                        List<TransferObject> lstServicoAnterior = new ArrayList<>();
                        lstServicoAnterior.add(servicoAnterior);
                        
                        List<TransferObject> lstLimiteTaxaJuros = limiteTaxaJurosController.listaLimiteTaxaJurosPorServico(lstServicoAnterior, null, null, null, responsavel);
                        
                        for (TransferObject limiteTaxaJuros : lstLimiteTaxaJuros) {
                        	String ltjCodigo = (String) limiteTaxaJuros.getAttribute(Columns.LTJ_CODIGO);
                        	Short prazo = (Short) limiteTaxaJuros.getAttribute(Columns.LTJ_PRAZO_REF);
                        	BigDecimal jurosMax = (BigDecimal) limiteTaxaJuros.getAttribute(Columns.LTJ_JUROS_MAX);
                        	
                        	
                        	servicosEspecificosSistema = new CustomTransferObject();
                        	servicosEspecificosSistema.setAttribute(Columns.CSA_NOME, servicoAnterior.getAttribute(Columns.CSA_NOME).toString());
                        	servicosEspecificosSistema.setAttribute(Columns.SVC_CODIGO, servicoAnterior.getAttribute(Columns.SVC_CODIGO).toString());
                        	servicosEspecificosSistema.setAttribute(Columns.SVC_DESCRICAO, servicoAnterior.getAttribute(Columns.SVC_DESCRICAO).toString());
                        	servicosEspecificosSistema.setAttribute(Columns.TPS_CODIGO, ltjCodigo);
                        	servicosEspecificosSistema.setAttribute(Columns.TPS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servico.limite.taxa.juros", responsavel, String.valueOf(prazo)));
                        	servicosEspecificosSistema.setAttribute(Columns.PSE_VLR, NumberHelper.formata(jurosMax.doubleValue(), "#,##0.00; #,##0.00"));
                        	servicosEspecificosSistema.setAttribute(Columns.PSE_VLR_REF, rotuloNadaEncontrado);
                        	servicosEspecificosSistema.setAttribute("SUBTITULO", rotuloRelatorioRegrasConvenioSubtituloServicosSistema);
                        	
                        	retorno.add(new RegrasConvenioServicosBean(csaCodigoRegra, servicosEspecificosSistema.getAttribute(Columns.CSA_NOME).toString(),
                        			servicosEspecificosSistema.getAttribute(Columns.SVC_CODIGO).toString(), servicosEspecificosSistema.getAttribute(Columns.SVC_DESCRICAO).toString(),
                        			servicosEspecificosSistema.getAttribute(Columns.TPS_CODIGO).toString(), servicosEspecificosSistema.getAttribute(Columns.TPS_DESCRICAO).toString(),
                        			servicosEspecificosSistema.getAttribute(Columns.PSE_VLR).toString(), (String) servicosEspecificosSistema.getAttribute(Columns.PSE_VLR_REF), servicosEspecificosSistema.getAttribute("SUBTITULO").toString(),
                                    salaryPay, acessaApi));
                        }
                    	
                    	svcCodigoAnterior = svcCodigo;
                    }
                    
                    retorno.add(new RegrasConvenioServicosBean(csaCodigoRegra, servico.getAttribute(Columns.CSA_NOME).toString(),
                            servico.getAttribute(Columns.SVC_CODIGO).toString(), servico.getAttribute(Columns.SVC_DESCRICAO).toString(),
                            servico.getAttribute(Columns.TPS_CODIGO).toString(), servico.getAttribute(Columns.TPS_DESCRICAO).toString(),
                            servico.getAttribute(Columns.PSE_VLR).toString(), (String) servico.getAttribute(Columns.PSE_VLR_REF), servico.getAttribute("SUBTITULO").toString(),
                            salaryPay, acessaApi));
                    
                    servicoAnterior = servico;
                }
            }

            return retorno;
        } catch (final HQueryException | MargemControllerException | ParametroControllerException | LimiteTaxaJurosControllerException ex) {
            LOG.debug(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<AvaliacaoFaqBean> listaAvaliacaoFaqAnalitico(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioAvaliacaoFaqAnaliticoQuery query = new RelatorioAvaliacaoFaqAnaliticoQuery();
            query.setCriterios(criterios);

            final List<TransferObject> resultados = query.executarDTO();
            final List<AvaliacaoFaqBean> avaliacoes = new ArrayList<>();

            for (final TransferObject resultado : resultados) {
                final AvaliacaoFaqBean avaliacao = new AvaliacaoFaqBean();
                avaliacao.setAvfCodigo((String) resultado.getAttribute("avfCodigo"));
                avaliacao.setFaqCodigo((String) resultado.getAttribute("faqCodigo"));
                avaliacao.setFaqTitulo1((String) resultado.getAttribute("faqTitulo1"));
                avaliacao.setAvaliacao((String) resultado.getAttribute("avaliacao"));
                avaliacao.setAvfData((Date) resultado.getAttribute("avfData"));
                avaliacao.setUsuNome((String) resultado.getAttribute("usuNome"));
                avaliacao.setUsuLogin((String) resultado.getAttribute("usuLogin"));
                avaliacao.setEntidade((String) resultado.getAttribute("entidade"));
                avaliacao.setAvfComentario(resultado.getAttribute("avfComentario") != null ? resultado.getAttribute("avfComentario").toString() : "");

                avaliacoes.add(avaliacao);
            }

            return avaliacoes;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
    @Override
    public List<AvaliacaoFaqBean> listaAvaliacaoFaqSintetico(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final RelatorioAvaliacaoFaqSinteticoQuery query = new RelatorioAvaliacaoFaqSinteticoQuery();
            query.setCriterios(criterios);

            final List<TransferObject> resultados = query.executarDTO();
            final List<AvaliacaoFaqBean> avaliacoes = new ArrayList<>();

            for (final TransferObject resultado : resultados) {
                final AvaliacaoFaqBean avaliacao = new AvaliacaoFaqBean();
                avaliacao.setAvfCodigo((String) resultado.getAttribute("avfCodigo"));
                avaliacao.setFaqCodigo((String) resultado.getAttribute("faqCodigo"));
                avaliacao.setFaqTitulo1((String) resultado.getAttribute("faqTitulo1"));
                avaliacao.setUtil((BigDecimal) resultado.getAttribute("util"));
                avaliacao.setInutil((BigDecimal) resultado.getAttribute("inutil"));

                avaliacoes.add(avaliacao);
            }

            return avaliacoes;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // DESENV-15891
    @Override
    public List<TransferObject> listaEstatisticoProcessamentoTipoArquivos(List<String> tarCodigos, AcessoSistema responsavel) throws RelatorioControllerException {
        List<TransferObject> estatisticoProcessamentoTipoArquivos;

        try {
            estatisticoProcessamentoTipoArquivos = new ListaTipoArquivoByTarCodigoQuery(tarCodigos).executarDTO();
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return estatisticoProcessamentoTipoArquivos;
    }
    @Override
    public List<TransferObject> listaEstatisticoProcessamentoPeriodos(List<String> funCodigos, List<String> tarCodigos, AcessoSistema responsavel) throws RelatorioControllerException {
        List<TransferObject> estatisticoProcessamentoPeriodos;

        try {
            estatisticoProcessamentoPeriodos = new RelatorioEstatisticoProcessamentoPeriodosQuery(funCodigos, tarCodigos, responsavel).executarDTO();
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return estatisticoProcessamentoPeriodos;
    }
    @Override
    public List<EstatisticoProcessamentoBean> listaEstatisticoProcessamento(List<java.sql.Date> harPeriodos, List<String> harPeriodosFormatados, List<TransferObject> estatisticoProcessamentoTipoArquivos, AcessoSistema responsavel) throws RelatorioControllerException {
        final List<EstatisticoProcessamentoBean> estatisticoProcessamentoBeans = new ArrayList<>();
        final short QNT_COLUNAS = 12;
        boolean preencheuCelulaTabela;

        try {
            final List<TransferObject> harMaxQntLinhasRetornoIntegracaoQuery = new RelatorioEstatisticoProcessamentoQuery(CodedValues.FUN_IMP_RET_INTEGRACAO, TipoArquivoEnum.ARQUIVO_RETORNO_INTEGRACAO.getCodigo(), harPeriodos, responsavel).executarDTO();
            final List<Integer> harMaxQntLinhasRetornoIntegracaoLista = new ArrayList<>();
            for (int i = 0; i < QNT_COLUNAS; i++) {
                preencheuCelulaTabela = false;
                if ((harPeriodosFormatados != null) && (i < harPeriodosFormatados.size())) {
                    for (int j = 0; j < harMaxQntLinhasRetornoIntegracaoQuery.size(); j++) {
                        try {
                            if ((harPeriodosFormatados.get(i).equals((DateHelper.reformat(harMaxQntLinhasRetornoIntegracaoQuery.get(j).getAttribute(Columns.HAR_PERIODO).toString(), "yyyy-MM-dd", "MM/yyyy"))))) {
                                harMaxQntLinhasRetornoIntegracaoLista.add((Integer) harMaxQntLinhasRetornoIntegracaoQuery.get(j).getAttribute("har_max_qnt_linhas"));
                                preencheuCelulaTabela = true;
                            }
                        } catch (IndexOutOfBoundsException | ParseException e) {
                            harMaxQntLinhasRetornoIntegracaoLista.add(null);
                            preencheuCelulaTabela = true;
                        }
                    }
                }
                if (!preencheuCelulaTabela) {
                    harMaxQntLinhasRetornoIntegracaoLista.add(null);
                }
            }

            final List<TransferObject> harMaxQntLinhasCadastroMargensQuery = new RelatorioEstatisticoProcessamentoQuery(CodedValues.FUN_IMP_CAD_MARGENS, TipoArquivoEnum.ARQUIVO_CADASTRO_MARGENS.getCodigo(), harPeriodos, responsavel).executarDTO();
            final List<Integer> harMaxQntLinhasCadastroMargensLista = new ArrayList<>();
            for (int i = 0; i < QNT_COLUNAS; i++) {
                preencheuCelulaTabela = false;
                if ((harPeriodosFormatados != null) && (i < harPeriodosFormatados.size())) {
                    for (int j = 0; j < harMaxQntLinhasCadastroMargensQuery.size(); j++) {
                        try {
                            if ((harPeriodosFormatados.get(i).equals((DateHelper.reformat(harMaxQntLinhasCadastroMargensQuery.get(j).getAttribute(Columns.HAR_PERIODO).toString(), "yyyy-MM-dd", "MM/yyyy"))))) {
                                harMaxQntLinhasCadastroMargensLista.add((Integer) harMaxQntLinhasCadastroMargensQuery.get(j).getAttribute("har_max_qnt_linhas"));
                                preencheuCelulaTabela = true;
                            }
                        } catch (IndexOutOfBoundsException | ParseException e) {
                            harMaxQntLinhasCadastroMargensLista.add(null);
                            preencheuCelulaTabela = true;
                        }
                    }
                }
                if (!preencheuCelulaTabela) {
                    harMaxQntLinhasCadastroMargensLista.add(null);
                }
            }

            final List<TransferObject> harMaxQntLinhasMovimentoFinanceiroQuery = new RelatorioEstatisticoProcessamentoQuery(CodedValues.FUN_EXP_MOV_FINANCEIRO, TipoArquivoEnum.ARQUIVO_MOVIMENTO_FINANCEIRO.getCodigo(), harPeriodos, responsavel).executarDTO();
            final List<Integer> harMaxQntLinhasMovimentoFinanceiroLista = new ArrayList<>();
            for (int i = 0; i < QNT_COLUNAS; i++) {
                preencheuCelulaTabela = false;
                if ((harPeriodosFormatados != null) && (i < harPeriodosFormatados.size())) {
                    for (int j = 0; j < harMaxQntLinhasMovimentoFinanceiroQuery.size(); j++) {
                        try {
                            if ((harPeriodosFormatados.get(i).equals((DateHelper.reformat(harMaxQntLinhasMovimentoFinanceiroQuery.get(j).getAttribute(Columns.HAR_PERIODO).toString(), "yyyy-MM-dd", "MM/yyyy"))))) {
                                harMaxQntLinhasMovimentoFinanceiroLista.add((Integer) harMaxQntLinhasMovimentoFinanceiroQuery.get(j).getAttribute("har_max_qnt_linhas"));
                                preencheuCelulaTabela = true;
                            }
                        } catch (IndexOutOfBoundsException | ParseException e) {
                            harMaxQntLinhasMovimentoFinanceiroLista.add(null);
                            preencheuCelulaTabela = true;
                        }
                    }
                }
                if (!preencheuCelulaTabela) {
                    harMaxQntLinhasMovimentoFinanceiroLista.add(null);
                }
            }

            for (final TransferObject tipoArquivo : estatisticoProcessamentoTipoArquivos) {
                final EstatisticoProcessamentoBean estatisticoProcessamentoBean = new EstatisticoProcessamentoBean(tipoArquivo.getAttribute(Columns.TAR_CODIGO).toString(), tipoArquivo.getAttribute(Columns.TAR_DESCRICAO).toString(), new ArrayList<>());
                estatisticoProcessamentoBeans.add(estatisticoProcessamentoBean);
            }

            for (final EstatisticoProcessamentoBean estatisticoProcessamentoBean : estatisticoProcessamentoBeans) {
                if (estatisticoProcessamentoBean.getTarCodigo().equals(TipoArquivoEnum.ARQUIVO_RETORNO_INTEGRACAO.getCodigo())) {
                    estatisticoProcessamentoBean.setHarMaxQntLinhas(harMaxQntLinhasRetornoIntegracaoLista);
                } else if (estatisticoProcessamentoBean.getTarCodigo().equals(TipoArquivoEnum.ARQUIVO_CADASTRO_MARGENS.getCodigo())) {
                    estatisticoProcessamentoBean.setHarMaxQntLinhas(harMaxQntLinhasCadastroMargensLista);
                } else if (estatisticoProcessamentoBean.getTarCodigo().equals(TipoArquivoEnum.ARQUIVO_MOVIMENTO_FINANCEIRO.getCodigo())) {
                    estatisticoProcessamentoBean.setHarMaxQntLinhas(harMaxQntLinhasMovimentoFinanceiroLista);
                }
            }
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return estatisticoProcessamentoBeans;
    }

    @Override
    public void criarPivotAux() throws RelatorioControllerException {
        try {
            final RelatorioDAO relatorioDAO = DAOFactory.getDAOFactory().getRelatorioDAO();

            relatorioDAO.pivotAux();

        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.nao.possivel.gerar.relatorios.sintetico.gerencial.geral", AcessoSistema.getAcessoUsuarioSistema(), ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaAverbacoesCsa(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaVolumeAverbacaoQuery query = new RelatorioSinteticoGerencialCsaVolumeAverbacaoQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();
                    try {
                        if (!TextHelper.isNull(to.getAttribute("QUANTIDADE"))) {
                            bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                        } else {
                            continue;
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }
                    if (!TextHelper.isNull(to.getAttribute("STATUS"))) {
                        bean.setStatus(to.getAttribute("STATUS").toString());
                    }
                    try {
                        if (!TextHelper.isNull(to.getAttribute("VLR_TOTAL"))) {
                            bean.setVlrTotal(new BigDecimal(to.getAttribute("VLR_TOTAL").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaAverbacoesCsaGrafico(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaVolumeAverbacaoGraficoQuery query = new RelatorioSinteticoGerencialCsaVolumeAverbacaoGraficoQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();
                    if (!TextHelper.isNull(to.getAttribute("TIPO"))) {
                        bean.setTipoContrato(to.getAttribute("TIPO").toString());
                    }
                    try {
                        if (!TextHelper.isNull(to.getAttribute("PERCENTUAL"))) {
                            bean.setPercentual(new BigDecimal(to.getAttribute("PERCENTUAL").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaAverbacoesCsaApi(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaVolumeAverbacaoApiQuery query = new RelatorioSinteticoGerencialCsaVolumeAverbacaoApiQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    if (TextHelper.isNull(to.getAttribute("QUANTIDADE")) && TextHelper.isNull(to.getAttribute("QUANTIDADE_API"))) {
                        continue;
                    }

                    if (!TextHelper.isNull(to.getAttribute("STATUS"))) {
                        bean.setTipoContrato(to.getAttribute("STATUS").toString());
                    }
                    try {
                        if (!TextHelper.isNull(to.getAttribute("QUANTIDADE"))) {
                            bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("QUANTIDADE_API"))) {
                            bean.setQuantidadeApi(Long.valueOf(to.getAttribute("QUANTIDADE_API").toString()));
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaAverbacoesCsaApiGrafico(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaVolumeAverbacaoApiGraficoQuery query = new RelatorioSinteticoGerencialCsaVolumeAverbacaoApiGraficoQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();
                    if (!TextHelper.isNull(to.getAttribute("TIPO"))) {
                        bean.setTipoContrato(to.getAttribute("TIPO").toString());
                    }
                    try {
                        if (!TextHelper.isNull(to.getAttribute("PERCENTUAL"))) {
                            bean.setPercentual(new BigDecimal(to.getAttribute("PERCENTUAL").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaMediaCetCsa(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaMediaCetCsaQuery query = new RelatorioSinteticoGerencialCsaMediaCetCsaQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();
                    if (!TextHelper.isNull(to.getAttribute("TIPO"))) {
                        bean.setTipoContrato(to.getAttribute("TIPO").toString());
                    }
                    try {
                        if (!TextHelper.isNull(to.getAttribute("PERCENTUAL"))) {
                            bean.setPercentual(new BigDecimal(to.getAttribute("PERCENTUAL").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaQuantidadeParcelas(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaQtdeParcelasQuery query = new RelatorioSinteticoGerencialCsaQtdeParcelasQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    if (!TextHelper.isNull(to.getAttribute("PRD_DATA_DESCONTO"))) {
                        bean.setNome(DateHelper.toPeriodMesExtensoString((Date) to.getAttribute("PRD_DATA_DESCONTO")));
                    }

                    if (!TextHelper.isNull(to.getAttribute("STATUS_PARCELA"))) {
                        bean.setStatus(to.getAttribute("STATUS_PARCELA").toString());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("QUANTIDADE"))) {
                            bean.setQuantidade(Long.parseLong(to.getAttribute("QUANTIDADE").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }
                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> getQuantidadePortabilidadeCsa(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaVolPortabilidadeQuantidadeQuery query = new RelatorioSinteticoGerencialCsaVolPortabilidadeQuantidadeQuery();
            query.csaCodigo = csaCodigo;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    if (!TextHelper.isNull(to.getAttribute("PRD_DATA_DESCONTO"))) {
                    	bean.setNome(DateHelper.toPeriodMesExtensoString((Date) to.getAttribute("PRD_DATA_DESCONTO")));
                    }

                    if (!TextHelper.isNull(to.getAttribute("STATUS_PARCELA"))) {
                    	bean.setStatus(to.getAttribute("STATUS_PARCELA").toString());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("QUANTIDADE"))) {
                            bean.setQuantidade(Long.parseLong(to.getAttribute("QUANTIDADE").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }
                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaVolumeFinanceiro(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaVolumeFinanceiroQuery query = new RelatorioSinteticoGerencialCsaVolumeFinanceiroQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    if (!TextHelper.isNull(to.getAttribute("PRD_DATA_DESCONTO"))) {
                    	bean.setNome(DateHelper.toPeriodMesExtensoString((Date) to.getAttribute("PRD_DATA_DESCONTO")));
                    }

                    if (!TextHelper.isNull(to.getAttribute("STATUS_PAGAMENTO"))) {
                    	bean.setStatus(to.getAttribute("STATUS_PAGAMENTO").toString());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("VALORES"))) {
                            bean.setVlrTotal(new BigDecimal(to.getAttribute("VALORES").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }
                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaIndicadorInsucesso(String csaCodigo, String periodo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaIndicadorInsucessoQuery query = new RelatorioSinteticoGerencialCsaIndicadorInsucessoQuery();
            query.csaCodigo = csaCodigo;
            query.periodo = periodo;
            query.responsavel = responsavel;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    bean.setNome(to.getAttribute("NSE_DESCRICAO").toString());

                    if (!TextHelper.isNull(to.getAttribute("OBSERVACAO"))) {
                    	bean.setObservacao(to.getAttribute("OBSERVACAO").toString());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("PERCENTUAL"))) {
                            bean.setPercentual(new BigDecimal(to.getAttribute("PERCENTUAL").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }
                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaInadimplenciaUltMovFin(String csaCodigo, String periodo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaInadimplenciaUltMovFinQuery query = new RelatorioSinteticoGerencialCsaInadimplenciaUltMovFinQuery();
            query.csaCodigo = csaCodigo;
            query.periodo = periodo;
            query.responsavel = responsavel;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();
                    bean.setNatureza(to.getAttribute(Columns.NSE_DESCRICAO).toString());
                    bean.setTipoContrato(to.getAttribute("TIPO").toString());

                    if ("1".equals(to.getAttribute("TIPO").toString())) {
                    	bean.setNome(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.inadimplencia.ult.mov.fin.financeiro", responsavel));
                    } else {
                    	bean.setNome(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.inadimplencia.ult.mov.fin.quantidade", responsavel));
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("ENVIADO_DESCONTO"))) {
                            bean.setEnviadoDesconto(new BigDecimal(to.getAttribute("ENVIADO_DESCONTO").toString()));
                        }
                        if (!TextHelper.isNull(to.getAttribute("DESCONTO_EFETUADO"))) {
                            bean.setDescontoEfetuado(new BigDecimal(to.getAttribute("DESCONTO_EFETUADO").toString()));
                        }
                        if (!TextHelper.isNull(to.getAttribute("DESCONTO_PARCIAL"))) {
                            bean.setDescontoParcial(new BigDecimal(to.getAttribute("DESCONTO_PARCIAL").toString()));
                        }
                        if (!TextHelper.isNull(to.getAttribute("DESCONTO_NAO_EFETUADO"))) {
                            bean.setDescontoNaoEfetuado(new BigDecimal(to.getAttribute("DESCONTO_NAO_EFETUADO").toString()));
                        }
                        if (!TextHelper.isNull(to.getAttribute("INADIMPLENCIA"))) {
                            bean.setInadimplencia(new BigDecimal(to.getAttribute("INADIMPLENCIA").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }
                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaConciliacaoOrgaos(String csaCodigo, String periodo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaConciliacaoOrgQuery query = new RelatorioSinteticoGerencialCsaConciliacaoOrgQuery();
            query.csaCodigo = csaCodigo;
            query.periodo = periodo;
            query.responsavel = responsavel;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    if (TextHelper.isNull(to.getAttribute("TIPO"))) {
                        continue;
                    }

                    bean.setNome(to.getAttribute("NOME").toString());
                    bean.setTipoContrato(to.getAttribute("TIPO").toString());

                    try {
                        if (!TextHelper.isNull(to.getAttribute("PERCENTUAL"))) {
                            bean.setPercentual(new BigDecimal(to.getAttribute("PERCENTUAL").toString()));
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaVolumePortabilidadeCsaGrafico(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaVolumePortabilidadeGraficoQuery query = new RelatorioSinteticoGerencialCsaVolumePortabilidadeGraficoQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    if (TextHelper.isNull(to.getAttribute("TIPO")) && TextHelper.isNull(to.getAttribute("PERCENTUAL"))) {
                        continue;
                    }

                    if (!TextHelper.isNull(to.getAttribute("TIPO"))) {
                        bean.setTipoContrato(to.getAttribute("TIPO").toString());
                    }
                    try {
                        if (!TextHelper.isNull(to.getAttribute("PERCENTUAL"))) {
                            bean.setPercentual(new BigDecimal(to.getAttribute("PERCENTUAL").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaVolumePortabilidadeCsaTipo(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaVolumePortabilidadeTipoQuery query = new RelatorioSinteticoGerencialCsaVolumePortabilidadeTipoQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    if (TextHelper.isNull(to.getAttribute("TIPO")) && TextHelper.isNull(to.getAttribute("PERCENTUAL"))) {
                        continue;
                    }

                    if (!TextHelper.isNull(to.getAttribute("TIPO"))) {
                        bean.setTipoContrato(to.getAttribute("TIPO").toString());
                    }

                    if (!TextHelper.isNull(to.getAttribute("NOME"))) {
                        bean.setNome(to.getAttribute("NOME").toString());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("PERCENTUAL"))) {
                            bean.setPercentual(new BigDecimal(to.getAttribute("PERCENTUAL").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("QUANTIDADE"))) {
                            bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaDiffBloqueiosDesbloqueiosCsa(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaBloqueiosQuery query = new RelatorioSinteticoGerencialCsaBloqueiosQuery();
            query.csaCodigo = csaCodigo;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    try {
                        if (!TextHelper.isNull(to.getAttribute("DATA_INICIO"))) {
                            bean.setDataInicio((Date) to.getAttribute("DATA_INICIO"));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("DATA_FIM"))) {
                            bean.setDataFim((Date) to.getAttribute("DATA_FIM"));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("QUANTIDADE"))) {
                            bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("OBSERVACAO"))) {
                            bean.setStatus(to.getAttribute("OBSERVACAO").toString());
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> getUltimaAtualizacaoCetCsa(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetQuery query = new RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetQuery();
            query.csaCodigo = csaCodigo;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    if (TextHelper.isNull(to.getAttribute("DATA_INICIO"))) {
                        continue;
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("DATA_INICIO"))) {
                            bean.setDataInicio((Date) (to.getAttribute("DATA_INICIO")));
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listaUltimaAtualizacaoCetSvcCsa(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetSvcQuery query = new RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetSvcQuery();
            query.csaCodigo = csaCodigo;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    if (!TextHelper.isNull(to.getAttribute("NOME"))) {
                        bean.setNome(to.getAttribute("NOME").toString());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("VALOR"))) {
                            bean.setVlrTotal(new BigDecimal(to.getAttribute("VALOR").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("DATA_FIM"))) {
                            bean.setDataFim((Date) (to.getAttribute("DATA_FIM")));
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> getMediaParcelaCsa(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaMedioParcelasQuery query = new RelatorioSinteticoGerencialCsaMedioParcelasQuery();
            query.csaCodigo = csaCodigo;
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    if (TextHelper.isNull(to.getAttribute("VALOR_MEDIO"))) {
                        continue;
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("VALOR_MEDIO"))) {
                            bean.setVlrTotal(new BigDecimal(to.getAttribute("VALOR_MEDIO").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> getQuantidadeCargosBloqueadosCsa(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFO_VINC_BLOQ_PADRAO, responsavel);
            final boolean bloqPadrao = CodedValues.TPA_SIM.equals(pcsVlr);

            final RelatorioSinteticoGerencialCsaQuantidadeCargosBloqueadosQuery query = new RelatorioSinteticoGerencialCsaQuantidadeCargosBloqueadosQuery();
            query.csaCodigo = csaCodigo;
            query.defaultBloqueio = bloqPadrao;

            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    try {
                        if (!TextHelper.isNull(to.getAttribute("QUANTIDADE")) && (Long.valueOf(to.getAttribute("QUANTIDADE").toString()) > 0)) {
                            bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                        } else {
                            continue;
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listCargosBloqueadosCsa(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFO_VINC_BLOQ_PADRAO, responsavel);
            final boolean bloqPadrao = CodedValues.TPA_SIM.equals(pcsVlr);

            final RelatorioSinteticoGerencialCsaCargosBloqueadosQuery query = new RelatorioSinteticoGerencialCsaCargosBloqueadosQuery();
            query.csaCodigo = csaCodigo;
            query.defaultBloqueio = bloqPadrao;

            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    try {
                        if (!TextHelper.isNull(to.getAttribute("NOME"))) {
                            bean.setNome(to.getAttribute("NOME").toString());
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listStatusServidores(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final RelatorioSinteticoGerencialCsaDadosServidoresQuery query = new RelatorioSinteticoGerencialCsaDadosServidoresQuery();
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    try {
                        if (!TextHelper.isNull(to.getAttribute("STATUS"))) {
                            bean.setNome(to.getAttribute("STATUS").toString());
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("QUANTIDADE"))) {
                            bean.setQuantidade(Long.valueOf(to.getAttribute("QUANTIDADE").toString()));
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<SinteticoGerencialConsignatariaBean> listDadosUltimoMovimentoComparado(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            final List<SinteticoGerencialConsignatariaBean> retorno = new ArrayList<>();

            final List<TransferObject> lstHistoricoRetorno = impRetornoController.lstHistoricoConclusaoRetorno(null, 2, null, true, responsavel);

            String ultimoPeriodoProcessado = "";
            String antesUltimoPeriodoProcessado = "";

            for (final TransferObject historicoRetorno : lstHistoricoRetorno) {
                if(TextHelper.isNull(ultimoPeriodoProcessado)) {
                    ultimoPeriodoProcessado = DateHelper.format((Date) historicoRetorno.getAttribute(Columns.HCR_PERIODO), "yyyy-MM-dd");
                } else {
                    antesUltimoPeriodoProcessado = DateHelper.format((Date) historicoRetorno.getAttribute(Columns.HCR_PERIODO), "yyyy-MM-dd");
                    break;
                }
            }

            if(TextHelper.isNull(antesUltimoPeriodoProcessado) && TextHelper.isNull(ultimoPeriodoProcessado)) {
                return retorno;
            } else if (!TextHelper.isNull(ultimoPeriodoProcessado) && TextHelper.isNull(antesUltimoPeriodoProcessado)) {
                antesUltimoPeriodoProcessado = ultimoPeriodoProcessado;
            }

            final RelatorioSinteticoGerencialCsaUltimoMovimentoQuery query = new RelatorioSinteticoGerencialCsaUltimoMovimentoQuery();
            query.csaCodigo = csaCodigo;
            query.ultimoPeriodoProcessado = ultimoPeriodoProcessado;
            query.AntesUltimoPeriodoProcessado = antesUltimoPeriodoProcessado;
            query.responsavel = responsavel;

            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final SinteticoGerencialConsignatariaBean bean = new SinteticoGerencialConsignatariaBean();

                    try {
                        if (!TextHelper.isNull(to.getAttribute("TIPO"))) {
                            bean.setTipoContrato(to.getAttribute("TIPO").toString());
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("NOME"))) {
                            bean.setNome(to.getAttribute("NOME").toString());
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("SIMBOLO"))) {
                            String simbolo = to.getAttribute("SIMBOLO").toString();
                            if(simbolo.equalsIgnoreCase("m")) {
                                bean.setSimbolo(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel));
                            } else if(simbolo.equalsIgnoreCase("p")) {
                                bean.setSimbolo("%");
                            }
                        } else {
                            bean.setSimbolo("");
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("QUANTIDADE"))) {
                            bean.setVlrTotal(new BigDecimal(to.getAttribute("QUANTIDADE").toString()));
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("PERCENTUAL"))) {
                            bean.setPercentual(new BigDecimal(to.getAttribute("PERCENTUAL").toString()));
                        }
                    } catch (final Exception e) {
                        LOG.warn(e.getMessage());
                    }

                    try {
                        if (!TextHelper.isNull(to.getAttribute("STATUS"))) {
                            bean.setStatus(to.getAttribute("STATUS").toString());
                        }
                    } catch (final NumberFormatException e) {
                        LOG.warn(e.getMessage());
                    }

                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException | ImpRetornoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public HashMap<String, Object> geraInformacoesSinteticoGerencialGeralCsa(HashMap<String, Object> parameters, String ultimoPeriodoProcessado, String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {

        try {
            criarPivotAux();
            try {
                final ConsignanteTransferObject cse = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                final List<ConsignanteTransferObject> consignante = new ArrayList<>();
                consignante.add(cse);
                parameters.put("CONSIGNANTE", consignante);
            } catch (final ConsignanteControllerException e) {
                LOG.error("ERROR: " + e.getMessage());
            }

            final String linkAcesso = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel).toString() : "";
            String dataImplantacao = "";
            if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_DATA_IMPLANTACAO_SISTEMA, responsavel))) {
                try {
                    dataImplantacao = DateHelper.reformat(ParamSist.getInstance().getParam(CodedValues.TPC_DATA_IMPLANTACAO_SISTEMA, responsavel).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
                } catch (final ParseException e) {
                    LOG.error("Não foi possível realizar a conversão da data de implantação do sistema.", e);
                }
            }
            String diaCorte = "";
            String diaRepasse = "";
            final Boolean ocultarDiaCorteDiaRepasse = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_OCULTAR_DIA_CORTE_DIA_REPASSE_DO_RELATORIO_GERENCIAL_CSA, responsavel)) ? ParamSist.getBoolParamSist(CodedValues.TPC_OCULTAR_DIA_CORTE_DIA_REPASSE_DO_RELATORIO_GERENCIAL_CSA, responsavel) : false;
            if(!ocultarDiaCorteDiaRepasse) {
                try {
                    diaCorte = String.valueOf(PeriodoHelper.getInstance().getProximoDiaCorte(null, responsavel));
                    diaRepasse = String.valueOf(RepasseHelper.getDiaRepasse(null, PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel), responsavel));
                } catch (PeriodoException|ViewHelperException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            final String versaoAtual = ApplicationResourcesHelper.getMessage("release.tag", responsavel);
            final Boolean moduloCompra = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, responsavel);
            final String emailSuporte = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel).toString() : "";
            final String telefoneSuporte = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TELEFONE_SUPORTE_ZETRASOFT, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_TELEFONE_SUPORTE_ZETRASOFT, responsavel).toString() : "";

            parameters.put("LINK_ACESSO_SISTEMA", linkAcesso);
            parameters.put("DATA_IMPLANTACAO", dataImplantacao);
            parameters.put("DIA_CORTE", diaCorte);
            parameters.put("DIA_REPASSE", diaRepasse);
            parameters.put("OCULTAR_DIA_CORTE_DIA_REPASSE", ocultarDiaCorteDiaRepasse);
            parameters.put("VERSAO_ATUAL_SISTEMA", versaoAtual);
            parameters.put("MODULO_COMPRA", moduloCompra);
            parameters.put("EMAIL_SUPORTE", emailSuporte);
            parameters.put("TELEFONE_SUPORTE", telefoneSuporte);

            parameters.put("LIST_AVERBACOES", listaAverbacoesCsa(csaCodigo, responsavel));
            parameters.put("LIST_AVERBACOES_GRAFICO", listaAverbacoesCsaGrafico(csaCodigo, responsavel));
            parameters.put("LIST_AVERBACOES_API", listaAverbacoesCsaApi(csaCodigo, responsavel));
            parameters.put("LIST_AVERBACOES_API_GRAFICO", listaAverbacoesCsaApiGrafico(csaCodigo, responsavel));
            parameters.put("LIST_MEDIA_CET", listaMediaCetCsa(csaCodigo, responsavel));
            parameters.put("LIST_QUANTIDADE_PARCELAS", listaQuantidadeParcelas(csaCodigo, responsavel));
            parameters.put("LIST_VOLULME_FINANCEIRO", listaVolumeFinanceiro(csaCodigo, responsavel));
            parameters.put("LIST_INDICADOR_INSUCESSO", listaIndicadorInsucesso(csaCodigo, ultimoPeriodoProcessado, responsavel));
            parameters.put("LIST_INADIMPLENCIA_ULT_MOV_FIN", listaInadimplenciaUltMovFin(csaCodigo, ultimoPeriodoProcessado, responsavel));
            parameters.put("LIST_CONCILIACAO_ORGAOS", listaConciliacaoOrgaos(csaCodigo, ultimoPeriodoProcessado, responsavel));
            parameters.put("QUANTIDADE_PORTABILIDADE", getQuantidadePortabilidadeCsa(csaCodigo, responsavel));
            parameters.put("VOLUME_PORTABILIDADE_GRAFICO", listaVolumePortabilidadeCsaGrafico(csaCodigo, responsavel));
            parameters.put("VOLUME_PORTABILIDADE_TIPO", listaVolumePortabilidadeCsaTipo(csaCodigo, responsavel));
            parameters.put("BLOQUEIOS_CSA", listaDiffBloqueiosDesbloqueiosCsa(csaCodigo, responsavel));
            parameters.put("ULTIMA_ATUALIZACAO_CET", getUltimaAtualizacaoCetCsa(csaCodigo, responsavel));
            parameters.put("ULTIMA_ATUALIZACAO_CET_SVC", listaUltimaAtualizacaoCetSvcCsa(csaCodigo, responsavel));
            parameters.put("LIST_MEDIA_VLR_PARCELA", getMediaParcelaCsa(csaCodigo, responsavel));
            parameters.put("QUANTIDADE_CARGOS_BLOQUEADOS", getQuantidadeCargosBloqueadosCsa(csaCodigo, responsavel));
            parameters.put("LIST_CARGOS_BLOQUEADOS", listCargosBloqueadosCsa(csaCodigo, responsavel));
            parameters.put("LIST_STATUS_SERVIDORES", listStatusServidores(csaCodigo, responsavel));

            final List<SinteticoGerencialConsignatariaBean> listBean = new ArrayList<>();

            final Date periodoProcessado = DateHelper.parse(ultimoPeriodoProcessado, "yyyy-MM-dd");
            final SinteticoGerencialConsignatariaBean sinteticoGerencialConsignatariaBean = new SinteticoGerencialConsignatariaBean();
            sinteticoGerencialConsignatariaBean.setNome(DateHelper.toPeriodMesExtensoString(periodoProcessado));
            listBean.add(sinteticoGerencialConsignatariaBean);

            parameters.put("DATA_ULTIMO_MOVIMENTO", listBean);
            parameters.put("ULTIMO_MOVIMENTO", listDadosUltimoMovimentoComparado(csaCodigo, responsavel));

        } catch (RelatorioControllerException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return parameters;
    }

    @Override
    public List<ContratosPorCsaBean> lstConsignatariaSituacao(AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<ContratosPorCsaBean> retorno = new ArrayList<>();

            final RelatorioGerencialGeralListaCsaSituacaoObsQuery query = new RelatorioGerencialGeralListaCsaSituacaoObsQuery();
            final List<TransferObject> lista = query.executarDTO();
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final ContratosPorCsaBean bean = new ContratosPorCsaBean();
                    if (!TextHelper.isNull(to.getAttribute("CONSIGNATARIA"))) {
                        bean.setConsignataria(to.getAttribute("CONSIGNATARIA").toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute("STATUS"))) {
                        bean.setStatus(to.getAttribute("STATUS").toString());
                    }
                    if (!TextHelper.isNull(to.getAttribute("OBSERVACAO"))) {
                        bean.setObservacao(to.getAttribute("OBSERVACAO").toString());
                    }
                    retorno.add(bean);
                }
            }

            return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }
    
    @Override
    public List<TermoUsoPrivacidadeAdesaoBean> lstTermoUsoPrivacidadeAdesaoAutorizado(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            List<TermoUsoPrivacidadeAdesaoBean> retorno = new ArrayList<>();
            final RelatorioTermoUsoPrivacidadeAdesaoAutorizadoQuery query = new RelatorioTermoUsoPrivacidadeAdesaoAutorizadoQuery();
            query.setCriterios(criterios);
            final List<TransferObject> lista = query.executarDTO();
            
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final TermoUsoPrivacidadeAdesaoBean bean = new TermoUsoPrivacidadeAdesaoBean();

                    bean.setUsuNome(to.getAttribute(Columns.USU_NOME) != null ? to.getAttribute(Columns.USU_NOME).toString() : "");
                    bean.setUsuLogin(to.getAttribute(Columns.USU_LOGIN) != null ? to.getAttribute(Columns.USU_LOGIN).toString() : "");
                    bean.setUsuTel(to.getAttribute(Columns.USU_TEL) != null ? to.getAttribute(Columns.USU_TEL).toString() : "");
                    bean.setUsuEmail(to.getAttribute(Columns.USU_EMAIL) != null ? to.getAttribute(Columns.USU_EMAIL).toString() : "");
                    bean.setUsuCPF(to.getAttribute(Columns.USU_CPF) != null ? to.getAttribute(Columns.USU_CPF).toString() : "");
                    bean.setStuDescricao(to.getAttribute(Columns.STU_DESCRICAO) != null ? to.getAttribute(Columns.STU_DESCRICAO).toString() : "");
                    bean.setEntidade(to.getAttribute("ENTIDADE") != null ? to.getAttribute("ENTIDADE").toString() : "");
                    bean.setData(to.getAttribute(Columns.OUS_DATA) != null ? to.getAttribute(Columns.OUS_DATA).toString() : "");
                    bean.setIpAcesso(to.getAttribute(Columns.OUS_IP_ACESSO) != null ? to.getAttribute(Columns.OUS_IP_ACESSO).toString() : "");
                    bean.setAceitacaoVia(to.getAttribute("ACEITACAO_VIA") != null ? to.getAttribute("ACEITACAO_VIA").toString() : "");
                    bean.setDocumentoAceito(to.getAttribute("DOCUMENTO_ACEITO") != null ? to.getAttribute("DOCUMENTO_ACEITO").toString() : "");
                    
                    retorno.add(bean);
               }
           }
           return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<TermoUsoPrivacidadeAdesaoBean> lstTermoAdesaoNaoAutorizado(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            List<TermoUsoPrivacidadeAdesaoBean> retorno = new ArrayList<>();
            final RelatorioTermoAdesaoNaoAutorizadoQuery query = new RelatorioTermoAdesaoNaoAutorizadoQuery();
            query.setCriterios(criterios);
            final List<TransferObject> lista = query.executarDTO();
            
            if (lista != null) {
                for (final TransferObject to : lista) {
                    final TermoUsoPrivacidadeAdesaoBean bean = new TermoUsoPrivacidadeAdesaoBean();

                    bean.setUsuNome(to.getAttribute(Columns.USU_NOME) != null ? to.getAttribute(Columns.USU_NOME).toString() : "");
                    bean.setUsuLogin(to.getAttribute(Columns.USU_LOGIN) != null ? to.getAttribute(Columns.USU_LOGIN).toString() : "");
                    bean.setUsuTel(to.getAttribute(Columns.USU_TEL) != null ? to.getAttribute(Columns.USU_TEL).toString() : "");
                    bean.setUsuEmail(to.getAttribute(Columns.USU_EMAIL) != null ? to.getAttribute(Columns.USU_EMAIL).toString() : "");
                    bean.setUsuCPF(to.getAttribute(Columns.USU_CPF) != null ? to.getAttribute(Columns.USU_CPF).toString() : "");
                    bean.setStuDescricao(to.getAttribute(Columns.STU_DESCRICAO) != null ? to.getAttribute(Columns.STU_DESCRICAO).toString() : "");
                    bean.setEntidade(to.getAttribute("ENTIDADE") != null ? to.getAttribute("ENTIDADE").toString() : "");
                    bean.setData(to.getAttribute(Columns.LTU_DATA) != null ? to.getAttribute(Columns.LTU_DATA).toString() : "");
                    bean.setIpAcesso(to.getAttribute(Columns.LTU_IP_ACESSO) != null ? to.getAttribute(Columns.LTU_IP_ACESSO).toString() : "");
                    bean.setAceitacaoVia(to.getAttribute("ACEITACAO_VIA") != null ? to.getAttribute("ACEITACAO_VIA").toString() : "");
                    bean.setDocumentoAceito(to.getAttribute("DOCUMENTO_ACEITO") != null ? to.getAttribute("DOCUMENTO_ACEITO").toString() : "");
                    
                    retorno.add(bean);
               }
           }
           return retorno;
        } catch (final HQueryException e) {
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }
    
    @Override
    public List<RegrasConvenioOrgaoBean> listaOrgaosSerRegrasConvenio(AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<TransferObject> lstOrgaosSer = servidorController.countQtdeServidorPorOrg(responsavel);
            final List<RegrasConvenioOrgaoBean> retorno =  new ArrayList<>();

            if ((lstOrgaosSer != null) && !lstOrgaosSer.isEmpty()) {
                for (final TransferObject orgaoSer : lstOrgaosSer) {
                	final String nome = (String) orgaoSer.getAttribute("nome");
                	final String cnpj = (String) orgaoSer.getAttribute("cnpj");
                	final Long valor = (Long) orgaoSer.getAttribute("valor");

                    retorno.add(new RegrasConvenioOrgaoBean(nome, cnpj, valor));
                }
            }

            return retorno;
        } catch (final ServidorControllerException ex) {
            LOG.debug(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
    
    @Override
    public List<RegrasConvenioMargensBean> lstMargensRegrasConvenio(AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<TransferObject> margens = margemController.lstMargem(responsavel);
            final List<RegrasConvenioMargensBean> retorno =  new ArrayList<>();

            if ((margens != null) && !margens.isEmpty()) {
            	final String rotuloSim = ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel);
                final String rotuloNao = ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
                final String rotuloPorcentagem = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.margem.simbolo.porcentagem", responsavel);
                final String rotuloNadaEncontrado = ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel);
                for (final TransferObject margem : margens) {
                	final String marDescricao = (String) margem.getAttribute(Columns.MAR_DESCRICAO);
                	final BigDecimal marPorcentagem = (BigDecimal) margem.getAttribute(Columns.MAR_PORCENTAGEM);
                	final Character exibeMargemNegativaCsa = (Character) margem.getAttribute(Columns.MAR_EXIBE_CSA);
                	
                	String porcentagem = !TextHelper.isNull(marPorcentagem) ? NumberHelper.formata(marPorcentagem.doubleValue(), "#,##0.00; #,##0.00") + " " + rotuloPorcentagem : rotuloNadaEncontrado;

                    retorno.add(new RegrasConvenioMargensBean(marDescricao, porcentagem, "0".equals(exibeMargemNegativaCsa.toString()) ? rotuloNao : rotuloSim));
                }
            }

            return retorno;
        } catch (final MargemControllerException ex) {
            LOG.debug(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<RegrasConvenioParametrosBean> listaParamServicosRegrasConvenio(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            RelatorioRegrasConvenioListaConveniosQuery queryCnv = new RelatorioRegrasConvenioListaConveniosQuery(responsavel);
            List<TransferObject> lstConvenios = queryCnv.executarDTO();
            Map<String, TransferObject> mapConvenios = new HashMap<>();

            if ((lstConvenios != null) && !lstConvenios.isEmpty()) {
                for (TransferObject convenio : lstConvenios) {
                    mapConvenios.put(convenio.getAttribute(Columns.CSA_CODIGO).toString(), convenio);
                }
            }

            RelatorioRegrasConvenioListaServicosQuery querySvc = new RelatorioRegrasConvenioListaServicosQuery(csaCodigo, responsavel);
            List<TransferObject> lstParamServicos = querySvc.executarDTO();

            Set<RegrasConvenioParametrosBean> retorno = new HashSet<>();
            
            // Obtém o convênio correspondente
            TransferObject convenio = mapConvenios.get(csaCodigo);
            String rotuloSim = ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel);
            String rotuloNao = ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
            String salaryPay = (convenio != null && !TextHelper.isNull(convenio.getAttribute("SALARY_PAY")) && "1".equals(convenio.getAttribute("SALARY_PAY").toString())) ? rotuloSim : rotuloNao;
            String acessaApi = (convenio != null && !TextHelper.isNull(convenio.getAttribute("ACESSA_API")) && ("1".equals(convenio.getAttribute("ACESSA_API").toString()) || "1".equals(convenio.getAttribute("ACESSA_API_1").toString()))) ? rotuloSim : rotuloNao;
            retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_SALARY_PAY, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servico.salary.pay", responsavel), salaryPay));
            retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_ACESSA_API, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servico.acessa.api", responsavel), acessaApi));
            
            if ((lstParamServicos != null) && !lstParamServicos.isEmpty()) {           	
                for (TransferObject servico : lstParamServicos) {                     	
                    String svcCodigoRegra = servico.getAttribute(Columns.SVC_CODIGO).toString();
                    String svcDescricao = servico.getAttribute(Columns.SVC_DESCRICAO).toString();
                    String tpsCodigo = servico.getAttribute(Columns.TPS_CODIGO).toString();
                    String tpsDescricao = servico.getAttribute(Columns.TPS_DESCRICAO).toString();
                    String pseVlr = (String) servico.getAttribute(Columns.PSE_VLR);
                    String pseVlrRef = (String) servico.getAttribute(Columns.PSE_VLR_REF);
                    
                    String descricao = svcDescricao + " - " + tpsDescricao;                    
                    RegrasConvenioParametrosBean parametroSvc = new RegrasConvenioParametrosBean(tpsCodigo, descricao, !TextHelper.isNull(pseVlr) ? pseVlr : pseVlrRef);
                    parametroSvc.setSvcCodigo(svcCodigoRegra);
                    
                    // Adiciona os parâmetros relacionados ao serviço
                    retorno.add(parametroSvc);
                }
            }
            return new ArrayList<>(retorno);
        } catch (HQueryException ex) {
            LOG.debug(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }        
    
    @Override
    public List<RegrasConvenioParametrosBean> listaParamOrgaosSerRegrasConvenio(AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<TransferObject> lstOrgaosSer = servidorController.countQtdeServidorPorOrg(responsavel);
            final List<RegrasConvenioParametrosBean> retorno =  new ArrayList<>();

            if ((lstOrgaosSer != null) && !lstOrgaosSer.isEmpty()) {
                for (final TransferObject orgaoSer : lstOrgaosSer) {
                	final String orgCodigo = (String) orgaoSer.getAttribute("codigo");
                	final String nomeOrg = (String) orgaoSer.getAttribute("nome");
                	final String cnpj = (String) orgaoSer.getAttribute("cnpj");
                	final Long quantidadeServidores = (Long) orgaoSer.getAttribute("valor");
                	
                	String chaveCnpj = nomeOrg + " - " + ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.orgaos.cnpj", responsavel);
                	String chaveQtdadeServidores = nomeOrg + " - " + ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.orgaos.qtdade.ser", responsavel);
                
                    retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_ORG_CNPJ, chaveCnpj, cnpj, orgCodigo));
                    retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_ORG_QUANTIDADE_SERVIDORES, chaveQtdadeServidores, quantidadeServidores.toString(), orgCodigo));
                }
            }

            return retorno;
        } catch (final ServidorControllerException ex) {
            LOG.debug(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
    
    @Override
    public List<RegrasConvenioParametrosBean> listaParamMargensRegrasConvenio(AcessoSistema responsavel) throws RelatorioControllerException {
        try {
            final List<TransferObject> margens = margemController.lstMargem(responsavel);
            final List<RegrasConvenioParametrosBean> retorno =  new ArrayList<>();

            if ((margens != null) && !margens.isEmpty()) {
            	final String rotuloSim = ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel);
                final String rotuloNao = ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
                final String rotuloPorcentagem = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.margem.simbolo.porcentagem", responsavel);
                final String rotuloNadaEncontrado = ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel);
                for (final TransferObject margem : margens) {
                	final Short marCodigo = (Short) margem.getAttribute(Columns.MAR_CODIGO);
                	final String marDescricao = (String) margem.getAttribute(Columns.MAR_DESCRICAO);
                	final BigDecimal marPorcentagem = (BigDecimal) margem.getAttribute(Columns.MAR_PORCENTAGEM);
                	final Character exibeMargemNegativaCsa = (Character) margem.getAttribute(Columns.MAR_EXIBE_CSA);
                	
                	String porcentagem = !TextHelper.isNull(marPorcentagem) ? NumberHelper.formata(marPorcentagem.doubleValue(), "#,##0.00; #,##0.00") + " " + rotuloPorcentagem : rotuloNadaEncontrado;

                	String chavePorcentegem = marDescricao + " - " + ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.margem.porcentagem", responsavel);
                	String chaveExibeMargemNegativaCsa = marDescricao + " - " + ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.margem.exibe.negativa.csa", responsavel);
                	
                    retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_MAR_PORCENTAGEM, chavePorcentegem, porcentagem, marCodigo));
                    retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_MAR_EXIBE_MARGEM_NEGATIVA_CSA, chaveExibeMargemNegativaCsa, "0".equals(exibeMargemNegativaCsa.toString()) ? rotuloNao : rotuloSim, marCodigo));
                }
            }

            return retorno;
        } catch (final MargemControllerException ex) {
            LOG.debug(ex.getMessage(), ex);
            throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
    
    @Override
    public void enviarNotificacaoCsaAlteracaoRegrasConvenio(AcessoSistema responsavel) throws RelatorioControllerException {
    	try {
	    	List<TransferObject> paramCsaList = parametroController.selectParamCsa(null, CodedValues.TPA_EMAIL_CSA_NOTIFICACAO_ALTERACAO_REGRAS_CONVENIO, responsavel);
			List<String> csasHabilitadasParaNotificacao = new ArrayList<>();
	        
			if (paramCsaList != null && !paramCsaList.isEmpty()) {
			    Set<String> csasHabilitadasParaNotificacaoSet = new HashSet<>();
			    for (final TransferObject paramCsa : paramCsaList) {
			        String pcsVlr = (String) paramCsa.getAttribute(Columns.PCS_VLR);
			        boolean permiteEnvioEmailCsaRCO = !TextHelper.isNull(pcsVlr) && pcsVlr.equals("S");
			        if (permiteEnvioEmailCsaRCO) {
			            String csaCodigo = paramCsa.getAttribute(Columns.PCS_CSA_CODIGO).toString();
			            csasHabilitadasParaNotificacaoSet.add(csaCodigo);
			        }
			    }				    
			    csasHabilitadasParaNotificacao.addAll(csasHabilitadasParaNotificacaoSet);
			}
	        
	        if((csasHabilitadasParaNotificacao != null) && !csasHabilitadasParaNotificacao.isEmpty()) {	  
	        	ProcessaRelatorioRegrasConvenio relatorio = new ProcessaRelatorioRegrasConvenio(null, null, null, false, responsavel);		        	
				for(String csaCodigo : csasHabilitadasParaNotificacao) {		
					List<RegrasConvenioParametrosBean> listaParamSistema = relatorio.getListParametros();
					List<RegrasConvenioParametrosBean> listaParamServico = listaParamServicosRegrasConvenio(csaCodigo, responsavel);	
		        	List<RegrasConvenioParametrosBean> listaParamOrgSer = listaParamOrgaosSerRegrasConvenio(responsavel);
					List<RegrasConvenioParametrosBean> listaParamMargens = listaParamMargensRegrasConvenio(responsavel);
		        	List<RegrasConvenioParametrosBean> listaParametros = new ArrayList<>();
		        	
					List<TransferObject> listaRegraConvenioByCsa = regraConvenioController.listaRegrasConvenioByCsa(csaCodigo, responsavel);
					//Caso não existam dados da CSA na tabela, os registros serão inseridos.
					if((listaRegraConvenioByCsa == null) || listaRegraConvenioByCsa.isEmpty()) {						
						listaParametros.addAll(listaParamSistema);
						listaParametros.addAll(listaParamServico);
						listaParametros.addAll(listaParamOrgSer);
						listaParametros.addAll(listaParamMargens);
						regraConvenioController.salvarRegrasConvenio(listaParametros, csaCodigo, responsavel);
					} else {
						Map<String, RegrasConvenioParametrosBean> mapaSistema = listaParamSistema.stream()
								.collect(Collectors.toMap(p -> p.getCodigo(), p -> p));						
						Map<String, RegrasConvenioParametrosBean> mapaServico = listaParamServico.stream()
								.collect(Collectors.toMap(p -> p.getCodigo() + "|" + p.getSvcCodigo(), p -> p));						
						Map<String, RegrasConvenioParametrosBean> mapaOrgao = listaParamOrgSer.stream()
								.collect(Collectors.toMap(p -> p.getCodigo() + "|" + p.getOrgCodigo(), p -> p));						
						Map<String, RegrasConvenioParametrosBean> mapaMargem = listaParamMargens.stream()
								.collect(Collectors.toMap(p -> p.getCodigo() + "|" + p.getMarCodigo(), p -> p));
						
						List<TransferObject> listaRegrasConvenioOld = regraConvenioController.listaRegrasConvenioByCsa(csaCodigo, responsavel);
						List<RegrasConvenioParametrosBean> dadosAlterados = new ArrayList<>();
						for(final TransferObject regraOld : listaRegrasConvenioOld) {
							String rcoCampoCodigoOld = regraOld.getAttribute(Columns.RCO_CAMPO_CODIGO).toString();
							String rcoCampoValorOld = regraOld.getAttribute(Columns.RCO_CAMPO_VALOR).toString();
							String svcCodigoOld = (String) regraOld.getAttribute(Columns.RCO_SVC_CODIGO);
							String orgCodigoOld = (String) regraOld.getAttribute(Columns.RCO_ORG_CODIGO);
							Short marCodigoOld = !TextHelper.isNull(regraOld.getAttribute(Columns.RCO_MAR_CODIGO)) ? Short.valueOf(regraOld.getAttribute(Columns.RCO_MAR_CODIGO).toString()) : null;
							
							RegrasConvenioParametrosBean parametro = null;
							
							if (!TextHelper.isNull(svcCodigoOld)) {
								parametro = mapaServico.get(rcoCampoCodigoOld + "|" + svcCodigoOld);
							} else if (!TextHelper.isNull(orgCodigoOld)) {
								parametro = mapaOrgao.get(rcoCampoCodigoOld + "|" + orgCodigoOld);
							} else if (!TextHelper.isNull(marCodigoOld)) {
								parametro = mapaMargem.get(rcoCampoCodigoOld + "|" + marCodigoOld);
							} else {
								parametro = mapaSistema.get(rcoCampoCodigoOld);
							}
							
							if (parametro != null && !parametro.getValor().equals(rcoCampoValorOld)) {
								dadosAlterados.add(parametro);
							}
						}			
						if(dadosAlterados != null && !dadosAlterados.isEmpty()) {	
							//Deleta todas as regras da csa
							regraConvenioController.removeRegrasConvenioByCsa(csaCodigo, responsavel);
							//Insere novamente todas regras com os novos valores
							listaParametros.addAll(listaParamSistema);
							listaParametros.addAll(listaParamServico);
							listaParametros.addAll(listaParamOrgSer);
							listaParametros.addAll(listaParamMargens);
							regraConvenioController.salvarRegrasConvenio(listaParametros, csaCodigo, responsavel);	
							//Envia notificação para CSA sobre alteração nas regras de convênio
							consignatariaController.enviarNotificacaoAlteracaoRegrasConvenio(csaCodigo, dadosAlterados, responsavel);
						}
					}					
				}
	        }
    	} catch (ZetraException e) {
			LOG.error(e.getMessage(), e);
			throw new RelatorioControllerException("mensagem.erroInternoSistema", responsavel, e);
		}
    } 
}