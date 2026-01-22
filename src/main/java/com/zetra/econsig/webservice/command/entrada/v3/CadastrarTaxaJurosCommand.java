package com.zetra.econsig.webservice.command.entrada.v3;

import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SERVICOS;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TAXA_JUROS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.ServicoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;
import com.zetra.econsig.webservice.soap.operacional.v3.TaxaDeJuros;

/**
 * <p>Title: CadastrarTaxaJurosCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig para cadastrar taxa de juros</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CadastrarTaxaJurosCommand extends RequisicaoExternaCommand {

    public CadastrarTaxaJurosCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);
        final String svcCodigo = (String) parametros.get(SVC_CODIGO);

        try {
            final ServicoDelegate serDelegate = new ServicoDelegate();
            serDelegate.findServico(svcCodigo);
        } catch (final ServicoControllerException e) {
            throw new ZetraException("mensagem.erro.servico.nao.encontrado", responsavel);
        }

        //Recupera identificador do órgão a pesquisar
        final String orgIdentificador = (String) parametros.get(ORG_IDENTIFICADOR);
        final String estIdentificador = (String) parametros.get(EST_IDENTIFICADOR);

        if (!TextHelper.isNull(orgIdentificador)) {
            final OrgaoTransferObject filtro = new OrgaoTransferObject();
            filtro.setOrgIdentificador(orgIdentificador);
            filtro.setAttribute(Columns.EST_IDENTIFICADOR, estIdentificador);
            final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

            final List<TransferObject> orgaos = cseDelegate.lstOrgaos(filtro, responsavel);

            // se encontrou um órgão distinto, recupera seus valores
            if ((orgaos == null) || orgaos.isEmpty() || (orgaos.size() > 1)) {
                throw new ZetraException("mensagem.erro.orgao.nao.encontrado", responsavel);
            }
        }

        @SuppressWarnings("unchecked")
        final List<TaxaDeJuros> taxaJurosList = (List<TaxaDeJuros>) parametros.get(TAXA_JUROS);

        final List<TransferObject> lstCoeficientes = new ArrayList<>();
        final Iterator<TaxaDeJuros> it = taxaJurosList.iterator();
        TaxaDeJuros taxaJuros = null;
        TransferObject cto = null;

        while (it.hasNext()) {
            taxaJuros = it.next();

            cto = new CustomTransferObject();
            cto.setAttribute(Columns.CFT_VLR, taxaJuros.getTaxa());
            cto.setAttribute(Columns.PRZ_VLR, (short) taxaJuros.getPrazo());
            lstCoeficientes.add(cto);
        }


        final SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
        simulacaoController.setTaxaJuros(csaCodigo, svcCodigo, lstCoeficientes, responsavel);
    }

    private void recuperaSvcCodigo (Map<CamposAPI, Object> parametros) throws ZetraException {
        final Object cnvCodVerba = parametros.get(CNV_COD_VERBA);
        final Object svcIdentificador = parametros.get(SERVICO_CODIGO);
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);
        final ConvenioDelegate cnvDelegate = new ConvenioDelegate();

        String svcCodigo = null; // Código do serviço na reserva de margem e na simulação

        final List<TransferObject> servicos = cnvDelegate.getSvcByCodVerbaSvcIdentificador((String)svcIdentificador, (String)cnvCodVerba, null, csaCodigo, true, responsavel);
        if (servicos.size() == 0) {
            throw new ZetraException("mensagem.erro.verba.ou.servico.invalido", responsavel);
        } else if (servicos.size() == 1) {
            final TransferObject convenio = servicos.get(0);
            svcCodigo = convenio.getAttribute(Columns.SVC_CODIGO).toString();
        } else if ((svcIdentificador != null) && !"".equals(svcIdentificador)) {
            final Iterator<TransferObject> it = servicos.iterator();
            TransferObject servico = null;
            while (it.hasNext()) {
                servico = it.next();
                if (servico.getAttribute(Columns.SVC_IDENTIFICADOR).equals(svcIdentificador)) {
                    svcCodigo = servico.getAttribute(Columns.SVC_CODIGO).toString();
                    break;
                }
            }
            if ((svcCodigo == null) || "".equals(svcCodigo)) {
                throw new ZetraException("mensagem.erro.verba.ou.servico.invalido", responsavel);
            }
        } else {
            final Map<Object, TransferObject> svcCodigosDistintos = new HashMap<>();

            for (final Object servico: servicos) {
                if (!svcCodigosDistintos.containsKey(((TransferObject) servico).getAttribute(Columns.SVC_CODIGO))) {
                    svcCodigosDistintos.put(((TransferObject) servico).getAttribute(Columns.SVC_CODIGO),(TransferObject) servico);
                }
            }

            if (svcCodigosDistintos.size() == 1) {
                final Collection<TransferObject> svcVlr = svcCodigosDistintos.values();
                final TransferObject convenio = svcVlr.iterator().next();
                svcCodigo = convenio.getAttribute(Columns.SVC_CODIGO).toString();
            } else {
                parametros.put(SERVICOS, Arrays.asList(svcCodigosDistintos.values().toArray()));
                throw new ZetraException("mensagem.maisDeUmServicoEncontrado", responsavel);
            }
        }

        parametros.put(SVC_CODIGO, svcCodigo);
    }

    @Override
    protected void preProcessa(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.preProcessa(parametros);
        recuperaSvcCodigo(parametros);
    }
}
