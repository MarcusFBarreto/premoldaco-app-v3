Descrição

O Premoldaço App v3 é um aplicativo Android desenvolvido para auxiliar no cálculo e gerenciamento de orçamentos de materiais pré-moldados (como lajes, blocos e estruturas). Ele integra uma calculadora web via WebView, autenticação de usuários via Firebase, envio de orçamentos para o Firestore e notificações push em tempo real para atualizações de status. O app é uma evolução de versões anteriores, com foco em estabilidade, notificações e integração com o site premoldaco.com.br.

Este repositório contém o código fonte completo do app, incluindo configurações Gradle, código Kotlin e recursos.
Funcionalidades Principais

    Calculadora de Orçamentos: Carrega uma página web interativa (calculadora.html) para cálculos de materiais, com pré-preenchimento de dados do usuário (ex.: email).
    Autenticação Firebase: Login e cadastro via email/senha, com suporte a autenticação anônima para envios rápidos.
    Envio de Orçamentos: Salva dados no Firestore e notifica o usuário via push quando o status muda (ex.: "Novo" para "Em Análise").
    Notificações Push (FCM): Integração com Cloud Functions para envio de notificações em atualizações de status.
    Integração WebView: Permite interações JS-Android, como reprodução de som e submissão de dados.
    Permissões e Compatibilidade: Solicita permissões para notificações (Android 13+) e suporta temas customizados para evitar crashes.
    Contato via WhatsApp: Links integrados para whatsapp de vendedores..

Tecnologias Utilizadas

    Linguagem: Kotlin
    Framework: Android SDK (minSdk 24, targetSdk 34)
    Bibliotecas:
        Firebase: Auth, Firestore, Messaging (FCM)
        Material Design: com.google.android.material:material:1.12.0
        Gson para parsing JSON
        Outras: AndroidX Core, AppCompat
    Build System: Gradle com Kotlin DSL
    Backend: Firebase Cloud Functions (Node.js para notificações)
    Web Integration: WebView com JavaScriptInterface para comunicação bidirecional

Pré-requisitos

    Android Studio (versão recente, ex.: Flamingo ou superior)
    Conta Firebase configurada (crie um projeto no console.firebase.google.com)
    Baixe o google-services.json do Firebase e coloque em /app
    Git instalado para clonar o repositório

Instalação e Configuração

    Clone o Repositório:
    text

    git clone https://github.com/MarcusFBarreto/premoldaco-app-v3.git
    cd premoldaco-app-v3
    Abra no Android Studio:
        Abra o projeto como uma pasta existente.
        Sync Gradle (pode pedir para atualizar dependências).
    Configure Firebase:
        No Firebase Console, adicione um app Android com o pacote com.pesquisapromo.premoldaco.premoldacoapp.v1.
        Baixe e adicione google-services.json em /app.
        Ative Auth (Email/Password), Firestore e Cloud Messaging.
    Cloud Functions (para Notificações):
        Crie uma pasta /functions fora do app (ou em um repo separado).
        Copie o código de index.js (para trigger em orçamentos) e deploy com firebase deploy --only functions.
    Build e Rode:
        Conecte um device/emulador.
        Rode o app via Android Studio (Shift + F10).

Uso

    Login/Cadastro: Na tela de autenticação, insira email/senha.
    Calculadora: A WebView carrega a ferramenta de cálculos; envie orçamentos para o Firestore.
    Notificações: Atualize status no Firestore (manual ou via admin) para testar pushes.
    Teste Local: Use o emulador Firebase para functions (npm run serve na pasta functions).

Exemplo de Fluxo:

    Abra o app e logue.
    Use a calculadora para enviar um orçamento.
    No Firebase Console, mude o status em /orcamentos — receba notificação push.

Contribuição

Contribuições são bem-vindas! Siga estes passos:

    Fork o repositório.
    Crie um branch: git checkout -b feature/nova-funcionalidade.
    Commit suas mudanças: git commit -m "Adiciona nova feature".
    Push para o branch: git push origin feature/nova-funcionalidade.
    Abra um Pull Request.

Relate issues no GitHub Issues.
Licença

Este projeto está licenciado sob a MIT License — veja o arquivo LICENSE para detalhes (adicione um se não existir).
Contato

    Desenvolvedor: Marcus F. Barreto
    WhatsApp: +55 85 99271-2043 (para orçamentos ou suporte)
    email:marcus1fialho@gmail.com

Agradeço por usar o Premoldaço App! Se precisar de ajuda, abra uma issue.
