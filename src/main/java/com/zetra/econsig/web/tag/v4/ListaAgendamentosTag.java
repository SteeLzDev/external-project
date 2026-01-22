package com.zetra.econsig.web.tag.v4;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusAgendamentoEnum;
import com.zetra.econsig.web.tag.ZetraTagSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: ListaAgendamentosTag</p>
 * <p>Description: Tag para listar os agendamentos de um usuário para um relatório.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAgendamentosTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaAgendamentosTag.class);

    @Autowired
    private AgendamentoController agendamentoController;

    private String tipoRelatorio;

    private String linkPaginacao;

    private int offset;

    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public String getLinkPaginacao() {
        return linkPaginacao;
    }

    public void setLinkPaginacao(String linkPaginacao) {
        this.linkPaginacao = linkPaginacao;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            final Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio(tipoRelatorio);

            List<TransferObject> agendamentos = null;
            String classeAgendamento = relatorio.getClasseAgendamento();
            if (TextHelper.isNull(classeAgendamento)) {
                classeAgendamento = com.zetra.econsig.job.jobs.RelatorioAgendadoJob.class.getName();
            }

            final int qtdPorPagina = JspHelper.LIMITE;
            try {
                if (!responsavel.isSer()) {
                    agendamentos = agendamentoController.lstAgendamentos(null, null, null, classeAgendamento, responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), "", tipoRelatorio, offset, qtdPorPagina, responsavel);
                }
            } catch (final AgendamentoControllerException e) {
                agendamentos = new ArrayList<>();
                LOG.error("Não foi possível listar os agendamentos do usuário: " + responsavel.getUsuCodigo());
            }

            if ((agendamentos != null) && !agendamentos.isEmpty()) {
                final String tituloPaginacao = ApplicationResourcesHelper.getMessage("rotulo.paginacao.titulo.relatorio.agendados", responsavel, relatorio.getTitulo());
                final String tituloRelatoriosAgendados = ApplicationResourcesHelper.getMessage("rotulo.titulo.relatorios.agendados", responsavel);
                int totalRegistros = agendamentos.size();
                try {
                    totalRegistros = agendamentoController.countAgendamentos(null, null, null, classeAgendamento, responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), "", tipoRelatorio, responsavel);
                } catch (final AgendamentoControllerException e) {
                    LOG.error("Não foi possível listar os agendamentos do usuário: " + responsavel.getUsuCodigo());
                }
                final int qtdPagina = (totalRegistros / qtdPorPagina) + ((totalRegistros % qtdPorPagina) == 0 ? 0 : 1);
                final int first = (totalRegistros > 0) ? offset + 1 : 0;
                final int last = Math.min(offset + qtdPorPagina, totalRegistros);
                final int paginaAtual = (last / qtdPorPagina) + ((last % qtdPorPagina) == 0 ? 0 : 1);

                // Inicia geração do código HTML
                final StringBuilder code = new StringBuilder();
                code.append("<div class=\"card\">\n");
                code.append("  <div class=\"card-header hasIcon\">\n");
                code.append("    <span class=\"card-header-icon\"><svg width=\"25\"><use xlink:href=\"../img/sprite.svg#i-relatorio\"></use></svg></span>\n");
                code.append("    <h2 class=\"card-header-title\">" + tituloRelatoriosAgendados + "</h2>\n");
                code.append("  </div>\n");
                code.append("  <div class=\"card-body table-responsive p-0\">");
                code.append("    <table class=\"table table-striped table-hover\">\n");
                if (!TextHelper.isNull(linkPaginacao)) {
                    final int total = agendamentoController.countAgendamentos(null, null, null, classeAgendamento, responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), "", tipoRelatorio, responsavel);
                    linkPaginacao += "&indice=2&total2=" + total;
                }
                code.append("      <thead>\n");
                code.append("        <tr>\n");
                code.append("          <th scope=\"col\" width=\"30%\">").append(ApplicationResourcesHelper.getMessage("rotulo.agendamento.descricao", responsavel)).append("</th>\n");
                code.append("          <th scope=\"col\" width=\"15%\">").append(ApplicationResourcesHelper.getMessage("rotulo.agendamento.data.cadastro", responsavel)).append("</th>\n");
                code.append("          <th scope=\"col\" width=\"15%\">").append(ApplicationResourcesHelper.getMessage("rotulo.agendamento.data.prevista.execucao", responsavel)).append("</th>\n");
                code.append("          <th scope=\"col\" width=\"15%\">").append(ApplicationResourcesHelper.getMessage("rotulo.agendamento.status", responsavel)).append("</th>\n");
                code.append("          <th scope=\"col\" width=\"10%\">").append(ApplicationResourcesHelper.getMessage("rotulo.agendamento.tipo", responsavel)).append("</th>\n");
                code.append("          <th scope=\"col\" width=\"10%\">").append(ApplicationResourcesHelper.getMessage("rotulo.agendamento.responsavel", responsavel)).append("</th>\n");
                code.append("          <th scope=\"col\" width=\"5%\">").append(ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel)).append("</th>\n");
                code.append("        </tr>\n");
                code.append("      </thead>\n");
                code.append("    <tbody>\n");
                for (final TransferObject to : agendamentos) {
                    final String agdCodigo = to.getAttribute(Columns.AGD_CODIGO).toString();
                    final String agdDescricao = to.getAttribute(Columns.AGD_DESCRICAO).toString();
                    final String agdDataCadastro = DateHelper.reformat(to.getAttribute(Columns.AGD_DATA_CADASTRO).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                    final String agdDataPrevista = DateHelper.reformat(to.getAttribute(Columns.AGD_DATA_PREVISTA).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
                    final String sagCodigo = to.getAttribute(Columns.SAG_CODIGO).toString();
                    final StatusAgendamentoEnum status = StatusAgendamentoEnum.recuperaStatusAgendamento(sagCodigo);
                    final String sagDescricao = to.getAttribute(Columns.SAG_DESCRICAO).toString();
                    final String tagDescricao = to.getAttribute(Columns.TAG_DESCRICAO).toString();
                    final String usuLogin = to.getAttribute(Columns.USU_LOGIN).toString();

                    code.append("        <tr>\n");
                    code.append("          <td>").append(TextHelper.forHtmlContent(agdDescricao)).append("</td>\n");
                    code.append("          <td>").append(TextHelper.forHtmlContent(agdDataCadastro)).append("</td>\n");
                    code.append("          <td>").append(TextHelper.forHtmlContent(agdDataPrevista)).append("</td>\n");
                    code.append("          <td>").append(TextHelper.forHtmlContent(sagDescricao)).append("</td>\n");
                    code.append("          <td>").append(TextHelper.forHtmlContent(tagDescricao)).append("</td>\n");
                    code.append("          <td>").append(TextHelper.forHtmlContent(usuLogin)).append("</td>\n");

                    if (StatusAgendamentoEnum.AGUARDANDO_EXECUCAO.equals(status) || StatusAgendamentoEnum.EXECUCAO_DIARIA.equals(status)) {
                        code.append("          <td>");
                        code.append("<a id=\"btnCancelarAgendamentoRelatorio_").append(TextHelper.forHtmlAttribute(agdDataCadastro).replace(' ', '_')).append("\" href=\"#no-back\" onClick=\"javascript:cancelaAgendamento('").append(TextHelper.forJavaScriptAttribute(agdCodigo)).append("'); return false;\">");
                        code.append(ApplicationResourcesHelper.getMessage("rotulo.botao.cancelar", responsavel)).append("</a>");
                        code.append("</td>\n");

                    } else {
                        code.append("          <td>").append(TextHelper.forHtmlContent(sagDescricao)).append("</td>\n");
                    }
                    code.append("        </tr>\n");
                }
                code.append("      </tbody>\n");
                code.append("      <tfoot>\n");
                code.append("        <tr>\n");
                code.append("          <td colspan=\"5\">");
                code.append(ApplicationResourcesHelper.getMessage("mensagem.rodape.tabela.relatorio.agendado", responsavel, relatorio.getTitulo(), DateHelper.format(DateHelper.getSystemDate(), "dd' de 'MMMMM' de 'yyyy")));
                code.append("<span class=\"font-italic\"> - ");
                code.append(ApplicationResourcesHelper.getMessage("rotulo.paginacao.registros.sem.estilo", responsavel, String.valueOf(first), String.valueOf(last), String.valueOf(totalRegistros)));
                code.append("</span></td>\n");
                code.append("        </tr>\n");
                code.append("      </tfoot>\n");
                code.append("    </table>\n");
                code.append("  </div>\n");

                code.append("  <div class=\"card-footer\">\n");
                code.append("    <nav aria-label=").append(TextHelper.forHtmlAttribute(tituloPaginacao)).append("\">\n");
                code.append("      <ul class=\"pagination justify-content-end\">\n");
                code.append("        <li class=\"page-item ").append((paginaAtual > 1) ? "" : "disabled").append("\">");
                code.append("<a class=\"page-link\" href=\"#no-back\" onClick=\"postData('").append(TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkPaginacao + "&offset2=0", request))).append("')\" aria-label='");
                code.append(ApplicationResourcesHelper.getMessage("rotulo.paginacao.anterior", responsavel)).append("'>«</a>");
                code.append("</li>\n");
                if ((paginaAtual - 10) > 1) {
                    final String linkPagina = SynchronizerToken.updateTokenInURL(linkPaginacao + "&offset2=" + 0, request);
                    code.append("        <li class=\"page-item\"><a class=\"page-link\" href=\"#no-back\" onClick=\"postData('").append(TextHelper.forJavaScriptAttribute(linkPagina)).append("')\">1</a></li>");
                    code.append("        <li class=\"page-item disabled\"><a class=\"page-link\" href=\"#no-back\">...</a></li>\n");
                }
                for (int contador = Math.max(1, paginaAtual - 10); contador <= Math.min(qtdPagina, paginaAtual + 10); contador++) {
                    final String linkPagina = SynchronizerToken.updateTokenInURL(linkPaginacao + "&offset2=" + ((contador - 1) * qtdPorPagina), request);
                    if (contador == paginaAtual) {
                        code.append("        <li class=\"page-item active\"><a class=\"page-link\" href=\"#no-back\" onClick=\"postData('").append(TextHelper.forJavaScriptAttribute(linkPagina)).append("')\">").append(contador);
                        code.append("<span class=\"sr-only\"> (").append(ApplicationResourcesHelper.getMessage("rotulo.paginacao.atual", responsavel)).append(")</span></a></li>\n");
                    } else {
                        code.append("        <li class=\"page-item\"><a class=\"page-link\" href=\"#no-back\" onClick=\"postData('").append(TextHelper.forJavaScriptAttribute(linkPagina)).append("')\">").append(contador).append("</a></li>\n");
                    }
                }
                if (qtdPagina > (paginaAtual + 10)) {
                    final String linkPagina = SynchronizerToken.updateTokenInURL(linkPaginacao + "&offset2=" + ((qtdPagina - 1) * qtdPorPagina), request);
                    code.append("        <li class=\"page-item disabled\"><a class=\"page-link\" href=\"#no-back\">...</a></li>\n");
                    code.append("        <li class=\"page-item\"><a class=\"page-link\" href=\"#no-back\" onClick=\"postData('").append(TextHelper.forJavaScriptAttribute(linkPagina)).append("')\">").append(qtdPagina).append("</a></li>");
                }
                code.append("        <li class=\"page-item ").append((paginaAtual < qtdPagina) ? "" : "disabled").append("\">");
                code.append("<a class=\"page-link\" href=\"#no-back\" onClick=\"postData('").append(TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkPaginacao + "&offset2=" + (paginaAtual * qtdPorPagina), request))).append("')\" ");
                code.append("aria-label='").append(ApplicationResourcesHelper.getMessage("rotulo.paginacao.proxima", responsavel)).append("'>»</a>");
                code.append("</li>\n");
                code.append("      </ul>\n");
                code.append("    </nav>\n");
                code.append("  </div>\n");
                code.append("</div>");

                pageContext.getOut().print(code.toString());
            }

            return EVAL_PAGE;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }
}
