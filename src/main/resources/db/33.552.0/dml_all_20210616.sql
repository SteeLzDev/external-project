-- DESENV-9319
UPDATE tb_acesso_recurso 
SET ACR_RECURSO = '/v3/editarTipoPenalidade',
ACR_PARAMETRO = 'acao',
ACR_OPERACAO = 'iniciar' 
WHERE ACR_RECURSO = '/penalidade/lst_tipo_penalidade.jsp';

UPDATE tb_acesso_recurso
SET ACR_RECURSO = '/v3/editarTipoPenalidade',
ACR_PARAMETRO = 'acao',
ACR_OPERACAO = 'editar' 
WHERE ACR_RECURSO = '/penalidade/edt_tipo_penalidade.jsp';

UPDATE tb_acesso_recurso
SET ACR_RECURSO = '/v3/editarTipoPenalidade',
ACR_PARAMETRO = 'acao',
ACR_OPERACAO = 'salvar' 
WHERE ACR_RECURSO = '/penalidade/modifica_tipo_penalidade.jsp';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16332', '7', '185', '/v3/editarTipoPenalidade', 'acao', 'excluir', 1, 'S', 'S', 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16333', '1', '185', '/v3/editarTipoPenalidade', 'acao', 'excluir', 1, 'S', 'S', 'S', '2');

