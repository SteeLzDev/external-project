-- DESENV-10994
delete from tb_ajuda where acr_codigo in ('14989', '14992');
delete from tb_acesso_usuario where acr_codigo in ('14989', '14992');
delete from tb_acesso_recurso where acr_codigo in ('14989', '14992');
update tb_acesso_recurso set acr_operacao = 'planoSaude' where acr_codigo = '14988';
