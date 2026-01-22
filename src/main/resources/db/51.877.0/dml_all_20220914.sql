-- DESENV-18439
UPDATE tb_tipo_arquivo SET tar_descricao = 'Arquivo Anexo Autorização RG' where tar_codigo = '57';

UPDATE tb_texto_sistema SET tex_texto = 'RG' WHERE tex_chave = 'rotulo.validar.documentos.rg.frente';

UPDATE tb_campo_sistema SET cas_chave = 'validarDocumentos_rg' WHERE cas_chave = 'validarDocumentos_rgFrente';

