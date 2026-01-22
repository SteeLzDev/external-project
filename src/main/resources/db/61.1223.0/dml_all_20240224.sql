-- DESENV-21071
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR, FUN_LIBERA_MARGEM)
VALUES ('555', '9', 'Vínculo de Consignatária e Registro Servidor', 'N', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16915', '2', '555', '/v3/manterVinculoCsaRse', 'acao', 'novo', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16916', '7', '555', '/v3/manterVinculoCsaRse', 'acao', 'novo', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16917', '2', '555', '/v3/manterVinculoCsaRse', 'acao', 'editar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16918', '7', '555', '/v3/manterVinculoCsaRse', 'acao', 'editar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16919', '2', '555', '/v3/manterVinculoCsaRse', 'acao', 'iniciar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16920', '7', '555', '/v3/manterVinculoCsaRse', 'acao', 'iniciar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16921', '2', '555', '/v3/manterVinculoCsaRse', 'acao', 'salvar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16922', '7', '555', '/v3/manterVinculoCsaRse', 'acao', 'salvar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16923', '2', '555', '/v3/manterVinculoCsaRse', 'acao', 'bloquearDesbloquear', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16924', '7', '555', '/v3/manterVinculoCsaRse', 'acao', 'bloquearDesbloquear', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16925', '2', '555', '/v3/manterVinculoCsaRse', 'acao', 'excluir', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16926', '7', '555', '/v3/manterVinculoCsaRse', 'acao', 'excluir', 1, 'S', 'S', NULL, 'N', '2');


UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Não é permitido reservar margem para o vínculo deste ${lower(rotulo.servidor.singular)}: {0} - {1}.'
WHERE TEX_CHAVE = 'mensagem.vinculoNaoPermiteReserva';

UPDATE tb_texto_sistema
SET TEX_TEXTO =  'HC        Não é permitido reservar margem para o vínculo deste ${lower(rotulo.servidor.singular)} {0} - {1}.'
WHERE TEX_CHAVE = 'mensagem.vinculoNaoPermiteReserva.lote';

UPDATE tb_texto_sistema
SET TEX_TEXTO = 'HC        Não é permitido reservar margem para o vínculo deste ${lower(rotulo.servidor.singular)} {0} - {1}.'
WHERE TEX_CHAVE = 'mensagem.vinculoNaoPermiteReserva.febraban';

UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Não é permitido consultar margem para o vínculo deste ${lower(rotulo.servidor.singular)}: {0} - {1}.'
WHERE TEX_CHAVE = 'mensagem.vinculoNaoPermiteConsultarMargem';

