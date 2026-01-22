package com.zetra.econsig.webservice.command.saida.v4;

import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PROVENTOS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V4_0;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO_SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SRS_ATIVO;
import static com.zetra.econsig.webservice.CamposAPI.SRS_BLOQUEADO;
import static com.zetra.econsig.webservice.CamposAPI.SRS_EXCLUIDO;
import static com.zetra.econsig.webservice.CamposAPI.SRS_FALECIDO;
import static com.zetra.econsig.webservice.CamposAPI.SRS_PENDENTE;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;

/**
 * <p>Title: RespostaDadosConsignacaoCommand</p>
 * <p>Description: classe command que gera uma lista de entidade Servidor em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaServidorCommand extends RespostaRequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RespostaServidorCommand.class);

    public RespostaServidorCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        CustomTransferObject servidor = (parametros.get(SERVIDOR_V4_0) != null) ? (CustomTransferObject) parametros.get(SERVIDOR_V4_0) : (CustomTransferObject) parametros.get(SERVIDOR);

        RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
        reg.setNome(SERVIDOR_V4_0);

        reg.addAtributo(SERVIDOR, servidor.getAttribute(Columns.SER_NOME));
        reg.addAtributo(SER_CPF, servidor.getAttribute(Columns.SER_CPF));
        reg.addAtributo(RSE_MATRICULA, servidor.getAttribute(Columns.RSE_MATRICULA));
        reg.addAtributo(EST_IDENTIFICADOR, servidor.getAttribute(Columns.EST_IDENTIFICADOR));
        reg.addAtributo(ESTABELECIMENTO, servidor.getAttribute(Columns.EST_NOME));
        reg.addAtributo(ORG_IDENTIFICADOR, servidor.getAttribute(Columns.ORG_IDENTIFICADOR));
        reg.addAtributo(ORGAO, servidor.getAttribute(Columns.ORG_NOME));
        reg.addAtributo(RSE_TIPO, servidor.getAttribute(Columns.RSE_TIPO));
        reg.addAtributo(RSE_DATA_ADMISSAO, servidor.getAttribute(Columns.RSE_DATA_ADMISSAO));
        reg.addAtributo(RSE_PRAZO, servidor.getAttribute(Columns.RSE_PRAZO));
        reg.addAtributo(RSE_SALARIO, servidor.getAttribute(Columns.RSE_SALARIO));
        reg.addAtributo(RSE_PROVENTOS, servidor.getAttribute(Columns.RSE_PROVENTOS));

        try {
            ParametroDelegate parDelegate = new ParametroDelegate();
            if (!parDelegate.hasValidacaoDataNasc(responsavel)) {
                reg.addAtributo(SER_DATA_NASC, servidor.getAttribute(Columns.SER_DATA_NASC));
            }
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        String srsCodigo = (String) servidor.getAttribute(Columns.SRS_CODIGO);
        RegistroRespostaRequisicaoExterna resSrs = new RegistroRespostaRequisicaoExterna();

        if (srsCodigo.equals(CodedValues.SRS_ATIVO)) {
            resSrs.addAtributo(SRS_ATIVO, Boolean.TRUE);
        } else if (CodedValues.SRS_BLOQUEADOS.contains(srsCodigo)) {
            resSrs.addAtributo(SRS_BLOQUEADO, Boolean.TRUE);
        } else if (srsCodigo.equals(CodedValues.SRS_EXCLUIDO)) {
            resSrs.addAtributo(SRS_EXCLUIDO, Boolean.TRUE);
        } else if (srsCodigo.equals(CodedValues.SRS_FALECIDO)) {
            resSrs.addAtributo(SRS_FALECIDO, Boolean.TRUE);
        } else if (srsCodigo.equals(CodedValues.SRS_PENDENTE)) {
            resSrs.addAtributo(SRS_PENDENTE, Boolean.TRUE);
        }

        reg.addAtributo(SITUACAO_SERVIDOR, resSrs);

        respostas.add(reg);

        return respostas;
    }
}
