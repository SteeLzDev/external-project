package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.COMPRADO_PELA_ENTIDADE;
import static com.zetra.econsig.webservice.CamposAPI.CONTRATOS_BLOQ_A_BLOQUEAR;
import static com.zetra.econsig.webservice.CamposAPI.CONTRATO_LIQUIDADO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_FIM_COMPRA;
import static com.zetra.econsig.webservice.CamposAPI.DATA_INICIO_COMPRA;
import static com.zetra.econsig.webservice.CamposAPI.DIAS_BLOQUEIO;
import static com.zetra.econsig.webservice.CamposAPI.DIAS_SEM_APRV_SDV;
import static com.zetra.econsig.webservice.CamposAPI.DIAS_SEM_INFO_SDV;
import static com.zetra.econsig.webservice.CamposAPI.DIAS_SEM_LIQUIDACAO;
import static com.zetra.econsig.webservice.CamposAPI.DIAS_SEM_PG_SDV;
import static com.zetra.econsig.webservice.CamposAPI.INFO_COMPRAS;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.PENDENCIA_COMPRA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SDV_APROVADO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_INFORMADO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_PAGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.compra.MontaCriterioAcompanhamentoCompra;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: AcompanharCompraContratoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de acompanhar compra de contrato</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AcompanharCompraContratoCommand extends RequisicaoExternaCommand {

	public AcompanharCompraContratoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
	}

	@Override
	protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
		boolean temPendenciaCompra = (parametros.get(PENDENCIA_COMPRA) != null) ? ((Boolean) parametros.get(PENDENCIA_COMPRA)).booleanValue():false;
		boolean temAdeABloqOuBloq = (parametros.get(CONTRATOS_BLOQ_A_BLOQUEAR) != null) ? ((Boolean) parametros.get(CONTRATOS_BLOQ_A_BLOQUEAR)).booleanValue():false;
		AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();

		CustomTransferObject criteriosSelecionadosPesquisa = montaCriterio(parametros);
		String csaCodigo = (String) parametros.get(CSA_CODIGO);

		List<TransferObject> listaContratos = new ArrayList<>();
		List<TransferObject> listAux = null;
		if (!temPendenciaCompra && !temAdeABloqOuBloq) {
			listaContratos = adeDelegate.pesquisarCompraContratos(criteriosSelecionadosPesquisa, csaCodigo, null, null, responsavel);
		} else if (temPendenciaCompra) {
			CustomTransferObject criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaInfoSaldoDevedor(criteriosSelecionadosPesquisa);
			listAux = adeDelegate.pesquisarCompraContratos(criteriosPesquisa, csaCodigo, null, null, responsavel);
			listaContratos.addAll(listAux);

			criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaPagtoSaldoDevedor(criteriosSelecionadosPesquisa);
			listAux = adeDelegate.pesquisarCompraContratos(criteriosPesquisa, csaCodigo, null, null, responsavel);
			listaContratos.addAll(listAux);

			criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaLiquidacao(criteriosSelecionadosPesquisa);
			listAux = adeDelegate.pesquisarCompraContratos(criteriosPesquisa, csaCodigo, null, null, responsavel);
			listaContratos.addAll(listAux);
		} else if (temAdeABloqOuBloq) {
			CustomTransferObject criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaBloqueioInfoSaldoDevedor(criteriosSelecionadosPesquisa);
			listAux = adeDelegate.pesquisarCompraContratos(criteriosPesquisa, csaCodigo, null, null, responsavel);
			listaContratos.addAll(listAux);

			criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaBloqueioPagtoSaldoDevedor(criteriosSelecionadosPesquisa);
			listAux = adeDelegate.pesquisarCompraContratos(criteriosPesquisa, csaCodigo, null, null, responsavel);
			listaContratos.addAll(listAux);

			criteriosPesquisa = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaBloqueioLiquidacao(criteriosSelecionadosPesquisa);
			listAux = adeDelegate.pesquisarCompraContratos(criteriosPesquisa, csaCodigo, null, null, responsavel);
			listaContratos.addAll(listAux);
		}

		if (listaContratos == null || listaContratos.isEmpty()) {
		    parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.nenhumaConsignacaoEncontrada", responsavel));
		    parametros.put(COD_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.nenhumaConsignacaoEncontrada" + ZetraException.MENSAGEM_PROCESSAMENTO_XML, responsavel));
        }

		parametros.put(INFO_COMPRAS, listaContratos);
	}

	@Override
	protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
		super.validaEntrada(parametros);

		if (DateHelper.dayDiff((Date) parametros.get(DATA_FIM_COMPRA), (Date) parametros.get(DATA_INICIO_COMPRA)) > 30) {
			throw new ZetraException("mensagem.erro.periodo.informado.data.compra.contrato", responsavel);
		}
	}

	/**
	 * monta o filtro a ser passado para ListaAcompanhamentoCompraQuery
	 * @param parametros
	 * @return
	 * @throws ZetraException
	 */
	private CustomTransferObject montaCriterio(Map<CamposAPI, Object> parametros) throws ZetraException {
		CustomTransferObject criteriosPesquisa = new CustomTransferObject();

		if (!TextHelper.isNull(parametros.get(COMPRADO_PELA_ENTIDADE))) {
			boolean compradoPelaEntidade = ((Boolean) parametros.get(COMPRADO_PELA_ENTIDADE)).booleanValue();
			criteriosPesquisa.setAttribute("origem", compradoPelaEntidade ? "1":"0");
		}

		String temSaldoDevedor = parametros.get(SDV_INFORMADO) != null ? ((((Boolean) parametros.get(SDV_INFORMADO)).booleanValue()) ? "SIM" : "NAO") : "TODOS";
		criteriosPesquisa.setAttribute("temSaldoDevedor", temSaldoDevedor);

		String temSaldoAprv = parametros.get(SDV_APROVADO) != null ? ((((Boolean) parametros.get(SDV_APROVADO)).booleanValue()) ? "SIM" : "NAO") : "TODOS";
		criteriosPesquisa.setAttribute("saldoDevedorAprovado", temSaldoAprv);


		String saldoDevedorPago = parametros.get(SDV_PAGO) != null ? ((((Boolean) parametros.get(SDV_PAGO)).booleanValue()) ? "SIM" : "NAO") : "TODOS";
		criteriosPesquisa.setAttribute("saldoDevedorPago", saldoDevedorPago);

		String liquidado = parametros.get(CONTRATO_LIQUIDADO) != null ? ((((Boolean) parametros.get(CONTRATO_LIQUIDADO)).booleanValue()) ? "SIM" : "NAO") : "TODOS";
		criteriosPesquisa.setAttribute("liquidado", liquidado);

		if (!TextHelper.isNull(parametros.get(DIAS_SEM_INFO_SDV))) {
			criteriosPesquisa.setAttribute("diasSemSaldoDevedor", parametros.get(DIAS_SEM_INFO_SDV));
		}
		if (!TextHelper.isNull(parametros.get(DIAS_SEM_APRV_SDV))) {
			criteriosPesquisa.setAttribute("diasSemAprovacaoSaldoDevedor", parametros.get(DIAS_SEM_APRV_SDV));
		}
		if (!TextHelper.isNull(parametros.get(DIAS_SEM_PG_SDV))) {
			criteriosPesquisa.setAttribute("diasSemPagamentoSaldoDevedor", parametros.get(DIAS_SEM_PG_SDV));
		}
		if (!TextHelper.isNull(parametros.get(DIAS_SEM_LIQUIDACAO))) {
			criteriosPesquisa.setAttribute("diasSemLiquidacao", parametros.get(DIAS_SEM_LIQUIDACAO));
		}
		if (!TextHelper.isNull(parametros.get(DIAS_BLOQUEIO))) {
			criteriosPesquisa.setAttribute("diasBloqueio", parametros.get(DIAS_BLOQUEIO));
		}
		if (!TextHelper.isNull(parametros.get(DATA_INICIO_COMPRA))) {
			criteriosPesquisa.setAttribute("periodoIni", DateHelper.toDateString((Date) parametros.get(DATA_INICIO_COMPRA)));
		}
		if (!TextHelper.isNull(parametros.get(DATA_FIM_COMPRA))) {
			criteriosPesquisa.setAttribute("periodoFim", DateHelper.toDateString((Date) parametros.get(DATA_FIM_COMPRA)));
		}
		if (!TextHelper.isNull(parametros.get(ADE_NUMERO))) {
			criteriosPesquisa.setAttribute(Columns.ADE_NUMERO, parametros.get(ADE_NUMERO));
		}
		if (!TextHelper.isNull(parametros.get(RSE_MATRICULA))) {
			criteriosPesquisa.setAttribute(Columns.RSE_MATRICULA, parametros.get(RSE_MATRICULA));
		}
		if (!TextHelper.isNull(parametros.get(SER_CPF))) {
			criteriosPesquisa.setAttribute(Columns.SER_CPF, parametros.get(SER_CPF));
		}
		/*
		TODO a consulta nem espera um critério chamado CSA_CODIGO ???!!!
		if (!TextHelper.isNull(parametros.get(CSA_IDENTIFICADOR))) {
			ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
			ConsignatariaTransferObject csaTO = csaDelegate.findConsignatariaByIdn((String) parametros.get(CSA_IDENTIFICADOR), responsavel);
			criteriosPesquisa.setAttribute(CSA_CODIGO, csaTO.getCsaCodigo());
		}
		*/

		return criteriosPesquisa;
	}
}
