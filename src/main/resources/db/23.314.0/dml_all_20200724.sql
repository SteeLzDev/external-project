-- DESENV-14094
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO) 
VALUES ('campo_data_nascimento', 'Data de Nascimento', '/relatorios/campos_relatorio/campo_data_inclusao.jsp', 'N');

UPDATE tb_relatorio_filtro SET TFR_CODIGO = 'campo_data_nascimento' WHERE REL_CODIGO = 'beneficiario_dt_nasc' AND TFR_CODIGO = 'campo_data_inicio_fim';
