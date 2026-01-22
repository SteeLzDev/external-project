-- DESENV-22153
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES ('campo_funcao_sensivel', 'Funções Sensíveis', '/relatorios/campos_relatorio/campo_funcao_sensivel.jsp', 'N');

UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1 WHERE REL_CODIGO = 'auditoria' AND RFI_SEQUENCIA >= 4;

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_EXIBE_SUP)
VALUES ('auditoria', 'campo_funcao_sensivel', '1', '1', '0', '1', '0', 4, '1');

UPDATE tb_texto_sistema SET TEX_TEXTO = 'Pelo menos um papel deve ser selecionado.' WHERE TEX_CHAVE = 'mensagem.informe.papel.usuario' AND TEX_TEXTO = 'Pelo menos um papel de ser selecionado.';

