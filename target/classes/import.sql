-- Inserindo Desenvolvedoras
INSERT INTO Desenvolvedora (nome, dataDeFundacao, paisDeOrigem) VALUES ('Nintendo', '1889-09-23', 'Japão');
INSERT INTO Desenvolvedora (nome, dataDeFundacao, paisDeOrigem) VALUES ('Valve Corporation', '1996-08-24', 'Estados Unidos');
INSERT INTO Desenvolvedora (nome, dataDeFundacao, paisDeOrigem) VALUES ('CD Projekt Red', '2002-02-01', 'Polônia');
INSERT INTO Desenvolvedora (nome, dataDeFundacao, paisDeOrigem) VALUES ('Capcom', '1979-05-30', 'Japão');
INSERT INTO Desenvolvedora (nome, dataDeFundacao, paisDeOrigem) VALUES ('ConcernedApe', '2012-01-01', 'Estados Unidos');

-- Inserindo Gêneros
INSERT INTO Genero (nome, descricao) VALUES ('Ação', 'Jogos que enfatizam desafios físicos, incluindo coordenação mão-olho e tempos de reação.');
INSERT INTO Genero (nome, descricao) VALUES ('RPG', 'Role-Playing Game, onde o jogador controla as ações de um personagem imerso em um mundo bem definido.');
INSERT INTO Genero (nome, descricao) VALUES ('Estratégia', 'Jogos onde a vitória é alcançada através de pensamento e planejamento tático superior.');
INSERT INTO Genero (nome, descricao) VALUES ('Simulação', 'Jogos projetados para simular atividades do mundo real.');
INSERT INTO Genero (nome, descricao) VALUES ('Terror', 'Jogos projetados para assustar o jogador através de suspense e horror.');

-- Inserindo Jogos
INSERT INTO Jogo (titulo, descricao, anoLancamento) VALUES ('The Legend of Zelda: Breath of the Wild', 'Jogo de ação e aventura em mundo aberto onde Link acorda de um sono de 100 anos.', 2017);
INSERT INTO Jogo (titulo, descricao, anoLancamento) VALUES ('Half-Life 2', 'Tiro em primeira pessoa que combina ação intensa com uma narrativa imersiva.', 2004);
INSERT INTO Jogo (titulo, descricao, anoLancamento) VALUES ('The Witcher 3: Wild Hunt', 'RPG de ação focado em narrativa ambientado em um mundo de fantasia visualmente deslumbrante.', 2015);
INSERT INTO Jogo (titulo, descricao, anoLancamento) VALUES ('Stardew Valley', 'Um RPG de simulação agrícola onde você herda a antiga fazenda do seu avô.', 2016);
INSERT INTO Jogo (titulo, descricao, anoLancamento) VALUES ('Resident Evil 4', 'Jogo de terror de sobrevivência que segue o agente especial Leon S. Kennedy.', 2005);