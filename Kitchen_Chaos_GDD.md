

🍳

**KITCHEN CHAOS**

*Game Design Document*

v1.0  —  2025

| Gênero Roguelike / Ação | Perspectiva Top-Down 2D |
| :---: | :---: |
| **Engine** Unity 2D / LibGDX | **Plataforma** PC (Windows / Linux) |

# **1\. Visão Geral do Jogo**

## **1.1 Conceito**

*Kitchen Chaos é um roguelike top-down 2D onde você joga como um chef preso numa cozinha mágica amaldiçoada. Os ingredientes ganharam vida e querem se vingar. Sobreviva, lute com utensílios de cozinha e combine ingredientes para criar poder-ups devastadores.*

| Título | Kitchen Chaos |
| :---- | :---- |
| **Gênero** | Roguelike / Ação Top-Down |
| **Engine** | Unity 2D ou LibGDX (Java) |
| **Plataforma** | PC — Windows / Linux |
| **Jogadores** | 1 jogador (single-player) |
| **Duração média** | 20 a 40 minutos por run |
| **Classificação** | Livre — sem violência realista |

## **1.2 Proposta de Valor**

O que torna Kitchen Chaos único é a combinação de três elementos:

* Tema inusitado e memorável — cozinha como arena de batalha

* Sistema de receitas como progressão — combine itens para ganhar poder

* Inimigos temáticos com comportamentos inspirados em comidas reais

## **1.3 Referências de Inspiração**

| Jogo | O que inspira em Kitchen Chaos |
| :---- | :---- |
| The Binding of Isaac | Geração procedural de salas e itens passivos |
| Enter the Gungeon | Combate ágil, esquiva, projéteis |
| Vampire Survivors | Simplicidade de controles, escalonamento de poder |
| Overcooked | Estética e tema culinário |

# **2\. Jogabilidade (Gameplay)**

## **2.1 Loop Principal**

O jogador percorre andares de uma cozinha mágica, cada um com salas geradas proceduralmente. A progressão é:

* Entrar em sala → Derrotar inimigos → Coletar itens/ingredientes

* Escolher caminho no mapa → Avançar para próxima sala

* Ao fim de cada andar: enfrentar um Boss temático

* Ao morrer: recomeçar do zero (roguelike clássico)

## **2.2 Controles**

| Tecla / Botão | Ação |
| :---- | :---- |
| WASD / Setas | Mover o personagem |
| Mouse (aponta) | Definir direção do ataque |
| Clique esquerdo | Atacar (arma primária) |
| Clique direito / E | Usar habilidade especial |
| Shift | Esquiva (dash com i-frames breves) |
| Q | Usar item consumível equipado |
| ESC / P | Pausar o jogo |

## **2.3 Sistema de Combate**

### **Armas (Utensílios de Cozinha)**

O jogador começa com uma arma e pode encontrar novas durante a run:

| Arma | Tipo | Dano | Velocidade | Efeito Especial |
| :---- | :---- | :---- | :---- | :---- |
| 🍳 Frigideira | Melee | Alta | Baixo | Golpe em arco frontal, knockback |
| 🔪 Faca de Chef | Melee | Média | Alto | Ataque rápido e preciso, 2 hits/s |
| 🥄 Concha | Ranged | Média | Médio | Arremessa sopa quente como projétil |
| 🧂 Saleiro | Ranged | Baixo | Muito Alto | Spread de micro-projéteis (shotgun) |
| 🪣 Balde d'Água | Ranged | Baixo | Médio | Projétil lento que enraíza inimigos |
| 🍴 Garfo Gigante | Melee | Muito Alta | Baixo | Ataque lento em linha, perfura inimigos |

### **Esquiva**

* O dash tem duração de 0.2s e confere brevíssima invencibilidade

* Cooldown de 1.5 segundos — use com estratégia

* Itens podem modificar o dash (distância, rastro de fogo, etc.)

## **2.4 Sistema de Receitas**

