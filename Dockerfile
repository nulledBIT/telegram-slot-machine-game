FROM ubuntu:latest

COPY . /app
WORKDIR /app

ENTRYPOINT ["kotlin"]
RUN /src/main/kotlin/com/soribot/slot/machine/telegram/bot/SlotMachineTelegramChatApplication.kt
