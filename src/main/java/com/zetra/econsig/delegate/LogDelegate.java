package com.zetra.econsig.delegate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.helper.log.ControleTipoEntidade;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleAcessoSeguranca;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.TipoEntidade;
import com.zetra.econsig.service.log.LogController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: Log</p>
 * <p>Description: Gerador de Log</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LogDelegate extends AbstractDelegate {
    private LogController logController = null;

    // Dados para gravação de log
    private String usuCodigo = null;
    private String codigoEntidade00 = null;
    private String codigoEntidade01 = null;
    private String codigoEntidade02 = null;
    private String codigoEntidade03 = null;
    private String codigoEntidade04 = null;
    private String codigoEntidade05 = null;
    private String codigoEntidade06 = null;
    private String codigoEntidade07 = null;
    private String codigoEntidade08 = null;
    private String codigoEntidade09 = null;
    private String codigoEntidade10 = null;
    private String entidade = null;
    private String tipo = null;
    private String funCodigo = null;
    private StringBuilder line = null;
    private String ipUsuario = null;
    private Integer portaLogica = null;
    private CanalEnum logCanal = null;

    private LogController getLogController() throws LogControllerException {
        try {
            if (logController == null) {
                logController = ApplicationContextProvider.getApplicationContext().getBean(LogController.class);
            }
            return logController;
        } catch (final Exception ex) {
            throw new LogControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    public LogDelegate() {
    }

    public LogDelegate(AcessoSistema responsavel, String entidade, String operacao, String tipoLog) {
        if (responsavel != null) {
            usuCodigo = !TextHelper.isNull(responsavel.getUsuCodigo()) ? responsavel.getUsuCodigo() : null;
            ipUsuario = !TextHelper.isNull(responsavel.getIpUsuario()) ? responsavel.getIpUsuario() : null;
            portaLogica = !TextHelper.isNull(responsavel.getPortaLogicaUsuario()) ? responsavel.getPortaLogicaUsuario() : null;
            logCanal = responsavel.getCanal();
        }

        this.entidade = !TextHelper.isNull(entidade) ? entidade : null;
        funCodigo = !TextHelper.isNull(responsavel.getFunCodigo()) ? responsavel.getFunCodigo() : null;
        tipo = ((tipoLog != null) && !"".equals(tipoLog)) ? tipoLog : null;
        line = new StringBuilder();

        String ope = null;

        // recupera a descrição da operação
        if (!TextHelper.isNull(operacao)) {
            ope = Log.getOperacao(operacao, responsavel);
            if ((ope == null) || "".equals(ope)) {
                ope = operacao;
            }
            line.append(ApplicationResourcesHelper.getMessage("rotulo.log.acao", responsavel)).append(": ").append(ope);
        }

        // faz a contagem de acessos com erros de segurança
        if (!TextHelper.isNull(tipo) && Log.LOG_ERRO_SEGURANCA.equals(tipo)) {
            ControleAcessoSeguranca.CONTROLESEGURANCA.bloqueiaLimiteErroSeguranca(responsavel, entidade, ope);
        }
    }

    public void setResponsavel(AcessoSistema responsavel) {
        if (responsavel != null) {
            usuCodigo = !TextHelper.isNull(responsavel.getUsuCodigo()) ? responsavel.getUsuCodigo() : null;
            ipUsuario = !TextHelper.isNull(responsavel.getIpUsuario()) ? responsavel.getIpUsuario() : null;
            logCanal = responsavel.getCanal();
        }
    }

    public void add(String msg) throws LogControllerException {
        if (line == null) {
            throw new LogControllerException("mensagem.erro.log.auditoria.adicionar.comentario", (AcessoSistema) null);
        }

        line.append("<BR>").append(msg);
    }

    public void add(String column, String msg) throws LogControllerException {
        add(Columns.getColumnLabel(column) + ": " + msg + ".");
    }

    public void add(String column, List<String> codigos, Class<? extends AbstractEntityHome> clazz) throws LogControllerException {
        final List<String> descricoes = getLogController().recuperaDescricoes(clazz, codigos);
        if ((descricoes != null) && !descricoes.isEmpty()) {
            add(Columns.getColumnLabel(column) + ": " + TextHelper.join(descricoes, ", ") + ".");
        }
    }

    public void addChangedField(String name, Object newValue, Object oldValue) throws LogControllerException {
        add(ApplicationResourcesHelper.getMessage("mensagem.log.auditoria.atributo.alterado.de.para", (AcessoSistema) null, Columns.getColumnLabel(name), formatObject(oldValue), formatObject(newValue)));
    }

    public void addChangedField(String name, Object newValue) throws LogControllerException {
        add(ApplicationResourcesHelper.getMessage("mensagem.log.auditoria.atributo.alterado.para", (AcessoSistema) null, Columns.getColumnLabel(name), formatObject(newValue)));
    }

    public String formatObject(Object value) {
        if (value == null) {
            return "";
        } else if (value instanceof java.sql.Date) {
            return DateHelper.toDateString((java.sql.Date) value);
        } else if (value instanceof java.util.Date) {
            return DateHelper.toDateTimeString((java.util.Date) value);
        } else {
            return value.toString();
        }
    }

    public CustomTransferObject getUpdatedFields(Map<String, Object> attrNew, Map<String, Object> attrOld) throws LogControllerException {
        final CustomTransferObject merge = new CustomTransferObject();
        for (final Entry<String, Object> entry : attrNew.entrySet()) {
            final String chave = entry.getKey().toString();
            final Object valorNew = entry.getValue();
            final Object valorOld = attrOld != null ? attrOld.get(chave) : null;

            // Se os objetos são diferentes (resultado do equals)
            if (!Objects.equals(valorNew, valorOld)) {
                boolean valorAlterado = true;

                // Verifica se não são nulos, e caso não sejam, trata os valores para melhorar a comparação
                if ((valorNew != null) && (valorOld != null)) {
                    if ((valorNew instanceof String) && (valorOld instanceof String)) {
                        // Se são Strings, remove espaços para a comparação
                        valorAlterado = !valorNew.toString().trim().equals(valorOld.toString().trim());
                    } else if ((valorNew instanceof BigDecimal) && (valorOld instanceof BigDecimal)) {
                        // Se são BigDecimal, utiliza o compareTo para ignorar a escala
                        // https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html#compareTo-java.math.BigDecimal-
                        valorAlterado = ((BigDecimal) valorNew).compareTo((BigDecimal) valorOld) != 0;
                    }
                } else if ((valorNew != null) && (valorOld == null)) {
                    // Se o antigo é nulo, verifica se o novo não é uma String vazia
                    if (valorNew instanceof String) {
                        valorAlterado = !valorNew.toString().isEmpty();
                    }
                } else // Se o novo é nulo, verifica se o antigo não é uma String vazia
				if (((valorNew == null) && (valorOld != null)) && (valorOld instanceof String)) {
				    valorAlterado = !valorOld.toString().isEmpty();
				}

                if (valorAlterado) {
                    merge.setAttribute(chave, valorNew);

                    if (!chave.toUpperCase().contains("_CODIGO")) {
                        if ((valorOld == null) || ("".equals(valorOld) && (valorNew == null))) {
                            addChangedField(chave, valorNew);
                        } else {
                            addChangedField(chave, valorNew, valorOld);
                        }
                    }
                }
            }
        }
        return merge;
    }

    public CustomTransferObject getDeletedFields(Map<String, Object> attrOld) throws LogControllerException {
        final Map<String, Object> attrNew = new HashMap<>(attrOld.size());
        for (final Entry<String, Object> entry : attrOld.entrySet()) {
            final String chave = entry.getKey().toString();
            attrNew.put(chave, null);
        }
        return getUpdatedFields(attrNew, attrOld);
    }

    public void write() throws LogControllerException {
        getLogController().gravarLog(line.toString(), usuCodigo, ipUsuario, portaLogica, tipo, entidade, funCodigo, codigoEntidade00, codigoEntidade01, codigoEntidade02, codigoEntidade03, codigoEntidade04, codigoEntidade05, codigoEntidade06, codigoEntidade07, codigoEntidade08, codigoEntidade09, codigoEntidade10, logCanal);
    }

    public List<TransferObject> lstTiposLog() throws LogControllerException {
        return getLogController().lstTiposLog();
    }

    public List<TransferObject> getLogDataAtual() throws LogControllerException {
        return getLogController().getLogDataAtual();
    }

    public List<TransferObject> lstTiposEntidadesAuditoria(AcessoSistema responsavel) throws LogControllerException {
        final List<TransferObject> tipoEntTotal = getLogController().lstTiposEntidadesAuditoria();
        List<TransferObject> retorno = new ArrayList<>();

        if (responsavel.isCsa()) {
            for (final TransferObject entidade : tipoEntTotal) {
                final String entCodigo = (String) entidade.getAttribute(Columns.TEN_CODIGO);

                if (Log.CONSIGNATARIA.equals(entCodigo) || Log.AUTORIZACAO.equals(entCodigo) || Log.CORRESPONDENTE.equals(entCodigo) || Log.PERFIL.equals(entCodigo) || Log.SERVICO.equals(entCodigo) || Log.USUARIO.equals(entCodigo)) {
                    retorno.add(entidade);
                }
            }
        } else {
            retorno = tipoEntTotal;
        }

        return retorno;
    }

    public List<TransferObject> lstTipoEntidade(List<String> tenCodigos) throws LogControllerException {
        return getLogController().lstTipoEntidade(tenCodigos);
    }

    public List<TipoEntidade> getTiposEntidade(AcessoSistema responsavel) throws LogControllerException {
        return getLogController().getTiposEntidade(responsavel);
    }

    public List<TransferObject> lstHistoricoArqLog(Date dataInicio, Date dataFim) throws LogControllerException {
        return getLogController().lstHistoricoArqLog(dataInicio, dataFim);
    }

    public void geraHistoricoLog(AcessoSistema responsavel) throws LogControllerException {
        getLogController().geraHistoricoLog(responsavel);
    }

    public void setAgendamento(String valor) {
        setCodigoEntidade(getAgendamento(), valor);
    }

    public String getAgendamento() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.AGD_CODIGO));
    }

    public void setStatusAgendamento(String valor) {
        setCodigoEntidade(getStatusAgendamento(), valor);
    }

    public String getStatusAgendamento() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.SAG_CODIGO));
    }

    public void setTipoAgendamento(String valor) {
        setCodigoEntidade(getTipoAgendamento(), valor);
    }

    public String getTipoAgendamento() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TAG_CODIGO));
    }

    public void setCalendario(String valor) {
        setCodigoEntidade(getCalendario(), valor);
    }

    public String getCalendario() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CAL_DATA));
    }

    public void setCalendarioBase(String valor) {
        setCodigoEntidade(getCalendarioBase(), valor);
    }

    public String getCalendarioBase() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CAB_DATA));
    }

    public void setTipoEntidade(String valor) {
        setCodigoEntidade(getTipoEntidade(), valor);
    }

    public String getTipoEntidade() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TEN_CODIGO));
    }

    public void setOcorrenciaAgendamento(String valor) {
        setCodigoEntidade(getOcorrenciaAgendamento(), valor);
    }

    public String getOcorrenciaAgendamento() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.OAG_CODIGO));
    }

    public void setAuditoriaCse(String valor) {
        setCodigoEntidade(getAuditoriaCse(), valor);
    }

    public String getAuditoriaCse() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ACE_CODIGO));
    }

    public void setAuditoriaCsa(String valor) {
        setCodigoEntidade(getAuditoriaCsa(), valor);
    }

    public String getAuditoriaCsa() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ACS_CODIGO));
    }

    public void setAuditoriaCor(String valor) {
        setCodigoEntidade(getAuditoriaCor(), valor);
    }

    public String getAuditoriaCor() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ACO_CODIGO));
    }

    public void setAuditoriaOrg(String valor) {
        setCodigoEntidade(getAuditoriaOrg(), valor);
    }

    public String getAuditoriaOrg() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.AOR_CODIGO));
    }

    public void setAuditoriaSup(String valor) {
        setCodigoEntidade(getAuditoriaSup(), valor);
    }

    public String getAuditoriaSup() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ASU_CODIGO));
    }

    public void setAutorizacaoDesconto(String valor) {
        setCodigoEntidade(getAutorizacaoDesconto(), valor);
    }

    public String getAutorizacaoDesconto() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ADE_CODIGO));
    }

    public void setParcelaDesconto(String valor) {
        setCodigoEntidade(getParcelaDesconto(), valor);
    }

    public String getParcelaDesconto() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PRD_CODIGO));
    }

    public void setAutorizacaoDescontoDestino(String valor) {
        setCodigoEntidade(getAutorizacaoDescontoDestino(), valor);
    }

    public String getAutorizacaoDescontoDestino() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ADE_CODIGO) + "_DESTINO");
    }

    public void setAcessoRecurso(String valor) {
        setCodigoEntidade(getAcessoRecurso(), valor);
    }

    public String getAcessoRecurso() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ACR_CODIGO));
    }

    public void setRegistroServidor(String valor) {
        setCodigoEntidade(getRegistroServidor(), valor);
    }

    public String getRegistroServidor() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.RSE_CODIGO));
    }

    public void setRegistroServidorDestino(String valor) {
        setCodigoEntidade(getRegistroServidorDestino(), valor);
    }

    public String getRegistroServidorDestino() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.RSE_CODIGO) + "_DESTINO");
    }

    public void setVincRseCodigo(String valor) {
        setCodigoEntidade(getVincRseCodigo(), valor);
    }

    public String getVincRseCodigo() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.VRS_CODIGO));
    }

    public void setCargoRseCodigo(String valor) {
        setCodigoEntidade(getCargoRseCodigo(), valor);
    }

    public String getCargoRseCodigo() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CRS_CODIGO));
    }

    public void setStatusRseCodigo(String valor) {
        setCodigoEntidade(getStatusRseCodigo(), valor);
    }

    public String getStatusRseCodigo() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.SRS_CODIGO));
    }

    public void setServidor(String valor) {
        setCodigoEntidade(getServidor(), valor);
    }

    public String getServidor() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.SER_CODIGO));
    }

    public void setVerbaConvenio(String valor) {
        setCodigoEntidade(getVerbaConvenio(), valor);
    }

    public String getVerbaConvenio() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.VCO_CODIGO));
    }

    public void setVerbaConvenioDestino(String valor) {
        setCodigoEntidade(getVerbaConvenioDestino(), valor);
    }

    public String getVerbaConvenioDestino() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.VCO_CODIGO) + "_DESTINO");
    }

    public void setConvenio(String valor) {
        setCodigoEntidade(getConvenio(), valor);
    }

    public String getConvenio() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CNV_CODIGO));
    }

    public void setConsignante(String valor) {
        setCodigoEntidade(getConsignante(), valor);
    }

    public String getConsignante() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CSE_CODIGO));
    }

    public void setConsignataria(String valor) {
        setCodigoEntidade(getConsignataria(), valor);
    }

    public String getConsignataria() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CSA_CODIGO));
    }

    public void setPlano(String valor) {
        setCodigoEntidade(getPlano(), valor);
    }

    public String getPlano() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PLA_CODIGO));
    }

    public void setConsignatariaDestino(String valor) {
        setCodigoEntidade(getConsignatariaDestino(), valor);
    }

    public String getConsignatariaDestino() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CSA_CODIGO) + "_DESTINO");
    }

    public void setGrupoConsignataria(String valor) {
        setCodigoEntidade(getGrupoConsignataria(), valor);
    }

    public String getGrupoConsignataria() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TGC_CODIGO));
    }

    public void setServico(String valor) {
        setCodigoEntidade(getServico(), valor);
    }

    public String getServico() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.SVC_CODIGO));
    }

    public void setServicoOrigem(String valor) {
        setCodigoEntidade(getServicoOrigem(), valor);
    }

    public String getServicoOrigem() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.RSV_SVC_CODIGO_ORIGEM));
    }

    public void setServicoDestino(String valor) {
        setCodigoEntidade(getServicoDestino(), valor);
    }

    public String getServicoDestino() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.RSV_SVC_CODIGO_DESTINO));
    }

    public void setGrupoServico(String valor) {
        setCodigoEntidade(getGrupoServico(), valor);
    }

    public String getGrupoServico() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TGS_CODIGO));
    }

    public void setTipoDadoAdicional(String valor) {
        setCodigoEntidade(getTipoDadoAdicional(), valor);
    }

    public String getTipoDadoAdicional() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TDA_CODIGO));
    }

    public void setTipoNatureza(String valor) {
        setCodigoEntidade(getTipoNatureza(), valor);
    }

    public String getTipoNatureza() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TNT_CODIGO));
    }

    public void setTipoNotificacao(String valor) {
        setCodigoEntidade(getTipoNotificacao(), valor);
    }

    public String getTipoNotificacao() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TNO_CODIGO));
    }

    public void setTipoOcorrencia(String valor) {
        setCodigoEntidade(getTipoOcorrencia(), valor);
    }

    public String getTipoOcorrencia() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TOC_CODIGO));
    }

    public void setTipoPenalidade(String valor) {
        setCodigoEntidade(getTipoPenalidade(), valor);
    }

    public String getTipoPenalidade() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TPE_CODIGO));
    }

    public void setTipoMotivoOperacao(String valor) {
        setCodigoEntidade(getTipoMotivoOperacao(), valor);
    }

    public String getTipoMotivoOperacao() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TMO_CODIGO));
    }

    public void setTipoMotivoReclamacao(String valor) {
        setCodigoEntidade(getTipoMotivoReclamacao(), valor);
    }

    public String getTipoMotivoReclamacao() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TMR_CODIGO));
    }

    public void setCoeficiente(String valor) {
        setCodigoEntidade(getCoeficiente(), valor);
    }

    public String getCoeficiente() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CFT_CODIGO));
    }

    public void setCoeficienteDesconto(String valor) {
        setCodigoEntidade(getCoeficienteDesconto(), valor);
    }

    public String getCoeficienteDesconto() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CDE_CODIGO));
    }

    public void setTipoCoeficienteCorrecao(String valor) {
        setCodigoEntidade(getTipoCoeficienteCorrecao(), valor);
    }

    public String getTipoCoeficienteCorrecao() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TCC_CODIGO));
    }

    public void setCorrespondente(String valor) {
        setCodigoEntidade(getCorrespondente(), valor);
    }

    public String getCorrespondente() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.COR_CODIGO));
    }

    public void setComunicacao(String valor) {
        setCodigoEntidade(getComunicacao(), valor);
    }

    public String getComunicacao() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CMN_CODIGO));
    }

    public void setEmpresaCorrespondente(String valor) {
        setCodigoEntidade(getEmpresaCorrespondente(), valor);
    }

    public String getEmpresaCorrespondente() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ECO_CODIGO));
    }

    public void setIndice(String valor) {
        setCodigoEntidade(getIndice(), valor);
    }

    public String getIndice() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.IND_CODIGO));
    }

    public void setMenu(String valor) {
        setCodigoEntidade(getMenu(), valor);
    }

    public String getMenu() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.MNU_CODIGO));
    }

    public void setItemMenu(String valor) {
        setCodigoEntidade(getItemMenu(), valor);
    }

    public String getItemMenu() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ITM_CODIGO));
    }

    public void setEndereco(String valor) {
        setCodigoEntidade(getEndereco(), valor);
    }

    public String getEndereco() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ECH_CODIGO));
    }

    public void setEnderecoConsignataria(String valor) {
        setCodigoEntidade(getEndereco(), valor);
    }

    public String getEnderecoConsignataria() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ENC_CODIGO));
    }

    public void setEnderecoCorrespondente(String valor) {
        setCodigoEntidade(getEndereco(), valor);
    }

    public String getEnderecoCorrespondente() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ECR_CODIGO));
    }

    public void setOrgao(String valor) {
        setCodigoEntidade(getOrgao(), valor);
    }

    public String getOrgao() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ORG_CODIGO));
    }

    public void setEstabelecimento(String valor) {
        setCodigoEntidade(getEstabelecimento(), valor);
    }

    public String getEstabelecimento() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.EST_CODIGO));
    }

    public void setUsuario(String valor) {
        setCodigoEntidade(getUsuario(), valor);
    }

    public String getUsuario() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.USU_CODIGO));
    }

    public void setOcorrenciaUsuario(String valor) {
        setCodigoEntidade(getOcorrenciaUsuario(), valor);
    }

    public String getOcorrenciaUsuario() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.OUS_CODIGO));
    }

    public void setPerfil(String valor) {
        setCodigoEntidade(getPerfil(), valor);
    }

    public String getPerfil() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PER_CODIGO));
    }

    public void setPerfilOrigem(String valor) {
        setCodigoEntidade(getPerfilOrigem(), valor);
    }

    public String getPerfilOrigem() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, "PER_CODIGO_ORIGEM");
    }

    public void setPermissionario(String valor) {
        setCodigoEntidade(getPermissionario(), valor);
    }

    public String getPermissionario() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PRM_CODIGO));
    }

    public void setPapel(String valor) {
        setCodigoEntidade(getPapel(), valor);
    }

    public String getPapel() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PAP_CODIGO));
    }

    public void setFuncao(String valor) {
        setCodigoEntidade(getFuncao(), valor);
    }

    public String getFuncao() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.FUN_CODIGO));
    }

    public void setGrupoFuncao(String valor) {
        setCodigoEntidade(getGrupoFuncao(), valor);
    }

    public String getGrupoFuncao() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.GRF_CODIGO));
    }

    public void setParamTarifCse(String valor) {
        setCodigoEntidade(getParamTarifCse(), valor);
    }

    public String getParamTarifCse() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PCV_CODIGO));
    }

    public void setTipoParamSvc(String valor) {
        setCodigoEntidade(getTipoParamSvc(), valor);
    }

    public String getTipoParamSvc() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TPS_CODIGO));
    }

    public void setTipoParamCsa(String valor) {
        setCodigoEntidade(getTipoParamCsa(), valor);
    }

    public String getTipoParamCsa() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TPA_CODIGO));
    }

    public void setParamSvcCse(String valor) {
        setCodigoEntidade(getParamSvcCse(), valor);
    }

    public String getParamSvcCse() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PSE_CODIGO));
    }

    public void setParamSvcCsa(String valor) {
        setCodigoEntidade(getParamSvcCsa(), valor);
    }

    public String getParamSvcCsa() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PSC_CODIGO));
    }

    public void setPrazo(String valor) {
        setCodigoEntidade(getPrazo(), valor);
    }

    public String getPrazo() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PRZ_CODIGO));
    }

    public void setPrazoConsignataria(String valor) {
        setCodigoEntidade(getPrazoConsignataria(), valor);
    }

    public String getPrazoConsignataria() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PZC_CODIGO));
    }

    public void setTipoParamSistCse(String valor) {
        setCodigoEntidade(getTipoParamSistCse(), valor);
    }

    public String getTipoParamSistCse() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TPC_CODIGO));
    }

    public void setTipoParamTarifCse(String valor) {
        setCodigoEntidade(getTipoParamTarifCse(), valor);
    }

    public String getTipoParamTarifCse() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TPT_CODIGO));
    }

    public void setTipoParamSistCsa(String valor) {
        setCodigoEntidade(getTipoParamSistCsa(), valor);
    }

    public String getTipoParamSistCsa() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TPA_CODIGO));
    }

    public void setTipoParamOrgao(String valor) {
        setCodigoEntidade(getTipoParamOrgao(), valor);
    }

    public String getTipoParamOrgao() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TAO_CODIGO));
    }

    public void setResultadoRegraValidacaoMovimento(String valor) {
        setCodigoEntidade(getResultadoRegraValidacaoMovimento(), valor);
    }

    public String getResultadoRegraValidacaoMovimento() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.RVM_CODIGO));
    }

    public void setResultadoValidacaoMovimento(String valor) {
        setCodigoEntidade(getResultadoValidacaoMovimento(), valor);
    }

    public String getResultadoValidacaoMovimento() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.RVA_CODIGO));
    }

    public void setStatusAutorizacao(String valor) {
        setCodigoEntidade(getStatusAutorizacao(), valor);
    }

    public String getStatusAutorizacao() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.SAD_CODIGO));
    }

    public void setStatusConvenio(String valor) {
        setCodigoEntidade(getStatusConvenio(), valor);
    }

    public String getStatusConvenio() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.SCV_CODIGO));
    }

    public void setStatusDespesaComum(String valor) {
        setCodigoEntidade(getStatusDespesaComum(), valor);
    }

    public String getStatusDespesaComum() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.SDC_CODIGO));
    }

    public void setStatusParcela(String valor) {
        setCodigoEntidade(getStatusParcela(), valor);
    }

    public String getStatusParcela() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.SPD_CODIGO));
    }

    public void setStatusLogin(String valor) {
        setCodigoEntidade(getStatusLogin(), valor);
    }

    public String getStatusLogin() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.STU_CODIGO));
    }

    public void setFaq(String valor) {
        setCodigoEntidade(getFaq(), valor);
    }

    public String getFaq() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.FAQ_CODIGO));
    }

    public void setMargem(String valor) {
        setCodigoEntidade(getMargem(), valor);
    }

    public String getMargem() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.MAR_CODIGO));
    }

    public void setMensagem(String valor) {
        setCodigoEntidade(getMensagem(), valor);
    }

    public String getMensagem() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.MEN_CODIGO));
    }

    public void setNotificacaoDispostivo(String valor) {
        setCodigoEntidade(getNotificacaoDispostivo(), valor);
    }

    public String getNotificacaoDispostivo() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.NDI_CODIGO));
    }

    public void setNotificacaoEmail(String valor) {
        setCodigoEntidade(getNotificacaoEmail(), valor);
    }

    public String getNotificacaoEmail() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.NEM_CODIGO));
    }

    public void setRelatorio(String valor) {
        setCodigoEntidade(getRelatorio(), valor);
    }

    public String getRelatorio() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.REL_CODIGO));
    }

    public void setRelacionamentoServico(String valor) {
        setCodigoEntidade(getRelacionamentoServico(), valor);
    }

    public String getRelacionamentoServico() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.RSV_CODIGO));
    }

    public void setNaturezaServico(String valor) {
        setCodigoEntidade(getNaturezaServico(), valor);
    }

    public String getNaturezaServico() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.NSE_CODIGO));
    }

    public void setCfcPeriodo(String valor) {
        setCodigoEntidade(getCfcPeriodo(), valor);
    }

    public String getCfcPeriodo() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CFC_PERIODO));
    }

    public void setCfePeriodo(String valor) {
        setCodigoEntidade(getCfePeriodo(), valor);
    }

    public String getCfePeriodo() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CFE_PERIODO));
    }

    public void setCfoPeriodo(String valor) {
        setCodigoEntidade(getCfoPeriodo(), valor);
    }

    public String getCfoPeriodo() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CFO_PERIODO));
    }

    public void setHistoricoMargem(String valor) {
        setCodigoEntidade(getHistoricoMargem(), valor);
    }

    public String getHistoricoMargem() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.HMR_CODIGO));
    }

    public void setRegraRestricaoAcesso(String valor) {
        setCodigoEntidade(getRegraRestricaoAcesso(), valor);
    }

    public String getRegraRestricaoAcesso() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.RRA_CODIGO));
    }

    public void setPosto(String valor) {
        setCodigoEntidade(getPosto(), valor);
    }

    public String getPosto() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.POS_CODIGO));
    }

    public void setParametroPlano(String valor) {
        setCodigoEntidade(getParametroPlano(), valor);
    }

    public String getParametroPlano() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.TPP_CODIGO));
    }

    public void setDespesaComum(String valor) {
        setCodigoEntidade(getDespesaComum(), valor);
    }

    public String getDespesaComum() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.DEC_CODIGO));
    }

    public void setLimiteTaxaJuros(String valor) {
        setCodigoEntidade(getLimiteTaxaJuros(), valor);
    }

    public String getLimiteTaxaJuros() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.LTJ_CODIGO));
    }

    public void setReclamacaoRegistroServidor(String valor) {
        setCodigoEntidade(getReclamacaoRegistroServidor(), valor);
    }

    public String getReclamacaoRegistroServidor() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.RRS_CODIGO));
    }

    public void setStatusProposta(String valor) {
        setCodigoEntidade(getStatusProposta(), valor);
    }

    public String getStatusProposta() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.STP_CODIGO));
    }

    public void setStatusProtocoloSenhaAutorizacao(String valor) {
        setCodigoEntidade(getStatusProtocoloSenhaAutorizacao(), valor);
    }

    public String getStatusProtocoloSenhaAutorizacao() {
        // TODO Criar tabela de status de protocolo de senha de autorização
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, "SPA_CODIGO");
    }

    public void setPropostaPagamentoDivida(String valor) {
        setCodigoEntidade(getPropostaPagamentoDivida(), valor);
    }

    public String getPropostaPagamentoDivida() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PPD_CODIGO));
    }

    public void setPropostaLeilaoSolicitacao(String valor) {
        setCodigoEntidade(getPropostaLeilaoSolicitacao(), valor);
    }

    public String getPropostaLeilaoSolicitacao() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PLS_CODIGO));
    }

    public void setAnaliseRiscoRegistroServidor(String valor) {
        setCodigoEntidade(getAnaliseRiscoRegistroServidor(), valor);
    }

    public String getAnaliseRiscoRegistroServidor() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ARR_CODIGO));
    }

    public void setProtocoloSenhaAutorizacao(String valor) {
        setCodigoEntidade(getProtocoloSenhaAutorizacao(), valor);
    }

    public String getProtocoloSenhaAutorizacao() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.PSA_CODIGO));
    }

    public void setContratoBeneficio(String valor) {
        setCodigoEntidade(getContratoBeneficio(), valor);
    }

    public String getContratoBeneficio() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CBE_CODIGO));
    }

    public void setBoletoServidor(String valor) {
        setCodigoEntidade(getBoletoServidor(), valor);
    }

    public String getBoletoServidor() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.BOS_CODIGO));
    }

    public void setArquivo(String valor) {
        setCodigoEntidade(getArquivo(), valor);
    }

    public String getArquivo() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ARQ_CODIGO));
    }

    public void setAtendimento(String valor) {
        setCodigoEntidade(getAtendimento(), valor);
    }

    public String getAtendimento() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.ATE_CODIGO));
    }


    private void setCodigoEntidade(String campo, String valor) {
        if (TextHelper.isNull(campo)) {
            return;
        }
        valor = !TextHelper.isNull(valor) ? valor : null;
        if (Columns.LOG_COD_ENTIDADE_00.equalsIgnoreCase(campo)) {
            codigoEntidade00 = valor;
        } else if (Columns.LOG_COD_ENTIDADE_01.equalsIgnoreCase(campo)) {
            codigoEntidade01 = valor;
        } else if (Columns.LOG_COD_ENTIDADE_02.equalsIgnoreCase(campo)) {
            codigoEntidade02 = valor;
        } else if (Columns.LOG_COD_ENTIDADE_03.equalsIgnoreCase(campo)) {
            codigoEntidade03 = valor;
        } else if (Columns.LOG_COD_ENTIDADE_04.equalsIgnoreCase(campo)) {
            codigoEntidade04 = valor;
        } else if (Columns.LOG_COD_ENTIDADE_05.equalsIgnoreCase(campo)) {
            codigoEntidade05 = valor;
        } else if (Columns.LOG_COD_ENTIDADE_06.equalsIgnoreCase(campo)) {
            codigoEntidade06 = valor;
        } else if (Columns.LOG_COD_ENTIDADE_07.equalsIgnoreCase(campo)) {
            codigoEntidade07 = valor;
        } else if (Columns.LOG_COD_ENTIDADE_08.equalsIgnoreCase(campo)) {
            codigoEntidade08 = valor;
        } else if (Columns.LOG_COD_ENTIDADE_09.equalsIgnoreCase(campo)) {
            codigoEntidade09 = valor;
        } else if (Columns.LOG_COD_ENTIDADE_10.equalsIgnoreCase(campo)) {
            codigoEntidade10 = valor;
        }
    }

    public void setNaturezaConsignataria(String valor) {
        setCodigoEntidade(getNaturezaConsignataria(), valor);
    }

    public String getNaturezaConsignataria() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.NCA_CODIGO));
    }

    public void setDefinicaoTaxaJuros(String valor) {
        setCodigoEntidade(getDefinicaoTaxaJuros(), valor);
    }

    public String getDefinicaoTaxaJuros() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.DTJ_CODIGO));
    }

	public void setFunCodigo(String funCodigo) {
		this.funCodigo = funCodigo;
	}

	public void setConsultaMargemSemSenha(String valor) {
        setCodigoEntidade(getConsultaMargemSemSenha(), valor);
    }

    public String getConsultaMargemSemSenha() {
        return ControleTipoEntidade.getInstance().recuperaColuna(entidade, Columns.getColumnName(Columns.CSS_CODIGO));
    }

}
