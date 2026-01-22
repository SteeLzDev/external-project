-- DESENV-22717
UPDATE tb_modelo_email
SET MEM_TEXTO = 'Prezada <@csa_nome>,<br> foram criados os novos vínculos abaixo:<p> <@vinculos> <p> gentileza acessar o sistema se deseja <@situacao_vinculo> o(s) vínculo(s) criado(s)'
WHERE MEM_CODIGO = 'enviarEmailCsaNovoVinculo';

