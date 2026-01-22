/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     23/05/2022 11:24:10                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_origem_solicitacao                                 */
/*==============================================================*/
create table tb_origem_solicitacao
(
   OSO_CODIGO           varchar(32) not null,
   OSO_DESCRICAO        varchar(40) not null,
   primary key (OSO_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_solicitacao_autorizacao
   add SOA_DATA_RESPOSTA datetime;

alter table tb_solicitacao_autorizacao
   add SOA_OBS text;

alter table tb_solicitacao_autorizacao
   add OSO_CODIGO varchar(32);

alter table tb_solicitacao_autorizacao add constraint FK_R_870 foreign key (OSO_CODIGO)
      references tb_origem_solicitacao (OSO_CODIGO) on delete restrict on update restrict;

