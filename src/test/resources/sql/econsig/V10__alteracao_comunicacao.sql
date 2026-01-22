alter table tb_assunto_comunicacao
add ASC_CONSIGNACAO bool not null default 0;

alter table tb_comunicacao
add ADE_CODIGO varchar(32);

alter table tb_comunicacao add constraint FK_R_978 foreign key (ADE_CODIGO)
references tb_aut_desconto (ADE_CODIGO) on delete restrict on update restrict;

update tb_param_sist_consignante set psi_vlr = '8' where tpc_codigo = '339';

update tb_param_sist_consignante set psi_vlr = '12' where tpc_codigo = '440';