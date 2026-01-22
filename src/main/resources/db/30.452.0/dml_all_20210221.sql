-- DESENV-15358
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('alterarMultiplasConsignacoes_tipoJustica', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('alterarMultiplasConsignacoes_comarcaJustica', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('alterarMultiplasConsignacoes_numeroProcesso', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('alterarMultiplasConsignacoes_dataDecisao', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('alterarMultiplasConsignacoes_textoDecisao', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('alterarMultiplasConsignacoes_anexo', 'S');

INSERT INTO tb_tipo_dado_adicional (TDA_CODIGO, TEN_CODIGO, TDA_DESCRICAO, TDA_EXPORTA, TDA_SUP_CONSULTA, TDA_CSE_CONSULTA, TDA_CSA_CONSULTA, TDA_SER_CONSULTA, TDA_SUP_ALTERA, TDA_CSE_ALTERA, TDA_CSA_ALTERA, TDA_SER_ALTERA, TDA_DOMINIO)
VALUES ('50', '19', 'Valor parcela anterior à alteração múltipla', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'MONETARIO');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16142', '1', '284', '/v3/listarCidades', 'acao', 'alteracaoMultiplosAdes', 1, 'S', 'S', NULL, 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16143', '7', '284', '/v3/listarCidades', 'acao', 'alteracaoMultiplosAdes', 1, 'S', 'S', NULL, 'N', '2'); 