*A mecânica mais criativa do jogo: combine ingredientes coletados em salas especiais para criar power-ups únicos. Cada combinação produz um efeito diferente.*

| Receita | Nome do Efeito | Descrição |
| :---- | :---- | :---- |
| 🌶️ Pimenta \+ 🫒 Óleo | Rastilho de Fogo | Deixa trilha de chamas ao se mover por 15s |
| 🧄 Alho \+ 🧅 Cebola | Aura Repelente | Inimigos próximos recuam por 10s |
| 🍋 Limão \+ 🧂 Sal | Lâmina Ácida | Ataques causam sangramento (dano contínuo) |
| 🍯 Mel \+ 🥛 Leite | Escudo Dourado | Absorve o próximo golpe recebido |
| 🥚 Ovo \+ 🧈 Manteiga | Acelerador | Velocidade \+50% por 20s |
| 🍅 Tomate \+ 🌿 Manjericão | Cura Instantânea | Recupera 30% do HP máximo |
| 🧀 Queijo \+ 🍄 Cogumelo | Duplicador | Dobra o dano do próximo ataque |
| 🍞 Pão \+ 🥩 Carne | Berserk | Dano \+100%, velocidade \+30%, sem esquiva por 10s |

Ingredientes são coletados em salas de "despensa" ao longo da run. O jogador pode guardar até 2 ingredientes e combiná-los a qualquer momento fora de combate.

## **2.5 Saúde e Morte**

* O jogador começa com 6 corações (12 HP)

* Ao chegar a 0 HP: morte permanente — a run recomeça do início

* Corações podem ser recuperados em salas especiais ou com receitas

* Alguns itens aumentam o HP máximo permanentemente durante a run

# **3\. Mundo e Estrutura de Fases**

## **3.1 Andares da Cozinha**

O jogo é dividido em 4 andares temáticos, cada um com Boss único:

| Andar | Inimigos Comuns | Boss | Mecânica do Boss |
| :---- | :---- | :---- | :---- |
| 1 — Despensa | Tomates, cebolas, ervas gigantes | Chef Tomate Bravão | Gera mini-tomates, arremessa sementes |
| 2 — Câmara Fria | Frangos zumbis, blocos de gelo | Frango Congelado | Ataque de gelo em área, invulnerabilidade por fases |
| 3 — Salão Principal | Talheres animados, guardanapos-fantasma | Maestro dos Garfos | Invoca ondas de garfos em padrão musical |
| 4 — Cozinha do Chef | Elite de todos os inimigos anteriores | Chef Fantasma Final | 3 fases, usa todas as receitas do jogo contra você |

## **3.2 Tipos de Sala**

| Tipo | Frequência | Descrição |
| :---- | :---- | :---- |
| ⚔️ Sala de Combate | Comum | Derrote todos os inimigos para liberar a saída |
| 🏪 Despensa | Incomum | Colete ingredientes e combine receitas |
| 🏪 Loja do Tempero | Incomum | Troque moedas por itens e upgrades |
| ❤️ Copa | Rara | Recupere HP ou aumente HP máximo |
| ❓ Sala Misteriosa | Rara | Evento aleatório — risco vs. recompensa |
| 💀 Sala do Boss | Fixa | Chefe do andar — recompensa generosa |

# **4\. Inimigos**

## **4.1 Inimigos Comuns**

| Inimigo | Arquétipo | Comportamento |
| :---- | :---- | :---- |
| 🍅 Tomate Raivoso | Perseguidor | Corre direto ao jogador — rápido, pouca vida |
| 🐔 Frango Zumbi | Tanque | Muito HP, ataque em linha, lento |
| 🥦 Brócolis Atirador | Atirador | Fica parado, atira projéteis em padrão de spread |
| 🧅 Cebola Explosiva | Suicida | Persegue e explode ao chegar perto do jogador |
| 🍄 Cogumelo Clonador | Suporte | Cria cópias de inimigos próximos — mate primeiro |
| 🦐 Camarão Velozes | Enxame | Vêm em grupos grandes, HP mínimo, são muito rápidos |

