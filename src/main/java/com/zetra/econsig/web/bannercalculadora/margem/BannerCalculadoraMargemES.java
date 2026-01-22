package com.zetra.econsig.web.bannercalculadora.margem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ValidaImportacaoSistema</p>
 * <p>Description: Interface para implementação de customizações para exibição da calculadora de margem na página inicial do Servidor
 * de importação de margem, retorno, transferidos e crítica.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class BannerCalculadoraMargemES extends BannerCalculadoraMargemBase {

    private static final Short MARGEM_CARTAO = Short.valueOf("3");
    private static final Short MARGEM_EMPRESTIMO = Short.valueOf("1");

	@Override
	public String montarBannerCalculadoraMargem (String rseCodigo, AcessoSistema responsavel) throws ZetraException {

		final StringBuilder corpoHtml = new StringBuilder();

		final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
		final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);

		final RegistroServidorTO rseTO = servidorController.findRegistroServidor(rseCodigo, responsavel);

		final RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);
		final BigDecimal salario = registroServidor.getRseSalario();
		final BigDecimal valorCompulsorio = registroServidor.getRseDescontosComp();
		final BigDecimal margemCartao = registroServidor.getRseMargem3();
		final BigDecimal valorRestEmprestimo = registroServidor.getRseMargemRest();

		final BigDecimal valorMargemEmprestimo = salario.multiply(new BigDecimal("0.40")).setScale(2, RoundingMode.HALF_UP);
		final BigDecimal valorMargemCartao = salario.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
		final BigDecimal limiteValorCompulsorio = (registroServidor.getRseMargem3() != null) && (registroServidor.getRseMargem3().compareTo(BigDecimal.ZERO) > 0) ? salario.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP) : salario.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);

		BigDecimal valorMaiorC = BigDecimal.ZERO ;

        if (!TextHelper.isNull(rseTO.getRseSalario()) && !TextHelper.isNull(valorCompulsorio)) {
            final List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, null, true, false, true, null, responsavel);
            final List<MargemTO> margensExibeSer = new ArrayList<>();
            final HashMap<Short, TransferObject> hashMargem = new HashMap<>();

            for (final MargemTO margem : margens) {
                if (!NAO_EXIBE.equals(margem.getMarExibeSer())) {
                    margensExibeSer.add(margem);
                }
            }

            for (final MargemTO margemExibeSer : margensExibeSer) {
            	if (TextHelper.isNull(margemExibeSer.getMarPorcentagem()) || (MARGEM_CARTAO.equals(margemExibeSer.getMarCodigo()) && (TextHelper.isNull(margemCartao) || (margemCartao.compareTo(BigDecimal.ZERO) == 0)))) {
                    continue;
                }

                if (MARGEM_CARTAO.equals(margemExibeSer.getMarCodigo()) && (valorRestEmprestimo.compareTo(BigDecimal.ZERO) < 0)) {
                	valorMaiorC = valorRestEmprestimo;
                } else if (MARGEM_CARTAO.equals(margemExibeSer.getMarCodigo()) && (valorRestEmprestimo.compareTo(BigDecimal.ZERO) >= 0)) {
                	valorMaiorC = BigDecimal.ZERO;
                } else if (MARGEM_EMPRESTIMO.equals(margemExibeSer.getMarCodigo())) {
                	valorMaiorC = limiteValorCompulsorio.subtract(valorCompulsorio).setScale(2, RoundingMode.HALF_UP);

            		if (valorMaiorC.compareTo(BigDecimal.ZERO) > 0) {
            			valorMaiorC = BigDecimal.ZERO;
            		}
                }

                final BigDecimal somaContratosAtivosMargem = MARGEM_EMPRESTIMO.equals(margemExibeSer.getMarCodigo()) ? registroServidor.getRseMargemUsada() : registroServidor.getRseMargemUsada3();
                final BigDecimal calculoPorcentagem = MARGEM_EMPRESTIMO.equals(margemExibeSer.getMarCodigo()) ? valorMargemEmprestimo : valorMargemCartao;

                final CustomTransferObject objetoMargem = new CustomTransferObject();
                objetoMargem.setAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_SER_SALARIO, NumberHelper.formata(salario.doubleValue(), ApplicationResourcesHelper.getMessage("rotulo.moeda.pattern",responsavel)));
                objetoMargem.setAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_MAR_DESCRICAO, margemExibeSer.getMarDescricao());
                objetoMargem.setAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_MAR_PORCENTAGEM, NumberHelper.formata(margemExibeSer.getMarPorcentagem().setScale(2, RoundingMode.HALF_UP).doubleValue(), "#,###; -#,###") + " %");
                objetoMargem.setAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_CALC_PORCENTAGEM, NumberHelper.formata(calculoPorcentagem.doubleValue(), ApplicationResourcesHelper.getMessage("rotulo.moeda.pattern",responsavel)));
                objetoMargem.setAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_TOTAL_CONSIGNACOES_ATIVAS, NumberHelper.formata(somaContratosAtivosMargem.doubleValue(), ApplicationResourcesHelper.getMessage("rotulo.moeda.pattern",responsavel)));
                objetoMargem.setAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_VALOR_COMPULSORIO, NumberHelper.formata(valorCompulsorio.doubleValue(), ApplicationResourcesHelper.getMessage("rotulo.moeda.pattern",responsavel)));
                objetoMargem.setAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_VALOR_MAIOR_C, NumberHelper.formata(valorMaiorC.doubleValue(), ApplicationResourcesHelper.getMessage("rotulo.moeda.pattern",responsavel)));
				objetoMargem.setAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_VALOR_FINAL_MARGEM, NumberHelper.formata(calculoPorcentagem.add(valorMaiorC).subtract(somaContratosAtivosMargem).doubleValue(), ApplicationResourcesHelper.getMessage("rotulo.moeda.pattern",responsavel)));

                hashMargem.put(margemExibeSer.getMarCodigo(), objetoMargem);
            }

            if (!hashMargem.isEmpty()) {
				for (final Map.Entry<Short, TransferObject> entry : hashMargem.entrySet()) {
					final TransferObject margem = entry.getValue();
					corpoHtml.append("<div class='col-sm-6'>");
					corpoHtml.append("<div class='card'> ");
					corpoHtml.append("<div class='card-header'><h2 class='card-header-title'>").append(ApplicationResourcesHelper.getMessage("rotulo.banner.calculadora.margem.titulo", responsavel)).append("</h2></div> ");
					corpoHtml.append("<div class='card-body'> ");
					corpoHtml.append("<h5 class='text-danger'> ");
					corpoHtml.append(margem.getAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_MAR_DESCRICAO)).append(" ").append(margem.getAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_MAR_PORCENTAGEM));
					corpoHtml.append("</h5> ");
					corpoHtml.append("<dl class='row data-list firefox-print-fix'> ");
					corpoHtml.append("<dt class='col-sm-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.banner.calculadora.margem.salario", responsavel)).append("</dt> ");
					corpoHtml.append("<dd class='col-sm-6'>").append(margem.getAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_SER_SALARIO)).append("</dd>");
					corpoHtml.append("<dt class='col-sm-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.banner.calculadora.margem.percentual.salario", responsavel, (String) margem.getAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_MAR_PORCENTAGEM))).append("</dt> ");
					corpoHtml.append("<dd class='col-sm-6'> ");
					corpoHtml.append(margem.getAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_CALC_PORCENTAGEM));
					corpoHtml.append("</dd> ");
					corpoHtml.append("<dt class='col-sm-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.banner.calculadora.margem.total.consig.compulsorio", responsavel)).append("</dt> ");
					corpoHtml.append("<dd class='col-sm-6 text-danger'> ");
					corpoHtml.append(margem.getAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_VALOR_COMPULSORIO));
					corpoHtml.append("</dd> ");
					corpoHtml.append("<dt class='col-sm-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.banner.calculadora.margem.total.consig.valor.maior.c", responsavel)).append("</dt> ");
					corpoHtml.append("<dd class='col-sm-6 text-danger'> ");
					corpoHtml.append(margem.getAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_VALOR_MAIOR_C));
					corpoHtml.append("</dd> ");
					corpoHtml.append("<dt class='col-sm-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.banner.calculadora.margem.total.consig.ativas.es", responsavel)).append("</dt> ");
					corpoHtml.append("<dd class='col-sm-6 text-danger'> ");
					corpoHtml.append(margem.getAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_TOTAL_CONSIGNACOES_ATIVAS));
					corpoHtml.append("</dd> ");
					corpoHtml.append("<div class='legend'></div> ");
					corpoHtml.append("<dt class='col-sm-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.banner.calculadora.margem.valor.final.es", responsavel, (String) margem.getAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_MAR_PORCENTAGEM))).append("</dt> ");
					corpoHtml.append("<dd class='col-sm-6'> ");
					corpoHtml.append(margem.getAttribute(CodedValues.CHAVE_CALC_MARGEM_PORTAL_VALOR_FINAL_MARGEM));
					corpoHtml.append("</dd> ");
					corpoHtml.append("</dl> ");
					corpoHtml.append("</div> ");
					corpoHtml.append("</div> ");
					corpoHtml.append("</div> ");
				}
			}
		}

		return corpoHtml.toString();
	}
}
