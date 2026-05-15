# Verdade ou Desafio App

Aplicativo Android desenvolvido em **Kotlin** com **Jetpack Compose** para jogar **Verdade ou Desafio** com mais organização, personalização e controle de pontuação.

---

## ✨ Visão geral

O **Verdade ou Desafio App** oferece uma experiência completa de jogo, com fluxo guiado entre menu, configuração dos jogadores, sorteio/rotação, escolha entre verdade ou desafio, exibição do resultado e controle de placar.

Além disso, o app permite personalizar perguntas, desafios, configurações de áudio e elementos visuais, mantendo os dados salvos localmente no dispositivo.

---

## 📱 Funcionalidades

* Cadastro e organização dos jogadores
* Tela de menu principal com opções de jogo e configurações
* Sorteio/rotação para definir quem desafia e quem será desafiado
* Escolha entre **Verdade** e **Desafio**
* Lista de verdades e desafios personalizados
* Sistema de pontuação por jogador
* Histórico de jogadas
* Captura e listagem de vídeos relacionados às rodadas
* Personalização de imagem da garrafa
* Configurações de som, música e volume
* Cartas/poderes especiais para a dinâmica do jogo
* Armazenamento local com banco de dados do próprio app

---

## 🧩 Telas do aplicativo

* **Menu Principal**
* **Configuração de Jogadores**
* **Sorteio / Giro da garrafa**
* **Revelação dos jogadores**
* **Escolha da rodada**
* **Seleção do tipo de pergunta**
* **Resultado da rodada**
* **Placar**
* **Cartas do jogador**
* **Captura de vídeo**
* **Lista de vídeos**
* **Opções / Configurações**

---

## 🛠️ Tecnologias utilizadas

* **Kotlin**
* **Jetpack Compose**
* **Room Database**
* **Android SDK**
* **Coroutines**
* **Material 3**

---

## 📂 Estrutura do projeto

```bash
app/
└── src/main/java/com/cafeteria/verdadeoudesafio/
    ├── database/
    ├── managers/
    ├── models/
    ├── repository/
    ├── screens/
    ├── ui/theme/
    └── MainActivity.kt
```

---

## ▶️ Como executar o projeto

### Requisitos

* Android Studio instalado
* Emulador Android ou celular físico com modo desenvolvedor ativado

### Passos

1. Clone o repositório:

```bash
git clone https://github.com/EmersonJun/Verdade-ou-Desafio-App.git
```

2. Abra o projeto no **Android Studio**.
3. Aguarde a sincronização do Gradle.
4. Execute o app em um emulador ou dispositivo Android.

---

## 🚀 Como gerar o APK

Se quiser gerar a versão de instalação do app:

1. Abra o projeto no Android Studio.
2. Vá em **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
3. Ao finalizar, o APK será gerado na pasta indicada pelo Android Studio.

---

## 🎮 Fluxo do jogo

1. O usuário inicia o app.
2. Os jogadores são cadastrados na tela de configuração.
3. O sistema realiza a rotação/sorteio.
4. É definido quem desafia e quem será desafiado.
5. O jogador escolhe entre verdade ou desafio.
6. O app exibe a pergunta ou desafio.
7. O resultado é registrado no placar.
8. O jogo pode seguir para a próxima rodada.

---

## 📌 Diferenciais do projeto

* Interface moderna com Compose
* Fluxo completo de jogo, sem depender de backend externo
* Dados persistidos localmente
* Possibilidade de customização da experiência
* Organização em camadas, facilitando manutenção e evolução

---

## 🔮 Melhorias futuras

* Integração com banco em nuvem
* Ranking geral entre partidas
* Novas cartas especiais
* Mais opções visuais de tema
* Compartilhamento dos resultados
* Exportação do histórico

---

## 👨‍💻 Autor

**Emerson Jun**

GitHub: [EmersonJun](https://github.com/EmersonJun)

---

## ⭐ Gostou do projeto?

Se este projeto te ajudou ou chamou atenção, deixe uma estrela no repositório.
