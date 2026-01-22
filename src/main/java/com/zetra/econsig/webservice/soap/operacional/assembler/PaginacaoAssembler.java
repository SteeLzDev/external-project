package com.zetra.econsig.webservice.soap.operacional.assembler;

import static com.zetra.econsig.webservice.CamposAPI.PAGINA_ATUAL;
import static com.zetra.econsig.webservice.CamposAPI.QTD_PAGINAS;
import static com.zetra.econsig.webservice.CamposAPI.QTD_REGISTROS;
import static com.zetra.econsig.webservice.CamposAPI.REGISTRO_FINAL;
import static com.zetra.econsig.webservice.CamposAPI.REGISTRO_INICIAL;

import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v8.Paginacao;

/**
 * <p>Title: PaginacaoAssembler</p>
 * <p>Description: Assembler para objeto paginacao da requisicao soap ListarParcelas</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Eduardo Fortes
 */

public class PaginacaoAssembler extends BaseAssembler {

    private PaginacaoAssembler() {
        //
    }

    public static Paginacao toPaginacaoV8(Map<CamposAPI, Object> paramResposta) {
        final Paginacao paginacao = new Paginacao();

        paginacao.setQtdRegistros((int) paramResposta.get(QTD_REGISTROS));
        paginacao.setQtdPaginas((int) paramResposta.get(QTD_PAGINAS));
        paginacao.setPaginaAtual((int) paramResposta.get(PAGINA_ATUAL));
        paginacao.setRegistroInicial((int) paramResposta.get(REGISTRO_INICIAL));
        paginacao.setRegistroFinal((int) paramResposta.get(REGISTRO_FINAL));

        return paginacao;
    }
}