## **4.2 Padrão de IA Básico**

A IA dos inimigos usa uma máquina de estados simples — ideal para implementar como iniciante em game dev:

* Idle: parado, detectando jogador no raio de visão

* Chase: perseguindo o jogador em linha reta (com pathfinding simples por grid)

* Attack: executando animação/padrão de ataque ao entrar no alcance

* Hurt: animação de dano recebido

* Dead: animação de morte \+ loot

# **5\. Progressão e Itens**

## **5.1 Moeda**

* Inimigos dropam Pimentas de Ouro (moeda do jogo)

* Pimentas são usadas na Loja do Tempero durante a run

* Ao morrer, todas as moedas são perdidas (roguelike clássico)

## **5.2 Itens Passivos (Exemplos)**

| Item | Tipo | Efeito |
| :---- | :---- | :---- |
| Avental Reforçado | Passivo | \+2 HP máximo |
| Faca Serrilhada | Passivo | Ataques melee causam sangramento |
| Tênis de Borracha | Passivo | \+20% de velocidade de movimento |
| Temporizador | Passivo | Reduz cooldown de esquiva em 30% |
| Avental de Amianto | Passivo | Imune a dano de fogo |
| Livro de Receitas | Passivo | Revela as 3 próximas receitas disponíveis |
| Chapéu de Chef \+1 | Passivo | \+15% de dano em todos os ataques |

## **5.3 Meta-Progressão**

*Entre runs, o jogador desbloqueia conteúdo permanente. Isso dá motivação para continuar jogando mesmo após mortes.*

* Receitas desbloqueadas ficam permanentemente visíveis no cookbook do menu

* Inimigos derrotados pela primeira vez são adicionados ao bestiário

* Modos de dificuldade desbloqueados após completar a run (modo Inferno)

* Skins cosméticas de chef desbloqueadas por conquistas

# **6\. Arte e Som**

## **6.1 Estilo Visual**

* Arte 2D pixel art — 16x16 ou 32x32 sprites por tile

* Paleta quente: laranjas, vermelhos, amarelos — contraste com azuis na câmara fria

* Inimigos com expressões exageradas e cômicas (olhos grandes, bocas raivosas)

* Interface limpa, inspirada em cardápio de restaurante

## **6.2 Recursos Gratuitos Recomendados**

| Recurso | Uso |
| :---- | :---- |
| Kenney.nl | Sprites, tiles e UI gratuitos de alta qualidade |
| itch.io (free) | Pacotes de pixel art temáticos (buscar 'food', 'kitchen') |
| OpenGameArt.org | Assets com licença Creative Commons |
| Piskel / Aseprite | Ferramentas para criar pixel art própria |

## **6.3 Som e Música**

* Música: jazz acelerado e percussivo — caótico como uma cozinha em horário de pico

* Efeitos sonoros: frituras, cortes, panelas batendo, inimigos gritando como comida

* Recursos gratuitos: freesound.org, OpenGameArt, Incompetech (música CC)

* Ferramenta de SFX gerado: sfxr / jsfxr — gera sons de jogo em segundos

# **7\. Cronograma de Desenvolvimento**

## **7.1 Divisão em 4 Sprints (1 Semestre)**

### **🟢 Sprint 1 — Semanas 1–4: Fundação**

* Configurar projeto Unity/LibGDX

* Implementar movimento do personagem (WASD \+ mouse)

* Implementar dash/esquiva

* Sistema de sala simples (uma sala fixa com inimigos)

* Inimigo básico: Tomate Raivoso com IA perseguidora

* Sistema de HP e morte do jogador

* Colisão e hitboxes básicas

### **🟡 Sprint 2 — Semanas 5–8: Sistemas Core**

* Geração procedural de salas (conectar salas por portas)

* 2 armas funcionais: Frigideira e Faca

* 3 tipos de inimigos com IA diferenciada

* Sistema de moedas e drop de loot

* Loja simples com 2–3 itens

