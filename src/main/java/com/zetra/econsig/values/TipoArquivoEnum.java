package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: TipoArquivoEnum</p>
 * <p>Description: Enumeração do tipo de arquivo.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum TipoArquivoEnum {

    ARQUIVO_CADASTRO_MARGENS("1"),
    ARQUIVO_TRANSFERIDOS("2"),
    ARQUIVO_BLOQUEIO_SERVIDOR("3"),
    ARQUIVO_RETORNO_INTEGRACAO("4"),
    ARQUIVO_RETORNO_ATRASADO("5"),
    ARQUIVO_CRITICA("6"),
    ARQUIVO_LOTE("7"),
    ARQUIVO_MOVIMENTO_FINANCEIRO("8"),
    ARQUIVO_CONCILIACAO("9"),
    ARQUIVO_REAJUSTE("10"),
    ARQUIVO_CONTRACHEQUES("11"),
    ARQUIVO_SENHAS_SERVIDORES("12"),
    ARQUIVO_ANEXO_AUTORIZACAO_GENERICO("13"),
    ARQUIVO_ANEXO_AUTORIZACAO_BOLETO("14"),
    ARQUIVO_ANEXO_AUTORIZACAO_DSD("15"),
    ARQUIVO_ANEXO_AUTORIZACAO_SUSPENSAO("16"),
    ARQUIVO_FALECIDO("17"),
    ARQUIVO_REGRA_INCONSISTENCIA("18"),
    ARQUIVO_ANEXO_AUTORIZACAO_COMPROVANTE_PAGAMENTO("19"),
    ARQUIVO_ADEQUACAO_A_MARGEM("20"),
    ARQUIVO_ANEXO_AUTORIZACAO_REATIVACAO("21"),
    ARQUIVO_ANEXO_AUTORIZACAO_DOC_ADICIONAL_COMPRA("22"),
    ARQUIVO_IMAGEM_PERFIL_USUARIO("23"),
    ARQUIVO_CONSIGNATARIAS("24"),
    ARQUIVO_CONVENIO("25"),
    ARQUIVO_HISTORICO("26"),
    ARQUIVO_DESLIGADO_BLOQUEADO("27"),
    ARQUIVO_ANEXO_AUTORIZACAO_CREDITO_ELETRONICO("28"),
    ARQUIVO_RG("29"),
    ARQUIVO_CPF("30"),
    ARQUIVO_COMPROVANTE_RESIDENCIA("31"),
    ARQUIVO_CERTIDAO_CASAMENTO("32"),
    ARQUIVO_DECLARACAO_UNIAO_ESTAVEL("33"),
    ARQUIVO_CERTIDAO_NASCIMENTO("34"),
    ARQUIVO_DECLARACAO_MATRICULA_FREQUENCIA_ESCOLAR("35"),
    ARQUIVO_TUTELA_CURATELA("36"),
    ARQUIVO_DECLARACAO_CARENCIA("37"),
    ARQUIVO_ATESTADO_MEDICO("38"),
    ARQUIVO_CADASTRO_MARGEM_COMPLEMENTAR("39"),
    ARQUIVO_CANCELAMENTO_BENEFICIOS_POR_INADIMPLENCIA("40"),
	ARQUIVO_PREVIA_FATURAMENTO_BENEFICIOS("41"),
	ARQUIVO_DIRF_SERVIDOR("42"),
	ARQUIVO_GENERICO("43"),
	ARQUIVO_BOLETO_PARCELA_EM_ATRASO("44"),
	ARQUIVO_MOVIMENTO_FINANCEIRO_DOWNLOAD("45"),
    ARQUIVO_ANEXO_AUTORIZACAO_ALTERACAO_AVANCADA("46"),
    ARQUIVO_DISPENSA_VALIDACAO_DIGITAL_SER("47"),
    ARQUIVO_RECUPERACAO_CREDITO("48"),
    ARQUIVO_ANEXO_AUTORIZACAO_LIQUIDACAO("49"),
    ARQUIVO_SALDO_DEVEDOR("50"),
    ARQUIVO_ANEXO_CONTRATO("51"),
    ARQUIVO_IMAGEM_BANNER_PUBLICIDADE("52"),
    ARQUIVO_ANEXO_DOCUMENTO_REGISTRO_SERVIDOR("53"),
    ARQUIVO_CARTEIRINHAS_A_SEREM_TOMBADAS("54"),
    ARQUIVO_CADASTRO_DEPENDENTE("55"),
    ARQUIVO_MENSAGEM("56"),
    ARQUIVO_ANEXO_RG("57"),
    // DESENV-18439: Solicitado remoção do tipo arquivo
    // ARQUIVO_ANEXO_RG_VERSO("58"),
    ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO("59"),
    ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE("60"),
    ARQUIVO_ANEXO_AUTORIZACAO_OUTRO("61"),
    ARQUIVO_ANEXO_CREDENCIAMENTO("62"),
    ARQUIVO_ANEXO_CREDENCIAMENTO_DOC_CSA("63"),
    ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO("64"),
    ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO("65"),
    ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO_CSE("66"),
	ARQUIVO_RELATORIO_CUSTOMIZADO("67"),
	ARQUIVO_LOTE_RESCISAO("68"),
    ARQUIVO_CONSIGNATARIA("69"),
    ARQUIVO_XML_MARGEM_RETORNO_MOVIMENTO("70"),
	ARQUIVO_RECONHECIMENTO_FACIAL_FRONTAL_SERVIDOR("71"),
	ARQUIVO_RECONHECIMENTO_FACIAL_PERFIL_ESQUERDO_SERVIDOR("72"),
	ARQUIVO_RECONHECIMENTO_FACIAL_PERFIL_DIREITO_SERVIDOR("73"),
	ARQUIVO_LOTE_CADASTRO_CONSIGNATARIA("74"),
    ARQUIVO_INTEGRACAO_CREDITO_TRABALHADOR("75");


    private final String codigo;

    private TipoArquivoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera um tipo de arquivo de acordo com o código passado.
     *
     * @param codigo Código do tipo de arquivo que deve ser recuperado.
     * @return Retorna um tipo de arquivo
     *
     * @throws IllegalArgumentException Caso o código do tipo de arquivo informádo seja inválido
     */

    public static TipoArquivoEnum recuperaTipoArquivo(String codigo) {
        TipoArquivoEnum tipoArquivo = null;

        for (final TipoArquivoEnum tipo : TipoArquivoEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                tipoArquivo = tipo;
                break;
            }
        }

        if (tipoArquivo == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.tipo.arquivo.invalido", (AcessoSistema) null));
        }

        return tipoArquivo;
    }

}
