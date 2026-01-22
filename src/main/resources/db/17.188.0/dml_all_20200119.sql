-- DESENV-11193
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/autenticarUsuarioCertificadoDigital', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '11751';

DELETE FROM tb_acesso_recurso WHERE ACR_RECURSO = '/login/validar_certificado_digital.jsp';
DELETE FROM tb_acesso_recurso WHERE ACR_RECURSO = '/login/fundo.jsp';
