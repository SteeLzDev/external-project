-- DESENV-14066
DELETE FROM tb_ajuda WHERE ACR_CODIGO = '15785';
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO = '15785';
DELETE FROM tb_acesso_recurso WHERE ACR_CODIGO = '15785';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15785', '1', '76', '/v3/consultarMargem', 'acao', 'gerarPdf', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15796', '3', '/v3/autenticarEuConsigoMais', 'acao', 'gerarToken', 1, 'S', 'S', 'N', '2'); 

DELETE FROM tb_texto_sistema WHERE TEX_CHAVE IN ('mensageme.erro.reativar.beneficio.detalhes.selecionar.odonto', 'mensageme.erro.reativar.beneficio.detalhes.selecionar.saude', 'mensageme.erro.reativar.beneficio.nenhum.contrato', 'mensageme.erro.reativar.beneficio.status.nao.permitido');
