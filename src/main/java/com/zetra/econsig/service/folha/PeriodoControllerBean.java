package com.zetra.econsig.service.folha;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.CalendarioFolhaCse;
import com.zetra.econsig.persistence.entity.CalendarioFolhaCseHome;
import com.zetra.econsig.persistence.entity.CalendarioFolhaEst;
import com.zetra.econsig.persistence.entity.CalendarioFolhaEstHome;
import com.zetra.econsig.persistence.entity.CalendarioFolhaOrg;
import com.zetra.econsig.persistence.entity.CalendarioFolhaOrgHome;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.PeriodoBeneficio;
import com.zetra.econsig.persistence.entity.PeriodoBeneficioHome;
import com.zetra.econsig.persistence.entity.PeriodoExportacao;
import com.zetra.econsig.persistence.entity.PeriodoExportacaoHome;
import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaCseAgrupadoFiltroPeriodoQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaCseAgrupadoQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaCseAjustesQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaEstAgrupadoFiltroPeriodoQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaEstAgrupadoQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaEstAjustesQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaOrgAgrupadoFiltroPeriodoQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaOrgAgrupadoQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaOrgAjustesQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemDatasPeriodoCalendarioBeneficioQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemDatasPeriodoCalendarioFolhaQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemDatasPeriodoDataFimCalendarioFolhaQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemPeriodoAposPrazoCalendarioFolhaQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemPeriodoAtualCalendarioFolhaQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemPeriodoBeneficioAposPrazoCalendarioFolhaQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemPeriodoBeneficioAtualCalendarioFolhaQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemPeriodoCalendarioFolhaQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemPrazoEntrePeriodosCalendarioFolhaQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemUltimoPeriodoCalendarioFolhaQuery;
import com.zetra.econsig.persistence.query.periodo.ListaPeriodoBeneficioQuery;
import com.zetra.econsig.persistence.query.periodo.ListaPeriodoExportacaoQuery;
import com.zetra.econsig.persistence.query.periodo.ObtemPeriodoExportacaoAtualQuery;
import com.zetra.econsig.persistence.query.periodo.ObtemUltimoPeriodoExportadoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: PeriodoControllerBean</p>
 * <p>Description: Session Bean para a rotina de manipulação do período.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class PeriodoControllerBean implements PeriodoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PeriodoControllerBean.class);

    @Override
    public List<TransferObject> obtemPeriodoExpMovimento(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, AcessoSistema responsavel) throws PeriodoException {
        return obtemPeriodoExpMovimento(orgCodigos, estCodigos, gravarPeriodo, false, responsavel);
    }

    /**
     * Obtém o período (data base, inicial e final) que será utilizado para a rotina de exportação
     * de movimento financeiro. O período é determinado pelo calendário folha (ORG -> EST -> CSE),
     * além dos históricos de integração com a folha. O período para exportação será o próximo a
     * ser exportado, ou o último caso ainda esteja dentro do limite de dias para definir o novo
     * período (TPC 133).
     * @param orgCodigos : Lista dos códigos de órgãos (NULL para todos)
     * @param estCodigos : Lista dos códigos de estabelecimentos (NULL para todos)
     * @param gravarPeriodo : TRUE se o período deve ser gravado na tabela de período exportação
     * @param responsavel : Responsável pela operação
     * @return
     * @throws PeriodoException
     */
    @Override
    public List<TransferObject> obtemPeriodoExpMovimento(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, boolean ultimoPeriodo, AcessoSistema responsavel) throws PeriodoException {
        return obtemPeriodo(orgCodigos, estCodigos, gravarPeriodo, ultimoPeriodo, true, false, null, responsavel);
    }

    /**
     * Obtém o período (data base, inicial e final) que será utilizado para a rotina de importação
     * do retorno da folha. O período é determinado pelo calendário folha (ORG -> EST -> CSE),
     * além dos históricos de integração com a folha. O período para retorno será o último período
     * exportado.
     * @param orgCodigos : Lista dos códigos de órgãos (NULL para todos)
     * @param estCodigos : Lista dos códigos de estabelecimentos (NULL para todos)
     * @param gravarPeriodo : TRUE se o período deve ser gravado na tabela de período exportação
     * @param responsavel : Responsável pela operação
     * @return
     * @throws PeriodoException
     */
    @Override
    public List<TransferObject> obtemPeriodoImpRetorno(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, AcessoSistema responsavel) throws PeriodoException {
        return obtemPeriodo(orgCodigos, estCodigos, gravarPeriodo, false, false, false, null, responsavel);
    }

    /**
     * Obtém o período (data base, inicial e final) que será utilizado para a rotina de importação
     * do retorno da folha. O período é determinado pelo calendário folha (ORG -> EST -> CSE),
     * além dos históricos de integração com a folha. O período para retorno será o último período
     * exportado.
     * @param orgCodigos : Lista dos códigos de órgãos (NULL para todos)
     * @param estCodigos : Lista dos códigos de estabelecimentos (NULL para todos)
     * @param gravarPeriodo : TRUE se o período deve ser gravado na tabela de período exportação
     * @param periodoRetAtrasado : Força o período do retorno passado
     * @param responsavel : Responsável pela operação
     * @return
     * @throws PeriodoException
     */
    @Override
    public List<TransferObject> obtemPeriodoImpRetornoAtrasado(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, Date periodoRetAtrasado, AcessoSistema responsavel) throws PeriodoException {
        // Cálculo de margem é passado verdadeiro para buscar o último período de conclusão do retorno na importação de retorno atrasado
        boolean calculoMargem = true;
        return obtemPeriodo(orgCodigos, estCodigos, gravarPeriodo, false, false, calculoMargem, periodoRetAtrasado, responsavel);
    }

    /**
     * Obtém o período (data base, inicial e final) que será utilizado para a rotina de cálculo
     * de margem. O período é determinado pelo calendário folha (ORG -> EST -> CSE),
     * além dos históricos de integração com a folha. O período para cálculo de margem será o
     * último período exportado que já tem retorno.
     * @param orgCodigos : Lista dos códigos de órgãos (NULL para todos)
     * @param estCodigos : Lista dos códigos de estabelecimentos (NULL para todos)
     * @param gravarPeriodo : TRUE se o período deve ser gravado na tabela de período exportação
     * @param responsavel : Responsável pela operação
     * @return
     * @throws PeriodoException
     */
    @Override
    public List<TransferObject> obtemPeriodoCalculoMargem(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, AcessoSistema responsavel) throws PeriodoException {
        return obtemPeriodo(orgCodigos, estCodigos, gravarPeriodo, false, false, true, null, responsavel);
    }

    /**
     * Obtém o período, seja de exportação de movimento, importação de retorno ou cálculo de margem.
     * @param orgCodigos : Lista dos códigos de órgãos (NULL para todos)
     * @param estCodigos : Lista dos códigos de estabelecimentos (NULL para todos)
     * @param gravarPeriodo : TRUE se o período deve ser gravado na tabela de período exportação
     * @param ultimoPeriodo: TRUE se for para obter o último período exportado
     * @param exportacao : TRUE se é para gerar período de exportação
     * @param calculoMargem : TRUE se é para gerar período de cálculo de margem
     * @param periodo : Força o período passado
     * @param responsavel : Responsável pela operação
     * @return
     * @throws PeriodoException
     */
    private List<TransferObject> obtemPeriodo(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, boolean ultimoPeriodo, boolean exportacao, boolean calculoMargem, Date periodo, AcessoSistema responsavel) throws PeriodoException {
        try {
            // 1) Verifica parâmetro se o sistema deve calcular ou não o período
            boolean setaPeriodo = !ParamSist.paramEquals(CodedValues.TPC_SET_PERIODO_EXP_MOV_MES, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());
            if (!setaPeriodo) {
                LOG.warn("Não está calculando o período de exportação.");

                // Obtém o período presente na tb_periodo_exportacao
                ListaPeriodoExportacaoQuery listPeriodoQuery = new ListaPeriodoExportacaoQuery();
                listPeriodoQuery.estCodigos = estCodigos;
                listPeriodoQuery.orgCodigos = orgCodigos;
                List<TransferObject> periodoExportacao = listPeriodoQuery.executarDTO();
                return periodoExportacao;
            }

            boolean dataIniFimDistintas = false;
            if ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) && exportacao) ||
                    (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_RETORNO, CodedValues.TPC_SIM, responsavel) && !exportacao)) {
                dataIniFimDistintas = true;
            }

            // 2) Pega o último período exportado do histórico de integração MAX(HIE_PERIODO).
            final Date ultPeriodoExportado = obtemUltimoPeriodoExportado(orgCodigos, estCodigos, calculoMargem, dataIniFimDistintas, periodo, responsavel);
            Date ultPeriodo = ultPeriodoExportado;

            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_DOIS_PERIODOS_EXPORTACAO_ABERTOS, CodedValues.TPC_SIM, responsavel)) {
                if (ultPeriodo != null && !exportacao && !calculoMargem) {
                    // 2.1) Verifica se o período anterior já teve retorno, senão, utiliza o anterior
                    // e não o último período exportado. Obtém o último período exportado que teve retorno
                    Date ultPeriodoRetorno = obtemUltimoPeriodoExportado(orgCodigos, estCodigos, true, false, periodo, responsavel);
                    if (ultPeriodoRetorno != null) {
                        if (!PeriodoHelper.folhaMensal(responsavel)) {
                            // Na periodicidade quinzenal, simplesmente obtém o período seguinte ao último
                            // exportado que já teve retorno, que pode ou não coincidir com o último exportado.
                            ObtemPeriodoAposPrazoCalendarioFolhaQuery query = new ObtemPeriodoAposPrazoCalendarioFolhaQuery();
                            query.orgCodigos = orgCodigos;
                            query.periodoInicial = ultPeriodoRetorno;
                            query.qtdPeriodos = 1;
                            List<Date> resultado = query.executarLista();
                            if (resultado != null && resultado.size() == 1) {
                                ultPeriodo = resultado.get(0);
                            }
                        } else if (DateHelper.monthDiff(ultPeriodo, ultPeriodoRetorno) > 1) {
                            // A diferença entre o último período exportado e último período com retorno é
                            // maior que 1 mês, então utiliza o período seguinte ao último com retorno.
                            ultPeriodo = DateHelper.addMonths(ultPeriodoRetorno, 1);
                        }
                    }
                }
            }

            // 3) Caso não tenha histórico de integração, obtém do calendário folha o maior período onde a data fim < data atual.
            if (ultPeriodo == null) {
                ObtemUltimoPeriodoCalendarioFolhaQuery ultCalendarioQuery = new ObtemUltimoPeriodoCalendarioFolhaQuery();
                ultCalendarioQuery.orgCodigos = orgCodigos;
                ultCalendarioQuery.estCodigos = estCodigos;
                List<Object> ultCalendarioList = ultCalendarioQuery.executarLista();
                ultPeriodo = (ultCalendarioList != null && ultCalendarioList.size() > 0) ? (Date) ultCalendarioList.get(0) : null;
            }

            // 4) Caso o período não possa ser obtido pelo calendário folha, envia mensagem de erro ao usuário
            if (ultPeriodo == null) {
                throw new PeriodoException("mensagem.erro.calendario.folha.periodo.nao.determinado", responsavel);
            }

            // 5) Obtém do calendário folha (ORG -> EST -> CSE) as datas iniciais e finais deste período.
            ObtemDatasPeriodoCalendarioFolhaQuery datasPeriodoQuery = new ObtemDatasPeriodoCalendarioFolhaQuery();
            datasPeriodoQuery.orgCodigos = orgCodigos;
            datasPeriodoQuery.estCodigos = estCodigos;
            datasPeriodoQuery.periodo = ultPeriodo;
            List<TransferObject> datasPeriodoList = datasPeriodoQuery.executarDTO();

            if ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) && exportacao) ||
                    (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_RETORNO, CodedValues.TPC_SIM, responsavel) && !exportacao && !calculoMargem)) {
                // Se permite agrupar períodos, adiciona à tabela de períodos aqueles que tem
                // mesma data fim do próximo período retornado
                ObtemDatasPeriodoDataFimCalendarioFolhaQuery datasPeriodoAgrupadoQuery = new ObtemDatasPeriodoDataFimCalendarioFolhaQuery();
                datasPeriodoAgrupadoQuery.orgCodigos = orgCodigos;
                datasPeriodoAgrupadoQuery.estCodigos = estCodigos;
                datasPeriodoAgrupadoQuery.periodo = ultPeriodo;

                // Se habilita data prevista para o retorno e é cálculo de período para processamento de retorno (!exportacao && !calculoMargem)
                // então deverá retornar os períodos agrupados que tenham a mesma data de previsão do retorno
                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_DATA_PREVISTA_RETORNO, CodedValues.TPC_SIM, responsavel) && !exportacao && !calculoMargem) {
                    datasPeriodoAgrupadoQuery.mesmaDataPrevista = true;
                }
                datasPeriodoList.addAll(datasPeriodoAgrupadoQuery.executarDTO());
            }

            // 6) Se as datas do período não estão presentes no calendário folha, envia mensagem de erro ao usuário
            if (datasPeriodoList == null || datasPeriodoList.size() == 0) {
                throw new PeriodoException("mensagem.erro.calendario.folha.datas.periodo.nao.determinadas", responsavel);
            }

            // 7) Se a data fim somado à quantidade de dias do TPC 133 < data atual, obtém do calendário folha as datas do próximo período.
            // Somente para período de exportação. Os demais, retorno e margem devem usar o último exportado sempre.
            if (exportacao && !ultimoPeriodo) {
                boolean novoPeriodo = false;
                int qtdDiasSetarNovoPeriodo = 10;
                try {
                    qtdDiasSetarNovoPeriodo = Integer.parseInt((String) ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_SET_NOVO_PERIODO_EXP, responsavel));
                } catch (NumberFormatException ex) {
                    qtdDiasSetarNovoPeriodo = 10;
                }

                Date dataAtual = DateHelper.getSystemDatetime();
                for (TransferObject pex : datasPeriodoList) {
                    Date pexDataFim = (Date) pex.getAttribute(Columns.PEX_DATA_FIM);
                    int qtdDiasDataFimPassada = DateHelper.dayDiff(dataAtual, pexDataFim);
                    if (qtdDiasDataFimPassada > qtdDiasSetarNovoPeriodo) {
                        novoPeriodo = true;
                        break;
                    }
                }

                if (novoPeriodo) {
                    Date proxPeriodo = ultPeriodo;
                    if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel)) {
                        for (TransferObject pex : datasPeriodoList) {
                            Date pexPeriodo = (Date) pex.getAttribute(Columns.PEX_PERIODO);
                            if (pexPeriodo.compareTo(proxPeriodo) > 0) {
                                proxPeriodo = pexPeriodo;
                            }
                        }
                    }
                    if (!PeriodoHelper.folhaMensal(responsavel)) {
                        proxPeriodo = obtemPeriodoAposPrazo(null, 1, proxPeriodo, false, responsavel);
                    } else {
                        proxPeriodo = DateHelper.addMonths(proxPeriodo, 1);
                    }
                    datasPeriodoQuery.periodo = proxPeriodo;
                    datasPeriodoList = datasPeriodoQuery.executarDTO();

                    if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel)) {
                        // Se permite agrupar períodos, adiciona à tabela de períodos aqueles que tem
                        // mesma data fim do próximo período retornado
                        ObtemDatasPeriodoDataFimCalendarioFolhaQuery datasPeriodoAgrupadoQuery = new ObtemDatasPeriodoDataFimCalendarioFolhaQuery();
                        datasPeriodoAgrupadoQuery.orgCodigos = orgCodigos;
                        datasPeriodoAgrupadoQuery.estCodigos = estCodigos;
                        datasPeriodoAgrupadoQuery.periodo = proxPeriodo;
                        datasPeriodoList.addAll(datasPeriodoAgrupadoQuery.executarDTO());
                    }
                }
            }

            // 8) Validar o período encontrado: verifica a quantidade mínima e máxima de dias no período
            int qtdMinimaDiasPeriodo = 0;
            int qtdMaximaDiasPeriodo = 0;

            try {
                qtdMinimaDiasPeriodo = Integer.parseInt((String) ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MIN_DIAS_PERIODO_EXP, responsavel));
            } catch (NumberFormatException ex) {
                qtdMinimaDiasPeriodo = 15;
            }
            try {
                qtdMaximaDiasPeriodo = Integer.parseInt((String) ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MAX_DIAS_PERIODO_EXP, responsavel));
            } catch (NumberFormatException ex) {
                qtdMaximaDiasPeriodo = 45;
            }

            LOG.debug("Validando período:");
            if (datasPeriodoList == null || datasPeriodoList.size() == 0) {
                throw new PeriodoException("mensagem.erro.calendario.folha.datas.periodo.nao.determinadas", responsavel);
            }

            for (TransferObject pex : datasPeriodoList) {
                String orgCodigo = (String) pex.getAttribute(Columns.ORG_CODIGO);
                String orgNome = (String) pex.getAttribute(Columns.ORG_NOME);
                Date pexPeriodo = (Date) pex.getAttribute(Columns.PEX_PERIODO);
                Date pexDataIni = (Date) pex.getAttribute(Columns.PEX_DATA_INI);
                Date pexDataFim = (Date) pex.getAttribute(Columns.PEX_DATA_FIM);
                // Se permite exportar movimento parcial e a data da exportação é menor que a data fim do período, cria os registros com o período finalizando na data do processamento.
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_EXPORTAR_MOVIMENTO_DATA_FIM_FUTURA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                	Date dataAtual = DateHelper.getSystemDatetime();
        			if (dataAtual.before(pexDataFim)) {
    					pexDataFim = dataAtual;
					}
            	}
                LOG.debug("Órgão: " + orgNome + " | Código: " + orgCodigo + " | Perído: " + DateHelper.toPeriodString(pexPeriodo) + " | Início: " + DateHelper.toDateTimeString(pexDataIni) + " | Fim: " + DateHelper.toDateTimeString(pexDataFim));

                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel)
                        && pexDataIni.compareTo(pexDataFim) == 0) {
                    continue;
                }

                // Valida se a data fim é maior que a data inicio do período
                if (!pexDataFim.after(pexDataIni)) {
                    throw new PeriodoException("mensagem.erro.periodo.data.fim.menor.data.inicial", responsavel, DateHelper.toDateString(pexDataFim), DateHelper.toDateString(pexDataIni));
                }

                // Devemos adicionar 1 à conta, já que o dia ini é às 00:00:00 e
                // o dia fim é as 23:59:59, e o método não irá considerar o dia
                // fim na conta.
                int qtdDiasPeriodo = DateHelper.dayDiff(pexDataFim, pexDataIni) + 1;

                if (qtdDiasPeriodo < qtdMinimaDiasPeriodo) {
                    throw new PeriodoException("mensagem.erro.periodo.menor.quantidade.minima", responsavel, String.valueOf(qtdDiasPeriodo), String.valueOf(qtdMinimaDiasPeriodo));
                } else if (qtdDiasPeriodo > qtdMaximaDiasPeriodo) {
                    throw new PeriodoException("mensagem.erro.periodo.maior.quantidade.maxima", responsavel, String.valueOf(qtdDiasPeriodo), String.valueOf(qtdMaximaDiasPeriodo));
                }
            }

            // 9) Se é para gravar o período na tabela de período exportação, então efetua a atualização.
            if (gravarPeriodo) {
                // Obtém o período presente na tb_periodo_exportacao para ser removido
                ListaPeriodoExportacaoQuery listPeriodoQuery = new ListaPeriodoExportacaoQuery();
                // DESENV-6421 : não repassa ORG/EST para remover todos os registros da tabela de período
                // de forma que conterá apenas os registros referente aos órgãos em processamento.
                //listPeriodoQuery.estCodigos = estCodigos;
                //listPeriodoQuery.orgCodigos = orgCodigos;
                List<TransferObject> lstPeriodoExportacao = listPeriodoQuery.executarDTO();
                for (TransferObject pex : lstPeriodoExportacao) {
                    String orgCodigo = (String) pex.getAttribute(Columns.ORG_CODIGO);
                    Date pexPeriodo = (Date) pex.getAttribute(Columns.PEX_PERIODO);
                    try {
                        PeriodoExportacao periodoExportacao = PeriodoExportacaoHome.findByPrimaryKey(orgCodigo, pexPeriodo);
                        PeriodoExportacaoHome.remove(periodoExportacao);
                    } catch (FindException ex) {
                    }
                }

                Date pexPeriodoControle = null;
                short pexSequencia = 0;

                for (TransferObject pex : datasPeriodoList) {
                    String orgCodigo = (String) pex.getAttribute(Columns.ORG_CODIGO);
                    Date pexPeriodo = (Date) pex.getAttribute(Columns.PEX_PERIODO);
                    Date pexDataIni = (Date) pex.getAttribute(Columns.PEX_DATA_INI);
                    Date pexDataFim = (Date) pex.getAttribute(Columns.PEX_DATA_FIM);
                    // Se permite exportar movimento parcial e a data da exportação é menor que a data fim do período, cria os registros com o período finalizando na data do processamento.
                    if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_EXPORTAR_MOVIMENTO_DATA_FIM_FUTURA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                    	Date dataAtual = DateHelper.getSystemDatetime();
            			if (dataAtual.before(pexDataFim)) {
        					pexDataFim = dataAtual;
						}
                	}
                    Short pexNumPeriodo = (pex.getAttribute(Columns.PEX_NUM_PERIODO) != null ? Short.valueOf(pex.getAttribute(Columns.PEX_NUM_PERIODO).toString()) : 0);
                    Short pexDiaCorte = (pex.getAttribute(Columns.PEX_DIA_CORTE) != null ? Short.valueOf(pex.getAttribute(Columns.PEX_DIA_CORTE).toString()) : 0);

                    Date pexPeriodoAnt = null;
                    Date pexPeriodoPos = null;
                    if (!PeriodoHelper.folhaMensal(responsavel)) {
                        pexPeriodoAnt = obtemPeriodoAposPrazo(orgCodigo, -1, pexPeriodo, false, responsavel);
                        pexPeriodoPos = obtemPeriodoAposPrazo(orgCodigo, +1, pexPeriodo, false, responsavel);
                    } else {
                        pexPeriodoAnt = DateHelper.addMonths(pexPeriodo, -1);
                        pexPeriodoPos = DateHelper.addMonths(pexPeriodo, +1);
                    }

                    // DESENV-20779 : Se permite múltiplos períodos de exportação abertos, atualiza o "pex_periodo_pos"
                    // para o período subsequente ao último exportado, pois este é usado para a inclusão de ocorrências
                    // no processamento  do retorno e estas devem ser incluídas no período que ainda não foi exportado.
                    if (!exportacao && ultPeriodoExportado != null && ParamSist.paramEquals(CodedValues.TPC_PERMITE_DOIS_PERIODOS_EXPORTACAO_ABERTOS, CodedValues.TPC_SIM, responsavel)) {
                        if (!PeriodoHelper.folhaMensal(responsavel)) {
                            pexPeriodoPos = obtemPeriodoAposPrazo(orgCodigo, +1, ultPeriodoExportado, false, responsavel);
                        } else {
                            pexPeriodoPos = DateHelper.addMonths(ultPeriodoExportado, +1);
                        }
                    }

                    if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel)) {
                        if (pexPeriodoControle != null) {
                            if (pexPeriodoControle.compareTo(pexPeriodo) < 0) {
                                pexSequencia++;
                                pexPeriodoControle = pexPeriodo;
                            }
                        } else {
                            pexPeriodoControle = pexPeriodo;
                        }
                    }
                    PeriodoExportacaoHome.create(orgCodigo, pexDiaCorte, pexDataIni, pexDataFim, pexPeriodo, pexPeriodoAnt, pexPeriodoPos, pexSequencia, pexNumPeriodo);
                }
            }

            return datasPeriodoList;

        } catch (PeriodoException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (CreateException | RemoveException | HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna a data base do último período de exportação, registrado na tabela de histórico de exportação.
     * @param orgCodigos
     * @param estCodigos
     * @param temRetorno
     * @param responsavel
     * @return Retorna a data base do último período de exportação.
     * @throws PeriodoException
     */
    @Override
    public Date obtemUltimoPeriodoExportado(List<String> orgCodigos, List<String> estCodigos, boolean temRetorno, Date periodo, AcessoSistema responsavel) throws PeriodoException {
        return obtemUltimoPeriodoExportado(orgCodigos, estCodigos, temRetorno, false, periodo, responsavel);
    }

    /**
     * Retorna a data base do último período de exportação, registrado na tabela de histórico de exportação.
     * @param orgCodigos
     * @param estCodigos
     * @param temRetorno
     * @param dataIniFimDistintas
     * @param periodo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    private Date obtemUltimoPeriodoExportado(List<String> orgCodigos, List<String> estCodigos, boolean temRetorno, boolean dataIniFimDistintas, Date periodo, AcessoSistema responsavel) throws PeriodoException {

        try {
            ObtemUltimoPeriodoExportadoQuery ultPeriodoQuery = new ObtemUltimoPeriodoExportadoQuery();
            ultPeriodoQuery.orgCodigos = orgCodigos;
            ultPeriodoQuery.estCodigos = estCodigos;
            ultPeriodoQuery.temRetorno = temRetorno;
            ultPeriodoQuery.dataIniFimDistintas = dataIniFimDistintas;
            ultPeriodoQuery.periodo = periodo;
            List<Object> ultPeriodoList = ultPeriodoQuery.executarLista();
            return (ultPeriodoList != null && ultPeriodoList.size() > 0) ? (Date) ultPeriodoList.get(0) : null;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna o período atual de lançamentos, para o dia corrente, sendo que
     * será aquele período onde a data corrente está entre data inicial e final
     * do calendário folha (ORG -> EST -> CSE).
     * @param orgCodigos : Lista dos códigos de órgãos (NULL para todos)
     * @param estCodigos : Lista dos códigos de estabelecimentos (NULL para todos)
     * @param responsavel : Responsável pela operação
     * @return
     * @throws PeriodoException
     */
    @Override
    public List<TransferObject> obtemPeriodoAtual(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws PeriodoException {
        try {
            ObtemPeriodoAtualCalendarioFolhaQuery query = new ObtemPeriodoAtualCalendarioFolhaQuery();
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna as datas do período de lançamento
     * configuradas no calendário folha de acordo com a data passada e final
     * do calendário folha (ORG -> EST -> CSE).
     * @param orgCodigos : Lista dos códigos de órgãos (NULL para todos)
     * @param estCodigos : Lista dos códigos de estabelecimentos (NULL para todos)
     * @param responsavel : Responsável pela operação
     * @return
     * @throws PeriodoException
     */
    @Override
    public TransferObject obtemPeriodoPorData(List<String> orgCodigos, List<String> estCodigos, Date data, AcessoSistema responsavel) throws PeriodoException {
        try {
            ObtemPeriodoCalendarioFolhaQuery query = new ObtemPeriodoCalendarioFolhaQuery();
            query.data = data;
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            List<TransferObject> perido = query.executarDTO();
            return (perido != null && !perido.isEmpty()) ? perido.get(0) : null;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna a lista de períodos que podem ser utilizados nas operações, seja por conta de agrupamento
     * de períodos, ou seja pelos períodos em intervalo para ajustes
     * @param orgCodigo   : código do órgão
     * @param dataLimite  : data limite máxima a ser retornada
     * @param responsavel : responsável pela operação
     * @return
     * @throws PeriodoException
     */
    @Override
    public Set<Date> listarPeriodosPermitidos(String orgCodigo, Date dataLimite, AcessoSistema responsavel) throws PeriodoException {
        Set<Date> periodos = new LinkedHashSet<>();
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {
            List<Date> periodosAjustes = obtemPeriodoParaAjustes(orgCodigo, dataLimite, responsavel);
            if (periodosAjustes != null && !periodosAjustes.isEmpty()) {
                periodos.addAll(periodosAjustes);
            }
        }
        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) {
            List<Date> periodosAgrupados = obtemPeriodoAgrupado(orgCodigo, dataLimite, responsavel);
            if (periodosAgrupados != null && !periodosAgrupados.isEmpty()) {
                periodos.addAll(periodosAgrupados);
            }
        }
        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel) &&
                !periodos.isEmpty()) {
            boolean soAceitaExclusao = ParamSist.paramEquals(CodedValues.TPC_PERIODO_COM_APENAS_REDUCOES_SOMENTE_EXCLUSAO, CodedValues.TPC_SIM, responsavel);
            // Verifica se a operação não é uma redução
            String funCodigo = responsavel.getFunCodigo();
            if (!TextHelper.isNull(funCodigo) && (
                    funCodigo.equals(CodedValues.FUN_RES_MARGEM) ||
                    funCodigo.equals(CodedValues.FUN_RENE_CONTRATO) ||
                    funCodigo.equals(CodedValues.FUN_COMP_CONTRATO) ||
                    funCodigo.equals(CodedValues.FUN_ALONGAR_CONTRATO) ||
                    funCodigo.equals(CodedValues.FUN_REAT_CONSIGNACAO) ||
                    funCodigo.equals(CodedValues.FUN_REIMP_CONSIGNACAO) ||
                    (funCodigo.equals(CodedValues.FUN_ALT_CONSIGNACAO) && soAceitaExclusao))) {
                Set<Date> periodosValidos = new LinkedHashSet<>();
                String periodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);
                for (Date periodo : periodos) {
                    // Não é operação de redução e o período só permite redução, então remove
                    // ele da listagem e adiciona o próximo que permite todas as operações
                    while (periodoPermiteApenasReducoes(periodo, orgCodigo, responsavel)) {
                        periodo = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, DateHelper.toSQLDate(periodo), 1, periodicidade, responsavel);
                    }
                    periodosValidos.add(periodo);
                }
                periodos = periodosValidos;
            }
        }
        return periodos;
    }

    /**
     * Retorna a lista de períodos que estão agrupados ao período atual passado por parâmetro
     * @param orgCodigo   : código do órgão
     * @param dataLimite  : data limite máxima a ser retornada
     * @param responsavel : responsável pela operação
     * @return
     * @throws PeriodoException
     */
    @Override
    public List<Date> obtemPeriodoAgrupado(String orgCodigo, Date dataLimite, AcessoSistema responsavel) throws PeriodoException {
        try {
            List<Date> periodos = null;
            Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);

            if (!TextHelper.isNull(orgCodigo) && (periodos == null || periodos.isEmpty())) {
                ListaCalFolhaOrgAgrupadoQuery query = new ListaCalFolhaOrgAgrupadoQuery();
                query.orgCodigo = orgCodigo;
                query.cfoPeriodo = periodoAtual;
                query.dataLimite = dataLimite;
                periodos = query.executarLista();
            }
            if (!TextHelper.isNull(orgCodigo) && (periodos == null || periodos.isEmpty())) {
                String estCodigo = OrgaoHome.findByPrimaryKey(orgCodigo).getEstabelecimento().getEstCodigo();

                ListaCalFolhaEstAgrupadoQuery query = new ListaCalFolhaEstAgrupadoQuery();
                query.estCodigo = estCodigo;
                query.cfePeriodo = periodoAtual;
                query.dataLimite = dataLimite;
                periodos = query.executarLista();
            }
            if (periodos == null || periodos.isEmpty()) {
                ListaCalFolhaCseAgrupadoQuery query = new ListaCalFolhaCseAgrupadoQuery();
                query.cseCodigo = CodedValues.CSE_CODIGO_SISTEMA;
                query.cfcPeriodo = periodoAtual;
                query.dataLimite = dataLimite;
                periodos = query.executarLista();
            }

            return periodos;
        } catch (HQueryException|FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna a lista de períodos que estão na janela para ajustes pelo gestor
     * @param orgCodigo   : código do órgão
     * @param dataLimite  : data limite máxima a ser retornada
     * @param responsavel : responsável pela operação
     * @return
     * @throws PeriodoException
     */
    private List<Date> obtemPeriodoParaAjustes(String orgCodigo, Date dataLimite, AcessoSistema responsavel) throws PeriodoException {
        try {
            List<Date> periodos = null;
            if (responsavel.isCseSupOrg()) {
                if (!TextHelper.isNull(orgCodigo) && (periodos == null || periodos.isEmpty())) {
                    ListaCalFolhaOrgAjustesQuery query = new ListaCalFolhaOrgAjustesQuery();
                    query.orgCodigo = orgCodigo;
                    query.dataLimite = dataLimite;
                    periodos = query.executarLista();
                }
                if (!TextHelper.isNull(orgCodigo) && (periodos == null || periodos.isEmpty())) {
                    String estCodigo = OrgaoHome.findByPrimaryKey(orgCodigo).getEstabelecimento().getEstCodigo();

                    ListaCalFolhaEstAjustesQuery query = new ListaCalFolhaEstAjustesQuery();
                    query.estCodigo = estCodigo;
                    query.dataLimite = dataLimite;
                    periodos = query.executarLista();
                }
                if (periodos == null || periodos.isEmpty()) {
                    ListaCalFolhaCseAjustesQuery query = new ListaCalFolhaCseAjustesQuery();
                    query.cseCodigo = CodedValues.CSE_CODIGO_SISTEMA;
                    query.dataLimite = dataLimite;
                    periodos = query.executarLista();
                }
                if (periodos != null && !periodos.isEmpty()) {
                    // Se existe períodos em ajuste, adiciona à listagem o período atual, que apesar
                    // de não estar no período de ajustes, os usuários gestores podem operar sobre ele
                    Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                    periodos.add(periodoAtual);
                }
            }
            return periodos;
        } catch (HQueryException|FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna o período correspondente à "qtdPeriodos" seguintes ao "periodoInicial", por
     * exemplo usado no cálculo da data final do contrato, sendo o período inicial de desconto
     * somado à quantidade de prestações.
     * @param orgCodigo
     * @param qtdPeriodos
     * @param periodoInicial
     * @param ignoraPeriodosAgrupados
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    @Override
    public Date obtemPeriodoAposPrazo(String orgCodigo, Integer qtdPeriodos, Date periodoInicial, boolean ignoraPeriodosAgrupados, AcessoSistema responsavel) throws PeriodoException {
        try {
            Date peridoFinal = null;
            ObtemPeriodoAposPrazoCalendarioFolhaQuery query = new ObtemPeriodoAposPrazoCalendarioFolhaQuery();
            query.orgCodigo = orgCodigo;
            query.periodoInicial = periodoInicial;
            query.qtdPeriodos = qtdPeriodos;
            query.ignoraPeriodosAgrupados = ignoraPeriodosAgrupados;
            List<Date> resultado = query.executarLista();
            if (resultado != null && resultado.size() == 1) {
                peridoFinal = resultado.get(0);
            }
            if (peridoFinal == null) {
                throw new PeriodoException("mensagem.erro.calculo.periodo.calendario.incompleto", responsavel, String.valueOf(qtdPeriodos));
            }
            return peridoFinal;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna o prazo entre o periodoInicial e periodoFinal, de acordo com os períodos
     * cadastrados no calendário folha do sistema.
     * @param orgCodigo
     * @param periodoInicial
     * @param periodoFinal
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    @Override
    public Integer obtemPrazoEntrePeriodos(String orgCodigo, Date periodoInicial, Date periodoFinal, AcessoSistema responsavel) throws PeriodoException {
        try {
            ObtemPrazoEntrePeriodosCalendarioFolhaQuery query = new ObtemPrazoEntrePeriodosCalendarioFolhaQuery();
            query.orgCodigo = orgCodigo;
            query.periodoInicial = periodoInicial;
            query.periodoFinal = periodoFinal;
            // O executar contador não funciona, porque o HQL não permite count distinct com múltiplos campos
            return query.executarLista().size();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna o período cadastrado na tb_periodo_exportacao, que possui as datas mais
     * frequentes dentre os vários órgãos (caso não seja informado nenhum filtro de
     * órgão e estabelecimento).
     * @param orgCodigos : Lista dos códigos de órgãos (NULL para todos)
     * @param estCodigos : Lista dos códigos de estabelecimentos (NULL para todos)
     * @param responsavel : Responsável pela operação
     * @return
     * @throws PeriodoException
     */
    @Override
    public TransferObject obtemPeriodoExportacaoDistinto(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws PeriodoException {
        try {
            ObtemPeriodoExportacaoAtualQuery query = new ObtemPeriodoExportacaoAtualQuery();
            query.estCodigos = estCodigos;
            query.orgCodigos = orgCodigos;
            List<TransferObject> resultado = query.executarDTO();
            if (resultado == null || resultado.size() == 0) {
                throw new PeriodoException("mensagem.erro.periodo.impossivel.recuperar", responsavel);
            }

            return resultado.get(0);
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Determina se um período do calendário folha permite apenas reduções, ou seja
     * exclusões e alterações para menor valor.
     * @param periodo
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    @Override
    public boolean periodoPermiteApenasReducoes(Date periodo, String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        String apenasReducoes = null;

        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel)) {
            if (TextHelper.isNull(apenasReducoes) && !TextHelper.isNull(orgCodigo)) {
                try {
                    CalendarioFolhaOrg cfo = CalendarioFolhaOrgHome.findByPrimaryKey(orgCodigo, periodo);
                    apenasReducoes = cfo.getCfoApenasReducoes();
                } catch (FindException ex) {
                    // Ok, calendário folha ORG não existe
                }
            }

            if (TextHelper.isNull(apenasReducoes) && !TextHelper.isNull(orgCodigo)) {
                try {
                    String estCodigo = OrgaoHome.findByPrimaryKey(orgCodigo).getEstabelecimento().getEstCodigo();
                    CalendarioFolhaEst cfe = CalendarioFolhaEstHome.findByPrimaryKey(estCodigo, periodo);
                    apenasReducoes = cfe.getCfeApenasReducoes();
                } catch (FindException ex) {
                    // Ok, calendário folha EST não existe
                }
            }

            if (TextHelper.isNull(apenasReducoes)) {
                try {
                    CalendarioFolhaCse cfc = CalendarioFolhaCseHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA, periodo);
                    apenasReducoes = cfc.getCfcApenasReducoes();
                } catch (FindException ex) {
                    // Ok, calendário folha CSE não existe
                }
            }
        }

        return ("S".equals(apenasReducoes));
    }

    /** Parte responsavel pelo periodo do modulo de Beneficio **/

    /**
     *
     * @param orgCodigos
     * @param estCodigos
     * @param gravarPeriodo
     * @param periodo
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    @Override
    public List<TransferObject> obtemPeriodoBeneficio(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, Date periodo, AcessoSistema responsavel) throws PeriodoException {
        try {
            List<TransferObject> datasPeriodoList = null;

            // Se for informado o periodo vamos calcular o perido com base;
            if (periodo != null) {
                ObtemDatasPeriodoCalendarioBeneficioQuery datasPeriodoQuery = new ObtemDatasPeriodoCalendarioBeneficioQuery();
                datasPeriodoQuery.orgCodigos = orgCodigos;
                datasPeriodoQuery.estCodigos = estCodigos;
                datasPeriodoQuery.periodo = periodo;
                datasPeriodoList = datasPeriodoQuery.executarDTO();
            } else {

                // Se não for defenido o periodo simplesmente pegamos periodo atual.
                datasPeriodoList = obtemPeriodoBeneficioAtual(orgCodigos, estCodigos, responsavel);
            }

            if (datasPeriodoList == null || datasPeriodoList.size() == 0) {
                throw new PeriodoException("mensagem.erro.calendario.folha.datas.periodo.nao.determinadas", responsavel);
            }

            for (TransferObject pex : datasPeriodoList) {
                String orgCodigo = (String) pex.getAttribute(Columns.ORG_CODIGO);
                String orgNome = (String) pex.getAttribute(Columns.ORG_NOME);
                Date pbePeriodo = (Date) pex.getAttribute(Columns.PBE_PERIODO);
                Date pbeDataIni = (Date) pex.getAttribute(Columns.PBE_DATA_INI);
                Date pbeDataFim = (Date) pex.getAttribute(Columns.PBE_DATA_FIM);
                LOG.debug("Órgão: " + orgNome + " | Código: " + orgCodigo + " | Perído: " + DateHelper.toPeriodString(pbePeriodo) + " | Início: " + DateHelper.toDateTimeString(pbeDataIni) + " | Fim: " + DateHelper.toDateTimeString(pbeDataFim));

                // Valida se a data fim é maior que a data inicio do período
                if (!pbeDataFim.after(pbeDataIni)) {
                    throw new PeriodoException("mensagem.erro.periodo.data.fim.menor.data.inicial", responsavel, DateHelper.toDateString(pbeDataFim), DateHelper.toDateString(pbeDataIni));
                }
            }

            if (gravarPeriodo) {
                ListaPeriodoBeneficioQuery listPeriodoQuery = new ListaPeriodoBeneficioQuery();
                List<TransferObject> lstPeriodoExportacao = listPeriodoQuery.executarDTO();
                for (TransferObject pbe : lstPeriodoExportacao) {
                    String orgCodigo = (String) pbe.getAttribute(Columns.ORG_CODIGO);
                    Date pbePeriodo = (Date) pbe.getAttribute(Columns.PBE_PERIODO);

                    try {
                        PeriodoBeneficio periodoBeneficio = PeriodoBeneficioHome.findByPrimaryKey(orgCodigo, pbePeriodo);
                        PeriodoBeneficioHome.remove(periodoBeneficio);
                    } catch (FindException ex) {
                    }
                }

                short pbeSequencia = 0;

                for (TransferObject pex : datasPeriodoList) {
                    String orgCodigo = (String) pex.getAttribute(Columns.ORG_CODIGO);
                    Date pbePeriodo = (Date) pex.getAttribute(Columns.PBE_PERIODO);
                    Date pbeDataIni = (Date) pex.getAttribute(Columns.PBE_DATA_INI);
                    Date pbeDataFim = (Date) pex.getAttribute(Columns.PBE_DATA_FIM);
                    Short pbeDiaCorte = (pex.getAttribute(Columns.PBE_DIA_CORTE) != null ? Short.valueOf(pex.getAttribute(Columns.PBE_DIA_CORTE).toString()) : 0);

                    Date pbePeriodoAnt = DateHelper.addMonths(pbePeriodo, -1);
                    Date pbePeriodoPos = DateHelper.addMonths(pbePeriodo, +1);

                    PeriodoBeneficioHome.create(orgCodigo, pbePeriodo, pbePeriodoAnt, pbePeriodoPos, pbeDiaCorte, pbeDataIni, pbeDataFim, pbeSequencia);
                }
            }

            return datasPeriodoList;
        } catch (HQueryException | RemoveException | CreateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (PeriodoException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    /**
     *
     * @param orgCodigos
     * @param estCodigos
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    @Override
    public List<TransferObject> obtemPeriodoBeneficioAtual(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws PeriodoException {
        try {
            ObtemPeriodoBeneficioAtualCalendarioFolhaQuery query = new ObtemPeriodoBeneficioAtualCalendarioFolhaQuery();
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna o período beneficio correspondente à "qtdPeriodos" seguintes ao "periodoInicial", por
     * exemplo usado no cálculo da data final do contrato, sendo o período inicial de desconto
     * somado à quantidade de prestações.
     * @param orgCodigo
     * @param qtdPeriodos
     * @param periodoInicial
     * @param ignoraPeriodosAgrupados
     * @param responsavel
     * @return
     * @throws PeriodoException
     */
    @Override
    public Date obtemPeriodoBeneficioAposPrazo(String orgCodigo, Integer qtdPeriodos, Date periodoInicial, boolean ignoraPeriodosAgrupados, AcessoSistema responsavel) throws PeriodoException {
        try {
            Date peridoFinal = null;
            ObtemPeriodoBeneficioAposPrazoCalendarioFolhaQuery query = new ObtemPeriodoBeneficioAposPrazoCalendarioFolhaQuery();
            query.orgCodigo = orgCodigo;
            query.periodoInicial = periodoInicial;
            query.qtdPeriodos = qtdPeriodos;
            query.ignoraPeriodosAgrupados = ignoraPeriodosAgrupados;
            List<Date> resultado = query.executarLista();
            if (resultado != null && resultado.size() == 1) {
                peridoFinal = resultado.get(0);
            }
            if (peridoFinal == null) {
                throw new PeriodoException("mensagem.erro.calculo.periodo.calendario.incompleto", responsavel, String.valueOf(qtdPeriodos));
            }
            return peridoFinal;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna a quantidade de período agrupados entre períodos
     * @param orgCodigo   : código do órgão
     * @param periodoIni  : a partir deste período
     * @param periodoFim  : até este periodo
     * @param responsavel : responsável pela operação
     * @return
     * @throws PeriodoException
     */
    @Override
    public Integer obtemQtdPeriodoAgrupado(String orgCodigo, Date periodoInicio, Date periodoFim, AcessoSistema responsavel) throws PeriodoException {
        try {
            List<Date> periodos = null;

            if (!TextHelper.isNull(orgCodigo) && (periodos == null || periodos.isEmpty())) {
                ListaCalFolhaOrgAgrupadoFiltroPeriodoQuery query = new ListaCalFolhaOrgAgrupadoFiltroPeriodoQuery();
                query.orgCodigo = orgCodigo;
                query.periodoInicio = periodoInicio;
                query.periodoFim = periodoFim;
                periodos = query.executarLista();
            }
            if (!TextHelper.isNull(orgCodigo) && (periodos == null || periodos.isEmpty())) {
                String estCodigo = OrgaoHome.findByPrimaryKey(orgCodigo).getEstabelecimento().getEstCodigo();

                ListaCalFolhaEstAgrupadoFiltroPeriodoQuery query = new ListaCalFolhaEstAgrupadoFiltroPeriodoQuery();
                query.estCodigo = estCodigo;
                query.periodoInicio = periodoInicio;
                query.periodoFim = periodoFim;
                periodos = query.executarLista();
            }
            if (periodos == null || periodos.isEmpty()) {
                ListaCalFolhaCseAgrupadoFiltroPeriodoQuery query = new ListaCalFolhaCseAgrupadoFiltroPeriodoQuery();
                query.cseCodigo = CodedValues.CSE_CODIGO_SISTEMA;
                query.periodoInicio = periodoInicio;
                query.periodoFim = periodoFim;
                periodos = query.executarLista();
            }

            return periodos != null ? periodos.size() : 0;
        } catch (HQueryException|FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
