-- DESENV-18146
INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('213', 'Termo Aditivo Assinado aprovado');
INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('214', 'Termo Aditivo Assinado rejeitado');

INSERT INTO tb_tipo_arquivo (TAR_CODIGO, TAR_DESCRICAO, TAR_QTD_DIAS_LIMPEZA, TAR_UPLOAD_SUP, TAR_UPLOAD_CSE, TAR_UPLOAD_ORG, TAR_UPLOAD_CSA, TAR_UPLOAD_COR, TAR_NOTIFICACAO_UPLOAD)
VALUES('67', 'Arquivo Credenciamento Termo Aditivo Aprovado', 0, 'S', 'S', 'S', 'S', 'S', 'N');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16662', '1', '528', '/v3/visualizarDashboardCredenciamento', 'acao', 'finalizarCredenciamentoCsa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16663', '7', '528', '/v3/visualizarDashboardCredenciamento', 'acao', 'finalizarCredenciamentoCsa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailCredConclCse', 'Credenciamento Concluído: <@csa_nome> ', 'Prezados,<br> o credenciamento da consignatária <@csa_nome> foi concluído com sucesso.');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailCredConclCsa', '<@nome_sistema>: Credenciamento Concluído', 'Prezada <@csa_nome>,<br> o credenciamento no sistema <@nome_sistema> foi concluído com sucesso');

UPDATE tb_texto_sistema SET TEX_TEXTO = 'Nenhum credenciamento encontrado' WHERE TEX_CHAVE = 'rotulo.dashboard.credenciamento.nenhum.registro';
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Listagem de ocorrências do crendenciamento' WHERE TEX_CHAVE = 'rotulo.dashboard.detalhar.paginacao.ocorrencias';

