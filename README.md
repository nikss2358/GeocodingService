Прокси-приложение с возможность прямого и обратного геокодирования(из координат в адрес и наоборот).
Использует API Яндекса Геокодера [https://yandex.ru/dev/geocode/doc/ru/](https://yandex.ru/maps-api/products/geocoder-api), кэширует в БД My SQL.

Использовать на порту 8080, начальный endpoint: /geocode.

Для кодирования адреса: /IP

Пример: 
Запрос: http://localhost:8080/geocode/IP/Москва
Ответ: 37.617698 55.755864

Для кодирования IP: /address

Пример: 
Запрос: http://localhost:8080/geocode/address/49.106414 55.796127
Ответ: Казань

Stack: Java, Spring(Boot, Data Jpa), My SQL, Docker(([aleksejmorozov](https://hub.docker.com/repositories/aleksejmorozov), docker images: appservice, dbservice)



