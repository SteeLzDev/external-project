/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     26/03/2024 09:35:13                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_funcao_altera_margem_ade                           */
/*==============================================================*/
create table tb_funcao_altera_margem_ade
(
   FUN_CODIGO           varchar(32) not null,
   PAP_CODIGO           varchar(32) not null,
   MAR_CODIGO_ORIGEM    smallint not null,
   MAR_CODIGO_DESTINO   smallint not null,
   primary key (FUN_CODIGO, PAP_CODIGO, MAR_CODIGO_ORIGEM, MAR_CODIGO_DESTINO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_funcao_altera_margem_ade add constraint FK_R_955 foreign key (FUN_CODIGO)
      references tb_funcao (FUN_CODIGO) on delete restrict on update restrict;

alter table tb_funcao_altera_margem_ade add constraint FK_R_956 foreign key (PAP_CODIGO)
      references tb_papel (PAP_CODIGO) on delete restrict on update restrict;

alter table tb_funcao_altera_margem_ade add constraint FK_R_957 foreign key (MAR_CODIGO_ORIGEM)
      references tb_margem (MAR_CODIGO) on delete restrict on update restrict;

alter table tb_funcao_altera_margem_ade add constraint FK_R_958 foreign key (MAR_CODIGO_DESTINO)
      references tb_margem (MAR_CODIGO) on delete restrict on update restrict;

