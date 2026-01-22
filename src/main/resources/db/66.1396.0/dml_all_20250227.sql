-- DESENV-23013
-- 1) ATUALIZAR O GRUPO DE PARAMETROS NOS NOVOS PARAMETROS:
update tb_tipo_param_sist_consignante set gps_codigo = '1' where tpc_codigo in ('976','977');
update tb_tipo_param_sist_consignante set gps_codigo = '2' where tpc_codigo in ('963','964','966','967','968','969');
update tb_tipo_param_sist_consignante set gps_codigo = '3' where tpc_codigo in ('965','970','971','972','973','974','975','978');


-- 2) CONCEDER A PERMISSAO DE VISUALIZAR E ALTERAR PARA USUÁRIOS SUP:
update tb_tipo_param_sist_consignante set tpc_sup_consulta = 'S', tpc_sup_altera ='S' where tpc_codigo in ('963','964','965','966','967','968','969','970','971','972','973','974','975','976','977','978');  


-- 3) VINCULAR OS PARAMETROS AOS PERFIS: 
insert into tb_perfil_param_sist_cse 
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-IMP_AVANCADO')
where tpc.tpc_codigo in ('963','964','965','966','967','968','969','970','971','972','973','974','975','976','977','978')
and not exists (
 select 1 from tb_perfil_param_sist_cse pcc
 where tpc.tpc_codigo = pcc.tpc_codigo
 and per.per_codigo = pcc.per_codigo
);


insert into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-MANUTENCAO')
where tpc.tpc_codigo in ('963','964','965','966','967','968','969','970','971','972','973','974','975','976','977','978')
and not exists (
 select 1 from tb_perfil_param_sist_cse pcc
 where tpc.tpc_codigo = pcc.tpc_codigo
 and per.per_codigo = pcc.per_codigo
);


insert into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-PRODUCAO')
where tpc.tpc_codigo in ('963','964','966','967','968','969')
and not exists (
 select 1 from tb_perfil_param_sist_cse pcc
 where tpc.tpc_codigo = pcc.tpc_codigo
 and per.per_codigo = pcc.per_codigo
);


insert into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-SEGURANCA_BASICO')
where tpc.tpc_codigo in ('976','977')
and not exists (
 select 1 from tb_perfil_param_sist_cse pcc
 where tpc.tpc_codigo = pcc.tpc_codigo
 and per.per_codigo = pcc.per_codigo
);

