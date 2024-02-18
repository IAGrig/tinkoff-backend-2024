![Bot](https://github.com/iagrig/tinkoff-backend-2024/actions/workflows/bot.yml/badge.svg)
![Scrapper](https://github.com/iagrig/tinkoff-backend-2024/actions/workflows/scrapper.yml/badge.svg)

# Link Tracker

ФИО: Григорьев Илья Алексеевич

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 21` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot
* Scrapper

Для работы требуется БД `PostgreSQL`. Присутствует опциональная зависимость на `Kafka`.
