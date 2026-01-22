package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.COPIAR_CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO_COPIAR;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.periodo.RepasseHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: CadastrarOrgaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de cadastrar órgão</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CadastrarOrgaoCommand extends RequisicaoExternaFolhaCommand {

    public CadastrarOrgaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        OrgaoTransferObject orgao = (OrgaoTransferObject) parametros.get(ORGAO);
        String estIdentificador = orgao.getEstCodigo();
        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

        EstabelecimentoTransferObject estTO = cseDelegate.findEstabelecimentoByIdn(estIdentificador, responsavel);
        orgao.setEstCodigo(estTO.getEstCodigo());
        orgao.setOrgAtivo(CodedValues.STS_ATIVO);

        String orgCodigo = cseDelegate.createOrgao(orgao, responsavel);

        boolean copiarConvenio = ((Boolean) parametros.get(COPIAR_CONVENIO)).booleanValue();
        String orgIdnACopiar = (String) parametros.get(ORGAO_COPIAR);

        // copiar convênios de órgão já existente
        if (copiarConvenio) {
            String orgCodigoACopiar = null;
            if (!TextHelper.isNull(orgIdnACopiar)) {
                OrgaoTransferObject orgACopiar = null;
                try {
                    orgACopiar = cseDelegate.findOrgaoByIdn(orgIdnACopiar, estTO.getEstCodigo(), responsavel);
                } catch (ZetraException ex) {
                    //não faz nada
                }
                orgCodigoACopiar = (orgACopiar != null) ? orgACopiar.getOrgCodigo():null;
            }
            ConvenioDelegate cnvDelegate = new ConvenioDelegate();
            cnvDelegate.criaConveniosParaNovoOrgao(orgCodigo, orgao.getEstCodigo(), orgCodigoACopiar, responsavel);
        }

        // Limpa o cache de parâmetros de dia de repasse
        RepasseHelper.getInstance().reset();
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        OrgaoTransferObject orgao = (OrgaoTransferObject) parametros.get(ORGAO);
        String orgIpAcesso = orgao.getOrgIPAcesso();

        // Valida a lista de IPs.
        if (!TextHelper.isNull(orgIpAcesso)) {
            List<String> ipsAcesso = Arrays.asList(orgIpAcesso.split(";"));
            if (!JspHelper.validaListaIps(ipsAcesso)) {
                throw new ZetraException("mensagem.erro.ip.invalido", responsavel);
            }
        }
    }
}
