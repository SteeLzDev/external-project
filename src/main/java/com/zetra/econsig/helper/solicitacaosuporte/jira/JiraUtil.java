package com.zetra.econsig.helper.solicitacaosuporte.jira;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ImpRetornoDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ValidaImportacaoControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.solicitacaosuporte.SolicitacaoSuporteConfig;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.service.folha.ValidaImportacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.jira.JiraEConsig;
import com.zetra.jira.exception.JiraException;

/**
 * <p>Title: JiraUtil</p>
 * <p>Description: Métodos para fazer integração da biblioteca JiraEConsig com o eConsig eConsig</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class JiraUtil {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(JiraUtil.class);

    private static String host = null;
    private static String port = null;
    private static String user = null;
    private static String pass = null;
    private static String versaoNoveSuperior = null;

    public JiraUtil() {
        try {
            final SolicitacaoSuporteConfig ssc = ApplicationContextProvider.getApplicationContext().getBean(SolicitacaoSuporteConfig.class);

            host = ssc.getBaseurl();
            port = ssc.getBaseHttpPort();
            user = ssc.getAuthUser();
            pass = ssc.getAuthPassword();
            versaoNoveSuperior = ssc.getVersaoJira();

        } catch (final Exception ex) {
            LOG.error("Erro interno de sistema ao ler arquivo de propriedades interface jira.", ex);
        }
    }

    //Atualizações diversas de Status em tarefas da Produção são separadas por aqui
    public String atualizaStatusProducao(String tipo, AcessoSistema responsavel, String acao, String status, String fileName, String path, String obs, String orgCodigo, String estCodigo) throws ConsignanteControllerException, IOException, LogControllerException, java.text.ParseException, JiraException {

        if (acao.equals("aguardarValidacaoArquivos") || acao.equals("comentar")) {
            try {
                return atualizaStatusProducaoUpload(tipo, responsavel, acao, fileName, path, obs, orgCodigo, estCodigo);
            } catch (final ValidaImportacaoControllerException | ImpRetornoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else if (!TextHelper.isNull(status)) {
            String tipoEntidade = null;
            String codEntidade = null;
            if (!TextHelper.isNull(estCodigo)) {
                tipoEntidade = "EST";
                codEntidade = estCodigo;
            } else if (!TextHelper.isNull(orgCodigo)) {
                tipoEntidade = "ORG";
                codEntidade = orgCodigo;
            }
            return atualizaStatusProducaoProcessamento(tipo, responsavel, acao, status, tipoEntidade, codEntidade, null, obs);
        }
        return null;
    }

    /**
     * atualiza o status da issue de produção de upload de arquivo de margem/retorno/processo
      * @param tipo - retorno, margem
      * @param responsavel
      * @param acao
      * @param fileName - nome do arquivo que está sendo enviado
      * @param path - path do arquivo enviado
      * @param obs - obs
      * @param orgCodigo - codigo do órgão
      * @param estCodigo - codigo do Estabelecimento
      * @return null
      * @throws java.text.ParseException
      * @throws ConsignanteControllerException
      * @throws IOException
      * @throws LogControllerException
      * @throws ValidaImportacaoControllerException
      * @throws ImpRetornoControllerException
     * @throws JiraException
      */
    private static String atualizaStatusProducaoUpload(String tipo, AcessoSistema responsavel, String acao, String fileName, String path, String obs, String orgCodigo, String estCodigo) throws ConsignanteControllerException, IOException, LogControllerException, java.text.ParseException, ValidaImportacaoControllerException, ImpRetornoControllerException, JiraException {

        StringBuilder comentario = new StringBuilder("O arquivo de ").append(tipo).append(" ").append(fileName).append(" foi enviado \\n");
        if (obs != null && !obs.isEmpty()) {
            comentario.append(responsavel.getUsuNome()).append(" comentou: ").append(obs);
        }

        try {
            /**********Verifica se todos os arquivos de Margem e Retorno já foram enviados ***********/
            final ValidaImportacaoController validaImportacaoController = ApplicationContextProvider.getApplicationContext().getBean(ValidaImportacaoController.class);
            final Map<String, String> paramValidacaoArq = validaImportacaoController.lstParamValidacaoArq(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), null, null, responsavel);

            int qtdArqNecessariosRetorno;
            if (paramValidacaoArq != null && !paramValidacaoArq.isEmpty() && paramValidacaoArq.containsKey("retorno.qtdMinimaArquivos") && !TextHelper.isNull(paramValidacaoArq.get("retorno.qtdMinimaArquivos"))) {
                qtdArqNecessariosRetorno = Integer.parseInt(paramValidacaoArq.get("retorno.qtdMinimaArquivos"));
            } else {
                qtdArqNecessariosRetorno = 0;
            }

            int qtdArqNecessariosMargem;
            if (paramValidacaoArq != null && !paramValidacaoArq.isEmpty() && paramValidacaoArq.containsKey("margem.qtdMinimaArquivos") && !TextHelper.isNull(paramValidacaoArq.get("margem.qtdMinimaArquivos"))) {
                qtdArqNecessariosMargem = Integer.parseInt(paramValidacaoArq.get("margem.qtdMinimaArquivos"));
            } else {
                qtdArqNecessariosMargem = 0;
            }

            final int totalArquivosRetorno = FileHelper.contaArquivosUpload("retorno", tipo, path, responsavel);
            final int totalArquivosMargem = FileHelper.contaArquivosUpload("margem", tipo, path, responsavel);

            final int restantesMargem = qtdArqNecessariosMargem - totalArquivosMargem;
            final int restanteRetorno = qtdArqNecessariosRetorno - totalArquivosRetorno;
            /*****************************************************************************************/

            final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            final String cseIdInterno = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel).getIdentificadorInterno();
            final String periodo = new ImpRetornoDelegate().recuperaPeriodoRetorno(CodedValues.TIPO_RETORNO_NORMAL, null, null, null, responsavel);
            String orgId = null;
            String estId = null;
            final String usuarioNome = responsavel.getUsuNome();

            if (estCodigo != null && !estCodigo.isEmpty()) {
                estId = cseDelegate.findEstabelecimento(estCodigo, responsavel).getEstIdentificador();
            } else if (orgCodigo != null && !orgCodigo.isEmpty()) {
                final OrgaoTransferObject orgao = cseDelegate.findOrgao(orgCodigo, responsavel);
                orgId = orgao.getOrgIdentificador();
                estId = cseDelegate.findEstabelecimento(orgao.getEstCodigo(), responsavel).getEstIdentificador();
            }

            //Deve ter todos os arquivos necessários para processamento disponíveis e status compativel no JIRA
            if (acao != null && (restantesMargem <= 0) && (restanteRetorno <= 0)) {
                new JiraEConsig(host, port, user, pass, versaoNoveSuperior).atualizaStatusProducao(tipo, acao, null, fileName, path, obs, orgId, estId, periodo, cseIdInterno, usuarioNome);
            } else if(acao != null) {
                new JiraEConsig(host, port, user, pass, versaoNoveSuperior).atualizaStatusProducao(tipo, "comentar", null, fileName, path, obs, orgId, estId, periodo, cseIdInterno, usuarioNome);

            }


        }
        catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            LOG.error("Erro interno do JIRA: "+ex.getMessage());
            throw new JiraException("Não foi possível integrar com o Jira");
        } finally {
            //Registra em LOG independente de ter sido registrado no JIRA
            if (!TextHelper.isNull(obs)) {
                final LogDelegate log = new LogDelegate(responsavel, Log.JIRA, Log.COMENTARIO_IMP_RETORNO, Log.LOG_INFORMACAO);
                log.add(comentario.toString().replace("\\n", "<BR>"));
                log.write();
            }
        }

        return null;
    }

    /**
     * atualiza o status da issue de produção processamento de arquivo de margem/retorno
     * @param tipo - retorno, margem
     * @param responsavel
     * @param acao - ações descritas no SolicitacaoSuporteApi.properties
     * @param status - status.name descritos no SolicitacaoSuporteApi.properties
     * @param codEntidade - codigo da entidade corrente
     * @param periodo - periodo MM/yyyy
     * @param obs - obs
     * @return null
     * @throws java.text.ParseException
     * @throws ConsignanteControllerException
     * @throws IOException
     * @throws LogControllerException
     */
    private static String atualizaStatusProducaoProcessamento(String tipo, AcessoSistema responsavel, String acao, String status, String tipoEntidade, String codEntidade, String periodo, String obs) throws ConsignanteControllerException, IOException, LogControllerException, java.text.ParseException {
        try {
            final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            final String cseIdInterno = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel).getIdentificadorInterno();
            if (TextHelper.isNull(periodo)) {
                periodo = new ImpRetornoDelegate().recuperaPeriodoRetorno(CodedValues.TIPO_RETORNO_NORMAL, null, null, null, responsavel);
            }

            String orgId = null;
            String estId = null;
            final String usuarioNome = responsavel.getUsuNome();

            if (!TextHelper.isNull(tipoEntidade) && tipoEntidade.equalsIgnoreCase("EST")) {
                estId = cseDelegate.findEstabelecimento(codEntidade, responsavel).getEstIdentificador();
            } else if (!TextHelper.isNull(tipoEntidade) && tipoEntidade.equalsIgnoreCase("ORG")) {
                final OrgaoTransferObject orgao = cseDelegate.findOrgao(codEntidade, responsavel);
                orgId = orgao.getOrgIdentificador();
                estId = cseDelegate.findEstabelecimento(orgao.getEstCodigo(), responsavel).getEstIdentificador();
            }

            new JiraEConsig(host, port, user, pass, versaoNoveSuperior).atualizaStatusProducao(tipo, acao, null, null, null, obs, orgId, estId, periodo, cseIdInterno, usuarioNome);

        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            LOG.error("Erro interno de sistema ao criar solicitação Jira");
        } catch (final ImpRetornoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            LOG.error("Erro interno eConsig");
        }

        return null;
    }

    /**
     * atualiza a data para ficar pronto da issue de produção de geração de movimento financeiro
     * @param corteOld - data de corte antiga
     * @param corteNew - data de corte nova
     * @param responsavel
     * @param recursivo - indica se deve ser feita alteração recursiva por essa entidade AcessoSistema.ENTIDADE_EST, AcessoSistema.ENTIDADE_ORG
     * @param tipo - AcessoSistema.ENTIDADE_CSE, AcessoSistema.ENTIDADE_ORG
     * @param orgCod - codigo do orgão a ser editado
     * @return null
     * @throws java.text.ParseException
     * @throws ConsignanteControllerException
     * @throws IOException
     * @throws LogControllerException
     */

    public String atualizaDataCorteProducao(String corteOld, String corteNew, AcessoSistema responsavel, String periodo, String tipoEntidade, String codEntidade) throws ConsignanteControllerException, IOException, LogControllerException, java.text.ParseException {
        try {
            final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            final String cseIdInterno = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel).getIdentificadorInterno();

            final List<Map<String, String>> orgIds = new ArrayList<>();
            final List<String> estIds = new ArrayList<>();
            final int ano = Integer.parseInt(periodo.substring(0, 4));
            final String mes = periodo.substring(5, 7);

            // Obtem data em dias úteis
            Date dataCorteAux = DateHelper.parse(periodo, "yyyy-MM-dd");
            dataCorteAux = DateHelper.addDays(dataCorteAux, Integer.parseInt(corteNew) - 1);

            final CalendarioController calendarioController = ApplicationContextProvider.getApplicationContext().getBean(CalendarioController.class);
            final Date proxDiaUtil = calendarioController.findProximoDiaUtil(dataCorteAux, 1);

            final String dataFicarPronto = DateHelper.format(proxDiaUtil, "yyyy-MM-dd");
            final String dataCorteNew = DateHelper.format(dataCorteAux, "yyyy-MM-dd");

            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                final OrgaoTransferObject orgao = cseDelegate.findOrgao(codEntidade, responsavel);
                final String orgIdentificador = orgao.getOrgIdentificador();
                final String estIdentificador = cseDelegate.findEstabelecimento(orgao.getEstCodigo(), responsavel).getEstIdentificador();

                final Map<String, String> orgMap = new HashMap<>();
                orgMap.put("orgId", orgIdentificador);
                orgMap.put("estId", estIdentificador);
                orgIds.add(orgMap);

            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_EST)) {

                final TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.ORG_ATIVO, Short.valueOf("1"));
                final List<TransferObject> orgaos = cseDelegate.lstOrgaos(criterio, responsavel);
                final Iterator<TransferObject> it = orgaos.iterator();
                if (it.hasNext()) {
                    TransferObject orgao = null;
                    while (it.hasNext()) {
                        orgao = it.next();
                        final String orgCodigo = (String) orgao.getAttribute(Columns.ORG_CODIGO);

                        final OrgaoTransferObject org = cseDelegate.findOrgao(orgCodigo, responsavel);
                        final String orgIdentificador = org.getOrgIdentificador();
                        final String estIdentificador = cseDelegate.findEstabelecimento(org.getEstCodigo(), responsavel).getEstIdentificador();

                        // Se nao houver data de corte preenchida para os orgaos, nao atualiza
                        final Map<Integer, TransferObject> calendarioAno = calendarioController.lstCalendarioFolhaAno(ano, AcessoSistema.ENTIDADE_ORG, orgCodigo, responsavel);

                        try {
                            final String teste = calendarioAno.get(Integer.valueOf(mes)).getAttribute(Columns.CFO_DIA_CORTE).toString();
                            if (TextHelper.isNull(teste)) {
                                throw new NullPointerException();
                            }
                        } catch (final NullPointerException ex) {
                            final Map<String, String> orgMap = new HashMap<>();
                            orgMap.put("orgId", orgIdentificador);
                            orgMap.put("estId", estIdentificador);
                            orgIds.add(orgMap);
                        }
                    }
                }
                //É incluido o proprio estabelecimento alterado
                final EstabelecimentoTransferObject est = cseDelegate.findEstabelecimento(codEntidade, responsavel);
                final String estIdentificador = est.getEstIdentificador();
                estIds.add(estIdentificador);
            }

            else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)) {
                final TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.ORG_ATIVO, Short.valueOf("1"));
                final List<TransferObject> orgaos = cseDelegate.lstOrgaos(criterio, responsavel);
                Iterator<TransferObject> it = orgaos.iterator();
                if (it.hasNext()) {
                    TransferObject orgao = null;
                    while (it.hasNext()) {
                        orgao = it.next();
                        final String orgCodigo = (String) orgao.getAttribute(Columns.ORG_CODIGO);

                        final OrgaoTransferObject org = cseDelegate.findOrgao(orgCodigo, responsavel);
                        final String orgIdentificador = org.getOrgIdentificador();
                        final String estIdentificador = cseDelegate.findEstabelecimento(org.getEstCodigo(), responsavel).getEstIdentificador();

                        final Map<Integer, TransferObject> calendarioAno = calendarioController.lstCalendarioFolhaAno(ano, AcessoSistema.ENTIDADE_ORG, orgCodigo, responsavel);

                        try {
                            final String teste = calendarioAno.get(Integer.valueOf(mes)).getAttribute(Columns.CFO_DIA_CORTE).toString();
                            if (TextHelper.isNull(teste)) {
                                throw new NullPointerException();
                            }
                        } catch (final NullPointerException ex) {
                            final Map<String, String> orgMap = new HashMap<>();
                            orgMap.put("orgId", orgIdentificador);
                            orgMap.put("estId", estIdentificador);
                            orgIds.add(orgMap);
                        }
                    }
                }

                final TransferObject criterioEst = new CustomTransferObject();
                criterioEst.setAttribute(Columns.EST_ATIVO, Short.valueOf("1"));
                final List<TransferObject> estabelecimentos = cseDelegate.lstEstabelecimentos(criterioEst, responsavel);
                it = estabelecimentos.iterator();
                if (it.hasNext()) {
                    TransferObject estabelecimento = null;
                    while (it.hasNext()) {
                        estabelecimento = it.next();
                        final String estCodigo = (String) estabelecimento.getAttribute(Columns.EST_CODIGO);

                        final EstabelecimentoTransferObject est = cseDelegate.findEstabelecimento(estCodigo, responsavel);
                        final String estIdentificador = est.getEstIdentificador();

                        final Map<Integer, TransferObject> calendarioAno = calendarioController.lstCalendarioFolhaAno(ano, AcessoSistema.ENTIDADE_EST, estCodigo, responsavel);

                        try {
                            final String teste = calendarioAno.get(Integer.valueOf(mes)).getAttribute(Columns.CFO_DIA_CORTE).toString();
                            if (TextHelper.isNull(teste)) {
                                throw new NullPointerException();
                            }
                        } catch (final NullPointerException ex) {
                            estIds.add(estIdentificador);
                        }
                    }
                }
            }

            new JiraEConsig(host, port, user, pass, versaoNoveSuperior).atualizaDataCorteProducao(cseIdInterno, periodo, dataCorteNew, dataFicarPronto, tipoEntidade, codEntidade, orgIds, estIds);

        } catch (final CalendarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return null;
    }
}
