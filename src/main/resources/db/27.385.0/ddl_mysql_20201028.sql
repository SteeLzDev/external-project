/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     28/10/2020 13:31:16                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_historico_status_ade                               */
/*==============================================================*/
create table tb_historico_status_ade
(
   ADE_CODIGO           varchar(32) not null,
   SAD_CODIGO_ANTERIOR  varchar(32) not null,
   SAD_CODIGO_NOVO      varchar(32) not null,
   HSA_DATA             datetime not null,
   primary key (ADE_CODIGO, SAD_CODIGO_ANTERIOR, SAD_CODIGO_NOVO, HSA_DATA)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_historico_status_ade add constraint FK_R_819 foreign key (ADE_CODIGO)
      references tb_aut_desconto (ADE_CODIGO) on delete restrict on update restrict;

alter table tb_historico_status_ade add constraint FK_R_820 foreign key (SAD_CODIGO_ANTERIOR)
      references tb_status_autorizacao_desconto (SAD_CODIGO) on delete restrict on update restrict;

alter table tb_historico_status_ade add constraint FK_R_821 foreign key (SAD_CODIGO_NOVO)
      references tb_status_autorizacao_desconto (SAD_CODIGO) on delete restrict on update restrict;

