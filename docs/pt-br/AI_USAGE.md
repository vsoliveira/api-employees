# Uso de IA no Desenvolvimento

## Objetivo

Este projeto foi desenvolvido através de SDD, isto é Spec-Driven Development onde o desenvolvedor atua no papel de arquiteto de software e a IA atende a estes requisitos acordados, otimizando o tempo e a qualidade do desenvolvimento da solução.

## Como Eu Uso

Utilizei o framework [copilot-agents-dojo](https://github.com/andreaswasita/copilot-agents-dojo) com algumas modificações pontuais mas o fluxo de trabalho é igual

### Agentes - api-employees/.github/agents

Em primeiro lugar, é preciso definir o fluxo de trabalho que será utilizado com a IA e para tal, eu inicio criando personas que atuarão como colegas colaboradores de equipe. Para esta aplicação, criou-se:

 - O [arquiteto](#): Persona que auxilia na organização e tomada de decisão entre soluções considerando trade-offs auto-descobertos pela IA além pontos e comentários feitos por um colaborador humano.
 Também é responsável por organizar as personas (agentes) a seguir.

 - O [engenheiro de segurança](#): Persona responsável por ponderar os pontos de segurança, exploração de vulnerabilidades, mitigação de falhas durante a elaboração da especificação.

- O [engenheiro de software](#): Persona que implementa as especificações efetivamente.

- O [gerente de projeto](#): Persona que organiza as atividades levando em consideração prazo, ordem de prioridade e cumprimento de um prazo ou objetivo. O resultado desse agente é um board organizado apontando em que ordem as atividades devem ser realizadas para cumprir com uma demanda de negócio.

- O [engenheiro de testes](#): Persona que planeja e implementa o escopo de testes das atitividades realizadas pelo engenheiro de software. 
Também se responsabiliza pela implementação de quality-gates para manter o projeto com qualidade.

### Skills - api-employees/skills

Posteriormente a definição dos "colaboradores" que trabalharão comigo, eu defino as habilidades que essas personas podem possuir e também defino como elas vão interagir umas com as outras.

Nessa etapa, são definidos as skills e majoritariamente orquestradas através do [copilot-instructions](.github/copilot-instructions.md) onde digo para carregar todas as skills no contexto conforme são necessárias. 

### Retroalimentação

Para finalizar meu fluxo de trabalho, retroalimento a IA com seu próprio output, isto é, peço para que em seu fluxo de trabalho, ela seja capaz de criar "memórias" ou documentar lições aprendidas.

Essa técnica permite o acompanhamento do fluxo de raciocínio da tecnologia e tamém a criação de novas skills.

Para esse projeto, é possível acompanhar essa técnica em `./tasks/lessons.md` e `./tasks/todo.md`

## Limites e Critérios

Procuro manter sempre a taxa do contexto abaixo de 60% para evitar alucinações da IA e avalio a cada prompt/interação, qual linha de raciocínio está sendo elaborada.

## Processo de Validação

Além dos arquivos `./tasks/lessons.md` e `./tasks/todo.md`, há o acompanhamento de cada modificação realizada.

Através de commits ou PRs bem estruturados, busco tornar esse acompanhamento mais simples e objetivo, garantindo que a IA está tendo conclusões determinísticas.
