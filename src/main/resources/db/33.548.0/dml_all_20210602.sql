-- DESENV-15908
UPDATE tb_texto_sistema SET TEX_TEXTO = 'ou Percentual da margem' WHERE TEX_CHAVE = 'rotulo.alterar.multiplo.consignacao.percentual.margem';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE IN ('mensagem.erro.alterar.multiplo.consignacao.incide.margem.diferente', 'mensagem.erro.alterar.multiplo.consignacao.margem.zerada');

UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '284' AND ACR_OPERACAO = 'salvar';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16329', '1', '284', '/v3/alterarMultiplasConsignacoes', 'acao', 'validar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16330', '2', '284', '/v3/alterarMultiplasConsignacoes', 'acao', 'validar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16331', '7', '284', '/v3/alterarMultiplasConsignacoes', 'acao', 'validar', 1, 'S', 'S', NULL, 'N', '2');
