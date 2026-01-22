#language:pt

Funcionalidade: Edição de parâmetro de posto de graduação.
	Permite configurar o valor do serviço por posto.
				
 Esquema do Cenário: Manutenção de CSA - exibir opção de Editar parâmetros de posto de graduação
	Dado que a função "<funcodigo>" exista para usuário "<usucodigo>" da consignatária "<csacodigo>"
	E que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
    	Quando UsuCsa Logado
    	E acessar menu Manutencao > Consignantaria
    	E clicar na ação Editar Posto de Parâmetro 
	Então deve ser exibida a tela de edição de parâmetros de posto de graduação
   
   Exemplos:
    | usuario    | funcodigo | usucodigo                        | csacodigo |
    | csa2       | 540       | C4228080808080808080808080800D80 | 267       |



 Esquema do Cenário: Manutenção de CSA - não exibir opção de Editar parâmetros de posto de graduação
	Dado que a função "<funcodigo>" não exista para usuário "<usucodigo>" da consignatária "<csacodigo>"
	E que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
    	Quando UsuCsa Logado
    	E acessar menu Manutencao > Consignantaria
    	E que não tenha a ação Editar Posto de Parâmetro 
   
   Exemplos:
    | usuario    | funcodigo | usucodigo                        | csacodigo |
    | csa2       | 540       | C4228080808080808080808080800D80 | 267       |

 Esquema do Cenário: Editar parâmetros de posto de graduação - definir valor da autorização
	Dado que a função "<funcodigo>" exista para usuário "<usucodigo>" da consignatária "<csacodigo>"
	Dado que tenha postos de graduação cadastrado
	Dado que o serviço "<svcCodigo>" tenha valor fixo por posto de graduação para consignatária "<csacodigo>"
	E que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
	Quando UsuCsa Logado
	E acessar menu Manutencao > Consignantaria
	E clicar na ação Editar Posto de Parâmetro
	E escolhe o serviço "<svcCodigo>" configurado para ter valor fixo por posto de graduação
	E informa valores para os postos de graduação e salva
	Então para cada posto de graduação, verifique se os dados foram persistidos no banco
	
   Exemplos:
    | usuario    | funcodigo | usucodigo                        | csacodigo | svcCodigo                        |
    | csa2       | 540       | C4228080808080808080808080800D80 | 267       | 4C868080808080808080808088886275 |
    


 Esquema do Cenário: Editar parâmetros de posto de graduação - listar serviços com valor fixo por posto de graduação
	Dado que a função "<funcodigo>" exista para usuário "<usucodigo>" da consignatária "<csacodigo>"
	Dado que o serviço "<svcCodigo>" tenha valor fixo por posto de graduação para consignatária "<csacodigo>"
	E que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
    	Quando UsuCsa Logado
    	E acessar menu Manutencao > Consignantaria
    	E clicar na ação Editar Posto de Parâmetro 
	Então o "<svcCodigo>" esteja presente no combo de serviços
   
   Exemplos:
    | usuario    | funcodigo | usucodigo                        | csacodigo | svcCodigo                        |
    | csa2       | 540       | C4228080808080808080808080800D80 | 267       | 4C868080808080808080808088886275 |