* 1 Boss funcional (Chef Tomate Bravão)

* Sistema de ingredientes e primeiras receitas

### **🟠 Sprint 3 — Semanas 9–14: Conteúdo**

* Andares 2 e 3 completos com inimigos e bosses

* Completar sistema de receitas (8 combinações)

* 5+ itens passivos implementados

* Arte e animações definitivas (substituir placeholders)

* Som e música integrados

* Interface do usuário (HUD, menus, mapa de salas)

* Balanceamento inicial de dificuldade

### **🔴 Sprint 4 — Semanas 15–18: Polimento**

* Andar 4 e Boss Final

* Meta-progressão (cookbook, bestiário)

* Testes extensivos e correção de bugs

* Balanceamento fino (dano, HP, drop rates)

* Game feel: juice, screen shake, partículas

* Build final e exportação

## **7.2 Linha do Tempo Visual**

| Fase | Período | Entregas |
| :---- | :---- | :---- |
| Sprint 1 | Sem. 1–4 | Movimento, colisão, combate básico, 1 inimigo |
| Sprint 2 | Sem. 5–8 | Salas procedurais, 3 inimigos, 1 boss, receitas |
| Sprint 3 | Sem. 9–14 | Conteúdo completo, arte, som, UI |
| Sprint 4 | Sem. 15–18 | Polimento, balanceamento, build final |

# **8\. Escopo e Riscos**

## **8.1 MVP — Produto Mínimo Viável**

*Se o tempo apertar, este é o jogo mínimo funcional e jogável que você precisa entregar:*

* 1 andar completo com geração procedural de salas

* 3 tipos de inimigos e 1 Boss

* 2 armas funcionais

* 5 receitas implementadas

* Sistema de HP e morte com restart

* Tela de início e game over

## **8.2 Features Extras (se sobrar tempo)**

* Modo Inferno (dificuldade aumentada)

* Placar de high scores local

* Conquistas e skins cosméticas

* Modo co-op local (2 jogadores)

* Andar 4 e Boss Final completos

## **8.3 Riscos e Mitigações**

| Risco | Probabilidade | Mitigação |
| :---- | :---- | :---- |
| Geração procedural complexa | Alta | Usar algoritmos prontos (BSP, Cellular Automata) — há tutoriais abundantes |
| Arte consume muito tempo | Alta | Usar assets gratuitos (Kenney) até o Sprint 3 e só então polir |
| Balanceamento ruim | Média | Testar com outras pessoas desde o Sprint 2 |
| Escopo grande demais | Média | Sempre priorizar o MVP — features extras são bônus |
| Bug crítico próximo do prazo | Baixa | Commits frequentes no Git, manter versão estável sempre |

# **9\. Stack Técnica Recomendada**

## **9.1 Unity (Recomendado para iniciantes em game dev)**

* Linguagem: C\# — sintaxe similar ao Java

* Renders 2D nativos, physics 2D, câmera e input bem documentados

* Asset Store gratuita com milhares de recursos

* Comunidade enorme — qualquer dúvida tem tutorial no YouTube

* Export fácil para Windows, Mac, Linux e WebGL

## **9.2 LibGDX (Boa opção para quem já sabe Java)**

* Framework em Java — aproveita seu conhecimento existente

* Mais controle de baixo nível — ótimo aprendizado

* Box2D integrado para física

* Curva de aprendizado maior, mas muito gratificante

* Export para Desktop e Android

## **9.3 Ferramentas de Apoio**

| Ferramenta | Uso |
| :---- | :---- |
| Git \+ GitHub | Controle de versão — essencial, faça commits diários |
| Tiled Map Editor | Criação de mapas/tiles 2D, integra com ambas as engines |
| Aseprite / Piskel | Criação de pixel art e animações |
| Audacity | Edição de efeitos sonoros |
| Trello / Notion | Gerenciamento do projeto e tasks do semestre |

*Kitchen Chaos GDD v1.0  —  Boa sorte no desenvolvimento\! 🍳🔥*