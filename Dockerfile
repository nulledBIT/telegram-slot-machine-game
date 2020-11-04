FROM ubuntu:latest

COPY . /app
WORKDIR /app

ENTRYPOINT ["kotlin"]
RUN SlotMachineTelegramChatApplication
